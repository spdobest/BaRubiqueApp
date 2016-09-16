package com.bestdealfinance.bdfpartner.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.TestFragmentAdapter;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import io.fabric.sdk.android.Fabric;

public class KnowMore extends FragmentActivity {

    TestFragmentAdapter mAdapter;
    public static ViewPager mPager;
    PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_know_more);

        mPager = (ViewPager)findViewById(R.id.pager);
        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager.setAdapter(mAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator = indicator;
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;
//        indicator.setBackgroundColor(0xFFCCCCCC);
        indicator.setRadius(6 * density);
        indicator.setStrokeColor(getResources().getColor(R.color.colorPrimary));
        indicator.setFillColor(getResources().getColor(R.color.blue));
        indicator.setPageColor(getResources().getColor(R.color.white));
        indicator.setStrokeWidth(1 * density);

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Know More Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }

}

