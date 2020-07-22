package com.minorfish.dtuapp.module;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.serialport.SerialPortFinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.minorfish.dtuapp.module.fragment.FmtAddSensorSetting;
import com.minorfish.dtuapp.module.fragment.FmtDTUSetting;
import com.minorfish.dtuapp.module.fragment.FmtHistoryFangshe;
import com.minorfish.dtuapp.module.fragment.FmtLog;
import com.minorfish.dtuapp.module.fragment.FmtModifyPwd;
import com.minorfish.dtuapp.module.fragment.FmtRealDataMulti;
import com.minorfish.dtuapp.module.fragment.FmtSensorSettingNew;
import com.minorfish.dtuapp.module.model.DtuOnlineDataBean;
import com.minorfish.dtuapp.module.model.FangsheHistoryBean;
import com.minorfish.dtuapp.module.model.LogDataBean;
import com.minorfish.dtuapp.module.model.MultiDataBean;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.minorfish.dtuapp.module.model.TriggerBean;
import com.minorfish.dtuapp.module.model.WorkStationOnlineDataBean;
import com.minorfish.dtuapp.mqtt.MqttPushDataService;
import com.minorfish.dtuapp.rs485.Device;
import com.minorfish.dtuapp.rs485.OnGetDataListener;
import com.minorfish.dtuapp.rs485.SerialPortManagerMulti;
import com.minorfish.dtuapp.util.MathUtil;
import com.minorfish.dtuapp.util.PreferenceKit;
import com.minorfish.dtuapp.util.Utils;
import com.tangjd.common.abs.BaseActivity;
import com.tangjd.common.abs.BaseFragment;
import com.tangjd.common.retrofit.string.OnStringRespListener;
import com.tangjd.common.utils.ByteUtil;
import com.tangjd.common.utils.FileKit;
import com.tangjd.common.utils.StringKit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: 485串口版  放射双参
 * Date: 2018/7/11
 */
public class ActFrame2 extends BaseActivity implements OnDatabaseListener {

    private static final String TAG = ActFrame2.class.getSimpleName();
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
    @Bind(R.id.text_version)
    TextView textVersion;

    private FragmentManager fm;

    public String[] mDevices;
    public List<String> mDeviceList;
    public List<SensorSettingBean> mSensorSettingList;
    public String port1 = "";
    public String port2 = "";
    public int currentUreaCod = 0;

    private Handler mIOHandler;
    private HandlerThread mIOThread;

    private HashMap<Integer, BaseFragment> mFragments = new HashMap<>();

    private HashMap<String, List<MonitorDicParameter.WarnLinesBean>> dicMap;
    public HashMap<String, String> sensorMap;

    private DatabaseHelper databaseHelper;

    /**
     * 实例化一个databaehelper
     */

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private MqttPushDataService mqttPushDataService;


    /**
     * 创建一个service 服务连接实例
     */

    private final ServiceConnection mqttConnection = new ServiceConnection() {
        /**
         * 系统调用onServiceConnected这个来传送在service的onBind()中返回的IBinder．
         */
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            mqttPushDataService = ((MqttPushDataService.MqttBinder) arg1).getService();
        }

        /**
         * Android系统在同service的连接意外丢失时调用这个．比如当service崩溃了或被强杀了．当客户端解除绑定时，这个方法不会被调用．
         */
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
        //SPManager.getInstance().putString(Constants.PREF_KEY_SENSOR_SETTING_LIST, "");

        //实例化ThreadHandler 对象
        mIOThread = new HandlerThread("IOThread");
        mIOThread.start();
        mIOHandler = new Handler(mIOThread.getLooper());

        getHelper();

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (App.getApp().getDtuSetting() != null) {
            FmtRealDataMulti fmt = new FmtRealDataMulti();
            mFragments.put(R.id.rb_real_data, fmt);
            ft.replace(R.id.fragment_container, fmt, FmtRealDataMulti.class.getName());
            ft.commit();
            dicMap = (HashMap<String, List<MonitorDicParameter.WarnLinesBean>>) FileKit.getObject(ActFrame2.this, Constants.FILE_KEY_MONITOR_DIC);
            if (dicMap == null || dicMap.size() == 0) {
                getMonitorDicParameters();
            }
        } else { //没配置dtu（数采仪）先引导去配置
            FmtRealDataMulti fmt2 = new FmtRealDataMulti();
            mFragments.put(R.id.rb_real_data, fmt2);

            FmtDTUSetting fmt = new FmtDTUSetting();
            mFragments.put(R.id.rb_dtu_setting, fmt);
            ft.replace(R.id.fragment_container, fmt, FmtDTUSetting.class.getName());
            ft.commit();
            radioGroup.check(R.id.rb_dtu_setting);
        }

