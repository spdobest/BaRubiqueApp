package com.bestdealfinance.bdfpartner.database;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.bestdealfinance.bdfpartner.R;

/**
 * Created by disha on 9/3/16.
 */
public class Otp extends Activity
{

    static EditText OtpNumber;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp);
        OtpNumber = findViewById(R.id.txtName);
    }
    public void recivedSms(String message)
    {
        try
        {
            OtpNumber.setText(message);
        }
        catch (Exception e)
        {
        }
    }

}
