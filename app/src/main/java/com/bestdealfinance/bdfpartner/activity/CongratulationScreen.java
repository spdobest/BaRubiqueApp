package com.bestdealfinance.bdfpartner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import io.fabric.sdk.android.Fabric;

//Its a Confirmation Screen
public class CongratulationScreen extends AppCompatActivity implements View.OnClickListener{

    Button apply, confirm;
    Bundle bundle;
    TextView payout_text, congoName, congoEmail, congoPhone, congo_amount, congoType, earn_more, pay_info;
    String name, payout, email, number, amount;
    ImageView img_back, imgLoanType;
    SharedPreferences sharedpreferences;
    ImageView progressBar;
    private AnimationDrawable animation;
    FrameLayout waiting_layout;
    SharedPreferences pref;
    RelativeLayout payout_layout;
    TextView msgForCongratulation;
    ProgressBar pay_bar, pay_bar2;
    LinearLayout hide_unregistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_congratulation_screen);
        bundle=getIntent().getExtras();


        bundle.putString("source","BA_APP");


        initialization();



        if(bundle.getString("type").equals("11")){
            msgForCongratulation.setText(R.string.congratulation_helper_credit_card);
        } else {
            msgForCongratulation.setText(R.string.congratulation_helper_loan);
        }
        //pref = getSharedPreferences(Util.MY_PREFERENCES, Context.MODE_PRIVATE);


        if (bundle.getString("bank_logo")!=null){
            imgLoanType.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(this).load(bundle.getString("bank_logo"))
                    .error(R.drawable.ic_arrow_left)
                    .placeholder(R.drawable.ic_arrow_left)
                    .fitCenter()
                    .into(imgLoanType);
        }
        else {
            Glide.with(this).load(Util.getImagebyLoanType(bundle.getString("type")))
                    .error(R.drawable.ic_arrow_left)
                    .placeholder(R.drawable.ic_arrow_left)
                    .into(imgLoanType);
        }
//        imgLoanType.setImageResource(Util.getImagebyLoanType(bundle.getString("type")));

        congoName.setText(Html.fromHtml(getString(R.string.txt_name_of_your_lead)+" : <b>" + bundle.getString("name") + "</b>"));
        if (bundle.getString("product_name")!=null){
            congoType.setText(bundle.getString("product_name"));
        }
        else {
            congoType.setText(Html.fromHtml("<b>" + bundle.getString("tname") + "</b>"));
        }

        congoEmail.setText(Html.fromHtml(getString(R.string.txt_email_id) +"<b>" + bundle.getString("email") + "</b>"));

        congoPhone.setText(Html.fromHtml(getString(R.string.txt_phone_number)+"<b>" + bundle.getString("phone") + "</b>"));
        if(bundle.getString("type").equals("11")){
            congo_amount.setVisibility(View.GONE);
        } else {
            congo_amount.setText(Html.fromHtml(getString(R.string.txt_amount)+":<b>" + Util.parseRs(bundle.getString("amount", "")) + "</b>"));
        }

        //sharedpreferences = getSharedPreferences(Util.MY_PREFERENCES, Context.MODE_PRIVATE);

        if (Util.getProductTypeURL(bundle.getString("type")).equals("0")){
            hide_unregistered.setVisibility(View.GONE);
        }

        img_back.setOnClickListener(this);
        if ((bundle.getString("type","")).equals("11")){
            congo_amount.setVisibility(View.GONE);
            pay_info.setVisibility(View.GONE);
            msgForCongratulation.setText(getString(R.string.congratulation_helper_card));

            Util.GetPayout pay = new Util.GetPayout(pay_bar,bundle.getString("type", ""), bundle.getString("amount", ""), "r", getApplicationContext(), payout_text, "Upto Rs. ", "","payout_amount");
            pay.executeOnExecutor(Util.threadPool);
            //TODO For Application , unhide this
            Util.GetPayout pay12 = new Util.GetPayout(pay_bar2,bundle.getString("type", ""), bundle.getString("amount", ""), "f", getApplicationContext(), earn_more, "Earn additional Rs. ", " on this referral by making an application","payout_amount");
            pay12.executeOnExecutor(Util.threadPool);
        }
        else {
            Util.GetPayout pay = new Util.GetPayout(pay_bar,bundle.getString("type", ""), bundle.getString("amount", ""), "r", getApplicationContext(), payout_text, "Upto Rs. ", "","payout_amount");
            pay.executeOnExecutor(Util.threadPool);
            //TODO For Application , unhide this
            Util.GetPayout pay12 = new Util.GetPayout(pay_bar2,bundle.getString("type", ""), bundle.getString("amount", ""), "f", getApplicationContext(), earn_more, "Earn additional Rs. ", " on this referral by making an application","payout_amount");
            pay12.executeOnExecutor(Util.threadPool);
        }


        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Referral Review Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }

    private void initialization() {
        payout_layout= (RelativeLayout) findViewById(R.id.payout_layout);
        pay_bar= (ProgressBar) findViewById(R.id.payout_wait);
        pay_bar2= (ProgressBar) findViewById(R.id.paybarwait2);
        msgForCongratulation = (TextView)findViewById(R.id.msg_for_congratulation);
        pay_info= (TextView) findViewById(R.id.payout_loan);

        waiting_layout = (FrameLayout)findViewById(R.id.waiting_layout);
        progressBar = (ImageView)findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        hide_unregistered= (LinearLayout) findViewById(R.id.hide_unregistered);
        img_back= (ImageView) findViewById(R.id.img_back);
        imgLoanType = (ImageView) findViewById(R.id.loan_congo_img);
        payout_text= (TextView) findViewById(R.id.congo_payout);
        congoName = (TextView) findViewById(R.id.loan_congo_name);
        congoEmail = (TextView) findViewById(R.id.loan_congo_email);
        congoPhone = (TextView) findViewById(R.id.loan_congo_number);
        congo_amount= (TextView) findViewById(R.id.loan_congo_amount);
        congoType = (TextView) findViewById(R.id.loan_congo_txt);

        //TODO For Application , unhide this
        earn_more= (TextView) findViewById(R.id.earn_more_txt);
        apply= (Button) findViewById(R.id.make_app);
        confirm= (Button) findViewById(R.id.just_refer);
        apply.setOnClickListener(this);

        confirm.setOnClickListener(this);
        payout_layout.bringToFront();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //TODO For Application , unhide this
            case R.id.make_app:
                Intent intent=new Intent(CongratulationScreen.this, OnBoardingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.img_back:
                finish();
                break;
            case R.id.just_refer:
                if (Util.isRegistered(this).equals("")){
                    Intent myIntent =  new Intent(CongratulationScreen.this,LoginRegSinglePage.class);
                    myIntent.putExtras(bundle);
                    startActivityForResult(myIntent, 2025);
                }
                else {
                    HttpAsyncTask task=new HttpAsyncTask();
                    task.executeOnExecutor(Util.threadPool);
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 2025) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                HttpAsyncTask task=new HttpAsyncTask();
                task.executeOnExecutor(Util.threadPool);
            }
        }
    }
    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waiting_layout.setVisibility(View.VISIBLE);
            animation.start();
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.REFER_A_LEAD);

                String json = "";
                JSONObject jsonObject = new JSONObject();
