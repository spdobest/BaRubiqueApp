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

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.PagerContainer;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.database.DBHelper;
import com.bumptech.glide.Glide;

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


public class ReferralActivity extends AppCompatActivity implements View.OnClickListener,APIUtils.OnTaskCompletedProducts, APIUtils.OnTaskCompletedCity {

    Spinner spr_salutation;
    TextView refer_header;
    EditText txt_refer_name, txt_refer_email, txt_refer_phone_num, txt_refer_product_type, txt_refer_product, txt_refer_submitter_email, txt_refer_notes,  txt_refer_amount;
    Button btnReferLead;
    AutoCompleteTextView txt_refer_city,scity;
    EditText sname,semail,sphone;
    CheckBox chb_self_referral;
    ImageView img_regi_back;
    String str_salutation, str_name, str_email, str_phone, str_product_type, str_product, str_submitter_email, str_notes, str_sname,str_semail,str_sphone,str_scity;
    int chb_val = 0, selected_product_type_id, selected_product_id;
    LinearLayout waiting_layout;
    List<String> dataList=new ArrayList<>();
    ImageView progressBar;
    String[] product_type_name, product_type_id, product_name, product_id;
    String[] salutation = {"Mr","Ms","Dr","Prof"};
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
    private String selected_loan_name ;
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
    String selectProductTypeBundle = "", selectedProductCode ="";
    private String str_city, str_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_referral);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        bundle=getIntent().getExtras();

        if (bundle!=null) {
            Logs.LogD("BundleReceiver",bundle.toString());
            Logs.LogD("BundleReceiver type",bundle.getString("product_type", ""));
            selectProductTypeBundle = bundle.getString("product_type", "");
            selectedProductCode = bundle.getString("product_id", "");
        }
        else {
            Logs.LogD("BundleReciev","Not Value Recieved");
        }

        //pref = getSharedPreferences(Util.MY_PREFERENCES, Context.MODE_PRIVATE);
        //controller = new DBHelper(this);
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        initialization();
        if (defaultItem==0){
            txt_refer_amount.setVisibility(View.GONE);
        }

        initialize_products();

        //initializecities();
        Util.intializeCity(this, txt_refer_city);
        Util.intializeCity(this,scity);
//        if(i.getStringExtra("data").equals("available")){
//            b = i.getBundleExtra("val");
//            txt_refer_product_type.setText(b.getString("product_type_name"));
//            selected_product_type_id = Integer.parseInt(b.getString("product_type_id"));
//            txt_refer_product.setText(b.getString("product_name"));
//            selected_product_id = Integer.parseInt(b.getString("product_id"));
//        }
        btnReferLead.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }
    private void initializecities(){
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Logs.LogD("City", s.toString());
                if (s.length()==3) {
                   GetCity data = new GetCity();
                    data.executeOnExecutor(Util.threadPool);
                }

            }
        };
        txt_refer_city.addTextChangedListener(watcher);
        txt_refer_city.setThreshold(2);

