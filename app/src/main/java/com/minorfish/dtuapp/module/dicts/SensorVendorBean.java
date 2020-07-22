package com.minorfish.dtuapp.module.dicts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minorfish.dtuapp.abs.App;
import com.tangjd.common.utils.FileUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SensorVendorBean {
    public static List<SensorVendorBean> data = new ArrayList<>();

//    static {
//        data = SensorVendorBean.arrayFromData(FileUtil.readFromAssets(App.getApp(), "SensorVendorData"));
//        if (data == null) {
//            data = new ArrayList<>();
//        }
//        SensorVendorBean firstItem = new SensorVendorBean();
//        firstItem.name = "未选择";
//        data.add(0, firstItem);
//    }

    public static void formData(String content){
        data = SensorVendorBean.arrayFromData(content);
        if (data == null) {
            data = new ArrayList<>();
        }
        SensorVendorBean firstItem = new SensorVendorBean();
        firstItem.name = "未选择";
        data.add(0, firstItem);
    }

    public String value;
    public String name;

    public static SensorVendorBean objectFromData(String str) {
        return new Gson().fromJson(str, SensorVendorBean.class);
    }

    public static List<SensorVendorBean> arrayFromData(String str) {
        Type listType = new TypeToken<ArrayList<SensorVendorBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    @Override
    public String toString() {
        return name;
    }
}
