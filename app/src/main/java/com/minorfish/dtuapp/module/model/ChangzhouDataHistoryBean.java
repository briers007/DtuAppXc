package com.minorfish.dtuapp.module.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Author: Chris 常州六参
 * Date: 2018/7/17
 */
@DatabaseTable(tableName = "changzhou_data_history")
public class ChangzhouDataHistoryBean {
    @DatabaseField(generatedId=true)//自增长的主键
    public int id;

    @DatabaseField
    public double ph;
    @DatabaseField
    public double phDeta;
    @DatabaseField
    public int phLevel;
    @DatabaseField
    public double yulv;
    @DatabaseField
    public double yulvDeta;
    @DatabaseField
    public int yulvLevel;
    @DatabaseField
    public double temperature;
    @DatabaseField
    public double temperatureDeta;
    @DatabaseField
    public int temperatureLevel;
    @DatabaseField
    public double zhuodu;
    @DatabaseField
    public double zhuoduDeta;
    @DatabaseField
    public int zhuoduLevel;

    @DatabaseField
    public double cond;
    @DatabaseField
    public double condDeta;
    @DatabaseField
    public int condLevel;

    @DatabaseField
    public double toc;
    @DatabaseField
    public double tocDeta;
    @DatabaseField
    public int tocLevel;

    @DatabaseField
    public Long date;
}
