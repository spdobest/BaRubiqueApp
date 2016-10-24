package com.bestdealfinance.bdfpartner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
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

//Congrats Page in case of referral
public class FinalCongrats extends AppCompatActivity {

    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_final_congrats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        img_back = (ImageView)findViewById(R.id.img_back);
        ImageView loanimage= (ImageView) findViewById(R.id.loan_congo_img);
        TextView loan_text= (TextView) findViewById(R.id.loan_congo_txt);
        TextView name= (TextView) findViewById(R.id.loan_congo_name);
        TextView amount= (TextView) findViewById(R.id.loan_congo_amount);
        TextView id= (TextView) findViewById(R.id.loan_congo_id);
        TextView payout= (TextView) findViewById(R.id.congo_payout);
        Intent intent =getIntent();
        Bundle bundle=intent.getExtras();


        id.setText(getString(R.string.txt_your_tracking_id)+": "+bundle.getString("id", ""));



        if(bundle.getString("type").equals("51")||bundle.getString("type").equals("52")||bundle.getString("type").equals("53"))
        {
            findViewById(R.id.payout_layout).setVisibility(View.GONE);

        }

        if (bundle.getString("product_name")!=null){
            loan_text.setText(bundle.getString("product_name"));
            if(bundle.getString("amount").equals(""))
            {
                amount.setVisibility(View.GONE);
            }
            else
            {
                amount.setText(getString(R.string.txt_loan_amount)+" : Rs"+ Util.formatRs(bundle.getString("amount","0")));
            }

        }
        else {
            loan_text.setText(Html.fromHtml("<b>" + bundle.getString("tname") + "</b>"));
            if(bundle.getString("amount").equals(""))
            {
                amount.setVisibility(View.GONE);
            }
            else
            {
                amount.setText(getString(R.string.txt_loan_amount)+" : Rs "+Util.formatRs(bundle.getString("amount","0")));
            }
        }

        if (bundle.getString("bank_logo")!=null){
            loanimage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(this).load(bundle.getString("bank_logo"))
                    .error(R.drawable.ic_arrow_left)
                    .placeholder(R.drawable.ic_arrow_left)
                    .fitCenter()
                    .into(loanimage);
        }
        else {
            Glide.with(this).load(Util.getImagebyLoanType(bundle.getString("type")))
                    .error(R.drawable.ic_arrow_left)
                    .placeholder(R.drawable.ic_arrow_left)
                    .into(loanimage);
        }
        loanimage.setImageResource(Util.getImagebyLoanType(bundle.getString("type")));

        name.setText(getString(R.string.txt_applicant_name)+": "+bundle.getString("name"));



        ProgressBar pay_bar= (ProgressBar) findViewById(R.id.payout_wait);
        String type = bundle.getString("type","22");

            Util.GetPayout pay = new Util.GetPayout(pay_bar,type,bundle.getString("amount","0"),"r",FinalCongrats.this,payout, "Upto Rs. ","*","payout_amount");
            pay.executeOnExecutor(Util.threadPool);



        Button another= (Button) findViewById(R.id.make_another);
        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(FinalCongrats.this, ReferralActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        /*TextView dashboard= (TextView) findViewById(R.id.dashboard);
        dashboard.setPaintFlags(dashboard.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dashboard.setText("MY DASHBOARD");
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FinalCongrats.this, DashBoard.class)
                        .putExtra("val", "dashboard")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });*/

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Final Congrats Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Logs.LogD("Cong","Back Pressed");
        Intent intent= new Intent(FinalCongrats.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }


}
