package com.bestdealfinance.bdfpartner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.DashboardAdapter;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

public class DashBoard extends AppCompatActivity {

    String val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        val = i.getStringExtra("val");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.txt_dashboard)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.txt_referral)));
        //tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.txt_payouts)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final DashboardAdapter adapter = new DashboardAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(val.equalsIgnoreCase(getString(R.string.txt_dashboard))) {
            viewPager.setCurrentItem(0);
        } else if(val.equalsIgnoreCase(getString(R.string.txt_referral))){
            viewPager.setCurrentItem(1);
        }
        else if(val.equalsIgnoreCase(getString(R.string.txt_payouts))){
            viewPager.setCurrentItem(2);
        }


        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Dashboard Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }



}
