package com.minorfish.dtuapp.module.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

public class MultiDataBean {
	
	//"数采仪MAC地址，大写"
    public String mac;
	//"数据产生时间戳UNIX，单位：毫秒(ms)"
    public long ts;
	//"数采仪（DTU）串口号"
    public String port;
	//"传感器数据"
    public List<SensorDataBean> sensorDatas = new ArrayList<>();
	
	public void addSensorData(SensorDataBean sensorData) {
        this.sensorDatas.add(sensorData);
    }
	
    public static MultiDataBean fromJson(String json) {
        return new Gson().fromJson(json, MultiDataBean.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static class SensorDataBean {
        public String code;
        public String value;
    }
    
}
