// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FmtSensorSettingNew$$ViewBinder<T extends com.minorfish.dtuapp.module.fragment.FmtSensorSettingNew> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230959, "field 'rvSensorList'");
    target.rvSensorList = finder.castView(view, 2131230959, "field 'rvSensorList'");
  }

  @Override public void unbind(T target) {
    target.rvSensorList = null;
  }
}
