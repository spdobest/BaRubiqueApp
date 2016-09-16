package com.bestdealfinance.bdfpartner.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.FinalSorry;
import com.bestdealfinance.bdfpartner.adapter.CC_offers_adapter;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.FeedItemCC;

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
import java.util.List;


public class CC_Offers extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<FeedItemCC> feedsList;
    private SharedPreferences pref;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String UID;
    private String mParam2;
    private View view;
    private Bundle referBundle;
    private TextView banner;
    private RecyclerView mRecyclerView;
    private CC_offers_adapter adapter;
    private ProgressBar progressBar;
    private RelativeLayout offer_layout;
    private String payload;

    // TODO: Rename and change types and number of parameters


    public CC_Offers() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cc_offers, container, false);
        banner = (TextView) view.findViewById(R.id.text1);
        referBundle = getArguments();
        payload = referBundle.getString("data");
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        offer_layout = (RelativeLayout) view.findViewById(R.id.offer_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CallStep2 step2 = new CallStep2();
        step2.executeOnExecutor(Util.threadPool);
        return view;
    }

    private boolean parseResult(String result) {
        boolean hasoffer = false;
        try {
            JSONObject response = new JSONObject(result);
            JSONObject body = response.optJSONObject("body");
            JSONArray product_list = body.optJSONArray("product_list");
            feedsList = new ArrayList<>();
            if (product_list != null) {
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject post = product_list.optJSONObject(i);
                    if (post.optString("filled_eligibility_qual").equals("true")) {
                        hasoffer = true;
                        FeedItemCC item = new FeedItemCC();
                        item.setProduct_id(post.optString("product_id"));
                        item.setFilledQual("true");
                        if (post.optString("full_eligibility_qual").equals("true")) {
                            item.setFullQual("true");
                        } else {
                            item.setFullQual("false");
                        }
                        //Product Info

                        JSONObject product_info = post.optJSONObject("product_info");
                        if (product_info != null) {
                            item.setTitle(product_info.optString("product_name", ""));
                            item.setThumbnail(product_info.optString("img_url", ""));
                            item.setFinbank(product_info.optString("finbank_integrated", "0"));
                            item.setFinbank_type(product_info.optString("integration_type", "A"));

                        }

                        JSONObject disbursalfields = post.optJSONObject("disbursal_field_list");
                        JSONObject fees = disbursalfields.optJSONObject("183");
                        item.setFees(fees.optString("value"));
                        Logs.LogD("Item", "Title= " + item.getTitle());
                        feedsList.add(item);

                    } else {
                        Logs.LogD("Offers", "ineligible");
                        //hasOffer=false;
                    }

                }
            } else {
                hasoffer = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            hasoffer = false;

        }
        return hasoffer;

    }

    private class CallStep2 extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(Util.QUALIFY_PRODUCT);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                if (Util.isRegistered(getContext()).equals("")) {
                    conn.addRequestProperty("Cookie", "ltoken=" + Util.ltoken);
                    Logs.LogD("Header", "Utoken not availabale, Sending ltoken= " + Util.ltoken);
                } else {
                    conn.addRequestProperty("Cookie", "utoken=" + Util.isRegistered(getContext()));
                    Logs.LogD("Header", " Sending ltoken= " + Util.isRegistered(getContext()));
                }
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject data = new JSONObject();
                data.put("lead_id", referBundle.getString("lead_id", ""));

                Logs.LogD("Request", data.toString());
                Logs.LogD("Refer", "Sent the Request");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
                } else {
                    response = "false";
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

        @Override
        protected void onPostExecute(String result) {
            Logs.LogD("Response", result);
            progressBar.setVisibility(View.GONE);
            if (result.equals("false")) {
                Intent intent = new Intent(getActivity(), FinalSorry.class);
                intent.putExtras(referBundle);
                startActivity(intent);
                getActivity().finish();
                return;
            }
            try {
                JSONObject response = new JSONObject(result);
                String code = response.getString("status_code");
                if (code.equals("2000")) {
                    if (parseResult(result)) {
                        banner.setText("We have found " + feedsList.size() + " Credit Cards for you");
                        offer_layout.setVisibility(View.VISIBLE);
                        adapter = new CC_offers_adapter(getActivity(), feedsList, referBundle);
                        mRecyclerView.setAdapter(adapter);
                    } else {
                        Intent intent = new Intent(getActivity(), FinalSorry.class);
                        intent.putExtras(referBundle);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                } else if (code.equals("600")) {
                    //No records.
                } else if (code.equals("700")) {
                    //Error
                } else {
                    //Other Error
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}