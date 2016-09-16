package com.bestdealfinance.bdfpartner.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.database.DBHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by vikas on 15/3/16.
 */
public class APIUtils  {

    static DBHelper controller;



    public interface OnTaskCompletedProducts{
        void OnTaskCompletedProducts();
    }
    public interface OnTaskCompletedCity{
        void OnTaskCompletedCity();
    }
    public static class GetCity extends AsyncTask<String, Void, String> {
        private OnTaskCompletedCity listnerc;
        private String payload;

        public GetCity(OnTaskCompletedCity listnerc) {
            this.listnerc = listnerc;
        }
        public GetCity() {
        }
        @Override
        protected String doInBackground(String... params) {
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
                JSONObject post=new JSONObject();
                post.put("list_id","10063");
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
            ArrayList cities = new ArrayList<String>();
            try {
                JSONObject jsonObj = new JSONObject(result);
                if (jsonObj.getString("status_code") != null) {
                    String status = jsonObj.getString("status_code");
                    if (status.equals("2000")) {
                        JSONArray array = jsonObj.getJSONArray("body");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject temp = (JSONObject) array.get(i);
                                cities.add(temp.getString("item_value"));
                            }
                            Util.city_names = cities;
                            if (listnerc!=null) {
                                listnerc.OnTaskCompletedCity();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //  Log.d("Cities", result);
        }


    }
    public static class ProductTypeAsyncTask extends AsyncTask<Void, Void, String> {
        private OnTaskCompletedProducts listnerp;

        public ProductTypeAsyncTask(OnTaskCompletedProducts listnerp) {
            this.listnerp = listnerp;
        }
        public ProductTypeAsyncTask() {
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream inputStream = null;
            String result = "", result1 = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Util.PRODUCT_TYPE);

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                HttpResponse httpResponse = httpclient.execute(httpPost);

                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    result = Util.convertInputStreamToString(inputStream);
                } else
                    result = "Did not work!";


            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String status, msg, utoken;
            try {
                JSONObject output1 = new JSONObject(result);
                if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                    status = output1.getString("status_code");
                    msg = output1.getString("msg");

                    if (status.equals("2000") && msg.equals("Success")) {
                        JSONArray jsonArray = new JSONArray(output1.getString("body"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObj = jsonArray.getJSONObject(i);
                            controller.insertProductType(jObj.getString("id"), jObj.getString("name"));
                        }


                        Util.product_type_data = controller.getAllProductName();
                        if (listnerp!=null){
                            listnerp.OnTaskCompletedProducts();
                        }

                    } else {

                    }
                }
            } catch (JSONException e) {

            }

        }

    }

    public static class SendSMS extends AsyncTask<String, Void, String> {
        private String code1;
        private Context mContext;
        private String name1;
        private String id1;
        private String mob1;
        private TextView textview;
        private String product1;
        String be,af;
        String key1;

        public SendSMS(String code, String id, String name ,Context context,String mobile, String product) {
            this.mContext=context;
            this.code1=code;
            this.mob1=mobile;
            this.name1=name;
            this.id1=id;
            this.product1=product;
            this.key1=code;
        }




        @Override
        protected String doInBackground(String... params) {
            Logs.LogD("City", "Calling City API");
            String response = "";
            try {
                HttpClient client = new DefaultHttpClient();
//                HttpGet request = new HttpGet();
                String url=Util.ROOT_URL_FI+"dpr-bank/sms/send/mobile/msg?";
                url=url+"msgId="+code1;
                url=url+"&mobileNo="+mob1;
                url=url+"&variables=";
                String url1="";
                if (code1.equals("2")){
                    Logs.LogD("Code","Code2");
                    url1=url1+name1+";";
                    url1=url1+product1+";";
                    url1=url1+id1;
                }
                if (code1.equals("3")){
                    Logs.LogD("Code","Code3");
                    url1=url1+name1+";";
                    url1=url1+product1+";";
                    url1=url1+id1;
                }
                if (code1.equals("5")){
                    Logs.LogD("Code","Code3");
                    url1=url1+name1+";";
                    url1=url1+product1+";";
                    url1=url1+id1;
                }
                if (code1.equals("8")){
                    url1=url1+name1;
                }
                url1=URLEncoder.encode(url1,"UTF-8");
                url=url+url1;
                Logs.LogD("Request", url);
                HttpGet request = new HttpGet(url);
//                request.setURI(new URI(value));
                HttpResponse execute = client.execute(request);
                InputStream content = execute.getEntity().getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
            Logs.LogD("Exception",e.getLocalizedMessage());
            }
            Logs.LogD("City", "Complete City Load");
            return response;
        }

        protected void onPostExecute(String result) {
            Logs.LogD("Result", result);

        }
    }


}
