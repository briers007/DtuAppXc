package com.minorfish.dtuapp.module.fragment;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.abs.App;
import com.minorfish.dtuapp.module.ActFrame2;
import com.minorfish.dtuapp.module.rv.RvSensorList;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.tangjd.common.abs.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


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
