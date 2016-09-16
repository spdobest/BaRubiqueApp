package com.bestdealfinance.bdfpartner.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bumptech.glide.Glide;

public class ApplicationCongrats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_app_congrats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.bg_congrates1));
        }
        TextView msg_for_cong= (TextView) findViewById(R.id.msg_for_congratulation);

        Bundle referbundle=getIntent().getExtras();
        String type=referbundle.getString("type", "");
        TextView loan_name= (TextView) findViewById(R.id.loan_congo_txt);
        TextView applicant_name= (TextView) findViewById(R.id.loan_congo_name);
        TextView payout_loan= (TextView) findViewById(R.id.payout_loan);
        TextView tracking_id= (TextView) findViewById(R.id.loan_congo_id);
        tracking_id.setText(Html.fromHtml("Your Tracking ID: "+referbundle.getString("lead_id","")));
        TextView congo_payout= (TextView) findViewById(R.id.congo_payout);
        loan_name.setText(referbundle.getString("product_name", ""));
        String amount=referbundle.getString("amount","");
        applicant_name.setText(Html.fromHtml("Applicant Name: "+referbundle.getString("name","")));
        TextView congo_amount= (TextView) findViewById(R.id.loan_congo_amount);
        if (type.equals("11")){
            msg_for_cong.setText("On successful issuance of card you will earn");
            congo_amount.setVisibility(View.GONE);
            payout_loan.setVisibility(View.GONE);
        }
        else {
            congo_amount.setText("Amount: "+ Util.parseRs(referbundle.getString("amount","")));
        }
        ImageView congo_img= (ImageView) findViewById(R.id.loan_congo_img);
        Glide.with(this)
                .load(referbundle.getString("bank_logo",""))
                .fallback(R.drawable.cc)
                .into(congo_img);

        ProgressBar pay_bar= (ProgressBar) findViewById(R.id.payout_wait);
        if (type.equals("11")){
            Util.GetFullPayout pay = new Util.GetFullPayout(pay_bar,type, amount,  getApplicationContext(), congo_payout, "Upto Rs. ", "*","payout_amount", referbundle.getString("product_id","0"));
            pay.executeOnExecutor(Util.threadPool);

        }
        else {
            Util.GetFullPayout pay = new Util.GetFullPayout(pay_bar,type, amount,  getApplicationContext(), congo_payout, "Upto Rs. ", "*","payout_amount", referbundle.getString("product_id","0"));
            pay.executeOnExecutor(Util.threadPool);

        }
        ImageView back= (ImageView) findViewById(R.id.img_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button another= (Button) findViewById(R.id.just_refer);
        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ApplicationCongrats.this, ReferralActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        String abc=referbundle.getString("message",getResources().getString(R.string.final_sorry));
        TextView appl= (TextView) findViewById(R.id.application_message);
        if (abc.equals("")) {
            appl.setText(Html.fromHtml(getResources().getString(R.string.final_sorry) + referbundle.getString("product_name", referbundle.getString("tname", ""))));
        }
        else {
            appl.setText(Html.fromHtml(abc));
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
