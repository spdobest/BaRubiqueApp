package com.bestdealfinance.bdfpartner.fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.ProductsIDAdapter;
import com.bestdealfinance.bdfpartner.application.Util;

import org.json.JSONArray;
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


public class Select_product extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ArrayList<HashMap<String, String>> list = new ArrayList<>();
    View view;
    String product_type;
    String product_id;
    Bundle product_type_id;
    ImageView back_arrow;
    OnRecycleClickListener listener;
    List<String> imgurl = new ArrayList<>();
    private ImageView progressBar;
    private AnimationDrawable animation;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public Select_product() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product_type_id = getArguments();
            product_type = getArguments().getString("type");
            Logs.LogD("Arguments", product_type_id.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        list = new ArrayList<>();
//        imgurl = new ArrayList<>();
        Logs.LogD("ArgumentsResume", product_type_id.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select_product, container, false);
        progressBar = (ImageView) view.findViewById(R.id.waiting);
        progressBar.setBackgroundResource(R.drawable.waiting);
        animation = (AnimationDrawable) progressBar.getBackground();
        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);
        TextView landing_head = (TextView) view.findViewById(R.id.landing_loan_type);
//        if (product_type.equals("11")){
        landing_head.setText(getProductName(product_type));
//        }
        DownJSON json = new DownJSON();
        json.executeOnExecutor(Util.threadPool);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        listener = new OnRecycleClickListener() {
            @Override
            public void onItemClick(int item) {
                String id = list.get(item).get("id");
                product_type_id.putString("id", id);
                android.support.v4.app.FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);

                if (product_type_id.getString("type").equals("11")) {
                    //TODO For Credit Card Fragment
                    Landing_CC fragment = new Landing_CC();
                    fragment.setArguments(product_type_id);
                    manager.replace(R.id.fragmnet, fragment).commit();
                } else {
                    //TODO For Loans Fragment.
                    Landing_Loan fragment = new Landing_Loan();
                    fragment.setArguments(product_type_id);
                    manager.replace(R.id.fragmnet, fragment).commit();
                }
            }
        };
//        mAdapter = new ProductsIDAdapter(getActivity(),list, imgurl, product_type,listener);
//        mRecyclerView.setAdapter(mAdapter);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });
        return view;
    }

    private String getProductName(String product_type) {
        String product = "";
        switch (product_type) {
            case "11":
                product = "Credit Card";
                break;
            case "25":
                product = "Personal Loan";
                break;
            case "26":
                product = "Home Loan";
                break;
            case "28":
                product = "Loan Against Property";
                break;
            case "22":
                product = "Car Loan";
                break;
            case "23":
                product = "Two Wheeler";
                break;
            case "39":
                product = "Business Loan";
                break;
        }
        return product;
    }

    public interface OnRecycleClickListener {
        void onItemClick(int item);
    }

    final class DownJSON extends AsyncTask<Void, Void, String> {

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
                URL url = new URL(Util.ALL_PRODUCTS_BY_TYPE);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(50000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                JSONObject data = new JSONObject();
                data.put("product_type_id", product_type);
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("status_code").equals("2000")) {
                    list = new ArrayList<>();
                    imgurl = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.optJSONArray("body");
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
                                Logs.LogD("map", map.toString());
                                Logs.LogD("imge", imgurl.get(i));
                            }
                        }
                        //TODO Do here
                        mAdapter = new ProductsIDAdapter(getActivity(), list, imgurl, product_type, listener);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    //TODO Product Type Not Found
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
