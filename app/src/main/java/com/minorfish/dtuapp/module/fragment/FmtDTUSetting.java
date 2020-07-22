package com.minorfish.dtuapp.module.fragment;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.gson.Gson;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.abs.Api;
import com.minorfish.dtuapp.abs.App;
import com.minorfish.dtuapp.abs.Constants;
import com.minorfish.dtuapp.abs.Resp;
import com.minorfish.dtuapp.module.ActFrame;
import com.minorfish.dtuapp.module.ActFrame2;
import com.minorfish.dtuapp.module.dicts.AddressBean;
import com.minorfish.dtuapp.module.dicts.DtuModelBean;
import com.minorfish.dtuapp.module.dicts.DtuVendorBean;
import com.minorfish.dtuapp.module.dicts.SensorModelBean;
import com.minorfish.dtuapp.module.model.DtuSettingBean;
import com.minorfish.dtuapp.module.widget.ItemViewEt;
import com.minorfish.dtuapp.module.widget.ItemViewEtBtn;
import com.minorfish.dtuapp.module.widget.ItemViewSpinner;
import com.minorfish.dtuapp.module.widget.ItemViewSpinnerAddress;
import com.minorfish.dtuapp.util.PreferenceKit;
import com.minorfish.dtuapp.util.Utils;
import com.tangjd.common.abs.BaseFragment;
import com.tangjd.common.manager.SPManager;
import com.tangjd.common.retrofit.string.OnStringRespListener;
import com.tangjd.common.utils.FileKit;
import com.tangjd.common.utils.StringKit;
import com.tangjd.common.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;

/**
 * 数采仪配置界面
 */

public class FmtDTUSetting extends BaseFragment {
    @Bind(R.id.item_location_name)
    ItemViewEt itemLocationName;
    @Bind(R.id.item_dtu_vendor)
    ItemViewSpinner itemDtuVendor;
    @Bind(R.id.item_dtu_model)
    ItemViewSpinner itemDtuModel;
    @Bind(R.id.item_dtu_name)
    ItemViewEt itemDtuName;
    @Bind(R.id.item_dtu_address)
    ItemViewSpinnerAddress itemDtuAddress;
    @Bind(R.id.item_dtu_org_name)
    ItemViewSpinner itemDtuOrgName;
    @Bind(R.id.item_address_detail)
    ItemViewEt itemAddressDetail;
    @Bind(R.id.item_dtu_mac)
    ItemViewEtBtn itemDtuMac;
    @Bind(R.id.item_dtu_mac_2)
    ItemViewEt itemDtuMac2;
    @Bind(R.id.item_dtu_ip)
    ItemViewEtBtn itemDtuIp;
    @Bind(R.id.btn_complete)
    Button btnComplete;

