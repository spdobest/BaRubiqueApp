package com.bestdealfinance.bdfpartner.application;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by disha on 7/2/16.
 */
public class Util {


    //TODO Production URLS


    //public static final String ROOT_URL_FI = "https://prod-fiapi.rubique.com/";
    //public static final String S3_URL = "https://staticcontent.rubique.com/json/";
    //public static final String ROOT_URL_V2 = "https://api.rubique.com/";


    //TODO Test URLS

    public static final String ROOT_URL_V2 = "http://testapi-newarch.rubique.com/";
    public static final String ROOT_URL_FI = "http://testfiapi.rubique.com/";
    public static final String S3_URL = "https://staticcontent.rubique.com/json/";


    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String pan_pattern = "[a-zA-Z]{5}+[0-9]{4}+[a-zA-Z]{1}";


    public static final String MY_PREFERENCES = "bdfpartner";
    public static final String utoken = "utoken";
    public static final String username = "username";
    public static final String userid = "userid";
    public static final String phone = "phone";

    public static final String GCM_REGISTER = Util.ROOT_URL_V2 + "notification/registerApp";
    public static final String GCM_UPDATE = Util.ROOT_URL_V2 + "notification/updateAppRegistration";
    public static final String APP_UPDATE = Util.ROOT_URL_V2 + "SystemVar/load";

    public static final String GET_CUSTOMER_PROFILE = Util.ROOT_URL_V2 + "Profile/getCustomerProfile";
    public static final String UPDATE_CUSTOMER_PROFILE = Util.ROOT_URL_V2 + "Profile/upsertCustomerProfile";

    public static final String TRAINING_FETCH = Util.ROOT_URL_V2 + "index.php/Training/getTrainingInfo";
    public static final String TRANSLATION = Util.ROOT_URL_V2+"Translation/getTranslation";


    public static final String GET_STATE_LIST = Util.ROOT_URL_V2 + "Profile/stateDD";
    public static final String GET_CITY_LIST = Util.ROOT_URL_V2 + "Profile/cityDD";
    public static final String GET_PROFESSION = Util.ROOT_URL_V2 + "Profile/profession";

