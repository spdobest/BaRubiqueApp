package com.bestdealfinance.bdfpartner.activity;

import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Stack;

import io.fabric.sdk.android.Fabric;

public class AboutUs extends AppCompatActivity {

    ImageView img_manav, img_sanadeep, img_anadi, img_manish, img_salil, img_sheetal, img_sumeru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("About Us");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About Us");
        initialization();

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.addTab(tabLayout.newTab().setText("About Us"));
//        tabLayout.addTab(tabLayout.newTab().setText("Philosophy"));
//        tabLayout.addTab(tabLayout.newTab().setText("The Team"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        final AboutUsAdapter adapter = new AboutUsAdapter
//                (getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

    private void initialization() {
       
        img_manav = (ImageView)findViewById(R.id.img_manav);
        img_sanadeep = (ImageView)findViewById(R.id.img_sanadeep);
//        img_anadi = (ImageView)findViewById(R.id.img_anadi);
        img_manish = (ImageView)findViewById(R.id.img_manish);
        img_salil = (ImageView)findViewById(R.id.img_salil);
        img_sheetal = (ImageView)findViewById(R.id.img_sheetal);
        img_sumeru = (ImageView)findViewById(R.id.img_sumeru);
        Glide.with(this).load("https://images001.rubique.com/management-images/manav.png").into(img_manav);
        Glide.with(this).load("https://images001.rubique.com/management-images/sandeep.png").into(img_sanadeep);
//        Glide.with(this).load("https://images001.rubique.com/management-images/anadi.png").into(img_anadi);
        Glide.with(this).load("https://images001.rubique.com/management-images/manish.png").into(img_manish);
        Glide.with(this).load("https://images001.rubique.com/management-images/salil.png").into(img_salil);
        Glide.with(this).load("https://images001.rubique.com/management-images/sheetal.png").into(img_sheetal);
        Glide.with(this).load("https://images001.rubique.com/management-images/sumeru.png").into(img_sumeru);
    }

}
