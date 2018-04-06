package com.bestdealfinance.bdfpartner.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.FragmentNew.CreditCardDetailsFragment;
import com.bestdealfinance.bdfpartner.FragmentNew.LoanDetailsFragment;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.ProductsIDAdapter;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SelectedProductFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ArrayList<HashMap<String, String>> list = new ArrayList<>();
    View view;
    String product_type;
    Bundle productBundle;
    String product_name;

    private RequestQueue queue;

    OnRecycleClickListener listener;
    List<String> imgurl = new ArrayList<>();
    private ImageView progressBar;
    private AnimationDrawable animation;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;

    public SelectedProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productBundle = getArguments();
            product_type = getArguments().getString("type");
            product_name = getArguments().getString("name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_product, container, false);


        queue = Volley.newRequestQueue(getActivity());
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(getActivity(), toolbar, product_name, false, true, false);

        progressBar = (ImageView) view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();

        progressBar.setVisibility(View.VISIBLE);
        animation.start();
        getAllSubProductsFromServer();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        listener = new OnRecycleClickListener() {
            @Override
            public void onItemClick(int item) {
                String id = list.get(item).get("id");
                productBundle.putString("id", id);
                FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);

                if (productBundle.getString("type").equals("11")) {
                    CreditCardDetailsFragment creditCardDetailsFragment = new CreditCardDetailsFragment();
                    creditCardDetailsFragment.setArguments(productBundle);
                    manager.replace(R.id.product_activity_main_layout, creditCardDetailsFragment).commit();
                } else {
                    LoanDetailsFragment fragment = new LoanDetailsFragment();
                    fragment.setArguments(productBundle);
                    manager.replace(R.id.product_activity_main_layout, fragment).commit();
                }
            }
        };

        return view;
    }

    public interface OnRecycleClickListener {
        void onItemClick(int item);
    }

    private void getAllSubProductsFromServer() {
        JSONObject request = new JSONObject();
        try {
            request.put("product_type_id", product_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest getSubProductsRequest = new JsonObjectRequest(Request.Method.POST, Util.ALL_PRODUCTS_BY_TYPE, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.optString("status_code").equals("2000")) {
                        list = new ArrayList<>();
                        imgurl = new ArrayList<>();
                        JSONArray jsonArray = response.optJSONArray("body");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject temp = jsonArray.optJSONObject(i);

                                if (temp != null) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    String id = temp.optString("name");
                                    String p_id = temp.optString("id");
                                    imgurl.add(i, temp.optString("image_url"));
                                    map.put("id", p_id);
                                    map.put("name", id);
                                    list.add(i, map);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            mAdapter = new ProductsIDAdapter(getActivity(), list, imgurl, product_type, listener);
                            mRecyclerView.setAdapter(mAdapter);
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
