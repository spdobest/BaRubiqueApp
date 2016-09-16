package com.bestdealfinance.bdfpartner.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.fragment.TrainingDocumentFragment;
import com.bestdealfinance.bdfpartner.fragment.TrainingVideoFragment;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class TrainingActivity extends AppCompatActivity {

    private RequestQueue queue;
    private JSONArray videoArray;
    private JSONArray documentArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

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


        final TabLayout tabLayout = (TabLayout) findViewById(R.id.training_tablayout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.training_viewpager);

        try {

            final Snackbar snackbar = Snackbar.make(findViewById(R.id.training_main_layout), "Loaading from Network , Please Wait", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            JSONObject reqObject = new JSONObject();
            reqObject.put("key", "tra!33$");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.TRAINING_FETCH, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    snackbar.dismiss();
                    try {
                        //Log.e("Data",response.toString());
                        JSONArray responseArray = response.getJSONObject("body").getJSONArray("data");
                        //Log.e("Data arary",responseArray.toString());
                        videoArray = new JSONArray();
                        documentArray = new JSONArray();
                        for (int i = 0; i < responseArray.length(); i++) {
                            if (responseArray.getJSONObject(i).getString("type").equals("V")) {
                                videoArray.put(responseArray.getJSONObject(i));
                            } else if (responseArray.getJSONObject(i).getString("type").equals("D")) {
                                documentArray.put(responseArray.getJSONObject(i));
                            }

                        }


                        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    snackbar.setText("Network Error").setAction("EXIT", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                }
            });

            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Training Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                Fragment fragment = new TrainingVideoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data", videoArray.toString());
                fragment.setArguments(bundle);
                return fragment;
            } else {
                Fragment fragment = new TrainingDocumentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data", documentArray.toString());
                fragment.setArguments(bundle);
                return fragment;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
