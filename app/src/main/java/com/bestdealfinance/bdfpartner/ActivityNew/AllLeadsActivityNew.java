package com.bestdealfinance.bdfpartner.ActivityNew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.LeadViewPagerAdapter;
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
import java.util.List;

public class AllLeadsActivityNew extends AppCompatActivity {
    private static final String TAG = "AllLeadsActivityNew";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<JSONObject> productTypeArray;
    JSONArray productListJsonArray;
    JSONArray allLeadDetailsArray;
    private static final int SIGN_REQUEST = 1;

    private RequestQueue queue;
    ProgressDialog progressDialog;
    private TextView tvNoDetailsText;
    private DB snappyDB;
    private LinearLayout layoutAllLeadsNew;

    boolean isActivityOnForeGround = true;
    private LinearLayout activity_all_leads_new;

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOnForeGround = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_leads_new);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, "Leads", false, true, true);
        tvNoDetailsText = (TextView) findViewById(R.id.lead_no_details);
        layoutAllLeadsNew = (LinearLayout) findViewById(R.id.activity_all_leads_new);
        activity_all_leads_new = (LinearLayout) findViewById(R.id.activity_all_leads_new);

        Helper.showSnackbarMessage(activity_all_leads_new,"Updating lead, Please Wait...", Snackbar.LENGTH_LONG);

        queue = Volley.newRequestQueue(this);
        productTypeArray = new ArrayList<JSONObject>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

        if (Helper.getStringSharedPreference(Constant.UTOKEN, AllLeadsActivityNew.this).equals("")) {
            startActivityForResult(new Intent(this, SigninActivityNew.class), SIGN_REQUEST);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Helper.getStringSharedPreference(Constant.UTOKEN, AllLeadsActivityNew.this).equals("")) {
            initializeValues();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
                initializeValues();
            } else {
                Toast.makeText(this, "You must login/register to view that page", Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

    private void initializeValues() {

        tabLayout = (TabLayout) findViewById(R.id.leads_tab_layout);
        viewPager = (ViewPager) findViewById(R.id.leads_viewpager);

        fetchProduct();
        // fet all the lead details from the snappy DB
        fetAllLeadDetailsArray();

        getAllLeads();
    }

    private void fetAllLeadDetailsArray(){

        try {
            snappyDB = DBFactory.open(AllLeadsActivityNew.this);
            if (snappyDB.exists(Constant.DB_ALL_LEAD_DETAILS_ARRAY)) {
                JSONObject body = new JSONObject(snappyDB.get(Constant.DB_ALL_LEAD_DETAILS_ARRAY));
                allLeadDetailsArray = body.getJSONArray("body");
            }
            snappyDB.close();
        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }
        if(allLeadDetailsArray!=null) {
            viewPager.setAdapter(new LeadViewPagerAdapter(AllLeadsActivityNew.this, getSupportFragmentManager(), allLeadDetailsArray, productListJsonArray));
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        }
    }


    private void fetchProduct() {
        try {
            snappyDB = DBFactory.open(AllLeadsActivityNew.this);
            if (snappyDB.exists(Constant.DB_ALL_PRODUCTS_DETAILS_OBJECT)) {
                JSONObject body = new JSONObject(snappyDB.get(Constant.DB_ALL_PRODUCTS_DETAILS_OBJECT));
                productListJsonArray = body.getJSONArray("list_item_products");
            }
            snappyDB.close();
        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAllLeads() {
        JSONObject request = new JSONObject();
        try {
            request.put("start_date", "");
            request.put("end_date", "");
            request.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, AllLeadsActivityNew.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Helper.showLog(URL.GET_LEAD_LIST,request.toString());

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, URL.GET_LEAD_LIST, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: all lead details "+response.toString());
                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                            try {

                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {

                                    progressDialog.dismiss();
                                    tvNoDetailsText.setVisibility(View.GONE);
                                    allLeadDetailsArray = response.getJSONArray("body");

                                    try {
                                        snappyDB = DBFactory.open(AllLeadsActivityNew.this);
                                        snappyDB.put(Constant.DB_ALL_LEAD_DETAILS_ARRAY,response.toString());
                                        snappyDB.close();
                                    } catch (Exception e ) {
                                        e.printStackTrace();
                                    }

                                    if(allLeadDetailsArray!=null && isActivityOnForeGround ) {
                                        if(viewPager!=null){
                                        viewPager.setAdapter(new LeadViewPagerAdapter(AllLeadsActivityNew.this, getSupportFragmentManager(), allLeadDetailsArray, productListJsonArray));

                                        Log.i(TAG, "onResponse: allLeadDetailsArray " + allLeadDetailsArray.toString() + " \n productListJsonArray " + productListJsonArray.toString());

                                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
                                    }}
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        tvNoDetailsText.setVisibility(View.VISIBLE);
                        Toast.makeText(AllLeadsActivityNew.this, "Unable to access", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(serverValues);

    }


}
