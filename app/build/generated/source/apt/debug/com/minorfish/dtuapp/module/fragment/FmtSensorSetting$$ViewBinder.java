// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FmtSensorSetting$$ViewBinder<T extends com.minorfish.dtuapp.module.fragment.FmtSensorSetting> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230867, "field 'itemSensorVendor'");
    target.itemSensorVendor = finder.castView(view, 2131230867, "field 'itemSensorVendor'");
    view = finder.findRequiredView(source, 2131230862, "field 'itemSensorModel'");
    target.itemSensorModel = finder.castView(view, 2131230862, "field 'itemSensorModel'");
    view = finder.findRequiredView(source, 2131230863, "field 'itemSensorName'");
    target.itemSensorName = finder.castView(view, 2131230863, "field 'itemSensorName'");
    view = finder.findRequiredView(source, 2131230861, "field 'itemSensorFreq'");
    target.itemSensorFreq = finder.castView(view, 2131230861, "field 'itemSensorFreq'");
    view = finder.findRequiredView(source, 2131230864, "field 'itemSensorPort'");
    target.itemSensorPort = finder.castView(view, 2131230864, "field 'itemSensorPort'");
    view = finder.findRequiredView(source, 2131230866, "field 'itemSensorType'");
    target.itemSensorType = finder.castView(view, 2131230866, "field 'itemSensorType'");
    view = finder.findRequiredView(source, 2131230865, "field 'itemSensorSubType'");
    target.itemSensorSubType = finder.castView(view, 2131230865, "field 'itemSensorSubType'");
    view = finder.findRequiredView(source, 2131230771, "field 'btnComplete'");
    target.btnComplete = finder.castView(view, 2131230771, "field 'btnComplete'");
    view = finder.findRequiredView(source, 2131230860, "field 'itemSensorAddress'");
    target.itemSensorAddress = finder.castView(view, 2131230860, "field 'itemSensorAddress'");
  }

  @Override public void unbind(T target) {
    target.itemSensorVendor = null;
    target.itemSensorModel = null;
    target.itemSensorName = null;
    target.itemSensorFreq = null;
    target.itemSensorPort = null;
    target.itemSensorType = null;
    target.itemSensorSubType = null;
    target.btnComplete = null;
    target.itemSensorAddress = null;
  }
}
