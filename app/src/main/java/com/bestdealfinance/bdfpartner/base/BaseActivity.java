package com.bestdealfinance.bdfpartner.base;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by siba.prasad on 19-12-2016.
 */

public abstract class BaseActivity extends AppCompatActivity {
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    // this is used to initialize child widgets in child class
    public abstract void initViews();
    public abstract void setClickListener();


    public void showProgressDialog(String message,String title,boolean isCancealable){

    }

    public void hideProgresDialog(){

    }
    public void showErrorDialog(String title,String message,String positiveButton,boolean isCancealable) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(message);

            builder.setPositiveButton(positiveButton,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // positive button logic
                            alertDialog.dismiss();
                        }
                    });

            alertDialog = builder.create();
            if (!isCancealable)
                alertDialog.setCancelable(false);
            // display dialog
            alertDialog.show();
        }
    }

}
