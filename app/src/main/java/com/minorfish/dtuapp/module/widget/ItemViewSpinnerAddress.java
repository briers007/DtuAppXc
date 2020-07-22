package com.minorfish.dtuapp.module.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minorfish.dtuapp.R;
import com.tangjd.common.utils.DisplayUtils;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.List;


public class ItemViewSpinnerAddress<T> extends ItemViewBase {
    private Context mContext;
    private String mTitle;
    private boolean mShowStar;


    public ItemViewSpinnerAddress(Context context) {
        this(context, null);
    }

    public ItemViewSpinnerAddress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemViewSpinnerAddress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemViewBase);
        mTitle = typedArray.getString(R.styleable.ItemViewBase_title);
        mShowStar = typedArray.getBoolean(R.styleable.ItemViewBase_showDrawable, false);
        init();
        typedArray.recycle();
    }

    public ImageView ivStar;
    public TextView tvTitle;
    public NiceSpinner sp1, sp2, sp3;

    private void init() {
        mContext = getContext();

        setGravity(Gravity.CENTER_VERTICAL);

        ivStar = new ImageView(mContext);
        ivStar.setImageResource(R.drawable.ic_star);
        ivStar.setVisibility(mShowStar ? VISIBLE : INVISIBLE);

        tvTitle = new TextView(mContext);
        tvTitle.setTextColor(getResources().getColor(R.color.light_black_ab));
        LayoutParams tvParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvParams.setMargins(10, 0, 30, 0);
        tvTitle.setLayoutParams(tvParams);
        tvTitle.setText(mTitle);

        LinearLayout llSpinner = new LinearLayout(mContext);
        llSpinner.setLayoutParams(new LayoutParams(DisplayUtils.Dp2px(mContext, 600), LayoutParams.WRAP_CONTENT));
        llSpinner.setOrientation(LinearLayout.HORIZONTAL);

        sp1 = new NiceSpinner(mContext);
        sp1.setBackground(getResources().getDrawable(R.drawable.bg_et));
        sp1.setLayoutParams(new LayoutParams(DisplayUtils.Dp2px(mContext, 200), LayoutParams.WRAP_CONTENT));
        sp1.setGravity(Gravity.CENTER);
        sp1.setTextColor(Color.WHITE);
        sp1.setArrowDrawable(org.angmarch.views.R.drawable.arrow);

        sp2 = new NiceSpinner(mContext);
        sp2.setBackground(getResources().getDrawable(R.drawable.bg_et));
        sp2.setLayoutParams(new LayoutParams(DisplayUtils.Dp2px(mContext, 200), LayoutParams.WRAP_CONTENT));
        sp2.setGravity(Gravity.CENTER);
        sp2.setTextColor(Color.WHITE);
        sp2.setArrowDrawable(org.angmarch.views.R.drawable.arrow);

        sp3 = new NiceSpinner(mContext);
        sp3.setBackground(getResources().getDrawable(R.drawable.bg_et));
        sp3.setLayoutParams(new LayoutParams(DisplayUtils.Dp2px(mContext, 200), LayoutParams.WRAP_CONTENT));
        sp3.setGravity(Gravity.CENTER);
        sp3.setTextColor(Color.WHITE);
        sp3.setArrowDrawable(org.angmarch.views.R.drawable.arrow);

        llSpinner.addView(sp1);
        llSpinner.addView(sp2);
        llSpinner.addView(sp3);

        addView(ivStar);
        addView(tvTitle);
        addView(llSpinner);
    }

    public void setData1(List<T> data) {
    }

    public void setData2(List<T> data) {
    }

    public void setData3(List<T> data) {
    }
}
