package com.bestdealfinance.bdfpartner.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.ReferralActivity;
import com.bestdealfinance.bdfpartner.adapter.ProductLandingAdapter;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.LandingModel;
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

public class Landing_CC extends Fragment {
    String product_type;
    String product_id;
    String product_url;
    String product_name;
    LinearLayout mainLayout;
    FrameLayout toolbar_layout;
    Button check_eligiblity;
    ImageView back;
    private ImageView progressBar;
    private AnimationDrawable animation;
    List<LandingModel> f_list = new ArrayList<>();
    List<LandingModel> e_list = new ArrayList<>();
    List<LandingModel> d_list = new ArrayList<>();
    List<LandingModel> c_list = new ArrayList<>();
    List<LandingModel> o_list = new ArrayList<>();
    ProductLandingAdapter featuresAdapter;
    ProductLandingAdapter eligiblityAdapter;
    ProductLandingAdapter documnetAdapter;
    ProductLandingAdapter offersAdapter;
    ProductLandingAdapter chargesAdapter;
    private JSONObject finalJson;
    private LinearLayoutManager mLinearLayoutManager;
    private View view;
    private Button refer_now;
    private ImageView headImage;
    private TextView time, landing_fees, landing_fees2, loan_type;
    private RecyclerView featuresView, eligiblityView, documentsView, chargesView, offersView;
    private boolean show_apply;

