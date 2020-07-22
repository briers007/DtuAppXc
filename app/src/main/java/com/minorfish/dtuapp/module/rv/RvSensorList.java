package com.minorfish.dtuapp.module.rv;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.model.SensorSettingBean;
import com.tangjd.common.utils.DateUtil;
import com.tangjd.common.utils.DecimalUtil;
import com.tangjd.common.widget.RvBase;

/**
 * Author: Chris
 * Date: 2018/7/27
 */
public class RvSensorList extends RvBase<SensorSettingBean> {
    public RvSensorList(Context context) {
        this(context, null);
    }

    public RvSensorList(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvSensorList(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.list_sensor_item;
    }

    @Override
    public void customConvert(BaseViewHolder holder, SensorSettingBean bean) {
        if(holder.getLayoutPosition() == getAdapter().getItemCount()-1){
            holder.setVisible(R.id.list_layout,false);
            holder.setVisible(R.id.list_add_layout,true);
        }else{
            holder.setVisible(R.id.list_layout,true);
            holder.setVisible(R.id.list_add_layout,false);
            holder.setText(R.id.list_sensor_name, "" + bean.mSensorName);
            if(bean.mVendorBean!=null) {
                holder.setText(R.id.list_sensor_made, "生产厂家：" + bean.mVendorBean.value);
            }else{
                holder.setText(R.id.list_sensor_made, "生产厂家：");
            }
            holder.setText(R.id.list_sensor_port, "串口号：" + bean.mSerialPort);

            if(bean.mEstimateType!=null) {
                holder.setText(R.id.list_sensor_type, "监测类型：" + bean.mEstimateType.name);
            }else{
                holder.setText(R.id.list_sensor_type, "监测类型：");
            }
            holder.setText(R.id.list_sensor_type_sub, "监测值：" + bean.mSubTypeText);
        }
    }
}
