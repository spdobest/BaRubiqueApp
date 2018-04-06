package com.bestdealfinance.bdfpartner.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.ProfileActivity;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ProfileHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.application.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BankInformationEditDialog extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "BankInformationEditDial";
    private EditText etAccountHolderName, etAccountNumber, etReenterAccountNumber, etIFSC, etBankName, etBranchName;
    private Button btnSave, btnCancel;
    private ProfileHelper profileHelper;
    private RequestQueue queue;

    //widgets
    private Toolbar toolbarEeditBankDetails;

    public static BankInformationEditDialog newInstance(Context mcontext, ProfileHelper profileHelper) {
        BankInformationEditDialog bankInformationEditDialog = new BankInformationEditDialog();

        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable("ProfileHelper", profileHelper);
            bankInformationEditDialog.setArguments(bundle);
            bankInformationEditDialog.setArguments(bundle);
        } catch (Exception e) {
            Log.i(TAG, "newInstance:Error" + "\n" + e.getMessage());
        }

        return bankInformationEditDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View inflaterView = inflater.inflate(R.layout.dialog_edit_bank_info, null);
        getDialog().setTitle(getString(R.string.bank_information));
        setUpUiComponents(inflaterView);
        setCancelable(true);
        queue = Volley.newRequestQueue(getActivity());

        profileHelper = (ProfileHelper) getArguments().getSerializable("ProfileHelper");

        setValuesForUI(profileHelper);

        toolbarEeditBankDetails = inflaterView.findViewById(R.id.toolbarEeditBankDetails);
        toolbarEeditBankDetails.setTitle(getResources().getString(R.string.edit_bank));

        return inflaterView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels;
        getDialog().getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void setValuesForUI(ProfileHelper profileHelper) {

        etAccountHolderName.setText(profileHelper.getBank_holder_name());
        etBankName.setText(profileHelper.getBank_name());
        etAccountNumber.setText(profileHelper.getAccount_number());
        etIFSC.setText(profileHelper.getIfsc());
        etBranchName.setText(profileHelper.getBranch_name());

    }

    private void setUpUiComponents(View inflaterView) {
        etAccountHolderName = inflaterView.findViewById(R.id.et_holder_name);
        etAccountNumber = inflaterView.findViewById(R.id.et_bank_account);
        etReenterAccountNumber = inflaterView.findViewById(R.id.et_bank_re_account);
        etIFSC = inflaterView.findViewById(R.id.et_ifsc);
        etBankName = inflaterView.findViewById(R.id.et_bank_name);
        etBranchName = inflaterView.findViewById(R.id.et_branch_name);

        btnCancel = inflaterView.findViewById(R.id.btn_cancel);
        btnSave = inflaterView.findViewById(R.id.btn_save);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        etReenterAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etAccountNumber.getText() != null) {
                    //accountNumberForVerification = etAccountNumber.getText().toString();
                    etAccountNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            View focusView = isValid();
            if (focusView != null) {
                focusView.requestFocus();
            } else {
                profileHelper.setBank_holder_name(etAccountHolderName.getText().toString());
                profileHelper.setAccount_number(etAccountNumber.getText().toString());
                profileHelper.setIfsc(etIFSC.getText().toString());
                profileHelper.setBank_name(etBankName.getText().toString());
                profileHelper.setBranch_name(etBranchName.getText().toString());

                setValuesToJson();

                sendDataToServer();


            }
        } else if (view.getId() == R.id.btn_cancel) {
            dismiss();
        }


    }


    private void sendDataToServer() {
        JSONObject response = profileHelper.getResponse();
        try {
            response.put(Constant.UTOKEN, Helper.getStringSharedPreference(Util.utoken, getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private void setValuesToJson() {
        JSONObject response = profileHelper.getResponse();

        JSONObject body, bankDetails;
        try {
            body = response.getJSONObject(Constant.BODY);
            bankDetails = body.getJSONObject("ba_bank");

            bankDetails.put("account_number", profileHelper.getAccount_number());
            bankDetails.put("holder_name", profileHelper.getBank_holder_name());

            bankDetails.put("bank_name", profileHelper.getBank_name());
            bankDetails.put("branch_name", profileHelper.getBranch_name());
            bankDetails.put("ifsc", profileHelper.getIfsc());

            bankDetails.put("pan", profileHelper.getBank_pan());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View isValid() {
        View focusView = null;

        if (etAccountHolderName.getText().toString().isEmpty()) {
            etAccountHolderName.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etAccountHolderName;
            }
        } else if (etIFSC.getText().toString().isEmpty()) {
            etIFSC.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etIFSC;
            }
        } else if (etBankName.getText().toString().isEmpty()) {
            etBankName.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etBankName;
            }
        } else if (etBranchName.getText().toString().isEmpty()) {
            etBranchName.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etBranchName;
            }
        } else if (etAccountNumber.getText().toString().isEmpty()) {
            etAccountNumber.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etAccountNumber;
            }
        } else if (etReenterAccountNumber.getText().toString().isEmpty()) {
            etReenterAccountNumber.setError(getString(R.string.empty_error));
            if (focusView == null) {
                focusView = etReenterAccountNumber;
            }
        }

        if (focusView == null && !etReenterAccountNumber.getText().toString().equals(etAccountNumber.getText().toString())) {
            etReenterAccountNumber.setError(getString(R.string.account_number_not_matched));
            focusView = etReenterAccountNumber;
        }
        return focusView;
    }

}
