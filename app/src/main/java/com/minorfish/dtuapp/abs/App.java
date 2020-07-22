package com.minorfish.dtuapp.abs;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minorfish.dtuapp.module.ActSignIn;
import com.minorfish.dtuapp.module.model.DtuSettingBean;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.minorfish.dtuapp.module.model.SignInBean;
import com.tangjd.common.manager.SPManager;
import com.tangjd.common.utils.StringKit;
import com.umeng.commonsdk.UMConfigure;

import java.util.ArrayList;
import java.util.List;


/**
 * SPManager SharedPreferences存储工具类
 */

public class App extends Application {
    private static App sApp;

    public static App getApp() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        //sp 初始化
        SPManager.getInstance().init(getApp());
        //崩溃捕获
        MyUnCaughtExceptionHandler crashHandler = new MyUnCaughtExceptionHandler();
        crashHandler.init(getApplicationContext());
        //友盟统计功能
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5b73f7a08f4a9d5a0f00012c");
    }



    /**
     * 存储和获取SignInBean 对象数据，此处相当于sp临时存储
     */
    private SignInBean mSignInBean;

    public void setSignInBean(SignInBean bean) {
        mSignInBean = bean;
        SPManager.getInstance().putString(Constants.PREF_KEY_SIGN_IN_BEAN, new Gson().toJson(bean));
    }

    public SignInBean getSignInBean() {
        if (mSignInBean == null) {
            synchronized (App.class) {
                mSignInBean = SignInBean.objectFromData(SPManager.getInstance().getString(Constants.PREF_KEY_SIGN_IN_BEAN, null));
            }
        }
        return mSignInBean;
    }


    /**
     * 存储和获取dtuSettingBean 设置中的实例对象
     */

    public DtuSettingBean mDtuSettingBean;

    public DtuSettingBean getDtuSetting() {
        if (mDtuSettingBean == null) {
            mDtuSettingBean = new Gson().fromJson(SPManager.getInstance().getString(Constants.PREF_KEY_DTU_SETTING, null), DtuSettingBean.class);
        }
        return mDtuSettingBean;
    }

    public void setDtuSetting(DtuSettingBean bean) {
        mDtuSettingBean = bean;
        SPManager.getInstance().putString(Constants.PREF_KEY_DTU_SETTING, new Gson().toJson(bean));
    }


    /**
     * 存储和获取SensorSettingBean 设置中的实例对象
     */
    private SensorSettingBean mSensorSettingBean;
    private List<SensorSettingBean> mSensorSettingList = new ArrayList<>();

    public SensorSettingBean getSensorSetting() {
        if (mSensorSettingBean == null) {
            mSensorSettingBean = new Gson().fromJson(SPManager.getInstance().getString(Constants.PREF_KEY_SENSOR_SETTING, null), SensorSettingBean.class);
        }
        return mSensorSettingBean;
    }

    public void setSensorSetting(SensorSettingBean bean) {
        mSensorSettingBean = bean;
        SPManager.getInstance().putString(Constants.PREF_KEY_SENSOR_SETTING, new Gson().toJson(bean));
    }

    /**
     * 添加SensorSettingList对象数组的存储和获取数据
     */

    public void addSensorSettingList(SensorSettingBean bean) {
        mSensorSettingList = getSensorSettingList();
        mSensorSettingList.add(bean);
        SPManager.getInstance().putString(Constants.PREF_KEY_SENSOR_SETTING_LIST, new Gson().toJson(mSensorSettingList));
    }

    public List<SensorSettingBean> getSensorSettingList() {
        if (mSensorSettingList.size() == 0) {
            String str = SPManager.getInstance().getString(Constants.PREF_KEY_SENSOR_SETTING_LIST, null);
            if(StringKit.isNotEmpty(str)) {
                mSensorSettingList = new Gson().fromJson(str, new TypeToken<List<SensorSettingBean>>(){}.getType());
            }
        }
        return mSensorSettingList;
    }


    /**
     * 崩溃捕获处理
     */

    class MyUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

        private Context context;

        /**
         * 异常处理初始化
         *
         * @param context
         */
        public void init(Context context) {
            this.context = context;
            // 设置该CrashHandler为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();

            Intent intent = new Intent(App.this, ActSignIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.this.startActivity(intent);

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
}
