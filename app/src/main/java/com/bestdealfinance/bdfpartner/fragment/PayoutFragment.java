package com.bestdealfinance.bdfpartner.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.LoginActivity;
import com.bestdealfinance.bdfpartner.activity.ProfileActivity;
import com.bestdealfinance.bdfpartner.adapter.PayoutCCAdapter;
import com.bestdealfinance.bdfpartner.adapter.PayoutLoansAdapter;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.database.DBHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class PayoutFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<String> loan_type = new ArrayList<>();
    private List<String> cc_type = new ArrayList<>();
    private List<String> payout1 = new ArrayList<>();
    private List<String> payout2 = new ArrayList<>();
    private List<String> cc_payout1 = new ArrayList<>();
    private List<String> cc_payout2 = new ArrayList<>();
    private PayoutLoansAdapter adapter;
    private PayoutCCAdapter adapter1;
    private ProgressBar pay_bar;
    private RecyclerView recycler_view_loans, recycler_view_cc;

    EditText payout_product, payout_referral_type, payout_loan_amount;

    String str_product_name, str_referral_type, str_amount;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String[] referral_name = {"Referral", "Application"};

    TextView payout_loans, payout_cc, calculat_payout;
    LinearLayout layout_loans, layout_cc, layout_payout_calculator;
    boolean payout_cal_show = false, loan = true;
    boolean loan_bool = false;

    Button btn_cal_payout;
    DBHelper helper;


    private AppCompatSeekBar seekBar;
    private static final int STATE_REFERRAL = 8;
    private static final int STATE_APP_FILL = 32;
    private static final int STATE_LOGISTICS = 60;
    private static final int STATE_DISBURSEMENT = 88;
    private RequestQueue queue;
    public JSONArray products;
    private JSONObject stepsData;
    private JSONArray payoutsArray;
    private SlabWiseAdapter slabWiseAdapter;
    private AppCompatCheckBox step1, step2, step3, step4;


    public PayoutFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payout, container, false);
        queue = Volley.newRequestQueue(getActivity());
        TextView tvRefferal = (TextView) view.findViewById(R.id.referral_text);
        TextView tvAppFill = (TextView) view.findViewById(R.id.app_fill_text);
        TextView tvLogistics = (TextView) view.findViewById(R.id.logistics_text);
        TextView tvDisbursement = (TextView) view.findViewById(R.id.disbursement_text);
        final LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);

        /*tvRefferal.setOnClickListener(this);
        tvAppFill.setOnClickListener(this);
        tvLogistics.setOnClickListener(this);
        tvDisbursement.setOnClickListener(this);*/

        step1 = (AppCompatCheckBox) view.findViewById(R.id.step1);
        step2 = (AppCompatCheckBox) view.findViewById(R.id.step2);
        step3 = (AppCompatCheckBox) view.findViewById(R.id.step3);
        step4 = (AppCompatCheckBox) view.findViewById(R.id.step4);

        step1.setOnCheckedChangeListener(this);
        step2.setOnCheckedChangeListener(this);
        step3.setOnCheckedChangeListener(this);
        step4.setOnCheckedChangeListener(this);


        final AppCompatSpinner productSpinner = (AppCompatSpinner) view.findViewById(R.id.product_spinner);
        //AppCompatSpinner bankSpinner = (AppCompatSpinner) view.findViewById(R.id.bank_spinner);
        final ListView payoutList = (ListView) view.findViewById(R.id.payout_list);
        slabWiseAdapter = new SlabWiseAdapter(payoutsArray);
        payoutList.setAdapter(slabWiseAdapter);

        StringRequest stringRequest = new StringRequest(Util.PRODUCT_TYPE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject res = new JSONObject(response);
                    products = res.getJSONArray("body");
                    List<String> productList = new ArrayList<String>();
                    productList.add("Select product");
                    for (int i = 0; i < products.length(); i++) {

                        if (products.getJSONObject(i).getInt("id") != 11)
                            productList.add(products.getJSONObject(i).getString("name"));
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_product_item, productList);
                    productSpinner.setAdapter(dataAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    if (Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()).equals("")) {

                        Snackbar.make(mainLayout, "Please login to see data.", Snackbar.LENGTH_INDEFINITE).setAction("LOGIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                getActivity().finish();
                            }
                        }).show();
                        return;
                    }

                    JSONObject reqObject = new JSONObject();
                    reqObject.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
                    reqObject.put("class", Helper.getStringSharedPreference(Constant.USER_CLASS, getActivity()));
                    reqObject.put("product_type", products.getJSONObject(i).getString("id"));

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Util.PAYOUT_CALCULATOR, reqObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                stepsData = response.getJSONObject("body").getJSONObject("steps");
                                payoutsArray = response.getJSONObject("body").getJSONArray("payouts");
                                slabWiseAdapter.updateData(payoutsArray, step1.isChecked(), step2.isChecked(), step3.isChecked(), step4.isChecked());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            slabWiseAdapter.updateData(null, step1.isChecked(), step2.isChecked(), step3.isChecked(), step4.isChecked());



                        }
                    });
                    queue.add(jsonObjectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        /*helper = new DBHelper(getActivity());

        layout_loans = (LinearLayout) view.findViewById(R.id.layout_loans);

        layout_cc = (LinearLayout) view.findViewById(R.id.layout_cc);
        layout_payout_calculator = (LinearLayout) view.findViewById(R.id.layout_payout_calculator);


        layout_cc.setVisibility(View.GONE);
        payout_loans = (TextView) view.findViewById(R.id.payout_loans);
        payout_cc = (TextView) view.findViewById(R.id.payout_cc);
        calculat_payout = (TextView) view.findViewById(R.id.calculat_payout);
        recycler_view_loans = (RecyclerView) view.findViewById(R.id.recycler_view_loans);
        recycler_view_loans.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_cc = (RecyclerView) view.findViewById(R.id.recycler_view_cc);
        recycler_view_cc.setLayoutManager(new LinearLayoutManager(getActivity()));


        HttpAsyncTaskLoans task = new HttpAsyncTaskLoans();
        task.executeOnExecutor(Util.threadPool);

        HttpAsyncTaskCC task1 = new HttpAsyncTaskCC();
        task1.executeOnExecutor(Util.threadPool);


        payout_loans.setOnClickListener(this);
        payout_cc.setOnClickListener(this);
        calculat_payout.setOnClickListener(this);
*/
        return view;
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.payout_loans:
                loan = true;
                payout_loans.setTextColor(getActivity().getResources().getColor(R.color.orange));
                payout_cc.setTextColor(getActivity().getResources().getColor(R.color.dark_gray));
                if(payout_cal_show){

                    layout_loans.setVisibility(View.GONE);
                    layout_cc.setVisibility(View.GONE);
                    payout_product.setText("");
                    payout_referral_type.setText("");
                    payout_loan_amount.setText("");
                } else {
                    layout_loans.setVisibility(View.VISIBLE);

                    layout_cc.setVisibility(View.GONE);
                }
                break;
            case R.id.payout_cc:
                loan = false;
                payout_loans.setTextColor(getActivity().getResources().getColor(R.color.dark_gray));
                payout_cc.setTextColor(getActivity().getResources().getColor(R.color.orange));
                if(payout_cal_show){

                    layout_loans.setVisibility(View.GONE);
                    layout_cc.setVisibility(View.GONE);
                    payout_product.setText("");
                    payout_referral_type.setText("");
                    payout_loan_amount.setText("");
                } else {
                    layout_loans.setVisibility(View.GONE);
                    layout_cc.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.calculat_payout:
                displayCustomPayoutCalcDialog();

                break;

        }
    }*/

    public void SingleChoiceProduct(Activity activity, String title, final TextView txtView, final TextView amount, final String[] arrList) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(arrList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        txtView.setText(arrList[whichButton]);

                    }
                }).show();
    }

    public void SingleChoiceReferral(Activity activity, String title, final TextView txtView, final String[] arrList) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(arrList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        txtView.setText(arrList[whichButton]);
                    }
                }).show();
    }


    private class HttpAsyncTaskLoans extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                String url = Util.PAYOUT_FOR_LOANS;
                HttpGet httpGet = new HttpGet(url);
