package com.minorfish.dtuapp.module;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.abs.Api;
import com.minorfish.dtuapp.abs.App;
import com.minorfish.dtuapp.abs.Resp;
import com.minorfish.dtuapp.module.model.SignInBean;
import com.tangjd.common.abs.BaseActivity;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.retrofit.string.OnStringRespListener;
import com.tangjd.common.utils.DisplayUtils;
import com.tangjd.common.utils.Log;
import com.tangjd.common.utils.StringUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActSignIn extends BaseActivity {

    private static final String TAG = ActSignIn.class.getSimpleName();
    @Bind(R.id.login_username)
    EditText etAccount;
    @Bind(R.id.login_password)
    EditText etPwd;
    @Bind(R.id.login_btn)
    TextView btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.requestTranslucentNavigation(this);
        DisplayUtils.requestTranslucentStatus(this);
        setContentView(R.layout.act_login_layout);
        ButterKnife.bind(this);

        //edittext动作完成后触发
        etPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    processLogin();//登录
                }
                return false; //返回true，保留软键盘。false，隐藏软键盘
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin();//登录
            }
        });
    }

    private void processLogin() {
        if (StringUtil.isEmpty(etAccount)) {
            showLongSnackbar("请输入账号");
            return;
        }
        if (StringUtil.isEmpty(etPwd)) {
            showLongSnackbar("请输入密码");
            return;
        }
        showProgressDialog();
        Api.signIn(etAccount.getText().toString().trim(), etPwd.getText().toString().trim(),  new OnStringRespListener() {
            @Override
            public void onResponse(String data) {
                Resp resp = Resp.objectFromData(data);
                String str=new Gson().toJson(resp);
                Log.e(TAG,"processLogin-Api.signIn: "+str);
                if (resp.canParse()) {//canParse（）是对结果code的处理，相当于success Code判断
                    SignInBean bean = SignInBean.objectFromData(resp.content);
                    App.getApp().setSignInBean(bean);//存储signInBean 对象到sp
                    startActivity();
                } else {
                    onError(resp.message);
                }
            }

            @Override
            public void onError(String error) {
                showTipDialog(error);
            }

            @Override
            public void onFinish(boolean withoutException) {
                dismissProgressDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (App.getApp().getSignInBean() != null) {
            startActivity();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (App.getApp().getSignInBean() != null) {
            startActivity();
        }
    }

    private void startActivity() {
        startAct(ActFrame2.class);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
