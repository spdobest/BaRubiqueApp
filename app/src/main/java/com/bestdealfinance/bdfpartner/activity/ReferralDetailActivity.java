package com.bestdealfinance.bdfpartner.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.ReferralDetailAdapter;
import com.bestdealfinance.bdfpartner.application.Util;
import com.github.ivbaranov.mli.MaterialLetterIcon;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReferralDetailActivity extends AppCompatActivity {

    TextView detail_lead_id,referral_lead_state, detail_loan_value, detail_product_type, detail_name, detail_phone, detail_interest, detail_tenure, detail_fees, detail_emi, updated_date;
    TextView detail_payout1, detail_payout2, eligible_payout;
    MaterialLetterIcon profile_image;
    Intent i;
    CardView payout_detail, layout_loan_value;
    LinearLayout layout_other_field, layout_eligible_payout;
    RecyclerView recyclerview_lead_detail;
    FloatingActionButton fab;
    RelativeLayout mainLayout;
    LinearLayoutManager mLayoutManager;
    ReferralDetailAdapter mAdapter;
    ImageView detail_lead_back;
    String loan_amount;
    String tenure;
    String name;
    String loan_name;
    String type;
    String phone;
    String product_id;
    String product_name;
    String product_logo;
    boolean sendQual=false;


    LinearLayout waiting_layout;
    ImageView progressBar;
    Button incomplete;
    String lead_id;
    private AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_referral_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialization();

        i = getIntent();
        waiting_layout = (LinearLayout)findViewById(R.id.waiting_layout);
        progressBar = (ImageView)findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();

        new HttpAsyncTask().execute();

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });


        detail_lead_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerview_lead_detail.setHasFixedSize(true);
        recyclerview_lead_detail.setLayoutManager(mLayoutManager);

    }

    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainLayout.setVisibility(View.GONE);
            waiting_layout.setVisibility(View.VISIBLE);
            animation.start();
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.GET_REFERRAL_DETAIL);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("lead_id", i.getStringExtra("lead_id"));

                json = jsonObject.toString();
                Logs.LogD("URL",Util.GET_REFERRAL_DETAIL);
                Logs.LogD("Payload",json);
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + Util.isRegistered(getApplicationContext()));

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
            Logs.LogD("Response",result);
            waiting_layout.setVisibility(View.GONE);
            animation.stop();
            mainLayout.setVisibility(View.VISIBLE);


            String msg;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    msg = output1.getString("msg");
                    if (msg.equalsIgnoreCase("Success")) {
                        JSONObject jsonObject = new JSONObject(output1.getString("body"));
                        lead_id=jsonObject.optString("id");
                        detail_lead_id.setText(lead_id);
                        detail_name.setText(jsonObject.optString("name"));
                        detail_phone.setText(jsonObject.optString("phone"));
                        if (jsonObject.isNull("product_name")|| jsonObject.optString("product_name").equals("")){
                            detail_product_type.setText(jsonObject.optString("product_type_name"));
                        }
                        else {
                            detail_product_type.setText(jsonObject.optString("product_name"));
                        }
                        profile_image.setLetter(jsonObject.getString("name"));
                        profile_image.setInitials(true);
                        profile_image.setLettersNumber(1);
                        profile_image.setShapeColor(R.color.Referred);
                        referral_lead_state.setText(jsonObject.optString("lead_state_name"));
                        if (jsonObject.optString("lead_state").equals("INCOMPLETE")||jsonObject.optString("lead_state").equals("Incomplete")){
                            incomplete.setVisibility(View.VISIBLE);
                             loan_amount=jsonObject.optString("loan_amount_needed","");
                             tenure=jsonObject.optString("116","");
                             name=jsonObject.optString("name","");
                             loan_name=jsonObject.optString("product_name","");
                             type=jsonObject.optString("product_type_sought","");
                             phone=jsonObject.optString("phone","");
                             if (jsonObject.isNull("product_id")){
                                 sendQual=false;

                             }
                            else {
                                 sendQual=true;
                                 product_id=jsonObject.optString("product_id");
                                 product_logo=jsonObject.optString("product_image_url");
                                 product_name=jsonObject.optString("product_name");
                             }

                        }
                        if(jsonObject.getString("eligible_payout").equals("null")){
                            layout_eligible_payout.setVisibility(View.GONE);
                        } else {
                            eligible_payout.setText(jsonObject.getString("eligible_payout"));
                        }


                        if(jsonObject.getString("payout_detail1").equals("null")&&jsonObject.getString("payout_detail2").equals("null")){
                            payout_detail.setVisibility(View.GONE);
                        } else {
                            if (!jsonObject.getString("payout_detail1").equals("null")){
                                detail_payout1.setText(jsonObject.getString("payout_detail1"));
                            } else {
                                detail_payout1.setVisibility(View.GONE);
                            }
                            if (!jsonObject.getString("payout_detail2").equals("null")){
                                detail_payout2.setText(jsonObject.getString("payout_detail2"));
                            } else {
                                detail_payout2.setVisibility(View.GONE);
                            }
                        }

                        try {
                            SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = format.parse(jsonObject.getString("date_updated"));
                            SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");
                            updated_date.setText("Updated on: " + postFormater.format(date));
                        } catch (Exception e) {
                            updated_date.setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                        if(jsonObject.getString("product_type_name").equals("Credit Card")){
                            layout_loan_value.setVisibility(View.GONE);
                        } else {
                            if (!jsonObject.isNull("loan_amount_needed")) {
                                detail_loan_value.setText("Rs. " + Util.formatRs(jsonObject.optString("loan_amount_needed")));
                            } else if (jsonObject.isNull("disbursed_loan_value")) {
                                detail_loan_value.setText("Rs. " + Util.formatRs(jsonObject.optString("loan_amount_needed")));
                            } else {
                               detail_loan_value.setText("Rs. " + Util.formatRs(jsonObject.optString("disbursed_loan_value", jsonObject.optString("loan_amount_needed"))));
                            }
                        }

                        if (jsonObject.getString("lead_state_name").equals("Received")) {
                            String[] status = {jsonObject.getString("lead_state_name")}, lead_status = {jsonObject.getString("lead_state")}, status_char = {"R"};
                            mAdapter = new ReferralDetailAdapter(getApplicationContext(), status, lead_status, status_char);
                            recyclerview_lead_detail.setAdapter(mAdapter);
                        }
                        else if (jsonObject.getString("lead_state_name").equals("Failed")) {
                            String[] status = {jsonObject.getString("lead_state_name"), "Received"}, lead_status = {jsonObject.getString("lead_state"), "Completed"}, status_char = {"F", "R"};
                            mAdapter = new ReferralDetailAdapter(getApplicationContext(), status, lead_status, status_char);
                            recyclerview_lead_detail.setAdapter(mAdapter);
                        }
                        else if (jsonObject.getString("lead_state_name").equals("Closed")) {
                                String[] status = {jsonObject.getString("lead_state_name"), "Received"}, lead_status = {jsonObject.getString("lead_state"), "Completed"}, status_char = {"C", "R"};
                                mAdapter = new ReferralDetailAdapter(getApplicationContext(), status, lead_status, status_char);
                                recyclerview_lead_detail.setAdapter(mAdapter);
                        } else if (jsonObject.getString("lead_state_name").equals("In progress")) {
                            String[] status = {jsonObject.getString("lead_state_name"), "Received"}, lead_status = {jsonObject.getString("lead_state"), "Completed"}, status_char = {"P", "R"};
                            mAdapter = new ReferralDetailAdapter(getApplicationContext(), status, lead_status, status_char);
                            recyclerview_lead_detail.setAdapter(mAdapter);
                        } else if (jsonObject.getString("lead_state_name").equals("Approved")) {
                            String[] status = {jsonObject.getString("lead_state_name"), "In progress", "Received"}, lead_status = {jsonObject.getString("lead_state"), "Completed", "Completed"}, status_char = {"A","P", "R"};
                            mAdapter = new ReferralDetailAdapter(getApplicationContext(), status, lead_status, status_char);
                            recyclerview_lead_detail.setAdapter(mAdapter);
                        } else if (jsonObject.getString("lead_state_name").equals("Disbursed")) {
                            String[] status = {jsonObject.getString("lead_state_name"), "Approved", "In progress", "Received"}, lead_status = {jsonObject.getString("lead_state"), "Completed", "Completed", "Completed"}, status_char = {"D", "A", "P","R"};
                            mAdapter = new ReferralDetailAdapter(getApplicationContext(), status, lead_status, status_char);
                            recyclerview_lead_detail.setAdapter(mAdapter);
                        }

                        if (!jsonObject.getString("product_name").equals("Credit Card")) {
                            if(jsonObject.isNull("165")) {
                                layout_other_field.setVisibility(View.GONE);
                            } else {
                                if(!jsonObject.isNull("165")) {
                                    detail_interest.setText(jsonObject.getString("165"));
                                } else {
                                    detail_interest.setVisibility(View.GONE);
                                }
                                if(!jsonObject.isNull("116")) {
                                    detail_tenure.setText(jsonObject.getString("116"));
                                } else {
                                    detail_tenure.setVisibility(View.GONE);
                                }
                                if(!jsonObject.isNull("166")) {
                                    detail_fees.setText(jsonObject.getString("166"));
                                } else {
                                    detail_fees.setVisibility(View.GONE);
                                }
                                if(!jsonObject.isNull("167")) {
                                    detail_emi.setText(jsonObject.getString("167"));
                                } else {
                                    detail_emi.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            layout_other_field.setVisibility(View.GONE);
                        }

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
            } catch (JSONException e) {

            }

        }
    }
    private void initialization() {
        incomplete= (Button) findViewById(R.id.incomplete_button);
        incomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("amount",loan_amount);
                bundle.putString("name",name);
                bundle.putString("email","");
                bundle.putString("tenure",tenure);
                bundle.putString("phone",phone);
                bundle.putString("tname",loan_name);
                bundle.putString("type",type);
                bundle.putString("lead_id",lead_id);
                bundle.putString("product_id",product_id);
                bundle.putString("product_name", product_name);
                bundle.putString("bank_logo", product_logo);
                Logs.LogD("bundle", bundle.toString());
                if (sendQual){
                    Intent intent= new Intent(getApplicationContext(),QuotesGroupActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent= new Intent(getApplicationContext(),OffersActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
        });
        waiting_layout = (LinearLayout)findViewById(R.id.waiting_layout);
        progressBar = (ImageView) findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        referral_lead_state= (TextView) findViewById(R.id.referral_lead_state);
        mainLayout= (RelativeLayout) findViewById(R.id.mainLayout);
        detail_lead_back = (ImageView) findViewById(R.id.detail_lead_back);
        detail_lead_id = (TextView) findViewById(R.id.detail_lead_id);
        detail_interest = (TextView) findViewById(R.id.detail_interest);
        detail_tenure = (TextView) findViewById(R.id.detail_tenure);
        detail_fees = (TextView) findViewById(R.id.detail_fees);
        detail_emi = (TextView) findViewById(R.id.detail_emi);
        detail_loan_value = (TextView) findViewById(R.id.detail_loan_value);
        profile_image = (MaterialLetterIcon) findViewById(R.id.profile_image);
        detail_name = (TextView) findViewById(R.id.detail_name);
        detail_phone = (TextView) findViewById(R.id.detail_phone);
        detail_product_type = (TextView) findViewById(R.id.detail_product_type);
        recyclerview_lead_detail = (RecyclerView) findViewById(R.id.recyclerview_lead_detail);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
        layout_other_field = (LinearLayout) findViewById(R.id.layout_other_field);
        layout_loan_value = (CardView) findViewById(R.id.layout_loan_value);
        layout_eligible_payout = (LinearLayout) findViewById(R.id.layout_eligible_payout);
        updated_date = (TextView) findViewById(R.id.updated_date);
        detail_payout1 = (TextView) findViewById(R.id.detail_payout1);
        detail_payout2 = (TextView) findViewById(R.id.detail_payout2);
        eligible_payout = (TextView) findViewById(R.id.eligible_payout);
        payout_detail = (CardView)findViewById(R.id.payout_detail);
    }

}