    private ActFrame2 mActivity;
    //private ActFrameSingle mActivity;

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fmt_dtu_setting_layout, container, false);
    }

    @Override
    protected void initView() {
        mActivity = (ActFrame2) getActivity();
        //mActivity = (ActFrameSingle) getActivity();
        itemDtuVendor.spContent.attachDataSource(DtuVendorBean.data);

        itemDtuModel.spContent.attachDataSource(DtuModelBean.data);

        getSensorVendor();
        getSensorModel();

//        String areaStr = PreferenceKit.getString(mActivity,"AreaTree");
//        if(StringKit.isNotEmpty(areaStr)) {
//            AddressBean.formData(areaStr);
//            initAddr();
//        }else{
            getAddr();
//        }
        getMonitorType();

        itemDtuMac.mBtnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final WifiManager wifiManager = (WifiManager) App.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                if (!wifiManager.isWifiEnabled()) {
//                    wifiManager.setWifiEnabled(true);
//                }
//                final String wifiMacAddress = wifiManager.getConnectionInfo().getMacAddress().toUpperCase();
                String wifiMacAddress = "";
                wifiMacAddress = Utils.getLocalMacAddressFromIp();
                if(StringKit.isEmpty(wifiMacAddress)){
                    try {
                        final WifiManager wifiManager = (WifiManager) App.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (!wifiManager.isWifiEnabled()) {
                            wifiManager.setWifiEnabled(true);
                        }
                        wifiMacAddress = wifiManager.getConnectionInfo().getMacAddress().toUpperCase();
                    }catch (Exception e){
                        mActivity.showLongToast("请打开wifi");
                    }
                }
                itemDtuMac.etContent.setText(wifiMacAddress);
            }
        });
        itemDtuIp.mBtnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wifiManager = (WifiManager) App.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                itemDtuIp.etContent.setText(intToIp(ipAddress));
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DtuSettingBean bean = new DtuSettingBean();
                bean.mPositionName = itemLocationName.etContent.getText().toString();
                bean.mDtuVendorBean = DtuVendorBean.data.get(itemDtuVendor.spContent.getSelectedIndex());
                bean.mDtuVendorIndex = itemDtuVendor.spContent.getSelectedIndex();
                bean.mDtuModelBean = DtuModelBean.data.get(itemDtuModel.spContent.getSelectedIndex());
                bean.mDtuModelIndex = itemDtuModel.spContent.getSelectedIndex();

                bean.mDtuName = itemDtuName.etContent.getText().toString();
                bean.mAddressBean = AddressBean.data.get(itemDtuAddress.sp2.getSelectedIndex());
                bean.mAddressIndex = itemDtuAddress.sp2.getSelectedIndex();
                bean.mAreaBean = AddressBean.data.get(itemDtuAddress.sp2.getSelectedIndex()).areaBeans.get(itemDtuAddress.sp3.getSelectedIndex());
                bean.mAreaIndex = itemDtuAddress.sp3.getSelectedIndex();
                bean.mOrgBean = AddressBean.data.get(itemDtuAddress.sp2.getSelectedIndex()).areaBeans.get(itemDtuAddress.sp3.getSelectedIndex()).orgBeans.get(itemDtuOrgName.spContent.getSelectedIndex());
                bean.mOrgIndex = itemDtuOrgName.spContent.getSelectedIndex();
                bean.mAddressDetail = itemAddressDetail.etContent.getText().toString();
                bean.mMac = itemDtuMac.etContent.getText().toString();
                bean.mMacOther = itemDtuMac2.etContent.getText().toString();
                bean.mIpAddress = itemDtuIp.etContent.getText().toString();

                if (!StringUtil.isEmpty(bean.mPositionName)
                        && bean.mDtuVendorBean != null && itemDtuVendor.spContent.getSelectedIndex() != 0
                        && bean.mDtuModelBean != null && itemDtuModel.spContent.getSelectedIndex() != 0
                        && bean.mAddressBean != null && itemDtuAddress.sp2.getSelectedIndex() != 0
                        && bean.mAreaBean != null && itemDtuAddress.sp3.getSelectedIndex() != 0
                        && bean.mOrgBean != null && itemDtuOrgName.spContent.getSelectedIndex() != 0
                        && !StringUtil.isEmpty(bean.mMac)
                        ) {
                    mActivity.showProgressDialog();
                    Api.configDtu(bean, new OnStringRespListener() {
                        @Override
                        public void onResponse(String s) {
                            Resp resp = Resp.objectFromData(s);
                            if (resp.isSuccess()) {
                                App.getApp().setDtuSetting(bean);
                                mActivity.showLongToast(resp.message);
                                mActivity.getMonitorDicParameters();
                                mActivity.connectMqtt();
                            } else {
                                onError(resp.message);
                            }
                        }

                        @Override
                        public void onError(String s) {
                            mActivity.showLongToast(s);
                        }

                        @Override
                        public void onFinish(boolean b) {
                            mActivity.dismissProgressDialog();
                        }
                    });

                } else {
                    mActivity.showLongToast("请填写完整");
                }
            }
        });
