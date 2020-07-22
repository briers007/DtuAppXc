package com.minorfish.dtuapp.module.model;

import com.minorfish.dtuapp.module.dicts.EstimateTypeBean;
import com.minorfish.dtuapp.module.dicts.SensorModelBean;
import com.minorfish.dtuapp.module.dicts.SensorVendorBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Administrator
 * Date: 2018/7/12
 */
public class SensorSettingBean implements Serializable {

    private static final long serialVersionUID = 110501481060011033L;

    public SensorVendorBean mVendorBean;
    public int mVendorIndex;
    public SensorModelBean mModelBean;
    public int mModelIndex;

    public String mSensorName;
    public int mSendFreq;
    public String mSerialPort; //串口号
    public int mSerialPortIndex;

    public EstimateTypeBean mEstimateType;
    public int mEstimateIndex;

    public List<EstimateTypeBean.EstimateSubTypeBean> mSubTypeBeans;
    public String mSubTypeText;
    public List<Integer> mSelectSubTypes = new ArrayList<>();

    public String mAddress;
}
