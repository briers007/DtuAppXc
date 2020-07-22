package com.minorfish.dtuapp.module.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.model.FangsheHistoryBean;
import com.minorfish.dtuapp.module.model.XuzhouDataHistoryBean;
import com.tangjd.common.utils.DateUtil;
import com.tangjd.common.widget.RvBase;

/**
 * Author: Chris
 * Date: 2018/7/11
 */
public class RvHistoryFangshe extends RvBase<FangsheHistoryBean> {

    public RvHistoryFangshe(Context context) {
        this(context, null);
    }

    public RvHistoryFangshe(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvHistoryFangshe(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_item_history_fangshe_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, FangsheHistoryBean bean) {
        holder.setText(R.id.ph_value, bean.value+"");
        holder.setText(R.id.sensor_name, bean.sensorName+"");

        try {
            long time = bean.date;
            String dateStr = DateUtil.simpleFormat("yyyy-MM-dd", time);
            String timeStr = DateUtil.simpleFormat("HH:mm", time);
            holder.setText(R.id.tv_date, dateStr);
            holder.setText(R.id.tv_time, timeStr);
        }catch (Exception e){}

        if(bean.valueLevel==2){
            holder.setTextColor(R.id.ph_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.ph_deta, bean.valueDeta+"");
            if (bean.valueDeta > 0) {
                holder.setTextColor(R.id.ph_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.ph_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.ph_deta,true);
        } else if(bean.valueLevel==1) {
            holder.setTextColor(R.id.ph_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.ph_deta,false);
        } else {
            holder.setTextColor(R.id.ph_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.ph_deta,false);
        }
    }
}
