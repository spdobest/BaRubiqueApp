package com.bestdealfinance.bdfpartner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

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

        controller = new DBHelper(this);
        if (Util.isNetworkAvailable(getApplicationContext())) {
//            ProductTypeAsyncTask task=new ProductTypeAsyncTask();
//            task.executeOnExecutor(Util.threadPool);
            controller.deleteTables();
            controller.insertProductType("11", Util.card[0]);
            controller.insertProductType("22", Util.product[1]);
            controller.insertProductType("23", Util.product[2]);
            controller.insertProductType("25", Util.product[3]);
            controller.insertProductType("26", Util.product[4]);
            controller.insertProductType("28", Util.product[5]);
            controller.insertProductType("39", Util.product[6]);

            Util.product_type_data = controller.getAllProductName();
//            APIUtils.GetCity getCity=new APIUtils.GetCity();
//            getCity.executeOnExecutor(Util.threadPool);
        } else {
            Snackbar.make(img_splash_bg, R.string.no_connection, Snackbar.LENGTH_LONG).show();
            // TODO set RETRY action
        }
        Util.product_type_data = controller.getAllProductName();

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

                    if (BuildConfig.VERSION_CODE < mandatoryVersion) {
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
                        checkLogin();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                Logs.LogD("SPlash", "Checking User Token");
                JSONObject request = new JSONObject();
                try {
                    request.put("utoken", str_utoken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest checkLoginRequest = new JsonObjectRequest(Request.Method.POST, Util.GET_LOGIN, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.opt("status_code") != null && response.opt("msg") != null) {
                                String status = null;

                                status = response.getString("status_code");

                                String msg = response.getString("msg");
                                if (status.equals("2000") && msg.equals("Success")) {
                                    JSONObject data = response.getJSONObject("body");
                                    Util.o_id = data.getString("id");
                                    Util.email = data.getString("email");
                                    Util.mobile = data.getString("mobile_number");
                                    Logs.LogD("Splash", "userid " + Util.o_id);
                                    Logs.LogD("Splash", "Utoken " + str_utoken);
                                    Util.nav_status = Constant.SPLASH;
                                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    Snackbar.make(img_splash_bg, R.string.login_expired, Snackbar.LENGTH_LONG).show();
                                    Helper.setStringSharedPreference(Constant.UTOKEN, "", SplashActivity.this);
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
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
                queue.add(checkLoginRequest);
                //HttpAsyncTask task2=new HttpAsyncTask();
                //task2.executeOnExecutor(Util.threadPool);
            } else {
                AlertDialog.Builder builder = null;
                builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Network Connection");
                builder.setMessage("Please make sure your internet connection is working");
                //builder.setIcon(R.drawable.net_conn);
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

    /*private void notificationFlow(){
        userid = Helper.getStringSharedPreference(Constant.USERID,SplashActivity.this);
        context = getApplicationContext();
        if (Helper.checkPlayServices(SplashActivity.this)) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                Logs.LogD(TAG, "Registration not found.");
                registerInBackground();
            } else {
                Logs.LogD("BDFGCM","Registration ID present, Skipped");
                Logs.LogD(TAG, "Reg ID= " + regid);
                editor.putString("RegID", regid);
                editor.apply();
            }
        }
        else {
            Logs.LogD("Splash", "No Play Services");
        }
    }


    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = pref_reg;
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration not found.");
            return "";
        }
        //  Log.d(TAG, "Registration fOUND WITH id. " + registrationId);
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Util.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Logs.LogD("Registration", msg);
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.

                        sendRegistrationIdToBackend(regid);


                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

        }.executeOnExecutor(Util.threadPool);
    }

    private void sendRegistrationIdToBackend(final String registration_id) {

        final int registeredVersion = pref_reg.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        editor.putString("RegID", registration_id);
        editor.apply();
        Logs.LogD("Splash", "Sending Registration to BackEnd");
        String android_id = "";
     try
     {
         if (ContextCompat.checkSelfPermission(SplashActivity.this,
                 Manifest.permission.READ_PHONE_STATE)
                 == PackageManager.PERMISSION_GRANTED) {
             TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
             android_id = mngr.getDeviceId();
         }
         else {
             android_id= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
             Logs.LogD("Marshmellow",android_id);
         }
    }
    catch (Exception e){
        Logs.LogD("Excepton",e.getLocalizedMessage());
        android_id= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        //Fuck You Marshmellon
    }


        final String finalAndroid_id = android_id;
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String response = "";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost;
                if (Util.isRegistered(getApplicationContext()).equals("")){

                    Logs.LogD("BDFGCM","User NOT Registered, calling RegisterApp");
                  httppost = new HttpPost(Util.ROOT_URL_V2+"notification/registerApp");
                }
                else {
                    Logs.LogD("BDFGCM","User Registered, calling UpdateApp");
                    httppost = new HttpPost(Util.ROOT_URL_V2+"notification/updateAppRegistration");
                }

                try {
                    // Add your data
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("deviceid", finalAndroid_id);
                    jsonObject.put("regID",registration_id);
                    jsonObject.put("source",Logs.source);
                    jsonObject.put("app_version", registeredVersion);
                    if (!Util.isRegistered(getApplicationContext()).equals("")) {
                        httppost.addHeader("Cookie", "utoken=" + Util.isRegistered(getApplicationContext()));
                        jsonObject.put("userid", Helper.getStringSharedPreference(Util.userid,SplashActivity.this));
                    }
                    StringEntity se = new StringEntity(jsonObject.toString());
                    // 6. set httpPost Entity
                    httppost.setEntity(se);
                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-type", "application/json");
                    Logs.LogD("Request", jsonObject.toString());
                    // Execute HTTP Post Request
                    HttpResponse result = httpclient.execute(httppost);
                    InputStream content = result.getEntity().getContent();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (ClientProtocolException e) {
                    Logs.LogD("Exception",e.getLocalizedMessage());

                } catch (IOException e) {
                    Logs.LogD("Exception",e.getLocalizedMessage());
                } catch (JSONException e) {
                    Logs.LogD("Exception",e.getLocalizedMessage());
                    e.printStackTrace();
                }
                Logs.LogD("Backend REsponse", response);
                return response;
            }
        }.executeOnExecutor(Util.threadPool);
    }


    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = pref_reg;
        int appVersion = getAppVersion(context);
        Logs.LogD(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }
*/


}