    public Landing_CC() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product_type = getArguments().getString("type");
            product_id = getArguments().getString("id");
            show_apply = getArguments().getBoolean("apply", true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_landing_cc, container, false);
        initializeViews();
        intializeRecyclerView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });


        DownloadJSON task = new DownloadJSON();
        task.executeOnExecutor(Util.threadPool);
        return view;
    }

    private void initializeViews() {
        if (show_apply) {
            refer_now = (Button) view.findViewById(R.id.refer_now);
            refer_now.setVisibility(View.VISIBLE);
            refer_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("product_type", "11");
                    bundle.putString("product_id", product_id);
                    bundle.putString("product_name", product_name);
                    bundle.putString("bank_logo", product_url);
                    Logs.LogD("bundleSendedr", bundle.toString());
                    Intent intent = new Intent(getActivity(), ReferralActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
        progressBar = (ImageView) view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        time = (TextView) view.findViewById(R.id.landing_time);
        mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        loan_type = (TextView) view.findViewById(R.id.landing_loan_type);
        headImage = (ImageView) view.findViewById(R.id.head_image);
        toolbar_layout = (FrameLayout) view.findViewById(R.id.toolbar_layout);
        back = (ImageView) view.findViewById(R.id.back_arrow);
        landing_fees = (TextView) view.findViewById(R.id.landing_fees);
        landing_fees2 = (TextView) view.findViewById(R.id.landing_fees2);
    }

    private void intializeRecyclerView() {
        featuresView = (RecyclerView) view.findViewById(R.id.features_list);
        featuresView.setHasFixedSize(true);
        featuresView.setLayoutManager(new LinearLayoutManager(getActivity()));
        featuresView.setAdapter(new ProductLandingAdapter(getActivity()));
        featuresView.setNestedScrollingEnabled(false);


        eligiblityView = (RecyclerView) view.findViewById(R.id.eligiblity_list);
        eligiblityView.setHasFixedSize(true);
        eligiblityView.setLayoutManager(new LinearLayoutManager(getActivity()));
        eligiblityView.setAdapter(new ProductLandingAdapter(getActivity()));
        eligiblityView.setNestedScrollingEnabled(false);


        documentsView = (RecyclerView) view.findViewById(R.id.document_list);
        documentsView.setHasFixedSize(true);
        documentsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        documentsView.setAdapter(new ProductLandingAdapter(getActivity()));
        documentsView.setNestedScrollingEnabled(false);

        chargesView = (RecyclerView) view.findViewById(R.id.charges_list);
        chargesView.setHasFixedSize(true);
        chargesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chargesView.setAdapter(new ProductLandingAdapter(getActivity()));
        chargesView.setNestedScrollingEnabled(false);


        offersView = (RecyclerView) view.findViewById(R.id.offers_list);
        offersView.setHasFixedSize(true);
        offersView.setLayoutManager(new LinearLayoutManager(getActivity()));
        offersView.setAdapter(new ProductLandingAdapter(getActivity()));
        offersView.setNestedScrollingEnabled(false);
    }

    private void doCalculations() throws JSONException {
        //TODO Features
        JSONObject product_info = finalJson.optJSONObject("product_info");
        JSONObject features = product_info.optJSONObject("f");
        if (features != null) {
            JSONArray info = features.optJSONArray("info_text");
            for (int i = 0; i < info.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info.getString(i);
                temp.setText(temp2);
                f_list.add(temp);
            }
            featuresAdapter = new ProductLandingAdapter(getActivity(), f_list, R.layout.itemlandinglayout);
        }

        //TODO Eligiblity

        JSONObject eligiblity = product_info.optJSONObject("e");
        if (eligiblity != null) {
            JSONArray info = eligiblity.optJSONArray("info_text");
            for (int i = 0; i < info.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info.getString(i);
                temp.setText(temp2);
                e_list.add(temp);
            }
            eligiblityAdapter = new ProductLandingAdapter(getActivity(), e_list, R.layout.itemlandinglayout);
        }
        //TODO Doc List

        JSONObject documents = product_info.optJSONObject("d");
        if (documents != null) {
            JSONArray info3 = documents.optJSONArray("info_text");
            for (int i = 0; i < info3.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info3.getString(i);
                temp.setText(temp2);
                d_list.add(temp);
            }
            documnetAdapter = new ProductLandingAdapter(getActivity(), d_list, R.layout.itemlandinglayout);
        }

        //TODO Charges

        JSONObject charges = product_info.optJSONObject("c");
        if (charges != null) {
            JSONArray info1 = charges.optJSONArray("info_text");
            for (int i = 0; i < info1.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info1.getString(i);
                temp.setText(temp2);
                c_list.add(temp);
            }
            chargesAdapter = new ProductLandingAdapter(getActivity(), c_list, R.layout.itemlandinglayout);
        }

        //TODO Offers
        JSONObject offers = product_info.optJSONObject("o");
        if (offers != null) {
//            Elements lines = Jsoup.parse(features).getElementsByTag("li");
            JSONArray info2 = offers.optJSONArray("info_text");
            for (int i = 0; i < info2.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info2.getString(i);
                temp.setText(temp2);
                o_list.add(temp);
            }
            offersAdapter = new ProductLandingAdapter(getActivity(), o_list, R.layout.itemlandinglayout);
        }
        mainLayout.setVisibility(View.VISIBLE);
        featuresView.setAdapter(featuresAdapter);
        documentsView.setAdapter(documnetAdapter);
        eligiblityView.setAdapter(eligiblityAdapter);
        chargesView.setAdapter(chargesAdapter);
        offersView.setAdapter(offersAdapter);

        String image_url = finalJson.optString("product_image_url");
        Glide.with(getActivity()).load(image_url).into(headImage);
        product_url = image_url;

        loan_type.setText(finalJson.optString("product_name"));
        product_name = finalJson.optString("product_name");
        String temp = finalJson.optString("product_mini_feature", "");
        String temp2 = temp.replace("$", "");
        Logs.LogD("temp2", temp2);
        time.setText(temp2);


        JSONObject annualfees = product_info.optJSONObject("annual_fees");
        if (annualfees != null) {
            JSONArray annual_fees = annualfees.optJSONArray("info_text");
            landing_fees.setText("Rs. " + annual_fees.getString(annual_fees.length() - 1));
            Logs.LogD("Fees", annual_fees.getString(0));
        }
        JSONObject ptime = product_info.optJSONObject("processing_time");
        if (ptime != null) {
            JSONArray p_time = ptime.optJSONArray("info_text");
            landing_fees2.setText(p_time.getString(0));
        }
        int color = Util.getBankColor(finalJson.optString("finbank_id"));
        toolbar_layout.setBackgroundColor(color);

    }


    final class DownloadJSON extends AsyncTask<Void, Void, String> {

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
                URL url = new URL(Util.PRODUCT_INFO_BYID);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject data = new JSONObject();
                data.put("product_id", product_id);
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

            // Logs.LogD("Response", response);
            return response;
        }

        protected void onPostExecute(String result) {
            animation.stop();
            progressBar.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.optString("status_code");
                if (status.equals("2000")) {
                    JSONObject body = jsonObject.optJSONObject("body");
                    finalJson = body;
                    doCalculations();
                } else {
                    throw new RuntimeException();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
