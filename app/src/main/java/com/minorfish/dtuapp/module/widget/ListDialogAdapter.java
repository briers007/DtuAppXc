package com.minorfish.dtuapp.module.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.minorfish.dtuapp.R;

import java.util.List;

/**
 * 页码列表item
 * @author Chris
 *
 */
public class ListDialogAdapter extends BaseAdapter {

    private int mList;

    public void setList(int mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList;
    }

    @Override
    public Object getItem(int position) {
        return position+1;
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
        int item = (int)getItem(arg0);
        vh.itemName.setText("第"+item+"页");
        return arg1;
    }

    class ViewHolder {
        TextView itemName;
    }
}
