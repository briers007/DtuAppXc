package com.minorfish.dtuapp.module.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Author: Chris 放射
 * Date: 2018/7/17
 */
@DatabaseTable(tableName = "fangshe_data_history")
public class FangsheHistoryBean {
    @DatabaseField(generatedId=true)//自增长的主键
    public int id;

    @DatabaseField
    public String sensorName;

    @DatabaseField
    public double value;
    @DatabaseField
    public double valueDeta;
    @DatabaseField
    public int valueLevel;

    @DatabaseField
    public Long date;
}
