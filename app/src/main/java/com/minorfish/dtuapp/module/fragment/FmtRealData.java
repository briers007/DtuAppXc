package com.minorfish.dtuapp.module.fragment;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.rv.RvRealData;
import com.minorfish.dtuapp.module.dicts.DeviceMonitorEnum;
import com.minorfish.dtuapp.module.model.RealDataBean;
import com.minorfish.dtuapp.module.model.TriggerBean;
import com.tangjd.common.abs.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;


public class FmtRealData extends BaseFragment {
    volatile HashMap<DeviceMonitorEnum, TriggerBean> mData = new HashMap<>();
    @Bind(R.id.rv_real_data)
    volatile RvRealData rvRealData;

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fmt_real_data_layout, container, false);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void getDataJustOnce() {
    }

    public synchronized void showData(HashMap<DeviceMonitorEnum, TriggerBean> data) {
        if(data == null){
            return;
        }
        mData.putAll(data);
        final List<RealDataBean> rvData = new ArrayList<>();
        for (Map.Entry<DeviceMonitorEnum, TriggerBean> item : mData.entrySet()) {
            TriggerBean triggerBean = item.getValue();
            RealDataBean bean = new RealDataBean();
            bean.mTitle = item.getKey().getValue();
            bean.mUnit = item.getKey().getUnit();
            if (triggerBean!=null) {
                bean.mValue = triggerBean.value;
                bean.level = triggerBean.level;
            }
            bean.mTime = System.currentTimeMillis();
            rvData.add(bean);
        }
        rvRealData.post(new Runnable() {
            @Override
            public void run() {
                setData(rvData);
            }
        });
    }

    private synchronized void setData(List<RealDataBean> rvData) {
        rvRealData.setData(rvData);
    }
}
