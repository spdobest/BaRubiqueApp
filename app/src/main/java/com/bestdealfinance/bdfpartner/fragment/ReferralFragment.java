package com.bestdealfinance.bdfpartner.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.LoginActivity;
import com.bestdealfinance.bdfpartner.activity.RegisterActivity;
import com.bestdealfinance.bdfpartner.adapter.ReferralAdapter;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.ReferralListModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by disha on 16/2/16.
 */
public class ReferralFragment extends Fragment implements View.OnClickListener {
//    String[] myDataset = {"CAR LOAN","HOME LOAN", "PERSONAL LOAN"};
//    ArrayList<HashMap<String,String>> myDataset;
    private RecyclerView recyclerview_referral;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Calendar myCalendar = Calendar.getInstance();
    DateFormatSymbols dfs = new DateFormatSymbols();
    String[] months = dfs.getMonths();
    SimpleDateFormat sdf;
    String val_start ="", val_end = "";
    ImageView line;
    private List<ReferralListModel> refferalList;
    Button referral_login,referral_register, referral_resend_verification;
    LinearLayout referral_list_msg, email_verifications;
    public String[] lead_id, name, email, phone, lead_state, product_type, payout_influencer, loan_key, loan_value, loan_eligible, date_created;
    LinearLayout referral_data_not_found;
    Spinner dashboard_data;
    LinearLayout waiting_layout, referral_main;
    ImageView progressBar;
    private AnimationDrawable animation;
    TextView no_data;
    SharedPreferences sharedpreferences;
    String[] user_data_duration = {"This Week","This Month", "This Year", "All"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_referral, null);
        sharedpreferences = getActivity().getSharedPreferences(Util.MY_PREFERENCES, Context.MODE_PRIVATE);
        referral_list_msg = (LinearLayout)view.findViewById(R.id.referral_list_msg);
        referral_data_not_found = (LinearLayout)view.findViewById(R.id.referral_data_not_found);
        dashboard_data = (Spinner)view.findViewById(R.id.dashboard_data);
        referral_main = (LinearLayout) view.findViewById(R.id.referral_main);
        email_verifications = (LinearLayout) view.findViewById(R.id.email_verifications);
        recyclerview_referral = (RecyclerView) view.findViewById(R.id.recyclerview_referral);
        waiting_layout = (LinearLayout) view.findViewById(R.id.waiting_layout);
        progressBar = (ImageView) view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        line= (ImageView) view.findViewById(R.id.line);
        referral_login = (Button)view.findViewById(R.id.referral_login);
        referral_register = (Button)view.findViewById(R.id.referral_register);
        referral_resend_verification = (Button)view.findViewById(R.id.referral_resend_verification);
        animation = (AnimationDrawable) progressBar.getBackground();
        no_data = (TextView)view.findViewById(R.id.no_data);
        no_data.setText("Your account must be verified to access the referral.  \n\nWe can resend the verification email to " + Util.email);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Util.user_data_duration);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dashboard_data.setAdapter(dataAdapter);
        dashboard_data.setSelection(3);

        dashboard_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setUpReferralData(dashboard_data.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        if(Util.isRegistered(getActivity()).equals("")){
            referral_main.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            referral_list_msg.setVisibility(View.VISIBLE);
        } else {
            referral_main.setVisibility(View.VISIBLE);
            referral_list_msg.setVisibility(View.GONE);
            recyclerview_referral.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerview_referral.setLayoutManager(mLayoutManager);


//            dashboard_data.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new DatePickerDialog(getActivity(), date, myCalendar
//                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                }
//            });
        }

        // specify an adapter (see also next example)
        sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        referral_login.setOnClickListener(this);
        referral_register.setOnClickListener(this);
        referral_resend_verification.setOnClickListener(this);

        return view;
    }

