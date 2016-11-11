package com.bestdealfinance.bdfpartner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.BuildConfig;
import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.database.DBHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {


    GoogleCloudMessaging gcm;
    static final String TAG = "BDFGCM";
    Context context;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String regid;
    public static final String PROPERTY_REG_ID = "registration_id";
    String str_username, str_utoken;
    static DBHelper controller;
    private String userid;
    private RequestQueue queue;
    private ImageView img_splash_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        queue = Volley.newRequestQueue(this);

        ImageView img_splash_bg = (ImageView) findViewById(R.id.img_splash_bg);
        ImageView rubique_logo = (ImageView) findViewById(R.id.rubique_logo);

        Glide.with(SplashActivity.this).load(R.drawable.splash_bg).into(img_splash_bg);
        Glide.with(SplashActivity.this).load(R.drawable.splash_logo).into(rubique_logo);

        str_username = Helper.getStringSharedPreference(Constant.USERNAME, SplashActivity.this);
        str_utoken = Helper.getStringSharedPreference(Constant.UTOKEN, SplashActivity.this);

        //TODO Notifcation Flow
        sendRegistrationIdToServer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        StringRequest stringRequest = new StringRequest(Util.APP_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject resObject = new JSONObject(response);
                    JSONObject bodyObject = resObject.getJSONObject("body");
                    int mandatoryVersion = bodyObject.getInt("mandatory_update");
                    int optionalVersion = bodyObject.getInt("optional_update");
                    //Toast.makeText(SplashActivity.this,"app update response"+mandatoryVersion+"-"+optionalVersion,Toast.LENGTH_SHORT).show();

                    if (BuildConfig.VERSION_CODE < mandatoryVersion) {
                        //Toast.makeText(SplashActivity.this,"in mandatory",Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                        alertDialogBuilder.setTitle("Mandatory Update Required");
                        alertDialogBuilder.setMessage("Your App is outdated. You may be missing some new features. Please update the app.");
                        alertDialogBuilder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.bestdealfinance.bdfpartner"));
                                startActivity(i);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    } else if (BuildConfig.VERSION_CODE < optionalVersion) {
                        //Toast.makeText(SplashActivity.this,"in optional",Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                        alertDialogBuilder.setTitle("Optional Update Required");
                        alertDialogBuilder.setMessage("Your App is outdated. You may be missing some new features. Please update the app.");
                        alertDialogBuilder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.bestdealfinance.bdfpartner"));
                                startActivity(i);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("IGNORE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkLogin();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    } else {
                        // Toast.makeText(SplashActivity.this,"in else",Toast.LENGTH_SHORT).show();

                        checkLogin();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkLogin();
            }
        });
        queue.add(stringRequest);
    }

    private void sendRegistrationIdToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                InstanceID instanceID = InstanceID.getInstance(SplashActivity.this);
                String token = "";
                String android_id = "";
                String url = "";
                try {
                    token = instanceID.getToken(Constant.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    android_id = Settings.Secure.getString(SplashActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

                    Log.i("GCM", "GCM Registration Token: " + token);

                    if (Helper.getStringSharedPreference(Constant.TOKEN, SplashActivity.this).equals(token)) {
                        return;
                    }

                    if (Helper.getStringSharedPreference(Constant.UTOKEN, SplashActivity.this).equals("")) {
                        url = Util.GCM_REGISTER;
                    } else {
                        url = Util.GCM_UPDATE;
                    }

                    JSONObject request = new JSONObject();
                    request.put("deviceid", android_id);
                    request.put("regID", token);
                    request.put("source", Logs.source);
                    request.put("app_version", BuildConfig.VERSION_CODE);
                    if (!str_utoken.equals("")) {
                        request.put("userid", Helper.getStringSharedPreference(Constant.USERID, SplashActivity.this));
                    }

                    Helper.setStringSharedPreference(Constant.TOKEN, token, SplashActivity.this);
                    JsonObjectRequest registerToken = new JsonObjectRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Accept", "application/json");
                            headers.put("Content-Type", "application/json");
                            if (!str_utoken.equals("")) {
                                headers.put("Cookie", "utoken=" + str_utoken);
                            }
                            return headers;
                        }

                    };

                    queue.add(registerToken);


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void checkLogin() {

        if (str_utoken.equals("")) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));

            finish();
        } else {
            if (Util.isNetworkAvailable(getApplicationContext())) {
                JSONObject request = new JSONObject();
                try {
                    request.put("utoken", str_utoken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(this,"Network available",Toast.LENGTH_SHORT).show();
                JsonObjectRequest checkLoginRequest = new JsonObjectRequest(Request.Method.POST, Util.GET_LOGIN, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //  Toast.makeText(SplashActivity.this,"Response"+response.toString(),Toast.LENGTH_LONG).show();

                            if (response.opt("status_code") != null && response.opt("msg") != null) {
                                String status = null;
                                //  Toast.makeText(SplashActivity.this,"Response",Toast.LENGTH_SHORT).show();
                                status = response.getString("status_code");

                                String msg = response.getString("msg");
                                if (status.equals("2000") && msg.equals("Success")) {
                                    //  Toast.makeText(SplashActivity.this,"Response success",Toast.LENGTH_SHORT).show();

                                    JSONObject data = response.getJSONObject("body");

                                    Util.o_id = data.getString("id");

                                    Util.email = data.getString("email");

                                    Util.mobile = data.getString("mobile_number");

                                    Util.nav_status = Constant.SPLASH;

                                    Helper.setStringSharedPreference(Constant.USER_CLASS, data.optString("ba_class", "5"), SplashActivity.this);

                                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                    finish();
                                } else {

                                    Toast.makeText(SplashActivity.this, "Your login has expired, please login again", Toast.LENGTH_SHORT).show();

                                    //Snackbar.make(img_splash_bg, R.string.login_expired, Snackbar.LENGTH_LONG).show();
                                    Helper.setStringSharedPreference(Constant.UTOKEN, "", SplashActivity.this);
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                }
                            } else {
                                Toast.makeText(SplashActivity.this, "Your login has expired, please login again", Toast.LENGTH_SHORT).show();

                                //Snackbar.make(img_splash_bg, R.string.login_expired, Snackbar.LENGTH_LONG).show();
                                Helper.setStringSharedPreference(Constant.UTOKEN, "", SplashActivity.this);
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SplashActivity.this, "Your login has expired, please login again", Toast.LENGTH_SHORT).show();

                        //Snackbar.make(img_splash_bg, R.string.login_expired, Snackbar.LENGTH_LONG).show();
                        Helper.setStringSharedPreference(Constant.UTOKEN, "", SplashActivity.this);
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();

                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/json");
                        headers.put("Cookie", "utoken=" + str_utoken);
                        return headers;
                    }
                };
                queue.getCache().clear();
                queue.add(checkLoginRequest);
                //HttpAsyncTask task2=new HttpAsyncTask();
                //task2.executeOnExecutor(Util.threadPool);
            } else {
                AlertDialog.Builder builder = null;
                builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Network Connection");
                builder.setMessage("Please make sure your internet connection is working");
                builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (str_username.equals("") && str_utoken.equals("")) {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                    finish();
                                }
                            }
                        }
                );
                builder.show();
            }
        }
    }


}