//        DtuSettingBean saveBean = App.getApp().getDtuSetting();
//        if(saveBean != null){
//            setData(saveBean);
//        }
    }

    private void setData(DtuSettingBean saveBean) {
        try {
            itemLocationName.etContent.setText(saveBean.mPositionName);
            itemDtuVendor.spContent.setSelectedIndex(saveBean.mDtuVendorIndex);
            itemDtuModel.spContent.setSelectedIndex(saveBean.mDtuModelIndex);
            itemDtuName.etContent.setText(saveBean.mDtuName);

            itemDtuAddress.sp2.setSelectedIndex(saveBean.mAddressIndex);
            itemDtuAddress.sp3.attachDataSource(AddressBean.data.get(saveBean.mAddressIndex).areaBeans);
            itemDtuAddress.sp3.setSelectedIndex(saveBean.mAreaIndex);

            itemDtuOrgName.spContent.attachDataSource(AddressBean.data.get(saveBean.mAddressIndex).areaBeans.get(saveBean.mAreaIndex).orgBeans);
            itemDtuOrgName.spContent.setSelectedIndex(saveBean.mOrgIndex);

            itemAddressDetail.etContent.setText(saveBean.mAddressDetail);
            itemDtuMac.etContent.setText(saveBean.mMac);
            itemDtuMac2.etContent.setText(saveBean.mMacOther);
            itemDtuIp.etContent.setText(saveBean.mIpAddress);
        }catch (Exception e){}
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    @Override
    protected void getDataJustOnce() {

    }

    private void initAddr() {
        itemDtuAddress.sp1.attachDataSource(Arrays.asList(new String[]{"江苏省"}));
        itemDtuAddress.sp2.attachDataSource(AddressBean.data);
        itemDtuAddress.sp3.attachDataSource(AddressBean.data.get(0).areaBeans);
        itemDtuOrgName.spContent.attachDataSource(AddressBean.data.get(0).areaBeans.get(0).orgBeans);
        itemDtuAddress.sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemDtuAddress.sp3.attachDataSource(AddressBean.data.get(i).areaBeans);
                itemDtuOrgName.spContent.attachDataSource(AddressBean.data.get(i).areaBeans.get(0).orgBeans);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        itemDtuAddress.sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemDtuOrgName.spContent.attachDataSource(AddressBean.data.get(itemDtuAddress.sp2.getSelectedIndex()).areaBeans.get(i).orgBeans);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DtuSettingBean saveBean = App.getApp().getDtuSetting();
        if(saveBean != null){
            setData(saveBean);
        }
    }

    public void getAddr() {
        Api.getAreaTree(new OnStringRespListener() {
            @Override
            public void onResponse(String data) {
                Resp resp = Resp.objectFromData(data);
                if (resp.canParse()) {
                    String dataStr = "";
                    try {
                        JSONArray array = new JSONArray(resp.content);
                        JSONObject object = array.optJSONObject(0);
                        if(object!=null) {
                            dataStr = object.opt("children") + "";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //PreferenceKit.putString(mActivity, "AreaTree", dataStr );
                    AddressBean.formData(dataStr);
                    initAddr();
                } else {
                    onError(resp.message);
                }
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });
    }

    public void getSensorVendor() {
        Api.getSensorVendor(new OnStringRespListener() {
            @Override
            public void onResponse(String data) {
                Resp resp = Resp.objectFromData(data);
                if (resp.canParse()) {
                    PreferenceKit.putString(mActivity, "SensorVendor", resp.content );
                } else {
                    onError(resp.message);
                }
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });
    }

    public void getSensorModel() {
        Api.getSensorModel(new OnStringRespListener() {
            @Override
            public void onResponse(String data) {
                Resp resp = Resp.objectFromData(data);
                if (resp.canParse()) {
                    PreferenceKit.putString(mActivity, "SensorModel", resp.content );
                } else {
                    onError(resp.message);
                }
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });
    }


    public void getMonitorType() {
        Api.getMonitorType(new OnStringRespListener() {
            @Override
            public void onResponse(String data) {
                Resp resp = Resp.objectFromData(data);
                if (resp.canParse()) {
                    PreferenceKit.putString(mActivity, "MonitorType", resp.content );
                } else {
                    onError(resp.message);
                }
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });
    }
}
