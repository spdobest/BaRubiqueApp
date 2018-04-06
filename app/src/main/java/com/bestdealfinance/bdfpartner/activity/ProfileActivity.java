package com.bestdealfinance.bdfpartner.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
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
import com.bestdealfinance.bdfpartner.ActivityNew.MainActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.SigninActivityNew;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ProfileHelper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.dialog.BankInformationEditDialog;
import com.bestdealfinance.bdfpartner.dialog.PersonalInformationEditDialog;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";

    private static final int SIGN_REQUEST = 1;
    PersonalInformationEditDialog personalInformationEditDialog;
    BankInformationEditDialog bankInformationEditDialog;
    private TextView profileName, profileEmail, profileProfession, profileAddress1, profileAddress2, profileCityAndPin, profileState, profileMobile, emailPhoneEdit, profileEdit, profilePAN, profileAadhar, profilePassport;
    private TextView bankHolderName, bankName, bankBranch, bankAccountNumber, bankPAN, bankEdit;
    private Button btnLogout;
    private RequestQueue queue;
    private View mainView;
    private ProfileHelper profileHelper;
    private ProgressDialog progressDialog;
    // DATABASE DECLARATION
    private DB snappyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, getResources().getString(R.string.nav_drawer_my_profile), false, true, true);

        initializeUI();

        queue = Volley.newRequestQueue(this);
        profileHelper = new ProfileHelper();

        // get all profession
        getProfessionAndUpdateDatabase();

        if (Helper.getStringSharedPreference(Constant.UTOKEN, ProfileActivity.this).equals("")) {
            startActivityForResult(new Intent(this, SigninActivityNew.class), SIGN_REQUEST);
        } else {
            getValuesFromServer();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_REQUEST) {
            if (resultCode == RESULT_OK) {
                getValuesFromServer();
            } else {
                Toast.makeText(this, "You must login/register to see profile details", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }


    public void getValuesFromServer() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        JSONObject request = new JSONObject();
        try {
            request.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, ProfileActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, URL.GET_CUSTOMER_PROFILE, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                            try {

                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {

                                    Log.i(TAG, "onResponse: profile response  " + response.toString());

                                    snappyDB = DBFactory.open(ProfileActivity.this);
                                    snappyDB.put(Constant.DB_PROFILE, response.toString());
                                    snappyDB.close();

                                    profileHelper.setResponse(response);
                                    JSONObject body, customer, bankDetails;
                                    body = response.getJSONObject(Constant.BODY);
                                    customer = body.getJSONObject("customer");
                                    bankDetails = body.getJSONObject("ba_bank");

                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    setAllValues(customer, bankDetails);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (SnappydbException e1) {
                                e1.printStackTrace();
                            } finally {
                                try {
                                    if (snappyDB != null && snappyDB.isOpen())
                                        snappyDB.close();
                                } catch (SnappydbException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        Snackbar.make(mainView, "Please login to see data.", Snackbar.LENGTH_INDEFINITE).setAction("LOGIN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ProfileActivity.this, SigninActivityNew.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }
                        }).show();
                    }
                }
        );
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
            profileHelper.setAddress3(address3);

            if (address2 == null && address3 == null) {
                profileAddress2.setText(Constant.DASHED_LINE);
            } else {
                profileAddress2.setText("" + address2 + " " + address3);
            }

            profileHelper.setState(checkForNullAndSet(customer, "state", null, profileState));

            String value;
            if (customer.isNull("city")) {
                value = "";
            } else {
                value = customer.getString("city");
                profileHelper.setCity(value);
            }

            if (customer.isNull("pincode") && customer.getString("pincode").equalsIgnoreCase("0")) {
                if (value == null || value == "") {
                    value = Constant.DASHED_LINE;
                } else {
                    value = value + " - " + "";
                }
            } else {
                profileHelper.setPincode(customer.getString("pincode"));
                if (customer.getString("pincode").equalsIgnoreCase("0")) {
                    Log.i(TAG, "setAllValues: spm dasda");
                    value = value + " - ";
                } else
                    value = value + " - " + customer.getString("pincode");
            }
            profileCityAndPin.setText(value);


            profileHelper.setBank_holder_name(checkForNullAndSet(bankDetails, "holder_name", null, bankHolderName));
            profileHelper.setBank_name(checkForNullAndSet(bankDetails, "bank_name", null, bankName));
            profileHelper.setBranch_name(checkForNullAndSet(bankDetails, "branch_name", null, bankBranch));
            profileHelper.setAccount_number(checkForNullAndSet(bankDetails, "account_number", Constant.ACCOUNT_NUMBER, bankAccountNumber));
//            profileHelper.setBank_pan(checkForNullAndSet(bankDetails, "pan", Constant.PAN, bankPAN));
            profileHelper.setPan(checkForNullAndSet(customer, "pan", Constant.PAN, bankPAN));
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

        btnLogout = (Button) findViewById(R.id.profile_logout);

        profileEdit.setOnClickListener(this);
        bankEdit.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.profile_edit:
                if (personalInformationEditDialog == null)
                    personalInformationEditDialog = new PersonalInformationEditDialog().newInstance(ProfileActivity.this, profileHelper);
                personalInformationEditDialog.show(getSupportFragmentManager(), PersonalInformationEditDialog.TAG);
                break;

            case R.id.bank_edit:
                if (bankInformationEditDialog == null)
                    bankInformationEditDialog = new BankInformationEditDialog().newInstance(ProfileActivity.this, profileHelper);
                bankInformationEditDialog.show(getSupportFragmentManager(), BankInformationEditDialog.TAG);
                break;
            case R.id.profile_logout:
                logoutRequest();
                break;
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

                                    Helper.setStringSharedPreference(Constant.UTOKEN, "", ProfileActivity.this);
                                    Helper.setStringSharedPreference(Constant.USERID, "", ProfileActivity.this);
                                    Helper.setStringSharedPreference(Constant.USEROBJECT, "", ProfileActivity.this);

                                    Intent i = new Intent(ProfileActivity.this, MainActivityNew.class);
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

    @Override
    protected void onPause() {
        if (personalInformationEditDialog != null)
            personalInformationEditDialog.dismiss();
        if (bankInformationEditDialog != null)
            bankInformationEditDialog.dismiss();
        super.onPause();

    }

    /*
///////////////////////////GET PROFESSION FROM API AND UPDATE TO DATABASE//////////////////////////////////
 */
    private void getProfessionAndUpdateDatabase() {

        JSONObject object = new JSONObject();
        try {
            object.put(URL.UrlConstants.LIST_ID, URL.UrlConstants.LIST_ID_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.FETCH_ALL_PROFESSION, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: getProfession " + response);
                try {
                    snappyDB = DBFactory.open(ProfileActivity.this);
                    snappyDB.put(Constant.DB_PROFESSION_LIST, response.toString());
                    snappyDB.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse getProfession: " + error.getMessage());
            }
        });
        Log.i(TAG, "getProfessionAndUpdateDatabase: url " + URL.FETCH_ALL_PROFESSION + "\n" + "params " + object.toString());
        queue.add(request);

    }
}
