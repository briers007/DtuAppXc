package com.minorfish.dtuapp.module.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.model.RealDataHistoryBean;
import com.minorfish.dtuapp.module.model.UreaDataHistoryBean;
import com.tangjd.common.utils.DateUtil;
import com.tangjd.common.widget.RvBase;

/**
 * Author: Chris
 * Date: 2018/7/11
 */
public class RvHistoryUrea extends RvBase<UreaDataHistoryBean> {
    public RvHistoryUrea(Context context) {
        this(context, null);
    }

    public RvHistoryUrea(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvHistoryUrea(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_item_history_urea_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, UreaDataHistoryBean bean) {
        holder.setText(R.id.urea_value, bean.urea+"");
        try {
            long time = bean.date;
            String dateStr = DateUtil.simpleFormat("yyyy-MM-dd", time);
            String timeStr = DateUtil.simpleFormat("HH:mm", time);
            holder.setText(R.id.tv_date, dateStr);
            holder.setText(R.id.tv_time, timeStr);
        }catch (Exception e){}

        if(bean.ureaLevel==2){
            holder.setTextColor(R.id.urea_value, getContext().getResources().getColor(R.color.text_red));
            holder.setText(R.id.urea_deta, bean.ureaDeta+"");
            if (bean.ureaDeta > 0) {
                holder.setTextColor(R.id.urea_deta, getContext().getResources().getColor(R.color.text_ff4739));
            }else{
                holder.setTextColor(R.id.urea_deta, getContext().getResources().getColor(R.color.text_3bcfff));
            }
            holder.setVisible(R.id.urea_deta,true);
        } else if(bean.ureaLevel==1) {
            holder.setTextColor(R.id.urea_value, getContext().getResources().getColor(R.color.text_yellow));
            holder.setVisible(R.id.urea_deta,false);
        } else {
            holder.setTextColor(R.id.urea_value, getContext().getResources().getColor(R.color.text_green));
            holder.setVisible(R.id.urea_deta,false);
        }
    }
}
