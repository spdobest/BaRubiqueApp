package com.bestdealfinance.bdfpartner.UI;

/**
 * Created by vikas on 7/4/16.
 */

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
import android.widget.ProgressBar;
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
 * Created by vikas on 25/3/16.
 */
public class CustomDropdownModelView extends LinearLayout implements DataFieldModel {
    private LinearLayout parentLayout;
    private Context context;
    private LayoutInflater layoutInflater;
    private String  title;
    private String tag;
    private View view;
    private TextView titles, error;
    private TextInputLayout child;

    private String payload,display_payload;
    private AutoCompleteTextView editText;
    private String key;
    private String previous_value;
    private ProgressBar progressBar;
    private List<String> fieldOptions;
    private List<String> fieldValues;
    private int selected_position;
    private List<String> dataList=new ArrayList<>();
    private ArrayAdapter adapter;
    private String URL;
    private HashMap<String,String> hashMap=new HashMap<>();
    private String usedmap;
    private boolean mandatory;

    public CustomDropdownModelView(final Context context, final String id, final String title, final String dataUrl, final String lookup, final String usemap,String filledID, boolean mandate) {
        super(context);
        this.context = context;
        this.tag=id;
        this.previous_value=filledID;
        this.URL=Util.ROOT_URL_V2+"list_controller/mapAutoSuggestion";
        this.title=title;
        this.key=lookup;
        this.fieldOptions=new ArrayList<>();
        this.fieldValues=new ArrayList<>();
        this.usedmap=usemap;
        this.mandatory=mandate;
        this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.custom_text_model_view, this, true);
        child= (TextInputLayout) findViewById(R.id.textinputlayout);
        editText = (AutoCompleteTextView) findViewById(R.id.editText);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
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

//        adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,dataList);
//        editText.setAdapter(adapter);
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
    }

    @Override
    public void setVisibility(int vis){
        super.setVisibility(vis);
        if (vis==VISIBLE){
//                    GetData data=new GetData();
//                    data.executeOnExecutor(Util.threadPool);
        }
    }
    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        Logs.LogD("ENGMODEL", "ATTACHED");
//        if (Util.associativeHash.get(usedmap)!=null){
//            GetData data=new GetData();
//            data.executeOnExecutor(Util.threadPool);
//        }

    }
    @Override
    public boolean validate() {
        if (Util.VALIDATE && mandatory) {
            Logs.LogD("TAG",tag);
            payload = editText.getText().toString().trim();
            Logs.LogD("Paylod",payload);
            if(hashMap.containsKey(payload)){
                Logs.LogD("Return","true");
                return true;
            }
            else{
                Logs.LogD("Return","false");
                return false;
            }
        }
        else return true;
    }
    @Override
    public void setData(String data) {
        editText.setText(data);
    }

    @Override
    public void setError() {
        child.setErrorEnabled(true);
        child.setError("Enter "+title);
        child.requestFocus();
    }

    @Override
    public void clearError() {
        child.setErrorEnabled(false);
        child.setError(null);
    }
    @Override
    public String getID() {
        return this.tag;
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
    public void onClickEvent() {

    }

    @Override
    public void addToView() {
        parentLayout.addView(view);
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
    public void setTitle() {
        titles.setText(title);
    }

    @Override
    public void setHint() {

    }
    final class GetData extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
           progressBar.setVisibility(VISIBLE);
        }
        protected String doInBackground(Void... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject post=new JSONObject();
                post.put("map_id",usedmap);
                post.put("source_id",Util.associativeHash.get(usedmap));
                post.put("keyword",editText.getText().toString());
                Logs.LogD("ENQ_MODEL",post.toString());
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
            progressBar.setVisibility(GONE);
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
