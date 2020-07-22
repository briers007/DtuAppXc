package com.minorfish.dtuapp.module.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.abs.App;
import com.minorfish.dtuapp.module.model.RealDataBean;
import com.tangjd.common.utils.DateUtil;
import com.tangjd.common.utils.DecimalUtil;
import com.tangjd.common.widget.RvBase;

/**
 * Author: Administrator
 * Date: 2018/7/11
 */
public class RvRealData extends RvBase<RealDataBean> {
    public RvRealData(Context context) {
        this(context, null);
    }

    public RvRealData(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvRealData(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new GridLayoutManager(context, 2);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_item_real_data_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, RealDataBean bean) {
        holder.setText(R.id.tv_title, bean.mTitle)
                .setText(R.id.tv_value, DecimalUtil.simpleFormat(bean.mValue))
               // .setText(R.id.tv_address, bean.mAddress)
                .setText(R.id.tv_time, DateUtil.simpleFormat("MM-dd HH:mm:ss", bean.mTime));
        if(bean.level==1){  //0正常 1预警 2报警
            holder.setText(R.id.tv_status, "警告");
            holder.setText(R.id.tv_unit, bean.mUnit);
            holder.setTextColor(R.id.tv_unit, getContext().getResources().getColor(R.color.text_yellow));
            holder.setTextColor(R.id.tv_status, getContext().getResources().getColor(R.color.text_yellow));
            holder.setTextColor(R.id.tv_value, getContext().getResources().getColor(R.color.text_yellow));
        }else if(bean.level==2){
            holder.setText(R.id.tv_status, "超标");
            holder.setText(R.id.tv_unit, bean.mUnit);
            holder.setTextColor(R.id.tv_unit, getContext().getResources().getColor(R.color.text_red));
            holder.setTextColor(R.id.tv_status, getContext().getResources().getColor(R.color.text_red));
            holder.setTextColor(R.id.tv_value, getContext().getResources().getColor(R.color.text_red));
        }else{
            holder.setText(R.id.tv_status, "正常");
            holder.setText(R.id.tv_unit, bean.mUnit);
            holder.setTextColor(R.id.tv_unit, getContext().getResources().getColor(R.color.text_green));
            holder.setTextColor(R.id.tv_status, getContext().getResources().getColor(R.color.text_green));
            holder.setTextColor(R.id.tv_value, getContext().getResources().getColor(R.color.text_green));
        }
        if(App.getApp().getSensorSetting()!=null){
            holder.setText(R.id.tv_address, App.getApp().getSensorSetting().mAddress);
        }
    }
}
