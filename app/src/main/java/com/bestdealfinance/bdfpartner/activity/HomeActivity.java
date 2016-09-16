package com.bestdealfinance.bdfpartner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.PagerContainer;
import com.bestdealfinance.bdfpartner.adapter.HomeOfferAdapter;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {


    private Toolbar toolbar;                              // Declaring the Toolbar Object
    private ViewPager mViewPager;
    PagerAdapter adapter;
    ViewPager pager;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout drawerLayout;                                  // Declaring DrawerLayout
    PagerContainer mContainer;
    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar drawerLayout Toggle
    //    LinearLayout tab_home,tab_wallet,tab_account, tab_update, layout_home, layout_wallet, layout_account, layout_update;
    private static final String TAB_1_TAG = "one";
    private static final String TAB_2_TAG = "two";
    private static final String TAB_3_TAG = "three";
    private static final String TAB_4_TAG = "four";
    private TabHost mTabHost;
    private Menu mainMenu;
    LayoutInflater mLayout;
    LinearLayout mainView;
    CardView ll_dashboard, ll_application, ll_payout;
    LinearLayout waiting_layout;
    ImageView progressBar;
    private AnimationDrawable animation;
    Button btn_get_started, product_landing;
    SharedPreferences pref;
    private boolean isLogin;
    private RequestQueue queue;
    private NavigationView navigationView;
    private View headerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        queue = Volley.newRequestQueue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Rubique");
        setSupportActionBar(toolbar);

        if (Helper.getStringSharedPreference(Constant.UTOKEN, this).equals("")) {
            isLogin = false;
        } else {
            isLogin = true;
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle); // drawerLayout Listener set to the drawerLayout toggle
        mDrawerToggle.syncState();

        initializeUIComponents();

        navigationView = (NavigationView) findViewById(R.id.main_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemTextColor(null);

        headerLayout = navigationView.getHeaderView(0);
        setHeaderValues(headerLayout);

        mLayout = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        StringRequest request = new StringRequest(Util.OFFER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String status, msg, utoken;
                try {
                    JSONObject output1 = new JSONObject(response);
                    if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                        status = output1.getString("status_code");
                        msg = output1.getString("msg");
                        if (status.equals("2000") && msg.equals("Success")) {
                            JSONArray jsonArray = new JSONArray(output1.getString("body"));

                            List<String> type = new ArrayList<>();
                            List<String> id = new ArrayList<>();
                            List<String> image = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                image.add((jsonObject.getString("image")));
                                type.add(jsonObject.getString("product_type_id"));
                                id.add((jsonObject.getString("product_id")));
                            }
                            initialize_viewpager(type, id, image);
                            //
                        }
                    }
                } catch (JSONException e) {
                    System.out.println("" + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);


    }

    private void setHeaderValues(View headerLayout) {

        TextView name = (TextView) headerLayout.findViewById(R.id.name);
        TextView email = (TextView) headerLayout.findViewById(R.id.email);
        TextView phone = (TextView) headerLayout.findViewById(R.id.phone);

        if (isLogin) {
            name.setText(Helper.getStringSharedPreference(Constant.NAME, this));
            email.setText(Helper.getStringSharedPreference(Constant.USERNAME, this));
            phone.setText(Helper.getStringSharedPreference(Constant.USER_PHONE, this));
        } else {
            name.setText("Guest");
            email.setText("");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);
        int mSelectedId = menuItem.getItemId();

        Intent resultIntent = Helper.getNavigationIntent(mSelectedId, isLogin, HomeActivity.this);

        if (resultIntent != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(resultIntent);
        }
        return true;
    }


    private class checkForMobile extends AsyncTask<Void, Void, String> {

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

                HttpPost httpPost = new HttpPost(Util.IS_MOBILE_VERIFIED);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + Helper.getStringSharedPreference(Util.utoken, HomeActivity.this));
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
                    if (msg.equals("Failed")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle("Message");
                        builder.setCancelable(false);
                        builder.setMessage(output1.getString("body"));
                        builder.setPositiveButton("Verify Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new VerifyMobile().execute();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new checkForEmail().execute();
                            }
                        });
                        builder.show();
                    } else if (status.equals("2000") && msg.equals("Success")) {
                        new checkForEmail().execute();
                    }
                }
            } catch (JSONException e) {

            }

        }
    }

    private class checkForEmail extends AsyncTask<Void, Void, String> {

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

                HttpPost httpPost = new HttpPost(Util.IS_EMAIL_VERIFIED);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + pref.getString(Util.utoken, ""));
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
                    if (msg.equals("Failed")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle("Message");
                        builder.setMessage(output1.getString("body"));
                        builder.setPositiveButton("Verify Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new VerifyEmail().execute();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                }
            } catch (JSONException e) {

            }

        }
    }

    private class VerifyMobile extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.SEND_OTP);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + pref.getString(Util.utoken, ""));

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

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (msg.equalsIgnoreCase("Success")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                        alert.setTitle("OTP");
                        alert.setCancelable(false);
                        alert.setMessage("Please enter mobile OTP");

// Set an EditText view to get user input
                        final EditText input = new EditText(HomeActivity.this);
                        alert.setView(input);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new CheckMobileOTP(input.getText().toString().trim()).execute();
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new checkForEmail().execute();
                            }
                        });

                        alert.show();

                    }
                }
            } catch (JSONException e) {

            }

        }
    }

    private class CheckMobileOTP extends AsyncTask<Void, Void, String> {

        String otp;

        public CheckMobileOTP(String input) {
            otp = input;
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.CHECK_OTP);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("otp", otp);

                json = jsonObject.toString();

                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + pref.getString(Util.utoken, ""));

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

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (msg.equalsIgnoreCase("Success")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                        alert.setTitle("Success");
                        alert.setCancelable(false);
                        alert.setMessage("Mobile verified successfully");

                        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new checkForEmail().execute();
                            }
                        });

                        alert.show();

                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                        alert.setTitle("Success");
                        alert.setCancelable(false);
                        alert.setMessage("Mobile not verified successfully");

                        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new checkForEmail().execute();
                            }
                        });

                        alert.show();
                    }
                }
            } catch (JSONException e) {

            }

        }
    }

    private class VerifyEmail extends AsyncTask<Void, Void, String> {


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

                httpPost.addHeader("Cookie", "utoken=" + pref.getString(Util.utoken, ""));

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

            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");
                    if (msg.equalsIgnoreCase("Success")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                        alert.setTitle("Email verification");
                        alert.setCancelable(false);
                        alert.setMessage(getResources().getString(R.string.msg_email_verification) + " " + Util.email);

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


    private void initialize_viewpager(List<String> type, List<String> id, List<String> image) {

        mContainer.setHorizontalScrollBarEnabled(true);

        pager = mContainer.getViewPager();
        pager.setClipToPadding(false);
        pager.setPadding(0, 0, 0, 0);
        pager.setPageMargin(0);
        adapter = new HomeOfferAdapter(getApplicationContext(), type, id, image);
        pager.setAdapter(adapter);

        pager.setOffscreenPageLimit(adapter.getCount());

        pager.setPageMargin(30);

        pager.setClipChildren(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //selected_dot.setVisibility(View.GONE);
            }

            @Override
            public void onPageSelected(int position) {

                Logs.LogD("Current", String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(pager);
        pager.setCurrentItem(1);
    }

    private void initializeUIComponents() {

        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        ll_dashboard = (CardView) findViewById(R.id.ll_dashboard);
        ll_application = (CardView) findViewById(R.id.ll_application);
        ll_payout = (CardView) findViewById(R.id.ll_payout);
        btn_get_started = (Button) findViewById(R.id.btn_get_started);
        //btn_get_started.setTypeface(Typeface.createFromAsset(this.getAssets(), getResources().getString(R.string.proxima)));
        waiting_layout = (LinearLayout) findViewById(R.id.waiting_layout);
        progressBar = (ImageView) findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        mainView = (LinearLayout) findViewById(R.id.mainView);

        ll_dashboard.setOnClickListener(this);
        ll_application.setOnClickListener(this);
        ll_payout.setOnClickListener(this);
        btn_get_started.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dashboard:
                startActivity(new Intent(HomeActivity.this, DashBoard.class).putExtra("val", "dashboard"));
                break;
            case R.id.ll_payout:
                startActivity(new Intent(HomeActivity.this, DashBoard.class).putExtra("val", "payout"));
                break;
            case R.id.ll_application:
                startActivity(new Intent(HomeActivity.this, DashBoard.class).putExtra("val", "referral"));
                break;
            case R.id.btn_get_started:
                startActivity(new Intent(HomeActivity.this, ReferralActivity.class).putExtra("data", "unavailable"));
                break;

        }
    }
}