package com.bestdealfinance.bdfpartner.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by siba.prasad on 20-12-2016.
 */

public abstract class BaseFragment extends Fragment {
    private ProgressDialog progressDialog;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public abstract void initViews(View rootView);
    public abstract void setClickListener();

    public void showProgressDialog(String message){
        if(progressDialog!=null && !progressDialog.isShowing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
        else{
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(message);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }
    public void hideProgresssDialog(){
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

}
