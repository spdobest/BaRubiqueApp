package com.bestdealfinance.bdfpartner.FragmentNew;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rbq on 10/20/16.
 */

public class MeetingFragmentNew extends Fragment {

    private static final int SIGN_REQUEST = 1;

    private RequestQueue queue;
    Toolbar toolbar;

    private RecyclerView recyclerView;
    private MeetingAdapter meetingAdapter;
    private ProgressDialog progressDialog;

    private ImageView nextDate, previousDate;
    private TextView tvMonth, tvDate, tvNoMeeting;

    public MeetingFragmentNew() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meeting_new, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(getActivity(), toolbar, "Meeting", false, false, false);
        queue = Volley.newRequestQueue(getActivity());

        nextDate = (ImageView) rootView.findViewById(R.id.iv_next_meeting);
        tvNoMeeting = (TextView) rootView.findViewById(R.id.no_meeting_text);
        previousDate = (ImageView) rootView.findViewById(R.id.iv_previous_meeting);

        nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO change date text and update adapter
            }
        });

        previousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO change date text and update adapter
            }
        });

        tvMonth = (TextView) rootView.findViewById(R.id.meeting_month);
        tvDate = (TextView) rootView.findViewById(R.id.meeting_date);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_meeting);
        meetingAdapter = new MeetingAdapter(getActivity());
        recyclerView.setAdapter(meetingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        getMeetingDetailsFromServer();

        return rootView;
    }


    private void getMeetingDetailsFromServer() {
        try {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            Helper.showLog(URL.MEETING_LIST,reqObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL.MEETING_LIST, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("Meetings ", "onResponse: meetings   "+response.toString());
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
}
