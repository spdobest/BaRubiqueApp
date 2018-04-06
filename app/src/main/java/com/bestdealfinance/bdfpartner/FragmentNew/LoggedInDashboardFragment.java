package com.bestdealfinance.bdfpartner.FragmentNew;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.ActivityNew.AllLeadsActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.LeaderBoardActivity;
import com.bestdealfinance.bdfpartner.ActivityNew.MainActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.ReminderActivity;
import com.bestdealfinance.bdfpartner.AdapterNew.AdapterReminder;
import com.bestdealfinance.bdfpartner.AdapterNew.LeaderBoardAdapterNew;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.ProfileActivity;
import com.bestdealfinance.bdfpartner.application.CircularSeekBar;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.base.BaseFragment;
import com.bestdealfinance.bdfpartner.base.SnackbarCallback;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by siba.prasad on 23-12-2016.
 */

public class LoggedInDashboardFragment extends BaseFragment implements View.OnClickListener, SnackbarCallback, SeekBar.OnSeekBarChangeListener, TabLayout.OnTabSelectedListener {
    public static final String TAG = "LoggedInDashboardFragme";
    String selectedTabId = "11";
    View notification_card;
    // widgets declaration
    private View rootView;
    private Toolbar toolbar;
    private LinearLayout linearLayoutProfileHome;
    private ImageView toolbar_user_image;
    private AppCompatImageView imageViewDownloadPdf;
    private TextView tvUserName;
    private TextView tvUserMobile;
    private TextView inAppNotificationTitle;
    private RelativeLayout relativeLayoutNotificationNotification;
    private LinearLayout linearLayoutDashboardLoggedUser;
    /// FOR LEAD STATUS
    private View leadCountCard;
    private TextView showMoreLess;
    private View viewNewLead, viewRefer, viewAppFilled, viewDocPick, viewUploadDoc, viewDisbursed, viewRejected;
    private TextView tvLeadsInProgress, tvLeadsApproved, tvLeadsClosed;
    private TextView tvSeeDetailLeads;
    private TextView tvNewLeadCount, tvReferCount, tvAppFilledCount, tvDocPickCount, tvUploadDocCount, tvDisbursedCount, tvRejectedCount;
    private boolean isShowingMore = false;
    // for topassocial=tes
    private RecyclerView recyclerViewTopAssociates;
    private LeaderBoardAdapterNew leaderBoardAdapterNew;
    private TextView seeLeaderBoardActivity;
    private JSONArray leaderBoardJsonArray;
    // for payout card
    private View payoutCard;
    // for pay;out calculator
    private TextView popupTextView;
    private AppCompatTextView txt_see_details_of_payout;
    private AppCompatSeekBar seekBarLoan;
    private View popupView, notificationView;
    private TabLayout tabLayoutPayoutCalculator;
    private String[] payLayoutIds = {"11", "25", "26", "28", "39", "22", "23", "29"};
    private AppCompatTextView textViewbankname1, textViewbankname2, textViewbankname3;
    private AppCompatTextView textViewbankPrice1, textViewbankPrice2, textViewbankPrice3;
    private AppCompatTextView textViewbankProduct1, textViewbankProduct2, textViewbankProduct3;
    private RelativeLayout relativelayoutSeekbarRoot;
    private CircularSeekBar seekbar;
    private JSONArray allPayoutJsonArray;
    private String name1, name2, name3;
    private String note1, note2, note3;
    private float percentage1 = 0.0f, percentage2 = 0.0f, percentage3 = 0.0f;
    //other class declaration
    //database initialization
    private DB snappyDB;
    // volley declaration
    private RequestQueue queue;

    private JSONArray allStepsJsonArray, maxPayoutInEachProductTypeJsonArray;

    private long sliderLoanAmount = 5000000;
    private boolean showInAppNotification = false;
    private TextView btnCheckNotification;

    private String customerNumber;

    private boolean isFragmentOnForeGround = true;

    private boolean isBankOrProfileNotification = false;

    int inProgressCount = 0;
    int approvedCount = 0;
    int closedCount = 0;

