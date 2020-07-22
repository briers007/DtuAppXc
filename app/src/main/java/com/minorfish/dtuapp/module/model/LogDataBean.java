package com.minorfish.dtuapp.module.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Author: Chris
 * Date: 2018/7/17
 */
@DatabaseTable(tableName = "log_data")
public class LogDataBean {
    @DatabaseField(generatedId=true)//自增长的主键
    public int id;

    @DatabaseField
    public int level;
    @DatabaseField
    public String sensorId;
    @DatabaseField
    public String reason;

    @DatabaseField
    public Long date;
}
