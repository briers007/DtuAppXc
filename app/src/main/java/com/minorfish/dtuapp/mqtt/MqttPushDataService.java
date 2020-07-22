package com.minorfish.dtuapp.mqtt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;
import com.minorfish.dtuapp.abs.App;
import com.minorfish.dtuapp.abs.Constants;

/**
 * 连接服务器传送数据服务
 */

/* 
 * MqttAbnormalService that does all of the work.
 * Most of the logic is borrowed from KeepAliveService.
 * http://code.google.com/p/android-random/source/browse/trunk/TestKeepAlive/src/org/devtcg/demo/keepalive/KeepAliveService.java?r=219
 */
public class MqttPushDataService extends Service {
    // this is the log tag
    public static final String TAG = "TTT";

    // the IP address, where your MQTT broker is running.
    //private static final String MQTT_HOST = "192.168.2.227";
    private static final String MQTT_HOST = "114.55.25.25";
    // the port at which the broker is running.
    private static final int MQTT_BROKER_PORT_NUM = 1883;
    // Let's not use the MQTT persistence.
    private static MqttPersistence MQTT_PERSISTENCE = null;
    // We don't need to remember any state between the connections, so we use a clean start.
    private static final boolean MQTT_CLEAN_START = true;
    // Let's set the internal keep alive for MQTT to 15 mins. I haven't tested this value much. It could probably be increased.
    private static final short MQTT_KEEP_ALIVE = 60;
    // Set quality of services to 0 (at most once delivery), since we don't want push notifications
    // arrive more than once. However, this means that some messages might get lost (delivery is not guaranteed)
    private static final int[] MQTT_QUALITIES_OF_SERVICE = {0};
    private static final int MQTT_QUALITY_OF_SERVICE = 2;
    // The broker should not retain any messages.
    private static final boolean MQTT_RETAINED_PUBLISH = false;

    // MQTT client ID, which is given the broker. In this example, I also use this for the topic header.
    // You can use this to run push notifications for multiple apps with one MQTT broker.
    public static String MQTT_CLIENT_ID = "ttt"; //dtu的mac地址，大写      用户名id也要

    private String USER_NAME = "admin";
    private String PASSWORD = "password";

    // These are the actions for the service (name are descriptive enough)
    private static final String ACTION_START = MQTT_CLIENT_ID + ".START";
    private static final String ACTION_STOP = MQTT_CLIENT_ID + ".STOP";
    private static final String ACTION_KEEPALIVE = MQTT_CLIENT_ID + ".KEEP_ALIVE";
    private static final String ACTION_RECONNECT = MQTT_CLIENT_ID + ".RECONNECT";

    // Connectivity manager to determining, when the phone loses connection
    private ConnectivityManager mConnMan;

    // Whether or not the service has been started.
    private boolean mStarted;

    // This the application level keep-alive interval, that is used by the AlarmManager
    // to keep the connection active, even when the device goes to sleep.
    private static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 28;

    // Retry intervals, when the connection is lost.
    private static final long INITIAL_RETRY_INTERVAL = 1000 * 60 * 3;

    // This is the instance of an MQTT connection.
    public MQTTConnection mConnection;

    public static boolean SERVICE_CONNECTED = false;

    // Static method to start the service
    public static void actionStart(Context ctx, String subscribeTopic) {
        String deviceID = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        Intent i = new Intent(ctx, MqttPushDataService.class);
        i.putExtra(EXTRA_SUBSCRIBE_TOPIC, subscribeTopic);
        i.putExtra(EXTRA_SUBSCRIBE_DEVICE_ID, deviceID);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }

