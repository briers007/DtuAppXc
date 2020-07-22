// Generated code from Butter Knife. Do not modify!
package com.minorfish.dtuapp.module.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FmtDTUSetting$$ViewBinder<T extends com.minorfish.dtuapp.module.fragment.FmtDTUSetting> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230857, "field 'itemLocationName'");
    target.itemLocationName = finder.castView(view, 2131230857, "field 'itemLocationName'");
    view = finder.findRequiredView(source, 2131230856, "field 'itemDtuVendor'");
    target.itemDtuVendor = finder.castView(view, 2131230856, "field 'itemDtuVendor'");
    view = finder.findRequiredView(source, 2131230853, "field 'itemDtuModel'");
    target.itemDtuModel = finder.castView(view, 2131230853, "field 'itemDtuModel'");
    view = finder.findRequiredView(source, 2131230854, "field 'itemDtuName'");
    target.itemDtuName = finder.castView(view, 2131230854, "field 'itemDtuName'");
    view = finder.findRequiredView(source, 2131230849, "field 'itemDtuAddress'");
    target.itemDtuAddress = finder.castView(view, 2131230849, "field 'itemDtuAddress'");
    view = finder.findRequiredView(source, 2131230855, "field 'itemDtuOrgName'");
    target.itemDtuOrgName = finder.castView(view, 2131230855, "field 'itemDtuOrgName'");
    view = finder.findRequiredView(source, 2131230847, "field 'itemAddressDetail'");
    target.itemAddressDetail = finder.castView(view, 2131230847, "field 'itemAddressDetail'");
    view = finder.findRequiredView(source, 2131230851, "field 'itemDtuMac'");
    target.itemDtuMac = finder.castView(view, 2131230851, "field 'itemDtuMac'");
    view = finder.findRequiredView(source, 2131230852, "field 'itemDtuMac2'");
    target.itemDtuMac2 = finder.castView(view, 2131230852, "field 'itemDtuMac2'");
    view = finder.findRequiredView(source, 2131230850, "field 'itemDtuIp'");
    target.itemDtuIp = finder.castView(view, 2131230850, "field 'itemDtuIp'");
    view = finder.findRequiredView(source, 2131230771, "field 'btnComplete'");
    target.btnComplete = finder.castView(view, 2131230771, "field 'btnComplete'");
  }

  @Override public void unbind(T target) {
    target.itemLocationName = null;
    target.itemDtuVendor = null;
    target.itemDtuModel = null;
    target.itemDtuName = null;
    target.itemDtuAddress = null;
    target.itemDtuOrgName = null;
    target.itemAddressDetail = null;
    target.itemDtuMac = null;
    target.itemDtuMac2 = null;
    target.itemDtuIp = null;
    target.btnComplete = null;
  }
}
