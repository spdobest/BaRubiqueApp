package com.bestdealfinance.bdfpartner.ActivityNew;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import com.bestdealfinance.bdfpartner.application.URL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siba.prasad on 26-12-2016.
 */

public class OtherActivity extends AppCompatActivity {
    private static final String TAG = "OtherActivity";
    private RequestQueue jsonObjectRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jsonObjectRequest = Volley.newRequestQueue(this);
    }

    private void getAdharOtp(String adharNo, boolean conscent) {
        try {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Verifying OTP");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("aadhaar_no", adharNo);
            reqObject.put("consent", conscent);

            Log.i(TAG, "verifyOtp: Param " + reqObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.VERIFY_ADHAR, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp " + response);
                    progressDialog.dismiss();
                    JSONObject body = response.optJSONObject("body");
                    if (body.optString("success").equals("1")) {

                    } else {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                        final String msg = errorObject.getString("msg");
                        final String body = errorObject.getString("body");
                        AlertDialog.Builder builder = new AlertDialog.Builder(OtherActivity.this);
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
                    Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), OtherActivity.this);
                    return super.parseNetworkResponse(response);
                }
            };

            jsonObjectRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void verifyAdharOTP(String otp, String transaction_id) {
        try {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Verifying OTP");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("otp", otp);
            reqObject.put("transaction_id", transaction_id);

            Log.i(TAG, "verifyOtp: Param " + reqObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.OTP_VERIFICATION, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp " + response);
                    progressDialog.dismiss();
                    JSONObject body = response.optJSONObject("body");
                    if (body.optString("success").equals("1")) {

                    } else {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                        final String msg = errorObject.getString("msg");
                        final String body = errorObject.getString("body");
                        AlertDialog.Builder builder = new AlertDialog.Builder(OtherActivity.this);
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
                    Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), OtherActivity.this);
                    return super.parseNetworkResponse(response);
                }
            };

            jsonObjectRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getAdharFileData(  String transaction_id) {
        try {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Verifying OTP");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("transaction_id", transaction_id);

            Log.i(TAG, "verifyOtp: Param " + reqObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.GET_ADHAR_DATA, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp " + response);
                    progressDialog.dismiss();
                    JSONObject body = response.optJSONObject("body");
                    if (body.optString("success").equals("1")) {

                    } else {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                        final String msg = errorObject.getString("msg");
                        final String body = errorObject.getString("body");
                        AlertDialog.Builder builder = new AlertDialog.Builder(OtherActivity.this);
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
                    Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), OtherActivity.this);
                    return super.parseNetworkResponse(response);
                }
            };

            jsonObjectRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void updateAdharData( String lead_id, String transaction_id,String product_type) {
        try {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Verifying OTP");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("transaction_id", transaction_id);

            Log.i(TAG, "verifyOtp: Param " + reqObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.UPDATE_ADHAR_DATA, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp " + response);
                    progressDialog.dismiss();
                    JSONObject body = response.optJSONObject("body");
                    if (body.optString("success").equals("1")) {

                    } else {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                        final String msg = errorObject.getString("msg");
                        final String body = errorObject.getString("body");
                        AlertDialog.Builder builder = new AlertDialog.Builder(OtherActivity.this);
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
                    Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), OtherActivity.this);
                    return super.parseNetworkResponse(response);
                }
            };

            jsonObjectRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void updateAdharDatainDatabase( String lead_id, String transaction_id,String product_type) {
        try {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Verifying OTP");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JSONObject reqObject = new JSONObject();
            reqObject.put("transaction_id", transaction_id);

            Log.i(TAG, "verifyOtp: Param " + reqObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.UPDATE_ADHAR_DATA, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: verifyOtp " + response);
                    progressDialog.dismiss();
                    JSONObject body = response.optJSONObject("body");
                    if (body.optString("success").equals("1")) {

                    } else {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        JSONObject errorObject = new JSONObject(new String(networkResponse.data));

                        final String msg = errorObject.getString("msg");
                        final String body = errorObject.getString("body");
                        AlertDialog.Builder builder = new AlertDialog.Builder(OtherActivity.this);
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
                    Helper.setStringSharedPreference(Constant.UTOKEN, response.headers.get("utoken"), OtherActivity.this);
                    return super.parseNetworkResponse(response);
                }
            };

            jsonObjectRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
