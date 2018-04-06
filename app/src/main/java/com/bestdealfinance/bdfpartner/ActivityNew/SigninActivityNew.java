package com.bestdealfinance.bdfpartner.ActivityNew;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import static com.bestdealfinance.bdfpartner.R.id.buttonSendOTPBottomSheet;
import static com.bestdealfinance.bdfpartner.R.id.checkBoxConditionBottomsheet;

public class SigninActivityNew extends AppCompatActivity {
    private static final String TAG = "SigninActivityNew";
    private RelativeLayout  relativeLayoutSigninRoot;
    private EditText etLoginInfo;
    private Button btnLogin;
    private EditText etRegisterMobile;
    private EditText etRegisterEmail;
    private Button btnRegisterButton;

    private String type = "email";
    private RequestQueue queue;
    private EditText etLoginEmail;
    private EditText etLoginMobile;
    private TextView tvLoginMessage;
    private Button applyPopButton;
    private TextView promoCodeShow;
    private AppCompatCheckBox tncCheckbox;
    private AlertDialog.Builder builder;
    private EditText promocodeView;
    private Button applyPromo;
    private Button cancelPromo;
    private AlertDialog alertDialog;
    private boolean isApplied = false;
    private int leadPartnerId = 0;
    private TextView tvRegisterMessage;
    private String mobileNumber = "";
    private TextView tvTnCLink;
    private Toolbar toolbar;

    private boolean isAskedPromoCode = false;

    private BottomSheetDialog bottomSheetDialogVeryfyOTP;
    ProgressBar progressBarOpt_login;
    AppCompatEditText edittextOtpLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        queue = Volley.newRequestQueue(this);


        initialize();

