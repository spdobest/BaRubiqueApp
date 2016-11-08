package com.bestdealfinance.bdfpartner.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.InnerDurationFragment;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class OnBoardingActivity extends AppCompatActivity implements View.OnClickListener {

    EditText is_salaried, occupation, et_income, et_tenure,et_year_income;
    AutoCompleteTextView company_name;
    TextInputLayout textinputlayout_salaried, textinputlayout_company, textinputlayout_occupation, textinputlayout_income, textinputlayout_tenure, txt_input_layout_year_income;
    ArrayAdapter adapter;
    HashMap<String,String> hashMap=new HashMap<>();
    List<String> company_list;
    ImageView back_arrow;
    Button btn_boarding_submit;
    Bundle bundle;
    String product_type_id, loan_amount;
    String str_salaried, str_company,str_comp_id, str_occupation, str_income, str_tenure, str_salaried_value, str_yearly_income;
    ImageView progressBar;
    LinearLayout waiting_layout;
    SharedPreferences preferences;
    private AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_on_boarding);

        Intent i = getIntent();
        bundle = i.getExtras();
        Logs.LogD("bundle",bundle.toString());
        product_type_id = bundle.getString("type", "");
        loan_amount = bundle.getString("amount", "");
        initialization();
        is_salaried.setOnClickListener(this);
        back_arrow.setOnClickListener(this);
        btn_boarding_submit.setOnClickListener(this);

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Application Fill Step 1");
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

        textinputlayout_salaried  = (TextInputLayout)findViewById(R.id.textinputlayout_salaried);
        textinputlayout_occupation  = (TextInputLayout)findViewById(R.id.textinputlayout_occupation);
        textinputlayout_company  = (TextInputLayout)findViewById(R.id.textinputlayout_company);
        textinputlayout_income  = (TextInputLayout)findViewById(R.id.textinputlayout_income);
        textinputlayout_tenure  = (TextInputLayout)findViewById(R.id.textinputlayout_tenure);
        txt_input_layout_year_income= (TextInputLayout) findViewById(R.id.textinputlayout_yearly_income);

        et_income = (EditText)findViewById(R.id.et_income);

        TextWatcher watcher = new TextWatcher() {
            String old="";
            EditText editText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_income.removeTextChangedListener(this);
                Logs.LogD("Watcher", "Text CHnged");
                String amount = s.toString();


                if (amount == null || amount.equals("null") || amount.equals("")) {

                }
                else {
                    try {
                        amount=amount.replaceAll(",","");
                        et_income.setText(Util.formatRs(amount));
                        et_income.setSelection(et_income.getText().length());
                        old=amount;
                    } catch (NumberFormatException n) {
                        Logs.LogD("Exception", n.getLocalizedMessage());
                    }
                }
                et_income.addTextChangedListener(this);
            }
        };
        TextWatcher watcher2 = new TextWatcher() {
            String old="";
            EditText editText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_year_income.removeTextChangedListener(this);
                Logs.LogD("Watcher", "Text CHnged");
                String amount = s.toString();


                if (amount == null || amount.equals("null") || amount.equals("")) {

                }
                else {
                    try {
                        amount=amount.replaceAll(",","");
                        et_year_income.setText(Util.formatRs(amount));
                        et_year_income.setSelection(et_year_income.getText().length());
                        old=amount;
                    } catch (NumberFormatException n) {
                        Logs.LogD("Exception", n.getLocalizedMessage());
                    }
                }
                et_year_income.addTextChangedListener(this);
            }
        };
        et_income.addTextChangedListener(watcher);
        et_year_income= (EditText) findViewById(R.id.et_income_year);
        et_year_income.addTextChangedListener(watcher2);
        et_tenure = (EditText)findViewById(R.id.et_tenure);

        is_salaried  = (EditText)findViewById(R.id.is_salaried);
        company_name = (AutoCompleteTextView) findViewById(R.id.company);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Logs.LogD("company", s.toString());
                if (s.length() == 3) {
                    GetCompany getCompany = new GetCompany();
                    getCompany.executeOnExecutor(Util.threadPool);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        company_name.addTextChangedListener(textWatcher);
        company_name.setThreshold(3);
        occupation = (EditText)findViewById(R.id.occupation);
        occupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleChoiceOccupation(OnBoardingActivity.this, "Occupation", occupation, Util.getOccupation());
            }
        });
        occupation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    SingleChoiceOccupation(OnBoardingActivity.this, "Occupation", occupation, Util.getOccupation());
                }
            }
        });
        back_arrow = (ImageView)findViewById(R.id.back_arrow);
        btn_boarding_submit = (Button)findViewById(R.id.btn_boarding_submit);
        et_tenure.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_tenure.setError(null);
                    InnerDurationFragment dateFragment = new InnerDurationFragment(et_tenure, getApplicationContext());
                    dateFragment.show(getSupportFragmentManager(), "date");
                }
            }
        });
        et_tenure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_tenure.setError(null);
                InnerDurationFragment dateFragment = new InnerDurationFragment(et_tenure, getApplicationContext());
                dateFragment.show(getSupportFragmentManager(), "date");

            }
        });
        checkForVisibility();
    }

    private void checkForVisibility() {
        switch (product_type_id){
            case "11":
                is_salaried.setText("Yes");
                textinputlayout_company.setHint("Company/Organization");
//                company_name.setHint("Company/Organization");
                textinputlayout_company.setVisibility(View.VISIBLE);
                textinputlayout_company.setFocusable(true);
                textinputlayout_occupation.setVisibility(View.GONE);
                textinputlayout_income.setVisibility(View.VISIBLE);
                textinputlayout_tenure.setVisibility(View.GONE);


                break;
            case "22"://CL
            case "23"://TW
            case "26"://HL
            case "28"://LAP
                is_salaried.setText("Yes");
                textinputlayout_company.setVisibility(View.VISIBLE);
                textinputlayout_company.setFocusable(true);
                break;
            case "25"://PL
                is_salaried.setText("Yes");
                is_salaried.setClickable(false);
                is_salaried.setFocusable(false);
                textinputlayout_company.setVisibility(View.VISIBLE);
                textinputlayout_company.setFocusable(true);
                textinputlayout_occupation.setVisibility(View.GONE);
                break;
            case "39"://BL
                is_salaried.setText("No");
                is_salaried.setClickable(false);
                is_salaried.setFocusable(false);
                textinputlayout_occupation.setVisibility(View.VISIBLE);
                textinputlayout_company.setVisibility(View.GONE);
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.is_salaried:
                SingleChoiceSalaried(OnBoardingActivity.this, "Are You Salaried?", is_salaried, Util.yes_no);
                break;
            case R.id.back_arrow:
                finish();
                break;
            case R.id.btn_boarding_submit:
                if(is_salaried.getText().toString().isEmpty()){
                    is_salaried.setError("Please select salaried option");
                    is_salaried.requestFocus();
                } else {
                    str_salaried = is_salaried.getText().toString().trim();
                    if(str_salaried.equals("Yes")){
                        str_salaried_value = "Salaried";
                        if(company_name.getText().toString().trim().isEmpty()){
                            company_name.setError("Please enter company name");
                            company_name.requestFocus();
                        } else {
                            str_company = company_name.getText().toString().trim();
                            str_comp_id=hashMap.get(str_company);
                            checkForIncomeAndTenure();
                        }
                    } else if(str_salaried.equals("No")){
                        str_salaried_value = "No";
                        if(occupation.getText().toString().trim().isEmpty()){
                            occupation.setError("Please select occupation");
                            occupation.requestFocus();
                        } else {
                            str_occupation = occupation.getText().toString().trim();
                            checkForIncomeAndTenureSelf();
                        }
                    }
                }
                break;
        }
    }

    private void checkForIncomeAndTenure() {
        if(et_income.getText().toString().trim().isEmpty()){
            et_income.setError("Please enter your income");
            et_income.requestFocus();
        } else {
            str_income = et_income.getText().toString().trim();
            if(et_tenure.getText().toString().trim().isEmpty() && !product_type_id.equals("11")){
                et_tenure.setError("Please enter tenure");
                et_tenure.requestFocus();
            } else {
                str_tenure = et_tenure.getText().toString().trim();
                new HttpAsyncTask().execute();
            }
        }
    }

    private void checkForIncomeAndTenureSelf() {
        if(et_year_income.getText().toString().trim().isEmpty()){
            et_year_income.setError("Please enter your income");
            et_year_income.requestFocus();
        } else {
            str_income = et_income.getText().toString().trim();
            if(et_tenure.getText().toString().trim().isEmpty() && !product_type_id.equals("11")){
                et_tenure.setError("Please enter tenure");
                et_tenure.requestFocus();
            } else {
                str_tenure = et_tenure.getText().toString().trim();
                new HttpAsyncTask().execute();
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

                HttpPost httpPost = new HttpPost(Util.CREATE_INCOMPLETE_LEAD);
                httpPost.addHeader("Cookie", "utoken=" + Util.isRegistered(getApplicationContext()));
                String json = "";
                JSONObject jsonObject = new JSONObject();
                JSONArray tupleList=new JSONArray();
                JSONObject tuple=new JSONObject();

                if (str_salaried_value.equals("No")){
                    tuple.put("base_id","38");
                    tuple.put("field_value",str_occupation);
                    tuple.put("list_item_id",getItemIDSalaried(str_occupation));
                    tupleList.put(tuple);
                    tuple=new JSONObject();
                    tuple.put("base_id","47");
                    tuple.put("field_value",str_income.replaceAll(",",""));
                    tuple.put("list_item_id","");
                    tupleList.put(tuple);
                }
                else {
                    tuple.put("base_id","38");
                    tuple.put("field_value",str_salaried_value);
                    tuple.put("list_item_id",getItemIDSalaried(str_salaried_value));
                    tupleList.put(tuple);
                    tuple=new JSONObject();
                    tuple.put("base_id","42");
                    tuple.put("field_value",str_income.replaceAll(",",""));
                    tuple.put("list_item_id","");
                    tupleList.put(tuple);
                }

                tuple=new JSONObject();
                tuple.put("base_id","115");
                tuple.put("field_value",loan_amount);
                tuple.put("list_item_id","");
                tupleList.put(tuple);

                tuple=new JSONObject();
                tuple.put("base_id","25");
                tuple.put("field_value", bundle.getString("city",""));
                tuple.put("list_item_id","");
                tupleList.put(tuple);

                tuple=new JSONObject();
                tuple.put("base_id","40");
                tuple.put("field_value",str_company);
                tuple.put("list_item_id",str_comp_id);
                tupleList.put(tuple);

                tuple=new JSONObject();
                tuple.put("base_id", "116");
                tuple.put("field_value", str_tenure);
                tuple.put("list_item_id", "");
                tupleList.put(tuple);
                //TODO For Phone=5
                tuple=new JSONObject();
                tuple.put("base_id", "5");
                tuple.put("field_value", bundle.getString("phone",""));
                tuple.put("list_item_id", "");
                tupleList.put(tuple);
                //TODO For Email=6
                tuple=new JSONObject();
                tuple.put("base_id", "6");
                tuple.put("field_value",  bundle.getString("email",""));
                tuple.put("list_item_id", "");
                tupleList.put(tuple);
                //TODO For First Name=2
                String name=bundle.getString("name"," ");
                String names[]=name.split(" ");
                tuple=new JSONObject();
                tuple.put("base_id", "2");
                tuple.put("field_value", names[0]);
                tuple.put("list_item_id", "");
                tupleList.put(tuple);
                //TODO Last Name =4
                if (names.length>1) {
                    tuple = new JSONObject();
                    tuple.put("base_id", "4");
                    tuple.put("field_value", names[names.length - 1]);
                    tuple.put("list_item_id", "");
                    tupleList.put(tuple);
                }


                jsonObject.accumulate("product_type_sought", product_type_id);
                jsonObject.put("tuple_list", tupleList);

                json = jsonObject.toString();
                Logs.LogD("Request", json);
                StringEntity se = new StringEntity(json);
                // 6. set httpPost Entity

                Logs.LogD("Utoken",Util.isRegistered(getApplicationContext()));
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.addHeader("Accept", "application/json");
                httpPost.addHeader("Content-type", "application/json");



                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                Header[] headers = httpResponse.getAllHeaders();
                for (Header header : headers) {
//                    Logs.LogD("Headers : ", header.getName() + " ,Value : " + header.getValue());
                    if (header.getName().equals("ltoken")){
                        Util.ltoken=header.getValue();
                        Logs.LogD("ltoken",Util.ltoken);
                    }
                }

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
            Logs.LogD("response",result);
            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if(msg.equals("Success")){
                        JSONObject body=output1.optJSONObject("body");
                        String lead_id=body.optString("lead_id");
                        bundle.putString("type", product_type_id);
                        bundle.putString("lead_id", lead_id);
                        bundle.putString("tenure",str_tenure);
                        Intent i = new Intent(getApplicationContext(), OffersActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    }

                }
            } catch (JSONException e) {

            }
        }
    }

    private String getItemIDSalaried(String value){
        String item_id="";
        switch (value){
            case "Salaried": item_id= "23"; break;
            case "Housewife": item_id= "23"; break;
            case "Retired": item_id= "25"; break;
            case "Self Employed": item_id= "24"; break;
            case "Self Employed Professional": item_id= "23"; break;
            case "Self Employed Professional Doctor": item_id= "23"; break;
            case "Student": item_id= "23"; break;
        }
        return item_id;
    }

    final class GetCompany extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                URL url = new URL(Util.COMPANYLISTV2);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject post = new JSONObject();
                post.put("list_id","1078");
                post.put("keyword", company_name.getText().toString());
                Logs.LogD("Request", post.toString());
                Logs.LogD("Refer", "Sent the Request");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(post.toString());
                writer.flush();
                writer.close();
                os.close();
                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    response = sb.toString();
                } else {
                    Logs.LogD("Exception", conn.getResponseMessage());
                }
            } catch (Exception e) {
                Logs.LogD("Task Exception" +
                        "on", e.getLocalizedMessage());
                e.printStackTrace();
                response = e.getLocalizedMessage();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return response;
        }
        protected void onPostExecute(String result) {
            Logs.LogD("Cities", result);
            try {
                JSONObject data = new JSONObject(result);
                JSONArray body=data.optJSONArray(("body"));
                company_list = new ArrayList<>();
                for (int i = 0; i < body.length(); i++) {
                    JSONObject temp=body.optJSONObject(i);
                    company_list.add(
                                    temp.getString("itemValue")
                    );
                    hashMap.put(temp.getString("itemValue"),temp.getString("id"));
                }
                adapter = new ArrayAdapter(OnBoardingActivity.this, android.R.layout.simple_dropdown_item_1line,company_list );
                company_name.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void SingleChoiceSalaried(FragmentActivity activity, String title, final TextView txtView, final String[] arrList) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(arrList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        txtView.setText(arrList[whichButton]);
                        if (arrList[whichButton].equals("No")) {
                            textinputlayout_occupation.setVisibility(View.VISIBLE);
                            textinputlayout_company.setVisibility(View.GONE);
                            txt_input_layout_year_income.setVisibility(View.VISIBLE);
                            textinputlayout_income.setVisibility(View.GONE);
                        } else {
                            textinputlayout_company.setVisibility(View.VISIBLE);
                            textinputlayout_occupation.setVisibility(View.GONE);
                            txt_input_layout_year_income.setVisibility(View.GONE);
                            textinputlayout_income.setVisibility(View.VISIBLE);
                        }
                    }
                }).show();
    }

    public void SingleChoiceOccupation(FragmentActivity activity, String title, final TextView txtView, final String[] arrList) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(arrList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        txtView.setText(arrList[whichButton]);
                    }
                }).show();    }
}
