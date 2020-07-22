package com.minorfish.dtuapp.rs485;

/**
 * Created by Chris on 2018/7/17.485数据获取回调接口 */
public interface OnGetDataListener {
    void OnGetData485(byte[] singlePacket);
}