//    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear,
//                              int dayOfMonth) {
//            // TODO Auto-generated method stub
//            myCalendar.set(Calendar.YEAR, year);
//            myCalendar.set(Calendar.MONTH, monthOfYear);
//            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            setUpReferralData(monthOfYear, year);
//        }
//
//    };

    private void setUpReferralData(String selected_data_val) {

        val_end = myCalendar.get(Calendar.YEAR)+"-"+(myCalendar.get(Calendar.MONTH)+1)+"-"+myCalendar.get(Calendar.DAY_OF_MONTH);
        switch(selected_data_val){
            case "This Week":
                final int currentDayOfWeek = (myCalendar.get(Calendar.DAY_OF_WEEK) + 7 - myCalendar.getFirstDayOfWeek()) % 7;
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_YEAR, -currentDayOfWeek);
                val_start = myCalendar.get(Calendar.YEAR)+"-"+(myCalendar.get(Calendar.MONTH)+1)+"-"+c1.get(Calendar.DAY_OF_MONTH);
                break;
            case "This Month":
                val_start = myCalendar.get(Calendar.YEAR)+"-"+(myCalendar.get(Calendar.MONTH)+1)+"-1";
                break;
            case "This Year":
                val_start = myCalendar.get(Calendar.YEAR)+"-1-1";
                break;
            case "All":
                val_start = "";
                val_end = "";
                break;
        }
        new HttpAsyncTask().execute();
