package com.bestdealfinance.bdfpartner.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.bestdealfinance.bdfpartner.R;

public class PdfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        String url = "https://docs.google.com/gview?embedded=true&url="+getIntent().getStringExtra("link");
        final String title = getIntent().getStringExtra("title");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.Grey200));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        WebView engine = (WebView) findViewById(R.id.pdf_webview);
        engine.getSettings().setJavaScriptEnabled(true);
        engine.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress!=100)
                {
                    toolbar.setTitle("Loading..."+newProgress+"%");
                }
                else
                {
                    toolbar.setTitle(title);
                }

                super.onProgressChanged(view, newProgress);


            }

        });
        engine.loadUrl(url);

    }
}
