// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ActFrame2guohong$$ViewBinder<T extends com.minorfish.dtuapp.module.ActFrame2guohong> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230823, "field 'fragmentContainer'");
    target.fragmentContainer = finder.castView(view, 2131230823, "field 'fragmentContainer'");
    view = finder.findRequiredView(source, 2131230943, "field 'rbRealData'");
    target.rbRealData = finder.castView(view, 2131230943, "field 'rbRealData'");
    view = finder.findRequiredView(source, 2131230937, "field 'rbHistory'");
    target.rbHistory = finder.castView(view, 2131230937, "field 'rbHistory'");
    view = finder.findRequiredView(source, 2131230936, "field 'rbDtuSetting'");
    target.rbDtuSetting = finder.castView(view, 2131230936, "field 'rbDtuSetting'");
    view = finder.findRequiredView(source, 2131230944, "field 'rbSensorSetting'");
    target.rbSensorSetting = finder.castView(view, 2131230944, "field 'rbSensorSetting'");
    view = finder.findRequiredView(source, 2131230938, "field 'rbLog'");
    target.rbLog = finder.castView(view, 2131230938, "field 'rbLog'");
    view = finder.findRequiredView(source, 2131230939, "field 'rbModifyPwd'");
    target.rbModifyPwd = finder.castView(view, 2131230939, "field 'rbModifyPwd'");
    view = finder.findRequiredView(source, 2131230935, "field 'radioGroup'");
    target.radioGroup = finder.castView(view, 2131230935, "field 'radioGroup'");
  }

  @Override public void unbind(T target) {
    target.fragmentContainer = null;
    target.rbRealData = null;
    target.rbHistory = null;
    target.rbDtuSetting = null;
    target.rbSensorSetting = null;
    target.rbLog = null;
    target.rbModifyPwd = null;
    target.radioGroup = null;
  }
}
