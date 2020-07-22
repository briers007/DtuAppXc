package com.minorfish.dtuapp.module.dicts;

import java.util.Arrays;
import java.util.List;

/**
 * Author: Administrator
 * Date: 2018/7/11
 */
public enum DeviceMonitorEnum {

    /**
     * 环境监测
     */
    MONITOR_ENV("100", "环境监测",""),
    MONITOR_ENV_TEMPERATURE("101", "温度","℃"),//temperature
    MONITOR_ENV_HUMIDITY("102", "湿度","%rh"),//humidity
    MONITOR_ENV_PM25("103", "PM2.5","μg/m³"),//pm25
    MONITOR_ENV_CO2("104", "二氧化碳","ppm"),//co2
    MONITOR_ENV_CH2O("105", "甲醛","mg/m³"),//ch2o
    MONITOR_ENV_VOC("106", "挥发性有机化合物","g/l"),//voc
    MONITOR_ENV_O2("107", "氧气",""),//o2
    MONITOR_ENV_PM10("108", "PM10","μg/m³"),//pm10
    MONITOR_LIGHT_INTENSITY("109", "光照",""),//光照监测 beam
    MONITOR_NOISE_INTENSITY("110", "噪声",""),//噪声监测 noise
    MONITOR_WINDSPEED_INTENSITY("111", "风速",""),//风速监测 wind


    /**
     * 放射监测
     */
    MONITOR_RAY("200", "放射监测",""),
    MONITOR_RAY_ALPHA("201", "α射线",""),//alpha
    MONITOR_RAY_BETA("202", "β射线",""),//beta
    MONITOR_RAY_GAMMA("203", "γ射线",""),//gamma
    MONITOR_RAY_X("204", "x射线",""),//x-ray
    MONITOR_RAY_TOGETHER("299", "环境放射","μSv/h"),//together


    /**
     * 水质监测-泳池水
     */
    MONITOR_WATER_SWIM("600", "水质监测" ,""),
    MONITOR_WATER_SWIM_TEMPERATURE("601", "温度" , "℃"),//temperature
    MONITOR_WATER_SWIM_CL("602", "余氯","mg/l"),//cl
    MONITOR_WATER_SWIM_TURB("603", "浊度","NTU"),//turb
    MONITOR_WATER_SWIM_PH("604", "酸碱度",""),//ph
    MONITOR_WATER_SWIM_UREA("605", "尿素","mg/l"),//尿素
    MONITOR_WATER_SWIM_COND("606", "电导率","us/cm"),//电导率


    /**
     * 水质监测-饮用水
     */
    MONITOR_WATER_DRINK("700", "水质监测",""),
    MONITOR_WATER_DRINK_TEMPERATURE("701", "温度","℃"),//temperature
    MONITOR_WATER_DRINK_CL("702", "余氯","mg/l"),//cl
    MONITOR_WATER_DRINK_TURB("703", "浊度","NTU"),//turb
    MONITOR_WATER_DRINK_PH("704", "酸碱度",""),//ph

    MONITOR_WATER_DRINK_TOC("705", "总有机碳","mg/l"),//总有机碳
    MONITOR_WATER_DRINK_COND("706", "电导率","us/cm"),//电导率
    MONITOR_WATER_DRINK_COD("707", "耗氧量","mg/l"),//耗氧量

    ;

    private String code;
    private String value;
    private String unit;

    public static List<String> monitorTypes() {
        return Arrays.asList(MONITOR_ENV.getCode(), MONITOR_RAY.getCode(), MONITOR_WATER_SWIM.getCode(), MONITOR_WATER_DRINK.getCode());
    }

    public static String parse(String code) {
        for (DeviceMonitorEnum en : DeviceMonitorEnum.values()) {
            if (en.getCode().equals(code)) {
                return en.getValue();
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private DeviceMonitorEnum(String code, String value, String unit) {
        this.code = code;
        this.value = value;
        this.unit = unit;
    }

}
