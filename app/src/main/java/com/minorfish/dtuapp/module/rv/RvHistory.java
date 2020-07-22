package com.minorfish.dtuapp.module.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.model.RealDataBean;
import com.minorfish.dtuapp.module.model.RealDataHistoryBean;
import com.tangjd.common.utils.DateUtil;
import com.tangjd.common.utils.DecimalUtil;
import com.tangjd.common.widget.RvBase;

/**
 * Author: Chris
 * Date: 2018/7/11
 */
public class RvHistory extends RvBase<RealDataHistoryBean> {
    public RvHistory(Context context) {
        this(context, null);
    }

    public RvHistory(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvHistory(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_item_history_data_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, RealDataHistoryBean bean) {
        holder.setText(R.id.ph_value, bean.ph+"")
                .setText(R.id.zhuodu_value, bean.zhuodu+"")
                .setText(R.id.temperature_value, bean.temperature+"")
                .setText(R.id.yulv_value, bean.yulv+"");
        try {
            long time = bean.date;
            String dateStr = DateUtil.simpleFormat("yyyy-MM-dd", time);
            String timeStr = DateUtil.simpleFormat("HH:mm", time);
            holder.setText(R.id.tv_date, dateStr);
            holder.setText(R.id.tv_time, timeStr);
        }catch (Exception e){}

        if(bean.phLevel==2){
            holder.setTextColor(R.id.ph_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.ph_deta, bean.phDeta+"");
            if (bean.phDeta > 0) {
                holder.setTextColor(R.id.ph_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.ph_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.ph_deta,true);
        } else if(bean.phLevel==1) {
            holder.setTextColor(R.id.ph_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.ph_deta,false);
        } else {
            holder.setTextColor(R.id.ph_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.ph_deta,false);
        }

        if(bean.yulvLevel==2){
            holder.setTextColor(R.id.yulv_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.yulv_deta, bean.yulvDeta+"");
            if (bean.yulvDeta > 0) {
                holder.setTextColor(R.id.yulv_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.yulv_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.yulv_deta,true);
        } else if(bean.yulvLevel==1) {
            holder.setTextColor(R.id.yulv_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.yulv_deta,false);
        } else{
            holder.setTextColor(R.id.yulv_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.yulv_deta,false);
        }

        if(bean.zhuoduLevel==2){
            holder.setTextColor(R.id.zhuodu_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.zhuodu_deta, bean.zhuoduDeta+"");
            if (bean.zhuoduDeta > 0) {
                holder.setTextColor(R.id.zhuodu_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.zhuodu_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.zhuodu_deta,true);
        } else if(bean.zhuoduLevel==1) {
            holder.setTextColor(R.id.zhuodu_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.zhuodu_deta,false);
        } else{
            holder.setTextColor(R.id.zhuodu_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.zhuodu_deta,false);
        }

        if(bean.temperatureLevel==2){
            holder.setTextColor(R.id.temperature_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.temperature_deta, bean.temperatureDeta+"");
            if (bean.temperatureDeta > 0) {
                holder.setTextColor(R.id.temperature_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.temperature_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.temperature_deta,true);
        } else if(bean.temperatureLevel==1) {
            holder.setTextColor(R.id.temperature_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.temperature_deta,false);
        } else {
            holder.setTextColor(R.id.temperature_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.temperature_deta,false);
        }
    }
}