//        val_month = (month+1)+"";
//        val_year = year+"";
//        if (month >= 0 && month <= 11 ) {
//
//            dashboard_data.setText(months[month]+"-"+year);
//        }
//        if(!val_month.equals("") && !val_year.equals("")) {
//            referral_main.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.referral_login:
                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(i);
                getActivity().finish();
                break;
            case R.id.referral_register:
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
                break;
            case R.id.referral_resend_verification:
                new VerifyEmail().execute();
                break;
        }
    }

    private class VerifyEmail extends AsyncTask<Void, Void, String> {

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

                HttpPost httpPost = new HttpPost(Util.SEND_EMAIL);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + sharedpreferences.getString(Util.utoken, ""));

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
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
            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (msg.equalsIgnoreCase("Success")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Email verification");
                        alert.setCancelable(false);
                        alert.setMessage(getActivity().getResources().getString(R.string.msg_email_verification) +" "+ Util.email);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
//                                new CheckEmailOTP(input.getText().toString().trim()).execute();
                            }
                        });

                        alert.show();

                    }
                }
            } catch (JSONException e) {

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

                HttpPost httpPost = new HttpPost(Util.GET_LEAD_LIST);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("start_date", val_start);
                jsonObject.accumulate("end_date", val_end);

                json = jsonObject.toString();

                System.out.println("!!!!!!!!!!!!!!!!!!!!!Disha "+json);
                StringEntity se = new StringEntity(json);
                Logs.LogD("Request",json.toString());
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
            Logs.LogD("Referral Response",result);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!Disha result " + result);

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("4009")) {
                        email_verifications.setVisibility(View.VISIBLE);
                        referral_main.setVisibility(View.INVISIBLE);
                        line.setVisibility(View.GONE);
                    } else {
                        if (msg.equalsIgnoreCase("Success")) {
                            referral_main.setVisibility(View.VISIBLE);
                            recyclerview_referral.setVisibility(View.VISIBLE);
                            line.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = new JSONArray(output1.getString("body"));
                            lead_id = new String[jsonArray.length()];
                            name = new String[jsonArray.length()];
                            email = new String[jsonArray.length()];
                            phone = new String[jsonArray.length()];
                            lead_state = new String[jsonArray.length()];
                            product_type = new String[jsonArray.length()];
                            loan_key = new String[jsonArray.length()];
                            loan_value = new String[jsonArray.length()];
                            loan_eligible = new String[jsonArray.length()];
                            date_created = new String[jsonArray.length()];
                            refferalList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                ReferralListModel temp = new ReferralListModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                temp.setLead_id(jsonObject.optString("id"));
                                String firstname=jsonObject.optString("first_name","");
//                                String middle_name=jsonObject.optString("middle_name","");
                                String last_name="";
                                if (!jsonObject.isNull("last_name")) {
                                    last_name = jsonObject.optString("last_name", "");
                                }

                                firstname=firstname+" "+ last_name;
                                temp.setName(firstname);
                                temp.setEmail(jsonObject.optString("email"));
                                temp.setPhone(jsonObject.optString("phone"));
                                if (!jsonObject.optString("product_name","").equals("")){
                                    temp.setProduct_name(jsonObject.optString("product_name"));
                                }
                                else {
                                    temp.setProduct_name(Util.getProductNameById(jsonObject.optString("product_type_id")));
                                }
                                temp.setLead_state(jsonObject.optString("lead_state"));
                                temp.setProduct_type(jsonObject.optString("product_type_id"));
                                temp.setLoan_key(jsonObject.optString("loan_key"));
                                temp.setLoan_value(jsonObject.optString("loan_value"));
                                temp.setPayout(jsonObject.optString("projected_payout"));
                                temp.setLoan_eligible(jsonObject.optString("loan_amount_needed"));
                                temp.setRefID(jsonObject.optString("id"));
                                if (!jsonObject.isNull("date_created")){
                                    String date_c = timestamp_Date(jsonObject.optString("date_created"));
                                    temp.setDate_created(date_c);
                                }
                                refferalList.add(temp);

                            }
                            referral_data_not_found.setVisibility(View.GONE);
                            mAdapter = new ReferralAdapter(getActivity(), refferalList);
                            recyclerview_referral.setAdapter(mAdapter);
                        } else {
                            if (msg.equals("Failed")) {
    //                            if(output1.getString("body").equals("You have not made any referrals yet. Once you start making referrals you will be able to view them here.")){
                                referral_data_not_found.setVisibility(View.VISIBLE);
                                line.setVisibility(View.GONE);
//                                referral_data_not_found.setText(output1.getString("body"));
                                recyclerview_referral.setVisibility(View.GONE);
    //                            } else {
    //                                referral_data_not_found.setVisibility(View.GONE);
    //                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    //                                builder.setTitle("Message");
    //                                builder.setCancelable(false);
    //                                builder.setMessage(output1.getString("body"));
    //                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    //                                    public void onClick(DialogInterface dialog, int which) {
    //
    //                                    }
    //                                });
    //                                builder.show();
    //                            }
                            }
                        }
                    }
                }
            } catch (JSONException e) {

                Logs.LogD("Exception",e.getLocalizedMessage());
            }

        }
    }

    private String timestamp_Date(String timestamp){

        Logs.LogD("Date",timestamp);
        String dates[] = timestamp.split(" ");
        String[] subdate = dates[0].split("-");
        String day;
        String month;
        String year;
        day = subdate[2];
        month = subdate[1];
        year = subdate[0];
        String finaldate = convertDate(day, month);
        return finaldate;
    }

    private String convertDate(String day, String month) {
        String temp;
        switch (month) {
            case "01":
                temp = "Jan";
                break;
            case "02":
                temp = "Feb";
                break;
            case "03":
                temp = "Mar";
                break;
            case "04":
                temp = "Apr";
                break;
            case "05":
                temp = "May";
                break;
            case "06":
                temp = "June";
                break;
            case "07":
                temp = "July";
                break;
            case "08":
                temp = "Aug";
                break;
            case "09":
                temp = "Sept";
                break;
            case "10":
                temp = "Oct";
                break;
            case "11":
                temp = "Nov";
                break;
            case "12":
                temp = "Dec";
                break;
            default:
                temp = " ";
                break;
        }
        int date_day = Integer.parseInt(day);
        //     Log.d("day",""+date_day);
        day = String.valueOf(date_day);
        String final_date =  day + " " + temp;
        //String final_date = "Date Referred: " + day + "-" + temp;
        return final_date;
    }

}