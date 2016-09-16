package com.bestdealfinance.bdfpartner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.fragment.Landing_CC;
import com.bestdealfinance.bdfpartner.fragment.Landing_Loan;
import com.bestdealfinance.bdfpartner.fragment.Product_Type;

public class Product_Landing extends AppCompatActivity {
    private static String product_type;
    private static String product_id;
    Bundle product_type_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_landing);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null) {
            if (!bundle.getString("type", "").equals("")) {
                String type = bundle.getString("type");
                String product_id = bundle.getString("id");
                android.support.v4.app.FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
                if (type != null) {
                    Logs.LogD("recieved", bundle.toString());
                    if (bundle.getString("type").equals("11")) {
                        //TODO For Credit Card Fragment
                        Landing_CC fragment = new Landing_CC();
                        fragment.setArguments(bundle);
                        manager.replace(R.id.fragmnet, fragment).commit();
                    } else {
                        //TODO For Loans Fragment.
                        Landing_Loan fragment = new Landing_Loan();
                        fragment.setArguments(bundle);
                        manager.replace(R.id.fragmnet, fragment).commit();
                    }
                }
            }
        } else {
            Product_Type fragment1 = new Product_Type();
            fragment1.setArguments(product_type_id);
            android.support.v4.app.FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
            manager.replace(R.id.fragmnet, fragment1).commit();
        }


    }

}
