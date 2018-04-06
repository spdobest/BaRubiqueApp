package com.bestdealfinance.bdfpartner.FragmentNew;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bestdealfinance.bdfpartner.ActivityNew.SelectRfcActivityNew;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bumptech.glide.Glide;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class CongratulationFragment extends Fragment {

    public static final String TAG = "CongratulationFragment";

    private RequestQueue queue;
    private Bundle productBundle;
    private TextView tvEarning;
    private ImageView productIcon;
    private TextView tvApplicantName;
    private TextView tvProduct;
    private TextView tvLoanAmount;
    private TextView tvLeadId;
    private TextView tvDate;
    private Button btnContinue;
    private JsonObjectRequest request;
    private RelativeLayout loanLayout;
    private DB snappyDB;
    private JSONArray allStepsJsonArray;
    private JSONArray allPayoutJsonArray;
    private JSONArray maxPayoutInEachProductTypeJsonArray;
    private TextView tvCongratMsg;

    String productTypeSought;

    public CongratulationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_congratulation, container, false);
        queue = Volley.newRequestQueue(getActivity());
        productBundle = getArguments();
        productTypeSought = productBundle.getString("product_type_sought");
        initialize(rootView);
        setMessagesAndButtons();
        setLeadData();
        setExactPayout();
        return rootView;
    }

    private void setMessagesAndButtons() {
        if (productBundle.getString("stage", "1").equals("1")) {
            btnContinue.setText("FINISH");
            tvCongratMsg.setText("You have succesfully referred a lead.");
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

        } else if (productBundle.getString("stage", "2").equals("2")) {
            btnContinue.setText("FINISH");
            tvCongratMsg.setText("You have succesfully filled an application.");
            updateLead();
        } else {
            btnContinue.setText("CONTINUE TO NEXT STAGE");
            updateLead();
        }


    }

    private void setExactPayout() {
        try {
            if (productTypeSought.equals("51") || productTypeSought.equals("52") || productTypeSought.equals("53") ) {
                tvEarning.setText("");
            }
            else
                tvEarning.setText("Calculating...");

            JSONObject stepObject = null;
            double payout = 0;
            snappyDB = DBFactory.open(getActivity());
            allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
            allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                    stepObject = allStepsJsonArray.getJSONObject(i);
                }
            }

            if (productBundle.getString("stage", "1").equals("1")) {
                maxPayoutInEachProductTypeJsonArray = getMaxInPayoutArray();
                for (int i = 0; i < maxPayoutInEachProductTypeJsonArray.length(); i++) {
                    if (maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                        payout = maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getDouble("payout");
                    }
                }
                double loan = 0;
                if (productBundle.getString("product_type_sought").equals("11")) {
                    loan = 100000;
                } else {
                    String loanAmount = productBundle.getString("amount");
                    if(!TextUtils.isEmpty(loanAmount))
                        loan = Double.parseDouble(productBundle.getString("amount"));
                }
                if(stepObject!=null && stepObject.has("step1"))
                    tvEarning.setText("Rs " + Math.round(loan * payout * stepObject.getDouble("step1") / 10000));

            } else {

                JSONObject object = new JSONObject();

                object.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
                object.put("lead_id", productBundle.getString("lead_id"));
                final JSONObject finalStepObject = stepObject;
                Helper.showLog(URL.GET_EXACT_PAYOUT,object.toString());
                JsonObjectRequest request = new JsonObjectRequest(URL.GET_EXACT_PAYOUT, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject body = response.getJSONObject("body");
                            Double epayout = body.getDouble("payouts");

                            tvEarning.setText("*Rs " + Math.round(epayout * (finalStepObject.getDouble("step1") + finalStepObject.getDouble("step2")) / 100));

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


        } catch (JSONException | SnappydbException e) {
            e.printStackTrace();
        }
    }

    private void setLeadData() {
        tvApplicantName.setText(productBundle.getString("name"));
        tvLeadId.setText(productBundle.getString("lead_id"));
        if (productTypeSought.equals("11") ||  productTypeSought.equals("51") || productTypeSought.equals("52") || productTypeSought.equals("53") ) {
            loanLayout.setVisibility(View.GONE);
        } else {
            loanLayout.setVisibility(View.VISIBLE);
            tvLoanAmount.setText("Rs " + productBundle.getString("amount"));
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        tvDate.setText(df.format(calendar.getTime()));

        switch (productBundle.getString("product_type_sought")) {
            case "11":
                Glide.with(getActivity()).load(R.drawable.ic_product_credit_card).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_car_loan));
                break;
            case "22":
//                Glide.with(getActivity()).load(R.drawable.ic_product_car_loan).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_car_loan));
                break;
            case "23":
//                Glide.with(getActivity()).load(R.drawable.ic_product_two_wheeler_loan).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_two_wheeler_loan));
                break;
            case "24":
                Glide.with(getActivity()).load(R.drawable.ic_product_credit_card).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_car_loan));
                break;
            case "25":
                Glide.with(getActivity()).load(R.drawable.ic_product_personal_loan).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_car_loan));
                break;
            case "26":
