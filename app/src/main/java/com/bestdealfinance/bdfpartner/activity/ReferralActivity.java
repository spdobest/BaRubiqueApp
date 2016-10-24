package com.bestdealfinance.bdfpartner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.PagerContainer;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.database.DBHelper;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;


public class ReferralActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spr_salutation;
    TextView refer_header;
    EditText txt_refer_name, txt_refer_email, txt_refer_phone_num, txt_refer_product_type, txt_refer_product, txt_refer_submitter_email, txt_refer_notes, txt_refer_amount;
    Button btnReferLead;
    AppCompatSpinner txt_refer_city;
    AutoCompleteTextView scity;
    EditText sname, semail, sphone;
    CheckBox chb_self_referral;
    ImageView img_regi_back;
    String str_salutation, str_name, str_email, str_phone, str_product_type, str_product, str_submitter_email, str_notes, str_sname, str_semail, str_sphone, str_scity;
    int chb_val = 0, selected_product_type_id, selected_product_id;
    LinearLayout waiting_layout;
    List<String> dataList = new ArrayList<>();
    ImageView progressBar;
    String[] product_type_name, product_type_id, product_name, product_id;
    String[] salutation = {"Mr", "Ms", "Dr", "Prof"};
    DBHelper controller;
    LinearLayout noregisterLayout;
    ImageView selected_dot;
    MyPagerAdapter adapter;
    ArrayAdapter<String> cityAdapter, scityAdapter;
    //    LinearLayout ll_submitter_email;
    ArrayList<HashMap<String, String>> array_product;

    private AnimationDrawable animation;

    private static final int NUM_ITEMS = 5;
    private static final String BUNDLE_LIST_PIXELS = "allPixels";
    PagerContainer mContainer;
    public static int defaultItem = 3;
    ArrayAdapter adapter2;
    private int currItem;
    private String selected_loan_code = "0";
    private String selected_loan_name;
    ViewPager pager;
    Bundle b;
    ImageView backArrow;
    private float itemWidth;
    private float padding;
    private float firstItemWidth;
    private float allPixels;
    private int mLastPosition;
    RecyclerView items;
    Toolbar mToolbar;
    SharedPreferences pref;
    private Bundle bundle;
    String selectProductTypeBundle = "", selectedProductCode = "";
    private String str_city, str_amount;
    private RequestQueue queue;
    private List<String> cityList;
    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_referral);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        queue = Volley.newRequestQueue(this);


        bundle = getIntent().getExtras();

        if (bundle != null) {
            Logs.LogD("BundleReceiver", bundle.toString());
            Logs.LogD("BundleReceiver type", bundle.getString("product_type", ""));
            selectProductTypeBundle = bundle.getString("product_type", "");
            selectedProductCode = bundle.getString("product_id", "");
        } else {
            Logs.LogD("BundleReciev", "Not Value Recieved");
        }

        //pref = getSharedPreferences(Util.MY_PREFERENCES, Context.MODE_PRIVATE);
        //controller = new DBHelper(this);
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        initialization();
        if (defaultItem == 0) {
            txt_refer_amount.setVisibility(View.GONE);
        }

        initialize_products();

        //initializecities();
        //Util.intializeCity(this, txt_refer_city);
        //Util.intializeCity(this,scity);
