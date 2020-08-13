package com.minorfish.dtuapp.module.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.abs.App;
import com.minorfish.dtuapp.abs.Constants;
import com.minorfish.dtuapp.module.ActFrame2;
import com.minorfish.dtuapp.module.ActSensorSetting;
import com.minorfish.dtuapp.module.rv.RvSensorList;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.tangjd.common.abs.BaseFragment;
import com.tangjd.common.utils.FileKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

import static android.app.Activity.RESULT_OK;


public class FmtSensorSettingNew extends BaseFragment {

    @Bind(R.id.rv_sensor)
    RvSensorList rvSensorList;

    private List<SensorSettingBean> mSensorSettingList = new ArrayList<>();
    ActFrame2 mActivity;

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fmt_sensor_list_layout, container, false);
    }

    @Override
    protected void initView() {
        mActivity = (ActFrame2) getActivity();
        setData(true);
        rvSensorList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                //mActivity.startActForResult(ActSensorSetting.class);
                try {
                    if(position == rvSensorList.getChildCount()-1){
                        mActivity.showAddSensorFmt();
                    }
                }catch (Exception e){

                }

              /*  FmtAddSensorSetting fragment = new FmtAddSensorSetting();

                Bundle args = new Bundle();
                args.putString("vendorName",mSensorSettingList.get(position).mVendorBean.name);//生产厂家
                args.putString("modeType",mSensorSettingList.get(position).mModelBean.name);//监测站型号
                args.putString("sensorName", mSensorSettingList.get(position).mSensorName);//监测站名称
                args.putInt("sendFlag",mSensorSettingList.get(position).mSendFreq);//发送频率
                args.putString("sensorPort",mSensorSettingList.get(position).mSerialPort);//串口号
                args.putString("sensorType",mSensorSettingList.get(position).mEstimateType.name);//监测类型


                fragment.setArguments(args);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();*/

            }
        });
    }

    public void setData(boolean addLast) {
        List<SensorSettingBean> sensorSettingList = App.getApp().getSensorSettingList();
        mSensorSettingList.clear();
        mSensorSettingList.addAll(sensorSettingList);
        if(addLast) {
            SensorSettingBean bean = new SensorSettingBean();
            mSensorSettingList.add(bean);
        }
        rvSensorList.setData(mSensorSettingList);
    }

    @Override
    protected void getDataJustOnce() {

    }
}
