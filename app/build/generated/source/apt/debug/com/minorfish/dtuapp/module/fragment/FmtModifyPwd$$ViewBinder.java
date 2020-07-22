// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FmtModifyPwd$$ViewBinder<T extends com.minorfish.dtuapp.module.fragment.FmtModifyPwd> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230771, "field 'mFinishBtn'");
    target.mFinishBtn = finder.castView(view, 2131230771, "field 'mFinishBtn'");
    view = finder.findRequiredView(source, 2131230858, "field 'newPasswordText'");
    target.newPasswordText = finder.castView(view, 2131230858, "field 'newPasswordText'");
    view = finder.findRequiredView(source, 2131230848, "field 'confirmPasswordText'");
    target.confirmPasswordText = finder.castView(view, 2131230848, "field 'confirmPasswordText'");
    view = finder.findRequiredView(source, 2131230859, "field 'editOldPwd'");
    target.editOldPwd = finder.castView(view, 2131230859, "field 'editOldPwd'");
  }

  @Override public void unbind(T target) {
    target.mFinishBtn = null;
    target.newPasswordText = null;
    target.confirmPasswordText = null;
    target.editOldPwd = null;
  }
}