    public static final String COMPANYLISTV2 = Util.ROOT_URL_V2 + "List_controller/listAutoSuggestion";
    public static final String UPDATE_INCOMPLETE_LEAD = ROOT_URL_V2 + "index.php/Leads/updateIncompleteLead";
    public static final String DUPLICATE_LEAD = ROOT_URL_V2 + "leads/duplicateLeadForConsumerPortal";
    public static final String QUALIFY_PRODUCT = ROOT_URL_V2 + "leads/qualify";
    public static final String SUBMITAPPLICATIONBA = ROOT_URL_V2 + "leads/submitApplicationBa";
    public static final String GET_DESCRIPTION = "http://test.rubique.com/assets/products-description.json";
    public static final String ALL_PRODUCTS_BY_TYPE = ROOT_URL_V2 + "product/getProductByType";
    public static final String PRODUCT_INFO_BYID = ROOT_URL_V2 + "product/getProductInfoById";
    /*public static final String STEP_ONE_SEARCH = ROOT_URL_V1 + "/mobileverify/productsearch_stepone";
    public static final String GET_CITY = ROOT_URL_V1 + "/AssociatePartner/getcity";
    public static final String COMPANYLIST = ROOT_URL_V1 + "/getPincode_Company/getCompany";*/
    public static final String GET_LOGIN = ROOT_URL_V2 + "index.php/Customer/getLogin";
    public static final String VERIFY_PROMO = ROOT_URL_V2 + "index.php/Promotion/getPromoCode";
    public static final String REGISTER = ROOT_URL_V2 + "index.php/Customer/register";
    public static final String REGISTER_NEW = ROOT_URL_V2 + "customer/registerCustomer";
    public static final String ALLLISTS = ROOT_URL_V2 + "List_controller/listAutoSuggestion";
    public static final String REFER_A_LEAD = ROOT_URL_V2 + "index.php/Customer/submitMobReferral";
    public static final String LOGIN = ROOT_URL_V2 + "index.php/Customer/login";
    public static final String CHANGE_PASSWORD = ROOT_URL_V2 + "index.php/customer/changePassword";
    public static final String LOGOUT = ROOT_URL_V2 + "index.php/Customer/logout";
    public static final String RESET_PASSWORD = ROOT_URL_V2 + "index.php/customer/resetPassword";
    public static final String FORGOT_PASSWORD = ROOT_URL_V2 + "index.php/customer/forgotPassword";
    public static final String IS_MOBILE_VERIFIED = ROOT_URL_V2 + "index.php/customer/isMobileVerified";
    public static final String SEND_OTP = ROOT_URL_V2 + "index.php/customer/sendOTP";
    public static final String CHECK_OTP = ROOT_URL_V2 + "index.php/customer/checkOTP";
    public static final String SEND_EMAIL = ROOT_URL_V2 + "index.php/customer/sendVerficationEmail";
    //    public static final String CHECK_EMAIL = ROOT_URL + "index.php/customer/checkEmailVerification";
    public static final String IS_EMAIL_VERIFIED = ROOT_URL_V2 + "index.php/customer/isEmailVerified";
    public static final String PRODUCT_TYPE = ROOT_URL_V2 + "index.php/Product/getAllProductTypes";
    //    public static final String PRODUCT_TYPE = "http://192.168.1.190/rubique.2/trunk/services/rubique_services/index.php/Product/getAllProductTypes";
//    public static final String PRODUCT = "http://192.168.1.190/rubique.2/trunk/services/rubique_services/index.php/Product/getProductByType";
    public static final String PRODUCT = ROOT_URL_V2 + "index.php/Product/getProductByType";
    public static final String GET_DASHBOARD = ROOT_URL_V2 + "index.php/Customer/getCustomerDashboard";
    public static final String GET_REFERRAL_LIST = ROOT_URL_V2 + "index.php/Customer/GetReferralList";
    public static final String GET_LEAD_LIST = ROOT_URL_V2 + "leads/getMyApplications";
    public static final String GET_REFERRAL_DETAIL = ROOT_URL_V2 + "index.php/customer/getLeadDetails";
    public static final String ABOUT_PHILOSOPHY = S3_URL + "ourPhilosophy.json";
    //    public static final String ABOUT_PHILOSOPHY =  "http://192.168.1.190/rubique.2/trunk/services/rubique_services/index.php/content/ourPhilosophy";
    public static final String ABOUT_TEAM = S3_URL + "ourTeam.json";
    public static final String ABOUT_US = S3_URL + "aboutUs.json";
    //    public static final String ABOUT_US =  "http://192.168.1.190/rubique.2/trunk/services/rubique_services/index.php/content/aboutUs";
    public static final String CONTACT_US = ROOT_URL_V2 + "index.php/Enquiry/addEnquiry";
    public static final String OFFER = S3_URL + "ourOffer.json";
    //public static final String OFFER_STEP_ONE = ROOT_URL_V1 + "/mobileverify/productsearch_stepone";
    public static final String APPLICATION_SUBMIT = ROOT_URL_V2 + "customer/submitCustomerAppWithFinbankDetails";
    public static final Executor threadPool = Executors.newFixedThreadPool(4);
    public static final String[] yes_no = {"Yes", "No"};
    public static final String[] occupation = {"Housewife", "Retired", "Self Employed", "Self Employed Professional", "Self Employed Professional Doctor", "Student", "Others"};
    public static final String[] occupation_without_income = {"Housewife", "Retired", "Self Employed", "Self employed - No income proof", "Self Employed Professional", "Self Employed Professional Doctor", "Student"};
    public static final String PAYOUT_FOR_LOANS = ROOT_URL_V2 + "payout/getAllProductTypeCommissionInfo";
    public static final String PAYOUT_FOR_CC = ROOT_URL_V2 + "payout/getCCPayouts";
    public static final String CALCULATE_PAYOUT = ROOT_URL_V2 + "payout/calculatePayout";
    public static final String CALCULATE_PAYOUT_PRODUCT = ROOT_URL_V2 + "payout/calculatePayoutForCC";
    public static final String PAYOUT_CALCULATOR = ROOT_URL_V2 + "payout/payoutCalculator";

