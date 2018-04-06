package com.bestdealfinance.bdfpartner.FragmentNew;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.ActivityNew.MainActivityNew;
import com.bestdealfinance.bdfpartner.AdapterNew.BankAndPayoutAdapter;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.base.BaseFragment;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PayoutFragmentNew extends BaseFragment   {

    public static final String TAG = "PayoutFragmentNew";
    public JSONArray products;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private BankAndPayoutAdapter bankAndPayoutAdapter;
    private RequestQueue queue;
    private DB snappyDB;
    private JSONArray allPayoutJsonArray;
    private JSONArray allStepsJsonArray;
    private AppCompatSeekBar payoutSeekbar;
    private TextView tvReferText;
    private TextView tvAppFillText;
    private TextView tvDocPickupText;
    private TextView tvDisburseText;
    private int STEP;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;

    private AppCompatTextView textViewIncludeTaskToolbar;

    public PayoutFragmentNew() {
    }

    public static PayoutFragmentNew newInstance() {
        Bundle args = new Bundle();
        PayoutFragmentNew fragment = new PayoutFragmentNew();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payout_new, container, false);

        queue = Volley.newRequestQueue(getActivity());

        // here initialize the widgets
        initViews(rootView);

        setSeekbar(true);


        return rootView;
    }

    @Override
    public void initViews(View rootView) {

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout_product);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_bank_and_payout);
        bankAndPayoutAdapter = new BankAndPayoutAdapter(getActivity());
        recyclerView.setAdapter(bankAndPayoutAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        payoutSeekbar = (AppCompatSeekBar) rootView.findViewById(R.id.payout_seekbar);
        tvReferText = (TextView) rootView.findViewById(R.id.refer_text);
        tvAppFillText = (TextView) rootView.findViewById(R.id.app_fill_text);
        tvDocPickupText = (TextView) rootView.findViewById(R.id.doc_pickup_text);
        tvDisburseText = (TextView) rootView.findViewById(R.id.disburse_text);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.new_toolbar);
        ToolbarHelper.initializeToolbar(getActivity(), toolbar, "PAYOUTS", false, false, true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setSeekbar(false);
                updateListPayout(STEP);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void setClickListener() {

    }

    private void setSeekbar(boolean isFirstTime) {
        if(isFirstTime) {
            STEP = 4;
            payoutSeekbar.setProgress(92);
            updateListPayout(STEP);
        }
        seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int i = seekBar.getProgress();
                if (i < 23) {
                    payoutSeekbar.setProgress(10);
                    STEP = 1;
                    tvAppFillText.setTextColor(getResources().getColor(R.color.Grey500));
                    tvDocPickupText.setTextColor(getResources().getColor(R.color.Grey500));
                    tvDisburseText.setTextColor(getResources().getColor(R.color.Grey500));

                } else if (i < 50) {
                    STEP = 2;
                    payoutSeekbar.setProgress(35);
                    tvAppFillText.setTextColor(getResources().getColor(R.color.Blue500));
                    tvDocPickupText.setTextColor(getResources().getColor(R.color.Grey500));
                    tvDisburseText.setTextColor(getResources().getColor(R.color.Grey500));

                } else if (i < 75) {
                    STEP = 3;
                    payoutSeekbar.setProgress(65);
                    tvAppFillText.setTextColor(getResources().getColor(R.color.Blue500));
                    tvDocPickupText.setTextColor(getResources().getColor(R.color.Blue500));
                    tvDisburseText.setTextColor(getResources().getColor(R.color.Grey500));

                } else {
                    STEP = 4;
                    payoutSeekbar.setProgress(92);
                    tvAppFillText.setTextColor(getResources().getColor(R.color.Blue500));
                    tvDocPickupText.setTextColor(getResources().getColor(R.color.Blue500));
                    tvDisburseText.setTextColor(getResources().getColor(R.color.Blue500));
                }
                updateListPayout(STEP);
            }
        };
        payoutSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private int setProductType(int position) {
        if (position == 0) {
            return 11;
        } else if (position == 1) {
            return 39;
        } else if (position == 2) {
            return 22;
        } else if (position == 3) {
            return 26;
        } else if (position == 4) {
            return 28;
        } else if (position == 5) {
            return 25;
        } else if (position == 6) {
            return 23;
        } else if (position == 7) {
            return 29;
        }
        return 0;
    }

    public void updateListPayout(int step) {
        try {
            snappyDB = DBFactory.open(getActivity());
            if (snappyDB.exists(Constant.DB_ALL_PAYOUTS_JSON_ARRAY) && snappyDB.exists(Constant.DB_ALL_STEPS_JSON_ARRAY)) {
                allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
                allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
                snappyDB.close();
                if (allPayoutJsonArray != null && allPayoutJsonArray.length() > 0 && allStepsJsonArray != null && allStepsJsonArray.length() > 0) {
                    int position = tabLayout.getSelectedTabPosition();
                    int productType = setProductType(position);


                    JSONObject stepObject = null;

                        for (int i = 0; i < allStepsJsonArray.length(); i++) {
                            try {
                                String pt = allStepsJsonArray.getJSONObject(i).getString("product_type");

                                if (pt.equals("" + productType)) {
                                    stepObject = allStepsJsonArray.getJSONObject(i);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    JSONArray payoutArray = new JSONArray();
                    for (int i = 0; i < allPayoutJsonArray.length(); i++) {
                        if (allPayoutJsonArray.getJSONObject(i).getString("product_type").equals("" + productType)) {
                            payoutArray.put(allPayoutJsonArray.getJSONObject(i));
                        }
                    }

                    bankAndPayoutAdapter.updateData(payoutArray, stepObject, step, productType);
                }
            }
        } catch (JSONException | SnappydbException e) {
            e.printStackTrace();
        }
    }
}
