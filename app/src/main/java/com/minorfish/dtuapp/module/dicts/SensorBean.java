package com.minorfish.dtuapp.module.dicts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SensorBean implements Serializable {

    private static final long serialVersionUID = -111201451060111039L;
    /**
     * code : string
     * monitorSubType : string
     */

    public String code;
    public String monitorSubType;

    public static SensorBean objectFromData(String str) {

        return new Gson().fromJson(str, SensorBean.class);
    }

    public static List<SensorBean> arraySensorBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<SensorBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }
}
