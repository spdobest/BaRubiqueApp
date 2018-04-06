package com.bestdealfinance.bdfpartner.ActivityNew;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.FragmentNew.PreviewFragment;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class LeadDetailActivityNew extends AppCompatActivity implements View.OnClickListener {

    private static final int SIGN_REQUEST = 1;
    ProgressDialog progressDialog;
    JSONObject responseLeadDetail;
    Bundle productBundle;
    String incomplete = "1";
    String strJson;
    Intent intentAction;
    boolean isFromLead = false;
    private TextView leadName, leadProductType, leadPhone, leadEarning, leadMeeting, leadAddress, leadId, leadBank, leadLoanAmount, leadCreatedDate;
    private ImageView ivPreview;
    private RequestQueue queue;
    private TextView textViewLeadPreview;
    private FrameLayout containerFragmentPreview;
    // widgets for line and bubble
    private View incomplete_date_line;
    private ImageView incomplete_date_circle;
    private TextView tvIncompleteRejectNotEligible;
    private TextView lead_created_date;
    private TextView lead_next_action_incomplete;
    private View viewLineAppointmentFixed;
    private ImageView imageViewCircleAppointmentFixed;
    private TextView textViewActionAppointmentFixed;
    private View viewDocVerified;
    private ImageView imageViewCircleDocVerified;
    private TextView textViewActionDocPickup;
    private TextView textViewDocPickup;
    private View viewLineLoginwithBank;
    private ImageView imageViewCircleLoginWithBank;
    private TextView textViewLoginWithBank;
    private TextView textViewActionLoginWithbank;
    private View viewLineApprovedOrRejected;
    private TextView textViewActionApprovedOrRejected;
    private TextView textViewApprovedOrRejected;
    private ImageView imageViewCircleApprovedOrRejected;
    private View viewLineWithdrawOrDisbursed;
    private ImageView imageViewCircleDisbursed;
    private TextView textViewDisbursed;
    private TextView textViewActionDisbursed;
    private RequestQueue jsonObjectRequest;
    private String leadIdValue;
    private JSONObject leadJsonObject;

    @Override
    public void onBackPressed() {
        if (containerFragmentPreview.getVisibility() == View.VISIBLE) {
            containerFragmentPreview.setVisibility(View.GONE);
        } else {
            Intent intentALlLead = new Intent(LeadDetailActivityNew.this, AllLeadsActivityNew.class);
            startActivity(intentALlLead);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_detail_new);

        jsonObjectRequest = Volley.newRequestQueue(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, "Lead Detail", false, true, false);

        ImageView toolbar_back_button = (ImageView) findViewById(R.id.toolbar_back_button);
        toolbar_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("lead_json")) {
                strJson = extras.getString("lead_json");
                try {
                    leadJsonObject = new JSONObject(strJson);
                    leadIdValue = leadJsonObject.optString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                if(extras.containsKey("lead_id")){
                    leadIdValue = extras.getString("lead_id");
                    Log.i("Lead Id", "onCreate: "+leadIdValue);
                }

            }
        }

        initializeUI();
        queue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Log.i("API", "onCreate: 1 ");

        getDetailsFromServer();
    }

    private void initializeUI() {
        leadName = (TextView) findViewById(R.id.lead_name);
        leadProductType = (TextView) findViewById(R.id.loan_product_type);
        leadPhone = (TextView) findViewById(R.id.lead_phone);
        leadEarning = (TextView) findViewById(R.id.lead_earning);
        leadMeeting = (TextView) findViewById(R.id.lead_meeting);
        leadAddress = (TextView) findViewById(R.id.lead_address);
        leadId = (TextView) findViewById(R.id.lead_id);
        leadBank = (TextView) findViewById(R.id.lead_bank);
        leadLoanAmount = (TextView) findViewById(R.id.lead_loan_amount);
        leadCreatedDate = (TextView) findViewById(R.id.lead_created_date);
        ivPreview = (ImageView) findViewById(R.id.lead_detail_preview);
        textViewLeadPreview = (TextView) findViewById(R.id.textViewLeadPreview);
        containerFragmentPreview = (FrameLayout) findViewById(R.id.containerFragmentPreview);

        // set onclick listener
        ivPreview.setOnClickListener(this);
        textViewLeadPreview.setOnClickListener(this);

        // initialize bubble layout
        incomplete_date_line = findViewById(R.id.incomplete_date_line);
        incomplete_date_circle = (ImageView) findViewById(R.id.incomplete_date_circle);
        tvIncompleteRejectNotEligible = (TextView) findViewById(R.id.tvIncompleteRejectNotEligible);
        lead_created_date = (TextView) findViewById(R.id.lead_created_date);
        lead_next_action_incomplete = (TextView) findViewById(R.id.lead_next_action_incomplete);

        lead_next_action_incomplete.setOnClickListener(this);

        viewLineAppointmentFixed = findViewById(R.id.viewLineAppointmentFixed);
        imageViewCircleAppointmentFixed = (ImageView) findViewById(R.id.imageViewCircleAppointmentFixed);
        textViewActionAppointmentFixed = (TextView) findViewById(R.id.textViewActionAppointmentFixed);

        textViewActionAppointmentFixed.setOnClickListener(this);

        viewDocVerified = findViewById(R.id.viewDocVerified);
        imageViewCircleDocVerified = (ImageView) findViewById(R.id.imageViewCircleDocVerified);
        textViewActionDocPickup = (TextView) findViewById(R.id.textViewActionDocPickup);
        textViewDocPickup = (TextView) findViewById(R.id.textViewDocPickup);

        textViewActionDocPickup.setOnClickListener(this);

        viewLineLoginwithBank = findViewById(R.id.viewLineLoginwithBank);
        imageViewCircleLoginWithBank = (ImageView) findViewById(R.id.imageViewCircleLoginWithBank);
        textViewLoginWithBank = (TextView) findViewById(R.id.textViewLoginWithBank);
        textViewActionLoginWithbank = (TextView) findViewById(R.id.textViewActionLoginWithbank);

        textViewActionLoginWithbank.setOnClickListener(this);

        viewLineApprovedOrRejected = findViewById(R.id.viewLineApprovedOrRejected);
        textViewActionApprovedOrRejected = (TextView) findViewById(R.id.textViewActionApprovedOrRejected);
        textViewApprovedOrRejected = (TextView) findViewById(R.id.textViewApprovedOrRejected);
        imageViewCircleApprovedOrRejected = (ImageView) findViewById(R.id.imageViewCircleApprovedOrRejected);

        textViewActionApprovedOrRejected.setOnClickListener(this);

        viewLineWithdrawOrDisbursed = findViewById(R.id.viewLineWithdrawOrDisbursed);
        imageViewCircleDisbursed = (ImageView) findViewById(R.id.imageViewCircleDisbursed);
        textViewDisbursed = (TextView) findViewById(R.id.textViewDisbursed);
        textViewActionDisbursed = (TextView) findViewById(R.id.textViewActionDisbursed);

        textViewActionDisbursed.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        progressDialog.show();

    }

    private void getDetailsFromServer() {
        Log.i("API", "onCreate: 2 ");
        JSONObject request = new JSONObject();
        try {
            request.put("lead_id", leadIdValue);
            request.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, LeadDetailActivityNew.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Helper.showLog(URL.GET_REFERRAL_DETAIL, "LEAD PARAMS : "+request.toString());
        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, URL.GET_REFERRAL_DETAIL, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {

                            try {

                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {

                                    Log.i("Lead", "onResponse: "+response.toString()+"\n"+leadJsonObject);

                                    String temp;
                                    responseLeadDetail = response.optJSONObject("body");
                                    leadName.setText(responseLeadDetail.optString("name"));
                                    temp = responseLeadDetail.optString("product_type_name");

                                    if(leadJsonObject == null || TextUtils.isEmpty(strJson)){
                                        lead_next_action_incomplete.setVisibility(View.GONE);
                                    }
                                    else
                                        setUpBubble(responseLeadDetail.optString("lead_state"),false);

                                    leadProductType.setText(temp);
                                    if (!temp.equals(Constant.CREDIT_CARD)) {
                                        leadLoanAmount.setText(getResources().getString(R.string.rupee_symbol) + " " + Helper.formatRs(responseLeadDetail.optString("loan_amount_needed")));
                                    } else {
                                        leadLoanAmount.setVisibility(View.GONE);
                                    }

                                    if(responseLeadDetail.has("payout")){
                                        leadEarning.setText("Payout : "+responseLeadDetail.optString("payout"));
                                    }

                                    leadPhone.setText(responseLeadDetail.optString("phone"));
                                    leadCreatedDate.setText("Created on " + Helper.timestampDate(responseLeadDetail.optString("date_created")));

                                    leadId.setText("ID : " + responseLeadDetail.optString("id"));
                                    temp = responseLeadDetail.optString("product_name");
                                    if (temp == null || temp.equals("null") || temp.isEmpty()) {
                                        leadBank.setVisibility(View.GONE);
                                    } else {
                                        leadBank.setVisibility(View.VISIBLE);
                                        leadBank.setText(temp);
                                    }

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
//                        progressDialog.hide();
                        Toast.makeText(LeadDetailActivityNew.this, "Unable to access", Toast.LENGTH_SHORT).show();
                        Log.i("ERROR", "onErrorResponse: "+error.getMessage());
//                        finish();
                    }
                }
        );
        queue.add(serverValues);
    }

    private void enableStages() {
        // TODO enable and disable stages based on current stage
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lead_detail_preview:
                containerFragmentPreview.setVisibility(View.VISIBLE);
                Bundle bundle = new Bundle();
                bundle.putString(Constant.BundleKey.LEAD_ID, leadIdValue);
                bundle.putBoolean(Constant.BundleKey.IS_FROM_LEAD_ACTIVITY, true);
                getSupportFragmentManager().beginTransaction().replace(R.id.containerFragmentPreview, PreviewFragment.newInstance(bundle), PreviewFragment.TAG).commit();

                break;
            case R.id.textViewLeadPreview:
                containerFragmentPreview.setVisibility(View.VISIBLE);
                Bundle bundle1 = new Bundle();
                bundle1.putString(Constant.BundleKey.LEAD_ID, leadIdValue);
                bundle1.putBoolean(Constant.BundleKey.IS_FROM_LEAD_ACTIVITY, true);
                getSupportFragmentManager().beginTransaction().replace(R.id.containerFragmentPreview, PreviewFragment.newInstance(bundle1), PreviewFragment.TAG).commit();
                break;
            case R.id.textViewActionDisbursed:

                break;
            case R.id.textViewActionApprovedOrRejected:
                if (responseLeadDetail != null) {
                    Helper.showAlertDialog(this, "", "Do you want to copy lead ?", "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getDuplicateLeadId(responseLeadDetail.optString("id"), responseLeadDetail.optString("product_type_id"));
                        }
                    }, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.textViewActionLoginWithbank:

                break;
            case R.id.textViewActionDocPickup:

                break;
            case R.id.textViewActionAppointmentFixed:

                break;

            case R.id.lead_next_action_incomplete:
                if (intentAction != null)
                    startActivity(intentAction);

                break;
        }
    }

    private void setUpBubble(String stage,boolean isFromNotification) {

        // initialize bubble layout
        if (stage.equalsIgnoreCase("Referred") || stage.equalsIgnoreCase("Incomplete") || stage.equalsIgnoreCase("Not_eligible")) {
            if (responseLeadDetail != null) {
                if (stage.equalsIgnoreCase("Incomplete")) {
                    lead_next_action_incomplete.setVisibility(View.VISIBLE);
                    lead_next_action_incomplete.setText("COMPLETE");

                    incomplete = "1";
                    Bundle productBundle = new Bundle();
                    productBundle.putString("incomplete", "1");
                    productBundle.putString("lead_id", leadJsonObject.optString("id"));
                    productBundle.putString("amount", leadJsonObject.optString("loan_amount_needed", "0"));
                    productBundle.putString("product_type_sought", leadJsonObject.optString("product_type_id"));
                    productBundle.putString("stage", leadJsonObject.optString("stage"));
                    productBundle.putString("name", leadJsonObject.optString("first_name"));

                    intentAction = new Intent(this, LeadFlowActivityNew.class);
                    intentAction.putExtra("bundle", productBundle);

                } else if (stage.equalsIgnoreCase("Not - eligible")) {
                    lead_next_action_incomplete.setVisibility(View.GONE);
                    lead_next_action_incomplete.setText("");
                } else if (stage.equalsIgnoreCase("Referred")) {
                    lead_next_action_incomplete.setVisibility(View.GONE);
                    tvIncompleteRejectNotEligible.setText("Referred");
                }
            }
        }
        else if (stage.equalsIgnoreCase("Applied") && leadJsonObject.optInt("partner_org_id", 0) > 0 && Integer.parseInt(leadJsonObject.optString("stage")) > 2) {
            tvIncompleteRejectNotEligible.setText("Applied");
            lead_next_action_incomplete.setVisibility(View.VISIBLE);
            lead_next_action_incomplete.setText("SCHEDULE MEETING");
            Bundle productBundle = new Bundle();
            productBundle.putString("incomplete", "1");
            productBundle.putString("lead_id", leadJsonObject.optString("id"));
            productBundle.putString("amount", leadJsonObject.optString("loan_amount_needed", "0"));
            productBundle.putString("product_type_sought", leadJsonObject.optString("product_type_id"));
            productBundle.putString("stage", leadJsonObject.optString("stage"));
            productBundle.putString("name", leadJsonObject.optString("first_name"));

            intentAction = new Intent(this, DocumentListActivity.class);
            intentAction.putExtra("productBundle", productBundle);
        } else if (stage.equalsIgnoreCase("Applied") && leadJsonObject.optString("partner_org_id", "0").equalsIgnoreCase("0") && Integer.parseInt(leadJsonObject.optString("stage")) > 2) {
            lead_next_action_incomplete.setVisibility(View.VISIBLE);
            tvIncompleteRejectNotEligible.setText("Appied");
            lead_next_action_incomplete.setText("SELECT RFC");
            Bundle productBundle = new Bundle();
            productBundle.putString("incomplete", "1");
            productBundle.putString("lead_id", leadJsonObject.optString("id"));
            productBundle.putString("amount", leadJsonObject.optString("loan_amount_needed", "0"));
            productBundle.putString("product_type_sought", leadJsonObject.optString("product_type_id"));
            productBundle.putString("stage", leadJsonObject.optString("stage"));
            productBundle.putString("name", leadJsonObject.optString("first_name"));

            intentAction = new Intent(this, SelectRfcActivityNew.class);
            intentAction.putExtra("productBundle", productBundle);

        }else if (stage.equalsIgnoreCase("Applied") && leadJsonObject.optString("partner_org_id", "0").equals("null") && Integer.parseInt(leadJsonObject.optString("stage")) > 2) {
            lead_next_action_incomplete.setVisibility(View.VISIBLE);
            tvIncompleteRejectNotEligible.setText("Applied");
            lead_next_action_incomplete.setText("SELECT RFC");
            Bundle productBundle = new Bundle();
            productBundle.putString("incomplete", "1");
            productBundle.putString("lead_id", leadJsonObject.optString("id"));
            productBundle.putString("amount", leadJsonObject.optString("loan_amount_needed", "0"));
            productBundle.putString("product_type_sought", leadJsonObject.optString("product_type_id"));
            productBundle.putString("stage", leadJsonObject.optString("stage"));
            productBundle.putString("name", leadJsonObject.optString("first_name"));

            intentAction = new Intent(this, SelectRfcActivityNew.class);
            intentAction.putExtra("productBundle", productBundle);

        } else if (stage.equalsIgnoreCase("Applied")) {
            tvIncompleteRejectNotEligible.setText("Applied");
        } else if (stage.equalsIgnoreCase("APPT_FIXED")) {
            tvIncompleteRejectNotEligible.setText("Applied");
            viewLineAppointmentFixed.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleAppointmentFixed.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
        } else if (stage.equalsIgnoreCase("DOC_COLLECTED") || stage.equalsIgnoreCase("DOC_VERIFIED")) {
            tvIncompleteRejectNotEligible.setText("Applied");
            textViewDocPickup.setText(stage);
            viewLineAppointmentFixed.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleAppointmentFixed.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionAppointmentFixed

            viewDocVerified.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleDocVerified.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
        } else if (stage.equalsIgnoreCase("FINBANK")||stage.equalsIgnoreCase("SUBMITTED_TO_BANK")) {
            tvIncompleteRejectNotEligible.setText("Applied");
            textViewDocPickup.setText("Doc Verified");
            textViewLoginWithBank.setText(stage);
            viewLineAppointmentFixed.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleAppointmentFixed.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionAppointmentFixed

            viewDocVerified.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleDocVerified.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionDocPickup;
//            textViewDocPickup;

            viewLineLoginwithBank.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleLoginWithBank.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
        } else if (stage.equalsIgnoreCase("Approved") || stage.equalsIgnoreCase("Rejected")) {

            tvIncompleteRejectNotEligible.setText("Applied");
            textViewDocPickup.setText("Doc Verified");
            textViewLoginWithBank.setText("Finbank");
            textViewApprovedOrRejected.setText(stage);

            viewLineAppointmentFixed.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleAppointmentFixed.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionAppointmentFixed

            viewDocVerified.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleDocVerified.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionDocPickup;
//            textViewDocPickup;

            viewLineLoginwithBank.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleLoginWithBank.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewLoginWithBank;
//            textViewActionLoginWithbank;

            viewLineApprovedOrRejected.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleApprovedOrRejected.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionApprovedOrRejected;
//            textViewApprovedOrRejected;

        } else if (stage.equalsIgnoreCase("Withdrawn") || stage.equalsIgnoreCase("Disbursed") || stage.equalsIgnoreCase("Disburse_Appointment_Fixed") || stage.equalsIgnoreCase("Disburse_Doc_Collected") || stage.equalsIgnoreCase("Disburse_Doc_Verified") || stage.equalsIgnoreCase("Disburse_Doc_Submitted to")) {

            tvIncompleteRejectNotEligible.setText("Applied");
            textViewDocPickup.setText("Doc Verified");
            textViewLoginWithBank.setText("Finbank");
            textViewApprovedOrRejected.setText("Approved");
            textViewDisbursed.setText(stage);

            viewLineAppointmentFixed.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleAppointmentFixed.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionAppointmentFixed

            viewDocVerified.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleDocVerified.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionDocPickup;
//            textViewDocPickup;

            viewLineLoginwithBank.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleLoginWithBank.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewLoginWithBank;
//            textViewActionLoginWithbank;

            viewLineApprovedOrRejected.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleApprovedOrRejected.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
//            textViewActionApprovedOrRejected;
//            textViewApprovedOrRejected;


            viewLineWithdrawOrDisbursed.setBackgroundColor(ContextCompat.getColor(this, R.color.Blue700));
            imageViewCircleDisbursed.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.custom_corner_25_blue_border_white_filled));
        }
    }

    private void getDuplicateLeadId(String leadId, String product_type) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(LeadDetailActivityNew.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("lead_id", leadId);
            reqObject.put("product_type", product_type);
            reqObject.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, this));


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.DUPLICATE_LEAD, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    progressDialog.dismiss();
                    try {
                        if (response.getInt("status_code") == 200) {
                            JSONObject body = response.optJSONObject("body");
                            String leadIdDuplicate = body.getString("lead_id");

                            Toast.makeText(LeadDetailActivityNew.this, "Duplicate Lead Created", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LeadDetailActivityNew.this, AllLeadsActivityNew.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                }
            });

            jsonObjectRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
