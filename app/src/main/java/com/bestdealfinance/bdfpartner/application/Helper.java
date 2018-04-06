
package com.bestdealfinance.bdfpartner.application;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.LruCache;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.base.SnackbarCallback;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;


public class Helper {

    static int cacheSize = 4 * 1024 * 1024; // 4MiB
    static LruCache<String, String> cache = new LruCache<>(cacheSize);
    private static Tracker mTracker;

    public static void setIntegerSharedPreference(String key, int value, Context context) {

        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntegerSharedPreference(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getInt(key, 0);
    }

    public static void setStringSharedPreference(String key, String value, Context context) {

        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringSharedPreference(String key, Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static void removeSharedPreference(String key, Context context) {

        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(key);
        editor.commit();
    }

    public static void deleteAllUserData(Context context) {

        Util.o_id = "";
        Util.email = "";
        Util.mobile = "";

        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(Util.username);
        editor.remove(Util.utoken);
        editor.remove(Util.userid);
        editor.apply();

        SharedPreferences.Editor pref_reg = context.getApplicationContext().getSharedPreferences("BDFREG2", 0).edit();
        pref_reg.remove("registration_id");
        pref_reg.apply();

    }

    public static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        return editor;
    }

    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(context, "Please Update Google Play Services.", Toast.LENGTH_SHORT);
            } else {
                Logs.LogD("PlayService", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean validateEditText(Context context, EditText editText, String type, boolean isToastEnable) {
        editText.setEnabled(true);
        if (editText.getText().toString().isEmpty()) {
            editText.setError("This field cannot be left blank");
            editText.requestFocus();
            String hint = editText.getHint().toString();
            hint = hint.replace("*", "");
            if (isToastEnable)
                Toast.makeText(context, "Specify " + hint, Toast.LENGTH_SHORT).show();
            editText.getHint();

            return false;
        } else switch (type) {
            case "phone":
                if (editText.getText().toString().trim().length() != 10) {
                    editText.setError("Phone Number should be of 10 digits");
                    editText.requestFocus();
                    return false;
                } else return true;
            case "email":
                if (!Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString().trim()).matches()) {
                    editText.setError("Invalid Email");
                    editText.requestFocus();
                    return false;
                } else return true;
            case "pan":
                if (!Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}").matcher(editText.getText().toString().trim()).matches()) {
                    editText.setError("Invalid Pan Card");
                    editText.requestFocus();
                    return false;
                } else return true;

            case "adhar":
                if (editText.getText().toString().trim().length() != 12 && editText.getText().toString().trim().length() > 12 && editText.getText().toString().trim().length() < 12) {
                    editText.setError("Invalid Adhar Number");
                    editText.requestFocus();
                    return false;
                } else return true;

            default:
                return true;
        }
    }

    public static boolean validate(final TextInputLayout textInputLayout, EditText editText, String type, Context context) {
        if (editText.getText().toString().isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("This field cannot be left blank");
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    textInputLayout.setErrorEnabled(false);
                }
            });
            return false;
        } else {
            if (type.equals("phone")) {
                if (editText.getText().toString().length() == 10) {
                    textInputLayout.setErrorEnabled(false);
                    return true;
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(context.getString(R.string.txt_error_phone));
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            textInputLayout.setErrorEnabled(false);
                        }
                    });
                    return false;
                }

            } else if (type.equals("email")) {
                if (Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString().trim()).matches()) {
                    textInputLayout.setErrorEnabled(false);
                    return true;
                } else {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(context.getString(R.string.txt_error_email));
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            textInputLayout.setErrorEnabled(false);
                        }
                    });
                    return false;
                }
            } else {
                textInputLayout.setErrorEnabled(false);
                return true;
            }


        }

    }

    synchronized public static Tracker getDefaultTracker(Context context) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-63900664-1");
        }
        return mTracker;
    }

    public static void translateTextView(final TextView textView, Context context) {
        String currentText = "" + textView.getText().toString().toLowerCase();
        final String lang = Locale.getDefault().getLanguage();


        RequestQueue queue = Volley.newRequestQueue(context);
        if (cache.get(md5(currentText)) == null) {
            Log.d("Translation", "Translation not in cache");
            try {
                JSONObject req = new JSONObject();
                req.put("key", "tra!33$");
                req.put("string", currentText);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.TRANSLATION, req, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject body = response.getJSONObject("body");
                            cache.put(body.getString("hash"), body.toString());
                            textView.setText(body.getString(lang));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.d("Translation", "Translation available in cache");
            JSONObject body = null;
            try {
                body = new JSONObject(cache.get(md5(currentText)));
                textView.setText(body.getString(lang));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    public static void translateAlertBox(final AlertDialog.Builder builder, String title, String message, Context context) {
        final String lang = Locale.getDefault().getLanguage();


        RequestQueue queue = Volley.newRequestQueue(context);
        if (cache.get(md5(title)) == null) {
            Log.d("Translation", "Translation not in cache");
            try {
                JSONObject req = new JSONObject();
                req.put("key", "tra!33$");
                req.put("string", title);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.TRANSLATION, req, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject body = response.getJSONObject("body");
                            cache.put(body.getString("hash"), body.toString());
                            builder.setTitle(body.getString(lang));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.d("Translation", "Translation available in cache");
            JSONObject body = null;
            try {
                body = new JSONObject(cache.get(md5(title)));
                builder.setTitle(body.getString(lang));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        if (cache.get(md5(message)) == null) {
            Log.d("Translation", "Translation not in cache");
            try {
                JSONObject req = new JSONObject();
                req.put("key", "tra!33$");
                req.put("string", message);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.TRANSLATION, req, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject body = response.getJSONObject("body");
                            cache.put(body.getString("hash"), body.toString());
                            builder.setMessage(body.getString(lang));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.d("Translation", "Translation available in cache");
            JSONObject body = null;
            try {
                body = new JSONObject(cache.get(md5(message)));
                builder.setMessage(body.getString(lang));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        builder.show();

    }

    public static void translateButton(final Button textView, Context context) {
        String currentText = "" + textView.getText().toString();
        final String lang = Locale.getDefault().getLanguage();


        RequestQueue queue = Volley.newRequestQueue(context);
        if (cache.get(md5(currentText)) == null) {
            Log.d("Translation", "Translation not in cache");
            try {
                JSONObject req = new JSONObject();
                req.put("key", "tra!33$");
                req.put("string", currentText);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.TRANSLATION, req, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject body = response.getJSONObject("body");
                            cache.put(body.getString("hash"), body.toString());
                            textView.setText(body.getString(lang));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.d("Translation", "Translation available in cache");
            JSONObject body = null;
            try {
                body = new JSONObject(cache.get(md5(currentText)));
                textView.setText(body.getString(lang));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    public static void translateEditText(final EditText textView, Context context) {
        String currentText = "" + textView.getHint();
        final String lang = Locale.getDefault().getLanguage();


        RequestQueue queue = Volley.newRequestQueue(context);
        if (cache.get(md5(currentText)) == null) {
            Log.d("Translation", "Translation not in cache");
            try {
                JSONObject req = new JSONObject();
                req.put("key", "tra!33$");
                req.put("string", currentText);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.TRANSLATION, req, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject body = response.getJSONObject("body");
                            cache.put(body.getString("hash"), body.toString());
                            textView.setHint(body.getString(lang));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.d("Translation", "Translation available in cache");
            JSONObject body = null;
            try {
                body = new JSONObject(cache.get(md5(currentText)));
                textView.setHint(body.getString(lang));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    public static void translateTextInputLayout(final TextInputLayout textView, Context context) {
        String currentText = "" + textView.getHint();
        final String lang = Locale.getDefault().getLanguage();


        RequestQueue queue = Volley.newRequestQueue(context);
        if (cache.get(md5(currentText)) == null) {
            Log.d("Translation", "Translation not in cache");
            try {
                JSONObject req = new JSONObject();
                req.put("key", "tra!33$");
                req.put("string", currentText);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.TRANSLATION, req, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject body = response.getJSONObject("body");
                            cache.put(body.getString("hash"), body.toString());
                            textView.setHint(body.getString(lang));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.d("Translation", "Translation available in cache");
            JSONObject body = null;
            try {
                body = new JSONObject(cache.get(md5(currentText)));
                textView.setHint(body.getString(lang));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    public static void translate(final String string, Context context, final Translator translator) {
        String currentText = string;
        final String lang = Locale.getDefault().getLanguage();


        RequestQueue queue = Volley.newRequestQueue(context);
        if (cache.get(md5(currentText)) == null) {
            Log.d("Translation", "Translation not in cache");
            try {
                JSONObject req = new JSONObject();
                req.put("key", "tra!33$");
                req.put("string", currentText);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Util.TRANSLATION, req, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject body = response.getJSONObject("body");
                            cache.put(body.getString("hash"), body.toString());
                            translator.onTranslate(body.getString(lang));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.d("Translation", "Translation available in cache");
            JSONObject body = null;
            try {
                body = new JSONObject(cache.get(md5(currentText)));
                translator.onTranslate(body.getString(lang));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    public static String isPANValid(String panNumber) {

        char[] panArray = panNumber.toCharArray();
        if (panArray.length < 10) {
            return "PAN can not be less that 10 characters";
        } else if (panArray.length > 10) {
            return "PAN can not be more that 10 characters";
        }
        for (int i = 0; i < panArray.length; i++) {
            if (i >= 0 && i < 5) {
                if (!Character.isLetter(panArray[i])) {
                    return "One of the first five character is not an alphabet";
                }
            } else if (i >= 5 && i < 9) {
                if (!Character.isDigit(panArray[i])) {
                    return "Middle characters are not digit";
                }
            } else if (i == 9) {
                if (!Character.isLetter(panArray[i])) {
                    return "Last character is not an alphabet";
                }
            }

        }
        return null;
    }


    public static String isAadharValid(String aadharNumber) {

        char[] aadharArray = aadharNumber.toCharArray();
        if (aadharArray.length < 12) {
            return "Aadhar can not be less that 12 characters";
        } else if (aadharArray.length > 12) {
            return "Aadhar can not be more that 12 characters";
        }
        for (int i = 0; i < aadharArray.length; i++) {
            if (!Character.isDigit(aadharArray[i])) {
                return "One of the character is not a digit";
            }
        }
        return null;
    }


    public static String isLandMarkValid(String landMark) {

        char[] landMarkArray = landMark.toCharArray();

        if (landMarkArray.length == 1) {
            return null;
        }
        boolean hasCharacter = false;

        for (int i = 0; i < landMarkArray.length; i++) {
            if (Character.isLetter(landMarkArray[i])) {
                hasCharacter = true;
                break;
            }
        }
        if (!hasCharacter) {
            return "Landmark must have an alphabet";
        }
        return null;
    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void showAlertDialog(Context context, String title, String message, String ptext, DialogInterface.OnClickListener plistener, String ntext, DialogInterface.OnClickListener nlistener) {
        if (context != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton(ptext, plistener);
            alertDialogBuilder.setNegativeButton(ntext, nlistener);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.Green500));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.Red500));
        }

    }

    public static String formatRs(String amount) throws NumberFormatException {
        if (amount == null || amount.equals("null") || amount.equals("")) {
            return "0";
        } else {
            amount = amount.replaceAll(",", "");
            NumberFormat formatter = new DecimalFormat("##,##,##,###");
            String result = formatter.format(Double.parseDouble(amount));
            return result;
        }

    }

    public static String timestampDate(String timestamp) {
        if (timestamp == null || timestamp.equals("null") || timestamp.isEmpty()) {
            return "";
        }
        String dates[] = timestamp.split(" ");
        String[] subdate = dates[0].split("-");
        String day;
        String month;
        String year;
        day = subdate[2];
        month = subdate[1];
        year = subdate[0];
        String finalDate = convertDate(day, month);
        return finalDate;
    }

    public static String timestampTime(String timestamp) {
        // "startDateTime": "2016-10-19 10:54:28",
        if (timestamp == null || timestamp.equals("null") || timestamp.isEmpty()) {
            return "";
        }

        String datetime[] = timestamp.split(" ");
        String time[] = datetime[1].split(":");

        return time[0] + ":" + time[1];
    }

    public static long convertDateToInt(String timestamp) {
        // "startDateTime": "2016-10-19 10:54:28",
        if (timestamp == null || timestamp.equals("null") || timestamp.isEmpty()) {
            return 0;
        }

        String datetime[] = timestamp.split(" ");
        String dateString = datetime[0];
        String dateArray[] = dateString.split("-");
        String date = dateArray[0] + dateArray[1] + dateArray[2];
        String timeString = datetime[1];
        String timeArray[] = timeString.split(":");
        date += timeArray[0] + timeArray[1] + timeArray[2];
        return Long.parseLong(date);

    }

    private static String convertDate(String day, String month) {
        String temp;
        switch (month) {
            case "01":
                temp = "Jan";
                break;
            case "02":
                temp = "Feb";
                break;
            case "03":
                temp = "Mar";
                break;
            case "04":
                temp = "Apr";
                break;
            case "05":
                temp = "May";
                break;
            case "06":
                temp = "June";
                break;
            case "07":
                temp = "July";
                break;
            case "08":
                temp = "Aug";
                break;
            case "09":
                temp = "Sept";
                break;
            case "10":
                temp = "Oct";
                break;
            case "11":
                temp = "Nov";
                break;
            case "12":
                temp = "Dec";
                break;
            default:
                temp = " ";
                break;
        }
        int date_day = Integer.parseInt(day);
        day = String.valueOf(date_day);
        String final_date = day + " " + temp;
        return final_date;
    }

    public static int getBankColor(String bankID) {
        int color = Color.rgb(25, 104, 129);
        switch (bankID) {

            case "1"://HDFC
                color = Color.rgb(11, 40, 117);
                break;
            case "2"://ICICI
                color = Color.rgb(224, 102, 16);
                break;
            case "5"://AU;
                color = Color.rgb(230, 64, 0);
                break;
            case "6"://RBL;
                color = Color.parseColor("#fa0000");
                break;
            case "8"://Fullerton;
                color = Color.rgb(200, 97, 39);
                break;
            case "11"://Hinduja
                color = Color.rgb(0, 111, 182);
                break;
            case "13": //Indiabulls
                color = Color.rgb(0, 120, 56);
                break;
            case "46"://Bajaj
                color = Color.rgb(0, 110, 181);
                break;
            case "47"://Indusnd
                color = Color.rgb(156, 58, 59);
                break;
            case "48"://Amex
                color = Color.parseColor("#105080");
                break;
            case "49"://Citi
                color = Color.rgb(0, 59, 112);
                break;
            case "51"://HFFC
                color = Color.rgb(23, 96, 164);
                break;
            case "54"://Tata
                color = Color.rgb(25, 104, 129);
                break;
            case "56"://SBI
                color = Color.parseColor("#0098db");
                break;
            case "58"://Edelwise
                color = Color.rgb(23, 55, 130);
                break;
            default:
                color = Color.rgb(0, 59, 112);
                break;
        }
        return color;
    }

    public static void showSnackbarMessage(View rootLayout, String message, int snackbarTime) {
        Snackbar snackbar = Snackbar
                .make(rootLayout, message, snackbarTime);
        snackbar.show();
    }

    //SNACKBAR WITH ACTION BUTTON
    public static void showSnackBar(final SnackbarCallback snackbarCallback, final View rootlayout, String message, final String action_name) {
        Snackbar snackbar = Snackbar
                .make(rootlayout, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action_name, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbarCallback.onSnackbarActionClick();
            }
        });

        snackbar.show();
    }

    public static void showToastWithUrl(Context context, String url) {
        Toast.makeText(context, "" + url, Toast.LENGTH_SHORT).show();
    }

    public static void showLog(String url, String params) {
        Log.i("API CALL ", "showLog : URL " + url + " params " + params);
    }

    public static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else if (phoneNo.substring(0, 3).contains("00")) return false;
        else return false;

    }
}