//                HttpPost httpPost = new HttpPost(url);
                HttpResponse httpResponse = httpclient.execute(httpGet);

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
            Logs.LogD("PayoutDevesh", result);
            super.onPostExecute(result);


            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.optString("status_code", "").equals("2000")) {
                    loan_type.add("Random");
                    payout1.add("0");
                    payout2.add("1");
                    JSONArray body = output1.optJSONArray("body");
                    if (body != null) {
                        for (int i = 0; i < body.length(); i++) {
                            JSONObject thitype = body.getJSONObject(i);
                            String tt = thitype.optString("name");
                            if (!loan_type.contains(tt)) {
//                                Logs.LogD("ProductType", tt);
                                loan_type.add(tt);
                            }
                        }
                        for (int i = 0; i < body.length(); i++) {
                            JSONObject thitype = body.getJSONObject(i);
                            String tt = thitype.optString("rate_type");
                            if (tt.equals("r")) {
                                Logs.LogD("Payout", thitype.optString("rate"));
                                payout1.add(thitype.optString("rate"));
                            } else if (tt.equals("f")) {
                                payout2.add(thitype.optString("rate"));
                            }
                        }
                        adapter = new PayoutLoansAdapter(getActivity(), payout1, payout2, loan_type);

                        recycler_view_loans.setAdapter(adapter);
//                        recycler_view_loans.setNestedScrollingEnabled(false);

                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

    }

    public class GetPayout extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                String url = Util.CALCULATE_PAYOUT;
                HttpPost httpPost = new HttpPost(url);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("product_type", helper.getProductTypeID(str_product_name));
                jsonObject.put("amount", str_amount);
                if (str_referral_type.equals(referral_name[0])) {
                    str_referral_type = "r";
                } else {
                    str_referral_type = "f";
                }
                jsonObject.put("activity", str_referral_type);
                String json = "";
                json = new String(jsonObject.toString().getBytes("ISO-8859-1"), "UTF-8");
                Logs.LogD("request", json);
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

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


        protected void onPostExecute(String result) {

            Logs.LogD("Result", result);
            try {
                JSONObject res = new JSONObject(result);
                String code = res.optString("status_code", "");
                if (code.equals("2000")) {
                    JSONObject body = res.getJSONObject("body");
                    String payout = body.getString("payout_amount");
                    displayCustomPayoutDialog(str_product_name, str_referral_type, str_amount);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void displayCustomPayoutCalcDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_payout_calc);
        dialog.setTitle("Payout Calculator");
        payout_product = (EditText) dialog.findViewById(R.id.payout_product);
        payout_referral_type = (EditText) dialog.findViewById(R.id.payout_referral_type);
        payout_loan_amount = (EditText) dialog.findViewById(R.id.payout_loan_amount);

        payout_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleChoiceProduct(getActivity(), getActivity().getResources().getString(R.string.product), payout_product, payout_loan_amount, Util.product);
            }
        });
        payout_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Logs.LogD("Choose", s.toString());
                if (s.toString().equals("Credit Card")) {
                    payout_loan_amount.setVisibility(View.GONE);
                    loan_bool = false;
                } else {
                    payout_loan_amount.setVisibility(View.VISIBLE);
                    loan_bool = true;
                }
            }
        });
        btn_cal_payout = (Button) dialog.findViewById(R.id.btn_cal_payout);

        btn_cal_payout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (payout_product.getText().toString().trim().isEmpty()) {
                    payout_product.setError("Please select product");
                    payout_product.requestFocus();
                } else {
                    str_product_name = payout_product.getText().toString().trim();
                    if (payout_referral_type.getText().toString().trim().isEmpty()) {
                        payout_referral_type.setError("Please select type of referral");
                        payout_referral_type.requestFocus();
                    } else {
                        str_referral_type = payout_referral_type.getText().toString().trim();
                        if (str_referral_type.equals(referral_name[0])) {
                            str_referral_type = "r";
                        } else {
                            str_referral_type = "f";
                        }
                        if (loan_bool) {
                            if (payout_loan_amount.getText().toString().trim().isEmpty()) {
                                payout_loan_amount.setError("Please enter loan amount");
                                payout_loan_amount.requestFocus();
                            } else {
                                str_amount = payout_loan_amount.getText().toString().trim();
                                displayCustomPayoutDialog(str_product_name, str_referral_type, str_amount);
                            }
                        } else {
                            str_amount = "0";
                            displayCustomPayoutDialog(str_product_name, str_referral_type, str_amount);

                        }
                    }
                }
            }
        });
        payout_referral_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleChoiceReferral(getActivity(), getActivity().getResources().getString(R.string.referral_type), payout_referral_type, referral_name);
            }
        });
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void displayCustomPayoutDialog(String str_product_name, String str_referral_type, String str_amount) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_payout_dialog);
        pay_bar = (ProgressBar) dialog.findViewById(R.id.payout_wait);
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payout_product.setText("");
                payout_referral_type.setText("");
                payout_loan_amount.setText("");
                dialog.dismiss();
            }
        });

        TextView dialogButton = (TextView) dialog.findViewById(R.id.dialogButtonOK);
        if (str_referral_type.equals("f")) {
            Util.GetFullPayout pay = new Util.GetFullPayout(pay_bar, String.valueOf(helper.getProductTypeID(str_product_name)), str_amount, getContext(), dialogButton, "Upto Rs. ", "", "payout_amount", "0");
            pay.executeOnExecutor(Util.threadPool);
        } else {
            Util.GetPayout pay = new Util.GetPayout(pay_bar, String.valueOf(helper.getProductTypeID(str_product_name)), str_amount, "r", getContext(), dialogButton, "Upto Rs. ", "", "payout_amount");
            pay.executeOnExecutor(Util.threadPool);
        }

        TextView product_name = (TextView) dialog.findViewById(R.id.product_name);
        product_name.setText(str_product_name);
        TextView referral_type = (TextView) dialog.findViewById(R.id.referral_type);
        if (str_referral_type.equals("r")) {
            referral_type.setText("Referral");
        } else {
            referral_type.setText("Application");
        }
        LinearLayout layout_amount = (LinearLayout) dialog.findViewById(R.id.layout_amount);
        TextView loan_amount = (TextView) dialog.findViewById(R.id.loan_amount);
        if (str_amount.equals("0")) {
            layout_amount.setVisibility(View.GONE);
        } else {
            loan_amount.setText("Rs. " + Util.formatRs(str_amount));
        }

        dialog.show();
    }

    private class HttpAsyncTaskCC extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                String url = Util.PAYOUT_FOR_CC;
                HttpGet httpGet = new HttpGet(url);
