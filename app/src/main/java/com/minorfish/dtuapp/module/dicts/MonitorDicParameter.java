package com.minorfish.dtuapp.module.dicts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MonitorDicParameter implements Serializable{

    private static final long serialVersionUID = -166501481060011033L;
    /**
     * code : string
     * monitorParameter : {"alias":"string","name":"string","unit":"string"}
     * warnLines : [{"limit":0,"type":0,"value":"string"}]
     */

    public String code;
    public MonitorParameterBean monitorParameter;
    public List<WarnLinesBean> warnLines;

    public static MonitorDicParameter objectFromData(String str) {

        return new Gson().fromJson(str, MonitorDicParameter.class);
    }

    public static List<MonitorDicParameter> arrayMonitorWarnLineParameterFromData(String str) {

        Type listType = new TypeToken<ArrayList<MonitorDicParameter>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static class MonitorParameterBean implements Serializable{

        private static final long serialVersionUID = 100901481060011089L;
        /**
         * alias : string
         * name : string
         * unit : string
         */

        public String alias;
        public String name;
        public String unit;

        public static MonitorParameterBean objectFromData(String str) {

            return new Gson().fromJson(str, MonitorParameterBean.class);
        }

        public static List<MonitorParameterBean> arrayMonitorParameterBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<MonitorParameterBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }
    }

    public static class WarnLinesBean implements Serializable {

        private static final long serialVersionUID = 176500481060010033L;
        /**
         * limit : 0
         * type : 0
         * value : string
         */

        public int limit;
        public int type;
        public String value;

        public static WarnLinesBean objectFromData(String str) {

            return new Gson().fromJson(str, WarnLinesBean.class);
        }

        public static List<WarnLinesBean> arrayWarnLinesBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<WarnLinesBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }
    }
}
