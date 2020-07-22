package com.minorfish.dtuapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.minorfish.dtuapp.module.model.ChangzhouDataHistoryBean;
import com.minorfish.dtuapp.module.model.EnvironmentHistoryBean;
import com.minorfish.dtuapp.module.model.FangsheHistoryBean;
import com.minorfish.dtuapp.module.model.LogDataBean;
import com.minorfish.dtuapp.module.model.RealDataHistoryBean;
import com.minorfish.dtuapp.module.model.UreaDataHistoryBean;
import com.minorfish.dtuapp.module.model.XuzhouDataHistoryBean;

import java.sql.SQLException;

/**
 * Created by Chris on 2018/7/17.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "dtu.db";
    private static final int VERSION = 9;

    private static DatabaseHelper instance;

    private RuntimeExceptionDao<RealDataHistoryBean, Long> realDataDao;
    private RuntimeExceptionDao<UreaDataHistoryBean, Long> ureaDataDao;
    private RuntimeExceptionDao<ChangzhouDataHistoryBean, Long> changzhouDataDao;
    private RuntimeExceptionDao<XuzhouDataHistoryBean, Long> xuzhouDataDao;
    private RuntimeExceptionDao<FangsheHistoryBean, Long> fangsheDataDao;
    private RuntimeExceptionDao<LogDataBean, Long> logDataDao;
    private RuntimeExceptionDao<EnvironmentHistoryBean, Long> environmentDataDao;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, RealDataHistoryBean.class);
            TableUtils.createTableIfNotExists(connectionSource, UreaDataHistoryBean.class);
            TableUtils.createTableIfNotExists(connectionSource, ChangzhouDataHistoryBean.class);
            TableUtils.createTableIfNotExists(connectionSource, XuzhouDataHistoryBean.class);
            TableUtils.createTableIfNotExists(connectionSource, FangsheHistoryBean.class);
            TableUtils.createTableIfNotExists(connectionSource, EnvironmentHistoryBean.class);
            TableUtils.createTableIfNotExists(connectionSource, LogDataBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

        try {
            TableUtils.dropTable(connectionSource, RealDataHistoryBean.class, true);
            TableUtils.dropTable(connectionSource, UreaDataHistoryBean.class, true);
            TableUtils.dropTable(connectionSource, ChangzhouDataHistoryBean.class, true);
            TableUtils.dropTable(connectionSource, XuzhouDataHistoryBean.class, true);
            TableUtils.dropTable(connectionSource, FangsheHistoryBean.class, true);
            TableUtils.dropTable(connectionSource, EnvironmentHistoryBean.class, true);
            TableUtils.dropTable(connectionSource, LogDataBean.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public RuntimeExceptionDao<RealDataHistoryBean, Long> getRealDataHistoryDao() {
        if (realDataDao == null) {
            realDataDao = getRuntimeExceptionDao(RealDataHistoryBean.class);
        }
        return realDataDao;
    }

    public RuntimeExceptionDao<UreaDataHistoryBean, Long> getUreaDataHistoryDao() {
        if (ureaDataDao == null) {
            ureaDataDao = getRuntimeExceptionDao(UreaDataHistoryBean.class);
        }
        return ureaDataDao;
    }

    public RuntimeExceptionDao<ChangzhouDataHistoryBean, Long> getChangzhouDataHistoryDao() {
        if (changzhouDataDao == null) {
            changzhouDataDao = getRuntimeExceptionDao(ChangzhouDataHistoryBean.class);
        }
        return changzhouDataDao;
    }

    public RuntimeExceptionDao<XuzhouDataHistoryBean, Long> getXuzhouDataHistoryDao() {
        if (xuzhouDataDao == null) {
            xuzhouDataDao = getRuntimeExceptionDao(XuzhouDataHistoryBean.class);
        }
        return xuzhouDataDao;
    }

    public RuntimeExceptionDao<FangsheHistoryBean, Long> getFangsheDataHistoryDao() {
        if (fangsheDataDao == null) {
            fangsheDataDao = getRuntimeExceptionDao(FangsheHistoryBean.class);
        }
        return fangsheDataDao;
    }

    public RuntimeExceptionDao<EnvironmentHistoryBean, Long> getEnvironmentHistoryDao() {
        if (environmentDataDao == null) {
            environmentDataDao = getRuntimeExceptionDao(EnvironmentHistoryBean.class);
        }
        return environmentDataDao;
    }

    public RuntimeExceptionDao<LogDataBean, Long> getLogDataDao() {
        if (logDataDao == null) {
            logDataDao = getRuntimeExceptionDao(LogDataBean.class);
        }
        return logDataDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close()
    {
        super.close();
        realDataDao = null;
        ureaDataDao = null;
        changzhouDataDao = null;
        xuzhouDataDao = null;
        fangsheDataDao = null;
        environmentDataDao = null;
        logDataDao = null;
    }
}
