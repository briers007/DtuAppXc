package com.minorfish.dtuapp.module;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.serialport.SerialPortFinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.minorfish.dtuapp.module.fragment.FmtHistoryYuanganFive;
import com.minorfish.dtuapp.module.fragment.FmtLog;
import com.minorfish.dtuapp.module.fragment.FmtModifyPwd;
import com.minorfish.dtuapp.module.fragment.FmtRealDataMulti;
import com.minorfish.dtuapp.module.fragment.FmtSensorSettingNew;
import com.minorfish.dtuapp.module.model.DtuOnlineDataBean;
import com.minorfish.dtuapp.module.model.LogDataBean;
import com.minorfish.dtuapp.module.model.MultiDataBean;
import com.minorfish.dtuapp.module.model.RealDataHistoryBean;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.minorfish.dtuapp.module.model.TriggerBean;
import com.minorfish.dtuapp.module.model.UreaDataHistoryBean;
import com.minorfish.dtuapp.module.model.WorkStationOnlineDataBean;
import com.minorfish.dtuapp.mqtt.MqttPushDataService;
import com.minorfish.dtuapp.rs485.Device;
import com.minorfish.dtuapp.rs485.OnGetDataListener;
import com.minorfish.dtuapp.rs485.SerialPortManagerMulti;
import com.minorfish.dtuapp.util.MathUtil;
import com.minorfish.dtuapp.util.Utils;
import com.tangjd.common.abs.BaseActivity;
import com.tangjd.common.abs.BaseFragment;
import com.tangjd.common.retrofit.string.OnStringRespListener;
import com.tangjd.common.utils.ByteUtil;
import com.tangjd.common.utils.FileKit;
import com.tangjd.common.utils.StringKit;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: 485串口版（多个监测站支持）重庆远感机器，多个电导率,修改为(参照"泳池水五参"修改为饮用水五参) 添加参数耗氧量
 * Date: 2018/7/11
 */
public class ActFrameYgFiveDrink extends BaseActivity implements OnDatabaseListener {

