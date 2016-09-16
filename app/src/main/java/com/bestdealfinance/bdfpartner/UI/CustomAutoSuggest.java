package com.bestdealfinance.bdfpartner.UI;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.DataFieldModel;

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
import java.util.List;

/**
 * Created by vikas on 26/3/16.
 */
public class CustomAutoSuggest extends LinearLayout implements DataFieldModel {
    LinearLayout parentLayout;
    Context context;
    LayoutInflater layoutInflater;
    String title;
    String tag;
    View view;
    HashMap<String,String> hashMap=new HashMap<>();
    TextView titles;
    TextInputLayout child;
    AutoCompleteTextView editText;
    String payload, display_payload;
    final static String urlData=Util.ROOT_URL_V2+"List_controller/listAutoSuggestion";
    String key;
    ArrayAdapter adapter;
    List<String> dataList=new ArrayList<>();
    boolean mandate;
    String previous_value;
    String optionList;
    boolean hard;

    public CustomAutoSuggest(Context context, String id, String uiname, String dataURL, String lookey, boolean mandatory, String filledID, boolean hard) {
        super(context);
        this.context = context;
        this.previous_value=filledID;
        this.tag = id;
        this.hard=hard;
        this.title=uiname;
        this.optionList=dataURL;
        this.key=lookey;
        this.mandate=mandatory;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.customautosuggest, this, true);
        child = (TextInputLayout) findViewById(R.id.textinputlayout);
        editText = (AutoCompleteTextView) findViewById(R.id.editText);
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Logs.LogD("City",s.toString());
                if (s.length()==3) {
                    getdata();
                    GetData data = new GetData();
                    data.executeOnExecutor(Util.threadPool);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editText.addTextChangedListener(textWatcher);

        if (mandatory)
            child.setHint(title+"*");
        else
            child.setHint(title);


        adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,dataList);
        editText.setAdapter(adapter);
        editText.setThreshold(2);
        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearError();
                editText.setText((CharSequence) adapter.getItem(position));
            }
        });
        editText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearError();
                editText.setText((CharSequence) adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        if (QuotesGroupActivity.fieldValues!=null){
//            editText.setText(QuotesGroupActivity.fieldValues.get(tag));
//            previous_value=QuotesGroupActivity.fieldItemIds.get(tag);
//        }

    }

    @Override
    public String getdata() {
        payload=editText.getText().toString().trim();
        String listid=hashMap.get(payload);
        if (listid==null)
        return previous_value;
        else return listid;
    }

    @Override
    public String getDisplayData() {
        display_payload=editText.getText().toString().trim();
        return display_payload;
    }

    @Override
    public String getID() {
        return this.tag;
    }

    @Override
    public void onClickEvent() {

    }

    @Override
    public void addToView() {

    }

    @Override
    public void setError() {
        child.setErrorEnabled(true);
        child.setError("Enter "+title);
        child.requestFocus();
    }


    @Override
    public void setData(String data) {
        editText.setText(data);
    }

    @Override
    public void clearError() {
        child.setErrorEnabled(false);
        child.setError(null);
    }

    @Override
    public void hideFromView() {

    }

    @Override
    public String fieldType() {
        return null;
    }

    @Override
    public void showinView() {

    }

    @Override
    public boolean validate() {
        payload = editText.getText().toString().trim();
        if (mandate && Util.VALIDATE) {
            if (hard){
                if(hashMap.containsKey(payload)){
                    return true;
                }
                else return false;
            }
            return payload.length() > 2;
        }
        else return true;
    }

    @Override
    public void setTitle() {

    }

    @Override
    public void setHint() {

    }
    final class GetData extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(urlData);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject post=new JSONObject();
                post.put("list_id",optionList);
                post.put("keyword",payload);
                Logs.LogD("Request",post.toString());
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
            Logs.LogD("Cities",result);
            try {
               JSONObject data=new JSONObject(result);
                if (data.optString("status_code").equals("2000")) {
                    JSONArray body=data.optJSONArray("body");

                    dataList = new ArrayList<>();
                    for (int i = 0; i < body.length(); i++) {
                        JSONObject temp= body.getJSONObject(i);

                        dataList.add(temp.getString("itemValue"));
                        hashMap.put(temp.getString("itemValue"), temp.getString("id"));
                    }
                    adapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, dataList);
                    editText.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
