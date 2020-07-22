package com.minorfish.dtuapp.module.fragment;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.minorfish.dtuapp.R;
import com.minorfish.dtuapp.abs.Api;
import com.minorfish.dtuapp.abs.Resp;
import com.minorfish.dtuapp.module.ActFrame;
import com.minorfish.dtuapp.module.ActFrame2;
import com.minorfish.dtuapp.module.widget.ItemViewEt;
import com.tangjd.common.abs.BaseFragment;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.retrofit.string.OnStringRespListener;

import org.json.JSONObject;

import butterknife.Bind;

public class FmtModifyPwd extends BaseFragment {

    private ActFrame2 mActivity;
    //private ActFrameSingle mActivity;

    @Bind(R.id.btn_complete)
    Button mFinishBtn;
    @Bind(R.id.item_new_pwd)
    ItemViewEt newPasswordText;
    @Bind(R.id.item_check_pwd)
    ItemViewEt confirmPasswordText;
    @Bind(R.id.item_old_pwd)
    ItemViewEt editOldPwd;
    private String newPassword;
    private String oldPassword;

    @Override
    protected View setContentView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fmt_modify_pwd_layout, container, false);
    }

    @Override
    protected void initView() {
        mActivity = (ActFrame2) getActivity();
        //mActivity = (ActFrameSingle) getActivity();

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassword = editOldPwd.etContent.getText().toString();
                if (oldPassword.length() == 0) { // 没有输入
                    editOldPwd.requestFocus();
                    editOldPwd.etContent.setError(getString(R.string.register_hint_password_old));
                    return;
                }
                newPassword = newPasswordText.etContent.getText().toString();
                if (newPassword.length() == 0) { // 没有输入
                    newPasswordText.requestFocus();
                    newPasswordText.etContent.setError(getString(R.string.register_hint_password_new));
                    return;
                }
                if (newPassword.length() < 6) { // 没有输入
                    newPasswordText.requestFocus();
                    newPasswordText.etContent.setError(getString(R.string.register_password_format));
                    return;
                }
                String passwordAgain = confirmPasswordText.etContent.getText().toString();
                if (passwordAgain.length() == 0) {
                    confirmPasswordText.requestFocus();
                    confirmPasswordText.etContent.setError(getString(R.string.register_hint_password_new_again));
                    return;
                }
                if (passwordAgain.length() < 6) {
                    confirmPasswordText.requestFocus();
                    confirmPasswordText.etContent.setError(getString(R.string.register_password_format));
                    return;
                }
                if (!passwordAgain.equals(newPassword)) {
                    confirmPasswordText.requestFocus();
                    confirmPasswordText.etContent.setError(getString(R.string.wrong_confirm_password));
                    return;
                }
                commit();
            }
        });
    }

    @Override
    protected void getDataJustOnce() {
    }

    public void commit() {
        mActivity.showProgressDialog();
        Api.modifyPwd(newPassword, oldPassword, new OnStringRespListener() {
            @Override
            public void onResponse(String data) {
                Resp resp = Resp.objectFromData(data);
                if (resp.isSuccess()) {
                    mActivity.showToast("修改成功");
                } else {
                    onError(resp.message);
                }
            }

            @Override
            public void onError(String error) {
                mActivity.showTipDialog(error);
            }

            @Override
            public void onFinish(boolean withoutException) {
                mActivity.dismissProgressDialog();
            }
        });
    }
}
