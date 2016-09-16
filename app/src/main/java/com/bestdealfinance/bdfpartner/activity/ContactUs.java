package com.bestdealfinance.bdfpartner.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bestdealfinance.bdfpartner.R;


public class ContactUs extends AppCompatActivity implements View.OnClickListener {

    private View layoutCall, layoutSMS, layoutEmail;
    private String smsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layoutCall = findViewById(R.id.contact_call);
        layoutSMS = findViewById(R.id.contact_sms);
        layoutEmail = findViewById(R.id.contact_email);
        layoutCall.setOnClickListener(this);
        layoutSMS.setOnClickListener(this);
        layoutEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.contact_call) {
            makeCall();
        } else if (id == R.id.contact_sms) {
            showMessageDialog();
        } else if (id == R.id.contact_email) {
            sendEmail();
        }
    }

    private void makeCall() {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getString(R.string.enquiry_number)));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContactUs.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        startActivity(intent);
    }

    private void showMessageDialog() {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", "56070");
        smsIntent.putExtra("sms_body","RBQ <CC>");
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

            // other 'case' lines to check for other
            // permissions this app might request
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
