package com.minorfish.dtuapp.module.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Administrator
 * Date: 2018/7/12
 */
public class PortBean {
    public List<String> data = new ArrayList<>();

//    static {
//        data.add("未选择");
//        data.add("/dev/bus/usb/001/003");
//    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
