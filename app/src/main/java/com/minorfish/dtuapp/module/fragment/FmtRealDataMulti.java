package com.minorfish.dtuapp.module.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.tangjd.common.utils.StringKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Author: Chris  （多个监测站版首页）
 * Date: 2018/7/11
 */
public class FmtRealDataMulti extends BaseFragment {

    private ActFrame2 mActivity;

    private int height1 = 0;
    private int height2 = 0;
    final List<RealDataBean> rvData1 = new ArrayList<>();
    final List<RealDataBean> rvData2 = new ArrayList<>();
    private int currentPosition = 0;

    volatile HashMap<DeviceMonitorEnum, TriggerBean> mData = new HashMap<>();
    volatile HashMap<DeviceMonitorEnum, TriggerBean> mDataUrea = new HashMap<>();
    @Bind(R.id.rv_real_data_multi)
    volatile RvRealDataMulti rvRealData;

    @Bind(R.id.real_radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.rb_real_1)
    RadioButton radio1;
    @Bind(R.id.rb_real_2)
    RadioButton radio2;

    private String name1 = "";
    private String name2 = "";
    private int type = 0;

    public static FmtRealDataMulti newInstance(int type) {
        FmtRealDataMulti fragment = new FmtRealDataMulti();
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
//        if(type == 0) {
            height1 = Utils.dip2px(getActivity(), 168);
            rvRealData.setHeight(height1);
//        }else{
//            height1 = Utils.dip2px(getActivity(), 94);
//            rvRealData.setHeight(height1);
//        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_real_1:
                        currentPosition = 0;
                        try {
                            rvRealData.setData(rvData1);
                        }catch (Exception e){}
                        break;
                    case R.id.rb_real_2:
                        currentPosition = 1;
                        try {
                            rvRealData.setData(rvData2);
                        }catch (Exception e){}
                        break;
                    default:
                        break;
                }
            }
        });
        if(StringKit.isNotEmpty(mActivity.tabName1)){
            radio1.setText(mActivity.tabName1);
        } else if(StringKit.isNotEmpty(name1)){
            radio1.setText(name1);
        }
        if(StringKit.isNotEmpty(mActivity.tabName2)){
            radio2.setText(mActivity.tabName2);
            radio2.setVisibility(View.VISIBLE);
        } else if(StringKit.isNotEmpty(name2)){
            radio2.setText(name2);
            radio2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void getDataJustOnce() {
    }

    public void setRadio(String name, int type) {
        try {
            if (type == 0) {
                if (radio1 != null) {
                    radio1.setText(name);
                }
                name1 = name;
            } else {
                if (radio2 != null) {
                    radio2.setText(name);
                    radio2.setVisibility(View.VISIBLE);
                }
                name2 = name;
            }
        }catch (Exception e){}
    }

    public synchronized void showData(HashMap<DeviceMonitorEnum, TriggerBean> data, final int type) {
        if(data == null){
            return;
        }
        try {
            if (type == 0) {
                mData.putAll(data);
                rvData1.clear();
                for (Map.Entry<DeviceMonitorEnum, TriggerBean> item : mData.entrySet()) {
                    TriggerBean triggerBean = item.getValue();
                    RealDataBean bean = new RealDataBean();
                    bean.mTitle = item.getKey().getValue();
                    bean.mUnit = item.getKey().getUnit();
                    if (triggerBean != null) {
                        bean.mValue = triggerBean.value;
                        bean.level = triggerBean.level;
                    }
                    bean.mTime = System.currentTimeMillis();
                    rvData1.add(bean);
                }
            } else {
                mDataUrea.putAll(data);
                rvData2.clear();
                for (Map.Entry<DeviceMonitorEnum, TriggerBean> item : mDataUrea.entrySet()) {
                    TriggerBean triggerBean = item.getValue();
                    RealDataBean bean = new RealDataBean();
                    bean.mTitle = item.getKey().getValue();
                    bean.mUnit = item.getKey().getUnit();
                    if (triggerBean != null) {
                        bean.mValue = triggerBean.value;
                        bean.level = triggerBean.level;
                    }
                    bean.mTime = System.currentTimeMillis();
                    rvData2.add(bean);
                }
            }

            if (type == currentPosition) {
                rvRealData.post(new Runnable() {
                    @Override
                    public void run() {
                        if (type == 0) {
                            setData(rvData1);
                        } else {
                            setData(rvData2);
                        }
                    }
                });
            }
        }catch (Exception e){}
    }

    private synchronized void setData(List<RealDataBean> rvData) {
        rvRealData.setData(rvData);
    }
}
