package com.bestdealfinance.bdfpartner.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {


    ImageView progressBar;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        queue = Volley.newRequestQueue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.waiting_layout);
        ImageView progressBar = (ImageView) findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        final AnimationDrawable animation = (AnimationDrawable) progressBar.getBackground();

        final LinearLayout emailForgotForm = (LinearLayout) findViewById(R.id.email_forgot_form);
        final LinearLayout setPasswordForm = (LinearLayout) findViewById(R.id.ll_set_password);

        final TextInputLayout forgotEmailLayout = (TextInputLayout) findViewById(R.id.txt_forget_email_layout);
        final EditText forgotEmailView = (EditText) findViewById(R.id.txt_forget_email);

        final TextView fpMessage = (TextView) findViewById(R.id.fp_message);

        TextInputLayout etForgotEmailLayout = (TextInputLayout) findViewById(R.id.et_forget_email_layout);
        final EditText etForgotEmailView = (EditText) findViewById(R.id.et_forget_email);

        final TextInputLayout etEmailOtpLayout = (TextInputLayout) findViewById(R.id.et_email_otp_layout);
        final EditText etEmailOtpView = (EditText) findViewById(R.id.et_email_otp);

        final TextInputLayout etNewPasswordLayout = (TextInputLayout) findViewById(R.id.et_new_password_layout);
        final EditText etNewPasswordView = (EditText) findViewById(R.id.et_new_password);
        etNewPasswordView.setTypeface(Typeface.DEFAULT);
        etNewPasswordView.setTransformationMethod(new PasswordTransformationMethod());

        Button forgotSubmit = (Button) findViewById(R.id.btn_forget_submit);

        Button buttonSetSubmit = (Button) findViewById(R.id.btn_set_submit);

        Button registerButton = (Button) findViewById(R.id.fp_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassword.this, RegisterActivity.class));
                finish();
            }
        });

        forgotSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Helper.hideKeyboard(ForgotPassword.this);

                    if(Helper.validate(forgotEmailLayout,forgotEmailView,"email",ForgotPassword.this))
                    {
                        progressBarLayout.setVisibility(View.VISIBLE);
                        animation.start();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("username",forgotEmailView.getText().toString().trim());

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Util.FORGOT_PASSWORD, reqObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressBarLayout.setVisibility(View.GONE);
                                animation.stop();
                                try {
                                    if(response.getString("status_code").equals("2000"))
                                    {
                                        emailForgotForm.setVisibility(View.GONE);
                                        setPasswordForm.setVisibility(View.VISIBLE);
                                        etForgotEmailView.setText(forgotEmailView.getText().toString().trim());
                                        fpMessage.setText("We've sent an OTP to your registered email address, please enter the OTP below to reset your password.");

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                NetworkResponse networkResponse = error.networkResponse;
                                Log.d("Status Code",""+networkResponse.statusCode);

                                progressBarLayout.setVisibility(View.GONE);
                                animation.stop();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                                builder.setTitle("Email Not Found");
                                builder.setMessage("Invalid Credentials.");
                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();


                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Accept", "application/json");
                                headers.put("Content-Type", "application/json");
                                return headers;
                            }
                        };
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000,1,1f));
                        queue.add(jsonObjectRequest);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        buttonSetSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Helper.hideKeyboard(ForgotPassword.this);
                    if(Helper.validate(etEmailOtpLayout,etEmailOtpView,"text",ForgotPassword.this) && Helper.validate(etNewPasswordLayout,etNewPasswordView,"text",ForgotPassword.this))
                    {
                        progressBarLayout.setVisibility(View.VISIBLE);
                        animation.start();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("email",forgotEmailView.getText().toString());
                        reqObject.put("otp",etEmailOtpView.getText().toString());
                        reqObject.put("password",etNewPasswordView.getText().toString());

                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Util.RESET_PASSWORD, reqObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    progressBarLayout.setVisibility(View.GONE);
                                    animation.stop();

                                    if (response.getString("status_code").equals("2000")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                                        builder.setTitle("Password Reset");
                                        builder.setMessage("Password successfully updated.");
                                        builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                        builder.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                Log.d("Status Code",""+networkResponse.statusCode);

                                progressBarLayout.setVisibility(View.GONE);
                                animation.stop();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                                builder.setTitle("Authentication Failed");
                                builder.setMessage("Try Again");
                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Accept", "application/json");
                                headers.put("Content-Type", "application/json");
                                return headers;
                            }
                        };
                        queue.add(objectRequest);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }



    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_forget_submit:
                if (txt_forget_email.getText().toString().trim().isEmpty()){
                    txt_forget_email.setError("Please enter valid register email-id.");
                    txt_forget_email.requestFocus();
                } else {
                    if(Patterns.EMAIL_ADDRESS.matcher(txt_forget_email.getText().toString().trim()).matches()) {
                        str_forget_password = txt_forget_email.getText().toString().trim();
                        new HttpAsyncTask().execute();
                    } else {
                        txt_forget_email.setError("Please enter valid Email-ID");
                        txt_forget_email.requestFocus();
                    }
                }
                break;
            case R.id.txt_password_bottom_text:
                startActivity(new Intent(ForgotPassword.this, RegisterActivity.class));
                break;
            case R.id.btn_set_submit:
                if(!et_forget_email.getText().toString().isEmpty()){
                    if(et_email_otp.getText().toString().trim().isEmpty()){
                        et_email_otp.setError("Please enter otp which is sent to Email");
                        et_email_otp.requestFocus();
                    } else {
                        if(et_new_password.getText().toString().trim().isEmpty()){
                            et_new_password.setError("Please enter new password");
                            et_new_password.requestFocus();
                        } else {
                            str_email_otp = et_email_otp.getText().toString().trim();
                            str_new_password = et_new_password.getText().toString().trim();
                            new ResetPasswordAPI().execute();
                        }
                    }
                }
                break;
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

                HttpPost httpPost = new HttpPost(Util.FORGOT_PASSWORD);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("username", str_forget_password);

                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);
                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else {
                    result = "Did not work!";
                }

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

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {
                        ll_forget_password.setVisibility(View.GONE);
                        ll_set_password.setVisibility(View.VISIBLE);
                        et_forget_email.setText(str_forget_password);
                    } else {
                        if(msg.equals("Failed")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                            builder.setTitle("Failed");
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

    private class ResetPasswordAPI extends AsyncTask<Void, Void, String> {

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

                HttpPost httpPost = new HttpPost(Util.RESET_PASSWORD);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", str_forget_password);
                jsonObject.accumulate("otp", str_email_otp);
                jsonObject.accumulate("password", str_new_password);

                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else {
                    result = "Did not work!";
                }

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
            String status, msg;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                        builder.setTitle("Password Reset");
                        builder.setMessage("Password successfully updated.");
//                          builder.setIcon(R.drawable.net_conn);
                        builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.show();
                    } else {
                        if(msg.equals("Failed")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                            builder.setTitle("Failed");
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
    }*/
}
