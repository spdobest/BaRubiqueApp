package com.bestdealfinance.bdfpartner.FragmentNew;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.ActivityNew.AboutUsActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.ContactUsActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.MeetingActivity;
import com.bestdealfinance.bdfpartner.ActivityNew.ProductsActivity;
import com.bestdealfinance.bdfpartner.ActivityNew.RewardActivity;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.ProfileActivity;
import com.bestdealfinance.bdfpartner.activity.TrainingActivity;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;
import com.bestdealfinance.bdfpartner.base.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MoreFragmentNew extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "MoreFragmentNew";

    Toolbar toolbar;
    TextView txtChooseLanguage, txtTermsOfService;

    // for reward point
    private RelativeLayout relativeLayoutRewardPoint;
    private TextView textViewRewardPoint;
    private ProgressBar progressBarGetRewards;
    // volley declaration
    private RequestQueue requestQueue;

    private String currentDateTime;
    private String emailId, companyId, hashKey;
    private String rewardPoints;
    private TextView txtRewardPoints;

    private LinearLayout linearLayoutProduct_More;
    private LinearLayout linearLayoutMeeting_More;
    private LinearLayout linearLayoutMyProfile_More;
    private LinearLayout linearLayoutTraining_More;
    private LinearLayout linearLayoutShare_More;
    private LinearLayout linearLayoutAboutUs_More;
    private LinearLayout linearLayoutContactus_More;

