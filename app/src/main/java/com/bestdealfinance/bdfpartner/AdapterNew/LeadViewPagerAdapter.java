package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.FragmentNew.LeadDetailFragment;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeadViewPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    JSONArray leadDetailsJsonArray, productListJsonArray;
    JSONArray inProgressLeadList, approvedLeadList, closedLeadList;
    private RequestQueue queue;

    public LeadViewPagerAdapter(Context context, FragmentManager fm, JSONArray leadDetailsJsonArray, JSONArray productListJsonArray) {
        super(fm);
        this.context = context;
        queue = Volley.newRequestQueue(context);
        this.leadDetailsJsonArray = leadDetailsJsonArray;

        this.productListJsonArray = productListJsonArray;
        inProgressLeadList = new JSONArray();
        approvedLeadList = new JSONArray();
        closedLeadList = new JSONArray();

        setDifferentLeadLists();
    }

    private void setDifferentLeadLists() {
        for (int i = 0; i < leadDetailsJsonArray.length(); i++) {
            JSONObject jsonObject = leadDetailsJsonArray.optJSONObject(i);
            try {

                if (jsonObject.optString("lead_state_original").equals("APPROVED")) {
                    approvedLeadList.put(jsonObject);
                } else if (jsonObject.optString("lead_state_original").equals("WITHDRAWN")||jsonObject.optString("lead_state_original").equals("DISBURSED")||jsonObject.optString("lead_state_original").equals("REJECTED")) {
                    closedLeadList.put(jsonObject);
                } else {
                    inProgressLeadList.put(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        JSONArray leadList;
        if (position == 0) {
            leadList = inProgressLeadList;
        } else if (position == 1) {
            leadList = approvedLeadList;
        } else {
            leadList = closedLeadList;
        }

//        LeadDetailFragment leadDetailFragment = new LeadDetailFragment(leadList, productListJsonArray);
        return LeadDetailFragment.newInstance(leadList, productListJsonArray.toString());
    }

    @Override
    public int getCount() {
        return 3;
    }




}