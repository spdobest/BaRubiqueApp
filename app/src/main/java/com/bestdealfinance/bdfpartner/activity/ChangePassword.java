package com.bestdealfinance.bdfpartner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
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

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    Button btn_pwd_submit, btn_pwd_cancel;
    EditText txt_old_password, txt_new_password,txt_retype_new_password;
    String str_old_password, str_new_password;
    LinearLayout waiting_layout;
    ImageView progressBar;
    SharedPreferences sharedpreferences;
    private AnimationDrawable animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedpreferences = getSharedPreferences(Util.MY_PREFERENCES, Context.MODE_PRIVATE);
        initialization();


        btn_pwd_submit.setOnClickListener(this);
        btn_pwd_cancel.setOnClickListener(this);

        txt_retype_new_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_new_password.getText().toString().equals(txt_retype_new_password.getText().toString())) {
                    //Password match
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

        });

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Change Password Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }

    private void initialization() {
        waiting_layout = (LinearLayout)findViewById(R.id.waiting_layout);
        progressBar = (ImageView)findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        txt_old_password = (EditText)findViewById(R.id.txt_old_password);
        txt_new_password = (EditText)findViewById(R.id.txt_new_password);
        txt_retype_new_password = (EditText)findViewById(R.id.txt_retype_new_password);
        btn_pwd_submit = (Button)findViewById(R.id.btn_pwd_submit);
        btn_pwd_cancel = (Button)findViewById(R.id.btn_pwd_cancel);
        txt_old_password.setTypeface(Typeface.createFromAsset(this.getAssets(), getResources().getString(R.string.domine)));
        txt_new_password.setTypeface(Typeface.createFromAsset(this.getAssets(), getResources().getString(R.string.domine)));
        txt_retype_new_password.setTypeface(Typeface.createFromAsset(this.getAssets(), getResources().getString(R.string.domine)));
        btn_pwd_submit.setTypeface(Typeface.createFromAsset(this.getAssets(), getResources().getString(R.string.domine)));
        btn_pwd_cancel.setTypeface(Typeface.createFromAsset(this.getAssets(), getResources().getString(R.string.domine)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pwd_submit:
                if (txt_old_password.getText().toString().isEmpty()) {
                    txt_old_password.setError("Please enter old password");
                    txt_old_password.requestFocus();
                } else {
                    if(txt_new_password.getText().toString().isEmpty()) {
                        txt_new_password.setError("Please enter new password");
                        txt_new_password.requestFocus();
                    } else {
                        if(txt_retype_new_password.getText().toString().isEmpty()){
                            txt_retype_new_password.setError("Please enter Re-Type new password");
                            txt_retype_new_password.requestFocus();
                        } else {
//                            if(txt_old_password.getText().toString().trim().equals(str_old_password)){
                                if(txt_new_password.getText().toString().equals(txt_retype_new_password.getText().toString())){
                                    str_new_password = txt_new_password.getText().toString().trim();
                                    str_old_password = txt_old_password.getText().toString().trim();
                                    new HttpAsyncTask().execute();
                                } else {
                                    txt_retype_new_password.setError("Please ensure re-type password is same as new password");
                                    txt_retype_new_password.requestFocus();
                                }
//                            } else {
//                                txt_old_password.setError("Please enter correct old password");
//                                txt_old_password.requestFocus();
//                            }
                        }
                    }
                }
                break;
            case R.id.btn_pwd_cancel:
                finish();
                break;
        }
    }

    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

        String result1 = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waiting_layout.setVisibility(View.VISIBLE);
            animation.start();
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.CHANGE_PASSWORD);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("old_password", str_old_password);
                jsonObject.accumulate("new_password", str_new_password);

                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);
                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + sharedpreferences.getString(Util.utoken, ""));
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
            waiting_layout.setVisibility(View.GONE);
            animation.stop();
            Logs.LogD("Response",result);
            String status, msg, utoken;
            try {
                final JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                        builder.setTitle("Message");
                        builder.setMessage(output1.getString("body")+"");
                        builder.setCancelable(false);
                        builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if(output1.getString("body").equals("Password updated successfully")){
                                        finish();
                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.show();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                        builder.setTitle("Message");
                        builder.setMessage(output1.getString("body") + "");
                        builder.setCancelable(false);
//                          builder.setIcon(R.drawable.net_conn);
                        builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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
}