    public static LoggedInDashboardFragment newInstance() {
        Bundle args = new Bundle();
        LoggedInDashboardFragment fragment = new LoggedInDashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentOnForeGround = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());

        try {
            snappyDB = DBFactory.open(getActivity());
            allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
            snappyDB.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard_logged_in, container, false);
        initViews(rootView);
        initializeLoanBars(rootView);
        initializePayoutCard(rootView);
        initializeLeadStatusViews(rootView);
        initializeInAppNotification(rootView);
        setLeaderBoard(rootView);
        setUserDetails();
        initializeDashboardPayoutsFromDB();
        return rootView;
    }

    private void setUserDetails() {
        try {
            snappyDB = DBFactory.open(getActivity());
            if (snappyDB.exists(Constant.DB_USER_DETAILS)) {
                JSONObject jsonObject = new JSONObject(snappyDB.get(Constant.DB_USER_DETAILS));
                setUpNotificationView(jsonObject);
                JSONObject customer = jsonObject.getJSONObject("customer");
                JSONObject bankDetails = jsonObject.getJSONObject("ba_bank");
                String s = bankDetails.optString("account_number");
                String name = customer.optString("name");
                String phone = customer.optString("mobile_number");
                tvUserName.setText(name);
                tvUserMobile.setText(phone);
            }
            snappyDB.close();
        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }


        //call the api
        if (Helper.isNetworkAvailable(getActivity())) {
            getValuesFromServer();
        } else
            Helper.showSnackBar(this, linearLayoutDashboardLoggedUser, getResources().getString(R.string.no_connection), "Retry");

    }

    @Override
    public void initViews(View rootView) {
        linearLayoutDashboardLoggedUser = (LinearLayout) rootView.findViewById(R.id.linearLayoutDashboardLoggedUser);
        toolbar = (Toolbar) rootView.findViewById(R.id.new_toolbar);
        tvUserName = (TextView) rootView.findViewById(R.id.toolbar_user_name);
        tvUserMobile = (TextView) rootView.findViewById(R.id.toolbar_user_phone);
        inAppNotificationTitle = (TextView) rootView.findViewById(R.id.notification_title);
        relativeLayoutNotificationNotification = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNotificationNotification);
        notification_card = rootView.findViewById(R.id.notification_card);



        ToolbarHelper.initializeToolbar(getActivity(), toolbar, null, true, false, false);

        linearLayoutProfileHome = (LinearLayout) rootView.findViewById(R.id.linearLayoutProfileHome);
        toolbar_user_image = (ImageView) rootView.findViewById(R.id.toolbar_user_image);
        imageViewDownloadPdf = (AppCompatImageView) rootView.findViewById(R.id.imageViewDownloadPdf);
        imageViewDownloadPdf.setVisibility(View.VISIBLE);
        imageViewDownloadPdf.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_notification));
        linearLayoutProfileHome.setOnClickListener(this);
        toolbar_user_image.setOnClickListener(this);

        setClickListener();
    }

    @Override
    public void setClickListener() {
        imageViewDownloadPdf.setOnClickListener(this);
    }

    private void getValuesFromServer() {

        JSONObject request = new JSONObject();
        try {
            request.put(Constant.UTOKEN, Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest serverValues = new JsonObjectRequest(Request.Method.POST, URL.GET_CUSTOMER_PROFILE, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null && isFragmentOnForeGround) {
                            try {

                                String status, msg;
                                status = response.getString(Constant.STATUS_CODE);
                                msg = response.getString(Constant.MSG);
                                if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {

                                    JSONObject body, customer, bankDetails;
                                    body = response.getJSONObject(Constant.BODY);

                                    try {
                                        snappyDB = DBFactory.open(getActivity());
                                        snappyDB.put(Constant.DB_USER_DETAILS, body.toString());
                                        snappyDB.close();
                                    } catch (SnappydbException e) {
                                        e.getMessage();
                                    }

                                    setUpNotificationView(body);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        serverValues.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1f));
        queue.add(serverValues);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayoutProfileHome :
                    getActivity().startActivity(new Intent(getActivity(),ProfileActivity.class));
                break;
            case R.id.toolbar_user_image :
                getActivity().startActivity(new Intent(getActivity(),ProfileActivity.class));
                break;
            case R.id.imageViewDownloadPdf :
                getActivity().startActivity(new Intent(getActivity(),ReminderActivity.class));
                break;

        }
    }

    @Override
    public void onSnackbarActionClick() {

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
        if (Helper.isNetworkAvailable(getActivity())) {
            getLeaderBoardFromServer();
        } else
        {
            Toast.makeText(getActivity(),"NO Internet Connection",Toast.LENGTH_LONG).show();
        }

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


        // here set the lead count details from db
        try {
            snappyDB = DBFactory.open(getActivity());
            if (snappyDB.exists(Constant.DB_LEAD_COUNT)) {
                JSONObject jsonObject = new JSONObject(snappyDB.get(Constant.DB_LEAD_COUNT));
                JSONArray leadCountArray = jsonObject.optJSONObject(Constant.BODY).optJSONObject("detail").optJSONArray("data");


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
                snappyDB.close();
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        } catch (JSONException e1) {
            e1.getMessage();
        }

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

    private void getLeaderBoardFromServer() {

        try {
            snappyDB = DBFactory.open(getActivity());
            if (snappyDB.exists(Constant.DB_LEADERBOARD_ARRAY)) {
                leaderBoardJsonArray = new JSONArray(snappyDB.get(Constant.DB_LEADERBOARD_ARRAY));
                leaderBoardAdapterNew.updateData(leaderBoardJsonArray, 3);
            }
            snappyDB.close();
        } catch (JSONException | SnappydbException e) {
            e.printStackTrace();
        }
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

                                    try {
                                        snappyDB = DBFactory.open(getActivity());
                                        snappyDB.put(Constant.DB_LEAD_COUNT, response.toString());
                                        snappyDB.close();
                                    } catch (SnappydbException e) {
                                        e.printStackTrace();
                                    }

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
                                                    case "WITHDRAWN":
                                                        closedCount += count;
                                                        break;
                                                    case "REJECTED":
                                                        closedCount += count;
                                                        break;
                                                    case "CLOSED":
                                                        closedCount += count;
                                                        break;
                                                    case "DISBURSED":
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

                                       /* if (inProgressCount > 0 || approvedCount > 0) {
                                            notificationView.setVisibility(View.VISIBLE);
                                            inAppNotificationTitle.setText("Complete personal details");
                                            btnCheckNotification.setText("Complete");
                                            isBankOrProfileNotification = true;
                                        }*/
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

    private void initializePayoutCard(View rootView) {

        payoutCard = rootView.findViewById(R.id.payout_card);
        if (Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()).equals("")) {
            payoutCard.setVisibility(View.GONE);
        } else {
            payoutCard.setVisibility(View.VISIBLE);
            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.seekbar);
            seekbar = new CircularSeekBar(getActivity());
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

    private void initializeLoanBars(View rootView) {
        seekBarLoan = (AppCompatSeekBar) rootView.findViewById(R.id.loan_seek_bar1);
        relativelayoutSeekbarRoot = (RelativeLayout) rootView.findViewById(R.id.relativelayoutSeekbarRoot);
        popupTextView = (TextView) rootView.findViewById(R.id.text1);
        tabLayoutPayoutCalculator = (TabLayout) rootView.findViewById(R.id.tabLayoutPayoutCalculator);
        textViewbankname1 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankname1);
        textViewbankname2 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankname2);
        textViewbankname3 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankname3);
        textViewbankPrice1 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankPrice1);
        textViewbankPrice2 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankPrice2);
        textViewbankPrice3 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankPrice3);

        textViewbankProduct1 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankProduct1);
        textViewbankProduct2 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankProduct2);
        textViewbankProduct3 = (AppCompatTextView) rootView.findViewById(R.id.textViewbankProduct3);

        textViewbankProduct1.setVisibility(View.VISIBLE);
        textViewbankProduct2.setVisibility(View.VISIBLE);
        textViewbankProduct3.setVisibility(View.VISIBLE);

        txt_see_details_of_payout = (AppCompatTextView) rootView.findViewById(R.id.txt_see_details_of_payout);

        txt_see_details_of_payout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityNew) getActivity()).setTabPosition(2);
            }
        });

