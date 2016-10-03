package com.bestdealfinance.bdfpartner.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Translator;
import com.bestdealfinance.bdfpartner.application.Util;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class RegisterActivity extends AppCompatActivity {


    private RequestQueue queue;
    private boolean isApplied = false;
    private int leadPartnerId = 0;
    private ArrayAdapter<String> professionAdapter;
    private AppCompatSpinner spinnerProfession;
    private AutoCompleteTextView cityView;
    private List<String> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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


        final LinearLayout waitingLayout = (LinearLayout) findViewById(R.id.waiting_layout);
        ImageView progressBar = (ImageView) findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        final AnimationDrawable animation = (AnimationDrawable) progressBar.getBackground();

        final TextInputLayout fullNameLayout = (TextInputLayout) findViewById(R.id.txt_full_name_layout);
        final EditText fullNameView = (EditText) findViewById(R.id.txt_full_name);

        final TextInputLayout phoneLayout = (TextInputLayout) findViewById(R.id.txt_phone_number_layout);
        final EditText phoneView = (EditText) findViewById(R.id.txt_phone_number);

        final TextInputLayout emailLayout = (TextInputLayout) findViewById(R.id.txt_email_layout);
        final EditText emailView = (EditText) findViewById(R.id.txt_email);

        final TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.txt_regi_password_layout);
        final EditText passwordView = (EditText) findViewById(R.id.txt_regi_password);
        passwordView.setTypeface(Typeface.DEFAULT);
        passwordView.setTransformationMethod(new PasswordTransformationMethod());

        final TextInputLayout cityLayout = (TextInputLayout) findViewById(R.id.txt_city_name_layout);
        cityView = (AutoCompleteTextView) findViewById(R.id.txt_city_name);
        setCityAdapter();


        /*final String[] COUNTRIES = new String[] {
                "Belgium", "France", "Italy", "Germany", "Spain"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        cityView.setAdapter(adapter);*/


        /*final TextInputLayout promocodeLayout = (TextInputLayout) findViewById(R.id.txt_promocode_layout);
        final EditText promocodeView = (EditText) findViewById(R.id.txt_promocode);*/

        final TextView promoCodeShow = (TextView) findViewById(R.id.promo_code_show);
        final TextView promoMessage = (TextView) findViewById(R.id.promo_message);

        Button loginButton = (Button) findViewById(R.id.txt_regi_login);

        final Button applyPopButton = (Button) findViewById(R.id.apply_btn_popup);
        final Button registerButton = (Button) findViewById(R.id.btn_regi_submit);

        //Util.intializeCity(this, cityView);

        spinnerProfession = (AppCompatSpinner) findViewById(R.id.spinner_profession);
        setProfessionAdapter();
        spinnerProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String profession = adapterView.getItemAtPosition(i).toString();
                /*if (profession.equalsIgnoreCase(Constant.OTHERS)) {
                    etOtherProfession.setVisibility(View.VISIBLE);
                } else {
                    etOtherProfession.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        final AppCompatCheckBox tncCheckbox = (AppCompatCheckBox) findViewById(R.id.tncBox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_promocode, null);
        builder.setView(view);

        final TextInputLayout promocodeLayout = (TextInputLayout) view.findViewById(R.id.txt_promocode_layout);
        final EditText promocodeView = (EditText) view.findViewById(R.id.txt_promocode);
        Button applyPromo = (Button) view.findViewById(R.id.apply_btn);
        Button cancelPromo = (Button) view.findViewById(R.id.cancel_popup);
        final AlertDialog alertDialog = builder.create();

        applyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.hideKeyboard(RegisterActivity.this);

                if (Helper.validate(promocodeLayout, promocodeView, "text", RegisterActivity.this)) {
                    alertDialog.dismiss();
                    try {
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("promo", promocodeView.getText().toString());
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.VERIFY_PROMO, reqObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    Log.d("Response", "" + response.toString());

                                    JSONObject body = response.getJSONObject("body");

                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setTitle(R.string.txt_promo_code_applied);
                                    builder.setMessage(getString(R.string.txt_partner_name) + body.getString("name"));
                                    builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Helper.hideKeyboard(RegisterActivity.this);
                                        }
                                    });
                                    builder.show();

                                    promoMessage.setText(body.getString("name"));
                                    isApplied = true;
                                    leadPartnerId = body.getInt("id");
                                    applyPopButton.setVisibility(View.VISIBLE);
                                    applyPopButton.setText(R.string.txt_remove);
                                    applyPopButton.setTextColor(getResources().getColor(R.color.Red500));
                                    promoCodeShow.setText(promocodeView.getText().toString());


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                promoMessage.setText("");
                                applyPopButton.setVisibility(View.VISIBLE);
                                applyPopButton.setText(R.string.txt_apply_code);
                                applyPopButton.setTextColor(getResources().getColor(R.color.Green500));


                                NetworkResponse networkResponse = error.networkResponse;
                                try {
                                    JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                                    final String msg = errorObject.getString("msg");
                                    final String body = errorObject.getString("body");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setTitle(msg);
                                    builder.setMessage(body);
                                    builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();


                                    /*Helper.translate(msg, RegisterActivity.this, new Translator() {
                                        @Override
                                        public void onTranslate(final String value1) {
                                            Helper.translate(body, RegisterActivity.this, new Translator() {
                                                @Override
                                                public void onTranslate(String value2) {


                                                }
                                            });
                                        }
                                    });*/
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        queue.add(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        cancelPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        applyPopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isApplied) {
                    leadPartnerId = 0;
                    isApplied = false;
                    promoCodeShow.setText(R.string.promocode_helper_text);
                    applyPopButton.setText(R.string.txt_apply_code);
                    applyPopButton.setTextColor(getResources().getColor(R.color.Green500));
                    promoMessage.setText("");

                } else {
                    alertDialog.show();
                }


            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Helper.hideKeyboard(RegisterActivity.this);

                    if (!tncCheckbox.isChecked()) {
                        Toast.makeText(RegisterActivity.this, "Please accept terms and conditions", Toast.LENGTH_LONG).show();
                        return;
                    }


                    if (Helper.validate(fullNameLayout, fullNameView, "text", RegisterActivity.this) && Helper.validate(phoneLayout, phoneView, "phone", RegisterActivity.this) && Helper.validate(emailLayout, emailView, "email", RegisterActivity.this) && Helper.validate(passwordLayout, passwordView, "password", RegisterActivity.this) && Helper.validate(cityLayout, cityView, "text", RegisterActivity.this)) {

                        boolean flag = false;
                        for(int i=0;i<cityList.size();i++)
                        {
                            if(cityView.getText().toString().trim().toUpperCase().equals(cityList.get(i).toString().toUpperCase()))
                            {
                                flag=true;
                            }
                        }

                        if(!flag)
                        {
                            Toast.makeText(RegisterActivity.this, "Please select city from dropdown only", Toast.LENGTH_LONG).show();
                            return;
                        }

                        waitingLayout.setVisibility(View.VISIBLE);
                        animation.start();
                        registerButton.setVisibility(View.GONE);

                        Toast.makeText(RegisterActivity.this, "Registration Success, Trying to Login..", Toast.LENGTH_LONG).show();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("name", fullNameView.getText().toString().trim());
                        reqObject.put("email", emailView.getText().toString().trim());
                        reqObject.put("mobile_number", phoneView.getText().toString().trim());
                        reqObject.put("password", passwordView.getText().toString().trim());
                        reqObject.put("city", cityView.getText().toString().trim());
                        reqObject.put("profession", spinnerProfession.getSelectedItem().toString());
                        reqObject.put("source", "BA_APP");
                        reqObject.put("is_ba", "1");
                        if (isApplied) {
                            reqObject.put("lead_partner_id", leadPartnerId);
                            reqObject.put("promo_code", promocodeView.getText().toString());
                        }


                        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST, Util.REGISTER_NEW, reqObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                waitingLayout.setVisibility(View.GONE);
                                animation.stop();
                                registerButton.setVisibility(View.VISIBLE);

                                Toast.makeText(RegisterActivity.this, R.string.register_success_helper, Toast.LENGTH_LONG).show();

                                try {

                                    JSONObject loginObject = new JSONObject();
                                    loginObject.put("username", emailView.getText().toString().trim());
                                    loginObject.put("password", passwordView.getText().toString().trim());
                                    loginObject.put("remember", 1);
                                    loginObject.put("is_ba", "1");

                                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, Util.LOGIN, loginObject, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {
                                                if (response.getString("status_code").equals("2000")) {
                                                    JSONObject body = response.getJSONObject("body");
                                                    Util.o_id = body.getString("id");
                                                    Util.email = body.getString("email");
                                                    Util.mobile = body.getString("mobile_number");
                                                    Helper.setStringSharedPreference(Constant.USERID, body.getString("id"), RegisterActivity.this);
                                                    Helper.setStringSharedPreference(Constant.USERNAME, body.getString("email"), RegisterActivity.this);
                                                    Helper.setStringSharedPreference(Constant.NAME, body.getString("name"), RegisterActivity.this);
                                                    Helper.setStringSharedPreference(Constant.USER_CLASS, body.getString("ba_class"), RegisterActivity.this);
                                                    Helper.setStringSharedPreference(Constant.USER_PHONE, body.getString("mobile_number"), RegisterActivity.this);
                                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
                                            try {
                                                JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                                                final String msg = errorObject.getString("msg");
                                                final String body = errorObject.getString("body");

                                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                builder.setTitle(msg);
                                                builder.setMessage(body);
                                                builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.show();


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }) {

                                        @Override
                                        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                            Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), RegisterActivity.this);
                                            return super.parseNetworkResponse(response);
                                        }
                                    };

                                    queue.add(loginRequest);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                waitingLayout.setVisibility(View.GONE);
                                animation.stop();
                                registerButton.setVisibility(View.VISIBLE);
                                NetworkResponse networkResponse = error.networkResponse;
                                try {
                                    JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                                    final String msg = errorObject.getString("msg");
                                    final String body = errorObject.getString("body");
                                    Helper.translate(msg, RegisterActivity.this, new Translator() {
                                        @Override
                                        public void onTranslate(final String value1) {
                                            Helper.translate(body, RegisterActivity.this, new Translator() {
                                                @Override
                                                public void onTranslate(String value2) {

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                    builder.setTitle(value1);
                                                    builder.setMessage(value2);
                                                    builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }
                                    });


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }) {
                            @Override
                            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                Log.d("Network Response", new String(response.data));
                                return super.parseNetworkResponse(response);
                            }
                        };
                        registerRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
                        queue.add(registerRequest);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_tnc);
        final WebView webView = (WebView) dialog.findViewById(R.id.tncWebview);


        findViewById(R.id.tncLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(Util.TNC_URL);
                dialog.show();
            }
        });



        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Registration Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }

    private void setCityAdapter() {
        StringRequest request = new StringRequest(Util.FETCH_ALL_CITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cityList = new ArrayList<>();

                try {
                    JSONObject res = new JSONObject(response);
                    JSONArray data = res.getJSONObject("body").getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        if (!data.getJSONObject(i).getString("item_value").equals("Others"))
                            cityList.add(data.getJSONObject(i).getString("item_value").toUpperCase());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_dropdown_item_1line,cityList);
                    cityView.setAdapter(adapter);


                } catch (JSONException e) {
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

    private void setProfessionAdapter() {
        // TODO delet and fetch from server and delete  Util.occupation

        StringRequest request = new StringRequest(Util.FETCH_PROFESSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> occupation = new ArrayList<>();

                try {
                    JSONObject res = new JSONObject(response);
                    JSONArray data = res.getJSONObject("body").getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        if (!data.getJSONObject(i).getString("item_value").equals("Others"))
                            occupation.add(data.getJSONObject(i).getString("item_value"));
                    }
                    occupation.add("Others");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                professionAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, occupation);
                professionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProfession.setAdapter(professionAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);

    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.txt_referral_code:
//                break;
            case R.id.txt_regi_login:
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.img_regi_back:
                finish();
                break;
            case R.id.btn_regi_submit:
                if (txt_full_name.getText().toString().trim().isEmpty()){
                    txt_full_name.setError("Please enter full name");
                    txt_full_name.requestFocus();
                } else {
                    str_name = txt_full_name.getText().toString().trim();
                    if (txt_phone_number.getText().toString().trim().isEmpty()){
                        txt_phone_number.setError("Please enter mobile number");
                        txt_phone_number.requestFocus();
                    } else {
                        str_mobile = txt_phone_number.getText().toString().trim();
                        if(android.util.Patterns.PHONE.matcher(txt_phone_number.getText().toString().intern()).matches() && txt_phone_number.getText().toString().length()==10){
                            if (txt_email_adrs.getText().toString().trim().isEmpty()){
                                txt_email_adrs.setError("Please enter Email-ID");
                                txt_email_adrs.requestFocus();
                            } else {
                                str_email = txt_email_adrs.getText().toString().trim();
                                if(Patterns.EMAIL_ADDRESS.matcher(txt_email_adrs.getText().toString().trim()).matches()) {
                                    if (txt_regi_password.getText().toString().trim().isEmpty()) {
                                        txt_regi_password.setError("Please enter password");
                                        txt_regi_password.requestFocus();
                                    } else {
                                        str_password = txt_regi_password.getText().toString().trim();
                                        if (txt_city_name.getText().toString().trim().isEmpty()) {
                                            txt_city_name.setError("Please enter city name");
                                            txt_city_name.requestFocus();
                                        } else {
                                            str_city = txt_city_name.getText().toString().trim();
                                            Util.hideKeyboard(RegisterActivity.this);
                                            new HttpAsyncTask().execute();
                                        }
                                    }
                                } else {
                                    txt_email_adrs.setError("Please enter valid Email-ID");
                                    txt_email_adrs.requestFocus();
                                }
                            }
                        } else {
                            txt_phone_number.setError("Please enter valid mobile number");
                            txt_phone_number.requestFocus();
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

                HttpPost httpPost = new HttpPost(Util.REGISTER_NEW);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("name", str_name);
                jsonObject.accumulate("email", str_email);
                jsonObject.accumulate("mobile_number", str_mobile);
                jsonObject.accumulate("password", str_password);
                jsonObject.accumulate("city", str_city);
                jsonObject.accumulate("utoken", "");

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

                String status, msg = null;

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                    Logs.LogD("Output",result);
                    final JSONObject output1 = new JSONObject(result);
                    if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                        status = output1.getString("status_code");
                        msg = output1.getString("msg");

                        if (msg.equals("Success")) {
//                            JSONObject data = output1.getJSONObject("body");
                            result1 = getLogin();

                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setTitle("Message");
                                    try {
                                        builder.setMessage(output1.getString("body") + "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    builder.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
//                                            finish();
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    } else{
                        result1 = msg;
                    }

                }
                else
                    result1 = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                Log.d("Exception", e.toString());
            }

            // 11. return result
            return result1;
        }


        private String getLogin() {
            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.ROOT_URL_V2+"index.php/Customer/login");

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("username", str_email);
                jsonObject.accumulate("password", str_password);
                jsonObject.accumulate("remember", 1);
                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);
                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                Header[] cookieHeader = httpResponse.getHeaders("Set-Cookie");
                if (cookieHeader.length > 0) {
                    String[] str_header = cookieHeader[0].getValue().split(";");
                    String[] str_token = str_header[0].split("=");
                    str_utoken = str_token[1];
                }
                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();
                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                    Logs.LogD("Getlogin",result);
                }
                else
                    result = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Logs.LogD("Login result",result.toString());
            waiting_layout.setVisibility(View.GONE);
            animation.stop();

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("2000") && msg.equals("Success")) {

                        JSONObject data = output1.getJSONObject("body");
                        Util.o_id = data.getString("id");
                        Util.email = data.getString("email");
                        Util.mobile = data.getString("mobile_number");
                        try {
                            APIUtils.SendSMS sms=new APIUtils.SendSMS("1","","",RegisterActivity.this,data.getString("mobile_number"),"");
                            sms.executeOnExecutor(Util.threadPool);
                        }
                        catch (Exception e){
                            //DO Nothing
                        }

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(Util.username, Util.email);
                        editor.putString(Util.utoken, str_utoken);
                        editor.commit();
                        editor.apply();
                        //NotificationUtil.registerInBackground(Util.o_id,RegisterActivity.this);
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            } catch (JSONException e){

            }

        }
    }*/

}
