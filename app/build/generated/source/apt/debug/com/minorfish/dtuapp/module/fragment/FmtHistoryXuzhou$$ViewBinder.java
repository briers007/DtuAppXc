// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FmtHistoryXuzhou$$ViewBinder<T extends com.minorfish.dtuapp.module.fragment.FmtHistoryXuzhou> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230952, "field 'rvHistory'");
    target.rvHistory = finder.castView(view, 2131230952, "field 'rvHistory'");
    view = finder.findRequiredView(source, 2131230998, "field 'startLayout'");
    target.startLayout = finder.castView(view, 2131230998, "field 'startLayout'");
    view = finder.findRequiredView(source, 2131230810, "field 'endLayout'");
    target.endLayout = finder.castView(view, 2131230810, "field 'endLayout'");
    view = finder.findRequiredView(source, 2131230826, "field 'tvDateStart'");
    target.tvDateStart = finder.castView(view, 2131230826, "field 'tvDateStart'");
    view = finder.findRequiredView(source, 2131230825, "field 'tvDateEnd'");
    target.tvDateEnd = finder.castView(view, 2131230825, "field 'tvDateEnd'");
    view = finder.findRequiredView(source, 2131230831, "field 'historyTotal'");
    target.historyTotal = finder.castView(view, 2131230831, "field 'historyTotal'");
    view = finder.findRequiredView(source, 2131230830, "field 'preBtn'");
    target.preBtn = finder.castView(view, 2131230830, "field 'preBtn'");
    view = finder.findRequiredView(source, 2131230829, "field 'nextBtn'");
    target.nextBtn = finder.castView(view, 2131230829, "field 'nextBtn'");
    view = finder.findRequiredView(source, 2131230827, "field 'currentLayout'");
    target.currentLayout = finder.castView(view, 2131230827, "field 'currentLayout'");
    view = finder.findRequiredView(source, 2131230828, "field 'currentText'");
    target.currentText = finder.castView(view, 2131230828, "field 'currentText'");
  }

  @Override public void unbind(T target) {
    target.rvHistory = null;
    target.startLayout = null;
    target.endLayout = null;
    target.tvDateStart = null;
    target.tvDateEnd = null;
    target.historyTotal = null;
    target.preBtn = null;
    target.nextBtn = null;
    target.currentLayout = null;
    target.currentText = null;
  }
}
