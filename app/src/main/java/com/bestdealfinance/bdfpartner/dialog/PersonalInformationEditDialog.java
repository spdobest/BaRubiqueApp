package com.bestdealfinance.bdfpartner.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.bestdealfinance.bdfpartner.activity.ProfileActivity;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ProfileHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.application.Util;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInformationEditDialog extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String TAG = "PersonalInformationEdit";
    private EditText etName, etAddress1, etAddress2, etAddress3, etPincode, etPAN, etAadhar, etPassprot, etOtherProfession;
    private AppCompatSpinner spinnerProfession, spinnerState;
    private AutoCompleteTextView autoCompleteCity;
    private Button btnSave, btnCancel;
    private ProfileHelper profileHelper;
    private RequestQueue queue;
    private String mapId, state, stateId;
    private JSONArray stateJsonArray;
    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> cityAdapter;
    private ArrayAdapter<String> professionAdapter;

    private List<String> stateList = new ArrayList<String>();
    private List<String> cityList = new ArrayList<String>();
    private List<String> professionList = new ArrayList<String>();

    private Toolbar toolbarEeditProfile;
    private LinearLayout linearLayoutEeditProfile;
    // DATABASE DECLARATION
    private DB snappyDB;

    public static PersonalInformationEditDialog newInstance(Context mcontext, ProfileHelper profileHelper) {
        PersonalInformationEditDialog personalInformationEditDialog = new PersonalInformationEditDialog();

        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable("ProfileHelper", profileHelper);
            personalInformationEditDialog.setArguments(bundle);
            personalInformationEditDialog.setArguments(bundle);
        } catch (Exception e) {
            Log.i(TAG, "newInstance:Error" + "\n" + e.getMessage());
        }

        return personalInformationEditDialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //second parameter for always be interface
        Bundle mBundle = getArguments();
        profileHelper = (ProfileHelper) getArguments().getSerializable("ProfileHelper");

