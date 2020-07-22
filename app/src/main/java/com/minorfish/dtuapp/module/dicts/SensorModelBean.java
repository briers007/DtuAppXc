package com.minorfish.dtuapp.module.dicts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minorfish.dtuapp.abs.App;
import com.tangjd.common.utils.FileUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SensorModelBean {
    public static List<SensorModelBean> data = new ArrayList<>();

//    static {
//        data = SensorModelBean.arrayFromData(FileUtil.readFromAssets(App.getApp(), "SensorModelData"));
//        if (data == null) {
//            data = new ArrayList<>();
//        }
//        SensorModelBean firstItem = new SensorModelBean();
//        firstItem.name = "未选择";
//        data.add(0, firstItem);
//    }

    public static void formData(String content){
        data = SensorModelBean.arrayFromData(content);
        if (data == null) {
            data = new ArrayList<>();
        }
        SensorModelBean firstItem = new SensorModelBean();
        firstItem.name = "未选择";
        data.add(0, firstItem);
    }

    public String value;
    public String name;

    public static SensorModelBean objectFromData(String str) {
        return new Gson().fromJson(str, SensorModelBean.class);
    }

    public static List<SensorModelBean> arrayFromData(String str) {
        Type listType = new TypeToken<ArrayList<SensorModelBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    @Override
    public String toString() {
        return name;
    }
}
