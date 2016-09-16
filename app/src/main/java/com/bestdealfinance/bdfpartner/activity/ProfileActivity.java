package com.bestdealfinance.bdfpartner.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.bestdealfinance.bdfpartner.application.ProfileHelper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.dialog.BankInformationEditDialog;
import com.bestdealfinance.bdfpartner.dialog.PersonalInformationEditDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView profileName, profileEmail, profileProfession, profileAddress1, profileAddress2, profileCityAndPin, profileState, profileMobile, profileEdit, profilePAN, profileAadhar, profilePassport;
    private TextView bankHolderName, bankName, bankBranch, bankAccountNumber, bankPAN, bankEdit;
    private Button btnChangePassword, btnLogout;
    private RequestQueue queue;
    private View mainView;
    private ProfileHelper profileHelper;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setTitle(getResources().getString(R.string.nav_drawer_my_profile));
        toolbar.setTitleTextColor(getResources().getColor(R.color.Grey200));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeUI();

        queue = Volley.newRequestQueue(this);
        profileHelper = new ProfileHelper();

    }

    @Override
    public void onResume() {
        super.onResume();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        getValuesFromServer();
    }

    private void getValuesFromServer() {
        JSONObject request = new JSONObject();
        try {
            request.put(Constant.UTOKEN, Helper.getStringSharedPreference(Util.utoken, ProfileActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, Util.GET_CUSTOMER_PROFILE, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                            try {

                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {
                                    profileHelper.setResponse(response);
                                    JSONObject body, customer, bankDetails;
                                    body = response.getJSONObject(Constant.BODY);
                                    customer = body.getJSONObject("customer");
                                    bankDetails = body.getJSONObject("ba_bank");

                                    setAllValues(customer, bankDetails);

                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
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
                        Snackbar.make(mainView, R.string.failed_to_retrieve, Snackbar.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Accept", "application/json");
                params.put("Content-type", "application/json");
                params.put("Cookie", "utoken=" + Helper.getStringSharedPreference(Util.utoken, ProfileActivity.this));

                return params;
            }
        };
        serverValues.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
        queue.add(serverValues);
    }

    private void setAllValues(JSONObject customer, JSONObject bankDetails) {
        try {
            profileName.setText(customer.getString("name"));

            setSomeStringBold(profileMobile, Constant.MOBILE, customer.getString("mobile_number"));
            setSomeStringBold(profileEmail, Constant.EMAIL, customer.getString("email"));

            profileHelper.setName(customer.getString("name"));
            profileHelper.setEmail(customer.getString("email"));
            profileHelper.setMobile(customer.getString("mobile_number"));

            profileHelper.setProfession(checkForNullAndSet(customer, "profession", null, profileProfession));
            profileHelper.setPan(checkForNullAndSet(customer, "pan", Constant.PAN, profilePAN));
            profileHelper.setAadhar_number(checkForNullAndSet(customer, "aadhar_number", Constant.AADHAR, profileAadhar));
            profileHelper.setPassport_number(checkForNullAndSet(customer, "passport_number", Constant.PASSPORT, profilePassport));

            profileHelper.setAddress1(checkForNullAndSet(customer, "address_line_1", null, profileAddress1));

            String address2 = checkForNullAndSet(customer, "address_line_2", null, profileAddress2);
            profileHelper.setAddress2(address2);
            String address3 = checkForNullAndSet(customer, "address_line_3", null, profileAddress2);
            profileAddress2.setText("" + address2 + " " + address3);
            profileHelper.setAddress3(address3);

            profileHelper.setState(checkForNullAndSet(customer, "state", null, profileState));

            String value;
            if (customer.isNull("city")) {
                value = "";
            } else {
                value = customer.getString("city");
                profileHelper.setCity(value);
            }

            if (customer.isNull("pincode")) {
                value = value + " - " + "";
            } else {
                value = value + " - " + customer.getString("pincode");
                profileHelper.setPincode(customer.getString("pincode"));
            }
            profileCityAndPin.setText(value);


            profileHelper.setBank_holder_name(checkForNullAndSet(bankDetails, "holder_name", null, bankHolderName));
            profileHelper.setBank_name(checkForNullAndSet(bankDetails, "bank_name", null, bankName));
            profileHelper.setBranch_name(checkForNullAndSet(bankDetails, "branch_name", null, bankBranch));
            profileHelper.setAccount_number(checkForNullAndSet(bankDetails, "account_number", Constant.ACCOUNT_NUMBER, bankAccountNumber));
            profileHelper.setBank_pan(checkForNullAndSet(bankDetails, "pan", Constant.PAN, bankPAN));

            if (!bankDetails.isNull("ifsc")) {
                value = bankDetails.getString("ifsc");
                profileHelper.setIfsc(value);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setSomeStringBold(TextView textView, String boldString, String nonBoldString) {
        textView.setText(Html.fromHtml("<b>" + boldString + "</b> " + " : " + nonBoldString));
    }

    private String checkForNullAndSet(JSONObject jsonObject, String jsonKey, String constantKey, TextView textView) throws JSONException {
        String value = null;
        String sourceString;
        if (jsonObject.isNull(jsonKey)) {
            if (constantKey == null) {
                textView.setText(Constant.DASHED_LINE);
            } else {
                setSomeStringBold(textView, constantKey, Constant.DASHED_LINE);
            }
        } else {
            value = jsonObject.getString(jsonKey);
            if (constantKey == null) {
                textView.setText(value);
            } else {
                setSomeStringBold(textView, constantKey, value);
            }
        }
        return value;
    }

    private void initializeUI() {
        mainView = findViewById(R.id.profile_main_layout);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileProfession = (TextView) findViewById(R.id.profile_profession);
        profileAddress1 = (TextView) findViewById(R.id.profile_address1);
        profileAddress2 = (TextView) findViewById(R.id.profile_address2);
        profileCityAndPin = (TextView) findViewById(R.id.profile_city_and_pin);
        profileState = (TextView) findViewById(R.id.profile_state);
        profileMobile = (TextView) findViewById(R.id.profile_mobile);
        profileEdit = (TextView) findViewById(R.id.profile_edit);
        profilePAN = (TextView) findViewById(R.id.profile_PAN);
        profileAadhar = (TextView) findViewById(R.id.profile_aadhar);
        profilePassport = (TextView) findViewById(R.id.profile_passport);

        bankHolderName = (TextView) findViewById(R.id.bank_holder_name);
        bankName = (TextView) findViewById(R.id.bank_name);
        bankAccountNumber = (TextView) findViewById(R.id.bank_account_number);
        bankPAN = (TextView) findViewById(R.id.bank_PAN);
        bankBranch = (TextView) findViewById(R.id.bank_branch);
        bankEdit = (TextView) findViewById(R.id.bank_edit);

        btnChangePassword = (Button) findViewById(R.id.profile_change_password);
        btnLogout = (Button) findViewById(R.id.profile_logout);

        profileEdit.setOnClickListener(this);
        bankEdit.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (view.getId() == R.id.profile_edit) {
            PersonalInformationEditDialog personalInformationEditDialog = new PersonalInformationEditDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("ProfileHelper", profileHelper);
            personalInformationEditDialog.setArguments(bundle);
            personalInformationEditDialog.show(fragmentManager, "personalInformationEditDialog");

        } else if (view.getId() == R.id.bank_edit) {
            BankInformationEditDialog bankInformationEditDialog = new BankInformationEditDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("ProfileHelper", profileHelper);
            bankInformationEditDialog.setArguments(bundle);
            bankInformationEditDialog.show(fragmentManager, "bankInformationEditDialog");

        } else if (view.getId() == R.id.profile_change_password) {
            startActivity(new Intent(this, ChangePassword.class));

        } else if (view.getId() == R.id.profile_logout) {
            logoutRequest();
        }
    }


    public void logoutRequest() {
        JsonObjectRequest logoutRequest = new JsonObjectRequest(Request.Method.GET, Util.LOGOUT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                            try {

                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {

                                    Helper.deleteAllUserData(ProfileActivity.this);

                                    Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
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
                        Toast.makeText(ProfileActivity.this, R.string.failed_to_logout, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Accept", "application/json");
                params.put("Content-type", "application/json");
                params.put("Cookie", "utoken=" + Helper.getStringSharedPreference(Util.utoken, ProfileActivity.this));

                return params;
            }
        };
        logoutRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
        queue.add(logoutRequest);

    }
}
