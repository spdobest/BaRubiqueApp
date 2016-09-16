package com.bestdealfinance.bdfpartner.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.fragment.LoginFragment;
import com.bestdealfinance.bdfpartner.fragment.RegisterFragment;

public class LoginRegSinglePage extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener ,RegisterFragment.OnFragmentInteractionListener {
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_login_reg_single_page);
        bundle=getIntent().getExtras();
        Logs.LogD("Bundle",bundle.toString());
        Logs.LogD("Content",bundle.getString("semail"));
        LoginFragment fragment1 = new LoginFragment();
        fragment1.setArguments(bundle);
        android.support.v4.app.FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
        manager.replace(R.id.fragment, fragment1).commit();
    }

    @Override
    public void onFragmentInteraction(boolean m) {
        Logs.LogD("REGLOG", "Interaction Received");
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        //TODO Click on the Referral Button
    }

    public void replaceFragment_NOHISTORY(Fragment fragment) {
        fragment.setArguments(bundle);
        FragmentManager frgManager = getSupportFragmentManager();
        frgManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragment, fragment, "")
                .commit();
    }
}
