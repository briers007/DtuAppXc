package com.minorfish.dtuapp.abs;

import android.text.TextUtils;

import com.minorfish.dtuapp.module.model.DtuSettingBean;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.tangjd.common.retrofit.string.ApiStringBase;
import com.tangjd.common.retrofit.string.OnStringRespListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class Api {
    public static final String TAG = "Api";
    //private static final String DOMAIN_NAME = "http://192.168.2.227:8081";
    //private static final String DOMAIN_NAME = "http://192.168.2.227:20001";
    private static final String DOMAIN_NAME = "http://js-iot.minorfish.com";
    private static ApiService sApiService;

    private static String getToken() {
        if (App.getApp().getSignInBean() == null || TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
            return "";
        }
        return App.getApp().getSignInBean().token;
    }

    private static String getParams(Entry... entries) {
        JSONObject params = new JSONObject();
        for (int i = 0; i < entries.length; i++) {
            Entry entry = entries[i];
            try {
                params.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return params.toString();
    }

    static {
        sApiService = ApiStringBase.getStringRetrofit(DOMAIN_NAME).create(ApiService.class);
    }

    public static void signIn(String account, String pwd,  OnStringRespListener listener) {
        ApiStringBase.enqueue(sApiService.login(getParams(
                new Entry("username", account),
                new Entry("password", pwd)
        )), listener);
    }

    public static void modifyPwd(String newPassword, String password, OnStringRespListener listener) {
        ApiStringBase.enqueue(sApiService.modifyPwd(getToken(), getParams(
                new Entry("newPassword", newPassword),
                new Entry("password", password)
        )), listener);
    }

    public static void getMonitorDicParameters(String mac,OnStringRespListener listener) {
        ApiStringBase.enqueue(sApiService.getMonitorDicParameters(getToken(), getParams(
                new Entry("mac", mac)
        )), listener);
    }

    public static void configDtu(DtuSettingBean bean, OnStringRespListener listener) {
        ApiStringBase.enqueue(sApiService.configDtu(getToken(), getParams(
                new Entry("point", bean.mPositionName),
                new Entry("address", bean.mAddressDetail),
                new Entry("city", bean.mAddressBean.cityCode),
                new Entry("district", bean.mAreaBean.areaCode),
                new Entry("instId", bean.mOrgBean.orgCode),
                new Entry("ip", bean.mIpAddress),
                new Entry("mac", bean.mMac),
                new Entry("manufactor", bean.mDtuVendorBean.value),
                new Entry("modelId", bean.mDtuModelBean.value),
                new Entry("name", bean.mDtuName),
                new Entry("province", 320000),
                new Entry("type", 2)
        )), listener);
    }

    public static void configSensor(SensorSettingBean bean, OnStringRespListener listener) {
        JSONArray subTypes = new JSONArray();
        for (int i = 0; i < bean.mSubTypeBeans.size(); i++) {
            subTypes.put(bean.mSubTypeBeans.get(i).code);
        }
        ApiStringBase.enqueue(sApiService.configSensor(getToken(), getParams(
                new Entry("address", bean.mAddress),
                new Entry("dtuMac", App.getApp().getDtuSetting().mMac),
                new Entry("manufactor", bean.mVendorBean.value),
                new Entry("modelId", bean.mModelBean.value),
                new Entry("monitorSubTypes", subTypes),
                new Entry("monitorType", bean.mEstimateType.code),
                new Entry("name", bean.mSensorName),
                new Entry("port", bean.mSerialPort),
                new Entry("sendCycle", bean.mSendFreq)
        )), listener);
    }

    public static void getAreaTree(OnStringRespListener listener) {
        ApiStringBase.enqueue(sApiService.getAreaTree(getToken()), listener);
    }

    public static void getSensorVendor(OnStringRespListener listener) {
        ApiStringBase.enqueue(sApiService.getSensorVendor(getToken()), listener);
    }

    public static void getSensorModel(OnStringRespListener listener) {
        ApiStringBase.enqueue(sApiService.getSensorModel(getToken()), listener);
    }

    public static void getMonitorType(OnStringRespListener listener) {
        ApiStringBase.enqueue(sApiService.getMonitorType(getToken()), listener);
    }

    public interface ApiService {
        @POST("/auth/login.do")
        Call<String> login(@Body String params);

        @POST("/ac/dtu/create.do")
        Call<String> configDtu(@Header("X-Auth-Token") String token, @Body String params);

        @POST("/ac/workStation/create.do")
        Call<String> configSensor(@Header("X-Auth-Token") String token, @Body String params);

        @POST("/ac/inst/monitorSubTypeValue/all.do")
        Call<String> getMonitorDicParameters(@Header("X-Auth-Token") String token, @Body String params);

        @POST("/ac/drop/areaTree.do")
        Call<String> getAreaTree(@Header("X-Auth-Token") String token);

        @POST("/ac/drop/sensor/idAndModels.do")
        Call<String> getSensorModel(@Header("X-Auth-Token") String token);

        @POST("ac/drop/sensor/manufacturers.do")
        Call<String> getSensorVendor(@Header("X-Auth-Token") String token);

        @POST("/ac/drop/sensor/monitorType.do")
        Call<String> getMonitorType(@Header("X-Auth-Token") String token);

        @POST("/ac/updatePassword.do")
        Call<String> modifyPwd(@Header("X-Auth-Token") String token, @Body String params);
    }
}
