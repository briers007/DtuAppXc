package com.minorfish.dtuapp.module.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.module.model.SensorSettingBean;

import java.util.List;

/**
 * 页码列表item
 * @author Chris
 *
 */
public class ListSensorAdapter extends BaseAdapter {

    private List<SensorSettingBean> mList;

    public void setList(List<SensorSettingBean> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder vh;
        if (arg1 == null) {
            arg1 = View.inflate(arg2.getContext(), R.layout.list_dialog_item,null);
            vh = new ViewHolder();
            vh.itemName = (TextView) arg1.findViewById(R.id.list_dialog_name);
            arg1.setTag(vh);
        } else {
            vh = (ViewHolder) arg1.getTag();
        }
        SensorSettingBean item = (SensorSettingBean)getItem(arg0);
        vh.itemName.setText(item.mSensorName);
        return arg1;
    }

    class ViewHolder {
        TextView itemName;
    }
}
