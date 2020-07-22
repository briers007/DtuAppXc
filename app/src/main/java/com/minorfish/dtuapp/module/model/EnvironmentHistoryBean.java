package com.minorfish.dtuapp.module.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Author: Chris 环境
 * Date: 2018/7/17
 */
@DatabaseTable(tableName = "environment_data_history")
public class EnvironmentHistoryBean {
    @DatabaseField(generatedId=true)//自增长的主键
    public int id;

    @DatabaseField
    public String sensorName;

    @DatabaseField
    public double temperature;
    @DatabaseField
    public double temperatureDeta;
    @DatabaseField
    public int temperatureLevel;

    @DatabaseField
    public double humidity;
    @DatabaseField
    public double humidityDeta;
    @DatabaseField
    public int humidityLevel;

    @DatabaseField
    public double pm25;
    @DatabaseField
    public double pm25Deta;
    @DatabaseField
    public int pm25Level;

    @DatabaseField
    public double pm10;
    @DatabaseField
    public double pm10Deta;
    @DatabaseField
    public int pm10Level;

    @DatabaseField
    public double co2;
    @DatabaseField
    public double co2Deta;
    @DatabaseField
    public int co2Level;

    @DatabaseField
    public double ch2o;
    @DatabaseField
    public double ch2oDeta;
    @DatabaseField
    public int ch2oLevel;

    @DatabaseField
    public double voc;
    @DatabaseField
    public double vocDeta;
    @DatabaseField
    public int vocLevel;

    @DatabaseField
    public double o2;
    @DatabaseField
    public double o2Deta;
    @DatabaseField
    public int o2Level;

    @DatabaseField
    public double beam;
    @DatabaseField
    public double beamDeta;
    @DatabaseField
    public int beamLevel;

    @DatabaseField
    public double noise;
    @DatabaseField
    public double noiseDeta;
    @DatabaseField
    public int noiseLevel;

    @DatabaseField
    public double wind;
    @DatabaseField
    public double windDeta;
    @DatabaseField
    public int windLevel;

    @DatabaseField
    public Long date;
}
