package com.bestdealfinance.bdfpartner.FragmentNew;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.ActivityNew.LeadFlowActivityNew;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.ProductLandingAdapter;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.LandingModel;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreditCardDetailsFragment extends Fragment {
    String product_type;
    String product_id;
    String product_url;
    String product_name;
    LinearLayout mainLayout;
    FrameLayout toolbar_layout;
    ImageView back;
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
    private ImageView progressBar;
    private AnimationDrawable animation;
    private JSONObject finalJson;
    private View view;
    private Button refer_now;
    private ImageView headImage;
    private TextView time, landing_fees, landing_fees2, loan_type;
    private RecyclerView featuresView, eligiblityView, documentsView, chargesView, offersView;
    private boolean show_apply;
    private RequestQueue queue;

    public CreditCardDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product_type = getArguments().getString("type");
            product_name = getArguments().getString("name");
            product_id = getArguments().getString("id");
            show_apply = getArguments().getBoolean("apply", true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_landing_cc, container, false);
        queue = Volley.newRequestQueue(getActivity());
        initializeViews();
        initializeRecyclerView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        animation.start();
        getAllSubProductsFromServer();
        return view;
    }

    private void initializeViews() {
        if (show_apply) {
            refer_now = (Button) view.findViewById(R.id.refer_now);
            refer_now.setVisibility(View.VISIBLE);
            refer_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Bundle bundle = new Bundle();
                    bundle.putString("product_type", "11");
                    bundle.putString("product_id", product_id);
                    bundle.putString("product_name", product_name);
                    bundle.putString("bank_logo", product_url);
                    Logs.LogD("bundleSendedr", bundle.toString());
                    Intent intent = new Intent(getActivity(), ReferralActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);*/

                    Bundle bundle = new Bundle();
                    bundle.putString("product_type_sought", product_type);
                    bundle.putString("product_type_name", product_name);
                    bundle.putString("product_id", product_id);
                    bundle.putString("incomplete", "0");
                    getActivity().startActivity(new Intent(getActivity(), LeadFlowActivityNew.class).putExtra("bundle", bundle));


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

    private void initializeRecyclerView() {
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
            featuresAdapter = new ProductLandingAdapter(getActivity(), f_list, R.layout.list_item_landinglayout);
        }


        JSONObject eligiblity = product_info.optJSONObject("e");
        if (eligiblity != null) {
            JSONArray info = eligiblity.optJSONArray("info_text");
            for (int i = 0; i < info.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info.getString(i);
                temp.setText(temp2);
                e_list.add(temp);
            }
            eligiblityAdapter = new ProductLandingAdapter(getActivity(), e_list, R.layout.list_item_landinglayout);
        }
        JSONObject documents = product_info.optJSONObject("d");
        if (documents != null) {
            JSONArray info3 = documents.optJSONArray("info_text");
            for (int i = 0; i < info3.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info3.getString(i);
                temp.setText(temp2);
                d_list.add(temp);
            }
            documnetAdapter = new ProductLandingAdapter(getActivity(), d_list, R.layout.list_item_landinglayout);
        }

        JSONObject charges = product_info.optJSONObject("c");
        if (charges != null) {
            JSONArray info1 = charges.optJSONArray("info_text");
            for (int i = 0; i < info1.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info1.getString(i);
                temp.setText(temp2);
                c_list.add(temp);
            }
            chargesAdapter = new ProductLandingAdapter(getActivity(), c_list, R.layout.list_item_landinglayout);
        }

        JSONObject offers = product_info.optJSONObject("o");
        if (offers != null) {
            JSONArray info2 = offers.optJSONArray("info_text");
            for (int i = 0; i < info2.length(); i++) {
                LandingModel temp = new LandingModel();
                String temp2 = info2.getString(i);
                temp.setText(temp2);
                o_list.add(temp);
            }
            offersAdapter = new ProductLandingAdapter(getActivity(), o_list, R.layout.list_item_landinglayout);
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
        time.setText(temp2);


        JSONObject annualfees = product_info.optJSONObject("annual_fees");
        if (annualfees != null) {
            JSONArray annual_fees = annualfees.optJSONArray("info_text");
            landing_fees.setText("Rs. " + annual_fees.getString(annual_fees.length() - 1));
        }
        JSONObject ptime = product_info.optJSONObject("processing_time");
        if (ptime != null) {
            JSONArray p_time = ptime.optJSONArray("info_text");
            landing_fees2.setText(p_time.getString(0));
        }
        int color = Util.getBankColor(finalJson.optString("finbank_id"));
        toolbar_layout.setBackgroundColor(color);

    }

    private void getAllSubProductsFromServer() {
        JSONObject request = new JSONObject();
        try {
            request.put("product_id", product_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest getSubProductsRequest = new JsonObjectRequest(Request.Method.POST, URL.PRODUCT_INFO_BY_ID, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.optString("status_code").equals("2000")) {
                        animation.stop();
                        progressBar.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                        try {
                            JSONObject body = response.optJSONObject("body");
                            finalJson = body;
                            doCalculations();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(getSubProductsRequest);
    }
}