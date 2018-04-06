package com.bestdealfinance.bdfpartner.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;

public class PdfActivity extends AppCompatActivity {

    String url;
    File direct;
    private AppCompatImageView imageViewDownloadPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        imageViewDownloadPdf = (AppCompatImageView) findViewById(R.id.imageViewDownloadPdf);

        imageViewDownloadPdf.setVisibility(View.VISIBLE);

        url = "https://docs.google.com/gview?embedded=true&url=" + getIntent().getStringExtra("link");
        final String title = getIntent().getStringExtra("title");
        Log.i("TAG", "onCreate: " + url);

        String name = Environment.getExternalStorageDirectory().getAbsolutePath();
        direct = new File(name + "/Rubique");
        /*if(!direct.exists()) {
            if(direct.mkdir());
        }*/
        Log.i("FILE ", "onCreate: " + direct.toString());

        imageViewDownloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(getIntent().getStringExtra("link"));
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, title, false, true, true);

        WebView engine = (WebView) findViewById(R.id.pdf_webview);
        engine.getSettings().setJavaScriptEnabled(true);

        final ProgressDialog progressDialog = new ProgressDialog(PdfActivity.this);

        engine.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 90) {
                    progressDialog.setMessage("Loading - " + newProgress + "%");
                } else {
                    progressDialog.hide();
                }
                super.onProgressChanged(view, newProgress);
            }

        });

        engine.loadUrl(url);
        progressDialog.setMessage("Loading - 0%");
        progressDialog.show();

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("PDF Show Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void downloadFile(String pdfFileUrl) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfFileUrl));
        request.setDescription("Rubique Pdf Downloading");
        String file_name = pdfFileUrl.substring(pdfFileUrl.lastIndexOf("/") + 1);
        request.setTitle(file_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
//        request.setDestinationInExternalPublicDir(direct.getAbsolutePath(), file_name);
        request.setDestinationInExternalFilesDir(this,/*Environment.DIRECTORY_DOWNLOADS*/direct.getAbsolutePath(), file_name);
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
