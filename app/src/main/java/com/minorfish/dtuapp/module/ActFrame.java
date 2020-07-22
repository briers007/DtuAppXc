package com.minorfish.dtuapp.module;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.felhr.usbserial.UsbSerialInterface;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.abs.Api;
import com.minorfish.dtuapp.abs.App;
import com.minorfish.dtuapp.abs.Constants;
import com.minorfish.dtuapp.abs.Resp;
import com.minorfish.dtuapp.database.DatabaseHelper;
import com.minorfish.dtuapp.database.OnDatabaseListener;
import com.minorfish.dtuapp.module.dicts.DeviceMonitorEnum;
import com.minorfish.dtuapp.module.dicts.MonitorDicParameter;
import com.minorfish.dtuapp.module.fragment.FmtDTUSetting;
import com.minorfish.dtuapp.module.fragment.FmtHistory;
import com.minorfish.dtuapp.module.fragment.FmtLog;
import com.minorfish.dtuapp.module.fragment.FmtModifyPwd;
import com.minorfish.dtuapp.module.fragment.FmtRealData;
import com.minorfish.dtuapp.module.fragment.FmtSensorSetting;
import com.minorfish.dtuapp.module.model.LogDataBean;
import com.minorfish.dtuapp.module.model.MultiDataBean;
import com.minorfish.dtuapp.module.model.RealDataHistoryBean;
import com.minorfish.dtuapp.module.model.TriggerBean;
import com.minorfish.dtuapp.mqtt.MqttPushDataService;
import com.minorfish.dtuapp.usb.SanzeHelper;
import com.minorfish.dtuapp.usb.UsbService;
import com.tangjd.common.abs.BaseActivity;
import com.tangjd.common.abs.BaseFragment;
import com.tangjd.common.retrofit.string.OnStringRespListener;
import com.tangjd.common.utils.ByteUtil;
import com.tangjd.common.utils.FileKit;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActFrame extends BaseActivity implements OnDatabaseListener {

    @Bind(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @Bind(R.id.rb_real_data)
    RadioButton rbRealData;
    @Bind(R.id.rb_history)
    RadioButton rbHistory;
    @Bind(R.id.rb_dtu_setting)
    RadioButton rbDtuSetting;
    @Bind(R.id.rb_sensor_setting)
    RadioButton rbSensorSetting;
    @Bind(R.id.rb_log)
    RadioButton rbLog;
    @Bind(R.id.rb_modify_pwd)
    RadioButton rbModifyPwd;
    @Bind(R.id.radio_group)
    RadioGroup radioGroup;

    private Handler mIOHandler;
    private HandlerThread mIOThread;

    private HashMap<Integer, BaseFragment> mFragments = new HashMap<>();

    private HashMap<String, List<MonitorDicParameter.WarnLinesBean>> dicMap;
    public HashMap<String, String> sensorMap;

    private DatabaseHelper databaseHelper;

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private MqttPushDataService mqttPushDataService;
    private final ServiceConnection mqttConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            mqttPushDataService = ((MqttPushDataService.MqttBinder) arg1).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mqttPushDataService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_frame_layout);
        ButterKnife.bind(this);

        mIOThread = new HandlerThread("IOThread");
        mIOThread.start();
        mIOHandler = new Handler(mIOThread.getLooper());

        getHelper();
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (App.getApp().getDtuSetting() != null) {
            FmtRealData fmt = new FmtRealData();
            mFragments.put(R.id.rb_real_data, fmt);
            ft.replace(R.id.fragment_container, fmt, FmtRealData.class.getName());
            ft.commit();
            dicMap = (HashMap<String, List<MonitorDicParameter.WarnLinesBean>>) FileKit.getObject(ActFrame.this, Constants.FILE_KEY_MONITOR_DIC);
            if (dicMap == null || dicMap.size() == 0) {
                getMonitorDicParameters();
            }

        } else { //没配置dtu先引导去配置
            FmtDTUSetting fmt = new FmtDTUSetting();
            mFragments.put(R.id.rb_dtu_setting, fmt);
            ft.replace(R.id.fragment_container, fmt, FmtDTUSetting.class.getName());
            ft.commit();
            radioGroup.check(R.id.rb_dtu_setting);
        }
        if (App.getApp().getSensorSetting() != null) { //已配置工作站
            sensorMap = (HashMap<String, String>) FileKit.getObject(ActFrame.this, Constants.FILE_KEY_SENSOR_DIC);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                final FragmentTransaction ft = fm.beginTransaction();
                BaseFragment fmt = mFragments.get(checkedId);
                boolean needAdd = (fmt == null);
                for (Map.Entry<Integer, BaseFragment> item : mFragments.entrySet()) {
                    ft.hide(item.getValue());
                }
                switch (checkedId) {
                    case R.id.rb_real_data:
                        if (needAdd) {
                            fmt = new FmtRealData();
                            ft.add(R.id.fragment_container, fmt, FmtRealData.class.getName());
                        }
                        break;
                    case R.id.rb_history:
                        if (needAdd) {
                            fmt = new FmtHistory();
                            ft.add(R.id.fragment_container, fmt, FmtHistory.class.getName());
                        }
                        break;
                    case R.id.rb_dtu_setting:
                        if (needAdd) {
                            fmt = new FmtDTUSetting();
                            ft.add(R.id.fragment_container, fmt, FmtDTUSetting.class.getName());
                        }
                        break;
                    case R.id.rb_sensor_setting:
                        if (needAdd) {
                            fmt = new FmtSensorSetting();
                            ft.add(R.id.fragment_container, fmt, FmtSensorSetting.class.getName());
                        }
                        break;
                    case R.id.rb_log:
                        if (needAdd) {
                            fmt = new FmtLog();
                            ft.add(R.id.fragment_container, fmt, FmtLog.class.getName());
                        }
                        break;
                    case R.id.rb_modify_pwd:
                        if (needAdd) {
                            fmt = new FmtModifyPwd();
                            ft.add(R.id.fragment_container, fmt, FmtModifyPwd.class.getName());
                        }
                        break;
                }
                if (needAdd) {
                    mFragments.put(checkedId, fmt);
                } else {
                    ft.show(fmt);
                }
                ft.commit();
            }
        });
        onUsbCreate();

        String subscribeTopic = "monitor_multi_data";
        MqttPushDataService.actionBind(this, mqttConnection, subscribeTopic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        unbindService(mqttConnection);
        MqttPushDataService.actionStop(getApplicationContext());
        onUsbDestroy();
    }

    private void onUsbCreate() {
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    private void onUsbDestroy() {
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private static final int PACKET_LENGTH = 37;
    private volatile ByteArrayOutputStream mBaos = new ByteArrayOutputStream();

    interface OnGetSinglePacketListener {
        void onGetSinglePacket(byte[] singlePacket);
    }

    private OnGetSinglePacketListener mListener = new OnGetSinglePacketListener() {
        @Override
        public synchronized void onGetSinglePacket(final byte[] singlePacket) {
            final float ph = byte2floatCDAB(singlePacket, 3);
            final float yulv = byte2floatCDAB(singlePacket, 15);
            final float temperature = byte2floatCDAB(singlePacket, 19);
            final float zhuodu = byte2floatCDAB(singlePacket, 23);

            mIOHandler.post(new Runnable() {
                @Override
                public void run() {
                    //心跳，本机和工作台状态
                    try {
                        //mqttPushDataService.pushData("monitor_multi_data", message);
                    } catch (Exception e) {
                    }

                    TriggerBean phBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_PH, ph);
                    TriggerBean yulvBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_CL, yulv);
                    TriggerBean temperatureBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_TEMPERATURE, temperature);
                    TriggerBean zhuoduBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB, zhuodu);

                    HashMap<DeviceMonitorEnum, TriggerBean> data = new HashMap<DeviceMonitorEnum, TriggerBean>();
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_PH, phBean);
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_CL, yulvBean);
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_TEMPERATURE, temperatureBean);
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB, zhuoduBean);
                    try {
                        if (!(mFragments.get(R.id.rb_real_data)).isHidden()) {
                            ((FmtRealData) mFragments.get(R.id.rb_real_data)).showData(data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // upload and save
                    String serialPort = "";
                    String mac = "";
                    if (App.getApp().getSensorSetting() != null) {
                        serialPort = App.getApp().getSensorSetting().mSerialPort;
                    }
                    if (App.getApp().getDtuSetting() != null) {
                        mac = App.getApp().getDtuSetting().mMac;
                    }
                    long time = System.currentTimeMillis();
                    RealDataHistoryBean bean = new RealDataHistoryBean();
                    bean.zhuodu = zhuoduBean.value;
                    bean.zhuoduDeta = zhuoduBean.deta;
                    bean.zhuoduLevel = zhuoduBean.level;
                    bean.ph = phBean.value;
                    bean.phDeta = phBean.deta;
                    bean.phLevel = phBean.level;
                    bean.temperature = temperatureBean.value;
                    bean.temperatureDeta = temperatureBean.deta;
                    bean.temperatureLevel = temperatureBean.level;
                    bean.yulv = yulvBean.value;
                    bean.yulvDeta = yulvBean.deta;
                    bean.yulvLevel = yulvBean.level;
                    bean.date = time;
                    try {
                        getHelper().getRealDataHistoryDao().createOrUpdate(bean);
                    } catch (Exception e) {
                    }
                    try {
                        MultiDataBean dataBean = new MultiDataBean();
                        MultiDataBean.SensorDataBean sensorData = new MultiDataBean.SensorDataBean();
                        sensorData.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_PH.getCode();
                        sensorData.value = ph + "";
                        MultiDataBean.SensorDataBean sensorData2 = new MultiDataBean.SensorDataBean();
                        sensorData2.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_TEMPERATURE.getCode();
                        sensorData2.value = temperature + "";
                        MultiDataBean.SensorDataBean sensorData3 = new MultiDataBean.SensorDataBean();
                        sensorData3.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_CL.getCode();
                        sensorData3.value = yulv + "";
                        MultiDataBean.SensorDataBean sensorData4 = new MultiDataBean.SensorDataBean();
                        sensorData4.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB.getCode();
                        sensorData4.value = zhuodu + "";
                        dataBean.addSensorData(sensorData);
                        dataBean.addSensorData(sensorData2);
                        dataBean.addSensorData(sensorData3);
                        dataBean.addSensorData(sensorData4);
                        dataBean.mac = mac;
                        dataBean.port = serialPort;
                        dataBean.ts = System.currentTimeMillis();
                        String message = dataBean.toJson();
                        if (mqttPushDataService != null) {
                            mqttPushDataService.pushData("monitor_multi_data", message);
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
    };

    /**
     * 如 AB AA 40 76 为传输顺序 C D A B,对应的存储顺序 A B C D 为 40 76 AB AA，转换成 float 为 3.854228
     */
    public static float byte2floatCDAB(byte[] b, int index) {
        int l;
        l = b[index + 1];
        l &= 0xff;
        l |= ((long) b[index + 0] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 3] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 2] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * byte转换为float，低位在前
     *
     * @param b     字节（4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2floatDCBA(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    private synchronized void parsePacket(byte[] buffer, OnGetSinglePacketListener listener) {
        mBaos.write(buffer, 0, buffer.length);
        if (mBaos.size() == 0) {
            return;
        }
        buffer = mBaos.toByteArray();
        int correctPacketStartIndex = 0;
        for (int i = 0; i < buffer.length - 2; i++) {
            if (buffer[i] == 0x01 && buffer[i + 1] == 0x03 && buffer[i + 2] == 0x20) {
                correctPacketStartIndex = i;
            }
        }
        // 01 03 20 AB AA 40 76 00 00      00 00 00 00 00 00 94 11 41 20 00 00 00 00 00 00 00 00 D7 0A 3C A3 10 82 40 AE 27 BE
        // 01 03 20 AB AA 40 76 00 00      00 00 00 00 00 00 94 11 41 20 00 00 00 00 00 00 00 00 D7 0A 3C A3 10 82 40 AE 27 BE
        if (buffer.length < PACKET_LENGTH + correctPacketStartIndex) {
            return;
        }
        listener.onGetSinglePacket(ByteUtil.subByteArr(buffer, correctPacketStartIndex, PACKET_LENGTH));
        mBaos.reset();
        mBaos.write(buffer, correctPacketStartIndex + PACKET_LENGTH, buffer.length - (correctPacketStartIndex + PACKET_LENGTH));
    }

    private boolean mUsbDeviceOnline = false;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    mUsbDeviceOnline = true;
                    Log.w("TTTTTT", "ACTION_USB_PERMISSION_GRANTED " + device.toString());
                    if (device.getVendorId() == 6790 && device.getProductId() == 21795) {
                        getSanzeData();
                        SanzeHelper.getInstance().setOnUsbReadCallback(new UsbSerialInterface.UsbReadCallback() {
                            @Override
                            public void onReceivedData(byte[] buffer) {
                                parsePacket(buffer, mListener);
                            }
                        });
                    }
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    mUsbDeviceOnline = false;
                    Log.w("TTTTTT", "USB Permission not granted" + device.toString());
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    mUsbDeviceOnline = false;
                    Log.w("TTTTTT", "USB disconnected" + device.toString());
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    mUsbDeviceOnline = false;
                    Log.w("TTTTTT", "USB device not supported" + device.toString());
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    mUsbDeviceOnline = false;
                    Log.w("TTTTTT", "No USB connected");
                    break;
                case UsbService.ACTION_USB_READY:
                    mUsbDeviceOnline = true;
                    Log.e("TTTTTT", "receive broadcast usb ready " + device.getVendorId() + "  " + device.getProductId());
                    break;
            }
        }
    };

    private Handler mHandler = new Handler();
    private Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("TTTTTT", "getSanzeData");
            SanzeHelper.getInstance().getData();
            mHandler.postDelayed(this, 5 * 60 * 1000);
        }
    };

    private void getSanzeData() {
        mHandler.removeCallbacks(getDataRunnable);
        mHandler.postDelayed(getDataRunnable, 5000);
    }

    private UsbService usbService;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        filter.addAction(UsbService.ACTION_USB_READY);
        registerReceiver(mUsbReceiver, filter);
    }

    @Override
    public DatabaseHelper getDatabaseHelper() {
        return getHelper();
    }

    private TriggerBean handleData(DeviceMonitorEnum deviceMonitorEnum, float value) {
        if (dicMap != null && dicMap.size() != 0) {
            String code = deviceMonitorEnum.getCode();
            List<MonitorDicParameter.WarnLinesBean> list = dicMap.get(code);
            TriggerBean bean = TriggerBean.trigger(value, list);
            if (bean.exception) { //有问题的数据才存
                LogDataBean logDataBean = new LogDataBean();
                logDataBean.level = bean.level;
                logDataBean.reason = bean.reason;
                String sensorId = "";
                if (sensorMap != null && sensorMap.size() != 0) {
                    sensorId = sensorMap.get(deviceMonitorEnum.getCode());
                }
                logDataBean.sensorId = sensorId;
                logDataBean.date = System.currentTimeMillis();
                try {
                    getHelper().getLogDataDao().createOrUpdate(logDataBean);
                } catch (Exception e) {
                }
            }
            return bean;
        } else {
            TriggerBean bean2 = new TriggerBean();
            bean2.value = value;
            return bean2;
        }
    }

    public void getMonitorDicParameters() {
        String mac = "78:A3:51:2B:77:14";
        if (App.getApp().getDtuSetting() != null) {
            mac = App.getApp().getDtuSetting().mMac;
        }
        Api.getMonitorDicParameters(mac, new OnStringRespListener() {
            @Override
            public void onResponse(String data) {
                Resp resp = Resp.objectFromData(data);
                if (resp.canParse()) {
                    List<MonitorDicParameter> list = MonitorDicParameter.arrayMonitorWarnLineParameterFromData(resp.content);
                    if (list != null && list.size() != 0) {
                        HashMap<String, List<MonitorDicParameter.WarnLinesBean>> map = new HashMap();
                        for (int i = 0; i < list.size(); i++) {
                            MonitorDicParameter bean = list.get(i);
                            map.put(bean.code, bean.warnLines);
                        }
                        dicMap = map;
                        FileKit.save(ActFrame.this, map, Constants.FILE_KEY_MONITOR_DIC);
                    }
                } else {
                    onError(resp.message);
                }
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });
    }
}
