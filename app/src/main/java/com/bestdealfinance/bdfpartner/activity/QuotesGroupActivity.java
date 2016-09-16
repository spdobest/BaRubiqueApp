package com.bestdealfinance.bdfpartner.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.fragment.Group_Question_Fragment;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class QuotesGroupActivity extends AppCompatActivity implements Group_Question_Fragment.OnGroupInteraction{
    private ImageView progressBar;
    private AnimationDrawable animation;
    Bundle bundle;
    public static HashMap<String,String> fieldValues=new HashMap<>();
    public static HashMap<String,String> fieldItemIds=new HashMap<>();


    String this_product_type;
    String this_product_id;
    String this_lead;
    private int current=-1;
    List<JSONArray>AllGroups=new ArrayList<>();
    List<String> thisgroup=new ArrayList<>();
    List<Fragment>AllFrags=new ArrayList<>();
    @Override
    protected void onRestart(){
        super.onRestart();
        Logs.LogD("Activity", "OnRestart");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new DefualtExceptionHandler());
        setContentView(R.layout.activity_quotes_group);
        fieldValues=new HashMap<>();
        bundle=getIntent().getExtras();
        this_product_type=bundle.getString("type", "");
        this_product_id=bundle.getString("product_id","");
        this_lead=bundle.getString("lead_id","");

        progressBar = (ImageView) findViewById(R.id.waiting);
        assert progressBar != null;
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        DownloadJSONForm getForm = new DownloadJSONForm();
        getForm.executeOnExecutor(Util.threadPool);

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Application Fill Full");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, Constant.FLURRY_API_KEY);

        Fabric.with(this, new Crashlytics());

    }


    public void replaceFragment_NOHISTORY(Fragment fragment) {
        Logs.LogD("Fragment", thisgroup.get(current));
        FragmentManager frgManager = getSupportFragmentManager();
        if (current==0){
            frgManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.group_fragment, fragment, thisgroup.get(current))
                    .commit();
        }
        else {
            frgManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.group_fragment, fragment, thisgroup.get(current))
                    .commit();
        }
    }

    public void changeFragment() throws JSONException {
        Logs.LogD("Quotes","Current= "+current+" Total = "+thisgroup.size());
        current++;
        if (current>=thisgroup.size()){
            callNextStep();
//            this.finish();
        }
        else {
            Logs.LogD("Fragment", "Current: " + current);
            bundle.putString("template", AllGroups.get(current).toString());
            bundle.putString("group_name", thisgroup.get(current));
            bundle.putString("lead_id",this_lead);
            bundle.putString("type",this_product_type);
            bundle.putString("product_id",this_product_id);
            Group_Question_Fragment fragment = new Group_Question_Fragment().newInstance(bundle);
            fragment.setArguments(bundle);
            replaceFragment_NOHISTORY(fragment);
            Logs.LogD("Fragment", "Current: " + current);
        }
    }
    public void changeFragmentNegetive() throws JSONException {
        Logs.LogD("Quotes","Current= "+current+" Total = "+thisgroup.size());
        if (current<0){
            this.finish();
        }
        else {
            Logs.LogD("Fragment", "Current: " + current);
            bundle.putString("template", AllGroups.get(current).toString());
            bundle.putString("group_name", thisgroup.get(current));
            bundle.putString("lead_id",this_lead);
            bundle.putString("type",this_product_type);
            bundle.putString("product_id",this_product_id);
            Group_Question_Fragment fragment = new Group_Question_Fragment().newInstance(bundle);
            fragment.setArguments(bundle);
            replaceFragment_NOHISTORY(fragment);
            Logs.LogD("Fragment", "Current: " + current);
        }
    }

    public void callNextStep(){
        Logs.LogD("QUAL","CALLING NEXT STEPS");
        if (Util.isRegistered(this).equals("")){
            //TODO ASK FOR LOGIN/REGISTER
            Intent myIntent =  new Intent(QuotesGroupActivity.this,LoginRegSinglePage.class);
            bundle.remove("template");
            myIntent.putExtras(bundle);
            startActivityForResult(myIntent, 2026);
        }
        else {
            //TODO Call SUBMIT APPLICATION API
            Logs.LogD("Activity","All fragments filled, User is logged in calling the submit API");
            Intent myIntent= new Intent(QuotesGroupActivity.this,AlternateOffersActivity.class);
            bundle.remove("template");
            myIntent.putExtras(bundle);
            startActivity(myIntent);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 2026) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //TODO Call SUBMIT APPLICATION API
                Logs.LogD("Activity","User is now Logged in Proceed to Submit App");
                Intent myIntent= new Intent(QuotesGroupActivity.this,AlternateOffersActivity.class);
                bundle.remove("template");
                myIntent.putExtras(bundle);
                startActivity(myIntent);
                finish();
            }
        }
    }

    @Override
    public void onChange(JSONObject revert) {
        Logs.LogD("REGLOG", "Interaction Received");
        try {
            jsonToBundle(revert);
            changeFragment();
        }
        catch (JSONException e){
            Logs.LogD("Exception", e.getLocalizedMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (current==0|| current<0){
            //fieldValues=null;
            finish();
        }
        else {
            current=current-1;
            try {
                bundle.putString("template", AllGroups.get(current).toString());
                bundle.putString("group_name",thisgroup.get(current));
//                FragmentManager fm = this.getSupportFragmentManager();
//                Fragment testtagFragment = fm.findFragmentByTag(thisgroup.get(current));
//                fm.beginTransaction()
//                        .replace(R.id.group_fragment, testtagFragment, thisgroup.get(current))
//                        .commit();
                changeFragmentNegetive();
                //fm.popBackStack();
            }
            catch (Exception e){
                Logs.LogD("Exception", e.getLocalizedMessage());
            }
        }
    }
    public void jsonToBundle(JSONObject jsonObject) throws JSONException {

        Iterator iter = jsonObject.keys();
        while(iter.hasNext()){
            String key = (String)iter.next();
            String value = jsonObject.getString(key);
            fieldValues.put(key,value);
        }
    }
    final class DownloadJSONForm extends AsyncTask<Void, Void, String> {
        private HttpURLConnection connection;
        private URL url;

        protected void onPreExecute() {
            //TODO Show the Waiting.
            progressBar.setVisibility(View.VISIBLE);
            animation.start();
        }

        protected String doInBackground(Void... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                String abc= Util.GETTEMPLATEPARTIAL;
                JSONObject data=new JSONObject();
                data.put("product_type_id",this_product_type);
                data.put("product_id", this_product_id);
                data.put("lead_id", this_lead);
                data.put("filter", "Full");
//                data.put("lead_id",this_lead);
                Logs.LogD("Request",data.toString());
                Logs.LogD("URL", abc);
                URL url = new URL(abc);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setRequestMethod("POST");
                if (Util.isRegistered(getApplicationContext()).equals("")){
                    conn.addRequestProperty("Cookie", "ltoken=" + Util.ltoken);
                    Logs.LogD("Header", "Utoken not availabale, Sending ltoken= "+Util.ltoken);
                }
                else {
                    conn.addRequestProperty("Cookie", "utoken=" + Util.isRegistered(getApplicationContext()));
                    Logs.LogD("Header", " Sending ltoken= " + Util.isRegistered(getApplicationContext()));
                }
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(data.toString());
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
                    //Logs.LogD("TAG", sb.toString());
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

            Logs.LogD("Response", response);
            return response;
        }

        protected void onPostExecute(String result) {
            animation.stop();
            progressBar.setVisibility(View.GONE);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String code = jsonObject.optString("status_code");
                if (code != null && code.equals("2000")) {
                    JSONObject body=jsonObject.optJSONObject("body");
                    JSONArray treevalues=body.optJSONArray("treeValues");
                    if (treevalues!=null){
                        Logs.LogD("TreeVales","Treevalue found");
                        for (int i=0;i<treevalues.length();i++){
                            JSONObject singleGroup=treevalues.getJSONObject(i);
                            fieldValues.put(singleGroup.optString("baseId"),singleGroup.optString("itemValue"));
                            fieldItemIds.put(singleGroup.optString("baseId"),singleGroup.optString("id"));
                        }
                    }
                    JSONArray resource = body.optJSONArray("tree");
                    if (resource != null) {
                        for (int i=0;i<resource.length();i++){

                            JSONObject singleGroup=resource.getJSONObject(i);
                                thisgroup.add(singleGroup.optString("groupName", ""));
                                AllGroups.add(singleGroup.optJSONArray("groupFields"));
                        }
                        changeFragment();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
