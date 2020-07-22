package com.minorfish.dtuapp.module.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Author: Chris 徐州五参
 * Date: 2018/7/17
 */
@DatabaseTable(tableName = "xuzhou_data_history")
public class XuzhouDataHistoryBean {
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
    public double cod;
    @DatabaseField
    public double codDeta;
    @DatabaseField
    public int codLevel;

    @DatabaseField
    public Long date;
}
