package com.minorfish.dtuapp.module.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.model.EnvironmentHistoryBean;
import com.minorfish.dtuapp.module.model.XuzhouDataHistoryBean;
import com.tangjd.common.utils.DateUtil;
import com.tangjd.common.widget.RvBase;

/**
 * Author: Chris
 * Date: 2018/7/11
 */
public class RvHistoryEnvironment extends RvBase<EnvironmentHistoryBean> {

    public RvHistoryEnvironment(Context context) {
        this(context, null);
    }

    public RvHistoryEnvironment(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvHistoryEnvironment(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_item_history_env_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, EnvironmentHistoryBean bean) {
        holder.setText(R.id.temp_value, bean.temperature+"")
                .setText(R.id.humidity_value, bean.humidity+"")
                .setText(R.id.pm25_value, bean.pm25+"")
                .setText(R.id.pm10_value, bean.pm10+"")
                .setText(R.id.co2_value, bean.co2+"")
                .setText(R.id.ch2o_value, bean.ch2o+"")
                .setText(R.id.voc_value, bean.voc+"");

        try {
            long time = bean.date;
            String dateStr = DateUtil.simpleFormat("yyyy-MM-dd", time);
            String timeStr = DateUtil.simpleFormat("HH:mm", time);
            holder.setText(R.id.tv_date, dateStr);
            holder.setText(R.id.tv_time, timeStr);
        }catch (Exception e){}

        if(bean.temperatureLevel==2){
            holder.setTextColor(R.id.temp_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.temp_deta, bean.temperatureDeta+"");
            if (bean.temperatureDeta > 0) {
                holder.setTextColor(R.id.temp_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.temp_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.temp_deta,true);
        } else if(bean.temperatureLevel==1) {
            holder.setTextColor(R.id.temp_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.temp_deta,false);
        } else {
            holder.setTextColor(R.id.temp_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.temp_deta,false);
        }

        if(bean.humidityLevel==2){
            holder.setTextColor(R.id.humidity_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.humidity_deta, bean.humidityDeta+"");
            if (bean.humidityDeta > 0) {
                holder.setTextColor(R.id.humidity_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.humidity_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.humidity_deta,true);
        } else if(bean.humidityLevel==1) {
            holder.setTextColor(R.id.humidity_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.humidity_deta,false);
        } else{
            holder.setTextColor(R.id.humidity_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.humidity_deta,false);
        }

        if(bean.pm25Level==2){
            holder.setTextColor(R.id.pm25_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.pm25_deta, bean.pm25Deta+"");
            if (bean.pm25Deta > 0) {
                holder.setTextColor(R.id.pm25_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.pm25_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.pm25_deta,true);
        } else if(bean.pm25Level==1) {
            holder.setTextColor(R.id.pm25_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.pm25_deta,false);
        } else{
            holder.setTextColor(R.id.pm25_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.pm25_deta,false);
        }

        if(bean.pm10Level==2){
            holder.setTextColor(R.id.pm10_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.pm10_deta, bean.pm10Deta+"");
            if (bean.pm10Deta > 0) {
                holder.setTextColor(R.id.pm10_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.pm10_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.pm10_deta,true);
        } else if(bean.pm10Level==1) {
            holder.setTextColor(R.id.pm10_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.pm10_deta,false);
        } else {
            holder.setTextColor(R.id.pm10_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.pm10_deta,false);
        }

        if(bean.co2Level==2){
            holder.setTextColor(R.id.co2_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.co2_deta, bean.co2Deta+"");
            if (bean.co2Deta > 0) {
                holder.setTextColor(R.id.co2_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.co2_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.co2_deta,true);
        } else if(bean.co2Level==1) {
            holder.setTextColor(R.id.co2_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.co2_deta,false);
        } else {
            holder.setTextColor(R.id.co2_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.co2_deta,false);
        }

        if(bean.ch2oLevel==2){
            holder.setTextColor(R.id.ch2o_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.ch2o_deta, bean.ch2oDeta+"");
            if (bean.ch2oDeta > 0) {
                holder.setTextColor(R.id.ch2o_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.ch2o_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.ch2o_deta,true);
        } else if(bean.ch2oLevel==1) {
            holder.setTextColor(R.id.ch2o_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.ch2o_deta,false);
        } else {
            holder.setTextColor(R.id.ch2o_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.ch2o_deta,false);
        }

        if(bean.vocLevel==2){
            holder.setTextColor(R.id.voc_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.voc_deta, bean.vocDeta+"");
            if (bean.vocDeta > 0) {
                holder.setTextColor(R.id.voc_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.voc_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.voc_deta,true);
        } else if(bean.vocLevel==1) {
            holder.setTextColor(R.id.voc_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.voc_deta,false);
        } else {
            holder.setTextColor(R.id.voc_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.voc_deta,false);
        }
    }
}
