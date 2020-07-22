package com.minorfish.dtuapp.module.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Author: Chris
 * Date: 2018/7/17
 */
@DatabaseTable(tableName = "urea_data_history")
public class UreaDataHistoryBean {
    @DatabaseField(generatedId=true)//自增长的主键
    public int id;

    @DatabaseField
    public double urea;
    @DatabaseField
    public double ureaDeta;
    @DatabaseField
    public int ureaLevel;

    @DatabaseField
    public Long date;
}
