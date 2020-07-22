// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ActSignIn$$ViewBinder<T extends com.minorfish.dtuapp.module.ActSignIn> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230902, "field 'etAccount'");
    target.etAccount = finder.castView(view, 2131230902, "field 'etAccount'");
    view = finder.findRequiredView(source, 2131230901, "field 'etPwd'");
    target.etPwd = finder.castView(view, 2131230901, "field 'etPwd'");
    view = finder.findRequiredView(source, 2131230900, "field 'btnSignIn'");
    target.btnSignIn = finder.castView(view, 2131230900, "field 'btnSignIn'");
  }

  @Override public void unbind(T target) {
    target.etAccount = null;
    target.etPwd = null;
    target.btnSignIn = null;
  }
}