    private static final String TAG = ActFrameYgFiveDrink.class.getSimpleName();
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
        //SPManager.getInstance().putString(Constants.PREF_KEY_SENSOR_SETTING_LIST, "");

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
            dicMap = (HashMap<String, List<MonitorDicParameter.WarnLinesBean>>) FileKit.getObject(ActFrameYgFiveDrink.this, Constants.FILE_KEY_MONITOR_DIC);
            //if (dicMap == null || dicMap.size() == 0) {
            getMonitorDicParameters();
            //}

        } else { //没配置dtu先引导去配置
            FmtDTUSetting fmt = new FmtDTUSetting();
            mFragments.put(R.id.rb_dtu_setting, fmt);
            ft.replace(R.id.fragment_container, fmt, FmtDTUSetting.class.getName());
            ft.commit();
            radioGroup.check(R.id.rb_dtu_setting);
            getMonitorDicParameters();
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
                            fmt = new FmtHistoryYuanganFive();
                            ft.add(R.id.fragment_container, fmt, FmtHistoryYuanganFive.class.getName());
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
    }

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
                sensorMap = (HashMap<String, String>) FileKit.getObject(ActFrameYgFiveDrink.this, Constants.FILE_KEY_SENSOR_DIC);//更新传感器列表
            }catch (Exception e){}
            try {
                //添加成功后打开串口
                //openOrSwitchPort();
                mSensorSettingList = App.getApp().getSensorSettingList();
                ((FmtSensorSettingNew) mFragments.get(R.id.rb_sensor_setting)).setData(true);
                if (mSensorSettingList != null && mSensorSettingList.size()!=0) { //已配置工作站
                    SensorSettingBean bean = mSensorSettingList.get(index);
                    String ss=new Gson().toJson(bean);
                    com.tangjd.common.utils.Log.e(TAG,"backToFmtSensorSetting: "+ss);
                    if(bean.mEstimateType!=null) {
                        if (DeviceMonitorEnum.MONITOR_WATER_DRINK.getCode().equals(bean.mEstimateType.code)) { //应用水
                            if (bean.mSubTypeBeans != null && bean.mSubTypeBeans.size() == 1) { //尿素
                                Device mDevice = new Device(bean.mSerialPort, "9600");
                                mSerialPortManagerMultiCod = new SerialPortManagerMulti();
                                openOrSwitchPort(mDevice, 1);
                                port2 = bean.mSerialPort;
                                tabName2 = bean.mSensorName;
                                ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).setRadio(bean.mSensorName, 1);
                            } else { //五参
                                Device mDevice = new Device(bean.mSerialPort, "9600");
                                mSerialPortManagerMulti = new SerialPortManagerMulti();
                                openOrSwitchPort(mDevice, 0);
                                port1 = bean.mSerialPort;
                                tabName1 = bean.mSensorName;
                                ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).setRadio(bean.mSensorName, 0);

                            }
                        }
                    }
                }
                //((FmtHistoryYuanganFive) mFragments.get(R.id.rb_history)).setSensor();
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
            mIOThread.quit();
            unregisterReceiver(mMqttReconnectReceiver);
            unbindService(mqttConnection);
            //SerialPortManagerMulti.instance().close();
            if(mSerialPortManagerMulti!=null){
                mSerialPortManagerMulti.close();
                mSerialPortManagerMulti = null;
            }
            if(mSerialPortManagerMultiCod!=null){
                mSerialPortManagerMultiCod.close();
                mSerialPortManagerMultiCod = null;
            }
            //MqttPushDataService.actionStop(this);

        }catch (Exception e){}
    }

    public static IntentFilter getReceiverFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_MQTT_FAIL);
        return filter;
    }

    private BroadcastReceiver mMqttReconnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_MQTT_FAIL)) {
//                showAlertDialog("多次尝试连接服务器未成功，请联系服务方修复之后重新连接。联系方式：0512-62867280。",
//                        "重试", "取消", false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                mqttPushDataService.scheduleReconnect();
//                            }
//                        }, null);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mqttPushDataService.scheduleReconnectFromAct();
                    }
                }, 2*60*60*1000);//两小时后再尝试连接mqtt服务器
            }
        }
    };

    //原來是 3 + 32 +2 位，现在加了18位数据，要拿水位状态，所以是55位
    private static final int PACKET_LENGTH = 25;
    private static final int PACKET_LENGTH_COD = 9;
    private volatile ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
    private volatile ByteArrayOutputStream mBaosCod = new ByteArrayOutputStream();

    private SerialPortManagerMulti mSerialPortManagerMulti = null; //四参数
    private SerialPortManagerMulti mSerialPortManagerMultiCod = null; // cod

    interface OnGetSinglePacketListener {
        void onGetSinglePacket(byte[] singlePacket);
    }

    private HashMap<DeviceMonitorEnum, TriggerBean> data;
    private HashMap<DeviceMonitorEnum, TriggerBean> dataCod;

    private OnGetSinglePacketListener mListener = new OnGetSinglePacketListener() {
        @Override
        public synchronized void onGetSinglePacket(final byte[] singlePacket) {
            Log.e(TAG,"打印数组："+singlePacket);
            float zhuoduInt = Utils.byteArrayToInt(singlePacket,3);
            float yulvInt = Utils.byteArrayToInt(singlePacket,7);
            float temperatureInt = Utils.byteArrayToInt(singlePacket,11);
            float phInt = Utils.byteArrayToInt(singlePacket,15);
            float condInt = Utils.byteArrayToInt(singlePacket,19);


            //五参包括cond
            final float zhuodu = zhuoduInt/1000f;
            final float yulv =  yulvInt/1000f;
            final float temperature =  temperatureInt/1000f;
            final float ph =  phInt/1000f;
            final float cond =  condInt/1000f;

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

                    //五参
                    TriggerBean phBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_PH, ph);
                    TriggerBean yulvBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_CL, yulv);
                    TriggerBean temperatureBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_TEMPERATURE, temperature);
                    TriggerBean zhuoduBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB, zhuodu);
                    TriggerBean condBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_COND, cond);

                    data = new HashMap<DeviceMonitorEnum, TriggerBean>();
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_PH, phBean);
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_CL, yulvBean);
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_TEMPERATURE, temperatureBean);
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_TURB, zhuoduBean);
                    data.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_COND, condBean);
                    try {
                        ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).showData(data,0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // upload and save
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
                    bean.cond = condBean.value;
                    bean.condDeta = condBean.deta;
                    bean.condLevel = condBean.level;
                    bean.date = ts;

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
                        MultiDataBean.SensorDataBean sensorData5 = new MultiDataBean.SensorDataBean();
                        sensorData5.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_COND.getCode();
                        sensorData5.value = cond + "";

                        dataBean.addSensorData(sensorData);
                        dataBean.addSensorData(sensorData2);
                        dataBean.addSensorData(sensorData3);
                        dataBean.addSensorData(sensorData4);
                        dataBean.addSensorData(sensorData5);

                        dataBean.mac = mac;
                        dataBean.port = serialPort;
                        dataBean.ts = ts;
                        String message = dataBean.toJson();
                        com.tangjd.common.utils.Log.e(TAG,"OnGetSinglePacketListener: "+message);
                        if (mqttPushDataService != null) {
                            mqttPushDataService.pushData("monitor_multi_data", message);
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
    };


    private OnGetSinglePacketListener mListenerCod = new OnGetSinglePacketListener() {
        @Override
        public synchronized void onGetSinglePacket(final byte[] singlePacket) {
            final float cod = byte2floatCDAB(singlePacket, 3);
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

                    TriggerBean ureaBean = handleData(DeviceMonitorEnum.MONITOR_WATER_DRINK_COD, cod);

                    dataCod = new HashMap<DeviceMonitorEnum, TriggerBean>();
                    dataCod.put(DeviceMonitorEnum.MONITOR_WATER_DRINK_COD, ureaBean);
                    try {
                        ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).showData(dataCod,1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // upload and save
                    UreaDataHistoryBean bean = new UreaDataHistoryBean();
                    bean.urea = ureaBean.value;
                    bean.ureaDeta = ureaBean.deta;
                    bean.ureaLevel = ureaBean.level;
                    bean.date = ts;
                    try {
                        getHelper().getUreaDataHistoryDao().createOrUpdate(bean);
                    } catch (Exception e) {
                    }
                    try {
                        MultiDataBean dataBean = new MultiDataBean();
                        MultiDataBean.SensorDataBean sensorData = new MultiDataBean.SensorDataBean();
                        sensorData.code = DeviceMonitorEnum.MONITOR_WATER_DRINK_COD.getCode();
                        sensorData.value = cod + "";
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

    private OnGetDataListener mOnGetDataListener = new OnGetDataListener() {
        @Override
        public void OnGetData485(byte[] buffer) {
            parsePacket(buffer, mListener);
        }

        @Override
        public void getDataString(String[] strData, byte[] singlePacket) {

        }

    };

    private OnGetDataListener mOnGetDataListenerCod = new OnGetDataListener() {
        @Override
        public void OnGetData485(byte[] buffer) {
            parsePacketCod(buffer, mListenerCod);
        }

        @Override
        public void getDataString(String[] strData, byte[] singlePacket) {

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
            if (buffer[i] == 0x06 && buffer[i + 1] == 0x03 && buffer[i + 2] == (byte)0x14) {
                correctPacketStartIndex = i;
            }
        }
        // 01 03 20  00 00 00 00 00 19 07 e2 00 07 00 1b 00 11 00 0b 00 1c
        // c2 8f 41 05 00 00 00 00 00 00 00
        // 01 03 20 AB AA 40 76 00 00      00 00 00 00 00 00 94 11 41 20 00 00 00 00 00 00 00 00 D7 0A 3C A3 10 82 40 AE 27 BE
        // 01 03 20 AB AA 40 76 00 00      00 00 00 00 00 00 94 11 41 20 00 00 00 00 00 00 00 00 D7 0A 3C A3 10 82 40 AE 27 BE
        if (buffer.length < PACKET_LENGTH + correctPacketStartIndex) {
            return;
        }
        listener.onGetSinglePacket(ByteUtil.subByteArr(buffer, correctPacketStartIndex, PACKET_LENGTH));
        mBaos.reset();
        mBaos.write(buffer, correctPacketStartIndex + PACKET_LENGTH, buffer.length - (correctPacketStartIndex + PACKET_LENGTH));
    }

    private synchronized void parsePacketCod(byte[] buffer, OnGetSinglePacketListener listener) {
        mBaosCod.write(buffer, 0, buffer.length);
        if (mBaosCod.size() == 0) {
            return;
        }
        buffer = mBaosCod.toByteArray();
        int correctPacketStartIndex = 0;
        for (int i = 0; i < buffer.length - 2; i++) {
            if (buffer[i] == 0x01 && buffer[i + 1] == 0x03 && buffer[i + 2] == 0x04) {
                correctPacketStartIndex = i;
            }
        }
        // 01 03 04 00 02 03 04 ** **
        if (buffer.length < PACKET_LENGTH_COD + correctPacketStartIndex) {
            return;
        }
        listener.onGetSinglePacket(ByteUtil.subByteArr(buffer, correctPacketStartIndex, PACKET_LENGTH_COD));
        mBaosCod.reset();
        mBaosCod.write(buffer, correctPacketStartIndex + PACKET_LENGTH_COD, buffer.length - (correctPacketStartIndex + PACKET_LENGTH_COD));
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
        if(mSerialPortManagerMulti!=null) {
            mSerialPortManagerMulti.sendCommand(new byte[]{0x06, 0x03, 0x00,  (byte)0x01, (byte)0x00, 0x0A, (byte) 0x95, (byte) 0xBA});
        }
        //010300010002   CB95
        if(mSerialPortManagerMultiCod!=null){
            mSerialPortManagerMultiCod.sendCommand(new byte[]{0x01, 0x03, 0x00, 0x01, 0x00, 0x02, (byte) 0x95, (byte) 0xCB});
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
        FileKit.save(ActFrameYgFiveDrink.this, mDeviceList, Constants.FILE_KEY_485_DEVICE);
        openDevice();
    }

    private void openDevice() {
        if (mSensorSettingList != null && mSensorSettingList.size()!=0) { //已配置工作站
            for(int i=0;i<mSensorSettingList.size();i++){
                SensorSettingBean bean = mSensorSettingList.get(i);
                if(bean.mEstimateType!=null) {
                    if (DeviceMonitorEnum.MONITOR_WATER_SWIM.getCode().equals(bean.mEstimateType.code)) { //水质
                        if (bean.mSubTypeBeans != null && bean.mSubTypeBeans.size() == 1) { //cod
                            Device mDevice = new Device(bean.mSerialPort, "9600");
                            mSerialPortManagerMultiCod = new SerialPortManagerMulti();
                            openOrSwitchPort(mDevice, 1);
                            port2 = bean.mSerialPort;
                            tabName2 = bean.mSensorName;
                            ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).setRadio(bean.mSensorName, 1);
                        } else { //四参
                            Device mDevice = new Device(bean.mSerialPort, "9600");
                            mSerialPortManagerMulti = new SerialPortManagerMulti();
                            openOrSwitchPort(mDevice, 0);
                            port1 = bean.mSerialPort;
                            tabName1 = bean.mSensorName;
                            ((FmtRealDataMulti) mFragments.get(R.id.rb_real_data)).setRadio(bean.mSensorName, 0);
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
            if(mSerialPortManagerMultiCod == null){
                mSerialPortManagerMultiCod = new SerialPortManagerMulti();
            }
            mSerialPortManagerMultiCod.setOnGetDataListener(mOnGetDataListenerCod);
            mOpened = mSerialPortManagerMultiCod.open(mDevice) != null;
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
                if (resp.canParse()) {
                    List<MonitorDicParameter> list = MonitorDicParameter.arrayMonitorWarnLineParameterFromData(resp.content);
                    if (list != null && list.size() != 0) {
                        HashMap<String, List<MonitorDicParameter.WarnLinesBean>> map = new HashMap();
                        for (int i = 0; i < list.size(); i++) {
                            MonitorDicParameter bean = list.get(i);
                            map.put(bean.code, bean.warnLines);
                        }
                        dicMap = map;
                        FileKit.save(ActFrameYgFiveDrink.this, map, Constants.FILE_KEY_MONITOR_DIC);
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
