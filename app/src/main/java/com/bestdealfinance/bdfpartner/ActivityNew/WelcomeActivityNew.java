package com.bestdealfinance.bdfpartner.ActivityNew;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.ScreenSlideAdapter;
import com.bestdealfinance.bdfpartner.R;

import java.util.ArrayList;

public class WelcomeActivityNew extends AppCompatActivity implements View.OnClickListener {

    private RequestQueue queue;
    private Button startButton;
    private ViewPager viewPager;
    private ImageView imageViewWelcomeIndicatorSliding1;
    private ImageView imageViewWelcomeIndicatorSliding2;
    private ImageView imageViewWelcomeIndicatorSliding3;
    private Button skipButton;
    private int currentPagerPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_new);

        queue = Volley.newRequestQueue(this);
        initializeView();
    }


    private void initializeView() {

        startButton = (Button) findViewById(R.id.start_earning);
        skipButton = (Button) findViewById(R.id.skip);
        imageViewWelcomeIndicatorSliding1 = (ImageView) findViewById(R.id.imageViewWelcomeIndicatorSliding1);
        imageViewWelcomeIndicatorSliding2 = (ImageView) findViewById(R.id.imageViewWelcomeIndicatorSliding2);
        imageViewWelcomeIndicatorSliding3 = (ImageView) findViewById(R.id.imageViewWelcomeIndicatorSliding3);
        // onclick listener
        startButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);
        viewPager.setAdapter(new ScreenSlideAdapter(WelcomeActivityNew.this,ScreenSlideAdapter.SLIDING_TYPE_TEXT_IMAGE));
        //set the current page is zero
        changePagerImage(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        currentPagerPosition = 0;
                        changePagerImage(currentPagerPosition);
                        break;
                    case 1:
                        currentPagerPosition = 1;
                        changePagerImage(currentPagerPosition);
                        break;
                    case 2:
                        currentPagerPosition = 2;
                        changePagerImage(currentPagerPosition);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changePagerImage(int currentPosition) {
        switch (currentPosition) {
            case 0:
                startButton.setText("Next");
                imageViewWelcomeIndicatorSliding1.setImageResource(R.drawable.ic_indicator_dark);
                imageViewWelcomeIndicatorSliding2.setImageResource(R.drawable.ic_indicator_light);
                imageViewWelcomeIndicatorSliding3.setImageResource(R.drawable.ic_indicator_light);
                break;
            case 1:
                startButton.setText("Next");
                imageViewWelcomeIndicatorSliding2.setImageResource(R.drawable.ic_indicator_dark);
                imageViewWelcomeIndicatorSliding3.setImageResource(R.drawable.ic_indicator_light);
                viewPager.setCurrentItem(currentPosition);
                break;
            case 2:
                imageViewWelcomeIndicatorSliding3.setImageResource(R.drawable.ic_indicator_dark);
                viewPager.setCurrentItem(currentPosition);
                startButton.setText("Start Earning");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_earning:
                currentPagerPosition++;
                if (currentPagerPosition > 2) {
                    changePagerImage(2);
                    startActivity(new Intent(WelcomeActivityNew.this, MainActivityNew.class));
                    finish();
                } else
                    changePagerImage(currentPagerPosition);

                break;
            case R.id.skip:
                startActivity(new Intent(WelcomeActivityNew.this, MainActivityNew.class));
                finish();
                break;
        }
    }
}