//        PersonalInformationEditDialog personalInformationEditDialog = new PersonalInformationEditDialog().newInstance(this);
//        personalInformationEditDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onViewCreated(view, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setWindowAnimations(
                    R.style.styleDialogFragment);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.dialog_edit_personal_info, null);
        getDialog().setTitle(getString(R.string.personal_information));
        setUpUiComponents(inflaterView);
        setCancelable(true);
        queue = Volley.newRequestQueue(getActivity());

        getProfessionFromDatabase();

        getStateValuesFromServerAndSetStateAdapter();

        setValuesForUI(profileHelper);

        toolbarEeditProfile = inflaterView.findViewById(R.id.toolbarEeditProfile);
        linearLayoutEeditProfile = inflaterView.findViewById(R.id.linearLayoutEeditProfile);
        toolbarEeditProfile.setTitle(getResources().getString(R.string.edit_profile));

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels;
        getDialog().getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                                if (stateFromProfile != null) {
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

        etPAN.setText(profileHelper.getPan());
        etAadhar.setText(profileHelper.getAadhar_number());
        etPassprot.setText(profileHelper.getPassport_number());

        etAddress1.setText(profileHelper.getAddress1());
        etAddress2.setText(profileHelper.getAddress2());
        etAddress3.setText(profileHelper.getAddress3());
        if (profileHelper.getPincode() != null && profileHelper.getPincode().length() >= 6)
            etPincode.setText(profileHelper.getPincode());

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
        etName = inflaterView.findViewById(R.id.et_name);

        etOtherProfession = inflaterView.findViewById(R.id.et_other_occupation);

        etAddress1 = inflaterView.findViewById(R.id.et_address1);
        etAddress2 = inflaterView.findViewById(R.id.et_address2);
        etAddress3 = inflaterView.findViewById(R.id.et_address3);
        etPincode = inflaterView.findViewById(R.id.et_pincode);

        etPAN = inflaterView.findViewById(R.id.et_pan);
        etAadhar = inflaterView.findViewById(R.id.et_aadhar);
        etPassprot = inflaterView.findViewById(R.id.et_passport);

        spinnerProfession = inflaterView.findViewById(R.id.spinner_profession);
        spinnerProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String profession = adapterView.getItemAtPosition(i).toString();
                if (profession.equalsIgnoreCase(Constant.OTHERS)) {
                    etOtherProfession.setVisibility(View.VISIBLE);
                } else {
                    etOtherProfession.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerState = inflaterView.findViewById(R.id.spinner_state);
        spinnerState.setOnItemSelectedListener(this);

        autoCompleteCity = inflaterView.findViewById(R.id.autocomplete_city);
        autoCompleteCity.setThreshold(1);


        btnCancel = inflaterView.findViewById(R.id.btn_cancel);
        btnSave = inflaterView.findViewById(R.id.btn_save);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            View focusView = isValid();
            if (focusView != null) {
                if (focusView == autoCompleteCity) {
                    Toast.makeText(getContext(), "Please fill city details", Toast.LENGTH_SHORT).show();
                }
                focusView.requestFocus();
            } else if (TextUtils.isEmpty(etAddress1.getText().toString().trim())) {
                etAddress1.setError(getString(R.string.empty_error));
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
                    if (profession.equals(Constant.OTHERS)) {
                        profession = etOtherProfession.getText().toString();
                    }
                    profileHelper.setProfession(profession);
                }

                setValuesToJson();

                upadteDataOnServer();


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

        Log.i(TAG, "upadteDataOnServer: URL " + URL.UPDATE_CUSTOMER_PROFILE + " \n params " + response);

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, URL.UPDATE_CUSTOMER_PROFILE, response,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((ProfileActivity) getActivity()).getValuesFromServer();
                        dismiss();
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
            customer.put("profession", profileHelper.getProfession());

            customer.put("pan", profileHelper.getPan());
            customer.put("aadhar_number", profileHelper.getAadhar_number());
            customer.put("passport_number", profileHelper.getPassport_number());

            customer.put("address_line_1", profileHelper.getAddress1());
            customer.put("address_line_2", profileHelper.getAddress2());
            customer.put("address_line_3", profileHelper.getAddress3());
            customer.put("city", profileHelper.getCity());
            customer.put("state", profileHelper.getState());
            customer.put("pincode", profileHelper.getPincode());

            Log.i(TAG, "setValuesToJson: customer " + customer.toString());

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
        } else if (spinnerProfession.getSelectedItem().equals(Util.occupation[0])) {
            Toast.makeText(getContext(), getResources().getString(R.string.please_selectProfession), Toast.LENGTH_SHORT).show();
            Helper.showSnackbarMessage(linearLayoutEeditProfile, getResources().getString(R.string.please_selectProfession), Snackbar.LENGTH_SHORT);
            focusView = etPincode;
        }
        /*else if(etAadhar.getText().toString().length()!=12 && etAadhar.getText().toString().length()<12 ){
            etAadhar.setError(getResources().getString(R.string.invalid_adhar));
            Helper.showSnackbarMessage(linearLayoutEeditProfile,getResources().getString(R.string.invalid_adhar), Snackbar.LENGTH_SHORT);
        }*/
        else if (etPincode.getText().toString().isEmpty() && etPincode.getText().toString().length() < 6 && etPincode.getText().toString().equals("000000") && etPincode.getText().toString().length() != 6) {
            Helper.showSnackbarMessage(linearLayoutEeditProfile, getResources().getString(R.string.enter_valid_pincode), Snackbar.LENGTH_SHORT);
            etPincode.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etPincode;
            }
        } else if (autoCompleteCity.getText().toString().isEmpty()) {
            if (focusView == null) {
                focusView = autoCompleteCity;
            }
            Helper.showSnackbarMessage(linearLayoutEeditProfile, getResources().getString(R.string.please_selectCity), Snackbar.LENGTH_SHORT);
        } else if (etOtherProfession.getVisibility() == View.VISIBLE && etOtherProfession.getText().toString().isEmpty()) {
            etOtherProfession.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etOtherProfession;
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
            Helper.showSnackbarMessage(linearLayoutEeditProfile, getResources().getString(R.string.invalid_adhar), Snackbar.LENGTH_SHORT);
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

    private void getProfessionFromDatabase() {
        ArrayList<String> listProfession = new ArrayList<>();
        try {
            snappyDB = DBFactory.open(getActivity());
            if (snappyDB.exists(Constant.DB_PROFESSION_LIST)) {
                JSONObject jsonObjectProfession = new JSONObject(snappyDB.get(Constant.DB_PROFESSION_LIST));
                if (jsonObjectProfession.has("msg")) {
                    if (jsonObjectProfession.getString("msg").equalsIgnoreCase("Success")) {
                        JSONArray jsonArrayProfession = jsonObjectProfession.getJSONArray("body");
                        for (int i = 0; i < jsonArrayProfession.length(); i++) {
                            JSONObject jsonObject = jsonArrayProfession.getJSONObject(i);
                            if (jsonObject.has("item_value"))
                                listProfession.add(jsonObject.getString("item_value"));
                        }
                        // set the adapter to profession spinner
                        // TODO delet and fetch from server and delete  Util.occupation
                        if (listProfession.size() > 0) {
                            listProfession.add(0, "Select Profession");
                            professionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listProfession);
                            professionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerProfession.setAdapter(professionAdapter);
                            String profession = profileHelper.getProfession();
                            if (profession != null) {
                                int spinnerPosition = professionAdapter.getPosition(profession);
                                if (spinnerPosition == -1) {
                                    spinnerPosition = professionAdapter.getPosition(Constant.OTHERS);
                                    etOtherProfession.setVisibility(View.VISIBLE);
                                    etOtherProfession.setText(profession);
                                }
                                spinnerProfession.setSelection(spinnerPosition);
                            }
                        }
                    }
                }
            }
            snappyDB.close();
        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }
    }
}
