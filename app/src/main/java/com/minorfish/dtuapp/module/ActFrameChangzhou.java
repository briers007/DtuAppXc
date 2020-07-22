//package com.minorfish.dtuapp.module;
//
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.serialport.SerialPortFinder;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.util.Log;
//import android.widget.FrameLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//import com.j256.ormlite.android.apptools.OpenHelperManager;
//import com.minorfish.dtuapp.R;
//import com.minorfish.dtuapp.abs.Api;
//import com.minorfish.dtuapp.abs.App;
//import com.minorfish.dtuapp.abs.Constants;
//import com.minorfish.dtuapp.abs.Resp;
//import com.minorfish.dtuapp.database.DatabaseHelper;
//import com.minorfish.dtuapp.database.OnDatabaseListener;
//import com.minorfish.dtuapp.module.dicts.DeviceMonitorEnum;
//import com.minorfish.dtuapp.module.dicts.MonitorDicParameter;
//import com.minorfish.dtuapp.module.fragment.FmtAddSensorSetting;
//import com.minorfish.dtuapp.module.fragment.FmtDTUSetting;
//import com.minorfish.dtuapp.module.fragment.FmtHistoryChangzhou;
//import com.minorfish.dtuapp.module.fragment.FmtLog;
//import com.minorfish.dtuapp.module.fragment.FmtModifyPwd;
//import com.minorfish.dtuapp.module.fragment.FmtRealDataChangzhou;
//import com.minorfish.dtuapp.module.fragment.FmtSensorSettingNew;
//import com.minorfish.dtuapp.module.model.ChangzhouDataHistoryBean;
//import com.minorfish.dtuapp.module.model.DtuOnlineDataBean;
//import com.minorfish.dtuapp.module.model.LogDataBean;
//import com.minorfish.dtuapp.module.model.MultiDataBean;
//import com.minorfish.dtuapp.module.model.RealDataHistoryBean;
//import com.minorfish.dtuapp.module.model.SensorSettingBean;
//import com.minorfish.dtuapp.module.model.TriggerBean;
//import com.minorfish.dtuapp.module.model.UreaDataHistoryBean;
//import com.minorfish.dtuapp.module.model.WorkStationOnlineDataBean;
//import com.minorfish.dtuapp.mqtt.MqttPushDataService;
//import com.minorfish.dtuapp.rs485.Device;
//import com.minorfish.dtuapp.rs485.OnGetDataListener;
//import com.minorfish.dtuapp.rs485.SerialPortManagerMulti;
//import com.minorfish.dtuapp.util.MathUtil;
//import com.minorfish.dtuapp.util.Utils;
//import com.tangjd.common.abs.BaseActivity;
//import com.tangjd.common.abs.BaseFragment;
//import com.tangjd.common.retrofit.string.OnStringRespListener;
//import com.tangjd.common.utils.ByteUtil;
//import com.tangjd.common.utils.FileKit;
//import com.tangjd.common.utils.StringKit;
//import com.umeng.analytics.MobclickAgent;
//
//import java.io.ByteArrayOutputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//
///**
// * Author: 485串口版（多个监测站支持）常州六参饮用水
// * Date: 2018/7/11
// */
//public class ActFrame2 extends BaseActivity implements OnDatabaseListener {
//
//    @Bind(R.id.fragment_container)
//    FrameLayout fragmentContainer;
//    @Bind(R.id.rb_real_data)
//    RadioButton rbRealData;
//    @Bind(R.id.rb_history)
//    RadioButton rbHistory;
//    @Bind(R.id.rb_dtu_setting)
//    RadioButton rbDtuSetting;
//    @Bind(R.id.rb_sensor_setting)
//    RadioButton rbSensorSetting;
//    @Bind(R.id.rb_log)
//    RadioButton rbLog;
//    @Bind(R.id.rb_modify_pwd)
//    RadioButton rbModifyPwd;
//    @Bind(R.id.radio_group)
//    RadioGroup radioGroup;
//
//    private FragmentManager fm;
//
//    public String[] mDevices;
//    public List<String> mDeviceList;
//    public List<SensorSettingBean> mSensorSettingList;
//    public String port1 = "";
//    public String port2 = "";
//    public String tabName1 = "";
//    public String tabName2 = "";
//
//    private Handler mIOHandler;
//    private HandlerThread mIOThread;
//
//    private HashMap<Integer, BaseFragment> mFragments = new HashMap<>();
//
//    private HashMap<String, List<MonitorDicParameter.WarnLinesBean>> dicMap;
//    public HashMap<String, String> sensorMap;
//
//    private DatabaseHelper databaseHelper;
//
//    private DatabaseHelper getHelper() {
//        if (databaseHelper == null) {
//            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
//        }
//        return databaseHelper;
//    }
//
//    private MqttPushDataService mqttPushDataService;
//    private final ServiceConnection mqttConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
//            mqttPushDataService = ((MqttPushDataService.MqttBinder) arg1).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mqttPushDataService = null;
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_frame_layout);
//        ButterKnife.bind(this);
//        //SPManager.getInstance().putString(Constants.PREF_KEY_SENSOR_SETTING_LIST, "");
//
//        mIOThread = new HandlerThread("IOThread");
//        mIOThread.start();
//        mIOHandler = new Handler(mIOThread.getLooper());
//
//        getHelper();
//
//        fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//
//        if (App.getApp().getDtuSetting() != null) {
//            FmtRealDataChangzhou fmt = new FmtRealDataChangzhou();
//            mFragments.put(R.id.rb_real_data, fmt);
//            ft.replace(R.id.fragment_container, fmt, FmtRealDataChangzhou.class.getName());
//            ft.commit();
//            dicMap = (HashMap<String, List<MonitorDicParameter.WarnLinesBean>>) FileKit.getObject(ActFrame2.this, Constants.FILE_KEY_MONITOR_DIC);
//            if (dicMap == null || dicMap.size() == 0) {
//                getMonitorDicParameters();
//            }
//
//        } else { //没配置dtu先引导去配置
//            FmtDTUSetting fmt = new FmtDTUSetting();
//            mFragments.put(R.id.rb_dtu_setting, fmt);
//            ft.replace(R.id.fragment_container, fmt, FmtDTUSetting.class.getName());
//            ft.commit();
//            radioGroup.check(R.id.rb_dtu_setting);
//        }
//
//        mSensorSettingList = App.getApp().getSensorSettingList();
//        initDevice();
//
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                final FragmentTransaction ft = fm.beginTransaction();
//                BaseFragment fmt = mFragments.get(checkedId);
//                boolean needAdd = (fmt == null);
//                for (Map.Entry<Integer, BaseFragment> item : mFragments.entrySet()) {
//                    ft.hide(item.getValue());
//                }
//                try {
//                    BaseFragment fmtAdd = mFragments.get(fmtAddId);
//                    if(fmtAdd!=null){
//                        ft.hide(fmtAdd);
//                    }
//                }catch (Exception e){}
//                switch (checkedId) {
//                    case R.id.rb_real_data:
//                        if (needAdd) {
//                            fmt = new FmtRealDataChangzhou();
//                            ft.add(R.id.fragment_container, fmt, FmtRealDataChangzhou.class.getName());
//                        }
//                        try {
//                            ((FmtRealDataChangzhou) mFragments.get(R.id.rb_real_data)).showData(data);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    case R.id.rb_history:
//                        if (needAdd) {
//                            fmt = new FmtHistoryChangzhou();
//                            ft.add(R.id.fragment_container, fmt, FmtHistoryChangzhou.class.getName());
//                        }
//                        break;
//                    case R.id.rb_dtu_setting:
//                        if (needAdd) {
//                            fmt = new FmtDTUSetting();
//                            ft.add(R.id.fragment_container, fmt, FmtDTUSetting.class.getName());
//                        }
//                        break;
//                    case R.id.rb_sensor_setting:
//                        if (needAdd) {
//                            fmt = new FmtSensorSettingNew();
//                            ft.add(R.id.fragment_container, fmt, FmtSensorSettingNew.class.getName());
//                        }
//                        break;
//                    case R.id.rb_log:
//                        if (needAdd) {
//                            fmt = new FmtLog();
//                            ft.add(R.id.fragment_container, fmt, FmtLog.class.getName());
//                        }
//                        break;
//                    case R.id.rb_modify_pwd:
//                        if (needAdd) {
//                            fmt = new FmtModifyPwd();
//                            ft.add(R.id.fragment_container, fmt, FmtModifyPwd.class.getName());
//                        }
//                        break;
//                }
//                if (needAdd) {
//                    mFragments.put(checkedId, fmt);
//                } else {
//                    ft.show(fmt);
//                }
//                ft.commit();
//            }
//        });
//
//        if (App.getApp().getDtuSetting() != null && StringKit.isNotEmpty(App.getApp().getDtuSetting().mMac)) {
//            connectMqtt();
//        }
//        registerReceiver(mMqttReconnectReceiver, getReceiverFilter());
//    }
//
//    public void connectMqtt() {
//        String subscribeTopic = "monitor_multi_data";
//        //String subscribeTopic = "$client/"+ "79:E2:49:45:62:92";
//        //String subscribeTopic = "$client/"+ App.getApp().getDtuSetting().mMac;
//        MqttPushDataService.actionBind(this, mqttConnection, subscribeTopic);
//    }
//
//    private int fmtAddId = -100;
//    public void showAddSensorFmt() {
//        FragmentTransaction ft = fm.beginTransaction();
//        BaseFragment fmt = mFragments.get(R.id.rb_sensor_setting);
//        ft.hide(fmt);
//        BaseFragment fmtAdd = mFragments.get(fmtAddId);
//        boolean needAdd = (fmtAdd == null);
//        if(needAdd){
//            fmtAdd = new FmtAddSensorSetting();
//            ft.add(R.id.fragment_container, fmtAdd, FmtAddSensorSetting.class.getName());
//            mFragments.put(fmtAddId, fmtAdd);
//        }else{
//            ft.show(fmtAdd);
//        }
//        ft.commit();
//    }
//
//    public void backToFmtSensorSetting(boolean isAdd,int index) {
//        FragmentTransaction ft = fm.beginTransaction();
//        BaseFragment fmtAdd = mFragments.get(fmtAddId);
//        if(fmtAdd!=null){
//            ft.hide(fmtAdd);
//        }
//        BaseFragment fmt = mFragments.get(R.id.rb_sensor_setting);
//        ft.show(fmt);
//        ft.commit();
//        if(isAdd) {
//            try {
//                sensorMap = (HashMap<String, String>) FileKit.getObject(ActFrame2.this, Constants.FILE_KEY_SENSOR_DIC);//更新传感器列表
//            }catch (Exception e){}
//            try {
//                //添加成功后打开串口
//                //openOrSwitchPort();
//                mSensorSettingList = App.getApp().getSensorSettingList();
//                ((FmtSensorSettingNew) mFragments.get(R.id.rb_sensor_setting)).setData(true);
//                if (mSensorSettingList != null && mSensorSettingList.size()!=0) { //已配置工作站
//                    SensorSettingBean bean = mSensorSettingList.get(index);
//                    if(bean.mEstimateType!=null) {
//                        if (DeviceMonitorEnum.MONITOR_WATER_DRINK.getCode().equals(bean.mEstimateType.code)) { //饮用水
//                            if (bean.mSubTypeBeans != null  && bean.mSubTypeBeans.size() != 0  && ( DeviceMonitorEnum.MONITOR_WATER_DRINK_TOC.getCode().equals(bean.mSubTypeBeans.get(0).code)
//                                    || DeviceMonitorEnum.MONITOR_WATER_DRINK_CL.getCode().equals(bean.mSubTypeBeans.get(0).code)
//                                    || DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB.getCode().equals(bean.mSubTypeBeans.get(0).code) )
//                                    ) { //4-20ma
//                                Device mDevice = new Device(bean.mSerialPort, "115200");
//                                mSerialPortManagerMultiUrea = new SerialPortManagerMulti();
//                                openOrSwitchPort(mDevice, 1);
//                                port2 = bean.mSerialPort;
//                            } else { //三参
//                                Device mDevice = new Device(bean.mSerialPort, "9600");
//                                mSerialPortManagerMulti = new SerialPortManagerMulti();
//                                openOrSwitchPort(mDevice, 0);
//                                port1 = bean.mSerialPort;
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            if (databaseHelper != null) {
//                OpenHelperManager.releaseHelper();
//                databaseHelper = null;
//            }
//            //释放资源
//            mIOThread.quit();
//            unbindService(mqttConnection);
//            if(mSerialPortManagerMulti!=null){
//                mSerialPortManagerMulti.close();
//                mSerialPortManagerMulti = null;
//            }
//            if(mSerialPortManagerMultiUrea!=null){
//                mSerialPortManagerMultiUrea.close();
//                mSerialPortManagerMultiUrea = null;
//            }
//            //MqttPushDataService.actionStop(this);
//
//            unregisterReceiver(mMqttReconnectReceiver);
//        }catch (Exception e){}
//    }
//
//    public static IntentFilter getReceiverFilter() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Constants.ACTION_MQTT_FAIL);
//        return filter;
//    }
//
//    private BroadcastReceiver mMqttReconnectReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Constants.ACTION_MQTT_FAIL)) {
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mqttPushDataService.scheduleReconnectFromAct();
//                    }
//                }, 2*60*60*1000);//两小时后再尝试连接mqtt服务器
//            }
//        }
//    };
//
//    //原來是 3 + 32 +2 位，现在加了18位数据，要拿水位状态，所以是55位
//    private static final int PACKET_LENGTH = 55;
//    private static final int PACKET_LENGTH_UREA = 21;
//    private volatile ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
//    private volatile ByteArrayOutputStream mBaosUrea = new ByteArrayOutputStream();
//
//    private SerialPortManagerMulti mSerialPortManagerMulti = null; //三参数
//    private SerialPortManagerMulti mSerialPortManagerMultiUrea = null; //4-20ma
//
//    interface OnGetSinglePacketListener {
//        void onGetSinglePacket(byte[] singlePacket);
//    }
//
//    private HashMap<DeviceMonitorEnum, TriggerBean> data = new HashMap<DeviceMonitorEnum, TriggerBean>();
//    private ChangzhouDataHistoryBean bean = new ChangzhouDataHistoryBean();
//
//    private OnGetSinglePacketListener mListener = new OnGetSinglePacketListener() {
//        @Override
//        public synchronized void onGetSinglePacket(final byte[] singlePacket) {
//            //String hexStr = com.minorfish.dtuapp.rs485.ByteUtil.bytes2HexStr(singlePacket, 0, singlePacket.length);
//            final float cond = Utils.getFloat(singlePacket, 0);
//            final float ph = Utils.getFloat(singlePacket, 4);
//            final float temperature = Utils.getFloat(singlePacket, 8);
//
//            mIOHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    long ts = System.currentTimeMillis();
//                    String serialPort = port1;
//                    String mac = "";
//                    if (App.getApp().getDtuSetting() != null) {
//                        mac = App.getApp().getDtuSetting().mMac;
//                    }
//                    //心跳，本机和工作台状态
//                    try {
//                        WorkStationOnlineDataBean workStationOnlineDataBean = new WorkStationOnlineDataBean();
//                        workStationOnlineDataBean.mac = mac;
//                        workStationOnlineDataBean.ts = ts;
//                        workStationOnlineDataBean.port = serialPort;
//                        workStationOnlineDataBean.online = "1";
//                        String  workStationStr = workStationOnlineDataBean.toJson();
//
//                        mqttPushDataService.pushData("ws_online_data", workStationStr);
//                    } catch (Exception e) {
//                    }
//
//                    TriggerBean condBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_COND, cond);
//                    TriggerBean phBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_PH, ph);
//                    TriggerBean temperatureBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_TEMPERATURE, temperature);
//
//                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_COND, condBean);
//                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_PH, phBean);
//                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_TEMPERATURE, temperatureBean);
//                    try {
//                        ((FmtRealDataChangzhou) mFragments.get(R.id.rb_real_data)).showData(data);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    // upload and save
//                    bean.cond = condBean.value;
//                    bean.condDeta = condBean.deta;
//                    bean.condLevel = condBean.level;
//                    bean.temperature = temperatureBean.value;
//                    bean.temperatureDeta = temperatureBean.deta;
//                    bean.temperatureLevel = temperatureBean.level;
//                    bean.ph = phBean.value;
//                    bean.phDeta = phBean.deta;
//                    bean.phLevel = phBean.level;
//                    if (bean.toc!=0.0d && bean.yulv!=0.0d) { //六参都拿到的时候，再保存数据
//                        bean.date = ts;
//                        try {
//                            getHelper().getChangzhouDataHistoryDao().createOrUpdate(bean);
//                        } catch (Exception e) {
//                        }
//                        bean = new ChangzhouDataHistoryBean(); //清空数据
//                    }
//                    try {
//                        MultiDataBean dataBean = new MultiDataBean();
//                        MultiDataBean.SensorDataBean sensorData = new MultiDataBean.SensorDataBean();
//                        sensorData.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_PH.getCode();
//                        sensorData.value = ph + "";
//                        MultiDataBean.SensorDataBean sensorData2 = new MultiDataBean.SensorDataBean();
//                        sensorData2.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_TEMPERATURE.getCode();
//                        sensorData2.value = temperature + "";
//                        MultiDataBean.SensorDataBean sensorData3 = new MultiDataBean.SensorDataBean();
//                        sensorData3.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_COND.getCode();
//                        sensorData3.value = cond + "";
//                        dataBean.addSensorData(sensorData);
//                        dataBean.addSensorData(sensorData2);
//                        dataBean.addSensorData(sensorData3);
//                        dataBean.mac = mac;
//                        dataBean.port = serialPort;
//                        dataBean.ts = ts;
//                        String message = dataBean.toJson();
//                        if (mqttPushDataService != null) {
//                            mqttPushDataService.pushData("monitor_multi_data", message);
//                        }
//                    } catch (Exception e) {
//                    }
//                }
//            });
//        }
//    };
//
//    private OnGetSinglePacketListener mListenerUrea = new OnGetSinglePacketListener() {
//        @Override
//        public synchronized void onGetSinglePacket(final byte[] singlePacket) {
//            int tocInt = Utils.byte2Int(singlePacket, 3);
//            int zhuoduInt = Utils.byte2Int(singlePacket, 5);
//            int yulvInt = Utils.byte2Int(singlePacket, 7);
//
//            final float toc = (tocInt * 0.004884f -4f) *(5f/16f);
//            final float yulv = (yulvInt * 0.004884f -4f) *(5f/16f);
//            final float zhuodu =(zhuoduInt * 0.004884f -4f) *(5f/16f);
//
//            mIOHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    long ts = System.currentTimeMillis();
//                    String serialPort = port2;
//                    String mac = "";
//                    if (App.getApp().getDtuSetting() != null) {
//                        mac = App.getApp().getDtuSetting().mMac;
//                    }
//                    //心跳，本机和工作台状态
//                    try {
//                        WorkStationOnlineDataBean workStationOnlineDataBean = new WorkStationOnlineDataBean();
//                        workStationOnlineDataBean.mac = mac;
//                        workStationOnlineDataBean.ts = ts;
//                        workStationOnlineDataBean.port = serialPort;
//                        workStationOnlineDataBean.online = "1";
//                        String  workStationStr = workStationOnlineDataBean.toJson();
//
//                        mqttPushDataService.pushData("ws_online_data", workStationStr);
//                    } catch (Exception e) {
//                    }
//
//                    TriggerBean tocBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_TOC, toc);
//                    TriggerBean yulvBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_CL, yulv);
//                    TriggerBean zhuoduBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB, zhuodu);
//
//                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_TOC, tocBean);
//                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_CL, yulvBean);
//                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB, zhuoduBean);
//                    try {
//                        ((FmtRealDataChangzhou) mFragments.get(R.id.rb_real_data)).showData(data);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    // upload and save
//                    bean.toc = tocBean.value;
//                    bean.tocDeta = tocBean.deta;
//                    bean.tocLevel = tocBean.level;
//                    bean.yulv = yulvBean.value;
//                    bean.yulvDeta = yulvBean.deta;
//                    bean.yulvLevel = yulvBean.level;
//                    bean.zhuodu = zhuoduBean.value;
//                    bean.zhuoduDeta = zhuoduBean.deta;
//                    bean.zhuoduLevel = zhuoduBean.level;
//                    if (bean.ph!=0.0d && bean.cond!=0.0d) { //六参都拿到的时候，再保存数据
//                        bean.date = ts;
//                        try {
//                            getHelper().getChangzhouDataHistoryDao().createOrUpdate(bean);
//                        } catch (Exception e) {
//                        }
//                        bean = new ChangzhouDataHistoryBean(); //清空数据
//                    }
//
//                    try {
//                        MultiDataBean dataBean = new MultiDataBean();
//                        MultiDataBean.SensorDataBean sensorData = new MultiDataBean.SensorDataBean();
//                        sensorData.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_TOC.getCode();
//                        sensorData.value = toc + "";
//                        MultiDataBean.SensorDataBean sensorData2 = new MultiDataBean.SensorDataBean();
//                        sensorData2.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_CL.getCode();
//                        sensorData2.value = yulv + "";
//                        MultiDataBean.SensorDataBean sensorData3 = new MultiDataBean.SensorDataBean();
//                        sensorData3.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB.getCode();
//                        sensorData3.value = zhuodu + "";
//                        dataBean.addSensorData(sensorData);
//                        dataBean.addSensorData(sensorData2);
//                        dataBean.addSensorData(sensorData3);
//                        dataBean.mac = mac;
//                        dataBean.port = serialPort;
//                        dataBean.ts = ts;
//                        String message = dataBean.toJson();
//                        if (mqttPushDataService != null) {
//                            mqttPushDataService.pushData("monitor_multi_data", message);
//                        }
//                    } catch (Exception e) {
//                    }
//                }
//            });
//        }
//    };
//
//    private OnGetDataListener mOnGetDataListener = new OnGetDataListener() {
//        @Override
//        public void OnGetData485(byte[] buffer) {
//            try {
//                parsePacket(buffer, mListener);
//            }catch (Exception e){}
//        }
//    };
//
//    private OnGetDataListener mOnGetDataListenerUrea = new OnGetDataListener() {
//        @Override
//        public void OnGetData485(byte[] buffer) {
//            parsePacketUrea(buffer, mListenerUrea);
//        }
//    };
//
//    byte[] bufferThree;
//    int dataGet = 0;
//    long lastThreeTime = 0;
//    long thisTime = 0;
//    private synchronized void parsePacket(byte[] buffer, ActFrame2.OnGetSinglePacketListener listener) {
//        //String hexStr = com.minorfish.dtuapp.rs485.ByteUtil.bytes2HexStr(buffer, 0, buffer.length);
//        if (buffer!=null && buffer.length>4) {
//            if (buffer[0] == 0x02 && buffer[1] == 0x03 && buffer[2] == (byte) 0x04) {
//                // 02 03 04 00 00 00 00 C933
//                if (buffer.length >= 9 && buffer[7] != (byte) 0xC9) {
//                    bufferThree = new byte[12];
//                    bufferThree[0] = buffer[3];
//                    bufferThree[1] = buffer[4];
//                    bufferThree[2] = buffer[5];
//                    bufferThree[3] = buffer[6];
//                }
//            } else if (buffer[0] == 0x04 && buffer[1] == 0x03 && buffer[2] == (byte) 0x00 && buffer[3] == (byte) 0x01) {
//                dataGet = 1; //ph
//            } else if (buffer[0] == 0x04 && buffer[1] == 0x03 && buffer[2] == (byte) 0x04 && dataGet == 1) {
//                if (buffer.length > 6) {
//                    bufferThree[4] = buffer[3];
//                    bufferThree[5] = buffer[4];
//                    bufferThree[6] = buffer[5];
//                    bufferThree[7] = buffer[6];
//                    dataGet = 2;
//                }
//            } else if (buffer[0] == 0x04 && buffer[1] == 0x03 && buffer[2] == (byte) 0x00 && buffer[3] == (byte) 0x06 && dataGet == 2) {
//                dataGet = 3;
//            } else if (buffer[0] == 0x04 && buffer[1] == 0x03 && buffer[2] == (byte) 0x04 && dataGet == 3) {
//                if (buffer.length > 6) {
//                    thisTime = System.currentTimeMillis();
//                    bufferThree[8] = buffer[3];
//                    bufferThree[9] = buffer[4];
//                    bufferThree[10] = buffer[5];
//                    bufferThree[11] = buffer[6];
//                    dataGet = 0;
//                    if (thisTime - lastThreeTime >= 5 * 60 * 1000) {
//                        lastThreeTime = System.currentTimeMillis();
//                        listener.onGetSinglePacket(bufferThree);
//                    }
//                }
//            }
//        }
//    }
//
//    private synchronized void parsePacketUrea(byte[] buffer, OnGetSinglePacketListener listener) {
//        mBaosUrea.write(buffer, 0, buffer.length);
//        if (mBaosUrea.size() == 0) {
//            return;
//        }
//
//        buffer = mBaosUrea.toByteArray();
//        int correctPacketStartIndex = 0;
//        for (int i = 0; i < buffer.length - 2; i++) {
//            if (buffer[i] == 0x01 && buffer[i + 1] == 0x04 && buffer[i + 2] == 0x10) {
//                correctPacketStartIndex = i;
//            }
//        }
//        // 01 03 04 00 02 03 04 ** **
//        if (buffer.length < PACKET_LENGTH_UREA + correctPacketStartIndex) {
//            return;
//        }
//        listener.onGetSinglePacket(ByteUtil.subByteArr(buffer, correctPacketStartIndex, PACKET_LENGTH_UREA));
//        mBaosUrea.reset();
//        mBaosUrea.write(buffer, correctPacketStartIndex + PACKET_LENGTH_UREA, buffer.length - (correctPacketStartIndex + PACKET_LENGTH_UREA));
//    }
//
//    private Handler mHandler = new Handler();
//    private Runnable getDataRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Log.e("TTTTTT", "getSanzeData");
//            sendData();
//            mHandler.postDelayed(this, 5 * 60 * 1000);
//        }
//    };
//
//    private void sendData() {
//        //C7F5
//        //SerialPortManager.instance().sendCommand(new byte[]{0x01, 0x03, 0x00, 0x10, 0x00, 0x10, 0x45, (byte) 0xC3});
//        // 01 03 00 00 00 06 C5 C8
////        if(mSerialPortManagerMulti!=null) {
////            //mSerialPortManagerMulti.sendCommand(new byte[]{0x01, 0x03, 0x00, 0x00, 0x00, 0x06, (byte) 0xC5, (byte) 0xC8});
////            mSerialPortManagerMulti.sendCommand(new byte[]{0x01, 0x03, 0x00, 0x07, 0x00, 0x19, (byte) 0x35, (byte) 0xC1});
////        }
//        //010300010002   CB95
//        //01    04    0000      0002    CB71
//        if(mSerialPortManagerMultiUrea!=null){
//            mSerialPortManagerMultiUrea.sendCommand(new byte[]{0x01, 0x04, 0x00, 0x00, 0x00, 0x08, (byte) 0xF1, (byte) 0xCC});
//        }
//
//        //心跳，本机状态
//        try {
//            String mac = "";
//            if (App.getApp().getDtuSetting() != null) {
//                mac = App.getApp().getDtuSetting().mMac;
//            }
//            long ts = System.currentTimeMillis();
//            DtuOnlineDataBean dtuOnlineDataBean = new DtuOnlineDataBean();
//            dtuOnlineDataBean.mac = mac;
//            dtuOnlineDataBean.ts = ts;
//            dtuOnlineDataBean.online = "1";
//            String dtuOnlineStr = dtuOnlineDataBean.toJson();
//            mqttPushDataService.pushData("dtu_online_data", dtuOnlineStr);
//        } catch (Exception e) {
//        }
//    }
//
//    private void getSanzeData() {
//        mHandler.removeCallbacks(getDataRunnable);
//        mHandler.postDelayed(getDataRunnable, 5000);
//    }
//
//    private void initDevice() {
//        SerialPortFinder serialPortFinder = new SerialPortFinder();
//        mDevices = serialPortFinder.getAllDevicesPath();
//        if (mDevices.length == 0) {
//            mDevices = new String[]{"找不到串口设备"};
//        }
//        mDeviceList = transferArrayToList(mDevices);
//        mDeviceList.add(0, "未选择");
//        //FileKit.save(ActFrame2.this, mDeviceList, Constants.FILE_KEY_485_DEVICE);
//        openDevice();
//    }
//
//    private void openDevice() {
//        if (mSensorSettingList != null && mSensorSettingList.size()!=0) { //已配置工作站
//            for(int i=0;i<mSensorSettingList.size();i++){
//                SensorSettingBean bean = mSensorSettingList.get(i);
//                if(bean.mEstimateType!=null) {
//                    if (DeviceMonitorEnum.MONITOR_WATER_DRINK.getCode().equals(bean.mEstimateType.code)) { //饮用水
//                        if (bean.mSubTypeBeans != null && bean.mSubTypeBeans.size() != 0 && ( DeviceMonitorEnum.MONITOR_WATER_DRINK_TOC.getCode().equals(bean.mSubTypeBeans.get(0).code)
//                                || DeviceMonitorEnum.MONITOR_WATER_DRINK_CL.getCode().equals(bean.mSubTypeBeans.get(0).code)
//                                || DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB.getCode().equals(bean.mSubTypeBeans.get(0).code) )
//                                ) { //4-20ma
//                            Device mDevice = new Device(bean.mSerialPort, "115200");
//                            mSerialPortManagerMultiUrea = new SerialPortManagerMulti();
//                            openOrSwitchPort(mDevice, 1);
//                            port2 = bean.mSerialPort;
//                        } else { //三参
//                            Device mDevice = new Device(bean.mSerialPort, "9600");
//                            mSerialPortManagerMulti = new SerialPortManagerMulti();
//                            openOrSwitchPort(mDevice, 0);
//                            port1 = bean.mSerialPort;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private static List<String> transferArrayToList(String[] array){
//        List<String> transferedList = new ArrayList<>();
//        for (int i = 0; i < array.length; i++) {
//            transferedList.add(array[i]);
//        }
//        return transferedList;
//    }
//
//    public void openOrSwitchPort(Device mDevice, int type) {
//        boolean mOpened = false;
//        if(type == 0){
//            if(mSerialPortManagerMulti == null){
//                mSerialPortManagerMulti = new SerialPortManagerMulti();
//            }
//            mSerialPortManagerMulti.setOnGetDataListener(mOnGetDataListener);
//            mOpened = mSerialPortManagerMulti.open(mDevice) != null;
//        } else if(type == 1){
//            if(mSerialPortManagerMultiUrea == null){
//                mSerialPortManagerMultiUrea = new SerialPortManagerMulti();
//            }
//            mSerialPortManagerMultiUrea.setOnGetDataListener(mOnGetDataListenerUrea);
//            mOpened = mSerialPortManagerMultiUrea.open(mDevice) != null;
//        }
//
//        if (mOpened) {
//            Toast.makeText(this, "成功打开串口", Toast.LENGTH_LONG).show();
//            getSanzeData();
//        } else {
//            Toast.makeText(this, "打开串口失败", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public DatabaseHelper getDatabaseHelper() {
//        return getHelper();
//    }
//
//    private TriggerBean handleData(DeviceMonitorEnum deviceMonitorEnum, float value) {
//        if (dicMap != null && dicMap.size() != 0) {
//            String code = deviceMonitorEnum.getCode();
//            List<MonitorDicParameter.WarnLinesBean> list = dicMap.get(code);
//            TriggerBean bean = TriggerBean.trigger(value, list);
//            if (bean.exception) { //有问题的数据才存
//                LogDataBean logDataBean = new LogDataBean();
//                logDataBean.level = bean.level;
//                logDataBean.reason = bean.reason;
//                String sensorId = "";
//                if (sensorMap != null && sensorMap.size() != 0) {
//                    sensorId = sensorMap.get(deviceMonitorEnum.getCode());
//                }
//                logDataBean.sensorId = sensorId;
//                logDataBean.date = System.currentTimeMillis();
//                try {
//                    getHelper().getLogDataDao().createOrUpdate(logDataBean);
//                } catch (Exception e) {
//                }
//            }
//            return bean;
//        } else {
//            TriggerBean bean2 = new TriggerBean();
//            bean2.value = MathUtil.scaleRoundHalfUp(value, 2);
//            return bean2;
//        }
//    }
//
//    public void getMonitorDicParameters() {
//        String mac = "78:A3:51:2B:77:14";
//        if (App.getApp().getDtuSetting() != null) {
//            mac = App.getApp().getDtuSetting().mMac;
//        }
//        Api.getMonitorDicParameters(mac, new OnStringRespListener() {
//            @Override
//            public void onResponse(String data) {
//                Resp resp = Resp.objectFromData(data);
//                if (resp.canParse()) {
//                    List<MonitorDicParameter> list = MonitorDicParameter.arrayMonitorWarnLineParameterFromData(resp.content);
//                    if (list != null && list.size() != 0) {
//                        HashMap<String, List<MonitorDicParameter.WarnLinesBean>> map = new HashMap();
//                        for (int i = 0; i < list.size(); i++) {
//                            MonitorDicParameter bean = list.get(i);
//                            map.put(bean.code, bean.warnLines);
//                        }
//                        dicMap = map;
//                        FileKit.save(ActFrame2.this, map, Constants.FILE_KEY_MONITOR_DIC);
//                    }
//                } else {
//                    onError(resp.message);
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//            }
//
//            @Override
//            public void onFinish(boolean withoutException) {
//            }
//        });
//    }
//
//    //友盟
//    @Override
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onResume(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        MobclickAgent.onPause(this);
//    }
//}
