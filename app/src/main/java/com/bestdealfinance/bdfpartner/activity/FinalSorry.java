package com.bestdealfinance.bdfpartner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

public class FinalSorry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_sorry);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.White));
        toolbar.setTitle("Best Offers");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CardView loan_head= (CardView) findViewById(R.id.loan_head);
        CardView cc_head= (CardView) findViewById(R.id.cc_head);
        Bundle bundle=getIntent().getExtras();
        TextView take_off= (TextView) findViewById(R.id.take_off);
        take_off.setText(Html.fromHtml(getResources().getString(R.string.take_offline)));
        String abc=bundle.getString("message",getResources().getString(R.string.final_sorry));
        TextView appl= (TextView) findViewById(R.id.application_name);
        if (abc.equals("")) {
            appl.setText(Html.fromHtml(getResources().getString(R.string.final_sorry) + bundle.getString("product_name", bundle.getString("tname", ""))));
        }
        else {
            appl.setText(Html.fromHtml(abc));
        }
        String product_type=bundle.getString("type");
        if (product_type.equals("11")){
            cc_head.setVisibility(View.VISIBLE);
            TextView lost_name= (TextView) findViewById(R.id.lost_cc_name);
            lost_name.setText(bundle.getString("product_name",""));
            TextView lost_cc_fees= (TextView) findViewById(R.id.lost_cc_fees);
            lost_cc_fees.setText(bundle.getString("fees",""));
        }
        else {
            loan_head.setVisibility(View.VISIBLE);
            TextView lost_amount= (TextView) findViewById(R.id.lost_fees);
            String lamount=bundle.getString("amount","0");
            lamount= Util.parseRs(lamount);
            lost_amount.setText(lamount);
            TextView lost_emi= (TextView) findViewById(R.id.lost_emi);
            lost_emi.setText(bundle.getString("emi","-"));
            TextView lost_tenure= (TextView) findViewById(R.id.lost_tenure);
            lost_tenure.setText(bundle.getString("tenure", "-"));
        }
        TextView message= (TextView) findViewById(R.id.application_name);


        ImageView view= (ImageView) findViewById(R.id.imageView2);
        Glide.with(this).
                load(R.drawable.more_info)
                .error(R.drawable.ic_arrow_left)
                .into(view);



        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Sorry Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent=new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
