package com.bestdealfinance.bdfpartner.FragmentNew;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.NonOfferListAdapter;
import com.bestdealfinance.bdfpartner.AdapterNew.OfferListAdapter;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfferListFragment extends Fragment {


    private RequestQueue queue;
    private Bundle productBundle;
    private RecyclerView offerList;
    private Button btnMoreOffers;
    private OfferListAdapter listAdapter;
    private JSONArray qualifiedProduct;
    private JSONArray nonQualifiedProduct;
    private RelativeLayout offerLayout;
    private LinearLayout forceLayout;
    private ImageButton btnCloseLayout;
    private RecyclerView nonOfferList;
    private NonOfferListAdapter nonOfferAdapter;
    private RelativeLayout noOfferLayout;
    private HashSet<String> hashSet;
    private AppCompatImageView referIcon;
    private AppCompatImageView appFillIcon;
    private AppCompatImageView docPickupIcon;
    private AppCompatImageView disburseIcon;
    private TextView referAmount;
    private TextView appFillAmount;
    private TextView docPickupAmount;
    private TextView disAmount;
    private DB snappyDB;
    private JSONArray allPayoutJsonArray;
    private JSONArray allStepsJsonArray;
    private JSONArray maxPayoutInEachProductTypeJsonArray;

    // widgets
    private TextView textViewOfferMessage1;
    private TextView textViewOfferMessage2;

    public OfferListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_offer_list, container, false);
        queue = Volley.newRequestQueue(getActivity());
        productBundle = getArguments();

        //For testing
        //productBundle.putString("lead_id", "8405");
        //productBundle.putString("stage", "3");
        //

        hashSet = new HashSet<String>();

        Helper.hideKeyboard(getActivity());
        qualifiedProduct = new JSONArray();
        nonQualifiedProduct = new JSONArray();
        initialize(rootView);
        fetchAllProducts();
        setBubbleLayout(rootView);
        forceLayout.setTranslationY(2500F);

        return rootView;
    }

    private void setBubbleLayout(View rootView) {

        referIcon = (AppCompatImageView) rootView.findViewById(R.id.refer_bubbble);
        appFillIcon = (AppCompatImageView) rootView.findViewById(R.id.app_fill_bubble);
        docPickupIcon = (AppCompatImageView) rootView.findViewById(R.id.doc_pickup_bubble);
        disburseIcon = (AppCompatImageView) rootView.findViewById(R.id.dis_bubble);

        referAmount = (TextView) rootView.findViewById(R.id.refer_bubbble_amount);
        appFillAmount = (TextView) rootView.findViewById(R.id.app_fill_bubble_amount);
        docPickupAmount = (TextView) rootView.findViewById(R.id.doc_pickup_bubble_amount);
        disAmount = (TextView) rootView.findViewById(R.id.dis_bubble_amount);

        appFillIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Orange300)));
        appFillAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Orange300));
      /*  docPickupIcon.setSupportBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Orange300)));
        docPickupAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Orange300));*/

        try {
            snappyDB = DBFactory.open(getActivity());
            allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
            allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
            maxPayoutInEachProductTypeJsonArray = getMaxInPayoutArray();

            snappyDB.close();
            double payout = 0;
            JSONObject stepObject = null;

            for (int i = 0; i < maxPayoutInEachProductTypeJsonArray.length(); i++) {
                if (maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                    payout = maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getDouble("payout");
                }
            }

            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals(productBundle.getString("product_type_sought"))) {
                    stepObject = allStepsJsonArray.getJSONObject(i);
                }
            }
            double loan = 0;
            if (productBundle.getString("product_type_sought").equals("11")) {
                loan = 100000;
            } else {
                loan = Double.parseDouble(productBundle.getString("amount"));
            }

            referAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.Grey400));

            referAmount.setText("Rs " + Math.round(loan * payout * stepObject.getDouble("step1") / 10000));
            appFillAmount.setText("Rs " + Math.round((loan * payout * stepObject.getDouble("step2") / 10000) + (loan * payout * stepObject.getDouble("step1") / 10000)));
            docPickupAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step3") / 10000));
            disAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step4") / 10000));

            /*referAmount.setText("Rs " + Math.round(loan * payout * stepObject.getDouble("step1") / 10000));
            appFillAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step2") / 10000));
            docPickupAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step3") / 10000));
            disAmount.setText("+Rs " + Math.round(loan * payout * stepObject.getDouble("step4") / 10000));*/


        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONArray getMaxInPayoutArray() {
        JSONArray resultJsonArray = new JSONArray();

        if (allStepsJsonArray != null && allStepsJsonArray.length() > 0) {
            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                try {
                    String productTypeId = allStepsJsonArray.getJSONObject(i).getString("product_type");
                    JSONObject newJsonObject = new JSONObject();
                    newJsonObject.put("product_type", productTypeId);
                    newJsonObject.put("payout", 0);
                    /* "payout": "0.5000",
                       "product_type": "11",*/
                    resultJsonArray.put(i, newJsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        if (allPayoutJsonArray != null && allPayoutJsonArray.length() > 0) {
            for (int i = 0; i < allPayoutJsonArray.length(); i++) {
                try {
                    String productTypeId = allPayoutJsonArray.getJSONObject(i).getString("product_type");
                    String payout = allPayoutJsonArray.getJSONObject(i).getString("payout");
                    int position = 0;
                    for (int j = 0; j < resultJsonArray.length(); j++) {
                        if (productTypeId.equals(resultJsonArray.getJSONObject(j).getString("product_type"))) {
                            position = j;
                            break;
                        }
                    }
                    float previousValue = Float.parseFloat(resultJsonArray.getJSONObject(position).getString("payout"));
                    if (previousValue < Float.parseFloat(payout)) {
                        resultJsonArray.getJSONObject(position).put("payout", payout);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultJsonArray;

    }

    private void fetchAllProducts() {
        try {
            JSONObject object = new JSONObject();
            object.put("product_type_id", productBundle.getString("product_type_sought"));
            object.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            Helper.showLog(URL.MEETING_LIST,object.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL.FETCH_ALL_PRODUCTS_BY_TYPE, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray body = response.optJSONArray("body");
                    for (int i = 0; i < body.length(); i++) {
                        try {
                            qualifyProduct(body.optJSONObject(i).optString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void qualifyProduct(final String id) throws JSONException {


        JSONObject object = new JSONObject();
        object.put("lead_id", productBundle.getString("lead_id"));
        object.put("product_id", id);
        object.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
        Helper.showLog(URL.QUALIFY_SINGLE,object.toString());
        JsonObjectRequest request = new JsonObjectRequest(URL.QUALIFY_SINGLE, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String product_id = response.getJSONObject("body").getJSONArray("product_list").getJSONObject(0).getString("product_id");

                    if (!hashSet.contains(product_id)) {
                        hashSet.add(product_id);
                        if (response.getJSONObject("body").getJSONArray("product_list").getJSONObject(0).optBoolean("full_eligibility_qual", false)) {
                            qualifiedProduct.put(response.optJSONObject("body").getJSONArray("product_list").getJSONObject(0));
                            listAdapter.updateProduct(qualifiedProduct);
                        } else {
                            nonQualifiedProduct.put(response.optJSONObject("body").getJSONArray("product_list").getJSONObject(0));
                            nonOfferAdapter.updateProduct(nonQualifiedProduct);
                        }

                        if (qualifiedProduct.length() == 0) {
                            noOfferLayout.setVisibility(View.VISIBLE);
                        } else
                            noOfferLayout.setVisibility(View.GONE);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 1F));
        queue.add(request);

    }

    private void initialize(View rootView) {
        offerList = (RecyclerView) rootView.findViewById(R.id.offers_list);
        offerLayout = (RelativeLayout) rootView.findViewById(R.id.offer_layout);
        textViewOfferMessage1 = (TextView) rootView.findViewById(R.id.textViewOfferMessage1);
        textViewOfferMessage2 = (TextView) rootView.findViewById(R.id.textViewOfferMessage2);

        listAdapter = new OfferListAdapter(getActivity(), productBundle);
        offerList.setAdapter(listAdapter);
        offerList.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnMoreOffers = (Button) rootView.findViewById(R.id.more_offers);
        btnMoreOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceLayout.animate().translationY(0F);

            }
        });

        forceLayout = (LinearLayout) rootView.findViewById(R.id.forced_layout);
        btnCloseLayout = (ImageButton) rootView.findViewById(R.id.close_button);
        btnCloseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceLayout.animate().translationY(2000F);
            }
        });

        nonOfferList = (RecyclerView) rootView.findViewById(R.id.non_offers_list);
        nonOfferAdapter = new NonOfferListAdapter(getActivity(), productBundle);
        nonOfferList.setAdapter(nonOfferAdapter);
        nonOfferList.setLayoutManager(new LinearLayoutManager(getActivity()));

        noOfferLayout = (RelativeLayout) rootView.findViewById(R.id.noProductsLayout);

    }


}
