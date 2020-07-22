package com.minorfish.dtuapp.module.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.model.LogDataBean;
import com.minorfish.dtuapp.module.model.RealDataHistoryBean;
import com.tangjd.common.utils.DateUtil;
import com.tangjd.common.widget.RvBase;

/**
 * Author: Chris
 * Date: 2018/7/11
 */
public class RvLog extends RvBase<LogDataBean> {
    public RvLog(Context context) {
        this(context, null);
    }

    public RvLog(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvLog(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_item_error_data_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, LogDataBean bean) {
        holder.setText(R.id.text_sensor_id, bean.sensorId+"")
                .setText(R.id.text_reason, bean.reason+"");
        if(bean.level==2){
            holder.setText(R.id.text_type, "异常");
            holder.setTextColor(R.id.text_type, getContext().getResources().getColor(R.color.text_red));
        } else if(bean.level==1) {
            holder.setText(R.id.text_type, "警告");
            holder.setTextColor(R.id.text_type, getContext().getResources().getColor(R.color.text_yellow));
        }
        try {
            long time = bean.date;
            String dateStr = DateUtil.simpleFormat("yyyy-MM-dd", time);
            String timeStr = DateUtil.simpleFormat("HH:mm", time);
            holder.setText(R.id.tv_date, dateStr);
            holder.setText(R.id.tv_time, timeStr);
        }catch (Exception e){}
    }
}
