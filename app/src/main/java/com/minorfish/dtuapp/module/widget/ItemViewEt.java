package com.minorfish.dtuapp.module.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minorfish.dtuapp.R;
import com.tangjd.common.utils.DisplayUtils;


public class ItemViewEt extends ItemViewBase {
    private Context mContext;
    private String mTitle;
    private boolean mShowStar;


    public ImageView ivStar;
    public TextView tvTitle;
    public EditText etContent;

    public ItemViewEt(Context context) {
        this(context, null);
    }

    public ItemViewEt(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemViewEt(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemViewBase);
        mTitle = typedArray.getString(R.styleable.ItemViewBase_title);
        mShowStar = typedArray.getBoolean(R.styleable.ItemViewBase_showDrawable, false);
        init();
        typedArray.recycle();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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

        etContent = new EditText(mContext);
        etContent.setBackground(getResources().getDrawable(R.drawable.bg_et));
        etContent.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtils.Dp2px(mContext, 600), ViewGroup.LayoutParams.MATCH_PARENT));
        etContent.setSingleLine(true);
        etContent.setGravity(Gravity.CENTER);
        etContent.setPadding(30, 0, 30, 0);
        etContent.setTextColor(Color.WHITE);

        addView(ivStar);
        addView(tvTitle);
        addView(etContent);
    }
}
