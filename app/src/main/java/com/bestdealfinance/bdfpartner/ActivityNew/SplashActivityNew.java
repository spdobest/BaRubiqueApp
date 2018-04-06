package com.bestdealfinance.bdfpartner.ActivityNew;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.BuildConfig;
import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SplashActivityNew extends AppCompatActivity {

    private static final String TAG = "SplashActivityNew";

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private int CACHE_PROGRESS = 0;
    private RequestQueue queue;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private DB snappyDB;
    private TextView tvLoadingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_new);


        queue = Volley.newRequestQueue(this);
        tvLoadingMessage = (TextView) findViewById(R.id.message);
        sendRegistrationIdToServer();
        requestPermission();
    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                )
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        else {
            checkAppUpdate();
        }
    }

    private void checkAppUpdate() {

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.fetch(1).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFirebaseRemoteConfig.activateFetched();

                }
            }
        });

        int mandatoryVersion = (int) mFirebaseRemoteConfig.getLong("mandatory");
        int optionalVersion = (int) mFirebaseRemoteConfig.getLong("optional");


        if (BuildConfig.VERSION_CODE < mandatoryVersion) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivityNew.this);
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivityNew.this);
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
                    cacheValidator();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            cacheValidator();
        }
    }

    public void cacheValidator() {
        try {
            snappyDB = DBFactory.open(this);
            if (snappyDB.exists(Constant.DB_ALL_PRODUCTS_DETAILS_OBJECT) && snappyDB.exists(Constant.DB_ALL_PAYOUTS_JSON_ARRAY) && snappyDB.exists(Constant.DB_ALL_STEPS_JSON_ARRAY) && snappyDB.exists(Constant.DB_REFER_CITY_LIST) && snappyDB.exists(Constant.DB_LEADERBOARD_ARRAY)) {
                snappyDB.close();
                updateCacheinBackground();
            } else {
                snappyDB.close();
                createCache();
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

    }

    private void createCache() {
        getPayoutDetailsFromServer(false);
        getLeaderBoardFromServer(false);
        getProductDetailsFromNetwork(false);
        getReferCity(false);

    }

    private void checkProgressAndContinune() {

        if (CACHE_PROGRESS < 100) {
            tvLoadingMessage.setText("Loading... " + CACHE_PROGRESS + "%");
        } else
            checkLogin();

    }

    private void updateCacheinBackground() {
        getPayoutDetailsFromServer(true);
        getLeaderBoardFromServer(true);
        getProductDetailsFromNetwork(true);
        getReferCity(true);
        checkLogin();
    }


    private void getReferCity(final boolean isBackground) {
        JSONObject object = new JSONObject();
        try {
            object.put("list_id", "10080");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.GET_UBER_LIST_FULL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("body");
                    snappyDB = DBFactory.open(SplashActivityNew.this);
                    snappyDB.put(Constant.DB_REFER_CITY_LIST, data.toString());
                    snappyDB.close();
                    CACHE_PROGRESS += 20;
                    if (!isBackground)
                        checkProgressAndContinune();
                } catch (JSONException | SnappydbException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

    private void getPayoutDetailsFromServer(final boolean isBackground) {
        try {

            JSONObject reqObject = new JSONObject();
            reqObject.put("key", Constant.OPEN_API_KEY);
            Helper.showLog(URL.PAYOUT_CALCULATOR, reqObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL.PAYOUT_CALCULATOR, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: getPayoutDetailsFromServer " + response);
                    if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                        try {
                            String status, msg;
                            status = response.getString(Constant.STATUS_CODE);
                            msg = response.getString(Constant.MSG);
                            if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {


                                if (snappyDB.isOpen()) snappyDB.close();
                                snappyDB = DBFactory.open(SplashActivityNew.this);
                                JSONObject body = response.optJSONObject(Constant.BODY);
                                JSONArray allStepsJsonArray = body.optJSONArray("steps");
                                JSONArray allPayoutJsonArray = body.optJSONArray("payouts");
                                if (allPayoutJsonArray != null && allPayoutJsonArray.length() > 0) {
                                    snappyDB.put(Constant.DB_ALL_PAYOUTS_JSON_ARRAY, allPayoutJsonArray.toString());
                                    CACHE_PROGRESS += 20;
                                }

                                if (allStepsJsonArray != null && allStepsJsonArray.length() > 0) {
                                    snappyDB.put(Constant.DB_ALL_STEPS_JSON_ARRAY, allStepsJsonArray.toString());
                                    CACHE_PROGRESS += 20;
                                }
                                snappyDB.close();

                                if (!isBackground)
                                    checkProgressAndContinune();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1F));
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getLeaderBoardFromServer(final boolean isBackground) {

        try {

            JSONObject reqObject = new JSONObject();
            reqObject.put("key", "tra!33$");
            Helper.showLog(URL.LEADER_BOARD, reqObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL.LEADER_BOARD, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: dashboardfragment  " + response);
                    if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                        try {
                            String status, msg;
                            status = response.getString(Constant.STATUS_CODE);
                            msg = response.getString(Constant.MSG);
                            if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {

                                JSONObject body = response.optJSONObject(Constant.BODY);
                                body = body.optJSONObject("detail");
                                JSONArray leaderBoardJsonArray = body.optJSONArray("data");
                                snappyDB = DBFactory.open(SplashActivityNew.this);
                                snappyDB.put(Constant.DB_LEADERBOARD_ARRAY, leaderBoardJsonArray.toString());
                                snappyDB.close();
                                CACHE_PROGRESS += 20;

                                if (!isBackground)
                                    checkProgressAndContinune();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getProductDetailsFromNetwork(final boolean isBackground) {

        JSONObject object = new JSONObject();
        try {
            object.put("key", Constant.OPEN_API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Helper.showLog(URL.FETCH_ALL_PRODUCTS_DETAILS, object.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.FETCH_ALL_PRODUCTS_DETAILS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONObject body = response.optJSONObject("body");
                try {
                    Log.i(TAG, "onResponse: json object " + body.toString());
                    snappyDB = DBFactory.open(SplashActivityNew.this);
                    snappyDB.put(Constant.DB_ALL_PRODUCTS_DETAILS_OBJECT, body.toString());
                    snappyDB.close();

                    CACHE_PROGRESS += 20;
                    if (!isBackground)
                        checkProgressAndContinune();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }


    public void checkLogin() {
        if (Helper.getStringSharedPreference(Constant.UTOKEN, SplashActivityNew.this).equals("")) {
            startActivity(new Intent(SplashActivityNew.this, WelcomeActivityNew.class));
            finish();
        } else {
            if (Helper.isNetworkAvailable(getApplicationContext())) {
                Logs.LogD("SPlash", "Checking User Token");
                JSONObject request = new JSONObject();
                try {
                    request.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, SplashActivityNew.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Helper.showLog(URL.GET_LOGIN, request.toString());
                JsonObjectRequest checkLoginRequest = new JsonObjectRequest(Request.Method.POST, URL.GET_LOGIN, request, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.opt("status_code") != null && response.opt("msg") != null) {
                                String status = null;

                                status = response.getString("status_code");

                                String msg = response.getString("msg");
                                if (status.equals("2000") && msg.equals("Success")) {

                                        JSONObject data = response.getJSONObject("body");
                                        Helper.setStringSharedPreference(Constant.USEROBJECT, data.toString(), SplashActivityNew.this);
                                        startActivity(new Intent(SplashActivityNew.this, MainActivityNew.class));
                                        finish();

                                } else {
                                    Helper.setStringSharedPreference(Constant.UTOKEN, "", SplashActivityNew.this);
                                    startActivity(new Intent(SplashActivityNew.this, WelcomeActivityNew.class));
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Helper.setStringSharedPreference(Constant.UTOKEN, "", SplashActivityNew.this);
                        startActivity(new Intent(SplashActivityNew.this, WelcomeActivityNew.class));
                        finish();
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/json");
                        headers.put("Cookie", "utoken=" + Helper.getStringSharedPreference(Constant.UTOKEN, SplashActivityNew.this));
                        return headers;
                    }
                };
                queue.add(checkLoginRequest);

            } else {
                AlertDialog.Builder builder = null;
                builder = new AlertDialog.Builder(SplashActivityNew.this);
                builder.setTitle("Network Connection");
                builder.setMessage("Please make sure your internet connection is working");
                //builder.setIcon(R.drawable.net_conn);
                builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }
                );
                builder.show();
            }
        }
    }

    private void sendRegistrationIdToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InstanceID instanceID = InstanceID.getInstance(SplashActivityNew.this);
                String token = "";
                String android_id = "";
                String url = "";
                try {
                    token = instanceID.getToken(Constant.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    android_id = Settings.Secure.getString(SplashActivityNew.this.getContentResolver(), Settings.Secure.ANDROID_ID);

                    Log.i("GCM", "GCM Registration Token: " + token);
                    if (Helper.getStringSharedPreference(Constant.UTOKEN, SplashActivityNew.this).equals("")) {
                        url = URL.GCM_REGISTER;
                    } else {
                        url = URL.GCM_UPDATE;
                    }
                    Log.i(TAG, "run: ");
                    JSONObject request = new JSONObject();
                    request.put("deviceid", android_id);
                    request.put("regID", token);
                    request.put("source", Logs.source);
                    request.put("app_version", BuildConfig.VERSION_CODE);
                    request.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, SplashActivityNew.this));
                    if (!Helper.getStringSharedPreference(Constant.UTOKEN, SplashActivityNew.this).equals("")) {
                        request.put("userid", Helper.getStringSharedPreference(Constant.USERID, SplashActivityNew.this));
                    }

                    Helper.setStringSharedPreference(Constant.TOKEN, token, SplashActivityNew.this);
                    Helper.showLog(url, request.toString());

                    JsonObjectRequest registerToken = new JsonObjectRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    queue.add(registerToken);


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED
                        && grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                    checkAppUpdate();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivityNew.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Permission Required");
                    alertDialog.setMessage("We need those permission. We will not store your personal data on our servers.");
                    alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermission();
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }

}
