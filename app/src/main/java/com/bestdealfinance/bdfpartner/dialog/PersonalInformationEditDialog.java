package com.bestdealfinance.bdfpartner.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.ProfileActivity;
import com.bestdealfinance.bdfpartner.activity.RegisterActivity;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ProfileHelper;
import com.bestdealfinance.bdfpartner.application.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInformationEditDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private EditText etName, etEmail, etPhone, etAddress1, etAddress2, etAddress3, etPincode, etPAN, etAadhar, etPassprot;
    private EditText etAnnualIncome, etTotalExperience;
    private AppCompatSpinner spinnerProfession, spinnerState, spinnerOccupation;
    private AutoCompleteTextView autoCompleteCity, autoCompleteCompanyName;
    private Button btnSave, btnCancel;
    private ProfileHelper profileHelper;
    private RequestQueue queue;
    private String mapId, state, stateId;
    private JSONArray stateJsonArray;
    private ArrayAdapter<String> stateAdapter, cityAdapter, professionAdapter, occupationAdapter, companyListAdapter;

    private List<String> stateList = new ArrayList<String>();
    private List<String> cityList = new ArrayList<String>();
    private List<String> companyList = new ArrayList<String>();


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onViewCreated(view, savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.dialog_edit_personal_info, null);
        getDialog().setTitle(getString(R.string.personal_information));

        setUpUiComponents(inflaterView);
        setCancelable(true);
        queue = Volley.newRequestQueue(getActivity());

        profileHelper = (ProfileHelper) getArguments().getSerializable("ProfileHelper");

        setProfessionAdapter();
        setOccupationAdapter();
        getStateValuesFromServerAndSetStateAdapter();

        // TODO
        // getCompanyList();

        setValuesForUI(profileHelper);

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels;
        getDialog().getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void setProfessionAdapter() {
        // TODO delet and fetch from server and delete  Util.occupation

        StringRequest request = new StringRequest(Util.FETCH_PROFESSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> occupation = new ArrayList<>();

                try {
                    JSONObject res = new JSONObject(response);
                    JSONArray data = res.getJSONObject("body").getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        if (!data.getJSONObject(i).getString("item_value").equals("Others"))
                            occupation.add(data.getJSONObject(i).getString("item_value"));
                    }
                    occupation.add("Others");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                professionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, occupation);
                professionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProfession.setAdapter(professionAdapter);


                String previousProfession = profileHelper.getProfession();
                if (previousProfession != null) {
                    for (int i = 0; i < occupation.size(); i++) {
                        if (previousProfession.equalsIgnoreCase(occupation.get(i))) {
                            spinnerProfession.setSelection(i);
                            break;
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);

    }

    private void setOccupationAdapter() {
        // TODO delet and fetch from server and delete  Util.occupation

        StringRequest request = new StringRequest(Util.FETCH_OCCUPATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> occupation = new ArrayList<>();

                try {
                    JSONObject res = new JSONObject(response);
                    JSONArray data = res.getJSONObject("body").getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        if (!data.getJSONObject(i).getString("item_value").equals("Others"))
                            occupation.add(data.getJSONObject(i).getString("item_value"));
                    }
                    occupation.add("Others");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                occupationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, occupation);
                occupationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerOccupation.setAdapter(occupationAdapter);

                String previousOccupation = profileHelper.getOccupation();
                if (previousOccupation != null) {
                    for (int i = 0; i < occupation.size(); i++) {
                        if (previousOccupation.equalsIgnoreCase(occupation.get(i))) {
                            spinnerOccupation.setSelection(i);
                            break;
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);

    }


    private void getStateValuesFromServerAndSetStateAdapter() {
        autoCompleteCity.setText(profileHelper.getCity());
        final JSONObject stateListRequest = new JSONObject();
        try {
            stateListRequest.put(Constant.UTOKEN, Helper.getStringSharedPreference(Util.utoken, getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, Util.GET_STATE_LIST, stateListRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                            try {

                                String status, msg, stateValue;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {
                                    //profileHelper.setResponse(response);
                                    JSONObject body, jsonMapId;
                                    JSONArray jsonStateArray;
                                    body = response.getJSONObject(Constant.BODY);
                                    jsonMapId = body.getJSONObject("0");
                                    mapId = jsonMapId.getString("map_id");
                                    jsonStateArray = body.getJSONArray("state");
                                    stateJsonArray = jsonStateArray;
                                    for (int i = 0; i < jsonStateArray.length(); i++) {
                                        stateValue = jsonStateArray.getJSONObject(i).getString("item_value");
                                        stateList.add(stateValue);
                                    }
                                }

                                stateAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stateList);
                                stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerState.setAdapter(stateAdapter);

                                String stateFromProfile = profileHelper.getState();
                                if (!stateFromProfile.equals(null)) {
                                    int spinnerPosition = stateAdapter.getPosition(stateFromProfile);
                                    spinnerState.setSelection(spinnerPosition);
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
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Accept", "application/json");
                params.put("Content-type", "application/json");
                params.put("Cookie", "utoken=" + Helper.getStringSharedPreference(Util.utoken, getActivity()));

                return params;
            }
        };
        serverValues.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
        queue.add(serverValues);
    }

    private void setValuesForUI(ProfileHelper profileHelper) {
        etName.setText(profileHelper.getName());
        etEmail.setText(profileHelper.getEmail());
        etPhone.setText(profileHelper.getMobile());

        etPAN.setText(profileHelper.getPan());
        etAadhar.setText(profileHelper.getAadhar_number());
        etPassprot.setText(profileHelper.getPassport_number());

        etAddress1.setText(profileHelper.getAddress1());
        etAddress2.setText(profileHelper.getAddress2());
        etAddress3.setText(profileHelper.getAddress3());
        etPincode.setText(profileHelper.getPincode());

        autoCompleteCompanyName.setText(profileHelper.getCompanyName());
        etAnnualIncome.setText(profileHelper.getAnnualIncome());
        etTotalExperience.setText(profileHelper.getTotalExperience());

        String previousState = profileHelper.getState();
        if (previousState != null) {
            for (int i = 0; i < stateList.size(); i++) {
                if (previousState.equalsIgnoreCase(stateList.get(i))) {
                    spinnerState.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setUpUiComponents(View inflaterView) {
        etName = (EditText) inflaterView.findViewById(R.id.et_name);
        etEmail = (EditText) inflaterView.findViewById(R.id.et_email);
        etPhone = (EditText) inflaterView.findViewById(R.id.et_phone);

        etAddress1 = (EditText) inflaterView.findViewById(R.id.et_address1);
        etAddress2 = (EditText) inflaterView.findViewById(R.id.et_address2);
        etAddress3 = (EditText) inflaterView.findViewById(R.id.et_address3);
        etPincode = (EditText) inflaterView.findViewById(R.id.et_pincode);
        etEmail.setEnabled(false);
        etPhone.setEnabled(false);

        etPAN = (EditText) inflaterView.findViewById(R.id.et_pan);
        etAadhar = (EditText) inflaterView.findViewById(R.id.et_aadhar);
        etPassprot = (EditText) inflaterView.findViewById(R.id.et_passport);

        etAnnualIncome = (EditText) inflaterView.findViewById(R.id.et_annual_income);
        etTotalExperience = (EditText) inflaterView.findViewById(R.id.et_total_experience);

        autoCompleteCompanyName = (AutoCompleteTextView) inflaterView.findViewById(R.id.auto_complete_company_name);
        autoCompleteCompanyName.setThreshold(2);

        spinnerProfession = (AppCompatSpinner) inflaterView.findViewById(R.id.spinner_profession);
        spinnerProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String profession = adapterView.getItemAtPosition(i).toString();
                if (profession.equalsIgnoreCase(Constant.OTHERS)) {
                    //TODO
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerOccupation = (AppCompatSpinner) inflaterView.findViewById(R.id.spinner_occupation);


        spinnerState = (AppCompatSpinner) inflaterView.findViewById(R.id.spinner_state);
        spinnerState.setOnItemSelectedListener(this);

        autoCompleteCity = (AutoCompleteTextView) inflaterView.findViewById(R.id.autocomplete_city);
        autoCompleteCity.setThreshold(1);


        btnCancel = (Button) inflaterView.findViewById(R.id.btn_cancel);
        btnSave = (Button) inflaterView.findViewById(R.id.btn_save);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            View focusView = isValid();
            if (focusView != null) {
                focusView.requestFocus();
            } else {
                profileHelper.setName(etName.getText().toString());
                //String phone = etPhone.getText().toString();
                //String email = etEmail.getText().toString();


                profileHelper.setAddress1(etAddress1.getText().toString());
                profileHelper.setAddress2(etAddress2.getText().toString());
                if (etAddress3.getText() != null) {
                    profileHelper.setAddress3(etAddress3.getText().toString());
                }

                if (etPincode.getText() != null) {
                    profileHelper.setPincode(etPincode.getText().toString());
                }

                // state selected on click of spinner
                String state = spinnerState.getSelectedItem().toString();
                if (state != null) {
                    profileHelper.setState(state);
                }

                profileHelper.setCity(autoCompleteCity.getText().toString());

                profileHelper.setPan(etPAN.getText().toString());

                if (etAadhar.getText() != null) {
                    profileHelper.setAadhar_number(etAadhar.getText().toString());
                }

                if (etPassprot.getText() != null) {
                    profileHelper.setPassport_number(etPassprot.getText().toString());
                }

                String profession = spinnerProfession.getSelectedItem().toString();
                if (profession != null) {
                    profileHelper.setProfession(profession);
                }

                String occupation = spinnerOccupation.getSelectedItem().toString();
                profileHelper.setOccupation(occupation);

                if (autoCompleteCompanyName.getText() != null) {
                    profileHelper.setCompanyName(autoCompleteCompanyName.getText().toString());
                }

                if (etAnnualIncome.getText() != null) {
                    profileHelper.setAnnualIncome(etAnnualIncome.getText().toString());
                }

                if (etTotalExperience.getText() != null) {
                    profileHelper.setTotalExperience(etTotalExperience.getText().toString());
                }

                setValuesToJson();

                upadteDataOnServer();

                // TODO send to server profileHelper.response
                //}

                dismiss();
                ((ProfileActivity) getActivity()).onResume();
            }

        } else {
            dismiss();
        }

    }

    private void upadteDataOnServer() {
        JSONObject response = profileHelper.getResponse();
        try {
            response.put(Constant.UTOKEN, Helper.getStringSharedPreference(Util.utoken, getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, Util.UPDATE_CUSTOMER_PROFILE, response,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Accept", "application/json");
                params.put("Content-type", "application/json");
                params.put("Cookie", "utoken=" + Helper.getStringSharedPreference(Util.utoken, getActivity()));

                return params;
            }
        };
        serverValues.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
        queue.add(serverValues);
    }

    private void getCompanyList() {
        JSONObject jsonCityRequest = new JSONObject();
        try {
            jsonCityRequest.put(Util.utoken, Helper.getStringSharedPreference(Util.utoken, getActivity()));
            //TODO
            jsonCityRequest.put("source_id", stateId);
            jsonCityRequest.put("map_id", mapId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, Util.GET_CITY_LIST, jsonCityRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                            try {

                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {
                                    JSONObject company;
                                    JSONObject body = response.getJSONObject(Constant.BODY);

                                    // TODO
                                    JSONArray companyJsonArray = body.getJSONArray("company");

                                    companyList.clear();

                                    for (int i = 0; i < companyJsonArray.length(); i++) {
                                        company = companyJsonArray.getJSONObject(i);
                                        //TODO
                                        companyList.add(company.getString("item_value"));
                                    }
                                    companyListAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, companyList);
                                    autoCompleteCity.setAdapter(cityAdapter);
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
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Accept", "application/json");
                params.put("Content-type", "application/json");
                params.put("Cookie", "utoken=" + Helper.getStringSharedPreference(Util.utoken, getActivity()));

                return params;
            }
        };
        serverValues.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
        queue.add(serverValues);

    }

    private void getCityList() {

        JSONObject jsonCityRequest = new JSONObject();
        try {
            jsonCityRequest.put(Util.utoken, Helper.getStringSharedPreference(Util.utoken, getActivity()));
            jsonCityRequest.put("source_id", stateId);
            jsonCityRequest.put("map_id", mapId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, Util.GET_CITY_LIST, jsonCityRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                            try {

                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {
                                    JSONObject city;
                                    JSONObject body = response.getJSONObject(Constant.BODY);
                                    JSONArray cityJsonArray = body.getJSONArray("city");

                                    cityList.clear();

                                    for (int i = 0; i < cityJsonArray.length(); i++) {
                                        city = cityJsonArray.getJSONObject(i);
                                        cityList.add(city.getString("item_value"));
                                    }
                                    cityAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, cityList);
                                    autoCompleteCity.setAdapter(cityAdapter);
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
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Accept", "application/json");
                params.put("Content-type", "application/json");
                params.put("Cookie", "utoken=" + Helper.getStringSharedPreference(Util.utoken, getActivity()));

                return params;
            }
        };
        serverValues.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
        queue.add(serverValues);

    }


    private void setValuesToJson() {
        JSONObject response = profileHelper.getResponse();

        JSONObject body, customer;
        try {
            body = response.getJSONObject(Constant.BODY);
            customer = body.getJSONObject("customer");

            customer.put("name", profileHelper.getName());

            customer.put("pan", profileHelper.getPan());
            customer.put("aadhar_number", profileHelper.getAadhar_number());
            customer.put("passport_number", profileHelper.getPassport_number());

            customer.put("address_line_1", profileHelper.getAddress1());
            customer.put("address_line_2", profileHelper.getAddress2());
            customer.put("address_line_3", profileHelper.getAddress3());
            customer.put("city", profileHelper.getCity());
            customer.put("state", profileHelper.getState());
            customer.put("pincode", profileHelper.getPincode());

            customer.put("profession", profileHelper.getProfession());
            customer.put("occupation", profileHelper.getOccupation());
            customer.put("annual_income", profileHelper.getAnnualIncome());
            customer.put("company_name", profileHelper.getCompanyName());
            customer.put("total_experience", profileHelper.getTotalExperience());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View isValid() {
        View focusView = null;

        if (etName.getText().toString().isEmpty()) {
            etName.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etName;
            }
        } else if (etPAN.getText().toString().isEmpty()) {
            etPAN.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etPAN;
            }
        } else if (autoCompleteCity.getText().toString().isEmpty()) {
            if (focusView == null) {
                focusView = autoCompleteCity;
            }
        }

        if (focusView == null) {
            String panError = Helper.isPANValid(etPAN.getText().toString());
            if (panError != null) {
                etPAN.setError(panError);
                focusView = etPAN;
            }
        }

        if (focusView == null && !etAadhar.getText().toString().isEmpty()) {
            String aadharError = Helper.isAadharValid(etAadhar.getText().toString());
            if (aadharError != null) {
                etAadhar.setError(aadharError);
                focusView = etAadhar;
            }
        }

        if (focusView == null && !etAddress3.getText().toString().isEmpty()) {
            String landMarkError = Helper.isLandMarkValid(etAddress3.getText().toString());
            if (landMarkError != null) {
                etAddress3.setError(landMarkError);
                focusView = etAddress3;
            }
        }

        return focusView;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            state = adapterView.getItemAtPosition(i).toString();
            if (!state.equals(profileHelper.getState())) {
                autoCompleteCity.setText(null);
                profileHelper.setState(state);
                if (stateJsonArray != null) {
                    stateId = stateJsonArray.getJSONObject(i).getString("member_id");
                }
            }

            getCityList();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
