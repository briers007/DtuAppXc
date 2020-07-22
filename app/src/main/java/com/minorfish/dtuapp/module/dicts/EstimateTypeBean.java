package com.minorfish.dtuapp.module.dicts;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.minorfish.dtuapp.abs.App;
import com.tangjd.common.utils.FileUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EstimateTypeBean {
    public static List<EstimateTypeBean> data = new ArrayList<>();

//    static {
//        data = EstimateTypeBean.arrayFromData(FileUtil.readFromAssets(App.getApp(), "EstimateTypeData"));
//
//        if (data == null) {
//            data = new ArrayList<>();
//        }
//        EstimateTypeBean firstItem = new EstimateTypeBean();
//        firstItem.name = "未选择";
//        data.add(0, firstItem);
//
////        for (int i = 0; i < data.size(); i++) {
////            if (data.get(i).subTypeBeans == null) {
////                data.get(i).subTypeBeans = new ArrayList<>();
////            }
////            EstimateSubTypeBean firstSubItem = new EstimateSubTypeBean();
////            firstSubItem.name = "未选择";
////            data.get(i).subTypeBeans.add(0, firstSubItem);
////        }
//    }

    public static void formData(String content){
        data = EstimateTypeBean.arrayFromData(content);
        if (data == null) {
            data = new ArrayList<>();
        }
        EstimateTypeBean firstItem = new EstimateTypeBean();
        firstItem.name = "未选择";
        data.add(0, firstItem);
    }

    /**
     * code : 100
     * value : 环境
     * children : [{"code":"101","value":"温度","children":[]},{"code":"102","value":"湿度","children":[]},{"code":"103","value":"PM2.5","children":[]},{"code":"104","value":"CO2","children":[]},{"code":"105","value":"CH2O","children":[]},{"code":"106","value":"VOC","children":[]},{"code":"107","value":"O2","children":[]},{"code":"108","value":"PM10","children":[]}]
     */

    public String code;
    @SerializedName("value")
    public String name;
    @SerializedName("children")
    public List<EstimateSubTypeBean> subTypeBeans;

    public static EstimateTypeBean objectFromData(String str) {
        return new Gson().fromJson(str, EstimateTypeBean.class);
    }

    public static List<EstimateTypeBean> arrayFromData(String str) {
        Type listType = new TypeToken<ArrayList<EstimateTypeBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    public static class EstimateSubTypeBean {
        /**
         * code : 101
         * value : 温度
         * children : []
         */

        public String code;
        @SerializedName("value")
        public String name;

        public static EstimateSubTypeBean objectFromData(String str) {
            return new Gson().fromJson(str, EstimateSubTypeBean.class);
        }

        public static List<EstimateSubTypeBean> arrayFromData(String str) {
            Type listType = new TypeToken<ArrayList<EstimateSubTypeBean>>() {
            }.getType();
            return new Gson().fromJson(str, listType);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
