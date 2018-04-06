package com.bestdealfinance.bdfpartner.ActivityNew;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.MeetingAdapter;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.base.BaseActivity;
import com.bestdealfinance.bdfpartner.base.SnackbarCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siba.prasad on 23-12-2016.
 */

public class MeetingActivity extends BaseActivity implements View.OnClickListener, SnackbarCallback {
    public static final String TAG = "MeetingActivity";

    private static final int SIGN_REQUEST = 1;
    private RequestQueue queue;
    // widgets declaration
    private LinearLayout linearLayoutRootMeeting;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private ProgressDialog progressDialog;
    private ImageView nextDate, previousDate;
    private TextView tvMonth, tvDate, tvNoMeeting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_meeting_new);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

        // initialize all the views
        initViews();
        // call the api
        if (Helper.isNetworkAvailable(this))
            getMeetingDetailsFromServer();
        else
            Helper.showSnackBar(this, linearLayoutRootMeeting, getResources().getString(R.string.no_connection), "Retry");


    }

    @Override
    public void initViews() {
        linearLayoutRootMeeting = (LinearLayout) findViewById(R.id.linearLayoutRootMeeting);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, "Meeting", false, true, true);
        queue = Volley.newRequestQueue(this);

        nextDate = (ImageView) findViewById(R.id.iv_next_meeting);
        tvNoMeeting = (TextView) findViewById(R.id.no_meeting_text);
        previousDate = (ImageView) findViewById(R.id.iv_previous_meeting);
        tvMonth = (TextView) findViewById(R.id.meeting_month);
        tvDate = (TextView) findViewById(R.id.meeting_date);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_meeting);
        meetingAdapter = new MeetingAdapter(this);
        recyclerView.setAdapter(meetingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set click listener
        setClickListener();
    }

    @Override
    public void setClickListener() {
        previousDate.setOnClickListener(this);
        nextDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_previous_meeting:

                break;
            case R.id.iv_next_meeting:

                break;
        }
    }

    private void getMeetingDetailsFromServer() {
        try {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, this));
            Helper.showLog(URL.MEETING_LIST,reqObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL.MEETING_LIST, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.hide();
                        if (response.getString("msg").equals("Success")) {
                            tvNoMeeting.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            JSONArray meetingJsonArray = response.getJSONObject("body").getJSONArray("detail");
                            meetingAdapter.updateData(meetingJsonArray);
                        } else {
                            tvNoMeeting.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.hide();
                    tvNoMeeting.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            });
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSnackbarActionClick() {
        if (Helper.isNetworkAvailable(this))
            getMeetingDetailsFromServer();
        else
            Helper.showSnackBar(this, linearLayoutRootMeeting, getResources().getString(R.string.no_connection), "Retry");
    }
}
