
package com.bestdealfinance.bdfpartner.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bestdealfinance.bdfpartner.activity.ContactUs;
import com.bestdealfinance.bdfpartner.activity.DashBoard;
import com.bestdealfinance.bdfpartner.activity.LeaderBoardActivity;
import com.bestdealfinance.bdfpartner.activity.Product_Landing;
import com.bestdealfinance.bdfpartner.activity.ProfileActivity;
import com.bestdealfinance.bdfpartner.activity.ReferralActivity;
import com.bestdealfinance.bdfpartner.activity.TrainingActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.acl.LastOwnerException;
import java.util.Locale;


public class Helper {

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


    public static Intent getNavigationIntent(int id, boolean isLogin, final Context context) {

        switch (id) {
            case R.id.nav_my_profile:
                if (isLogin) return new Intent(context, ProfileActivity.class);
                else { Toast.makeText(context,"Please Login to see profile",Toast.LENGTH_LONG).show(); return null;}
            case R.id.nav_refer_lead:
                return new Intent(context, ReferralActivity.class).putExtra("data", "unavailable");
            case R.id.nav_dashboard:
                return new Intent(context, DashBoard.class).putExtra("val", context.getString(R.string.txt_dashboard));
            case R.id.nav_referral:
                return new Intent(context, DashBoard.class).putExtra("val", context.getString(R.string.txt_referral));
            case R.id.nav_payouts:
                return new Intent(context, DashBoard.class).putExtra("val", context.getString(R.string.txt_payouts));
            case R.id.nav_training:
                return new Intent(context, TrainingActivity.class);
            case R.id.nav_products:
                return new Intent(context, Product_Landing.class);
           /* case R.id.nav_leader_board:
                return new Intent(context, LeaderBoardActivity.class);*/
            case R.id.nav_contact_us:
                return new Intent(context, ContactUs.class);
            case R.id.nav_share: {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Constant.SHARE_TEXT);
                sendIntent.setType("text/plain");
                return sendIntent;
            }
            default:
                return null;
        }

    }

    private static Tracker mTracker;

    synchronized public static Tracker getDefaultTracker(Context context) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-63900664-1");
        }
        return mTracker;
    }


    static int cacheSize = 4 * 1024 * 1024; // 4MiB
    static LruCache<String, String> cache = new LruCache<>(cacheSize);


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




    /*public static String translateString(String string, Context context) {

        final String lang = Locale.getDefault().getLanguage();


        RequestQueue queue = Volley.newRequestQueue(context);
        if (cache.get(md5(string)) == null) {
            Log.d("Translation", "Translation not in cache");
            try {
                JSONObject req = new JSONObject();
                req.put("key", "tra!33$");
                req.put("string", string);
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
*/

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

}
