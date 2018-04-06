package com.bestdealfinance.bdfpartner.activity;

/**
 * Created by vikas on 20/4/16.
 */
public class NotificationUtil {


    /*public static final String splash = "splash";
    public static final String about_content = "about_content";
    public static final String about_philosophy = "about_philosophy";
    public static final String about_team = "about_team";
    GoogleCloudMessaging gcm;
    static final String TAG = "BDFGCM";
    SharedPreferences pref;
    SharedPreferences pref_reg;
    Activity activity;
    Context context;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String regid;
    public static final String PROPERTY_REG_ID = "registration_id";

    public NotificationUtil(Context context,Activity activity) {
        this.context = context;
        this.activity=activity;
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
    }

    public static SharedPreferences sharedpreferences;
    String str_username, str_utoken;
    static DBHelper controller;
    private String userid;


    private static String getRegistrationId(Context context) {
        final SharedPreferences prefs;
        prefs = context.getSharedPreferences("BDFREG2", 0);// 0 - for private mode

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration not found.");
            return "";
        }
        //  Log.d(TAG, "Registration fOUND WITH id. " + registrationId);
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    public static void sendRegistrationIdToBackend(final String registration_id, final Context context, final String userid) {
        final SharedPreferences pref_reg =context.getSharedPreferences("BDFREG2", 0);// 0 - for private mode;
        final SharedPreferences.Editor editor = pref_reg.edit();
        final  SharedPreferences pref = context.getSharedPreferences(Util.MY_PREFERENCES, 0);
        final int registeredVersion = pref_reg.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        Logs.LogD("Splash", "Sending Registration to BackEnd");
        String android_id = "";
        try
        {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
                android_id = mngr.getDeviceId();
            }
            else {
                android_id= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        catch (Exception e){
            Logs.LogD("Excepton",e.getLocalizedMessage());
            android_id= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            //Fuck You Marshmellon
        }


        final String finalAndroid_id = android_id;
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String response = "";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost;

                    httppost = new HttpPost(Util.ROOT_URL_V2+"notification/updateAppRegistration");

                try {
                    // Add your data
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("deviceid", finalAndroid_id);
                    jsonObject.put("regID",registration_id);
                    jsonObject.put("source",Logs.source);
                    jsonObject.put("app_version", registeredVersion);
                    jsonObject.put("userid", userid);
                    Logs.LogD("Request", jsonObject.toString());
                    StringEntity se = new StringEntity(jsonObject.toString());
                    // 6. set httpPost Entity
                    httppost.setEntity(se);
                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-type", "application/json");
                    httppost.addHeader("Cookie", "utoken=" + Util.isRegistered(context));

                    // Execute HTTP Post Request
                    HttpResponse result = httpclient.execute(httppost);
                    InputStream content = result.getEntity().getContent();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (ClientProtocolException e) {

                } catch (IOException e) {
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Logs.LogD("Backend REsponse", response);
                return response;
            }
        }.executeOnExecutor(Util.threadPool);
    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    public static void  registerInBackground(final String userid,final Context context) {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                GoogleCloudMessaging gcm;
                try {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    String regid = gcm.register(Util.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    Logs.LogD("Registration", msg);
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.

                    sendRegistrationIdToBackend(regid, context,userid);


                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

        }.executeOnExecutor(Util.threadPool);
    }

    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs =context.getSharedPreferences("BDFREG2", 0);// 0 - for private mode;
        int appVersion = getAppVersion(context);
        Logs.LogD(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }*/
}
