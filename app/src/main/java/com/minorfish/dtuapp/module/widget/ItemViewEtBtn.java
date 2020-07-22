package com.minorfish.dtuapp.module.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.minorfish.dtuapp.R;
import com.tangjd.common.utils.DisplayUtils;


public class ItemViewEtBtn extends ItemViewBase {
    private Context mContext;
    private String mTitle;
    private boolean mShowStar;


    public ImageView ivStar;
    public TextView tvTitle;
    public EditText etContent;
    public Button mBtnAction;

    public ItemViewEtBtn(Context context) {
        this(context, null);
    }

    public ItemViewEtBtn(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemViewEtBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemViewBase);
        mTitle = typedArray.getString(R.styleable.ItemViewBase_title);
        mShowStar = typedArray.getBoolean(R.styleable.ItemViewBase_showDrawable, false);
        init();
        typedArray.recycle();
    }

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
        LayoutParams etParams = new LayoutParams(DisplayUtils.Dp2px(mContext, 500), ViewGroup.LayoutParams.WRAP_CONTENT);
        etContent.setLayoutParams(etParams);
        etContent.setGravity(Gravity.CENTER);
        etContent.setPadding(30, 0, 30, 0);
        etContent.setTextColor(Color.WHITE);

        mBtnAction = new Button(mContext);
        LayoutParams btnParams = new LayoutParams(DisplayUtils.Dp2px(mContext, 100), ViewGroup.LayoutParams.WRAP_CONTENT);
        mBtnAction.setLayoutParams(btnParams);
        mBtnAction.setText("自动获取");
        mBtnAction.setTextColor(getResources().getColor(R.color.light_black_ab));
        mBtnAction.setBackground(getResources().getDrawable(R.drawable.btn_black_selector));

        addView(ivStar);
        addView(tvTitle);
        addView(etContent);
        addView(mBtnAction);
    }
}