//                HttpPost httpPost = new HttpPost(url);
                HttpResponse httpResponse = httpclient.execute(httpGet);

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
            Logs.LogD("PayoutDevesh", result);
            super.onPostExecute(result);
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.optString("status_code", "").equals("2000")) {
                    JSONArray body = output1.optJSONArray("body");
                    if (body != null) {
                        cc_type.add("Random");
                        cc_payout1.add("random");
                        cc_payout2.add("random");
                        for (int i = 0; i < body.length(); i++) {
                            JSONObject thitype = body.getJSONObject(i);
                            String tt = thitype.optString("name");
                            if (!cc_type.contains(tt)) {
                                Logs.LogD("ProductType", tt);
                                cc_type.add(tt);
                            }
                        }
                        for (int i = 0; i < body.length(); i++) {
                            JSONObject thitype = body.getJSONObject(i);
                            String tt = thitype.optString("rate_type");
                            if (tt.equals("r")) {
                                cc_payout1.add(thitype.optString("rate"));
                            } else if (tt.equals("f")) {
                                cc_payout2.add(thitype.optString("rate"));
                            }
                        }
                        adapter1 = new PayoutCCAdapter(getActivity(), cc_payout1, cc_payout2, cc_type);

                        recycler_view_cc.setAdapter(adapter1);
//                        recycler_view_cc.setNestedScrollingEnabled(false);

                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

    }


    private class SlabWiseAdapter extends BaseAdapter {


        private final LayoutInflater inflater;
        private JSONArray jsonArray;
        private int a = 1;
        private int b = 1;
        private int c = 1;
        private int d = 1;


        public SlabWiseAdapter(JSONArray jsonArray) {

            this.jsonArray = jsonArray;
            inflater = (LayoutInflater) getActivity().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            if (jsonArray == null) return 0;
            else return jsonArray.length() + 1;

        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            try {
                View rootView;
                if (view == null) {
                    rootView = inflater.inflate(R.layout.list_item_payout_calculator, null);
                } else rootView = view;

                TextView bank = (TextView) rootView.findViewById(R.id.bank);
                TextView slab = (TextView) rootView.findViewById(R.id.slab);
                TextView payout = (TextView) rootView.findViewById(R.id.payout);

                if (i == 0) {
                    bank.setText("BANK");
                    bank.setTextColor(getActivity().getResources().getColor(R.color.Grey800));
                    slab.setText("SLAB(in lakhs)");
                    slab.setTextColor(getActivity().getResources().getColor(R.color.Grey800));
                    payout.setText("PAYOUTS");
                    payout.setTextColor(getActivity().getResources().getColor(R.color.Grey800));
                } else {

                    bank.setText(jsonArray.getJSONObject(i - 1).getString("name"));
                    bank.setTextColor(getActivity().getResources().getColor(R.color.Grey600));

                    String more;
                    if (jsonArray.getJSONObject(i - 1).getString("slab_max").equals("999999999"))
                        more = "more";
                    else more = jsonArray.getJSONObject(i - 1).getString("slab_max");

                    slab.setText(jsonArray.getJSONObject(i - 1).getString("slab_min") + " - " + more);
                    slab.setTextColor(getActivity().getResources().getColor(R.color.Grey600));

                    double payoutValue = Double.parseDouble(jsonArray.getJSONObject(i - 1).getString("payout")) * ((stepsData.getDouble("step1") * a / 100) + (stepsData.getDouble("step2") * b / 100) + (stepsData.getDouble("step3") * c / 100) + (stepsData.getDouble("step4") * d / 100));
                    payoutValue = Math.round(payoutValue * 100.0) / 100.0;
                    payout.setText("" + payoutValue + "%");
                    payout.setTextColor(getActivity().getResources().getColor(R.color.Grey600));


                }


                return rootView;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void updateData(JSONArray json, boolean i, boolean j, boolean k, boolean l) {
            jsonArray = json;
            this.a = i ? 1 : 0;
            this.b = j ? 1 : 0;
            this.c = k ? 1 : 0;
            this.d = l ? 1 : 0;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        slabWiseAdapter.updateData(payoutsArray, step1.isChecked(), step2.isChecked(), step3.isChecked(), step4.isChecked());
    }

}