//    sandbox.xoxoengage.com/home/auth/login?EmailAddress=xxxxxxx&RequestDateTime=xxxxxxxx&CompanyID=xxx&Hash=xxx


    public MoreFragmentNew() {
    }

    public static MoreFragmentNew newInstance() {
        Bundle args = new Bundle();
        MoreFragmentNew fragment = new MoreFragmentNew();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more_new, container, false);
        // initialize the ui widgets here
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(getActivity());

        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//2016-12-29
        Date currentTime = new Date();
        currentDateTime = simpleDateFormatTime.format(currentTime);
        Log.i(TAG, "onCreate: DATE TIME  " + currentDateTime);

    }

    @Override
    public void initViews(View rootView) {
        linearLayoutMeeting_More = (LinearLayout) rootView.findViewById(R.id.linearLayoutMeeting_More);

        relativeLayoutRewardPoint = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRewardPoint);
        textViewRewardPoint = (TextView) rootView.findViewById(R.id.textViewRewardPoint);
        progressBarGetRewards = (ProgressBar) rootView.findViewById(R.id.progressBarGetRewards);


        txtRewardPoints = (TextView) rootView.findViewById(R.id.reward_points);
        txtChooseLanguage = (TextView) rootView.findViewById(R.id.more_choose_language);

        txtTermsOfService = (TextView) rootView.findViewById(R.id.more_terms_of_service);

        linearLayoutProduct_More = (LinearLayout) rootView.findViewById(R.id.linearLayoutProduct_More);
        linearLayoutMyProfile_More = (LinearLayout) rootView.findViewById(R.id.linearLayoutMyProfile_More);
        linearLayoutTraining_More = (LinearLayout) rootView.findViewById(R.id.linearLayoutTraining_More);
        linearLayoutShare_More = (LinearLayout) rootView.findViewById(R.id.linearLayoutShare_More);
        linearLayoutAboutUs_More = (LinearLayout) rootView.findViewById(R.id.linearLayoutAboutUs_More);
        linearLayoutContactus_More = (LinearLayout) rootView.findViewById(R.id.linearLayoutContactus_More);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(getActivity(), toolbar, "MORE", false, false, false);

        if(!Helper.getStringSharedPreference(Constant.USERID,getActivity()).equals(""))
            getRewardAuth(rootView);

        setClickListener();
    }

    @Override
    public void setClickListener() {

        txtTermsOfService.setOnClickListener(this);
        txtChooseLanguage.setOnClickListener(this);
        relativeLayoutRewardPoint.setOnClickListener(this);

        linearLayoutMeeting_More.setOnClickListener(this);
        linearLayoutProduct_More.setOnClickListener(this);
        linearLayoutMyProfile_More.setOnClickListener(this);
        linearLayoutTraining_More.setOnClickListener(this);
        linearLayoutShare_More.setOnClickListener(this);
        linearLayoutAboutUs_More.setOnClickListener(this);
        linearLayoutContactus_More.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutMeeting_More:
                Intent intentMeeting = new Intent(getActivity(), MeetingActivity.class);
                startActivity(intentMeeting);
                break;
            case R.id.linearLayoutProduct_More:
                startActivity(new Intent(getActivity(), ProductsActivity.class));
                break;

            case R.id.linearLayoutMyProfile_More:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;

            case R.id.linearLayoutTraining_More:
                startActivity(new Intent(getActivity(), TrainingActivity.class));
                break;
            case R.id.linearLayoutShare_More:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Constant.SHARE_TEXT);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.more_terms_of_service:

                break;
            case R.id.linearLayoutContactus_More:
                startActivity(new Intent(getActivity(), ContactUsActivityNew.class));
                break;
            case R.id.linearLayoutAboutUs_More:
                startActivity(new Intent(getActivity(), AboutUsActivityNew.class));
                break;
            case R.id.more_choose_language:

                break;

            case R.id.relativeLayoutRewardPoint:
                Intent intentReward = new Intent(getActivity(), RewardActivity.class);
                intentReward.putExtra("reward", rewardPoints);
                //intentReward.putExtra("email",Helper.getStringSharedPreference(Constant.USERID,getActivity())+"@rubique.com");
                intentReward.putExtra("email","5434@rubique.com");
                getActivity().startActivity(intentReward);
                break;
        }
    }

    private void getRewardAuth(final View rootView) {

        //final String email = Helper.getStringSharedPreference(Constant.USERID,getActivity())+"@rubique.com";
        final String email = "5434@rubique.com";
        //final String email = "amit.nishad@rubique.com";
        String emailBase64Encoded = null;
        try {
            emailBase64Encoded = Base64.encodeToString(email.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        currentDateTime = format1.format(date);
        Log.i(TAG, "getRewardAuth: "+currentDateTime);

        if (Helper.isNetworkAvailable(getActivity())) {
            final String finalEmailBase64Encoded = emailBase64Encoded;
            StringRequest getRequest = new StringRequest(Request.Method.POST, URL.GIFTXOXO_AUTH,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, "onResponse REWARD : " + response.toString());
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getString("success").equals("true"))
                                {
                                    rootView.findViewById(R.id.relativeLayoutRewardPoint).setVisibility(View.VISIBLE);
                                }
                                    //getRewardPoints(object.getString("token"), finalEmailBase64Encoded);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", "");
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> reqObject = new HashMap<String, String>();
                    reqObject.put("email", finalEmailBase64Encoded);
                    reqObject.put("req_date_time", currentDateTime);
                    reqObject.put("client_id", Constant.GIFTXOXO_CLIENT_ID);
                    reqObject.put("hash", Helper.md5(Constant.GIFTXOXO_SECRET_KEY + email + Constant.GIFTXOXO_CLIENT_ID + currentDateTime));

                    Log.i(TAG, "getParams: "+reqObject.toString());
                    return reqObject;
                }
            };
            getRequest.setShouldCache(false);
            getRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 2, 1f));
            requestQueue.add(getRequest);

        }
    }

    private void getRewardPoints(final String token, final String email) {

        if (getActivity() != null && Helper.isNetworkAvailable(getActivity())) {

            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL.GET_REWARD_POINTS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        relativeLayoutRewardPoint.setVisibility(View.VISIBLE);
                        txtRewardPoints.setText(object.getString("remaining"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "onResponse: " + response);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> reqObject = new HashMap<String, String>();
                    reqObject.put("token", token);
                    reqObject.put("email", email);
                    return reqObject;
                }

            };
            requestQueue.add(jsonObjectRequest);
        }
    }

}