//                jsonObject.accumulate("salutation", "Mr");
                jsonObject.accumulate("name", bundle.getString("name"));
                jsonObject.accumulate("email", bundle.getString("email"));
                jsonObject.accumulate("phone", bundle.getString("phone"));
                jsonObject.accumulate("city", bundle.getString("city"));
                if (!bundle.getString("type","").equals("11")){
                    jsonObject.accumulate("amount", bundle.getString("amount"));
                }
                jsonObject.accumulate("product_type_sought", bundle.getString("type",""));
                jsonObject.accumulate("product_id", bundle.getString("product_id",""));
                jsonObject.accumulate("note", bundle.getString("product_id",""));
//                jsonObject.accumulate("submitter_phone", bundle.getString("semail",""));
                jsonObject.accumulate("self_refferal", bundle.getString("self",""));
                jsonObject.accumulate("source", bundle.getString("source",""));
//                jsonObject.accumulate("note", bundle.getString("note",""));
                Logs.LogD("REquest",jsonObject.toString());
                json = new String(jsonObject.toString().getBytes("ISO-8859-1"), "UTF-8");

                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + pref.getString(Util.utoken, ""));

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Logs.LogD("Result",result);
            waiting_layout.setVisibility(View.GONE);
            animation.stop();
            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (msg.equalsIgnoreCase("Success")) {
                        JSONObject body=output1.optJSONObject("body");
                        JSONObject info=body.optJSONObject("lead_info");
                        String id=info.optString("id");
                        try {
//                            APIUtils.SendSMS sms= new APIUtils.SendSMS("2",id,bundle.getString("name"),CongratulationScreen.this, bundle.getString("phone"),bundle.getString("tname"));
//                            sms.executeOnExecutor(Util.threadPool);
                        }
                        catch (Exception e){
                            //DO Nothing Failed to Send the SMS
                        }


                        Intent intent= new Intent(CongratulationScreen.this, FinalCongrats.class);
                        bundle.putString("id",id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        if(msg.equals("Failed")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(CongratulationScreen.this);
                            builder.setTitle("Message");
                            builder.setCancelable(false);
                            builder.setMessage(output1.getString("body"));
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    }
                }
            } catch (JSONException e) {

            }

        }
    }
}