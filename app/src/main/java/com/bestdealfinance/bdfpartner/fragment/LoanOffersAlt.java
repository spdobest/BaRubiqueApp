package com.bestdealfinance.bdfpartner.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bestdealfinance.bdfpartner.adapter.Loan_Alt_Adapter;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.FeedItemLoan;
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

public class LoanOffersAlt extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharedPreferences pref;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String UID;
    private String mParam2, loan_type;
    private View view;
    private TextView header_text,lost_amount,lost_emi,lost_tenure,sorry_text;
    private RecyclerView mRecyclerView;
    private Loan_Alt_Adapter adapter;
    private List<FeedItemLoan> feedsList;
    private ImageView lost_image;
    private ImageView progressBar;
    private RelativeLayout  failed_layout, waiting_layout;
    private LinearLayout offer_layout;
    private static String type;
    private String payload;
    Bundle referBundle;
    String finbank;

    private AnimationDrawable animation;

    // TODO: Rename and change types and number of parameters
    public static LoanOffersAlt newInstance(String param1, String param2) {
        LoanOffersAlt fragment = new LoanOffersAlt();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LoanOffersAlt() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logs.LogD("First", "Destroying View");
        animation.stop();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_loan_offers_alt, container, false);
        referBundle = getArguments();
        payload=referBundle.getString("data");
        type=referBundle.getString("type");
        loan_type=type;
        finbank=referBundle.getString("finbank");
        header_text = (TextView) view.findViewById(R.id.header_text);
        sorry_text= (TextView) view.findViewById(R.id.sorry_text);
        lost_image= (ImageView) view.findViewById(R.id.lost_offer);
        sorry_text.setText(getResources().getString(R.string.sorry_eligiblity)+" "+referBundle.getString("product_name",""));
        lost_amount= (TextView) view.findViewById(R.id.lost_fees);
        lost_amount.setText(Util.parseRs(referBundle.getString("amount","0")));
        lost_emi= (TextView) view.findViewById(R.id.lost_emi);
        lost_emi.setText(referBundle.getString("emi","-"));
        lost_tenure= (TextView) view.findViewById(R.id.lost_tenure);
        lost_tenure.setText(referBundle.getString("tenure","-"));
        Glide.with(getContext()).load(referBundle.getString("bank_logo")).into(lost_image);
        Logs.LogD("Bundle",referBundle.toString());
        UID = Util.isRegistered(getActivity());
        offer_layout = (LinearLayout) view.findViewById(R.id.offer_layout);
        failed_layout = (RelativeLayout) view.findViewById(R.id.failed_layout);
        waiting_layout = (RelativeLayout) view.findViewById(R.id.waiting_layout);
        progressBar = (ImageView) view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        animation.start();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CallStep2 step2=new CallStep2();
        step2.executeOnExecutor(Util.threadPool);
        return view;
    }

    public class CallStep2 extends AsyncTask<String, Void, String > {

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
                URL url = new URL(Util.SUBMITAPPLICATIONBA);
                Logs.LogD("URL",url.toString());
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
                data.put("product_type_sought", referBundle.getString("type", ""));
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
                Logs.LogD("Task Exception" +"on", e.getLocalizedMessage());
                e.printStackTrace();
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
                    if (feedsList.size()>0) {
                        offer_layout.setVisibility(View.VISIBLE);
                        adapter = new Loan_Alt_Adapter(getActivity(), feedsList, loan_type, referBundle);
                        mRecyclerView.setAdapter(adapter);
                    } else {

                        //TODO. No Offers Found
                        Logs.LogD("Sorry","No Alternative recommendation as well ass user not qualifed");
                        Intent intent=new Intent(getActivity(), FinalSorry.class);
                        referBundle.putString("message",body.optString("message_to_display",""));
                        intent.putExtras(referBundle);
                        startActivity(intent);
                        getActivity().finish();
                        return;
//                        failed_layout.setVisibility(View.VISIBLE);
                    }
                } else if (code.equals("4002")) {
                    Intent intent=new Intent(getActivity(), FinalSorry.class);
                    intent.putExtras(referBundle);
                    startActivity(intent);
                    getActivity().finish();
                } else if (code.equals("700")) {
                    Intent intent=new Intent(getActivity(), FinalSorry.class);
                    intent.putExtras(referBundle);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    //Other Error
                    failed_layout.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //TODO. No Offers Found
                Logs.LogD("Sorry","No Alternative recommendation as well ass user not qualifed");
                Intent intent=new Intent(getActivity(), FinalSorry.class);
                intent.putExtras(referBundle);
                startActivity(intent);
                getActivity().finish();
                return;
            }
        }

        private void parseResult(String result) {
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
                for (int i = 0; i < product_list.length(); i++) {
                    JSONObject post = product_list.optJSONObject(i);
                    if (post.optString("filled_eligibility_qual").equals("true")) {
                        FeedItemLoan item = new FeedItemLoan();
                        item.setProduct_id(post.optString("product_id"));
                        item.setFilledQual("true");
                        if (post.optString("full_eligibility_qual").equals("true")){
                            item.setFullQual("true");
                        }
                        else {
                            item.setFullQual("false");
                        }
                        //Product Info

                        JSONObject product_info=post.optJSONObject("product_info");
                        if (product_info!=null) {
                            item.setTitle_bank(product_info.optString("product_name", ""));
                            item.setBank_logo(product_info.optString("img_url", ""));
                            item.setFinbank(product_info.optString("finbank_integrated", "0"));
                            item.setFinbank_type(product_info.optString("integration_type","A"));
                        }

                        JSONObject disbursalfields=post.optJSONObject("disbursal_field_list");

                        JSONObject roi=disbursalfields.optJSONObject("165");
                        item.setRoi(roi.optString("value"));

                        JSONObject pf=disbursalfields.optJSONObject("166");
                        item.setPf(pf.optString("value"));

                        JSONObject amount=disbursalfields.optJSONObject("198");
                        item.setEligible_amount(amount.optString("value"));
                        JSONObject emi=disbursalfields.optJSONObject("167");
                        item.setEmi(emi.optString("value"));

                        JSONObject tenure=disbursalfields.optJSONObject("116");
                        if (tenure!=null) {
                            item.setTenure(tenure.optString("value"));
                        }
                        feedsList.add(item);
                        Logs.LogD("Feed", "PF" + item.getPf() + " roi=" + item.getEmi() + " bank=" + item.getTitle_bank()+" url "+item.getBank_logo());
                    } else {
                        Logs.LogD("Offers", "ineligible");
                        //hasOffer=false;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




}
