package com.minorfish.dtuapp.module.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

public class ItemsDialogFragment extends DialogFragment {

    private String title;

    private String[] items;

    private DialogInterface.OnClickListener onClickListener;

    public void show(String title, String[] items, DialogInterface.OnClickListener onClickListener,
                     FragmentManager fragmentManager) {
        this.title = title;
        this.items = items;
        this.onClickListener = onClickListener;
        show(fragmentManager, "ItemsDialogFragment");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setItems(items, onClickListener);
        return builder.create();
    }

}
