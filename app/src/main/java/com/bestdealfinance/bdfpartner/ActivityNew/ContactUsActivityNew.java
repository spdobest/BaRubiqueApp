package com.bestdealfinance.bdfpartner.ActivityNew;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class ContactUsActivityNew extends AppCompatActivity implements View.OnClickListener {

    private View layoutCall, layoutEmail;
    private String smsText;
//    private LinearLayout layoutSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_new);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, getResources().getString(R.string.txt_contact_us), false, true, true);


        layoutCall = findViewById(R.id.contact_call);
        layoutEmail = findViewById(R.id.contact_email);
        layoutCall.setOnClickListener(this);
        layoutEmail.setOnClickListener(this);

        Tracker mTracker = Helper.getDefaultTracker(this);
        mTracker.setScreenName("Contact Us Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.contact_call) {
            makeCall();
        }/* else if (id == R.id.contact_sms) {
            showMessageDialog();
        }*/ else if (id == R.id.contact_email) {
            sendEmail();
        }
    }

    private void makeCall() {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.enquiry_number)));
        startActivity(intent);
    }

    private void showMessageDialog() {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", "56070");
        smsIntent.putExtra("sms_body", "RBQ <CC>");
        startActivity(smsIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                    // permission was granted, yay!
                } else {
                    // permission denied
                    Snackbar.make(layoutCall, "Not permitted to make a call from this app", Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void sendEmail() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.enquiry_email)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Rubique Support Team");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email to Rubique"));
        } catch (ActivityNotFoundException ex) {
            Snackbar.make(layoutCall, "There is no email client installed.", Snackbar.LENGTH_LONG).show();
        }
    }
}
