package com.minorfish.dtuapp.module.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.minorfish.dtuapp.R;
import com.tangjd.common.utils.DisplayUtils;

import org.angmarch.views.NiceSpinner;

import java.util.List;


public class ItemViewSpinner<T> extends ItemViewBase {
    private Context mContext;
    private String mTitle;
    private boolean mShowStar;


    public ItemViewSpinner(Context context) {
        this(context, null);
    }

    public ItemViewSpinner(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemViewSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemViewBase);
        mTitle = typedArray.getString(R.styleable.ItemViewBase_title);
        mShowStar = typedArray.getBoolean(R.styleable.ItemViewBase_showDrawable, false);
        init();
        typedArray.recycle();
    }

    public ImageView ivStar;
    public TextView tvTitle;
    public NiceSpinner spContent;

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

        spContent = new NiceSpinner(mContext);
        spContent.setBackground(getResources().getDrawable(R.drawable.bg_et));
        spContent.setLayoutParams(new LayoutParams(DisplayUtils.Dp2px(mContext, 600), LayoutParams.WRAP_CONTENT));
        spContent.setGravity(Gravity.CENTER);
        spContent.setTextColor(Color.WHITE);
        spContent.setArrowDrawable(org.angmarch.views.R.drawable.arrow);

        addView(ivStar);
        addView(tvTitle);
        addView(spContent);
    }

    public void setData(List<T> data) {
        spContent.attachDataSource(data);
    }
}
