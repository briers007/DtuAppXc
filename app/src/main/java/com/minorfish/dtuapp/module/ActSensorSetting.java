package com.minorfish.dtuapp.module;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;

import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.abs.Api;
import com.minorfish.dtuapp.abs.App;
import com.minorfish.dtuapp.abs.Constants;
import com.minorfish.dtuapp.abs.Resp;
import com.minorfish.dtuapp.module.dicts.EstimateTypeBean;
import com.minorfish.dtuapp.module.dicts.SensorBean;
import com.minorfish.dtuapp.module.dicts.SensorModelBean;
import com.minorfish.dtuapp.module.dicts.SensorVendorBean;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.minorfish.dtuapp.module.widget.ItemViewEt;
import com.minorfish.dtuapp.module.widget.ItemViewSpinner;
import com.minorfish.dtuapp.module.widget.ItemViewTv;
import com.tangjd.common.abs.BaseActivity;
import com.tangjd.common.retrofit.string.OnStringRespListener;
import com.tangjd.common.utils.FileKit;
import com.tangjd.common.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ActSensorSetting extends BaseActivity {

    @Bind(R.id.item_sensor_vendor)
    ItemViewSpinner itemSensorVendor;
    @Bind(R.id.item_sensor_model)
    ItemViewSpinner itemSensorModel;
    @Bind(R.id.item_sensor_name)
    ItemViewEt itemSensorName;
    @Bind(R.id.item_sensor_freq)
    ItemViewEt itemSensorFreq;
    @Bind(R.id.item_sensor_port)
    ItemViewSpinner itemSensorPort;
    @Bind(R.id.item_sensor_type)
    ItemViewSpinner itemSensorType;
    @Bind(R.id.item_sensor_sub_type)
    ItemViewTv itemSensorSubType;
    @Bind(R.id.btn_complete)
    Button btnComplete;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.item_sensor_address)
    ItemViewEt itemSensorAddress;

    List<Integer> mSelectSubTypes = new ArrayList<>();

    public List<String> mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sensor_setting_layout);
        ButterKnife.bind(this);
        mDeviceList = (List<String>)FileKit.getObject(ActSensorSetting.this,Constants.FILE_KEY_485_DEVICE);
        initView();
    }

    private void initView() {
        itemSensorVendor.spContent.attachDataSource(SensorVendorBean.data);
        itemSensorModel.spContent.attachDataSource(SensorModelBean.data);
        itemSensorPort.spContent.attachDataSource(mDeviceList);
        itemSensorPort.spContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0) {
//                    mActivity.mDeviceIndex = i-1;
//                    mActivity.mDevice.setPath(mActivity.mDevices[i - 1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        itemSensorType.spContent.attachDataSource(EstimateTypeBean.data);
        itemSensorType.spContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemSensorSubType.etContent.setText("");
                mSelectSubTypes.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        itemSensorSubType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<Integer> selectSubTypes = new ArrayList<>();
                showMultiChoiceDialog(
                        true,
                        EstimateTypeBean.data.get(itemSensorType.spContent.getSelectedIndex()).subTypeBeans,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mSelectSubTypes = selectSubTypes;
                                StringBuilder sb = new StringBuilder();
                                for (int j = 0; j < selectSubTypes.size(); j++) {
                                    sb.append(EstimateTypeBean.data.get(itemSensorType.spContent.getSelectedIndex()).subTypeBeans.get(selectSubTypes.get(j)).name);
                                    if (j != selectSubTypes.size() - 1) {
                                        sb.append(", ");
                                    }
                                }
                                itemSensorSubType.etContent.setText(sb.toString());
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        },
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                if (b) {
                                    selectSubTypes.add(i);
                                } else {
                                    selectSubTypes.remove((Object)i);
                                }
                            }
                        }
                );
            }
        });
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.getApp().getDtuSetting() == null || StringUtil.isEmpty(App.getApp().getDtuSetting().mMac)) {
                    showTipDialog("请先配置DTU");
                    return;
                }
                final SensorSettingBean bean = new SensorSettingBean();
                bean.mVendorBean = SensorVendorBean.data.get(itemSensorVendor.spContent.getSelectedIndex());
                bean.mVendorIndex = itemSensorVendor.spContent.getSelectedIndex();
                bean.mModelBean = SensorModelBean.data.get(itemSensorModel.spContent.getSelectedIndex());
                bean.mModelIndex = itemSensorModel.spContent.getSelectedIndex();

                bean.mSensorName = itemSensorName.etContent.getText().toString();
                try {
                    bean.mSendFreq = Integer.valueOf(itemSensorFreq.etContent.getText().toString());
                } catch (Exception e) {
                    showLongToast("频率请输入整数");
                }
                bean.mSerialPort = mDeviceList.get(itemSensorPort.spContent.getSelectedIndex());
                bean.mSerialPortIndex = itemSensorPort.spContent.getSelectedIndex();
                bean.mEstimateIndex = itemSensorType.spContent.getSelectedIndex();
                bean.mEstimateType = EstimateTypeBean.data.get(itemSensorType.spContent.getSelectedIndex());
                List<EstimateTypeBean.EstimateSubTypeBean> subTypes = EstimateTypeBean.data.get(itemSensorType.spContent.getSelectedIndex()).subTypeBeans;
                if (subTypes != null) {
                    bean.mSelectSubTypes = mSelectSubTypes;
                    bean.mSubTypeText = itemSensorSubType.etContent.getText().toString();
                    bean.mSubTypeBeans = new ArrayList<>();
                    for (int i = 0; i < mSelectSubTypes.size(); i++) {
                        bean.mSubTypeBeans.add(subTypes.get(mSelectSubTypes.get(i)));
                    }
                }
                bean.mAddress = itemSensorAddress.etContent.getText().toString();
                if (
                        itemSensorVendor.spContent.getSelectedIndex() != 0
                                && itemSensorModel.spContent.getSelectedIndex() != 0
                                && !StringUtil.isEmpty(itemSensorFreq.etContent)
                                && itemSensorPort.spContent.getSelectedIndex() != 0
                                && itemSensorType.spContent.getSelectedIndex() != 0
                                && bean.mSubTypeBeans != null && !bean.mSubTypeBeans.isEmpty()
                        ) {
                    showProgressDialog();
                    Api.configSensor(bean, new OnStringRespListener() {
                        @Override
                        public void onResponse(String s) {
                            Resp resp = Resp.objectFromData(s);
                            if (resp.canParse()) {

                                App.getApp().addSensorSettingList(bean);

                                showLongToast(resp.message);

                                List<SensorBean> list = SensorBean.arraySensorBeanFromData(resp.content);
                                HashMap<String, String> map = null;
                                try {
                                    map = (HashMap<String, String>) FileKit.getObject(ActSensorSetting.this, Constants.FILE_KEY_SENSOR_DIC);
                                }catch (Exception e){}
                                if(map == null){
                                    map = new HashMap<>();
                                }
                                if(list!=null && list.size()!=0) {
                                    for (int i=0;i<list.size();i++) {
                                        SensorBean bean = list.get(i);
                                        map.put(bean.monitorSubType,bean.code);
                                    }
                                    FileKit.save(ActSensorSetting.this, map, Constants.FILE_KEY_SENSOR_DIC);
                                    //sensorMap = map;
                                }

                                setResult(RESULT_OK);
                                //mActivity.openOrSwitchPort();
                                finish();
                            } else {
                                onError(resp.message);
                            }
                        }

                        @Override
                        public void onError(String s) {
                            showLongToast(s);
                        }

                        @Override
                        public void onFinish(boolean b) {
                            dismissProgressDialog();
                        }
                    });
                } else {
                    showLongToast("请填写完整");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    private void setData(SensorSettingBean saveBean) {
//        itemSensorVendor.spContent.setSelectedIndex(saveBean.mVendorIndex);
//        itemSensorModel.spContent.setSelectedIndex(saveBean.mModelIndex);
//        itemSensorName.etContent.setText(saveBean.mSensorName);
//        itemSensorFreq.etContent.setText(saveBean.mSendFreq+"");
//        itemSensorPort.spContent.setSelectedIndex(saveBean.mSerialPortIndex);
//
//        itemSensorType.spContent.setSelectedIndex(saveBean.mEstimateIndex);
//
//        mSelectSubTypes = saveBean.mSelectSubTypes;
//        itemSensorSubType.etContent.setText(saveBean.mSubTypeText);
//        itemSensorAddress.etContent.setText(saveBean.mAddress);
//    }
}