//                Glide.with(getActivity()).load(R.drawable.ic_product_home_loan).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_home_loan));
                break;
            case "28":
//                Glide.with(getActivity()).load(R.drawable.ic_product_loan_against_property).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_loan_against_property));
                break;
            case "29":
//                Glide.with(getActivity()).load(R.drawable.ic_product_car_loan).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_car_loan));
                break;
            case "51":
//                Glide.with(getActivity()).load(R.drawable.ic_product_life_insurance).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_life_insurance));
                break;
            case "52":
//                Glide.with(getActivity()).load(R.drawable.ic_product_general_insurance).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_general_insurance));
                break;
            case "53":
//                Glide.with(getActivity()).load(R.drawable.ic_product_health_insurance).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_health_insurance));
                break;
            default:
//                Glide.with(getActivity()).load(R.drawable.ic_product_business_loan).into(productIcon);
                productIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_product_business_loan));
                break;
        }

        tvProduct.setText(productBundle.getString("product_type_name"));


    }

    private void updateLead() {
        try {
            JSONObject object = new JSONObject();
            object.put("lead_id", productBundle.getString("lead_id"));
            object.put("product_id", productBundle.getString("product_id", ""));
            object.put("product_type_sought", productBundle.getString("product_type_sought"));
            object.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setTitle("Updating Lead");
            progressDialog.show();
            Helper.showLog(URL.UPDATE_INCOMPLETE_LEAD,object.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.UPDATE_INCOMPLETE_LEAD, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    submitApplicationToServer();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error Updating", Toast.LENGTH_LONG).show();


                }
            });

            request.setShouldCache(false);
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 2, 1f));
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void submitApplicationToServer() {
        try {

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Submitting Application");
            progressDialog.setCancelable(false);
            progressDialog.show();

            JSONObject object = new JSONObject();
            object.put("lead_id", productBundle.getString("lead_id"));
            object.put("force_select", productBundle.getString("force_select"));
            object.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));

            Helper.showLog(URL.SUBMIT_APPLICATION_BA,object.toString());

            request = new JsonObjectRequest(URL.SUBMIT_APPLICATION_BA, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    Log.d("SubmitApplication", response.toString());
                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (productBundle.getString("stage", "2").equals("2")) {
                                getActivity().finish();
                            } else {
                                startActivity(new Intent(getActivity(), SelectRfcActivityNew.class).putExtra("productBundle", productBundle));
                                getActivity().finish();
                            }

                        }
                    });


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("SubmitApplication", "Server Error... Retrying....");
                    queue.add(request);
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 10, 2F));
            queue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialize(View rootView) {

        tvEarning = (TextView) rootView.findViewById(R.id.lead_earning);
        tvCongratMsg = (TextView) rootView.findViewById(R.id.msg_for_congratulation);
        tvApplicantName = (TextView) rootView.findViewById(R.id.applicant_name);
        productIcon = (ImageView) rootView.findViewById(R.id.product_icon);
        tvProduct = (TextView) rootView.findViewById(R.id.product);
        tvLoanAmount = (TextView) rootView.findViewById(R.id.loan_amount);
        tvLeadId = (TextView) rootView.findViewById(R.id.lead_id);
        tvDate = (TextView) rootView.findViewById(R.id.submission_date);
        btnContinue = (Button) rootView.findViewById(R.id.continue_button);
        loanLayout = (RelativeLayout) rootView.findViewById(R.id.loan_amount_layout);
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


}