//        seekBarLoan.clearFocus();
        seekBarLoan.setOnSeekBarChangeListener(this);

        popupView = rootView.findViewById(R.id.popup_view1);

        relativelayoutSeekbarRoot.setVisibility(View.GONE);

        tabLayoutPayoutCalculator.addOnTabSelectedListener(this);

//        for(int i = 0;i<tabLayoutPayoutCalculator.getTabCount();i++)


        getDataFromDb(selectedTabId);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getProgress() > 0) {
            // setSeekBarAndPopupValue();
        }
        setSeekBarAndPopupValue();
        Log.i(TAG, "onProgressChanged: " + progress);
        changeWeightOfLoanBars(progress * 100000);
        sliderLoanAmount = progress * 100000;
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //  changePayoutTextSize(16);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //  changePayoutTextSize(14);
    }

    private void setSeekBarAndPopupValue() {
        popupTextView.setText("" + seekBarLoan.getProgress() + ",00,000");
        float xOffset = getXPositionOfSeekBar(seekBarLoan);
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.i(TAG, "onTabSelected: tab id " + payLayoutIds[tab.getPosition()] + " position " + tab.getPosition());
        percentage1 = 0.0f;
        percentage2 = 0.0f;
        percentage3 = 0.0f;
        getDataFromDb(payLayoutIds[tab.getPosition()]);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void getDataFromDb(String tabId) {
        selectedTabId = tabId;

        if (tabId.equalsIgnoreCase("11")) {
            relativelayoutSeekbarRoot.setVisibility(View.GONE);
        } else
            relativelayoutSeekbarRoot.setVisibility(View.VISIBLE);

        if (allPayoutJsonArray != null && allPayoutJsonArray.length() > 0) {
            //Log.i(TAG, "getDataFromDb: id "+tabId+allPayoutJsonArray );
            for (int i = 0; i < allPayoutJsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = allPayoutJsonArray.getJSONObject(i);

                    String tab = jsonObject.getString("product_type");
                    //Log.i(TAG, "getDataFromDb: tab id "+tab);
                    if (tabId.equalsIgnoreCase(tab)) {
                        float percentage = Float.parseFloat(jsonObject.getString("payout"));
                        if (percentage1 < percentage) {
                            Log.i(TAG, "getDataFromDb  1234 : " + jsonObject.toString());
                            name3 = name2;
                            percentage3 = percentage2;
                            note3 = note2;

                            name2 = name1;
                            percentage2 = percentage1;
                            note2 = note1;

                            percentage1 = percentage;
                            name1 = jsonObject.getString("name");
                            note1 = jsonObject.getString("note");

                        } else if (percentage2 < percentage) {

                            name3 = name2;
                            percentage3 = percentage2;
                            note3 = note2;

                            percentage2 = percentage;
                            name2 = jsonObject.getString("name");
                            note2 = jsonObject.getString("note");

                        } else if (percentage3 < percentage) {
                            percentage3 = percentage;
                            name3 = jsonObject.getString("name");
                            note3 = jsonObject.getString("note");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.i(TAG, "getDataFromDb: product 1  " + note1 + "  " + note2 + " " + note3);

            // set the values
            if (percentage1 > 0) {
                textViewbankname1.setText(name1);
                if (tabId.equalsIgnoreCase("11")) {
                    textViewbankProduct1.setVisibility(View.VISIBLE);
                    textViewbankPrice1.setText("Rs " + Math.round((percentage1 * 100000) / 100) + "");
                    textViewbankProduct1.setText(note1);
                } else {
                    textViewbankProduct1.setVisibility(View.GONE);
                    textViewbankPrice1.setText("Rs " + Math.round((percentage1 * 5000000) / 100) + "");
                }
            }
            if (percentage2 > 0) {
                textViewbankname2.setText(name2);
                if (tabId.equalsIgnoreCase("11")) {
                    textViewbankProduct2.setVisibility(View.VISIBLE);
                    textViewbankPrice2.setText("Rs " + Math.round((percentage2 * 100000) / 100) + "");
                    textViewbankProduct2.setText(note2);
                } else {
                    textViewbankProduct2.setVisibility(View.GONE);
                    textViewbankPrice2.setText("Rs " + Math.round((percentage2 * 5000000) / 100) + "");
                }
            }
            if (percentage3 > 0) {
                textViewbankname3.setText(name3);
                if (tabId.equalsIgnoreCase("11")) {
                    textViewbankProduct3.setVisibility(View.VISIBLE);
                    textViewbankPrice3.setText("Rs " + Math.round((percentage3 * 100000) / 100) + "");
                    textViewbankProduct3.setText(note3);
                } else {
                    textViewbankProduct3.setVisibility(View.GONE);
                    textViewbankPrice3.setText("Rs " + Math.round((percentage3 * 5000000) / 100) + "");
                }
            }

            if (!tabId.equalsIgnoreCase("11")) {
                changeWeightOfLoanBars(sliderLoanAmount);
            }

        }
    }

    private void changeWeightOfLoanBars(long amount) {
        textViewbankPrice1.setText("Rs " + Math.round((percentage1 * amount)) / 100);
        textViewbankPrice2.setText("Rs " + Math.round((percentage2 * amount)) / 100);
        textViewbankPrice3.setText("Rs " + Math.round((percentage3 * amount)) / 100);

    }

    private void initializeInAppNotification(View rootView) {

        notificationView = rootView.findViewById(R.id.notification_card);
        inAppNotificationTitle = (TextView) rootView.findViewById(R.id.notification_title);
        btnCheckNotification = (TextView) rootView.findViewById(R.id.notification_check_button);

        btnCheckNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(customerNumber))
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + customerNumber));
                    startActivity(callIntent);
                }
            }
        });

        if (showInAppNotification) {
            notificationView.setVisibility(View.VISIBLE);
        } else {
            notificationView.setVisibility(View.GONE);
        }
    }

    private void getMeetingDetailsFromServer() {
        try {

            JSONObject reqObject = new JSONObject();
            reqObject.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            Helper.showLog(URL.MEETING_LIST, reqObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL.MEETING_LIST, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "onResponse: meeting " + response);
                   /* {"status_code":2000,"msg":"Success",
                            "body":{"detail":[{"id":"47","customer_contact":"6886868686","lead_id":"11444","location":"Customer Address",
                            "startDateTime":"0000-00-00 00:00:00","endDateTime":"0000-00-00 00:00:00","customer_name":"dh bfxhycyffu"}]}}*/
                    try {

                        if (response.getString("msg").equals("Success")) {
                            JSONObject jsonObjectBody = response.getJSONObject("body");
                            JSONArray jsonArrayCustomerDetails = jsonObjectBody.getJSONArray("detail");

                            // for date
                            SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("hh:mm:ss");//2016-12-29 12:12
                            Date currentTime = new Date();
                            String strCurrentTime = simpleDateFormatTime.format(currentTime);

                            SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd");//2016-12-29
                            String currentDate = simpleDateFormatDate.format(currentTime);

                            DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

                            Date currentDateTime = dateFormat.parse(strCurrentTime);
                            long minimumDiffTime = 100000000;
                            for (int i = 0; i < jsonArrayCustomerDetails.length(); i++) {
                                JSONObject jsonObjectInner = jsonArrayCustomerDetails.getJSONObject(i);
                                if (jsonObjectInner.optString("startDateTime").contains(currentDate)) {

                                    String meetingTime = jsonObjectInner.optString("startDateTime").substring(12);
                                    Date endDate = dateFormat.parse(meetingTime);

                                    long duration = endDate.getTime() - currentDateTime.getTime();
                                    Log.i(TAG, "onResponse: difference  " + TimeUnit.MILLISECONDS.toMinutes(duration));
                                    if (minimumDiffTime > TimeUnit.MILLISECONDS.toMinutes(duration)) {
                                        Log.i(TAG, "onResponse: difference  get ");
                                        minimumDiffTime = TimeUnit.MILLISECONDS.toMinutes(duration);
                                        notificationView.setVisibility(View.VISIBLE);
                                        Log.i(TAG, "onResponse: difference  get 2");

                                        inAppNotificationTitle.setText("Meeting with : " + jsonObjectInner.optString("customer_name") + " \n" + "Time : " + jsonObjectInner.optString("endDateTime"));
                                        btnCheckNotification.setText("Call");
                                        customerNumber = jsonObjectInner.optString("customer_contact");
                                        inAppNotificationTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.Blue500));
                                        btnCheckNotification.setTextColor(ContextCompat.getColor(getActivity(), R.color.Blue500));
                                        notification_card.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Blue50));

                                        Log.i(TAG, "onResponse: difference  get 3");

                                    }
                                }
                            }
                        } else {

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
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUpNotificationView(JSONObject userDetailsJsonObject) {
        try {
            JSONObject customer, bankDetails;
            customer = userDetailsJsonObject.getJSONObject("customer");
            bankDetails = userDetailsJsonObject.getJSONObject("ba_bank");
            String s = bankDetails.optString("account_number");

            String name = customer.optString("name");
            String phone = customer.optString("mobile_number");

            tvUserName.setText(name);
            tvUserMobile.setText(phone);

            try {
                snappyDB = DBFactory.open(getActivity());
                if (snappyDB.exists(Constant.DB_PROFILE)) {
                    JSONObject jsonObjectProfile = new JSONObject(snappyDB.get(Constant.DB_PROFILE));


                    if ( approvedCount > 0) {
                        if (s == null || s.isEmpty() || s.equals("null")) {
                            isBankOrProfileNotification = true;
                            notificationView.setVisibility(View.VISIBLE);
                            inAppNotificationTitle.setText("Complete Your Profile Now");
                            btnCheckNotification.setText("OK");
                            return;
                        }

                        if (customer.optString("pan").equals("null")
                                || customer.optString("city").equals("null")
                                || customer.optString("aadhar_number").equals("null")
                                || customer.optString("passport_number").equals("null")
                                || customer.optString("profession").equals("null")) {
                            notificationView.setVisibility(View.VISIBLE);
                            inAppNotificationTitle.setText("Complete Your Profile Now");
                            btnCheckNotification.setText("OK");
                            isBankOrProfileNotification = true;
                            return;
                        }
                    }
                }
            }
            catch (SnappydbException e){
                e.printStackTrace();
            }

            /*if (s == null || s.isEmpty() || s.equals("null")) {
                isBankOrProfileNotification = true;
                notificationView.setVisibility(View.VISIBLE);
                inAppNotificationTitle.setText("Complete Your Profile Now");
                btnCheckNotification.setText("OK");
                return;
            }

            if (customer.optString("pan").equals("null")
                    || customer.optString("city").equals("null")
                    || customer.optString("aadhar_number").equals("null")
                    || customer.optString("passport_number").equals("null")
                    || customer.optString("profession").equals("null")) {
                notificationView.setVisibility(View.VISIBLE);
                inAppNotificationTitle.setText("Complete Your Profile Now");
                btnCheckNotification.setText("OK");
                isBankOrProfileNotification = true;
                return;
            }*/
            if (!isBankOrProfileNotification)
                getMeetingDetailsFromServer();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeDashboardPayoutsFromDB() {
        try {
            snappyDB = DBFactory.open(getActivity());

            if (snappyDB.exists(Constant.DB_ALL_PAYOUTS_JSON_ARRAY)) {
                allPayoutJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_PAYOUTS_JSON_ARRAY));
                allStepsJsonArray = new JSONArray(snappyDB.get(Constant.DB_ALL_STEPS_JSON_ARRAY));
                maxPayoutInEachProductTypeJsonArray = getMaxInPayoutArray();
                changeWeightOfLoanBars(seekbar.getProgress() * 100000);
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
}
