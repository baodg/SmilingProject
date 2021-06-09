package com.example.firebaseapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;


public class LoadingDialog {
    private AlertDialog dialog;

    public LoadingDialog(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
    }

    public void startLoading() {
        dialog.show();
    }

    public void hideLoading() {
        dialog.dismiss();
    }
}
