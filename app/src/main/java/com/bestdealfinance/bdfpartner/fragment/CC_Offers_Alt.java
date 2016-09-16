package com.bestdealfinance.bdfpartner.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.ApplicationCongrats;
import com.bestdealfinance.bdfpartner.activity.FinalSorry;
import com.bestdealfinance.bdfpartner.adapter.CC_Alt_Adapter;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.FeedItemCC;
import com.bumptech.glide.Glide;

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


public class CC_Offers_Alt extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharedPreferences pref;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String UID;
    private String mParam2;
    private View view;

    private TextView header_text,lost_amount,lost_emi,lost_tenure,sorry_text, lost_name;
    private RecyclerView mRecyclerView;
    private CC_Alt_Adapter adapter;
    private List<FeedItemCC> feedsList;
    private ImageView lost_image;
    private ImageView progressBar;
    private RelativeLayout  failed_layout, waiting_layout;
    private LinearLayout offer_layout;
    private static String type;
    private String payload;
    private final String loan_type="11";
    Bundle referBundle;
    private AnimationDrawable animation;
    String finbank;

    // TODO: Rename and change types and number of parameters


    public CC_Offers_Alt() {
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
        view=inflater.inflate(R.layout.fragment_cc__offers__alt, container, false);
        referBundle = getArguments();
        payload=referBundle.getString("data");
        type=referBundle.getString("type");
        finbank=referBundle.getString("finbank");
        header_text = (TextView) view.findViewById(R.id.header_text);
        sorry_text= (TextView) view.findViewById(R.id.sorry_text);
        lost_image= (ImageView) view.findViewById(R.id.lost_offer);
        sorry_text.setText(Html.fromHtml(getResources().getString(R.string.sorry_eligiblity)+" "+referBundle.getString("product_name","")));
        lost_amount= (TextView) view.findViewById(R.id.lost_fees);
        lost_amount.setText(referBundle.getString("fees", "-"));
        Glide.with(getContext()).load(referBundle.getString("bank_logo")).into(lost_image);
        lost_name= (TextView) view.findViewById(R.id.lost_cc_name);
        lost_name.setText(Html.fromHtml(referBundle.getString("product_name","")));
        Logs.LogD("Bundle",referBundle.toString());
        UID = Util.isRegistered(getActivity());
        offer_layout = (LinearLayout) view.findViewById(R.id.offer_layout);
        failed_layout = (RelativeLayout) view.findViewById(R.id.failed_layout);
        waiting_layout = (RelativeLayout) view.findViewById(R.id.waiting_layout);
        progressBar = (ImageView) view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CallStep2 step2=new CallStep2();
        step2.executeOnExecutor(Util.threadPool);
        return view;
    }

    private class CallStep2 extends AsyncTask<String, Void, String > {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            animation.start();
        }
        protected String doInBackground(String... params) {
            String response = "";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(Util.SUBMITAPPLICATIONBA);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.addRequestProperty("Cookie", "utoken=" + Util.isRegistered(getContext()));
//                conn.addRequestProperty("Cookie", "ltoken=" + Util.ltoken);
                conn.setDoOutput(true);
                JSONObject data=new JSONObject();
                data.put("lead_id", referBundle.getString("lead_id", ""));
                data.put("product_type_sought", referBundle.getString("type", "11"));
                data.put("product_id", referBundle.getString("product_id", ""));

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
                    response="false";
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
            Logs.LogD("Response",result);
            waiting_layout.setVisibility(View.GONE);
            animation.stop();
            if (result.equals("false")){
                Intent intent=new Intent(getActivity(), FinalSorry.class);
                intent.putExtras(referBundle);
                startActivity(intent);
                getActivity().finish();
                return;
            }
            try {
                JSONObject response = new JSONObject(result);
                String code = response.getString("status_code");
                if (code.equals("2000")) {
                    JSONObject body=response.optJSONObject("body");
                    String qualified=body.optString("product_qualified", "false");
                    String finbank_qualifed=body.optString("in_prin_approved","false");
                    if (Util.isQqualifed(qualified, finbank, finbank_qualifed)) {
                        Intent intent=new Intent(getActivity(), ApplicationCongrats.class);
                        referBundle.putString("message",body.optString("message_to_display",""));
                        intent.putExtras(referBundle);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                    parseResult(result);
                    if (feedsList!=null && feedsList.size()>0) {
                        offer_layout.setVisibility(View.VISIBLE);
                        adapter = new CC_Alt_Adapter(getActivity(), feedsList,  referBundle);
                        mRecyclerView.setAdapter(adapter);
                    } else {
                        Logs.LogD("Sorry","No Alternative recommendation as well ass user not qualifed");
                        //TODO. No Offers Found
                        Intent intent=new Intent(getActivity(), FinalSorry.class);
                        referBundle.putString("message",body.optString("message_to_display",""));
                        intent.putExtras(referBundle);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                } else if (code.equals("4002")) {
                    Intent intent=new Intent(getActivity(), FinalSorry.class);

                    intent.putExtras(referBundle);
                    startActivity(intent);
                    getActivity().finish();
                } else if (code.equals("700")) {
                    //Error
                    failed_layout.setVisibility(View.VISIBLE);
                } else {
                    //Other Error
                    failed_layout.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Logs.LogD("Sorry","No Alternative recommendation as well ass user not qualifed");
                Intent intent=new Intent(getActivity(), FinalSorry.class);
                intent.putExtras(referBundle);
                startActivity(intent);
                getActivity().finish();
                return;
            }
        }

    }
    private void parseResult(String result) {
        boolean hasoffer=false;
        try {
            JSONObject response = new JSONObject(result);
            JSONObject body = response.optJSONObject("body");
            String qualified=body.optString("product_qualified", "");
            JSONArray product_list=body.optJSONArray("more_recommendations");
            if (product_list==null){
                Intent intent=new Intent(getActivity(), FinalSorry.class);
                intent.putExtras(referBundle);
                startActivity(intent);
                getActivity().finish();
            }
            feedsList = new ArrayList<>();
            if (product_list!=null) {
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject post = product_list.optJSONObject(i);
                    if (post.optString("filled_eligibility_qual").equals("true")) {

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
                        feedsList.add(item);
                        Logs.LogD("Item", "Title= " + item.getTitle() + "Finbank");
                    } else {
                        Logs.LogD("Offers", "ineligible");
                        //hasOffer=false;
                    }

                }
            }
            else {
                hasoffer=false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            hasoffer=false;
        }
    }


}