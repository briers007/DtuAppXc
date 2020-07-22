package com.minorfish.dtuapp.module.model;

import android.support.annotation.Nullable;

import com.minorfish.dtuapp.module.dicts.AddressBean;
import com.minorfish.dtuapp.module.dicts.DtuModelBean;
import com.minorfish.dtuapp.module.dicts.DtuVendorBean;

import java.io.Serializable;

/**
 * Author: Administrator
 * Date: 2018/7/11
 */
public class DtuSettingBean implements Serializable {

    public String mPositionName;

    public DtuVendorBean mDtuVendorBean;
    public int mDtuVendorIndex;

    public DtuModelBean mDtuModelBean;
    public int mDtuModelIndex;

    @Nullable
    public String mDtuName;

    public AddressBean mAddressBean;
    public int mAddressIndex;

    public AddressBean.AreaBean mAreaBean;
    public int mAreaIndex;

    public AddressBean.AreaBean.OrgBean mOrgBean;
    public int mOrgIndex;

    @Nullable
    public String mAddressDetail;

    public String mMac;

    @Nullable
    public String mMacOther;

    @Nullable
    public String mIpAddress;
}