        etLoginInfo.addTextChangedListener(new TextW());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("email")) {
                    if (Helper.validateEditText(SigninActivityNew.this,etLoginInfo, "email",false)) {
                        login();
                    }
                } else if (type.equals("mobile")) {
                    if (Helper.validateEditText(SigninActivityNew.this,etLoginInfo, "phone",false)) {
                        login();
                    }
                }
            }
        });

        btnRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.validateEditText(SigninActivityNew.this,etRegisterMobile, "phone",false) && Helper.validateEditText(SigninActivityNew.this,etRegisterEmail, "email",false)) {
                    if (!tncCheckbox.isChecked()) {
                        Toast.makeText(SigninActivityNew.this, "Please accept terms and conditions", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(!isAskedPromoCode){
                        Helper.showAlertDialog(SigninActivityNew.this, "", "Do you have Promocde ? ", "YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isAskedPromoCode = true;
                                if (isApplied) {
                                    leadPartnerId = 0;
                                    isApplied = false;
                                    promoCodeShow.setText(R.string.promocode_helper_text);
                                    applyPopButton.setText(R.string.txt_apply_code);
                                    applyPopButton.setTextColor(getResources().getColor(R.color.Green500));
                                } else {
                                    alertDialog.show();
                                }
                            }
                        }, "NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isAskedPromoCode = true;
                                dialog.dismiss();
                                register();
                            }
                        });
                    }
                    else
                        register();
                }
            }
        });


        applyPopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isApplied) {
                    leadPartnerId = 0;
                    isApplied = false;
                    promoCodeShow.setText(R.string.promocode_helper_text);
                    applyPopButton.setText(R.string.txt_apply_code);
                    applyPopButton.setTextColor(getResources().getColor(R.color.Green500));


                } else {
                    alertDialog.show();
                }


            }
        });
        cancelPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        applyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.hideKeyboard(SigninActivityNew.this);

                if (Helper.validateEditText(SigninActivityNew.this,promocodeView, "text",false)) {
                    alertDialog.dismiss();
                    applyPromoCode();
                }

            }
        });

       /* btnOtpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpLayout.animate().translationY(1000f);
            }
        });

        btnOtpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.validateEditText(SigninActivityNew.this,etOtp, "text",false)) {
                    Helper.hideKeyboard(SigninActivityNew.this);
                    otpLayout.animate().translationY(1000F);
                    verifyOtp();
                }

            }
        });*/


        tvTnCLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SigninActivityNew.this);
                WebView wv = new WebView(SigninActivityNew.this);
                wv.loadUrl(URL.TNC_LINK);
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                alert.setView(wv);
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }

    private void verifyOtp(String otp) {

        try {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Verifying OTP");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("otp", otp);
            reqObject.put("mobile", mobileNumber);

            Log.i(TAG, "verifyOtp: Param "+reqObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.CUSTOMER_VERIFY_OTP, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp "+response);
                    progressDialog.dismiss();
                    JSONObject body = response.optJSONObject("body");
                    if (body.optString("success").equals("1")) {
                        JSONObject customer = body.optJSONObject("customer");
                        Helper.setStringSharedPreference(Constant.USERID, customer.optString("id"), SigninActivityNew.this);
                        Helper.setStringSharedPreference(Constant.USEROBJECT, customer.toString(), SigninActivityNew.this);
                        Helper.setStringSharedPreference(Constant.EMAIL, customer.optString("email"), SigninActivityNew.this);
                        Helper.setStringSharedPreference(Constant.MOBILE, customer.optString("mobile_number"), SigninActivityNew.this);
                        setResult(RESULT_OK);
                        finish();
                    } else {

                        if(progressBarOpt_login!=null)
                            progressBarOpt_login.setVisibility(View.GONE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivityNew.this);
                        builder.setTitle("OTP Error");
                        builder.setMessage(body.optString("message"));
                        builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    if(progressBarOpt_login!=null)
                        progressBarOpt_login.setVisibility(View.GONE);
                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                        final String msg = errorObject.getString("msg");
                        final String body = errorObject.getString("body");
                        AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivityNew.this);
                        builder.setTitle(msg);
                        builder.setMessage(body);
                        builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }) {

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), SigninActivityNew.this);
                    return super.parseNetworkResponse(response);
                }
            };

            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void applyPromoCode() {
        try {
            JSONObject reqObject = new JSONObject();
            reqObject.put("promo", promocodeView.getText().toString());
            Log.i(TAG, "applyPromoCode: params  "+reqObject);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.VERIFY_PROMO, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        Log.d("Response", "" + response.toString());

                        JSONObject body = response.getJSONObject("body");


                        AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivityNew.this);
                        builder.setTitle(R.string.txt_promo_code_applied);
                        builder.setMessage(getString(R.string.txt_partner_name) + body.getString("name"));
                        builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Helper.hideKeyboard(SigninActivityNew.this);
                            }
                        });
                        builder.show();

                        isApplied = true;
                        leadPartnerId = body.getInt("id");
                        applyPopButton.setVisibility(View.VISIBLE);
                        applyPopButton.setText(R.string.txt_remove);
                        applyPopButton.setTextColor(getResources().getColor(R.color.Red500));
                        promoCodeShow.setText(promocodeView.getText().toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    applyPopButton.setVisibility(View.VISIBLE);
                    applyPopButton.setText(R.string.txt_apply_code);
                    applyPopButton.setTextColor(getResources().getColor(R.color.Green500));


                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                        final String msg = errorObject.getString("msg");
                        final String body = errorObject.getString("body");

                        AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivityNew.this);
                        builder.setTitle(msg);
                        builder.setMessage(body);
                        builder.setNeutralButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void register() {
        try {


            btnRegisterButton.setText("Registering...");
            Helper.hideKeyboard(this);
            JSONObject request = new JSONObject();
            request.put("email", etRegisterEmail.getText().toString().trim());
            request.put("mobile_number", etRegisterMobile.getText().toString().trim());
            request.put("is_ba", "1");
            request.put("source", "BA_APP");
            if (isApplied) {
                request.put("lead_partner_id", leadPartnerId);
                request.put("promo_code", promocodeView.getText().toString());
            }
            Log.i(TAG, "register: params  "+request);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL.CUSTOMER_REGISTER, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.i(TAG, "onResponse: register "+response);
                    btnRegisterButton.setText("Register");
                    JSONObject body = response.optJSONObject("body");
                    if (body.optString("success").equals("1")) {
                        JSONObject customer = body.optJSONObject("customer");

                        mobileNumber = etRegisterMobile.getText().toString().trim();
                        tvRegisterMessage.setText("Please verify with otp");
                        tvRegisterMessage.setTextColor(getResources().getColor(R.color.Green500));
                        tvRegisterMessage.setBackgroundResource(R.drawable.custom_border_green);
//                        otpLayout.animate().translationY(0f);

                    } else {
                        tvRegisterMessage.setText(body.optString("message"));
                        tvRegisterMessage.setTextColor(getResources().getColor(R.color.Red500));
                        tvRegisterMessage.setBackgroundResource(R.drawable.custom_border_red);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SigninActivityNew.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Network Error");
                    alertDialog.setMessage("No internet connection available");
                    alertDialog.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            });
            objectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1f));
            queue.add(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        try {
            btnLogin.setText("Logging in...");
            Helper.hideKeyboard(this);
            JSONObject request = new JSONObject();
            request.put("id", etLoginInfo.getText().toString().trim());
            request.put("type", type);
            request.put("is_ba", "1");
            request.put("source", "BA_APP");

            Helper.showLog(URL.CUSTOMER_LOGIN,request.toString());

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL.CUSTOMER_LOGIN, request, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: login "+response);
                    JSONObject body = response.optJSONObject("body");
                    if (body.optString("success").equals("1")) {
                        btnLogin.setText("Login");
                        JSONObject customer = body.optJSONObject("customer");
                        if (type.equals("email")) {
                            etLoginMobile.setVisibility(View.VISIBLE);
                            etLoginMobile.setText(customer.optString("mobile_number"));
                            etLoginMobile.setEnabled(false);
                        } else {
                            etLoginEmail.setVisibility(View.VISIBLE);
                            etLoginEmail.setText(customer.optString("email"));
                            etLoginEmail.setEnabled(false);
                        }
                        mobileNumber = customer.optString("mobile_number");
                        tvLoginMessage.setVisibility(View.VISIBLE);
                        tvLoginMessage.setBackgroundResource(R.drawable.custom_border_green);
                        tvLoginMessage.setText("Please verify with otp");
                        tvLoginMessage.setTextColor(getResources().getColor(R.color.Green500));
//                        otpLayout.animate().translationY(0f);

                        showLoginOtpBottomsheet("");

                    } else {
                        tvLoginMessage.setVisibility(View.VISIBLE);
                        tvLoginMessage.setText("No Account Found. Please register below");
                        tvLoginMessage.setTextColor(getResources().getColor(R.color.Red500));
                        tvLoginMessage.setBackgroundResource(R.drawable.custom_border_red);
                        btnLogin.setText("LOGIN");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    btnLogin.setText("LOGIN");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SigninActivityNew.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Network Error");
                    alertDialog.setMessage("No internet connection available");
                    alertDialog.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            });
            objectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1f));
            queue.add(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initialize() {
        relativeLayoutSigninRoot = (RelativeLayout) findViewById(R.id.relativeLayoutSigninRoot);
        etLoginInfo = (EditText) findViewById(R.id.login_info);
        etLoginEmail = (EditText) findViewById(R.id.login_email);
        etLoginMobile = (EditText) findViewById(R.id.login_mobile);
        tvLoginMessage = (TextView) findViewById(R.id.login_message);
        btnLogin = (Button) findViewById(R.id.login_button);


        etRegisterMobile = (EditText) findViewById(R.id.register_mobile);
        etRegisterEmail = (EditText) findViewById(R.id.register_email);
        btnRegisterButton = (Button) findViewById(R.id.register_button);
        tvRegisterMessage = (TextView) findViewById(R.id.register_message);
        tvTnCLink = (TextView) findViewById(R.id.tncLink);

        applyPopButton = (Button) findViewById(R.id.apply_btn_popup);
        promoCodeShow = (TextView) findViewById(R.id.promo_code_show);
        tncCheckbox = (AppCompatCheckBox) findViewById(R.id.tncBox);

        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_promocode, null);
        builder.setView(view);

        promocodeView = (EditText) view.findViewById(R.id.txt_promocode);
        applyPromo = (Button) view.findViewById(R.id.apply_btn);
        cancelPromo = (Button) view.findViewById(R.id.cancel_popup);
        alertDialog = builder.create();

        toolbar = (Toolbar)  findViewById(R.id.toolbarProfile);
        ToolbarHelper.initializeToolbar(SigninActivityNew.this, toolbar, "Sign In", false, true, true);
    }


    class TextW implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString().trim();

            try {
                Long.parseLong(text);
                type = "mobile";
                changeIcon(type);
            } catch (Exception ex) {
                type = "email";
                changeIcon(type);
            }


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void changeIcon(String type) {
        if (type.equals("email")) {
            etLoginInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0);
        } else if (type.equals("mobile")) {
            etLoginInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0, 0);
        }

        etLoginEmail.setVisibility(View.GONE);
        etLoginMobile.setVisibility(View.GONE);
        tvLoginMessage.setVisibility(View.GONE);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String smsbody = "";
            SmsMessage[] sms = null;
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus == null) {
                    //Snackbar.make(splashView,"Null Receiver", Snackbar.LENGTH_LONG).show();
                } else {
                    sms = new SmsMessage[pdus.length];
                    sms[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);
                    smsbody = sms[0].getMessageBody();
                    String text = getOTP(smsbody);
                    if(bottomSheetDialogVeryfyOTP!=null && bottomSheetDialogVeryfyOTP.isShowing() && edittextOtpLogin!=null){
                        edittextOtpLogin.setText(text);
                    }
                    else{
                        showLoginOtpBottomsheet(text);
                    }
                }
            }
            abortBroadcast();
        }

        String getOTP(String str) {
            String arr[] = str.split("-");
            String result = arr[1].substring(0, 5);
            return result.trim();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(this.receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }


    private void showLoginOtpBottomsheet(String otp){
        AppCompatButton buttonCancelOtp_Login;
        AppCompatButton buttonVeriFylOtp_Login;


        if (bottomSheetDialogVeryfyOTP == null) {
            bottomSheetDialogVeryfyOTP = new BottomSheetDialog(SigninActivityNew.this);
        }
        bottomSheetDialogVeryfyOTP.setContentView(R.layout.bottomsheet_login_otp);


        buttonCancelOtp_Login = (AppCompatButton) bottomSheetDialogVeryfyOTP.findViewById(R.id.buttonCancelOtp_Login);
        buttonVeriFylOtp_Login = (AppCompatButton) bottomSheetDialogVeryfyOTP.findViewById(R.id.buttonVeriFylOtp_Login);
        edittextOtpLogin = (AppCompatEditText) bottomSheetDialogVeryfyOTP.findViewById(R.id.edittextOtpLogin);
        progressBarOpt_login = (ProgressBar) bottomSheetDialogVeryfyOTP.findViewById(R.id.progressBarOpt_login);

        buttonCancelOtp_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogVeryfyOTP.dismiss();
            }
        });

        buttonVeriFylOtp_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpMsg = edittextOtpLogin.getText().toString().trim();
                if(!TextUtils.isEmpty(otpMsg)) {
                    progressBarOpt_login.setVisibility(View.VISIBLE);
                    verifyOtp(otpMsg);
                }
                else{
                    edittextOtpLogin.setEnabled(true);
                    edittextOtpLogin.setError("This Cant be empty");
                }
            }
        });

        bottomSheetDialogVeryfyOTP.setCanceledOnTouchOutside(false);
        bottomSheetDialogVeryfyOTP.show();
    }

}
