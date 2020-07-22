package com.minorfish.dtuapp.module.dicts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minorfish.dtuapp.abs.App;
import com.tangjd.common.utils.FileUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DtuVendorBean {
    public static List<DtuVendorBean> data;

    static {
        data = DtuVendorBean.arrayFromData(FileUtil.readFromAssets(App.getApp(), "DtuVendorData"));
        if (data == null) {
            data = new ArrayList<>();
        }
        DtuVendorBean firstItem = new DtuVendorBean();
        firstItem.name = "未选择";
        data.add(0, firstItem);
    }

    /**
     * value : 西门子（数采仪）
     * name : 西门子（数采仪）
     */

    public String value;
    public String name;

    public static DtuVendorBean objectFromData(String str) {
        return new Gson().fromJson(str, DtuVendorBean.class);
    }

    public static List<DtuVendorBean> arrayFromData(String str) {
        Type listType = new TypeToken<ArrayList<DtuVendorBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    @Override
    public String toString() {
        return name;
    }
}