    public static final String GETTEMPLATEPARTIAL = ROOT_URL_V2 + "leads/getTemplate";
    public static final String JSON_FORM = ROOT_URL_FI + "/finbank/template/ordered?productTypeId=";
    public static final String CREATE_INCOMPLETE_LEAD = ROOT_URL_V2 + "/leads/createIncompleteLead";
    public static final String SOME_ACTION = "com.bestdealfinance.partner.refresh";
    public static final String[] product = {"Credit Card", "Car Loan", "Two Wheeler Loan", "Personal Loan", "Home Loan", "Loan Against Property", "Business Loan"};
    public static final String[] card = {"Credit Card"};
    public static String o_id = "";
    public static String mobile = "";
    public static String ltoken = "";

    public static String email = "";
    public static String nav_status = "";

    public static boolean VALIDATE = true;
    public static String[] user_data_duration = {"This Week", "This Month", "This Year", "All"};

    //    http://static.rubique.com/json/ourPhilosophy.json
//    http://static.rubique.com/json/ourTeam.json
    public static HashMap<String, String> associativeHash = new HashMap<>();
    public static ArrayList<HashMap<String, String>> product_type_data;
    public static ArrayList<String> city_names;
    public static String selected_manufacturer_id;

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String isRegistered(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constant.UTOKEN, "");
    }

    public static String getPhoneNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constant.USER_PHONE, "");
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

    public static boolean isCheckEmail(String email) {
        if (email.matches(emailPattern) && email.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPANVALID(String email) {
        if (email.matches(pan_pattern) && email.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }

    public static String formatRs(String amount) throws NumberFormatException {
        if (amount == null || amount.equals("null") || amount.equals("")) {
            return "0";
        } else {
            NumberFormat formatter = new DecimalFormat("##,##,##,###");
            String result = formatter.format(Double.parseDouble(amount));
            return result;
        }

    }

    public static void SingleChoice(Context activity, String title, final TextView txtView, final String[] arrList) {
        new android.app.AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(arrList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        txtView.setText(arrList[whichButton]);

                    }
                }).show();

    }

    public static String getProductNameById(String prod_id) {
        String result = "";
        switch (prod_id) {
            case "11":
                result = "Credit Card";
                break;
            case "22":
                result = "Car Loan";
                break;
            case "23":
                result = "Two Wheeler Loan";
                break;
            case "24":
                result = "Education Loan";
                break;
            case "25":
                result = "Personal Loan";
                break;
            case "26":
                result = "Home Loan";
                break;
            case "27":
                result = "LAS";
                break;
            case "28":
                result = "Loan Against Property";
                break;
            case "29":
                result = "CV";
                break;
            case "32":
                result = "SME";
                break;
            case "39":
                result = "Business Loan";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }


    public static int getImagebyLoanType(String loan_type) {
        int result = R.drawable.rupee;
        switch (loan_type) {
            case "11":
                //Credit Card
                result = R.drawable.cc;
                break;
            case "22":
                //CL
                result = R.drawable.cl;
                break;
            case "23":
                //TWL
                result = R.drawable.twl;
                break;
            case "24":
                //EL
                result = R.drawable.bl;
                break;
            case "25":
                //PL
                result = R.drawable.pl;
                break;
            case "26":
                //HL
                result = R.drawable.hl;
                break;
            case "27":
                //LAS
                result = R.drawable.bl;
                break;
            case "28":
                //LAP
                result = R.drawable.bl;
                break;
            case "29":
                //CV
                result = R.drawable.cv;
                break;
            case "32":
                //SME
                result = R.drawable.sme;
                break;
            case "39":
                //BL
                result = R.drawable.bl;
                break;
            default:
                //ANY Other Loan
                result = R.drawable.bl;
                break;
        }
        return result;
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

    public static String[] getOccupation() {
        String[] occupation = {"Housewife", "Pensioner", "Retired", "Self Employed", "Self Employed Professional", "Self Employed Professional Doctor", "Student"};
        return occupation;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getProductTypeURL(String this_product) {
        if (this_product.equals("11"))
            return "1";
        if (this_product.equals("25"))
            return "1";
        if (this_product.equals("26"))
            return "1";
        if (this_product.equals("28"))
            return "1";
        if (this_product.equals("22"))
            return "1";
        if (this_product.equals("23"))
            return "1";
        if (this_product.equals("39"))
            return "1";
        return "0";
    }

    public static String parseRs(String money) {
        try {
            NumberFormat formatter = new DecimalFormat("##,##,##,###");
            return formatter.format(Double.parseDouble(money));
        } catch (Exception e) {
            return "0";
        }
    }


    public static boolean isQqualifed(String qualified, String finbank, String finbank_qualified) {
        if (qualified.equals("true")) {
            Logs.LogD("Qual", "Qual Engine Qualified");
            if (finbank.equals("1")) {
                Logs.LogD("Qual", "Finbank Product");
                if (finbank_qualified.equals("true")) {
                    Logs.LogD("Qual", "Finbank Qualified");
                    return true;
                } else {
                    Logs.LogD("Qual", "Finbank NOT Qualified");
                    return false;
                }
            } else {
                Logs.LogD("Qual", "Not A Finbank Product");
                return true;
            }
        } else {
            Logs.LogD("Qual", "Qual Engine NOT Qualified");
            return false;
        }

    }

    public static void intializeCity(final Context context, final AutoCompleteTextView textView) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Logs.LogD("City", s.toString());
                if (s.length() == 3) {
                    String key = textView.getText().toString();
                    GetCity city = new GetCity(textView, context, key);
                    city.executeOnExecutor(threadPool);

                }

            }
        };
        textView.addTextChangedListener(watcher);
        textView.setThreshold(3);

    }

    public static class GetPayout extends AsyncTask<String, Void, String> {
        String be, af;
        String key1;
        ProgressBar pbar;
        private String type;
        private Context mContext;
        private String amount;
        private String activity;
        private TextView textview;

        public GetPayout(ProgressBar pbaar, String type, String amount, String activity, Context context, TextView text, String before, String after, String key) {
            this.activity = activity;
            this.amount = amount;
            this.type = type;
            this.mContext = context;
            this.textview = text;
            this.be = before;
            this.af = after;
            this.key1 = key;
            this.pbar = pbaar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                String url = CALCULATE_PAYOUT;
                HttpPost httpPost = new HttpPost(url);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("product_type", type);
                jsonObject.put("amount", amount);
                jsonObject.put("activity", activity);
                String json = "";
                json = new String(jsonObject.toString().getBytes("ISO-8859-1"), "UTF-8");
                Logs.LogD("request", json);
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + Util.isRegistered(mContext));
                Logs.LogD("utoken", Util.isRegistered(mContext));
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else
                    result = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }


        protected void onPostExecute(String result) {
            pbar.setVisibility(View.GONE);
            Logs.LogD("Result", result);
            try {
                JSONObject res = new JSONObject(result);
                String code = res.optString("status_code", "");
                if (code.equals("2000")) {
                    JSONObject body = res.getJSONObject("body");
                    String payout = body.optString(key1, "");
                    if (textview.getVisibility() == View.VISIBLE) {
                        textview.setText(be + parseRs(payout) + af);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public static class GetFullPayout extends AsyncTask<String, Void, String> {
        String be, af;
        String product;
        String key1;
        ProgressBar pbar;
        private String type;
        private Context mContext;
        private String amount;
        private TextView textview;

        public GetFullPayout(ProgressBar pbaar, String type, String amount, Context context, TextView text, String before, String after, String key, String product_id) {
            this.amount = amount;
            this.type = type;
            this.mContext = context;
            this.textview = text;
            this.be = before;
            this.af = after;
            this.key1 = key;
            this.product = product_id;
            this.pbar = pbaar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            InputStream inputStream1 = null;
            String payout_r = "0";
            String payout_f = "0";
            int payout = 0;
            String result2 = "", result1 = "", result = "0";
            String url;
            try {
                HttpClient httpclient = new DefaultHttpClient();

                if (type.equals("11")) {
                    url = CALCULATE_PAYOUT_PRODUCT;
                } else {
                    url = CALCULATE_PAYOUT;
                }
                HttpPost httpPost = new HttpPost(url);
                JSONObject jsonObject = new JSONObject();

                if (type.equals("11")) {
                    if (product.equals("0")) {
                        jsonObject.put("product_type", type);
                        httpPost = new HttpPost(CALCULATE_PAYOUT);
                    } else {
                        jsonObject.put("product_id", product);
                    }
                } else {
                    jsonObject.put("product_type", type);
                }
                jsonObject.put("amount", amount);
                jsonObject.put("activity", "r");
                String json = "";
                json = new String(jsonObject.toString().getBytes("ISO-8859-1"), "UTF-8");
                Logs.LogD("request", json);
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpPost.addHeader("Cookie", "utoken=" + Util.isRegistered(mContext));
                Logs.LogD("utoken", Util.isRegistered(mContext));
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result1 = Util.convertInputStreamToString(inputStream);
                } else
                    result1 = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            try {
                HttpClient httpclient2 = new DefaultHttpClient();
                if (type.equals("11")) {
                    url = CALCULATE_PAYOUT_PRODUCT;
                } else {
                    url = CALCULATE_PAYOUT;
                }
                JSONObject jsonObject2 = new JSONObject();
                HttpPost httpPost2 = new HttpPost(url);
                if (type.equals("11")) {
                    if (product.equals("0")) {
                        jsonObject2.put("product_type", type);
                        httpPost2 = new HttpPost(CALCULATE_PAYOUT);
                    } else {
                        jsonObject2.put("product_id", product);
                    }
                } else {
                    jsonObject2.put("product_type", type);
                }

                jsonObject2.put("product_type", type);
                jsonObject2.put("amount", amount);
                jsonObject2.put("activity", "f");
                String json = "";
                json = new String(jsonObject2.toString().getBytes("ISO-8859-1"), "UTF-8");
                Logs.LogD("request", json);
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost2.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost2.setHeader("Accept", "application/json");
                httpPost2.setHeader("Content-type", "application/json");

                httpPost2.addHeader("Cookie", "utoken=" + Util.isRegistered(mContext));
                Logs.LogD("utoken", Util.isRegistered(mContext));
                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient2.execute(httpPost2);

                // 9. receive response as inputStream
                inputStream1 = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream1 != null) {
                    result2 = Util.convertInputStreamToString(inputStream1);
                } else result2 = "Did not work!";
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }


            try {
                Logs.LogD("Result1", result1);
                JSONObject res = new JSONObject(result1);
                String code = res.optString("status_code", "");
                if (code.equals("2000")) {
                    JSONObject body = res.getJSONObject("body");
                    payout_r = body.optString(key1, "0");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Logs.LogD("Result2", result2);
                JSONObject res2 = new JSONObject(result2);
                String code = res2.optString("status_code", "");
                if (code.equals("2000")) {
                    JSONObject body = res2.getJSONObject("body");
                    payout_f = body.optString(key1, "0");
                    payout = (int) (Double.valueOf(payout_r) + Double.valueOf(payout_f));
                    result = String.valueOf(payout);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }


        protected void onPostExecute(String result) {
            pbar.setVisibility(View.GONE);
            Logs.LogD("Result", result);
            if (textview.getVisibility() == View.VISIBLE) {
                textview.setText(be + parseRs(result) + af);
            }
        }
    }

    static class GetCity extends AsyncTask<Void, Void, String> {
        AutoCompleteTextView text_refer_city;
        ArrayList<String> dataList;
        ArrayAdapter<String> adapter;
        String keyword;
        Context mcontext;

        public GetCity(AutoCompleteTextView textView, Context context, String key) {
            text_refer_city = textView;
            keyword = key;
            mcontext = context;
        }

        protected String doInBackground(Void... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(Util.ALLLISTS);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject post = new JSONObject();
                post.put("list_id", "10063");
                post.put("keyword", keyword);
                Logs.LogD("Request", post.toString());
                Logs.LogD("Refer", "Sent the Request");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(post.toString());
                writer.flush();
                writer.close();
                os.close();
                int HttpResult = conn.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    response = sb.toString();
                } else {
                    Logs.LogD("Exception", conn.getResponseMessage());
                }
            } catch (Exception e) {
                Logs.LogD("Task Exception" +
                        "on", e.getLocalizedMessage());
                e.printStackTrace();
                response = e.getLocalizedMessage();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            Logs.LogD("Cities", result);
            try {
                JSONObject data = new JSONObject(result);
                if (data.optString("status_code").equals("2000")) {
                    JSONArray body = data.optJSONArray("body");

                    dataList = new ArrayList<>();
                    for (int i = 0; i < body.length(); i++) {
                        JSONObject temp = body.getJSONObject(i);
                        dataList.add(temp.getString("itemValue"));
                    }
                    adapter = new ArrayAdapter(mcontext, R.layout.simpledropdown, dataList.toArray());
                    text_refer_city.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
