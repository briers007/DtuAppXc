package com.minorfish.dtuapp.module.dicts;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.minorfish.dtuapp.abs.App;
import com.tangjd.common.utils.FileUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddressBean {

    public static List<AddressBean> data = new ArrayList<>();

//    static {
//        data = AddressBean.arrayFromData(FileUtil.readFromAssets(App.getApp(), "AddressData"));
//        if (data == null) {
//            data = new ArrayList<>();
//        }
//        AddressBean cityFirst = new AddressBean();
//        cityFirst.cityName = "未选择";
//        data.add(0, cityFirst);
//
//        for (int i = 0; i < data.size(); i++) {
//            if (data.get(i).areaBeans == null) {
//                data.get(i).areaBeans = new ArrayList<>();
//            }
//            AreaBean areaFirst = new AreaBean();
//            areaFirst.areaName = "未选择";
//            data.get(i).areaBeans.add(0, areaFirst);
//
//            for (int j = 0; j < data.get(i).areaBeans.size(); j++) {
//                if (data.get(i).areaBeans.get(j).orgBeans == null) {
//                    data.get(i).areaBeans.get(j).orgBeans = new ArrayList<>();
//                }
//                AreaBean.OrgBean orgFirst = new AreaBean.OrgBean();
//                orgFirst.orgName = "未选择";
//                data.get(i).areaBeans.get(j).orgBeans.add(0, orgFirst);
//            }
//        }
//    }

    public static void formData(String content){
        data = AddressBean.arrayFromData(content);
        if (data == null) {
            data = new ArrayList<>();
        }
        AddressBean cityFirst = new AddressBean();
        cityFirst.cityName = "未选择";
        data.add(0, cityFirst);

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).areaBeans == null) {
                data.get(i).areaBeans = new ArrayList<>();
            }
            AreaBean areaFirst = new AreaBean();
            areaFirst.areaName = "未选择";
            data.get(i).areaBeans.add(0, areaFirst);

            for (int j = 0; j < data.get(i).areaBeans.size(); j++) {
                if (data.get(i).areaBeans.get(j).orgBeans == null) {
                    data.get(i).areaBeans.get(j).orgBeans = new ArrayList<>();
                }
                AreaBean.OrgBean orgFirst = new AreaBean.OrgBean();
                orgFirst.orgName = "未选择";
                data.get(i).areaBeans.get(j).orgBeans.add(0, orgFirst);
            }
        }
    }

    /**
     * code : 320100
     * value : 南京市
     * children : [{"code":"320101","value":"市辖区","children":[{"code":"1","value":"南京市市辖区医院01","children":[]},{"code":"5","value":"南京市市辖区医院02","children":[]}]},{"code":"320102","value":"玄武区","children":[{"code":"2","value":"南京市玄武区医院01","children":[]}]},{"code":"320103","value":"白下区","children":[]},{"code":"320104","value":"秦淮区","children":[]},{"code":"320105","value":"建邺区","children":[]},{"code":"320106","value":"鼓楼区","children":[]},{"code":"320107","value":"下关区","children":[]},{"code":"320111","value":"浦口区","children":[]},{"code":"320113","value":"栖霞区","children":[]},{"code":"320114","value":"雨花台区","children":[]},{"code":"320115","value":"江宁区","children":[]},{"code":"320116","value":"六合区","children":[]},{"code":"320124","value":"溧水县","children":[]},{"code":"320125","value":"高淳县","children":[]}]
     */

    @SerializedName("code")
    public String cityCode;
    @SerializedName("value")
    public String cityName;
    @SerializedName("children")
    public List<AreaBean> areaBeans;

//    public static AddressBean objectFromData(String str) {
//        return new Gson().fromJson(str, AddressBean.class);
//    }

    public static List<AddressBean> arrayFromData(String str) {
        Type listType = new TypeToken<ArrayList<AddressBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    public static class AreaBean {
        /**
         * code : 320101
         * value : 市辖区
         * children : [{"code":"1","value":"南京市市辖区医院01","children":[]},{"code":"5","value":"南京市市辖区医院02","children":[]}]
         */

        @SerializedName("code")
        public String areaCode;
        @SerializedName("value")
        public String areaName;
        @SerializedName("children")
        public List<OrgBean> orgBeans;

//        public static AreaBean objectFromData(String str) {
//            return new Gson().fromJson(str, AreaBean.class);
//        }

        public static List<AreaBean> arrayFromData(String str) {
            Type listType = new TypeToken<ArrayList<AreaBean>>() {
            }.getType();
            return new Gson().fromJson(str, listType);
        }

        public static class OrgBean {
            /**
             * code : 1
             * value : 南京市市辖区医院01
             * children : []
             */

            @SerializedName("code")
            public String orgCode;
            @SerializedName("value")
            public String orgName;

//            public static OrgBean objectFromData(String str) {
//                return new Gson().fromJson(str, OrgBean.class);
//            }

            public static List<OrgBean> arrayFromData(String str) {
                Type listType = new TypeToken<ArrayList<OrgBean>>() {
                }.getType();
                return new Gson().fromJson(str, listType);
            }

            @Override
            public String toString() {
                return orgName;
            }
        }

        @Override
        public String toString() {
            return areaName;
        }
    }

    @Override
    public String toString() {
        return cityName;
    }
}
