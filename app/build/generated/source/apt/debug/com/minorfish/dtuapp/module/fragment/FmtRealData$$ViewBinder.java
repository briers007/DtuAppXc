// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FmtRealData$$ViewBinder<T extends com.minorfish.dtuapp.module.fragment.FmtRealData> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230957, "field 'rvRealData'");
    target.rvRealData = finder.castView(view, 2131230957, "field 'rvRealData'");
  }

  @Override public void unbind(T target) {
    target.rvRealData = null;
  }
}
