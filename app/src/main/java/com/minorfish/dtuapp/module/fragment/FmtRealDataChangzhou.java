package com.minorfish.dtuapp.module.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.ActFrame2;
import com.minorfish.dtuapp.module.rv.RvRealDataMulti;
import com.minorfish.dtuapp.module.dicts.DeviceMonitorEnum;
import com.minorfish.dtuapp.module.model.RealDataBean;
import com.minorfish.dtuapp.module.model.TriggerBean;
import com.minorfish.dtuapp.util.Utils;
import com.tangjd.common.abs.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Author: Chris  （常州版首页）
 * Date: 2018/7/11
 */
public class FmtRealDataChangzhou extends BaseFragment {

    private ActFrame2 mActivity;

    private int type = 0;
    private int height1 = 0;

    final List<RealDataBean> rvData1 = new ArrayList<>();
    private int currentPosition = 0;
    volatile HashMap<DeviceMonitorEnum, TriggerBean> mData = new HashMap<>();

    @Bind(R.id.rv_real_data_multi)
    volatile RvRealDataMulti rvRealData;

    @Bind(R.id.real_radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.rb_real_1)
    RadioButton radio1;
    @Bind(R.id.rb_real_2)
    RadioButton radio2;

    public static FmtRealDataChangzhou newInstance(int type) {
        FmtRealDataChangzhou fragment = new FmtRealDataChangzhou();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            type = args.getInt("type",0);
        }
    }

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fmt_real_data_multi_layout, container, false);
    }

    @Override
    protected void initView() {
        mActivity =  (ActFrame2) getActivity();
        if(type == 1) {
            height1 = Utils.dip2px(getActivity(), 188);
            rvRealData.setHeight(height1);
        }else{
            height1 = Utils.dip2px(getActivity(), 94);
            rvRealData.setHeight(height1);
        }
        radioGroup.setVisibility(View.GONE);
    }

    @Override
    protected void getDataJustOnce() {
    }

    public synchronized void showData(HashMap<DeviceMonitorEnum, TriggerBean> data) {
        if(data == null){
            return;
        }
        mData.putAll(data);
        rvData1.clear();
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
            rvData1.add(bean);
        }
        setData(rvData1);
    }

    private synchronized void setData(final List<RealDataBean> rvData) {
        if (mActivity!=null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rvRealData.setData(rvData);
                }
            });
        }
    }
}
