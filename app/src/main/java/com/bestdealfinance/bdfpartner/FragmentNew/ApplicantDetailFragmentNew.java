package com.bestdealfinance.bdfpartner.FragmentNew;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicantDetailFragmentNew extends Fragment {

    private static final String TAG = "ApplicantDetailFragment";

    private EditText etApplicantName;
    private EditText etApplicantEmail;
    private EditText etApplicantPhone;
    private EditText etApplicantLoan;
    private EditText etApplicantCity;
    private Button btnSubmitLead;
    private ArrayList<String> cityList;
    private RequestQueue queue;
    private AlertDialog cityDialog;
    private String[] stringCityArray;
    private AlertDialog.Builder builder;
    private AppCompatImageView referIcon;
    private TextView referText;
    private TextView referSubText;
    private AppCompatCheckBox referCheckbox;
    private TextView referAmount;
    private AppCompatImageView appFillIcon;
    private TextView appFillText;
    private TextView appFillSubText;
    private TextView appFillAmount;
    private AppCompatCheckBox appFillCheckbox;
    private AppCompatImageView docPickupIcon;
    private TextView docPickupText;
    private TextView docPickupSubText;
    private TextView docPickupAmount;
    private AppCompatCheckBox docPickupCheckbox;
    private int STAGE = 1;
    private Bundle productBundle;
    private CompoundButton.OnCheckedChangeListener docPickupCheckboxListner;
    private CompoundButton.OnCheckedChangeListener appFillCheckboxListner;
    private DB snappyDB;
    private JSONArray allPayoutJsonArray;
    private JSONArray allStepsJsonArray;
    private JSONArray maxPayoutInEachProductTypeJsonArray;
    private AppCompatImageView disburseIcon;
    private TextView disburseText;
    private TextView disburseSubText;
    private TextView disburseAmount;
    private AppCompatCheckBox disburseCheckbox;
    private CompoundButton.OnCheckedChangeListener disburseCheckboxListner;
    private EditText etProductName;
    private RelativeLayout appFillLayout;
    private RelativeLayout docPickupLayout;
    private RelativeLayout disburseLayout;
    private List<String> cityIdList;
    private String cityItemId;


    private long priceReferOnly;
    private long priceAppFill;
    private long priceDocPickup;
    private long priceDisburment;

    private double percentageReferOnly;
    private double percentageAppFill;
    private double percentageDocPickup;
    private double percentageDisburment;
    private boolean isLoan = false;
    private long loanAmount = 0;

    private String product_id;

    public ApplicantDetailFragmentNew() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        Helper.hideKeyboard(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        etApplicantName.requestFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        queue = Volley.newRequestQueue(getActivity());
        productBundle = getArguments();

        if(productBundle.containsKey("product_id")){
            product_id = productBundle.getString("product_id");
        }

        View rootView = inflater.inflate(R.layout.fragment_applicant_detail_fragment_new, container, false);
        initialise(rootView);
        setBubbleLayout(rootView);
        setCityDialog();
        setStagePayout();


        etApplicantCity.setClickable(true);
        etApplicantCity.setFocusable(false);
        etApplicantCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);*/
                Helper.hideKeyboard(getActivity());
                if (cityDialog != null) {
                    cityDialog.show();
                } else {
                    Toast.makeText(getActivity(), "Loading city list", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSubmitLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Helper.hideKeyboard(getActivity());

                if (Helper.validateEditText(getActivity(), etApplicantName, "text", false) && Helper.validateEditText(getActivity(), etApplicantEmail, "email", false) && Helper.validateEditText(getActivity(), etApplicantPhone, "phone", false)) {

                    String phone = etApplicantPhone.getText().toString().trim();


                    if (!phone.substring(0, 3).contains("00")) {

                        productBundle.putString("stage", "" + STAGE);
                        productBundle.putString(Constant.BundleKey.LOAN_AMOUNT, "" + loanAmount);
                        if ((productBundle.getString("product_type_sought", "").equals("11") || productBundle.getString("product_type_sought", "").equals("11") || productBundle.getString("product_type_sought", "").equals("51") || productBundle.getString("product_type_sought", "").equals("52") || productBundle.getString("product_type_sought", "").equals("53")) && Helper.validateEditText(getActivity(), etApplicantCity, "text", false)) {
                            if (STAGE == 1)
                                sendReferToServer();
                            else {
                                sendToAppFillStage();

                            }
                        } else {

                            if (!TextUtils.isEmpty(etApplicantCity.getText().toString().trim())) {
                                if (STAGE == 1)
                                    sendReferToServer();
                                else {
                                    sendToAppFillStage();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please Select City", Toast.LENGTH_SHORT).show();
                            }

                       /* if (Helper.validateEditText(getActivity(),etApplicantLoan, "text",false) && Helper.validateEditText(getActivity(),etApplicantCity, "text",false)) {

                        }*/

                        }
                    } else {
                        Toast.makeText(getActivity(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return rootView;
    }

    private void sendToAppFillStage() {

        productBundle.putString("name", etApplicantName.getText().toString().trim());
        productBundle.putString("email", etApplicantEmail.getText().toString().trim());
        productBundle.putString("phone", etApplicantPhone.getText().toString().trim());
        productBundle.putString("city", etApplicantCity.getText().toString().trim());
        if (productBundle.getString("product_type_sought", "").equals("11") || productBundle.getString("product_type_sought", "").equals("11") || productBundle.getString("product_type_sought", "").equals("51") || productBundle.getString("product_type_sought", "").equals("52") || productBundle.getString("product_type_sought", "").equals("53")) {
            productBundle.putString("amount", "100000");
        } else
            productBundle.putString("amount", etApplicantLoan.getText().toString().trim().replaceAll(",", ""));
        productBundle.putString("product_id", "");
        productBundle.putString("note", "");
        productBundle.putString("self_refferal", "");
        productBundle.putString("source", "BA_APP");
        productBundle.putString("stage", "" + STAGE);

        try {
            createTupleSendToServer();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //AppFillStage1FragmentNew aNew = new AppFillStage1FragmentNew();
        //aNew.setArguments(productBundle);
        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, aNew, "AppFill1Fragment").commit();
    }

    private void setCityDialog() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select City");

        try {
            snappyDB = DBFactory.open(getActivity());
            if (snappyDB.exists(Constant.DB_REFER_CITY_LIST)) {
                cityList = new ArrayList<>();
                cityIdList = new ArrayList<>();
                JSONArray data = new JSONArray(snappyDB.get(Constant.DB_REFER_CITY_LIST));
                snappyDB.close();
                for (int i = 0; i < data.length(); i++) {
                    if (!data.getJSONObject(i).getString("item_value").equals("Others")) {
                        cityIdList.add(data.getJSONObject(i).getString("id"));
                        cityList.add(data.getJSONObject(i).getString("item_value"));
                    }

                }

                stringCityArray = cityList.toArray(new String[cityList.size()]);
                builder.setItems(stringCityArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        etApplicantCity.setText(stringCityArray[item]);
                        cityItemId = cityIdList.get(item);
                    }
                });
                cityDialog = builder.create();

                JSONObject object = new JSONObject();
                try {
                    object.put("list_id", "10080");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Helper.showLog(URL.GET_UBER_LIST_FULL, object.toString());

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.GET_UBER_LIST_FULL, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("body");
                            snappyDB = DBFactory.open(getActivity());
                            snappyDB.put(Constant.DB_REFER_CITY_LIST, data.toString());
                            snappyDB.close();

                        } catch (JSONException | SnappydbException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(request);

            } else {
                JSONObject object = new JSONObject();
                try {
                    object.put("list_id", "10080");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.GET_UBER_LIST_FULL, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONArray data = response.getJSONArray("body");
                            cityList = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                if (!data.getJSONObject(i).getString("item_value").equals("Others"))
                                    cityList.add(data.getJSONObject(i).getString("item_value"));
                            }

                            stringCityArray = cityList.toArray(new String[cityList.size()]);
                            builder.setItems(stringCityArray, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    etApplicantCity.setText(stringCityArray[item]);
                                }
                            });
                            cityDialog = builder.create();

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
                snappyDB.close();
            }


        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }


        JSONObject object = new JSONObject();
        try {
            object.put("list_id", "10080");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.GET_UBER_LIST_FULL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("body");
                    snappyDB = DBFactory.open(getActivity());
                    snappyDB.put(Constant.DB_REFER_CITY_LIST, data.toString());
                    snappyDB.close();

                } catch (JSONException | SnappydbException e) {
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

    private void createTupleSendToServer() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        JSONArray tupleList = new JSONArray();
        JSONObject tuple = new JSONObject();

        String loanAmount = productBundle.getString("product_type_sought", "").equals("11") || productBundle.getString("product_type_sought", "").equals("51") || productBundle.getString("product_type_sought", "").equals("52") || productBundle.getString("product_type_sought", "").equals("53") ? "100000" : etApplicantLoan.getText().toString().trim().replaceAll(",", "");

        tuple = new JSONObject();

        //TODO For Loan Amount Required=115
        tuple.put("base_id", "115");
        tuple.put("field_value", loanAmount );
        tuple.put("list_item_id", "");
        tupleList.put(tuple);

        //TODO For Phone=5
        tuple = new JSONObject();
        tuple.put("base_id", "5");
        tuple.put("field_value", etApplicantPhone.getText().toString().trim());
        tuple.put("list_item_id", "");
        tupleList.put(tuple);
        //TODO For Email=6
        tuple = new JSONObject();
        tuple.put("base_id", "6");
        tuple.put("field_value", etApplicantEmail.getText().toString().trim());
        tuple.put("list_item_id", "");
        tupleList.put(tuple);
        //TODO For First Name=2
        String name = etApplicantName.getText().toString().trim();
        String names[] = name.split(" ");
        tuple = new JSONObject();
        tuple.put("base_id", "2");
        tuple.put("field_value", names[0]);
        tuple.put("list_item_id", "");
        tupleList.put(tuple);
        //TODO Last Name =4
        if (names.length > 1) {
            tuple = new JSONObject();
            tuple.put("base_id", "4");
            tuple.put("field_value", names[names.length - 1]);
            tuple.put("list_item_id", "");
            tupleList.put(tuple);
        }


        tuple = new JSONObject();
        tuple.put("base_id", "25");
        tuple.put("field_value", etApplicantCity.getText().toString());
        tuple.put("list_item_id", cityItemId);
        tupleList.put(tuple);


        jsonObject.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
        jsonObject.put("product_type_sought", productBundle.getString("product_type_sought", ""));
        jsonObject.put("city",cityItemId);
        jsonObject.put("loan_amount_needed",loanAmount);
        jsonObject.put("tuple_list", tupleList);
        jsonObject.put("source", "BA_APP");
        jsonObject.put("stage", "" + STAGE);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Creating Lead");
        progressDialog.show();

        Helper.showLog(URL.CREATE_INCOMPLETE_LEAD, jsonObject.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.CREATE_INCOMPLETE_LEAD, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.d("Lead Created", response.toString());
                JSONObject body = response.optJSONObject("body");
                productBundle.putString("lead_id", body.optString("lead_id"));
                AppFillGroupFragment aNew = new AppFillGroupFragment();
                if(!TextUtils.isEmpty(product_id))
                    productBundle.putString("product_id",product_id);
                aNew.setArguments(productBundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, aNew, "ApplicantFillFragment").commit();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        });

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 2, 1f));
        queue.add(request);
    }

    private void setBubbleLayout(View rootView) {


        referIcon = (AppCompatImageView) rootView.findViewById(R.id.refer_icon);
        referText = (TextView) rootView.findViewById(R.id.refer_text);
        referSubText = (TextView) rootView.findViewById(R.id.refer_subtext);
        referAmount = (TextView) rootView.findViewById(R.id.refer_amount);
        referCheckbox = (AppCompatCheckBox) rootView.findViewById(R.id.refer_checkbox);

        appFillLayout = (RelativeLayout) rootView.findViewById(R.id.app_fill_layout);
        appFillIcon = (AppCompatImageView) rootView.findViewById(R.id.app_fill_icon);
        appFillText = (TextView) rootView.findViewById(R.id.app_fill_text);
        appFillSubText = (TextView) rootView.findViewById(R.id.app_fill_subtext);
        appFillAmount = (TextView) rootView.findViewById(R.id.app_fill_amount);
        appFillCheckbox = (AppCompatCheckBox) rootView.findViewById(R.id.app_fill_checkbox);


        docPickupLayout = (RelativeLayout) rootView.findViewById(R.id.doc_pickup_layout);
        docPickupIcon = (AppCompatImageView) rootView.findViewById(R.id.doc_pickup_icon);
        docPickupText = (TextView) rootView.findViewById(R.id.doc_pickup_text);
        docPickupSubText = (TextView) rootView.findViewById(R.id.doc_pickup_subtext);
        docPickupAmount = (TextView) rootView.findViewById(R.id.doc_pickup_amount);
        docPickupCheckbox = (AppCompatCheckBox) rootView.findViewById(R.id.doc_pickup_checkBox);


        disburseLayout = (RelativeLayout) rootView.findViewById(R.id.disburse_layout);
        disburseIcon = (AppCompatImageView) rootView.findViewById(R.id.disburse_icon);
        disburseText = (TextView) rootView.findViewById(R.id.disburse_text);
        disburseSubText = (TextView) rootView.findViewById(R.id.disburse_subtext);
        disburseAmount = (TextView) rootView.findViewById(R.id.disburse_amount);
        disburseCheckbox = (AppCompatCheckBox) rootView.findViewById(R.id.disburse_checkBox);


        referCheckbox.setChecked(true);
        referCheckbox.setClickable(false);
        referIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Blue400));
        referAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Green600));


        try {
            snappyDB = DBFactory.open(getActivity());
            JSONObject products = new JSONObject(snappyDB.get(Constant.DB_ALL_PRODUCTS_DETAILS_OBJECT));
            snappyDB.close();
            JSONArray jsonArray = products.optJSONArray("list_item_products");
            JSONObject productObject = null;

            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("id").equals(productBundle.getString("product_type_sought"))) {
                    productObject = jsonArray.getJSONObject(i);
                }
            }

            if (productObject != null) {
                switch (productObject.getString("stage")) {
                    case "1": {
                        appFillLayout.setVisibility(View.GONE);
                    }
                    case "2": {
                        docPickupLayout.setVisibility(View.GONE);
                    }
                    case "3": {
                        disburseLayout.setVisibility(View.GONE);
                    }
                    case "4":
                        break;
                }
            }


        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }


        appFillCheckboxListner = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    STAGE = 2;
                    appFillIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Blue400));
                    appFillAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Green600));
                    referAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey300));
                    if (!isLoan)
                        appFillAmount.setText("Rs " + Math.round(priceAppFill + priceReferOnly));
                    else {
                        if (loanAmount > 0) {
                            appFillAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount)));
                        } else {
                            referAmount.setText(percentageReferOnly + " %");
                            appFillAmount.setText(percentageAppFill + " %");
                            docPickupAmount.setText(percentageDocPickup + " %");
                            disburseAmount.setText(percentageDisburment + " %");
                        }
                    }
                    // by prasad

                } else {
                    STAGE = 1;
                    appFillIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Grey400));
                    appFillAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Grey300));

                    docPickupCheckbox.setOnCheckedChangeListener(null);
                    docPickupCheckbox.setChecked(false);
                    docPickupCheckbox.setOnCheckedChangeListener(docPickupCheckboxListner);
                    docPickupIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Grey400));
                    docPickupAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Grey300));

                    disburseCheckbox.setOnCheckedChangeListener(null);
                    disburseCheckbox.setChecked(false);
                    disburseCheckbox.setOnCheckedChangeListener(disburseCheckboxListner);
                    disburseIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Grey400));
                    disburseAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Grey300));

                    // by prasad
                    if (isLoan) {
                        if (loanAmount > 0) {
                            Log.i(TAG, "onCheckedChanged: loan > 0 ");
                            appFillAmount.setText("+ Rs " + Math.round(getPriceFromPercentage(percentageAppFill, loanAmount)/*+getPriceFromPercentage(percentageReferOnly,loanAmount)*/));
                            docPickupAmount.setText("+ Rs " + Math.round(getPriceFromPercentage(percentageDocPickup, loanAmount)/*+getPriceFromPercentage(percentageAppFill,loanAmount)+getPriceFromPercentage(percentageReferOnly,loanAmount)*/));
                            disburseAmount.setText("+ Rs " + Math.round(getPriceFromPercentage(percentageDisburment, loanAmount)/*+getPriceFromPercentage(percentageDocPickup,loanAmount)+getPriceFromPercentage(percentageAppFill,loanAmount)+getPriceFromPercentage(percentageReferOnly,loanAmount)*/));
                        } else {
                            Log.i(TAG, "onCheckedChanged: loan = 0 " + percentageReferOnly);
                            referAmount.setText(percentageReferOnly + " %");
                            appFillAmount.setText(percentageAppFill + " %");
                            docPickupAmount.setText(percentageDocPickup + " %");
                            disburseAmount.setText(percentageDisburment + " %");
                        }
                    } else {
                        disburseAmount.setText("+ Rs " + priceDisburment);
                        docPickupAmount.setText("+ Rs " + priceDocPickup);
                        appFillAmount.setText("+ Rs " + priceAppFill);
                    }
                    referAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Green600));
                }

            }
        };

        docPickupCheckboxListner = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    STAGE = 3;

                    docPickupIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Blue400));
                    docPickupAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Green600));


                    appFillIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Blue400));
                    appFillAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Green600));
                    appFillCheckbox.setOnCheckedChangeListener(null);
                    appFillCheckbox.setChecked(true);
                    appFillCheckbox.setOnCheckedChangeListener(appFillCheckboxListner);
                    docPickupAmount.setText("Rs " + Math.round(priceReferOnly + priceAppFill + priceDocPickup));

                    referAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey300));
                    appFillAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey300));
                    disburseAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey300));

                    if (isLoan) {
                        if (loanAmount > 0) {
                            docPickupAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageDocPickup, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageReferOnly, loanAmount)));
                        } else {
                            referAmount.setText(percentageReferOnly + " %");
                            appFillAmount.setText(percentageAppFill + " %");
                            docPickupAmount.setText(percentageDocPickup + " %");
                            disburseAmount.setText(percentageDisburment + " %");
                        }
                    } else {
                        disburseAmount.setText("+ Rs " + Math.round(priceDisburment));
                        appFillAmount.setText("Rs " + Math.round(priceAppFill));
                    }
                } else {

                    STAGE = 2;

                    docPickupIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Grey400));
                    docPickupAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Grey300));

                    disburseCheckbox.setOnCheckedChangeListener(null);
                    disburseCheckbox.setChecked(false);
                    disburseCheckbox.setOnCheckedChangeListener(disburseCheckboxListner);
                    disburseIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Grey400));
                    disburseAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Grey300));

                    if (isLoan) {
                        if (loanAmount > 0) {
                            docPickupAmount.setText("+ Rs " + Math.round(getPriceFromPercentage(percentageDocPickup, loanAmount)/*+getPriceFromPercentage(percentageAppFill,loanAmount)+getPriceFromPercentage(percentageReferOnly,loanAmount)*/));
                            disburseAmount.setText("+ Rs " + Math.round(getPriceFromPercentage(percentageDisburment, loanAmount)/*+getPriceFromPercentage(percentageDocPickup,loanAmount)+getPriceFromPercentage(percentageAppFill,loanAmount)+getPriceFromPercentage(percentageReferOnly,loanAmount)*/));
                            appFillAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageReferOnly, loanAmount)));
                        } else {
                            referAmount.setText(percentageReferOnly + " %");
                            appFillAmount.setText(percentageAppFill + " %");
                            docPickupAmount.setText(percentageDocPickup + " %");
                            disburseAmount.setText(percentageDisburment + " %");
                        }
                    } else {
                        // prasad
                        disburseAmount.setText("+ Rs " + Math.round(priceDisburment));
                        docPickupAmount.setText("+ Rs " + Math.round(priceDocPickup));
                        appFillAmount.setText("Rs " + Math.round(priceAppFill + priceReferOnly));
                    }
                    appFillAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Green600));
                }
            }
        };

        disburseCheckboxListner = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    STAGE = 4;

                    disburseIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Blue400));
                    disburseAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Green600));


                    appFillIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Blue400));
                    appFillAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Green600));
                    appFillCheckbox.setOnCheckedChangeListener(null);
                    appFillCheckbox.setChecked(true);
                    appFillCheckbox.setOnCheckedChangeListener(appFillCheckboxListner);


                    docPickupIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Blue400));
                    docPickupAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Green600));
                    docPickupCheckbox.setOnCheckedChangeListener(null);
                    docPickupCheckbox.setChecked(true);
                    docPickupCheckbox.setOnCheckedChangeListener(docPickupCheckboxListner);
                    disburseAmount.setText("Rs " + (priceDisburment + priceReferOnly + priceAppFill + priceDocPickup));

                    referAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey300));
                    appFillAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey300));
                    docPickupAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey300));

                    docPickupAmount.setText("Rs " + Math.round(priceDocPickup));
                    appFillAmount.setText("Rs " + Math.round(priceAppFill));

                    if (isLoan) {
                        if (loanAmount > 0) {
                            disburseAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageDisburment, loanAmount) + getPriceFromPercentage(percentageDocPickup, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageReferOnly, loanAmount)));
                            appFillAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageAppFill, loanAmount)));
                            docPickupAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageDocPickup, loanAmount)));
                        } else {
                            referAmount.setText(percentageReferOnly + " %");
                            appFillAmount.setText(percentageAppFill + " %");
                            docPickupAmount.setText(percentageDocPickup + " %");
                            disburseAmount.setText(percentageDisburment + " %");
                        }
                    }

                } else {

                    STAGE = 3;

                    disburseIcon.setSupportBackgroundTintList(getActivity().getResources().getColorStateList(R.color.Grey400));
                    disburseAmount.setTextColor(getActivity().getResources().getColorStateList(R.color.Grey300));
                    disburseAmount.setText("+ Rs " + Math.round(priceDisburment));
                    docPickupAmount.setText("Rs " + Math.round(priceDocPickup + priceAppFill + priceReferOnly));

                    if (isLoan) {
                        if (loanAmount > 0) {
                            docPickupAmount.setText(" Rs " + Math.round(getPriceFromPercentage(percentageDocPickup, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageReferOnly, loanAmount)));
                            disburseAmount.setText(" Rs " + Math.round(getPriceFromPercentage(percentageDisburment, loanAmount)));
                            appFillAmount.setText(" Rs " + Math.round(getPriceFromPercentage(percentageAppFill, loanAmount)));
                        } else {
                            referAmount.setText(percentageReferOnly + " %");
                            appFillAmount.setText(percentageAppFill + " %");
                            docPickupAmount.setText(percentageDocPickup + " %");
                            disburseAmount.setText(percentageDisburment + " %");
                        }
                    }
                    docPickupAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Green600));
                }
            }
        };

        etApplicantName.setClickable(true);
        etApplicantName.setFocusable(true);
        etApplicantName.requestFocus();

        appFillCheckbox.setOnCheckedChangeListener(appFillCheckboxListner);
        docPickupCheckbox.setOnCheckedChangeListener(docPickupCheckboxListner);
        disburseCheckbox.setOnCheckedChangeListener(disburseCheckboxListner);
    }

    private void initialise(View rootView) {
        etProductName = (EditText) rootView.findViewById(R.id.product_name);
        etApplicantName = (EditText) rootView.findViewById(R.id.applicant_name);
        etApplicantEmail = (EditText) rootView.findViewById(R.id.applicant_email);
        etApplicantPhone = (EditText) rootView.findViewById(R.id.applicant_phone);
        etApplicantLoan = (EditText) rootView.findViewById(R.id.applicant_amount);
        etApplicantLoan.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
        etApplicantCity = (EditText) rootView.findViewById(R.id.applicant_city);
        btnSubmitLead = (Button) rootView.findViewById(R.id.submit_lead_button);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(getActivity(), toolbar, "Create New Lead", false, true, true);

        if (productBundle.getString("product_type_sought", "").equals("11") || productBundle.getString("product_type_sought", "").equals("51") || productBundle.getString("product_type_sought", "").equals("52") || productBundle.getString("product_type_sought", "").equals("53")) {
            rootView.findViewById(R.id.loan_amount_layout).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.loan_amount_layout).setVisibility(View.VISIBLE);
        }

        etApplicantLoan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etApplicantLoan.getText().toString().trim().length() > 0) {
                    String strAmount = etApplicantLoan.getText().toString().trim().replaceAll(",", "");
                    loanAmount = Long.parseLong(strAmount);
                    Log.i(TAG, "afterTextChanged: " + strAmount);

                    referAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount)));

                    if (disburseCheckbox.isChecked()) {
                        appFillAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount)));
                        docPickupAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageDocPickup, loanAmount)));
                        disburseAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageDocPickup, loanAmount) + getPriceFromPercentage(percentageDisburment, loanAmount)));
                    } else if (docPickupCheckbox.isChecked()) {
                        appFillAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount)));
                        docPickupAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageDocPickup, loanAmount)));
                        disburseAmount.setText("+ Rs " + Math.round(getPriceFromPercentage(percentageDisburment, loanAmount)));
                    } else if (appFillCheckbox.isChecked()) {
                        appFillAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount)));
                        disburseAmount.setText("+ Rs " + Math.round(getPriceFromPercentage(percentageDisburment, loanAmount)));
                        docPickupAmount.setText("+ Rs " + Math.round(getPriceFromPercentage(percentageDocPickup, loanAmount)));
                    }


                    Log.i(TAG, "afterTextChanged: prefer " + getPriceFromPercentage(percentageReferOnly, loanAmount));
                    if (appFillCheckbox.isChecked()) {
                        appFillAmount.setText("Rs " + Math.round((getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount))));
                    } else {
                        appFillAmount.setText("+ Rs " + Math.round((getPriceFromPercentage(percentageAppFill, loanAmount))));
                        disburseAmount.setText("+ Rs " + Math.round((getPriceFromPercentage(percentageDisburment, loanAmount))));
                        docPickupAmount.setText("+ Rs " + Math.round((getPriceFromPercentage(percentageDocPickup, loanAmount))));
                    }
                    if (disburseCheckbox.isChecked()) {
                        appFillAmount.setText("Rs " + Math.round((getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount))));
                        disburseAmount.setText("Rs " + Math.round((getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageDisburment, loanAmount))));
                    }
                    if (docPickupCheckbox.isChecked()) {
                        disburseAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageDisburment, loanAmount)));
                        appFillAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount)));
                        docPickupAmount.setText("Rs " + Math.round(getPriceFromPercentage(percentageReferOnly, loanAmount) + getPriceFromPercentage(percentageAppFill, loanAmount) + getPriceFromPercentage(percentageDocPickup, loanAmount) + getPriceFromPercentage(percentageDisburment, loanAmount)));
                    }

                    if (loanAmount > 0) {
                        Log.i(TAG, "afterTextChanged: greater than zero ");
                        etApplicantLoan.removeTextChangedListener(this);
                        etApplicantLoan.setText(Helper.formatRs(s.toString()));
                        etApplicantLoan.addTextChangedListener(this);
                        etApplicantLoan.setSelection(etApplicantLoan.getText().length());
                    }
                } else {
                    loanAmount = 0;
                    Log.i(TAG, "afterTextChanged: zero ");
                    referAmount.setText(percentageReferOnly + " %");
                    appFillAmount.setText(percentageAppFill + " %");
                    docPickupAmount.setText(percentageDocPickup + " %");
                    disburseAmount.setText(percentageDisburment + " %");
                }
            }
        });

        etProductName.setText(productBundle.getString("product_type_name"));
        etProductName.setTextColor(getResources().getColor(R.color.Grey800));
        etProductName.setEnabled(false);

        etApplicantName.requestFocus();


    }

    private void sendReferToServer() {
        try {

            JSONObject jsonObject = new JSONObject();
            JSONArray tupleList = new JSONArray();
            JSONObject tuple ;



            String name = etApplicantName.getText().toString().trim();
            String names[] = name.split(" ");

            tuple = new JSONObject();
            tuple.put("base_id", "2");
            tuple.put("field_value",names[0]);
            tuple.put("field_ui", "alpha");
            tupleList.put(tuple);

            //TODO For last name
            if (names.length > 1) {
                tuple = new JSONObject();
                tuple.put("base_id", "4");
                tuple.put("field_value", names[names.length - 1]);
                tuple.put("field_ui", "alpha");
                tupleList.put(tuple);
            }

            //TODO For phone
            tuple = new JSONObject();
            tuple.put("base_id", "5");
            tuple.put("field_value",etApplicantPhone.getText().toString().trim());
            tuple.put("field_ui", "mobile");
            tupleList.put(tuple);

            //TODO For Email=6
            tuple = new JSONObject();
            tuple.put("base_id", "6");
            tuple.put("field_value", etApplicantEmail.getText().toString().trim());
            tuple.put("field_ui", "email");
            tupleList.put(tuple);
            //TODO For First Name=2

            tuple = new JSONObject();
            tuple.put("base_id", "25");
            tuple.put("field_value", etApplicantCity.getText().toString());
            tuple.put("field_ui", "static");
            tuple.put("list_item_id",cityItemId);
            tupleList.put(tuple);

            tuple = new JSONObject();
            tuple.put("base_id", "115");
            if (productBundle.getString("product_type_sought", "").equals("11") || productBundle.getString("product_type_sought", "").equals("51") || productBundle.getString("product_type_sought", "").equals("52") || productBundle.getString("product_type_sought", "").equals("53")) {
                tuple.put("field_value", "0");
            } else
                tuple.put("field_value",etApplicantLoan.getText().toString().trim().replaceAll(",", ""));

            tuple.put("field_ui", "iwc");
            tupleList.put(tuple);


            jsonObject.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            jsonObject.put("product_type_sought", productBundle.getString("product_type_sought", ""));
            jsonObject.put("tuple_list", tupleList);
//            jsonObject.put("source", "BA_APP");
//            jsonObject.put("stage", "" + STAGE);

            //=======================================================================

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait..");
            progressDialog.setTitle("Referring your lead");
            progressDialog.show();


            /*productBundle.putString("name", etApplicantName.getText().toString().trim());
            productBundle.putString("email", etApplicantEmail.getText().toString().trim());
            productBundle.putString("phone", etApplicantPhone.getText().toString().trim());
            productBundle.putString("city", etApplicantCity.getText().toString().trim());
            if (productBundle.getString("product_type_sought", "").equals("11") || productBundle.getString("product_type_sought", "").equals("51") || productBundle.getString("product_type_sought", "").equals("52") || productBundle.getString("product_type_sought", "").equals("53")) {
                productBundle.putString("amount", "0");
            } else
                productBundle.putString("amount", etApplicantLoan.getText().toString().trim().replaceAll(",", ""));
            productBundle.putString("product_id", "");
            productBundle.putString("note", "");
            productBundle.putString("self_refferal", "");
            productBundle.putString("source", "BA_APP");
            productBundle.putString("stage", "" + STAGE);


            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            jsonObject.put("name", etApplicantName.getText().toString().trim());
            jsonObject.put("email", etApplicantEmail.getText().toString().trim());
            jsonObject.put("phone", etApplicantPhone.getText().toString().trim());
            jsonObject.put("city", etApplicantCity.getText().toString().trim());
            if (!productBundle.getString("product_type_sought", "").equals("11")) {
                jsonObject.put("amount", etApplicantLoan.getText().toString().trim().replaceAll(",", ""));
            }
            jsonObject.put("product_type_sought", productBundle.getString("product_type_sought", ""));
            jsonObject.put("product_id", "");
            jsonObject.put("note", "");
            jsonObject.put("self_refferal", "");
            jsonObject.put("source", "BA_APP");
            jsonObject.put("stage", "" + STAGE);*/

            Helper.showLog(URL.CREATE_INCOMPLETE_LEAD, jsonObject.toString());

            Log.i(TAG, "sendReferToServer: URL  "+URL.REFER_A_LEAD+" params "+jsonObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,  URL.REFER_A_LEAD , jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Lead Submitted", Toast.LENGTH_LONG).show();



                    Log.i(TAG, "onResponse: "+response.toString()+" "+etApplicantLoan.getText().toString().trim());
//                    {"status_code":2000,"msg":"Success","body":{"lead_id":14754}}


                    if(response.optInt("status_code")==2000) {
                        String loan = etApplicantLoan.getText().toString().trim().replace(",","");

                        JSONObject body = response.optJSONObject("body");


                        productBundle.putString("lead_id", "" + body.optInt("lead_id"));
                        productBundle.putString("name", etApplicantName.getText().toString().trim());
                        Log.i(TAG, "onResponse: "+loan);
                        productBundle.putString("amount", loan);

                        CongratulationFragment aNew = new CongratulationFragment();
                        aNew.setArguments(productBundle);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, aNew, CongratulationFragment.TAG).commit();

                    }


                   /* Intent intent = new Intent(CongratulationScreen.this, FinalCongrats.class);
                    bundle.putString("id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();*/

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Message");
                    builder.setCancelable(false);
                    builder.setMessage(error.getMessage());
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1f));
            queue.add(jsonObjectRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setStagePayout() {
        try {
            snappyDB = DBFactory.open(getActivity());
            allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
            allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
            maxPayoutInEachProductTypeJsonArray = getMaxInPayoutArray();
            snappyDB.close();
            double payout = 0;
            JSONObject stepObject = null;

            boolean isProductIdExist = false;
            if(!TextUtils.isEmpty(product_id)){
                for (int i = 0; i < allPayoutJsonArray.length(); i++) {
                    JSONObject jsonObject = allPayoutJsonArray.getJSONObject(i);
                    if(jsonObject.optString("product").equals(product_id)){
                        isProductIdExist = true;
                        payout = jsonObject.optDouble("payout");
                        break;
                    }
                }
            }
            if(!isProductIdExist) {
                for (int i = 0; i < maxPayoutInEachProductTypeJsonArray.length(); i++) {
                    if (maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                        payout = maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getDouble("payout");
                    }
                }
            }

            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                    stepObject = allStepsJsonArray.getJSONObject(i);
                }
            }

            if (stepObject != null) {

                if (productBundle.getString("product_type_sought").equals("11")) {

                    priceReferOnly = Math.round(100000 * payout * stepObject.optDouble("step1", 0) / 10000);
                    priceAppFill = Math.round(100000 * payout * stepObject.optDouble("step2", 0) / 10000);
                    priceDocPickup = Math.round(100000 * payout * stepObject.optDouble("step3", 0) / 10000);
                    priceDisburment = Math.round(100000 * payout * stepObject.optDouble("step4", 0) / 10000);

                    referAmount.setText(" Rs " + Math.round(100000 * payout * stepObject.optDouble("step1", 0) / 10000) + " ");
                    appFillAmount.setText("+ Rs " + Math.round(100000 * payout * stepObject.optDouble("step2", 0) / 10000) + " ");
                    docPickupAmount.setText("+ Rs " + Math.round(100000 * payout * stepObject.optDouble("step3", 0) / 10000) + " ");
                    disburseAmount.setText("+ Rs " + Math.round(100000 * payout * stepObject.optDouble("step4", 0) / 10000) + " ");

                    isLoan = false;
                } else {
                    referAmount.setText(" " + (payout * stepObject.optDouble("step1", 0) / 100) + " %");
                    appFillAmount.setText("+ " + (payout * stepObject.optDouble("step2", 0) / 100) + " %");
                    docPickupAmount.setText("+ " + (payout * stepObject.optDouble("step3", 0) / 100) + " %");
                    disburseAmount.setText("+ " + (payout * stepObject.optDouble("step4", 0) / 100) + " %");

                    percentageReferOnly = (payout * stepObject.optDouble("step1", 0) / 100);
                    percentageAppFill = (payout * stepObject.optDouble("step2", 0) / 100);
                    percentageDocPickup = (payout * stepObject.optDouble("step3", 0) / 100);
                    percentageDisburment = (payout * stepObject.optDouble("step4", 0) / 100);
                    isLoan = true;
                }
            } else {
                Log.i(TAG, "initializeDashboardPayoutsFromDB:  json is null now");
                referAmount.setText(" 0 %");
                appFillAmount.setText("+ 0 %");
                docPickupAmount.setText("+ 0 %");
                disburseAmount.setText("+ 0 %");
            }


        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray getMaxInPayoutArray() {
        JSONArray resultJsonArray = new JSONArray();

        if (allStepsJsonArray != null && allStepsJsonArray.length() > 0) {
            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                try {
                    String productTypeId = allStepsJsonArray.getJSONObject(i).getString("product_type");
                    JSONObject newJsonObject = new JSONObject();
                    newJsonObject.put("product_type", productTypeId);
                    newJsonObject.put("payout", 0);
                    /* "payout": "0.5000",
                       "product_type": "11",*/
                    resultJsonArray.put(i, newJsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        if (allPayoutJsonArray != null && allPayoutJsonArray.length() > 0) {
            for (int i = 0; i < allPayoutJsonArray.length(); i++) {
                try {
                    String productTypeId = allPayoutJsonArray.getJSONObject(i).getString("product_type");
                    String payout = allPayoutJsonArray.getJSONObject(i).getString("payout");
                    int position = 0;
                    for (int j = 0; j < resultJsonArray.length(); j++) {
                        if (productTypeId.equals(resultJsonArray.getJSONObject(j).getString("product_type"))) {
                            position = j;
                            break;
                        }
                    }
                    float previousValue = Float.parseFloat(resultJsonArray.getJSONObject(position).getString("payout"));
                    if (previousValue < Float.parseFloat(payout)) {
                        resultJsonArray.getJSONObject(position).put("payout", payout);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultJsonArray;
    }

    private double getPriceFromPercentage(double percentageRate, long price) {
        double priceDiscount = (price / 100) * percentageRate;
        return priceDiscount;
    }
}
