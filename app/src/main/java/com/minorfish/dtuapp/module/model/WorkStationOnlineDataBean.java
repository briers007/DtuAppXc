package com.minorfish.dtuapp.module.model;


import com.google.gson.Gson;

public class WorkStationOnlineDataBean {

    public String mac;
    public String port;
    //"数据产生时间戳UNIX，单位：毫秒(ms)"
    public long ts;
    //设备状态：1在线，0离线"
    public String online;

    public static WorkStationOnlineDataBean fromJson(String json) {
        return new Gson().fromJson(json, WorkStationOnlineDataBean.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
