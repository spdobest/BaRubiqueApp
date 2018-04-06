package com.bestdealfinance.bdfpartner.ActivityNew;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.TouchImageView;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;

public class AboutUsActivityNew extends AppCompatActivity  {

    private String url = "https://www.rubique.com/about-us";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_new);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, "About Us", false, true, true);
    }
}
