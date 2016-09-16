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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
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
import com.bestdealfinance.bdfpartner.UI.PercentView;
import com.bestdealfinance.bdfpartner.activity.LoginActivity;
import com.bestdealfinance.bdfpartner.activity.RegisterActivity;
import com.bestdealfinance.bdfpartner.adapter.StatusAdapter;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.StatusModel;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by disha on 16/2/16.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener {

    LinearLayout dashboard_data_not_found;
            Spinner dashboard_data;
    String val_start ="", val_end = "";
    private AnimationDrawable animation;
    LinearLayout waiting_layout, dashboard_login_msg, dashboard_main, dashboard_main1;
    ImageView progressBar;
    //PieChart dashboard_chart;
    String[] val_count, val_name, val_color;
    RecyclerView status_list;
    Bundle piedata=new Bundle();
    Bundle piecolor=new Bundle();
    int total_count,lead_count;
    PercentView percent_apps;
    List<StatusModel> statusModelList;
    ArrayList<Integer> count=new ArrayList<>();
    ArrayList<String> colors=new ArrayList<>();
    String val_month ="";
    String val_year = "";
    TextView ap0,ap1,ap2,ap3,ap4,ap5,ap6,ap7,one0,one1,one2,one3,one4,one5,one6,one7,onet;
    ImageView c1,c2,c3,c4,c5,c6,c7,c8;
    Calendar myCalendar = Calendar.getInstance();
    DateFormatSymbols dfs = new DateFormatSymbols();
    String[] months = dfs.getMonths();
    ArrayList<TextView> dcounts, dnames;
    ArrayList<ImageView> dcolors;
    LinearLayout l1,l2,l3,l4,l5,l6,l7,l8, email_verifications;
    ArrayList<LinearLayout> dlayouts;
    SharedPreferences sharedpreferences;
    TextView no_data;
    Button dashboard_login,dashboard_register, referral_resend_verification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, null);
        sharedpreferences = getActivity().getSharedPreferences(Util.MY_PREFERENCES, Context.MODE_PRIVATE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Profile");
        percent_apps = (PercentView) view.findViewById(R.id.percentview_apps);
        dashboard_login_msg = (LinearLayout)view.findViewById(R.id.dashboard_login_msg);
        dashboard_data_not_found = (LinearLayout)view.findViewById(R.id.dashboard_data_not_found);
        dashboard_data = (Spinner)view.findViewById(R.id.dashboard_data);
        //dashboard_chart = (PieChart) view.findViewById(R.id.dashboard_chart);
        dashboard_main1 = (LinearLayout)view.findViewById(R.id.dashboard_main1);
        dashboard_main = (LinearLayout)view.findViewById(R.id.dashboard_main);
        email_verifications = (LinearLayout)view.findViewById(R.id.email_verifications);
        waiting_layout = (LinearLayout)view.findViewById(R.id.waiting_layout);
        progressBar = (ImageView)view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        dashboard_login = (Button)view.findViewById(R.id.dashboard_login);
        dashboard_register = (Button)view.findViewById(R.id.dashboard_register);
        referral_resend_verification = (Button)view.findViewById(R.id.referral_resend_verification);
        status_list= (RecyclerView) view.findViewById(R.id.status_list);
        status_list.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        no_data = (TextView)view.findViewById(R.id.no_data);
        no_data.setText("Your account must be verified to access the dashboard.  \n\nWe can resend the verification email to " + Util.email);

        onet = (TextView) view.findViewById(R.id.totalApps);

        Logs.LogD("Performance", "Step5");
        animation = (AnimationDrawable) progressBar.getBackground();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Util.user_data_duration);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dashboard_data.setAdapter(dataAdapter);
        dashboard_data.setSelection(3);

        if(sharedpreferences.getString(Util.utoken, "").equals("")){
            dashboard_main.setVisibility(View.GONE);
            dashboard_login_msg.setVisibility(View.VISIBLE);
        } else {
//            setUpGraph(myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.YEAR));
            setUpDashboardData(dashboard_data.getSelectedItem().toString());
        }

        dashboard_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUpDashboardData(dashboard_data.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dashboard_login.setOnClickListener(this);
        dashboard_register.setOnClickListener(this);
        referral_resend_verification.setOnClickListener(this);
        return view;
    }

    private void setUpDashboardData(String selected_data_val) {

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
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dashboard_login:
                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(i);
                getActivity().finish();
                break;
            case R.id.dashboard_register:
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

    private void setUpGraph(int month, int year) {

        dashboard_data_not_found.setVisibility(View.GONE);
        dashboard_main.setVisibility(View.GONE);
        val_month = (month+1)+"";
        val_year = year+"";

        if(!val_month.equals("") && !val_year.equals("")) {
            dashboard_main.setVisibility(View.VISIBLE);
            new HttpAsyncTask().execute();
        }
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
//            setUpGraph(monthOfYear, year);
//        }
//
//    };

    /*private void setData(int count, float range) {

        float mult = range;

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count ; i++) {
            yVals1.add(new Entry((float) (Integer.parseInt(val_count[i])* mult) + mult / 5, i));
        }

        for (int i = 0; i < count; i++){
            xVals.add(val_name[i]+" ("+val_count[i]+")");
            colors.add(Color.parseColor(val_color[i]));
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(5f);
        dataSet.setSelectionShift(3f);
        dataSet.setDrawValues(false);
        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        data.setDrawValues(false);

        dashboard_chart.getLegend().setEnabled(true);
        dashboard_chart.setData(data);
        dashboard_chart.setDrawSliceText(false);

    }
*/
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

                HttpPost httpPost = new HttpPost(Util.GET_DASHBOARD);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("start_date", val_start);
                jsonObject.accumulate("end_date", val_end);

                json = jsonObject.toString();
                System.out.println("!!!!!!!!!!!!!!!!!!!!!Disha "+json);

                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);
                Logs.LogD("URL",Util.GET_DASHBOARD);
                Logs.LogD("Request", json.toString());

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + sharedpreferences.getString(Util.utoken, ""));
                Logs.LogD("utoken",sharedpreferences.getString(Util.utoken,""));
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
            Logs.LogD("Dashboard",result);
            super.onPostExecute(result);
            waiting_layout.setVisibility(View.GONE);
            animation.stop();

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (status.equals("4009")) {
                        email_verifications.setVisibility(View.VISIBLE);
                        dashboard_main.setVisibility(View.GONE);
                    } else {
                        if (msg.equalsIgnoreCase("Success")) {
                            email_verifications.setVisibility(View.GONE);
                            dashboard_main.setVisibility(View.VISIBLE);
                            count = new ArrayList<>();
                            colors = new ArrayList<>();
                            dashboard_main1.setVisibility(View.VISIBLE);
                            dashboard_data_not_found.setVisibility(View.GONE);
                            JSONArray jsonArray = new JSONArray(output1.getString("body"));
                            val_count = new String[jsonArray.length()];
                            val_name = new String[jsonArray.length()];
                            val_color = new String[jsonArray.length()];
                            total_count = 0;
                            lead_count = 0;

                            statusModelList=new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                lead_count++;
                                JSONObject jObj = jsonArray.getJSONObject(i);
                                StatusModel temp=new StatusModel();
                                temp.setCount(jObj.getString("count"));
                                temp.setStatus(jObj.getString("name"));
                                temp.setColor(jObj.getString("color"));
                                statusModelList.add(temp);
                                count.add(Integer.parseInt(jObj.getString("count")));
                                colors.add(jObj.getString("color"));
                                val_count[i] = jObj.getString("count");
                                val_name[i] = jObj.getString("name");
                                val_color[i] = jObj.getString("color");
//                                dcounts.get(i).setText(val_count[i]);
//                                dnames.get(i).setText(String.format("%s : ", val_name[i]));
//                                dcolors.get(i).setBackgroundColor(Color.parseColor(val_color[i]));
                                total_count = total_count + Integer.parseInt(val_count[i]);
//                                dlayouts.get(i).setVisibility(View.VISIBLE);
                            }
                            StatusAdapter adapter=new StatusAdapter(getContext(),statusModelList);
                            status_list.setAdapter(adapter);
                            onet.setText("" + total_count);
                            Logs.LogD("Performance", "Returning View");
                            percent_apps.setData(count, colors, total_count);

                            percent_apps.invalidate();

                        } else {
                            if (msg.equals("Failed")) {
                                dashboard_data_not_found.setVisibility(View.VISIBLE);
//                                dashboard_data_not_found.setText(output1.getString("body"));
                                dashboard_main1.setVisibility(View.GONE);

                            }
                        }
                    }
                }
            } catch (JSONException e) {

            }

        }
    }

//    public int getColor1(String lead_state){
//        switch (lead_state){
//            case "FRESH":
//                return R.color.lead_fresh;
//            case "INPRIN_APPROV":
//                return R.color.lead_inprin_approv;
//            case "ASSIGNED":
//                return R.color.lead_assigned;
//            case "APPT_FIXED":
//                return R.color.lead_appt_fixed;
//            case "DOC_COLLECTED":
//                return R.color.lead_doc_collected;
//            case "DISB_DOC_COLLECTED":
//                return R.color.lead_disb_doc_collected;
//            case "FAC_VERIFIED":
//                return R.color.lead_fac_verified;
//            case "VERIFIED":
//                return R.color.lead_verified;
//            case "FINBANK":
//                return R.color.lead_finbank;
//            case "APPROVED":
//                return R.color.lead_approval;
//            case "DISBURSED":
//                return R.color.lead_disbursal;
//            case "PAY_COMPUTED":
//                return R.color.lead_pay_computed;
//            case "PAID":
//                return R.color.lead_paid;
//            case "CLOSE":
//                return R.color.lead_cloase;
//            default:
//                return 0;
//        }
//    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString(total_count+"\nLeads");
//        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
//        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
//        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
//        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

}