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
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        queue = Volley.newRequestQueue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView textView = (TextView) findViewById(R.id.txt_login_heading);

        TextView fpHeading = (TextView) findViewById(R.id.forgot_password_heading);

        TextView registerHeading = (TextView) findViewById(R.id.register_heading);


        final ImageView progressBar = (ImageView) findViewById(R.id.waiting);
        final LinearLayout progressBarLayout = (LinearLayout) findViewById(R.id.waiting_layout);
        progressBar.setBackgroundResource(R.drawable.waiting);
        final AnimationDrawable animation = (AnimationDrawable) progressBar.getBackground();

        final TextInputLayout emailLayout = (TextInputLayout) findViewById(R.id.txt_login_email_layout);
        final EditText emailView = (EditText) findViewById(R.id.txt_login_email);


        final TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.txt_login_password_layout);
        final EditText passwordView = (EditText) findViewById(R.id.txt_login_password);


        passwordView.setTypeface(Typeface.DEFAULT);
        passwordView.setTransformationMethod(new PasswordTransformationMethod());
        Button logIn = (Button) findViewById(R.id.btn_login_signin);
        Button forgotPassword = (Button) findViewById(R.id.btn_login_forgot_password);
        Button register = (Button) findViewById(R.id.btn_login_register);


        forgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));

            }
        });

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        logIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.validate(emailLayout, emailView, "email", LoginActivity.this) && Helper.validate(passwordLayout, passwordView, "", LoginActivity.this)) {
                    progressBarLayout.setVisibility(View.VISIBLE);
                    animation.start();
                    Helper.hideKeyboard(LoginActivity.this);
                    try {
                        JSONObject requestObject = new JSONObject();
                        requestObject.put("username", emailView.getText().toString());
                        requestObject.put("password", passwordView.getText().toString());
                        requestObject.put("remember", 1);
                        requestObject.put("is_ba","1");

                        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.LOGIN, requestObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Response", response.toString());
                                progressBarLayout.setVisibility(View.GONE);
                                animation.stop();
                                try {
                                    if (response.getString("status_code").equals("2000")) {
                                        JSONObject body = response.getJSONObject("body");
                                        Util.o_id = body.getString("id");
                                        Util.email = body.getString("email");
                                        Util.mobile = body.getString("mobile_number");
                                        Helper.setStringSharedPreference(Constant.USERID, body.getString("id"), LoginActivity.this);
                                        Helper.setStringSharedPreference(Constant.USERNAME, body.getString("email"), LoginActivity.this);
                                        Helper.setStringSharedPreference(Constant.NAME, body.getString("name"), LoginActivity.this);
                                        Helper.setStringSharedPreference(Constant.USER_PHONE, body.getString("mobile_number"), LoginActivity.this);
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                Log.d("Status Code", "" + networkResponse.statusCode);

                                progressBarLayout.setVisibility(View.GONE);
                                animation.stop();
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle(R.string.txt_login_failed);
                                builder.setMessage(R.string.txt_invalid_credentials);
                                builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();

                            }
                        }) {

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Accept", "application/json");
                                headers.put("Content-Type", "application/json");
                                return headers;
                            }

                            @Override
                            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), LoginActivity.this);
                                return super.parseNetworkResponse(response);
                            }
                        };
                        queue.add(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        });

    }
    /*private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

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

                HttpPost httpPost = new HttpPost(Util.LOGIN);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("username", str_name);
                jsonObject.accumulate("password", str_password);
                jsonObject.accumulate("remember", 1);

                json = jsonObject.toString();
                Logs.LogD("Payload", json);
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);
                httpPost.addHeader("Accept", "application/json");
                httpPost.addHeader("Content-type", "application/json");

                // 7. Set some headers to inform server about the type of the content

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                Header[] headers = httpResponse.getAllHeaders();
                for (Header header : headers) {
//                    Logs.LogD("Headers : ", header.getName() + " ,Value : " + header.getValue());
                    if (header.getName().equals("utoken")){
                        str_utoken=header.getValue();
                        Logs.LogD("utoken",str_utoken);
                    }
                }
//                Header[] cookieHeader = httpResponse.getHeaders("Set-Cookie");
//                if (cookieHeader.length > 0) {
//                    String[] str_header = cookieHeader[0].getValue().split(";");
//                    String[] str_token = str_header[0].split("=");
//                    str_utoken = str_token[1];
//                }
                Logs.LogD("Token",str_utoken);
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
            Logs.LogD("Login",result);
            waiting_layout.setVisibility(View.GONE);
            animation.stop();
            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {
//                        if(ref_val.equals("referral")){
//                            ReferralActivity.callSubmitFunctionn();
//                        } else {
                            JSONObject data = output1.getJSONObject("body");
                            Util.o_id = data.getString("id");
                            Util.email = data.getString("email");
                            Util.mobile = data.getString("mobile_number");
                            SharedPreferences.Editor editor=getSharedPreferences(Util.MY_PREFERENCES, MODE_PRIVATE).edit();
                            editor.putString(Util.username, Util.email);
                            editor.putString(Util.utoken, str_utoken);
                            editor.putString(Util.phone,Util.mobile);
                            editor.putString(Util.userid, data.getString("id"));
                            editor.commit();


                            startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();

                    } else {
                        if(msg.equals("Failed")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Message");
                            builder.setMessage(output1.getString("body"));
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }) {
                            });
                            builder.show();
                        }
                    }
                }
            } catch (JSONException e) {

            }

        }
    }
*/


}