        mSensorSettingList = App.getApp().getSensorSettingList();
        initDevice();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                final FragmentTransaction ft = fm.beginTransaction();
                BaseFragment fmt = mFragments.get(checkedId);
                boolean needAdd = (fmt == null);
                for (Map.Entry<Integer, BaseFragment> item : mFragments.entrySet()) {
                    ft.hide(item.getValue());
                }
                try {
                    BaseFragment fmtAdd = mFragments.get(fmtAddId);
                    if(fmtAdd!=null){
                        ft.hide(fmtAdd);
                    }
                }catch (Exception e){}
                switch (checkedId) {
                    case R.id.rb_real_data:
                        if (needAdd) {
                            fmt = new FmtRealDataMulti();
                            ft.add(R.id.fragment_container, fmt, FmtRealDataMulti.class.getName());
                        }
//                        try {
//                            ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).showData(data);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        break;
                    case R.id.rb_history:
                        if (needAdd) {
                            fmt = new FmtHistoryFangshe();
                            ft.add(R.id.fragment_container, fmt, FmtHistoryFangshe.class.getName());
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
                            fmt = new FmtSensorSettingNew();
                            ft.add(R.id.fragment_container, fmt, FmtSensorSettingNew.class.getName());
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

        if (App.getApp().getDtuSetting() != null && StringKit.isNotEmpty(App.getApp().getDtuSetting().mMac)) {
            connectMqtt();
        }
        registerReceiver(mMqttReconnectReceiver, getReceiverFilter());

        if(PreferenceKit.getBoolean(ActFrame2.this,Constants.FILE_KEY_UPDATE)){
            textVersion.setText("软件版本：v1.1");
        }
    }

    /**
     * 应用组件(客户端)可以调用bindService()绑定到一个service．Android系统之后调用service的onBind()方法，它返回一个用来与service交互的IBinder．
     * 绑定是异步的．bindService()会立即返回，它不会返回IBinder给客户端．要接收IBinder，客户端必须创建一个ServiceConnection的实例并传给bindService()．
     * ServiceConnection包含一个回调方法，系统调用这个方法来传递要返回的IBinder．
     * 注：只有activities,services,和contentproviders可以绑定到一个service—你不能从一个broadcastreceiver绑定到service．
     */

    public void connectMqtt() {
        String subscribeTopic = "monitor_multi_data";
        MqttPushDataService.actionBind(this, mqttConnection, subscribeTopic);
    }

    private int fmtAddId = -100;
    public void showAddSensorFmt() {
        FragmentTransaction ft = fm.beginTransaction();
        BaseFragment fmt = mFragments.get(R.id.rb_sensor_setting);
        ft.hide(fmt);
        BaseFragment fmtAdd = mFragments.get(fmtAddId);
        boolean needAdd = (fmtAdd == null);
        if(needAdd){
            fmtAdd = new FmtAddSensorSetting();
            ft.add(R.id.fragment_container, fmtAdd, FmtAddSensorSetting.class.getName());
            mFragments.put(fmtAddId, fmtAdd);
        }else{
            ft.show(fmtAdd);
        }
        ft.commit();
    }

    public String tabName1 = "";
    public String tabName2 = "";
    public void backToFmtSensorSetting(boolean isAdd,int index) {
        FragmentTransaction ft = fm.beginTransaction();
        BaseFragment fmtAdd = mFragments.get(fmtAddId);
        if(fmtAdd!=null){
            ft.hide(fmtAdd);
        }
        BaseFragment fmt = mFragments.get(R.id.rb_sensor_setting);
        ft.show(fmt);
        ft.commit();
        if(isAdd) {
            try {
                sensorMap = (HashMap<String, String>) FileKit.getObject(ActFrame2.this, Constants.FILE_KEY_SENSOR_DIC);//更新传感器列表
            }catch (Exception e){}
            try {
                //添加成功后打开串口
                //openOrSwitchPort();
                mSensorSettingList = App.getApp().getSensorSettingList();
                ((FmtSensorSettingNew) mFragments.get(R.id.rb_sensor_setting)).setData(true);
                if (mSensorSettingList != null && mSensorSettingList.size()!=0) { //已配置工作站
                    SensorSettingBean bean = mSensorSettingList.get(index);
                    if(bean.mEstimateType!=null) {
                        if (DeviceMonitorEnum.MONITOR_RAY.getCode().equals(bean.mEstimateType.code)) { //放射
                            if(index == 0) {
                                Device mDevice = new Device(bean.mSerialPort, "9600");
                                mSerialPortManagerMulti = new SerialPortManagerMulti();
                                openOrSwitchPort(mDevice,0);
                                port1 = bean.mSerialPort;
                                tabName1 = bean.mSensorName;
                            }else{
                                Device mDevice = new Device(bean.mSerialPort, "9600");
                                mSerialPortManagerMulti2 = new SerialPortManagerMulti();
                                openOrSwitchPort(mDevice,1);
                                port2 = bean.mSerialPort;
                                tabName2 = bean.mSensorName;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (databaseHelper != null) {
                OpenHelperManager.releaseHelper();
                databaseHelper = null;
            }
            //释放资源
            mIOThread.quit();//终止looper中的死循环操作，不在接受新的事件加入消息队列
            unbindService(mqttConnection);
            //SerialPortManagerMulti.instance().close();
            if(mSerialPortManagerMulti!=null){
                mSerialPortManagerMulti.close();
                mSerialPortManagerMulti = null;
            }
            if(mSerialPortManagerMulti2!=null){
                mSerialPortManagerMulti2.close();
                mSerialPortManagerMulti2 = null;
            }
            //MqttPushDataService.actionStop(this);

            unregisterReceiver(mMqttReconnectReceiver);
        }catch (Exception e){}
    }

    public static IntentFilter getReceiverFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_MQTT_FAIL);
        filter.addAction(Constants.ACTION_MQTT_CHANGE);
        filter.addAction(Constants.ACTION_MQTT_RESTART);
        return filter;
    }

    private BroadcastReceiver mMqttReconnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_MQTT_FAIL)) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mqttPushDataService.scheduleReconnectFromAct();
                    }
                }, 2*60*60*1000);//两小时后再尝试连接mqtt服务器
            } else if (Constants.ACTION_MQTT_CHANGE.equals(intent.getAction())){
                try {
                    PreferenceKit.putBoolean(ActFrame2.this,Constants.FILE_KEY_UPDATE,true);
                    showProgressDialog("更新中");
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);//让他显示10秒后，取消ProgressDialog
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            dismissProgressDialog();
                            showToast("更新成功");
                            ActFrame2.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textVersion.setText("软件版本：v1.1");
                                }
                            });
                        }
                    });
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Constants.ACTION_MQTT_RESTART.equals(intent.getAction())){
                try {
                    final Dialog dialog = new Dialog(ActFrame2.this, R.style.Dialog_Nor);
                    //设置是否允许Dialog可以被点击取消,也会阻止Back键
                    dialog.setCancelable(false);
                    //获取Dialog窗体的根容器
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    ViewGroup root = (ViewGroup) dialog.getWindow().getDecorView().findViewById(android.R.id.content);
                    //设置窗口大小为屏幕大小
                    WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                    Point screenSize = new Point();
                    wm.getDefaultDisplay().getSize(screenSize);
                    root.setLayoutParams(new LinearLayout.LayoutParams(screenSize.x, screenSize.y));
                    //获取自定义布局,并设置给Dialog
                    View view = inflater.inflate(R.layout.dialog_layout, root, false);
                    dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    dialog.show();
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);//让他显示10秒后，取消ProgressDialog
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            showToast("重启成功");
                        }
                    });
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //原來是 3 + 20 +2
    private static final int PACKET_LENGTH = 9;
    private volatile ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
    private volatile ByteArrayOutputStream mBaos2 = new ByteArrayOutputStream();

    private SerialPortManagerMulti mSerialPortManagerMulti = null;
    private SerialPortManagerMulti mSerialPortManagerMulti2 = null;

    interface OnGetSinglePacketListener {
        void onGetSinglePacket(byte[] singlePacket);
    }

    private HashMap<DeviceMonitorEnum, TriggerBean> data;
    private OnGetSinglePacketListener mListener = new OnGetSinglePacketListener() {
        @Override
        public synchronized void onGetSinglePacket(final byte[] singlePacket) {

            //long fangsheLong = byte2floatCDAB(singlePacket,3);
            final float fangshe = byte2floatCDAB(singlePacket,3);

            mIOHandler.post(new Runnable() {
                @Override
                public void run() {
                    long ts = System.currentTimeMillis();
                    String serialPort = port1;
                    String mac = "";
                    if (App.getApp().getDtuSetting() != null) {
                        mac = App.getApp().getDtuSetting().mMac;
                    }
                    //心跳，本机和工作台状态
                    try {
                        WorkStationOnlineDataBean workStationOnlineDataBean = new WorkStationOnlineDataBean();
                        workStationOnlineDataBean.mac = mac;
                        workStationOnlineDataBean.ts = ts;
                        workStationOnlineDataBean.port = serialPort;
                        workStationOnlineDataBean.online = "1";
                        String  workStationStr = workStationOnlineDataBean.toJson();

                        mqttPushDataService.pushData("ws_online_data", workStationStr);
                    } catch (Exception e) {
                    }

                    TriggerBean fangsheBean = handleData(DeviceMonitorEnum.MONITOR_RAY_TOGETHER, fangshe);

                    data = new HashMap<DeviceMonitorEnum, TriggerBean>();
                    data.put(DeviceMonitorEnum.MONITOR_RAY_TOGETHER, fangsheBean);

                    try {
                        ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).showData(data,0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // upload and save
                    FangsheHistoryBean bean = new FangsheHistoryBean();
                    bean.sensorName = tabName1;
                    bean.value = fangsheBean.value;
                    bean.valueDeta = fangsheBean.deta;
                    bean.valueLevel = fangsheBean.level;
                    bean.date = ts;
                    try {
                        getHelper().getFangsheDataHistoryDao().createOrUpdate(bean);
                    } catch (Exception e) {
                    }
                    try {
                        MultiDataBean dataBean = new MultiDataBean();
                        MultiDataBean.SensorDataBean sensorData = new MultiDataBean.SensorDataBean();
                        sensorData.code = DeviceMonitorEnum.MONITOR_RAY_TOGETHER.getCode();
                        sensorData.value = fangshe + "";
                        dataBean.addSensorData(sensorData);
                        dataBean.mac = mac;
                        dataBean.port = serialPort;
                        dataBean.ts = ts;
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

    private OnGetSinglePacketListener mListener2 = new OnGetSinglePacketListener() {
        @Override
        public synchronized void onGetSinglePacket(final byte[] singlePacket) {

            //long fangsheLong = Utils.byte2Long(singlePacket,3);
            final float fangshe = byte2floatCDAB(singlePacket,3);

            mIOHandler.post(new Runnable() {
                @Override
                public void run() {
                    long ts = System.currentTimeMillis();
                    String serialPort = port2;
                    String mac = "";
                    if (App.getApp().getDtuSetting() != null) {
                        mac = App.getApp().getDtuSetting().mMac;
                    }
                    //心跳，本机和工作台状态
                    try {
                        WorkStationOnlineDataBean workStationOnlineDataBean = new WorkStationOnlineDataBean();
                        workStationOnlineDataBean.mac = mac;
                        workStationOnlineDataBean.ts = ts;
                        workStationOnlineDataBean.port = serialPort;
                        workStationOnlineDataBean.online = "1";
                        String  workStationStr = workStationOnlineDataBean.toJson();

                        mqttPushDataService.pushData("ws_online_data", workStationStr);
                    } catch (Exception e) {
                    }

                    TriggerBean fangsheBean = handleData(DeviceMonitorEnum.MONITOR_RAY_TOGETHER, fangshe);

                    data = new HashMap<DeviceMonitorEnum, TriggerBean>();
                    data.put(DeviceMonitorEnum.MONITOR_RAY_TOGETHER, fangsheBean);

                    try {
                        ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).showData(data,1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // upload and save
                    FangsheHistoryBean bean = new FangsheHistoryBean();
                    bean.sensorName = tabName2;
                    bean.value = fangsheBean.value;
                    bean.valueDeta = fangsheBean.deta;
                    bean.valueLevel = fangsheBean.level;
                    bean.date = ts;
                    try {
                        getHelper().getFangsheDataHistoryDao().createOrUpdate(bean);
                    } catch (Exception e) {
                    }
                    try {
                        MultiDataBean dataBean = new MultiDataBean();
                        MultiDataBean.SensorDataBean sensorData = new MultiDataBean.SensorDataBean();
                        sensorData.code = DeviceMonitorEnum.MONITOR_RAY_TOGETHER.getCode();
                        sensorData.value = fangshe + "";
                        dataBean.addSensorData(sensorData);
                        dataBean.mac = mac;
                        dataBean.port = serialPort;
                        dataBean.ts = ts;
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

    private OnGetDataListener mOnGetDataListener = new OnGetDataListener() {
        @Override
        public void OnGetData485(byte[] buffer) {
            parsePacket(buffer, mListener);
        }
    };

    private OnGetDataListener mOnGetDataListener2 = new OnGetDataListener() {
        @Override
        public void OnGetData485(byte[] buffer) {
            parsePacket2(buffer, mListener2);
        }
    };

    private synchronized void parsePacket(byte[] buffer, OnGetSinglePacketListener listener) {
        mBaos.write(buffer, 0, buffer.length);
        if (mBaos.size() == 0) {
            return;
        }
        buffer = mBaos.toByteArray();
        int correctPacketStartIndex = 0;
        for (int i = 0; i < buffer.length - 2; i++) {
            if (buffer[i] == 0x01 && buffer[i + 1] == 0x04 && buffer[i + 2] == (byte)0x04) {
                correctPacketStartIndex = i;
            }
        }
        if (buffer.length < PACKET_LENGTH + correctPacketStartIndex) {
            return;
        }
        listener.onGetSinglePacket(ByteUtil.subByteArr(buffer, correctPacketStartIndex, PACKET_LENGTH));
        mBaos.reset();
        mBaos.write(buffer, correctPacketStartIndex + PACKET_LENGTH, buffer.length - (correctPacketStartIndex + PACKET_LENGTH));
    }

    private synchronized void parsePacket2(byte[] buffer, OnGetSinglePacketListener listener) {
        mBaos2.write(buffer, 0, buffer.length);
        if (mBaos2.size() == 0) {
            return;
        }
        buffer = mBaos2.toByteArray();
        int correctPacketStartIndex = 0;
        for (int i = 0; i < buffer.length - 2; i++) {
            if (buffer[i] == 0x01 && buffer[i + 1] == 0x04 && buffer[i + 2] == (byte)0x04) {
                correctPacketStartIndex = i;
            }
        }
        if (buffer.length < PACKET_LENGTH + correctPacketStartIndex) {
            return;
        }
        listener.onGetSinglePacket(ByteUtil.subByteArr(buffer, correctPacketStartIndex, PACKET_LENGTH));
        mBaos2.reset();
        mBaos2.write(buffer, correctPacketStartIndex + PACKET_LENGTH, buffer.length - (correctPacketStartIndex + PACKET_LENGTH));
    }

    private Handler mHandler = new Handler();
    private Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("TTTTTT", "getSanzeData");
            sendData();
            mHandler.postDelayed(this, 5 * 60 * 1000);
        }
    };

    private void sendData() {
        //C7F5
        //SerialPortManager.instance().sendCommand(new byte[]{0x01, 0x03, 0x00, 0x10, 0x00, 0x10, 0x45, (byte) 0xC3});
        //01 03 00 00 00 02 C4 0B        01 03 00 00 00 02 C4 0B
        if(mSerialPortManagerMulti!=null) {
            mSerialPortManagerMulti.sendCommand(new byte[]{0x01, 0x04, 0x00,  (byte)0x00, (byte)0x00, 0x02, (byte) 0x71, (byte) 0xCB});
        }
        if(mSerialPortManagerMulti2!=null){
            mSerialPortManagerMulti2.sendCommand(new byte[]{0x01, 0x04, 0x00,  (byte)0x00, (byte)0x00, 0x02, (byte) 0x71, (byte) 0xCB});
        }
        //心跳，本机状态
        try {
            String mac = "";
            if (App.getApp().getDtuSetting() != null) {
                mac = App.getApp().getDtuSetting().mMac;
            }
            long ts = System.currentTimeMillis();
            DtuOnlineDataBean dtuOnlineDataBean = new DtuOnlineDataBean();
            dtuOnlineDataBean.mac = mac;
            dtuOnlineDataBean.ts = ts;
            dtuOnlineDataBean.online = "1";
            String dtuOnlineStr = dtuOnlineDataBean.toJson();
            com.tangjd.common.utils.Log.e(TAG,"sendData: "+dtuOnlineStr);
            mqttPushDataService.pushData("dtu_online_data", dtuOnlineStr);
        } catch (Exception e) {
        }
    }

    private void getSanzeData() {
        mHandler.removeCallbacks(getDataRunnable);
        mHandler.postDelayed(getDataRunnable, 5000);
    }

    private void initDevice() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        mDevices = serialPortFinder.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[]{"找不到串口设备"};
        }
        mDeviceList = transferArrayToList(mDevices);
        mDeviceList.add(0, "未选择");
        openDevice();
    }

    private void openDevice() {
        if (mSensorSettingList != null && mSensorSettingList.size()!=0) { //已配置工作站
            for(int i=0;i<mSensorSettingList.size();i++){
                SensorSettingBean bean = mSensorSettingList.get(i);
                String str=new Gson().toJson(bean);
                com.tangjd.common.utils.Log.e(TAG,"openDevice: "+str);
                if(bean.mEstimateType!=null) {
                    if (DeviceMonitorEnum.MONITOR_RAY.getCode().equals(bean.mEstimateType.code)) {//放射
                        if(i == 0) {
                            Device mDevice = new Device(bean.mSerialPort, "9600");
                            mSerialPortManagerMulti = new SerialPortManagerMulti();
                            openOrSwitchPort(mDevice,0);
                            port1 = bean.mSerialPort;
                            tabName1 = bean.mSensorName;
                        }else{
                            Device mDevice = new Device(bean.mSerialPort, "9600");
                            mSerialPortManagerMulti2 = new SerialPortManagerMulti();
                            openOrSwitchPort(mDevice,1);
                            port2 = bean.mSerialPort;
                            tabName2 = bean.mSensorName;
                        }
                    }
                }
            }
        }
    }

    private static List<String> transferArrayToList(String[] array){
        List<String> transferedList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            transferedList.add(array[i]);
        }
        return transferedList;
    }

    public void openOrSwitchPort(Device mDevice, int type) {
        boolean mOpened = false;
        if(type == 0){
            if(mSerialPortManagerMulti == null){
                mSerialPortManagerMulti = new SerialPortManagerMulti();
            }
            mSerialPortManagerMulti.setOnGetDataListener(mOnGetDataListener);
            mOpened = mSerialPortManagerMulti.open(mDevice) != null;
        }else{
            if(mSerialPortManagerMulti2 == null){
                mSerialPortManagerMulti2 = new SerialPortManagerMulti();
            }
            mSerialPortManagerMulti2.setOnGetDataListener(mOnGetDataListener2);
            mOpened = mSerialPortManagerMulti2.open(mDevice) != null;
        }
        if (mOpened) {
            Toast.makeText(this, "成功打开串口", Toast.LENGTH_LONG).show();
            getSanzeData();
        } else {
            Toast.makeText(this, "打开串口失败", Toast.LENGTH_LONG).show();
        }
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
            bean2.value = MathUtil.scaleRoundHalfUp(value, 2);
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
                String str=new Gson().toJson(resp);
                com.tangjd.common.utils.Log.e(TAG,"getMonitorDicParameters-Api.getMonitorDicParameters: "+str);
                if (resp.canParse()) {
                    List<MonitorDicParameter> list = MonitorDicParameter.arrayMonitorWarnLineParameterFromData(resp.content);
                    if (list != null && list.size() != 0) {
                        HashMap<String, List<MonitorDicParameter.WarnLinesBean>> map = new HashMap();
                        for (int i = 0; i < list.size(); i++) {
                            MonitorDicParameter bean = list.get(i);
                            map.put(bean.code, bean.warnLines);
                        }
                        dicMap = map;
                        FileKit.save(ActFrame2.this, map, Constants.FILE_KEY_MONITOR_DIC);
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


    //友盟
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
