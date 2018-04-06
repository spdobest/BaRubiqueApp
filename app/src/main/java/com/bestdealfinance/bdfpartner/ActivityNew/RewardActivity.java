package com.bestdealfinance.bdfpartner.ActivityNew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.bestdealfinance.bdfpartner.application.URL;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RewardActivity extends AppCompatActivity {

    //widgets declaration
    private RelativeLayout relativeLayoutGetReward;
    private LinearLayout rootView;
    private AppCompatButton buttonGetRewaard;
    private AppCompatTextView textViewRewardname;
    private WebView webViewReward;
    private Toolbar toolbar;

    String rewardPoint;
    private String email;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, "Meeting", false, true, true);
        Intent intentBundle = getIntent();
        if (intentBundle.hasExtra("reward"))
            rewardPoint = intentBundle.getStringExtra("reward");
        if (intentBundle.hasExtra("email"))
            email = intentBundle.getStringExtra("email");


        initView();
        loadWebview();

    }

    private void loadWebview() {
        if (Helper.isNetworkAvailable(this)) {
            String url = URL.GIFTXOXO_REDIRECT;
//            String email = "amit.nishad@rubique.com";
            String emailBase64Encoded = null;
            try {
                emailBase64Encoded = Base64.encodeToString(email.getBytes("UTF-8"), Base64.NO_WRAP);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cal.getTimeInMillis()+120000);
            Date date = cal.getTime();
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00");
            String currentDateTime = format1.format(date);
            url += "?EmailAddress=" + emailBase64Encoded + "&RequestDateTime=" + currentDateTime + "&CompanyID=" + Constant.GIFTXOXO_CLIENT_ID + "&Hash=" + Helper.md5(Constant.GIFTXOXO_SECRET_KEY + email + Constant.GIFTXOXO_CLIENT_ID + currentDateTime);
            webViewReward.setVisibility(View.VISIBLE);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.txt_loading) + "...");
            progressDialog.setTitle("0%");
            progressDialog.show();
            webViewReward.setWebViewClient(new WebViewClient());
            webViewReward.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress != 100) {
                        progressDialog.setTitle("" + newProgress + "%");
                    } else {
                        progressDialog.dismiss();
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });
            webViewReward.clearCache(true);
            webViewReward.getSettings().setJavaScriptEnabled(true);
            webViewReward.loadUrl(url);
        } else {
            Helper.showSnackbarMessage(rootView, "No internet Connection", Snackbar.LENGTH_SHORT);
        }
    }

    private void initView() {
        rootView = (LinearLayout) findViewById(R.id.activity_reward);
        webViewReward = (WebView) findViewById(R.id.webViewReward);
        toolbar = (Toolbar) rootView.findViewById(R.id.new_toolbar);
        ToolbarHelper.initializeToolbar(RewardActivity.this, toolbar, "Reward", false, true, true);
    }


}
