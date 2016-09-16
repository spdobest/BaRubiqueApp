package com.bestdealfinance.bdfpartner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class GetNotifyActivity extends AppCompatActivity {
    private boolean close = false;

    @Override
    public void onResume() {
        super.onResume();
        if (close) {
            //  finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_about_us);
        final String appPackageName = getPackageName();
        Intent one = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        one.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent two = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        two.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // getPackageName() from Context or Activity object
        try {
            close = true;
            startActivity(one);
            finish();
        } catch (android.content.ActivityNotFoundException anfe) {
            close = true;
            startActivity(two);
            finish();
        }

    }


}
