package com.bestdealfinance.bdfpartner.activity;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by vikas on 31/3/16.
 */

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.fragment.CC_Offers;
import com.bestdealfinance.bdfpartner.fragment.LoanOffers_fragment;

public class OffersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_offer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        Bundle hisData=intent.getExtras();
        String type=hisData.getString("type");
//        String data=hisData.getString("data");

        if (type.equals("11")){
            // TODO Show CC OFFers Fragment
            CC_Offers fv = new CC_Offers();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            fv.setArguments(hisData);
            ft.add(R.id.fragment, fv);
            getSupportActionBar().setTitle(R.string.txt_apply_credit_card);
            ft.commit();
        }
        else {
            LoanOffers_fragment fv = new LoanOffers_fragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            hisData.putString("type", type);
            fv.setArguments(hisData);
            ft.add(R.id.fragment, fv);
            getSupportActionBar().setTitle(R.string.txt_apply_for_loan);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
