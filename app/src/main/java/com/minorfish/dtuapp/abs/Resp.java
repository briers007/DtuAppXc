package com.minorfish.dtuapp.abs;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tangjd on 2018/4/20.
 */

public class Resp {

    /**
     * code : 0
     * data :
     * message : string
     */

    public int code;
    public String content;
    public Object data;
    public String message;


    /**
     * 将object转换成实例对象
     */

    public static Resp objectFromData(JSONObject object) {
        Resp resp = new Resp();
        resp.code = object.optInt("code");
        resp.data = object.opt("data");
        resp.content = resp.data + "";
        resp.message = object.optString("message");
        if (TextUtils.isEmpty(resp.message)) {
            resp.message = "数据出错";
        }
        return resp;
    }

    /**
     * 将字符串转换成实例对象
     */
    public static Resp objectFromData(String data) {
        Resp resp = new Resp();
        JSONObject object;
        try {
            object = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            resp.message = "数据出错";
            return resp;
        }
        resp.code = object.optInt("code");
        resp.data = object.opt("data");
        resp.content = resp.data + "";
        resp.message = object.optString("message");
        if (TextUtils.isEmpty(resp.message)) {
            resp.message = "数据出错";
        }
        return resp;
    }


    public boolean isSuccess() {
        return code == 200;
    }

    public boolean canParse() {
        if (code != 200) {
            return false;
        }
        if (data == null) {
            message = "没有数据";
            return false;
        }
        if (data instanceof JSONArray && ((JSONArray) data).length() == 0) {
            message = "没有数据";
            return false;
        }
        if (!(data instanceof JSONArray) && !(data instanceof JSONObject)) {
            message = "数据格式出错";
            return false;
        }
        return true;
    }
}