    public static void actionBind(Context ctx, ServiceConnection serviceConnection, String subscribeTopic) {
        if(App.getApp().getDtuSetting()!=null) {
            MQTT_CLIENT_ID = App.getApp().getDtuSetting().mMac;
        }
        String deviceID = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!MqttPushDataService.SERVICE_CONNECTED) {
            Intent i = new Intent(ctx, MqttPushDataService.class);
            i.putExtra(EXTRA_SUBSCRIBE_TOPIC, subscribeTopic);
            i.putExtra(EXTRA_SUBSCRIBE_DEVICE_ID, deviceID);
            i.setAction(ACTION_START);
            ctx.startService(i);
        }
        Intent bindingIntent = new Intent(ctx, MqttPushDataService.class);
        bindingIntent.putExtra(EXTRA_SUBSCRIBE_TOPIC, subscribeTopic);
        bindingIntent.putExtra(EXTRA_SUBSCRIBE_DEVICE_ID, deviceID);
        bindingIntent.setAction(ACTION_START);
        ctx.bindService(bindingIntent,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    // Static method to stop the service
    public static void actionStop(Context ctx) {
        Intent i = new Intent(ctx, MqttPushDataService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }

    // Static method to send a keep alive message
    public static void actionPing(Context ctx) {
        Intent i = new Intent(ctx, MqttPushDataService.class);
        i.setAction(ACTION_KEEPALIVE);
        ctx.startService(i);
    }

    public void pushData(String topicName, String message) {
        try {
            mConnection.publishToTopic(topicName, message);
        }catch (Exception e){}
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MqttPushDataService.SERVICE_CONNECTED = true;
        mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		/* If our process was reaped by the system for any reason we need
         * to restore our state with merely a call to onCreate.  We record
		 * the last "started" value and restore it here if necessary. */
        handleCrashedService();

        mIOThread = new HandlerThread("IOThreadService");
        mIOThread.start();
        mIOHandler = new Handler(mIOThread.getLooper());
    }

    private IBinder binder = new MqttBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MqttBinder extends Binder {
        public MqttPushDataService getService() {
            return MqttPushDataService.this;
        }
    }

    // This method does any necessary clean-up need in case the server has been destroyed by the system
    // and then restarted
    private void handleCrashedService() {
        if (wasStarted()) {
            Log.e("TTT", "Handling crashed service...");
            // stop the keep alives
            stopKeepAlives();

            // Do a clean start
            start();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("TTT", "Service destroyed (started=" + mStarted + ")");
        // Stop the services, if it has been started
        MqttPushDataService.SERVICE_CONNECTED = false;
        if (mStarted) {
            stop();
        }
        //释放资源
        mIOThread.quit() ;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("TTT", "Service started with intent=" + intent);
        // Do an appropriate action based on the intent.
        if (ACTION_STOP.equals(intent.getAction())) {
            stop();
            stopSelf();
        } else if (ACTION_START.equals(intent.getAction())) {
            mDeviceId = intent.getStringExtra(EXTRA_SUBSCRIBE_DEVICE_ID);
            mSubscribeTopic = intent.getStringExtra(EXTRA_SUBSCRIBE_TOPIC);
            start();
        } else if (ACTION_KEEPALIVE.equals(intent.getAction())) {
            keepAlive();
        } else if (ACTION_RECONNECT.equals(intent.getAction())) {
            if (isNetworkAvailable()) {
                reconnectIfNecessary();
            }
        }
    }

    // Reads whether or not the service has been started from the preferences
    private boolean wasStarted() {
        return mStarted;
    }

    // Sets whether or not the services has been started in the preferences.
    private void setStarted(boolean started) {
        mStarted = started;
    }

    private synchronized void start() {
        Log.e("TTT", "Starting service...");
        // Do nothing, if the service is already running.
        if (mStarted) {
            Log.w(TAG, "Attempt to start connection that is already active");
            return;
        }
        // Establish an MQTT connection
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();

        // Register a connectivity listener
        registerReceiver(mConnectivityChanged, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private synchronized void stop() {
        // Do nothing, if the service is not running.
        if (!mStarted) {
            Log.w(TAG, "Attempt to stop connection not active.");
            return;
        }
        // Save stopped state in the preferences
        setStarted(false);
        // Remove the connectivity receiver
        unregisterReceiver(mConnectivityChanged);
        // Any existing reconnect timers should be removed, since we explicitly stopping the service.
        cancelReconnect();

        // Destroy the MQTT connection if there is one
        if (mConnection != null) {
            mConnection.disconnect();
            mConnection = null;
        }
    }

    //
    private synchronized void connect() {
        Log.e("TTT", "Connecting...");
        // fetch the device ID from the preferences.
        String deviceID = mDeviceId;
        // Create a new connection only if the device id is not NULL
        if (deviceID == null) {
            Log.e("TTT", "Device ID not found.");
        } else {
            try {
                mConnection = new MQTTConnection(MQTT_HOST);
                retryCount = 0;//连接成功，重试次数置0
            } catch (MqttException e) {
                // Schedule a reconnect, if we failed to connect
                Log.e("TTT", "MqttException: " + (e.getMessage() != null ? e.getMessage() : "NULL"));
                e.printStackTrace();
                if (isNetworkAvailable()) {
                    mConnection = null;
                    scheduleReconnect();
                }
            } catch (Exception e) {
                //Log.e("TTT", "Exception: " + (e.getMessage() != null ? e.getMessage() : "NULL"));
            }
            setStarted(true);
        }
    }

    private synchronized void keepAlive() {
        try {
            // Send a keep alive, if there is a connection.
            if (mStarted && mConnection != null) {
                mConnection.sendKeepAlive();
            }
        } catch (MqttException e) {
            Log.e("TTT", "MqttException: " + (e.getMessage() != null ? e.getMessage() : "NULL"), e);
            mConnection.disconnect();
            mConnection = null;
            cancelReconnect();
        }
    }

    // Schedule application level keep-alives using the AlarmManager
    private void startKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, MqttPushDataService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, pi);
    }

    // Remove all scheduled keep alives
    private void stopKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, MqttPushDataService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.cancel(pi);
    }

    public void scheduleReconnectFromAct() {
        retryCount = 0;
        scheduleReconnect();
    }

    // We schedule a reconnect based on the starttime of the service
    public void scheduleReconnect() {
        // the last keep-alive interval
        long interval = INITIAL_RETRY_INTERVAL;

        Log.e("TTT", "Rescheduling connection in " + interval + "ms.  " + retryCount + "retry.");

        // Schedule a reconnect using the alarm manager.
        Intent i = new Intent();
        i.setClass(this, MqttPushDataService.class);
        i.setAction(ACTION_RECONNECT);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pi);
    }

    // Remove the scheduled reconnect
    public void cancelReconnect() {
        Intent i = new Intent();
        i.setClass(this, MqttPushDataService.class);
        i.setAction(ACTION_RECONNECT);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.cancel(pi);
    }

    private Handler mIOHandler;
    private HandlerThread mIOThread;
    private int retryCount = 0;

    private synchronized void reconnectIfNecessary2() {
        if (mStarted && mConnection == null) {
            Log.e("TTT", "Reconnecting...");
            connect();
        }
    }

    private void reconnectIfNecessary() {
        if(retryCount <= 6 ) {
            if (mStarted && mConnection == null) {
                Log.e("TTT", "Reconnecting...");
                mIOHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        retryCount++;
                        reconnectIfNecessary2();
                    }
                });
            }
        } else {
            retryCount = 0;
            cancelReconnect();
            Intent broadIntent = new Intent();
            broadIntent.setAction(Constants.ACTION_MQTT_FAIL);
            sendBroadcast(broadIntent);
        }
    }

    // This receiver listeners for network changes and updates the MQTT connection
    // accordingly
    private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get network info
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            // Is there connectivity?
            boolean hasConnectivity = (info != null && info.isConnected());

            Log.e("TTT", "Connectivity changed: connected=" + hasConnectivity);

            if (hasConnectivity) {
                reconnectIfNecessary();
            } else if (mConnection != null) {
                // if there no connectivity, make sure MQTT connection is destroyed
                mConnection.disconnect();
                cancelReconnect();
                mConnection = null;
            }
        }
    };

    // Check if we are online
    private boolean isNetworkAvailable() {
        NetworkInfo info = mConnMan.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    // This inner class is a wrapper on top of MQTT client.
    private class MQTTConnection implements MqttSimpleCallback {
        IMqttClient mqttClient = null;

        // Creates a new connection given the broker address and initial topic
        MQTTConnection(String brokerHostName) throws MqttException {
            // Create connection spec
            String mqttConnSpec = "tcp://" + brokerHostName + ":" + MQTT_BROKER_PORT_NUM;
            // Create the client and connect
            mqttClient = MqttClient.createMqttClient(mqttConnSpec, MQTT_PERSISTENCE);
            //String clientID = MQTT_CLIENT_ID + "/" + mDeviceId;
            String clientID = MQTT_CLIENT_ID ;
            mqttClient.connect(clientID, MQTT_CLEAN_START, MQTT_KEEP_ALIVE);

            // register this client app has being able to receive messages
            mqttClient.registerSimpleHandler(this);

            // Subscribe to an initial topic, which is combination of client ID and device ID.
            subscribeToTopic(mSubscribeTopic);

            Log.e("TTT", "Connection established to " + brokerHostName + " on topic " + mSubscribeTopic);

            // Star the keep-alives
            startKeepAlives();
        }

        // Disconnect
        void disconnect() {
            try {
                stopKeepAlives();
                mqttClient.disconnect();
            } catch (MqttPersistenceException e) {
                Log.e("TTT", "MqttException" + (e.getMessage() != null ? e.getMessage() : " NULL"), e);
            }
        }

        /*
         * Send a request to the message broker to be sent messages published with
         *  the specified topic name. Wildcards are allowed.
         */
        private void subscribeToTopic(String topicName) throws MqttException {

            if ((mqttClient == null) || (!mqttClient.isConnected())) {
                // quick sanity check - don't try and subscribe if we don't have
                //  a connection
                Log.e("TTT", "Connection error" + "No connection");
            } else {
                String[] topics = {topicName};
                mqttClient.subscribe(topics, MQTT_QUALITIES_OF_SERVICE);
            }
        }

        /*
         * Sends a message to the message broker, requesting that it be published
         *  to the specified topic.
         */
        public void publishToTopic(String topicName, String message) throws MqttException {
            if ((mqttClient == null) || (!mqttClient.isConnected())) {
                // quick sanity check - don't try and publish if we don't have
                //  a connection
                Log.e("TTT", "No connection to public to");
            } else {
                mqttClient.publish(topicName, message.getBytes(), MQTT_QUALITY_OF_SERVICE, MQTT_RETAINED_PUBLISH);
            }
        }

        /*
         * Called if the application loses it's connection to the message broker.
         */
        public void connectionLost() throws Exception {
            Log.e("TTT", "Loss of connection" + "connection downed");
            stopKeepAlives();
            // null itself
            mConnection = null;
            if (isNetworkAvailable()) {
                reconnectIfNecessary();
            }
        }

        /*
         * Called when we receive a message from the message broker.
         */
        public void publishArrived(String topicName, byte[] payload, int qos, boolean retained) {
            // Show a notification
            String s = new String(payload);
            // showNotification(s);
            Log.e("TTT", "received: " + s);
            if("version".equalsIgnoreCase(s)){
                Intent intent = new Intent(Constants.ACTION_MQTT_CHANGE);
                sendBroadcast(intent);
            }else if("restart".equalsIgnoreCase(s)){
                Intent intent = new Intent(Constants.ACTION_MQTT_RESTART);
                sendBroadcast(intent);
            }
            //不用发送
//            Intent intent = new Intent(ACTION_MQTT_REAL_DATA_RECEIVED);
//            intent.putExtra(EXTRA_MQTT_RECEIVED_REAL_DATA, s);
//            sendBroadcast(intent);
        }

        void sendKeepAlive() throws MqttException {
            Log.e("TTT", "Sending keep alive");
            // publish to a keep-alive topic
            publishToTopic(MQTT_CLIENT_ID + "/keepalive", mDeviceId);
        }
    }

    private String mDeviceId, mSubscribeTopic;

    public static final String ACTION_MQTT_REAL_DATA_RECEIVED = "action_mqtt_real_data_received";
    public static final String EXTRA_MQTT_RECEIVED_REAL_DATA = "extra_mqtt_received_real_data";
    public static final String EXTRA_SUBSCRIBE_TOPIC = "extra_subscribe_topic";
    public static final String EXTRA_SUBSCRIBE_DEVICE_ID = "extra_subscribe_device_id";

    public static IntentFilter getReceiverFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MQTT_REAL_DATA_RECEIVED);
        return filter;
    }
}