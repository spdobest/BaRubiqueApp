package com.bestdealfinance.bdfpartner.fragment;

import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.PhilosophyAdapter;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by disha on 24/2/16.
 */
public class AboutUsPhilosophy extends Fragment {

    String header;
    RecyclerView recyclerview_philosophy;
    RecyclerView.Adapter mAdapter;
    public String[] tag, content;
    RecyclerView.LayoutManager mLayoutManager;
    TextView philosophy_header;

    LinearLayout waiting_layout;
    ImageView progressBar;
    private AnimationDrawable animation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us_phillosophy, null);

        waiting_layout = (LinearLayout) view.findViewById(R.id.waiting_layout);
        progressBar = (ImageView) view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        philosophy_header = (TextView) view.findViewById(R.id.philosophy_header);
//        myDataset = new ArrayList<HashMap<String, String>>();
        recyclerview_philosophy = (RecyclerView) view.findViewById(R.id.recyclerview_philosophy);
        recyclerview_philosophy.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerview_philosophy.setLayoutManager(mLayoutManager);

        if (Util.isNetworkAvailable(getActivity())) {
            new HttpAsyncTask().execute();
        } else {
            if (!Helper.getStringSharedPreference(Constant.ABOUT_PHILOSOPY, getActivity()).equals("")) {
                parseData(Helper.getStringSharedPreference(Constant.ABOUT_PHILOSOPY, getContext()));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Message");
                builder.setCancelable(false);
                builder.setMessage("No data available");
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        }

        return view;
    }

    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waiting_layout.setVisibility(View.VISIBLE);
            animation.start();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(Util.ABOUT_PHILOSOPHY);

                urlConnection = (HttpURLConnection) url
                        .openConnection();

                InputStream is = urlConnection.getInputStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(is));

                String inputLine, result_ssl = new String();
                while ((inputLine = in.readLine()) != null) {
                    result_ssl += inputLine;
                }
                result = result_ssl;

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            waiting_layout.setVisibility(View.GONE);
            animation.stop();

            parseData(result);

        }
    }

    private void parseData(String result) {
        String status, msg, utoken;
        try {
            JSONObject output1 = new JSONObject(result);
            if (output1.opt("status_code") != null && output1.opt("msg") != null) {
                status = output1.getString("status_code");
                msg = output1.getString("msg");
                if (msg.equalsIgnoreCase("Success")) {
                    JSONObject jsonObject = new JSONObject(output1.getString("body"));
                    header = jsonObject.getString("heading");
                    philosophy_header.setText(header);
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("philosophy"));
                    tag = new String[jsonArray.length()];
                    content = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        tag[i] = jObj.getString("tag");
                        content[i] = jObj.getString("content");
                    }
                    mAdapter = new PhilosophyAdapter(tag, content);
                    recyclerview_philosophy.setAdapter(mAdapter);

                } else {
                    if (msg.equals("Failed")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Message");
                        builder.setCancelable(false);
                        builder.setMessage(output1.getString("body"));
                        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                }
            }
        } catch (JSONException e) {

        }
    }
}