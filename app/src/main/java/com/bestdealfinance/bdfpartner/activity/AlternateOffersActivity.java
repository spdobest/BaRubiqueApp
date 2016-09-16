package com.bestdealfinance.bdfpartner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.fragment.CC_Offers;
import com.bestdealfinance.bdfpartner.fragment.CC_Offers_Alt;
import com.bestdealfinance.bdfpartner.fragment.LoanOffersAlt;
import com.bestdealfinance.bdfpartner.fragment.LoanOffers_fragment;

public class AlternateOffersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_alternate_offers);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.txt_refer_for_loan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        Bundle hisData=intent.getExtras();
        String type=hisData.getString("type");
        String data=hisData.getString("data");

        if (type.equals("11")){
            //TODO Show CC Offers Fragment
            CC_Offers_Alt fv = new CC_Offers_Alt();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            fv.setArguments(hisData);
            ft.add(R.id.fragment, fv);
            getSupportActionBar().setTitle(R.string.txt_apply_credit_card);
            ft.commit();
        }
        else {
            LoanOffersAlt fv = new LoanOffersAlt();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            hisData.putString("type", type);
            fv.setArguments(hisData);
            ft.add(R.id.fragment, fv);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
