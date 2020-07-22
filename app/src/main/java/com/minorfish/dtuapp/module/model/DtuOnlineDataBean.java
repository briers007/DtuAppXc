package com.minorfish.dtuapp.module.model;


import com.google.gson.Gson;

public class DtuOnlineDataBean {

    public String mac;
    public long ts;
    public String online;

    public static DtuOnlineDataBean fromJson(String json) {
        return new Gson().fromJson(json, DtuOnlineDataBean.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
