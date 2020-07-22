// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FmtRealDataMulti$$ViewBinder<T extends com.minorfish.dtuapp.module.fragment.FmtRealDataMulti> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230958, "field 'rvRealData'");
    target.rvRealData = finder.castView(view, 2131230958, "field 'rvRealData'");
    view = finder.findRequiredView(source, 2131230946, "field 'radioGroup'");
    target.radioGroup = finder.castView(view, 2131230946, "field 'radioGroup'");
    view = finder.findRequiredView(source, 2131230940, "field 'radio1'");
    target.radio1 = finder.castView(view, 2131230940, "field 'radio1'");
    view = finder.findRequiredView(source, 2131230941, "field 'radio2'");
    target.radio2 = finder.castView(view, 2131230941, "field 'radio2'");
  }

  @Override public void unbind(T target) {
    target.rvRealData = null;
    target.radioGroup = null;
    target.radio1 = null;
    target.radio2 = null;
  }
}
