package com.bestdealfinance.bdfpartner.FragmentNew;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rbq on 10/20/16.
 */

public class PayoutAndEarningFragment extends Fragment {

    private TabLayout tabLayout;
    private static final int SIGN_REQUEST = 1;

    private RequestQueue queue;

    public PayoutAndEarningFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payout_and_earning_new, container, false);


        queue = Volley.newRequestQueue(getActivity());

        tabLayout = (TabLayout) rootView.findViewById(R.id.payout_tab_layout);

        PayoutFragmentNew payoutFragmentNew = new PayoutFragmentNew();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.main_layout_payout_and_earning, payoutFragmentNew, "PayoutFragmentNew")
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    PayoutFragmentNew payoutFragmentNew = new PayoutFragmentNew();
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.main_layout_payout_and_earning, payoutFragmentNew, "PayoutFragmentNew")
                            .commit();
                } else if (tab.getPosition() == 1) {
                    EarningsFragmentNew earningsFragmentNew = new EarningsFragmentNew();
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.main_layout_payout_and_earning, earningsFragmentNew, "EarningsFragmentNew")
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }
}
