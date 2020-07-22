package com.minorfish.dtuapp.module.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangjd on 2018/4/20.
 */

public class SignInBean {

    /**
     * username : 320000
     * token : eyJhbGciOiJIUzUxMiJ9.eyJpbnN0SWQiOjEsInByb3ZpbmNlIjoiMzIwMDAwIiwiY2l0eSI6bnVsbCwiZGlzdHJpY3QiOm51bGwsImluc3ROYW1lIjoi5rGf6IuP55yB5Y2r55uRIiwidHlwZSI6MiwiZXhwIjoxNjI0MjAyNzYxLCJ1c2VySWQiOjEsImlhdCI6MTUyNDIwNTU2MSwidXNlcm5hbWUiOiIzMjAwMDAifQ.c3vqSt0GmmiAXJGYZkV3Md9n5gz5iLmKanjbesCQlpAX2P-xiTwOeDE16UZEtd79IftiReQ7MO5rHJm2WEE5vw
     * userId : 1
     * province : 320000
     * city : null
     * district : null
     * type : 2
     */

    public String username;
    public String token;
    public String userId;
    public String province;
    public String city;
    public String district;
    /**
     * 2:省卫监
     * 3:市卫监
     * 4:区卫监
     * 5:机构
     */
    public int type;
    public String instId;
    public String instName;


    /**
     * 将字符串转换成SignInBean实例对象
     */

    public static SignInBean objectFromData(String str) {
        return new Gson().fromJson(str, SignInBean.class);
    }

    /**
     * 将字符串转换成SignInBean对象数组
     */

    public static List<SignInBean> arrayFromData(String str) {
        Type listType = new TypeToken<ArrayList<SignInBean>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }
}