//        if(i.getStringExtra("data").equals("available")){
//            b = i.getBundleExtra("val");
//            txt_refer_product_type.setText(b.getString("product_type_name"));
//            selected_product_type_id = Integer.parseInt(b.getString("product_type_id"));
//            txt_refer_product.setText(b.getString("product_name"));
//            selected_product_id = Integer.parseInt(b.getString("product_id"));
//        }
        btnReferLead.setOnClickListener(this);
        backArrow.setOnClickListener(this);

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Refer Only Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }


    private void initialize_products() {
//        ArrayList<HashMap<String, String>> product_type_data=new ArrayList<>();
//        HashMap<String,String> temp=new HashMap<>();
//        temp.put("11", "Credit Card");
//        product_type_data.add(temp);
//        HashMap<String,String> temp1=new HashMap<>();
//        temp.put("22", "Car Loan");
//        product_type_data.add(temp1);
//        HashMap<String,String> temp2=new HashMap<>();
//        temp.put("23", "Two Wheeler");
//        product_type_data.add(temp2);
//        HashMap<String,String> temp3=new HashMap<>();
//        temp.put("25", "Personal Loan");
//        product_type_data.add(temp3);
//        HashMap<String,String> temp4=new HashMap<>();
//        temp.put("26", "Home Loan");
//        product_type_data.add(temp4);
//        HashMap<String,String> temp5=new HashMap<>();
//        temp.put("28", "Loan Against Property");
//        product_type_data.add(temp5);
//        HashMap<String,String> temp6=new HashMap<>();
//        temp.put("39", "Business Loan");
//        product_type_data.add(temp6);
//
//        initialize_viewpager(product_type_data);
        Util.product_type_data = new ArrayList<>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id","11");
        map.put("name","Credit Card");
        Util.product_type_data.add(0,map);
        map = new HashMap<String, String>();
        map.put("id","22");
        map.put("name","Car Loan");
        Util.product_type_data.add(1,map);
        map = new HashMap<String, String>();
        map.put("id","23");
        map.put("name","Two Wheeler Loan");
        Util.product_type_data.add(2,map);
        map = new HashMap<String, String>();
        map.put("id","25");
        map.put("name","Personal Loan");
        Util.product_type_data.add(3,map);
        map = new HashMap<String, String>();
        map.put("id","26");
        map.put("name","Home Loan");
        Util.product_type_data.add(4,map);
        map = new HashMap<String, String>();
        map.put("id","28");
        map.put("name","Loan Against Property");
        Util.product_type_data.add(5,map);
        /*map = new HashMap<String, String>();
        map.put("id","29");
        map.put("name","Commercial Vehicle Finance");
        Util.product_type_data.add(6,map);*/
        map = new HashMap<String, String>();
        map.put("id","39");
        map.put("name","Business Loan");
        Util.product_type_data.add(6,map);
        map = new HashMap<String, String>();
        map.put("id","51");
        map.put("name","Life Insurance");
        Util.product_type_data.add(7,map);
        map = new HashMap<String, String>();
        map.put("id","52");
        map.put("name","General Insurance");
        Util.product_type_data.add(8,map);
        map = new HashMap<String, String>();
        map.put("id","53");
        map.put("name","Health Insurance");
        Util.product_type_data.add(9,map);


        initialize_viewpager(Util.product_type_data);

    }

    private void initialize_viewpager(final ArrayList<HashMap<String, String>> productList) {
        mContainer.setHorizontalScrollBarEnabled(true);
        pager = mContainer.getViewPager();
        pager.setClipToPadding(false);
        pager.setPadding(0, 0, 0, 0);
        pager.setPageMargin(0);
        adapter = new MyPagerAdapter(productList);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setPageMargin(30);
        pager.setCurrentItem(defaultItem, true);
        currItem = defaultItem;
        selected_loan_code = productList.get(defaultItem).get("id");
        selected_loan_name = productList.get(defaultItem).get("name");

        pager.setClipChildren(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //selected_dot.setVisibility(View.GONE);
            }

            @Override
            public void onPageSelected(int position) {
                selected_dot.setVisibility(View.VISIBLE);
                currItem = position;
                selected_loan_code = productList.get(position).get("id");
                selected_loan_name = productList.get(position).get("name");
                if (selected_loan_code.equals("11") || selected_loan_code.equals("51") || selected_loan_code.equals("52") || selected_loan_code.equals("53")) {
                    txt_refer_amount.setVisibility(View.GONE);
                    txt_refer_amount.setText("");
                    str_amount = "";

                } else {
                    txt_refer_amount.setVisibility(View.VISIBLE);
                }
                Logs.LogD("Current", productList.get(position).get("id"));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        selected_dot.bringToFront();
    }


    private class MyPagerAdapter extends PagerAdapter {
        ArrayList<HashMap<String, String>> mproductsList;
        int mcount;

        public MyPagerAdapter(ArrayList<HashMap<String, String>> productList) {
            mproductsList = productList;
            mcount = mproductsList.size();


            Logs.LogD("Size", "Size " + mcount);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mPage = inflater.inflate(R.layout.product_card_view, null);
            ImageView view = (ImageView) mPage.findViewById(R.id.p_image);
            TextView t_view = (TextView) mPage.findViewById(R.id.p_name);
            ImageView cover = (ImageView) mPage.findViewById(R.id.covering);
            cover.bringToFront();


            switch (mproductsList.get(position).get("id")) {
                case "11":
                    //Credit Card
                    Glide.with(getApplicationContext()).load(R.drawable.cc).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "22":
                    //CL
                    Glide.with(getApplicationContext()).load(R.drawable.cl).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "23":
                    //TWL
                    Glide.with(getApplicationContext()).load(R.drawable.twl).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "24":
                    //EL
                    Glide.with(getApplicationContext()).load(R.drawable.bl).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "25":
                    //PL
                    Glide.with(getApplicationContext()).load(R.drawable.pl).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "26":
                    //HL
                    Glide.with(getApplicationContext()).load(R.drawable.hl).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "27":
                    //LAS
                    Glide.with(getApplicationContext()).load(R.drawable.bl).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "28":
                    //LAP
                    Glide.with(getApplicationContext()).load(R.drawable.lap).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "29":
                    //CV
                    Glide.with(getApplicationContext()).load(R.drawable.cv).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "32":
                    //SME
                    Glide.with(getApplicationContext()).load(R.drawable.sme).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "39":
                    //BL
                    Glide.with(getApplicationContext()).load(R.drawable.bl).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;

                case "51":
                    //Life Insurance
                    Glide.with(getApplicationContext()).load(R.drawable.life_insurance).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "52":
                    //General Insurance
                    Glide.with(getApplicationContext()).load(R.drawable.general_insurance).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
                case "53":
                    //Health Insurance
                    Glide.with(getApplicationContext()).load(R.drawable.health_insurance).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;

                default:
                    //ANY Other Loan
                    Glide.with(getApplicationContext()).load(R.drawable.bl).into(view);
                    t_view.setText(mproductsList.get(position).get("name"));
                    container.addView(mPage);
                    break;
            }
            return mPage;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mcount;
        }

        @Override
        public float getPageWidth(int position) {
            return 1.0f;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private void initialization() {
        if (bundle != null) {
            getProductType(selectProductTypeBundle);
        }
        txt_refer_submitter_email = (EditText) findViewById(R.id.txt_semail);
        txt_refer_city = (AppCompatSpinner) findViewById(R.id.txt_refer_city);
        waiting_layout = (LinearLayout) findViewById(R.id.waiting_layout);
        refer_header = (TextView) findViewById(R.id.refer_header);
        String sourceString = "<b>" + "Swipe" + "</b> " + " to " + "<b>" + "Select" + "</b> " + "a product to refer";
        refer_header.setText(Html.fromHtml(sourceString));
        selected_dot = (ImageView) findViewById(R.id.selected_dot);
        progressBar = (ImageView) findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        if (Util.product_type_data == null) {
            finish();
            return;
        }
        product_type_id = new String[Util.product_type_data.size()];
        product_type_name = new String[Util.product_type_data.size()];

        for (int i = 0; i < Util.product_type_data.size(); i++) {
            product_type_id[i] = Util.product_type_data.get(i).get("id");
            product_type_name[i] = Util.product_type_data.get(i).get("name");
        }

        txt_refer_name = (EditText) findViewById(R.id.txt_refer_name);
        txt_refer_email = (EditText) findViewById(R.id.txt_refer_email);
        txt_refer_phone_num = (EditText) findViewById(R.id.txt_refer_phone_num);
        String code = "+91";
//        txt_refer_phone_num.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code), null, null, null);
//        txt_refer_phone_num.setCompoundDrawablePadding(code.length() * 10);
        txt_refer_notes = (EditText) findViewById(R.id.txt_refer_notes);
        btnReferLead = (Button) findViewById(R.id.btn_refer_lead);
        txt_refer_amount = (EditText) findViewById(R.id.txt_refer_amount);
        backArrow = (ImageView) findViewById(R.id.back_arrow);
        sname = (EditText) findViewById(R.id.txt_sname);
        semail = (EditText) findViewById(R.id.txt_semail);
        sphone = (EditText) findViewById(R.id.txt_sphone);
        scity = (AutoCompleteTextView) findViewById(R.id.txt_scity);
        noregisterLayout = (LinearLayout) findViewById(R.id.ba_details_layout);
        if (Helper.getStringSharedPreference(Constant.UTOKEN, this).equals("")) {
            noregisterLayout.setVisibility(View.VISIBLE);
            isLogin = false;

        } else {
            noregisterLayout.setVisibility(View.GONE);
            isLogin = true;

        }

        setAllCityAdapter();
        setCityAdapter();


    }

    private void getProductType(String select_product_bundle) {
        switch (select_product_bundle) {
            case "11":
                defaultItem = 0;
                break;
            case "22":
                defaultItem = 1;
                break;
            case "23":
                defaultItem = 2;
                break;
            case "25":
                defaultItem = 3;
                break;
            case "26":
                defaultItem = 4;
                break;
            case "28":
                defaultItem = 5;
                break;
            case "39":
                defaultItem = 6;
                break;
            case "51":
                defaultItem = 7;
                break;
            case "52":
                defaultItem = 8;
                break;
            case "53":
                defaultItem = 9;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                finish();
                break;
            case R.id.btn_refer_lead:
                if (validateRefer()) {


                    boolean flag = false;
                    for (int i = 0; i < cityList.size(); i++) {
                        if (scity.getText().toString().trim().toUpperCase().equals(cityList.get(i).toString().toUpperCase())) {
                            flag = true;
                        }
                    }

                    if (!flag && !isLogin) {
                        Toast.makeText(ReferralActivity.this, "Please select city from suggestion only", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (str_city.equals("Select City")) {
                        Toast.makeText(ReferralActivity.this, "Please select city", Toast.LENGTH_LONG).show();
                        return;
                    }


                    Bundle bundle1 = new Bundle();
                    bundle1.putString("type", selected_loan_code);
                    if (selected_loan_code.equals(selectProductTypeBundle)) {
                        bundle1.putString("product_id", selectedProductCode);
                        bundle1.putString("product_name", bundle.getString("product_name", null));
                        bundle1.putString("bank_logo", bundle.getString("bank_logo", null));
                    } else {
                        bundle1.putString("product_id", "");

                    }
                    bundle1.putString("product_type", selected_loan_code);
                    bundle1.putString("amount", str_amount.replaceAll(",", ""));
                    bundle1.putString("name", str_name);
                    bundle1.putString("email", str_email);
                    bundle1.putString("phone", str_phone);
                    bundle1.putString("city", str_city);
                    bundle1.putString("tname", selected_loan_name);
                    bundle1.putString("sname", str_sname);
                    bundle1.putString("sphone", str_sphone);
                    bundle1.putString("scity", str_scity);
                    bundle1.putString("type", selected_loan_code);
                    bundle1.putString("semail", str_submitter_email);
                    Intent intent = new Intent(ReferralActivity.this, CongratulationScreen.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
        }
    }

    private boolean validateRefer() {
        str_name = txt_refer_name.getText().toString().trim();
        str_email = txt_refer_email.getText().toString().trim();
        str_phone = txt_refer_phone_num.getText().toString().trim();
        str_city = txt_refer_city.getSelectedItem().toString();
        str_amount = txt_refer_amount.getText().toString().trim();


        if (!isTextValid(str_name)) {
            txt_refer_name.setError("Please enter Referral name");
            txt_refer_name.requestFocus();
            return false;
        }

        if (!isEmailValid(str_email)) {
            txt_refer_email.setError("Please enter valid Email ID");
            txt_refer_email.requestFocus();
            return false;
        }
        if (!isPhoneValid(str_phone)) {
            txt_refer_phone_num.setError("Please enter valid mobile number");
            txt_refer_phone_num.requestFocus();
            return false;
        }


        if (str_amount.length() < 5 && txt_refer_amount.getVisibility() == View.VISIBLE) {
            txt_refer_amount.setError("Please Enter Valid Loan Amount");
            txt_refer_amount.requestFocus();
            return false;
        }
        if (Util.isRegistered(getApplicationContext()).equals("")) {


            isLogin = false;
            str_sname = sname.getText().toString().trim();
            str_sphone = sphone.getText().toString().trim();
            str_scity = scity.getText().toString().trim();
            str_submitter_email = txt_refer_submitter_email.getText().toString().trim();
            if (!isTextValid(str_sname)) {
                sname.setError("Please Enter Your Name");
                sname.requestFocus();
                return false;
            }
            if (!isEmailValid(str_submitter_email)) {
                txt_refer_submitter_email.setError("Please Enter Your Email ID ");
                txt_refer_submitter_email.requestFocus();
                return false;
            }
            if (!isPhoneValid(str_phone)) {
                semail.setError("Please Enter Your 10 Digit mobile number");
                semail.requestFocus();
                return false;
            }
            if (!isTextValid(str_city)) {
                scity.setError("Please Enter Your City");
                scity.requestFocus();
                return false;
            }

        }
        Logs.LogD("Referral", "Validation Passed");
        return true;
    }


    private boolean isPhoneValid(String phone) {
        return android.util.Patterns.PHONE.matcher(phone.trim()).matches() && phone.trim().length() == 10;

    }

    private boolean isTextValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }


    private void setAllCityAdapter() {
        StringRequest request = new StringRequest(Util.FETCH_ALL_CITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cityList = new ArrayList<>();

                try {
                    JSONObject res = new JSONObject(response);
                    JSONArray data = res.getJSONObject("body").getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        if (!data.getJSONObject(i).getString("item_value").equals("Others"))
                            cityList.add(data.getJSONObject(i).getString("item_value").toUpperCase());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReferralActivity.this, android.R.layout.simple_dropdown_item_1line, cityList);
                    scity.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

    private void setCityAdapter() {
        StringRequest request = new StringRequest(Util.FETCH_APP_CITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cityList = new ArrayList<>();
                cityList.add("Select City");
                try {
                    JSONObject res = new JSONObject(response);
                    JSONArray data = res.getJSONObject("body").getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        if (!data.getJSONObject(i).getString("item_value").equals("Others"))
                            cityList.add(data.getJSONObject(i).getString("item_value").toUpperCase());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReferralActivity.this, android.R.layout.simple_dropdown_item_1line, cityList);
                    txt_refer_city.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }


}