//        if(Util.city_names == null) {
//            APIUtils.GetCity city=new APIUtils.GetCity(new APIUtils.OnTaskCompletedCity() {
//                @Override
//                public void OnTaskCompletedCity() {
//                    cityAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.text_list_item, Util.city_names);
//                    scityAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.text_list_item, Util.city_names);
//                    txt_refer_city.setAdapter(cityAdapter);
//                    scity.setAdapter(scityAdapter);
//                }
//            });
//            city.executeOnExecutor(Util.threadPool);
//
//        }



    }

    @Override
    public void OnTaskCompletedProducts() {

    }

    @Override
    public void OnTaskCompletedCity() {

    }
    private void initialize_products(){
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
        if(Util.product_type_data == null) {
            if (Util.isNetworkAvailable(getApplicationContext())) {
                APIUtils.ProductTypeAsyncTask task1=new APIUtils.ProductTypeAsyncTask(new APIUtils.OnTaskCompletedProducts() {
                    @Override
                    public void OnTaskCompletedProducts() {
                        initialize_viewpager(Util.product_type_data);
                    }
                });
                task1.executeOnExecutor(Util.threadPool);
            }
        } else {
            Logs.LogD("Refer", "Products Found");
            initialize_viewpager(Util.product_type_data);
        }
    }

    private void initialize_viewpager(final ArrayList<HashMap<String, String>>productList) {
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
        selected_loan_name=productList.get(defaultItem).get("name");

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
                if (selected_loan_code.equals("11")) {
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


            Logs.LogD("Size","Size "+mcount);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mPage = inflater.inflate(R.layout.product_card_view, null);
            ImageView view= (ImageView) mPage.findViewById(R.id.p_image);
            TextView t_view= (TextView) mPage.findViewById(R.id.p_name);
            ImageView cover= (ImageView) mPage.findViewById(R.id.covering);
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
        if (bundle!=null) {
            getProductType(selectProductTypeBundle);
        }
        txt_refer_submitter_email= (EditText) findViewById(R.id.txt_semail);
        txt_refer_city = (AutoCompleteTextView) findViewById(R.id.txt_refer_city);
        waiting_layout = (LinearLayout)findViewById(R.id.waiting_layout);
        refer_header= (TextView) findViewById(R.id.refer_header);
        String sourceString = "<b>" + "Swipe" + "</b> " + " to "+"<b>" + "Select" + "</b> "+ "a product to refer";
        refer_header.setText(Html.fromHtml(sourceString));
        selected_dot= (ImageView) findViewById(R.id.selected_dot);
        progressBar = (ImageView)findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        if (Util.product_type_data==null){
            finish();
            return;
        }
        product_type_id = new String[Util.product_type_data.size()];
        product_type_name = new String[Util.product_type_data.size()];

        for (int i = 0; i< Util.product_type_data.size(); i++){
            product_type_id[i] = Util.product_type_data.get(i).get("id");
            product_type_name[i] = Util.product_type_data.get(i).get("name");
        }

        txt_refer_name = (EditText)findViewById(R.id.txt_refer_name);
        txt_refer_email = (EditText)findViewById(R.id.txt_refer_email);
        txt_refer_phone_num = (EditText)findViewById(R.id.txt_refer_phone_num);
        String code = "+91";
//        txt_refer_phone_num.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code), null, null, null);
//        txt_refer_phone_num.setCompoundDrawablePadding(code.length() * 10);
        txt_refer_notes = (EditText)findViewById(R.id.txt_refer_notes);
        btnReferLead = (Button)findViewById(R.id.btn_refer_lead);
        txt_refer_amount= (EditText) findViewById(R.id.txt_refer_amount);
        backArrow = (ImageView) findViewById(R.id.back_arrow);
        sname= (EditText) findViewById(R.id.txt_sname);
        semail= (EditText) findViewById(R.id.txt_semail);
        sphone= (EditText) findViewById(R.id.txt_sphone);
        scity= (AutoCompleteTextView) findViewById(R.id.txt_scity);
        noregisterLayout= (LinearLayout) findViewById(R.id.ba_details_layout);
        if (Util.isRegistered(ReferralActivity.this).equals("")){
            noregisterLayout.setVisibility(View.VISIBLE);
        }
        else {
            noregisterLayout.setVisibility(View.GONE);
        }
        TextWatcher watcher = new TextWatcher() {
            String old="";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txt_refer_amount.removeTextChangedListener(this);
                Logs.LogD("Watcher", "Text CHnged");
                String amount = s.toString();


                if (amount == null || amount.equals("null") || amount.equals("")) {

                }
                else {
                    try {
                        amount=amount.replaceAll(",","");
                        txt_refer_amount.setText(Util.formatRs(amount));
                        txt_refer_amount.setSelection(txt_refer_amount.getText().length());
                        old=amount;
                    } catch (NumberFormatException n) {
                        Logs.LogD("Exception", n.getLocalizedMessage());
                    }
                }
                txt_refer_amount.addTextChangedListener(this);
            }
        };
        txt_refer_amount.addTextChangedListener(watcher);


    }

    private void getProductType(String select_product_bundle) {
        switch (select_product_bundle){
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                finish();
                break;
            case R.id.btn_refer_lead:
                if (validateRefer()) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("type", selected_loan_code);
                    if(selected_loan_code.equals(selectProductTypeBundle)){
                        bundle1.putString("product_id", selectedProductCode);
                        bundle1.putString("product_type",selected_loan_code);
                        bundle1.putString("product_name",bundle.getString("product_name",null));
                        bundle1.putString("bank_logo",bundle.getString("bank_logo",null));
                    } else {
                        bundle1.putString("product_id","");
                    }
                    bundle1.putString("amount", str_amount.replaceAll(",",""));
                    bundle1.putString("name",str_name);
                    bundle1.putString("email",str_email);
                    bundle1.putString("phone",str_phone);
                    bundle1.putString("city",str_city);
                    bundle1.putString("tname",selected_loan_name);
                    bundle1.putString("sname",str_sname);
                    bundle1.putString("sphone",str_sphone);
                    bundle1.putString("scity",str_scity);
                    bundle1.putString("type",selected_loan_code);
                    bundle1.putString("semail",str_submitter_email);
                    Logs.LogD("Bundle",bundle1.toString());
                    Intent intent=new Intent(ReferralActivity.this, CongratulationScreen.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
        }
    }
    private boolean validateRefer(){
        str_name = txt_refer_name.getText().toString().trim();
        str_email = txt_refer_email.getText().toString().trim();
        str_phone = txt_refer_phone_num.getText().toString().trim();
        str_city = txt_refer_city.getText().toString();
        str_amount = txt_refer_amount.getText().toString().trim();


        if (!isTextValid(str_name)){
            txt_refer_name.setError("Please enter Referral name");
            txt_refer_name.requestFocus();
            return false;
        }

        if(!isEmailValid(str_email)) {
            txt_refer_email.setError("Please enter valid Email ID");
            txt_refer_email.requestFocus();
            return false;
        }
        if (!isPhoneValid(str_phone)) {
            txt_refer_phone_num.setError("Please enter valid mobile number");
            txt_refer_phone_num.requestFocus();
            return false;
        }

        if (!isTextValid(str_city)) {
            txt_refer_city.setError("Enter city");
            txt_refer_city.requestFocus();
            return false;
        }
        if (str_amount.length() < 5 && txt_refer_amount.getVisibility() == View.VISIBLE) {
            txt_refer_amount.setError("Please Enter Valid Loan Amount");
            txt_refer_amount.requestFocus();
            return false;
        }
        if (Util.isRegistered(getApplicationContext()).equals("")) {
            str_sname=sname.getText().toString().trim();
            str_sphone=sphone.getText().toString().trim();
            str_scity=scity.getText().toString().trim();
            str_submitter_email=txt_refer_submitter_email.getText().toString().trim();
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
            if (!isPhoneValid(str_phone)){
                semail.setError("Please Enter Your 10 Digit mobile number");
                semail.requestFocus();
                return false;
            }
            if (!isTextValid(str_city)){
                scity.setError("Please Enter Your City");
                scity.requestFocus();
                return false;
            }

        }
        Logs.LogD("Referral", "Validation Passed");
        return true;
    }


    private boolean isPhoneValid(String phone){
        return android.util.Patterns.PHONE.matcher(phone.trim()).matches() && phone.trim().length()==10;

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
    private class ProductAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.PRODUCT);
                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("product_type_id", selected_product_type_id+"");

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
            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");

                    if (status.equals("2000") && msg.equals("Success")) {
                        JSONArray jsonArray = new JSONArray(output1.getString("body"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObj = jsonArray.getJSONObject(i);
                            controller.insertProductByProductType(jObj.getString("id"), jObj.getString("name"), selected_product_type_id + "");
                        }

                        array_product = controller.getProductByProductType(selected_product_type_id + "");
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

    private void checkForNote() {
        if(txt_refer_notes.getText().toString().trim().isEmpty()){
            str_notes = "";
        } else {
            str_notes = txt_refer_notes.getText().toString().trim();
        }
        if(chb_self_referral.isChecked()) {
            chb_val = 1;
        } else {
            chb_val = 0;
        }
        new HttpAsyncTask().execute();
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

                HttpPost httpPost = new HttpPost(Util.REFER_A_LEAD);

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("salutation", str_salutation);
                jsonObject.accumulate("name", str_name);
                jsonObject.accumulate("email", str_email);
                jsonObject.accumulate("phone", str_phone);
                jsonObject.accumulate("city", str_city);
                jsonObject.accumulate("amount", str_amount);
                jsonObject.accumulate("product_type_sought", selected_loan_code);
                jsonObject.accumulate("product_id", selected_product_id);
                jsonObject.accumulate("submitter_email", str_submitter_email);
                jsonObject.accumulate("self_refferal", chb_val);
                jsonObject.accumulate("note", str_notes);

                json = new String(jsonObject.toString().getBytes("ISO-8859-1"), "UTF-8");

                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + pref.getString(Util.utoken, ""));

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else
                    result = "Did not work!";

                System.out.println("!!!!!!!!!!!!!!!!Disha result= " + result);

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
//                        AlertDialog.Builder builder = new AlertDialog.Builder(ReferralActivity.this);
//                        builder.setTitle("Message");
//                        builder.setCancelable(false);
//                        builder.setMessage("Lead successfully submitted.");//output1.getString("body"));
//                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        });
//                        builder.show();

                        startActivity(new Intent(ReferralActivity.this, CongratulationScreen.class));
                        finish();
                    } else {
                        if(msg.equals("Failed")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReferralActivity.this);
                            builder.setTitle("Message");
                            builder.setCancelable(false);
                            builder.setMessage(output1.getString("body"));
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    }
                }
            } catch (JSONException e) {

                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!JSONException " + e);
            }

        }
    }

    final class GetCity extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(Util.ALLLISTS);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject post=new JSONObject();
                post.put("list_id","10063");
                post.put("keyword",txt_refer_city.getText().toString());
                Logs.LogD("Request",post.toString());
                Logs.LogD("Refer", "Sent the Request");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(post.toString());
                writer.flush();
                writer.close();
                os.close();
                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    response = sb.toString();
                } else {
                    Logs.LogD("Exception", conn.getResponseMessage());
                }
            } catch (Exception e) {
                Logs.LogD("Task Exception" +
                        "on", e.getLocalizedMessage());
                e.printStackTrace();
                response = e.getLocalizedMessage();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            Logs.LogD("Cities",result);
            try {
                JSONObject data=new JSONObject(result);
                if (data.optString("status_code").equals("2000")) {
                    JSONArray body=data.optJSONArray("body");

                    dataList = new ArrayList<>();
                    for (int i = 0; i < body.length(); i++) {
                        JSONObject temp= body.getJSONObject(i);
                        dataList.add(temp.getString("itemValue"));
                    }
                    adapter2=new ArrayAdapter(getApplicationContext(),R.layout.simpledropdown, dataList.toArray());
                    txt_refer_city.setAdapter(adapter2);
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

