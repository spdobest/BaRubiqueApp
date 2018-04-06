package com.bestdealfinance.bdfpartner.FragmentNew;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.ActivityNew.AllLeadsActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.LeaderBoardActivity;
import com.bestdealfinance.bdfpartner.ActivityNew.MainActivityNew;
import com.bestdealfinance.bdfpartner.AdapterNew.LeaderBoardAdapterNew;
import com.bestdealfinance.bdfpartner.AdapterNew.ScreenSlideAdapter;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.CircularSeekBar;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DashboardFragmentNew extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "DashboardFragmentNew";

    private boolean isShowingMore = false;
    private DB snappyDB;

    public static final String IN_APP_NOTIFICATION_REQUIRED = "in_app_notification_required";

    private TextView tvLeadsInProgress, tvLeadsApproved, tvLeadsClosed;
    private TextView tvNewLeadCount, tvReferCount, tvAppFilledCount, tvDocPickCount, tvUploadDocCount, tvDisbursedCount, tvRejectedCount;
    private View viewNewLead, viewRefer, viewAppFilled, viewDocPick, viewUploadDoc, viewDisbursed, viewRejected;
    private TextView tvSeeDetailLeads;

    private View businessLoanBar, homeLoanBar, carLoanBar, personalLoanBar;
    private View businessLoanBarUpperLayout, homeLoanBarUpperLayout, carLoanBarUpperLayout, personalLoanBarUpperLayout;

    private TextView businessRefer, businessAppFill, businessDocPick;
    private TextView homeRefer, homeAppFill, homeDocPick;
    private TextView carRefer, carAppFill, carDocPick;
    private TextView personalRefer, personalAppFill, personalDocPick;

    private View loanBtnLayout, creditCardBtnLayout;

    private View popupView, notificationView;
    private TextView popupTextView;

    private TextView showMoreLess, inAppNotificationTitle, btnCheckNotification;

    private AppCompatSeekBar seekBar;

    private TextView seeLeaderBoardActivity, seeCalculatorFragment;

    private JSONArray leaderBoardJsonArray;

    private RecyclerView recyclerViewTopAssociates;
    private LeaderBoardAdapterNew leaderBoardAdapterNew;
    private RequestQueue queue;
    private TextView userName, userMobile;

    private boolean showInAppNotification = false;

    private JSONArray allPayoutJsonArray, allStepsJsonArray, maxPayoutInEachProductTypeJsonArray;

    float weightSum1 = 0, weightSum2 = 0, weightSum3 = 0, weightSum4 = 0;
    int blStep1 = 0, blStep2 = 0, blStep3 = 0, blStep4 = 0;
    int hlStep1 = 0, hlStep2 = 0, hlStep3 = 0, hlStep4 = 0;
    int clStep1 = 0, clStep2 = 0, clStep3 = 0, clStep4 = 0;
    int plStep1 = 0, plStep2 = 0, plStep3 = 0, plStep4 = 0;

    private View loanLayout, creditCardLayout;
    private TextView creditCard1, creditCard2, creditCard3, creditCard4;
    private View leadCountCard;
    private View welcomeCard;
    private View payoutCard;

    //PROGRESSBAR FOR CALCULATION
    private ProgressBar progressBarBusinessLoan;
    private ProgressBar progressBarHomeLoan;
    private ProgressBar progressBarCarLoan;
    private ProgressBar progressBarPersonalLoan;
    float maxPrice;

    private ViewPager viewpagerWelcomeHomeBanner;
    private ImageView imageViewIndicatorSliding1;
    private ImageView imageViewIndicatorSliding2;
    private ImageView imageViewIndicatorSliding3;

    public DashboardFragmentNew() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView;

        if (Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()).equals("")) {
            rootView = inflater.inflate(R.layout.fragment_dashboard_new, container, false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rootView.findViewById(R.id.nested_scroll_view).scrollTo(0, 0);
                }
            }, 2000);
        } else {//fragment_dashboard_new_logged
            rootView = inflater.inflate(R.layout.fragment_dashboard_new, container, false);
        }

        userName = (TextView) rootView.findViewById(R.id.toolbar_user_name);
        userMobile = (TextView) rootView.findViewById(R.id.toolbar_user_phone);
        queue = Volley.newRequestQueue(getActivity());
        initializeLeadStatusViews(rootView);
        initializeLoanBars(rootView);
        initializePayoutCard(rootView);

        initializeDashboardPayoutsFromDB();


        initializeButtons(rootView);
        changeWeightOfLoanBars(5000000, true);
        setSeekBarPercentage(false, 50);
        setLeaderBoard(rootView);
        initializeBanner(rootView);

        return rootView;

    }

    private void initializeBanner(View rootView) {
        viewpagerWelcomeHomeBanner = (ViewPager) rootView.findViewById(R.id.viewpagerWelcomeHomeBanner);
        imageViewIndicatorSliding1 = (ImageView) rootView.findViewById(R.id.imageViewIndicatorSliding1);
        imageViewIndicatorSliding2 = (ImageView) rootView.findViewById(R.id.imageViewIndicatorSliding2);
        imageViewIndicatorSliding3 = (ImageView) rootView.findViewById(R.id.imageViewIndicatorSliding3);
        viewpagerWelcomeHomeBanner.setAdapter(new ScreenSlideAdapter(getActivity(), ScreenSlideAdapter.SLIDING_TYPE_IMAGE));

        viewpagerWelcomeHomeBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changePagerImage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void changePagerImage(int currentPosition) {
        switch (currentPosition) {
            case 0:
                imageViewIndicatorSliding1.setImageResource(R.drawable.ic_indicator_dark);
                imageViewIndicatorSliding2.setImageResource(R.drawable.ic_indicator_light);
                imageViewIndicatorSliding3.setImageResource(R.drawable.ic_indicator_light);
                break;
            case 1:
                imageViewIndicatorSliding2.setImageResource(R.drawable.ic_indicator_dark);
                imageViewIndicatorSliding3.setImageResource(R.drawable.ic_indicator_light);
                break;
            case 2:
                imageViewIndicatorSliding3.setImageResource(R.drawable.ic_indicator_dark);
                break;
        }
    }


    private void initializePayoutCard(View rootView) {

        payoutCard = rootView.findViewById(R.id.payout_card);
        if (Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()).equals("")) {
            payoutCard.setVisibility(View.GONE);
        } else {
            payoutCard.setVisibility(View.VISIBLE);
            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.seekbar);
            CircularSeekBar seekbar = new CircularSeekBar(getActivity());
            seekbar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            seekbar.setProgress(1);
            seekbar.setIsTouchEnabled(false);
            seekbar.setCircleColor(getResources().getColor(R.color.Grey300));
            seekbar.setCircleProgressColor(getResources().getColor(R.color.Blue300));
            seekbar.setPointerAlpha(0);
            seekbar.setPointerColor(Color.parseColor("#00FFFFFF"));

            seekbar.setEnabled(false);
            ll.addView(seekbar);

        }
    }


    private void initializeDashboardPayoutsFromDB() {
        try {
            snappyDB = DBFactory.open(getActivity());

            if (snappyDB.exists(Constant.DB_ALL_PAYOUTS_JSON_ARRAY)) {
                allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
                allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
                maxPayoutInEachProductTypeJsonArray = getMaxInPayoutArray();
                changeWeightOfLoanBars(seekBar.getProgress() * 100000, true);
                setSeekBarPercentage(false, 50);
            }
            snappyDB.close();

        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }
    }


    private void setLeaderBoard(View rootView) {

        recyclerViewTopAssociates = (RecyclerView) rootView.findViewById(R.id.recycler_view_dashboard_leader_board);
        leaderBoardAdapterNew = new LeaderBoardAdapterNew(getActivity());
        recyclerViewTopAssociates.setAdapter(leaderBoardAdapterNew);
        recyclerViewTopAssociates.setNestedScrollingEnabled(false);
        recyclerViewTopAssociates.setLayoutManager(new LinearLayoutManager(getActivity()));

        seeLeaderBoardActivity = (TextView) rootView.findViewById(R.id.txt_see_details_of_leader_board);
        seeLeaderBoardActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leaderBoardJsonArray != null && leaderBoardJsonArray.length() > 0) {
                    Intent newIntent = new Intent(getContext(), LeaderBoardActivity.class);
                    newIntent.putExtra("leaderBoardJsonArray", leaderBoardJsonArray.toString());
                    startActivity(newIntent);
                } else {
                    Toast.makeText(getActivity(), "Unable to access!", Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
            snappyDB = DBFactory.open(getActivity());

            if (snappyDB.exists(Constant.DB_LEADERBOARD_ARRAY)) {
                leaderBoardJsonArray = new JSONArray(snappyDB.get(Constant.DB_LEADERBOARD_ARRAY));
                leaderBoardAdapterNew.updateData(leaderBoardJsonArray, 3);
            }
            snappyDB.close();
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

    private void changePayoutValuesOnSeekBarChanged() {

        //businessRefer.setText(Helper.formatRs("" + (int) weightSum1 * blStep1 / 100));
        businessAppFill.setText("₹ " + Helper.formatRs("" + (int) weightSum1));
        //businessDocPick.setText(Helper.formatRs("" + (int) weightSum1 * blStep3 / 100));

        //homeRefer.setText(Helper.formatRs("" + (int) weightSum2 * hlStep1 / 100));
        homeAppFill.setText("₹ " + Helper.formatRs("" + (int) weightSum2));
        //homeDocPick.setText(Helper.formatRs("" + (int) weightSum2 * hlStep3 / 100));

        //carRefer.setText(Helper.formatRs("" + (int) weightSum3 * clStep1 / 100));
        carAppFill.setText("₹ " + Helper.formatRs("" + (int) weightSum3));
        //carDocPick.setText(Helper.formatRs("" + (int) weightSum3 * clStep3 / 100));

        //personalRefer.setText(Helper.formatRs("" + (int) weightSum4 * plStep1 / 100));
        personalAppFill.setText("₹ " + Helper.formatRs("" + (int) weightSum4));
        //personalDocPick.setText(Helper.formatRs("" + (int) weightSum4 * plStep3 / 100));
    }

    private void changePayoutTextSize(int size) {

        businessRefer.setTextSize(size);
        businessAppFill.setTextSize(size);
        businessDocPick.setTextSize(size);

        homeRefer.setTextSize(size);
        homeAppFill.setTextSize(size);
        homeDocPick.setTextSize(size);

        carRefer.setTextSize(size);
        carAppFill.setTextSize(size);
        carDocPick.setTextSize(size);

        personalRefer.setTextSize(size);
        personalAppFill.setTextSize(size);
        personalDocPick.setTextSize(size);
    }


    private void changeWeightOfLoanBars(int loanAmount, boolean firstTime) {

        // BL -39, HL - 26, CL - 22, PL - 25
        if (allStepsJsonArray != null && allStepsJsonArray.length() > 0
                && maxPayoutInEachProductTypeJsonArray != null && maxPayoutInEachProductTypeJsonArray.length() > 0) {
            for (int i = 0; i < allStepsJsonArray.length(); i++) {
                try {
                    if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals("39")) {
                        blStep1 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step1"));
                        blStep2 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step2"));
                        blStep3 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step3"));
                        blStep4 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step4"));
                        weightSum1 = (loanAmount * Float.parseFloat(maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("payout"))) / 100;

                    } else if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals("26")) {
                        hlStep1 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step1"));
                        hlStep2 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step2"));
                        hlStep3 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step3"));
                        hlStep4 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step4"));
                        weightSum2 = (loanAmount * Float.parseFloat(maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("payout"))) / 100;

                    } else if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals("22")) {
                        clStep1 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step1"));
                        clStep2 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step2"));
                        clStep3 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step3"));
                        clStep4 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step4"));
                        weightSum3 = (loanAmount * Float.parseFloat(maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("payout"))) / 100;

                    } else if (allStepsJsonArray.getJSONObject(i).getString("product_type").equals("25")) {
                        plStep1 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step1"));
                        plStep2 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step2"));
                        plStep3 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step3"));
                        plStep4 = Integer.parseInt(allStepsJsonArray.getJSONObject(i).getString("step4"));
                        weightSum4 = (loanAmount * Float.parseFloat(maxPayoutInEachProductTypeJsonArray.getJSONObject(i).getString("payout"))) / 100;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (firstTime) {

            float max = findMax(weightSum1, weightSum2, weightSum3, weightSum4);
            maxPrice = max;
            ((LinearLayout) businessLoanBarUpperLayout).setWeightSum(100);
            ((LinearLayout) homeLoanBarUpperLayout).setWeightSum(100);
            ((LinearLayout) carLoanBarUpperLayout).setWeightSum(100);
            ((LinearLayout) personalLoanBarUpperLayout).setWeightSum(100);

            float weight = 0;
            LinearLayout.LayoutParams param11 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT);

            weight = (weightSum1 / max) * 100;
            if (weight < 50) {
                param11.weight = 60;
            } else {
                param11.weight = weight;
            }
            businessLoanBar.setLayoutParams(param11);

            LinearLayout.LayoutParams param12 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT);

            weight = (weightSum2 / max) * 100;
            if (weight < 50) {
                param12.weight = 60;
            } else {
                param12.weight = weight;
            }

            homeLoanBar.setLayoutParams(param12);

            LinearLayout.LayoutParams param13 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT);

            weight = (weightSum3 / max) * 100;
            if (weight < 50) {
                param13.weight = 60;
            } else {
                param13.weight = weight;
            }

            carLoanBar.setLayoutParams(param13);

            LinearLayout.LayoutParams param14 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT);

            weight = (weightSum4 / max) * 100;
            if (weight < 50) {
                param14.weight = 60;
            } else {
                param14.weight = weight;
            }

            personalLoanBar.setLayoutParams(param14);


        }

        changePayoutValuesOnSeekBarChanged();
    }

    private void initializeButtons(View rootView) {

        seeCalculatorFragment = (TextView) rootView.findViewById(R.id.txt_see_details_of_payout);
        seeCalculatorFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityNew) getActivity()).setTabPosition(2);
            }
        });
        if (Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()).equals("")) {
            FloatingActionButton newLeadFabButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
            newLeadFabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivityNew) getActivity()).setTabPosition(1);
                }
            });
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getProgress() > 0) {
            setSeekBarAndPopupValue();
            changeWeightOfLoanBars(seekBar.getProgress() * 100000, true);
            setSeekBarPercentage(false, progress);
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        changePayoutTextSize(16);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        changePayoutTextSize(14);
    }


    private void setSeekBarAndPopupValue() {
        popupTextView.setText("" + seekBar.getProgress() + ",00,000");
        float xOffset = getXPositionOfSeekBar(seekBar);
        popupView.setX(xOffset);
    }

    private float getXPositionOfSeekBar(SeekBar seekBar) {

        int progress = seekBar.getProgress();

        if (seekBar.getProgress() > 90) {
            progress = 90;
        } else if (seekBar.getProgress() < 10) {
            progress = 10;
        }

        float val = (((float) progress * ((float) seekBar.getWidth() - 2 * seekBar.getThumbOffset())) /
                seekBar.getMax());

        float offset = seekBar.getThumbOffset();

        float newX = val + offset - (popupView.getWidth() / 2);

        return newX;
    }


    private float findMax(float weightSum1, float weightSum2, float weightSum3, float weightSum4) {
        if (weightSum1 > weightSum2 && weightSum1 > weightSum3 && weightSum1 > weightSum4) {
            return weightSum1;
        } else if (weightSum2 > weightSum1 && weightSum2 > weightSum3 && weightSum2 > weightSum4) {
            return weightSum2;
        } else if (weightSum3 > weightSum1 && weightSum3 > weightSum2 && weightSum3 > weightSum4) {
            return weightSum3;
        } else {
            return weightSum4;
        }
    }

    private void initializeLoanBars(View rootView) {

        seekBar = (AppCompatSeekBar) rootView.findViewById(R.id.loan_seek_bar);
        seekBar.clearFocus();
        seekBar.setOnSeekBarChangeListener(this);

        progressBarBusinessLoan = (ProgressBar) rootView.findViewById(R.id.progressBarBusinessLoan);
        progressBarHomeLoan = (ProgressBar) rootView.findViewById(R.id.progressBarHomeLoan);
        progressBarCarLoan = (ProgressBar) rootView.findViewById(R.id.progressBarCarLoan);
        progressBarPersonalLoan = (ProgressBar) rootView.findViewById(R.id.progressBarPersonalLoan);

        popupView = rootView.findViewById(R.id.popup_view);
        popupTextView = (TextView) rootView.findViewById(R.id.text);

        businessLoanBar = rootView.findViewById(R.id.bar_business_loan);
        homeLoanBar = rootView.findViewById(R.id.bar_home_loan);
        carLoanBar = rootView.findViewById(R.id.bar_car_loan);
        personalLoanBar = rootView.findViewById(R.id.bar_personal_loan);

        businessLoanBarUpperLayout = rootView.findViewById(R.id.bar_business_loan_upper_layout);
        homeLoanBarUpperLayout = rootView.findViewById(R.id.bar_home_loan_upper_layout);
        carLoanBarUpperLayout = rootView.findViewById(R.id.bar_car_loan_upper_layout);
        personalLoanBarUpperLayout = rootView.findViewById(R.id.bar_personal_loan_upper_layout);

        businessRefer = (TextView) rootView.findViewById(R.id.bar_business_refer);
        businessAppFill = (TextView) rootView.findViewById(R.id.bar_business_app_fill);
        businessDocPick = (TextView) rootView.findViewById(R.id.bar_business_doc_pick);

        homeRefer = (TextView) rootView.findViewById(R.id.bar_home_refer);
        homeAppFill = (TextView) rootView.findViewById(R.id.bar_home_app_fill);
        homeDocPick = (TextView) rootView.findViewById(R.id.bar_home_doc_pick);

        carRefer = (TextView) rootView.findViewById(R.id.bar_car_refer);
        carAppFill = (TextView) rootView.findViewById(R.id.bar_car_app_fill);
        carDocPick = (TextView) rootView.findViewById(R.id.bar_car_doc_pick);

        personalRefer = (TextView) rootView.findViewById(R.id.bar_personal_refer);
        personalAppFill = (TextView) rootView.findViewById(R.id.bar_personal_app_fill);
        personalDocPick = (TextView) rootView.findViewById(R.id.bar_personal_doc_pick);


        loanLayout = rootView.findViewById(R.id.loan_layout);
        creditCardLayout = rootView.findViewById(R.id.credit_card_layout);
        creditCard1 = (TextView) rootView.findViewById(R.id.credit_card_1);
        creditCard2 = (TextView) rootView.findViewById(R.id.credit_card_2);
        creditCard3 = (TextView) rootView.findViewById(R.id.credit_card_3);
        creditCard4 = (TextView) rootView.findViewById(R.id.credit_card_4);
    }

    private void initializeLeadStatusViews(View rootView) {

        leadCountCard = rootView.findViewById(R.id.lead_count_card);
        showMoreLess = (TextView) rootView.findViewById(R.id.lead_show_more_less);
        viewNewLead = rootView.findViewById(R.id.status_new_lead);
        viewRefer = rootView.findViewById(R.id.status_refer);
        viewAppFilled = rootView.findViewById(R.id.status_app_filled);
        viewDocPick = rootView.findViewById(R.id.status_doc_pick);
        viewUploadDoc = rootView.findViewById(R.id.status_upload_doc);
        viewDisbursed = rootView.findViewById(R.id.status_disbursed);
        viewRejected = rootView.findViewById(R.id.status_rejected);

        tvLeadsInProgress = (TextView) rootView.findViewById(R.id.leads_in_progress);
        tvLeadsApproved = (TextView) rootView.findViewById(R.id.leads_approved);
        tvLeadsClosed = (TextView) rootView.findViewById(R.id.leads_closed);


        tvSeeDetailLeads = (TextView) rootView.findViewById(R.id.txt_see_details_of_leads);
        tvSeeDetailLeads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllLeadsActivityNew.class));
            }
        });

        tvNewLeadCount = (TextView) rootView.findViewById(R.id.status_new_lead_count);
        tvReferCount = (TextView) rootView.findViewById(R.id.status_refer_count);
        tvAppFilledCount = (TextView) rootView.findViewById(R.id.status_app_filled_count);
        tvDocPickCount = (TextView) rootView.findViewById(R.id.status_doc_pick_count);
        tvUploadDocCount = (TextView) rootView.findViewById(R.id.status_upload_doc_count);
        tvDisbursedCount = (TextView) rootView.findViewById(R.id.status_disbursed_count);
        tvRejectedCount = (TextView) rootView.findViewById(R.id.status_rejected_count);

        setVisibilityOfSubLeadStatus(View.GONE);
        setSubLeadStatusValue(0, 0, 0, 0, 0, 0, 0);

        getLeadCount();

        showMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int visibility;

                if (isShowingMore) {
                    visibility = View.GONE;
                    isShowingMore = false;
                    showMoreLess.setText("SEE MORE");
                    showMoreLess.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down, 0);
                } else {
                    visibility = View.VISIBLE;
                    isShowingMore = true;
                    showMoreLess.setText("SEE LESS");
                    showMoreLess.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_up, 0);
                }
                setVisibilityOfSubLeadStatus(visibility);
            }
        });
    }

    private void getLeadCount() {

        try {

            if (Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()).equals("")) {
                leadCountCard.setVisibility(View.GONE);
            } else {
                leadCountCard.setVisibility(View.VISIBLE);
                JSONObject reqObject = new JSONObject();
                reqObject.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
                Helper.showLog(URL.LEAD_COUNT, reqObject.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL.LEAD_COUNT, reqObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: getLeadCount " + response);
                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                            try {
                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {

                                    JSONArray leadCountArray = response.optJSONObject(Constant.BODY).optJSONObject("detail").optJSONArray("data");
                                    int inProgressCount = 0;
                                    int approvedCount = 0;
                                    int closedCount = 0;
                                    if (leadCountArray.length() > 0) {
                                        for (int i = 0; i < leadCountArray.length(); i++) {
                                            JSONObject newObject = leadCountArray.getJSONObject(i);
                                            String leadState = newObject.optString("lead_state");
                                            int count = Integer.parseInt(newObject.optString("total_leads"));
                                            if (leadState != null && !leadState.equals("null")) {
                                                switch (leadState) {
                                                    case "ASSIGNED":
                                                        inProgressCount += count;
                                                        break;
                                                    case "CLOSED":
                                                        closedCount += count;
                                                        break;
                                                    case "FRESH":
                                                        inProgressCount += count;
                                                        break;
                                                    case "INCOMPLETE":
                                                        inProgressCount += count;
                                                        break;
                                                    case "INPRIN_APPROV":
                                                        inProgressCount += count;
                                                        break;
                                                    case "APPROVED":
                                                        approvedCount += count;
                                                        break;
                                                    default:
                                                        inProgressCount += count;
                                                        break;
                                                }
                                            }
                                        }
                                        tvLeadsInProgress.setText("" + inProgressCount);
                                        tvLeadsApproved.setText("" + approvedCount);
                                        tvLeadsClosed.setText("" + closedCount);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(jsonObjectRequest);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setVisibilityOfSubLeadStatus(int visibility) {
        viewNewLead.setVisibility(visibility);
        viewRefer.setVisibility(visibility);
        viewAppFilled.setVisibility(visibility);
        viewDocPick.setVisibility(visibility);
        viewUploadDoc.setVisibility(visibility);
        viewDisbursed.setVisibility(visibility);
        viewRejected.setVisibility(visibility);
    }

    private void setSubLeadStatusValue(int newLead, int refer, int filled, int docPick, int uploadDoc, int disbursed, int rejected) {
        tvNewLeadCount.setText("" + newLead);
        tvReferCount.setText("" + refer);
        tvAppFilledCount.setText("" + filled);
        tvDocPickCount.setText("" + docPick);
        tvUploadDocCount.setText("" + uploadDoc);
        tvDisbursedCount.setText("" + disbursed);
        tvRejectedCount.setText("" + rejected);
    }

    private void setSeekBarPercentage(boolean isFirstTime, int progress) {
        if (isFirstTime) {
            progressBarBusinessLoan.setProgress(Math.round((weightSum1 / maxPrice) * 50));
            progressBarHomeLoan.setProgress(Math.round((weightSum2 / maxPrice) * 50));
            progressBarCarLoan.setProgress(Math.round((weightSum3 / maxPrice) * 50));
            progressBarPersonalLoan.setProgress(Math.round((weightSum4 / maxPrice) * 50));
        } else {
            progressBarBusinessLoan.setProgress(Math.round((weightSum1 / maxPrice) * progress));
            progressBarHomeLoan.setProgress(Math.round((weightSum2 / maxPrice) * progress));
            progressBarCarLoan.setProgress(Math.round((weightSum3 / maxPrice) * progress));
            progressBarPersonalLoan.setProgress(Math.round((weightSum4 / maxPrice) * progress));
        }
    }
}

