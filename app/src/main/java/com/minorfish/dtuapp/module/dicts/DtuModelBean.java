package com.minorfish.dtuapp.module.dicts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minorfish.dtuapp.abs.App;
import com.tangjd.common.utils.FileUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DtuModelBean {
    public static List<DtuModelBean> data;

    static {
        data = DtuModelBean.arrayFromData(FileUtil.readFromAssets(App.getApp(), "DtuModelData"));
        if (data == null) {
            data = new ArrayList<>();
        }
        DtuModelBean firstItem = new DtuModelBean();
        firstItem.name = "未选择";
        data.add(0, firstItem);
    }

    public String value;
    public String name;

    public static DtuModelBean objectFromData(String str) {
        return new Gson().fromJson(str, DtuModelBean.class);
    }

    public static List<DtuModelBean> arrayFromData(String str) {
        Type listType = new TypeToken<ArrayList<DtuModelBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    @Override
    public String toString() {
        return name;
    }
}
