package com.bestdealfinance.bdfpartner.ActivityNew;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bestdealfinance.bdfpartner.FragmentNew.ReferFragmentNew;
import com.bestdealfinance.bdfpartner.R;

public class ProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_new);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.product_activity_main_layout, ReferFragmentNew.newInstance(false), "ReferFragmentNew")
                .commit();

    }
}
