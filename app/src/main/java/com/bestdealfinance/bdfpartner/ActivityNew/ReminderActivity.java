package com.bestdealfinance.bdfpartner.ActivityNew;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.AdapterReminder;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class ReminderActivity extends AppCompatActivity {

    private static final String TAG = "ReminderActivity";
    AdapterReminder adapterReminder;
    JSONArray jsonArrayReminderList = new JSONArray();
    RequestQueue jsonObjectRequest;
    String ba_reminder_id = "";
    String reminder = "";
    //database initialization
    private DB snappyDB;
    //widgets
    private RecyclerView recyclerViewReminder;
    private Toolbar new_toolbar;
    private ImageView toolbar_back_button;
    private AppCompatTextView textViewNoReminder;

    private JSONArray  jsonArrayForAdapter;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReminderActivity.this, MainActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //initialize viiews
        initView();

        Intent intent = getIntent();
        if (intent.hasExtra("reminder")) {
            reminder = intent.getExtras().getString("reminder");
        }

        jsonObjectRequest = Volley.newRequestQueue(ReminderActivity.this);

        JSONObject jsonObject = null;


        getBundleData(reminder);

        /* Helper.showAlertDialog(this, "", "Do you want to continue ? ", "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doReminderApiCall("",0);
            }
        }, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/


        try {
            snappyDB = DBFactory.open(getApplicationContext());
            if (snappyDB.exists(Constant.DB_REMINDER)) {
                JSONObject jsonObjectReminderFromDB = new JSONObject(snappyDB.get(Constant.DB_REMINDER));
                if (jsonObjectReminderFromDB.has("reminderArray")) {
                    jsonArrayReminderList = jsonObjectReminderFromDB.getJSONArray("reminderArray");

                }
            } else {
                try {
                    jsonObject = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("abcd", "asdasdasd");
                    for (int i = 0; i < 5; i++)
                        jsonArray.put(jsonObject1);

                    jsonObject.put("arr", jsonArray);

                    Log.i(TAG, "onCreate: " + jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // snappyDB.put(Constant.DB_REMINDER, jsonObject.toString());
            }

            snappyDB.close();
        } catch (SnappydbException e) {
            e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        recyclerViewReminder = (RecyclerView) findViewById(R.id.recyclerViewReminder);
        recyclerViewReminder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        new_toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        toolbar_back_button = (ImageView) findViewById(R.id.toolbar_back_button);
        textViewNoReminder = (AppCompatTextView) findViewById(R.id.textViewNoReminder);

        ToolbarHelper.initializeToolbar(ReminderActivity.this, new_toolbar, "Lead Reminders", false, true, true);
        toolbar_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getBundleData(final String strReminder) {
        textViewNoReminder.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(strReminder)) {
            try {
                JSONObject jsonObjectReminder = new JSONObject(strReminder);
                jsonObjectReminder.put("isApicallDone", false);
                ba_reminder_id = jsonObjectReminder.optString("ba_reminder_id");
                snappyDB = DBFactory.open(getApplicationContext());
                if (snappyDB.exists(Constant.DB_REMINDER)) {
                    JSONArray jsonArrayFromDb = new JSONArray(snappyDB.get(Constant.DB_REMINDER));
                     jsonArrayFromDb.put(jsonObjectReminder);
                    snappyDB.put(Constant.DB_REMINDER, jsonArrayFromDb.toString());
                    setReminderAdapter(jsonArrayFromDb,-1);

                } else {

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObjectReminder);

                    snappyDB.put(Constant.DB_REMINDER, jsonArray.toString());

                    setReminderAdapter(jsonArray,-1);

                }
                snappyDB.close();
            } catch (SnappydbException e) {
                e.getMessage();
            } catch (JSONException e) {
                e.getMessage();
                textViewNoReminder.setVisibility(View.VISIBLE);
            } finally {
                try {
                    if (snappyDB != null && snappyDB.isOpen())
                        snappyDB.close();
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                snappyDB = DBFactory.open(getApplicationContext());
                if (snappyDB.exists(Constant.DB_REMINDER)) {
                    JSONArray jsonArrayFromDb = new JSONArray(snappyDB.get(Constant.DB_REMINDER));
                    snappyDB.put(Constant.DB_REMINDER, jsonArrayFromDb.toString());
                    setReminderAdapter(jsonArrayFromDb,-1);

                } else
                    textViewNoReminder.setVisibility(View.VISIBLE);
            } catch (SnappydbException e) {
                e.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void doReminderApiCall(String ba_reminder_id, final int position,final String leadId) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(ReminderActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("consent", "YES");
            reqObject.put("ba_reminder_id", ba_reminder_id);
            reqObject.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, this));
            Helper.showLog(URL.UPDATE_CONSENT,"params "+reqObject.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.UPDATE_CONSENT, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        if (response.getInt("status_code") == 2000 || response.getString("msg").equalsIgnoreCase("Success")) {
                            try {
                                snappyDB = DBFactory.open(getApplicationContext());
                                if (snappyDB.exists(Constant.DB_REMINDER)) {
                                    JSONArray jsonArrayFromDb = new JSONArray(snappyDB.get(Constant.DB_REMINDER));
                                    if (position < jsonArrayFromDb.length()) {

                                          for(int i = 0;i<jsonArrayFromDb.length();i++){
                                            JSONObject jsonObjectInner = jsonArrayFromDb.getJSONObject(i);
                                             Log.i(TAG, "onResponse: json "+jsonObjectInner.toString());

                                            if(jsonObjectInner.has("lead_id") && jsonObjectInner.getString("lead_id").equals(leadId)){
                                                Log.i(TAG, "onResponse: loop ");
                                                jsonObjectInner.put("isApicallDone",true);
                                                jsonArrayFromDb.put(position,jsonObjectInner);
                                                Log.i(TAG, "onResponse: json1 "+jsonObjectInner.toString());
                                            }
                                        }



                                        /*JSONObject jsonObjectInner = jsonArrayFromDb.getJSONObject(position);
                                        jsonObjectInner.put("isApicallDone", true);
                                        jsonArrayFromDb.put(position, jsonObjectInner);*/


                                        setReminderAdapter(jsonArrayFromDb,position);

                                        snappyDB.put(Constant.DB_REMINDER, jsonArrayFromDb.toString());
                                        snappyDB.close();
                                    }
                                }
                            } catch (SnappydbException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.i(TAG, "onErrorResponse: "+error.getMessage());

                }
            });
            jsonObjectRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setReminderAdapter(JSONArray jsonArray,int position){
        Log.i(TAG, "setReminderAdapter: "+jsonArray.toString());
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                jsonArrayForAdapter = new JSONArray();
                HashSet listLeadId = new HashSet();
                for (int j = jsonArray.length() - 1; j >= 0; j--) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    if (listLeadId.add(jsonObject.getString("lead_id")) && !jsonObject.getBoolean("isApicallDone")) {
                        jsonArrayForAdapter.put(jsonObject);
                    }
                }
            }
        }
        catch (JSONException  e){
            e.printStackTrace();
        }
        if(jsonArrayForAdapter.length()>0) {
            recyclerViewReminder.setVisibility(View.VISIBLE);

            if(adapterReminder!=null && position!=-1) {
                adapterReminder.notifyItemRemoved(position);
                adapterReminder.refreshAfterRemove(position);
            }
            else {
                adapterReminder = new AdapterReminder(ReminderActivity.this, jsonArrayForAdapter);
                recyclerViewReminder.setAdapter(adapterReminder);
            }
        }
        else{
            recyclerViewReminder.setVisibility(View.INVISIBLE);
            textViewNoReminder.setVisibility(View.VISIBLE);
        }
    }

}
