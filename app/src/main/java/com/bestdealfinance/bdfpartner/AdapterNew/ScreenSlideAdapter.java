package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.ActivityNew.WelcomeActivityNew;
import com.bestdealfinance.bdfpartner.R;

import java.util.ArrayList;

public class ScreenSlideAdapter extends PagerAdapter {
    public static final int SLIDING_TYPE_IMAGE = 1;
    public static final int SLIDING_TYPE_TEXT_IMAGE = 2;
    private final LayoutInflater inflater;
    private ImageView imageView;
    private TextView tv1;
    private TextView tv2;
    private int slidingType = 0;
    Context context;

    TypedArray arrayWelcomeImages;
    String[] arrayTitle;
    String[] arrayMessage;

    public ScreenSlideAdapter(Context context,int sliding_type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.slidingType = sliding_type;
        if(slidingType == SLIDING_TYPE_TEXT_IMAGE){
            arrayWelcomeImages = context.getResources().obtainTypedArray(R.array.array_welcome_images);
            arrayTitle = context.getResources().getStringArray(R.array.array_welcome_title);
            arrayMessage = context.getResources().getStringArray(R.array.array_welcome_message);
        }
        else{
            arrayWelcomeImages = context.getResources().obtainTypedArray(R.array.array_landing_images);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return arrayWelcomeImages.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View layout = inflater.inflate(R.layout.viewpager_welcome, container, false);
        imageView = (ImageView) layout.findViewById(R.id.image);
        tv1 = (TextView) layout.findViewById(R.id.text1);
        tv2 = (TextView) layout.findViewById(R.id.text2);

        if(slidingType == SLIDING_TYPE_TEXT_IMAGE){
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);

            tv1.setText(arrayTitle[position]);
            tv2.setText(arrayMessage[position]);
        }
        else{
            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        imageView.setImageResource(arrayWelcomeImages.getResourceId(position, -1));

        container.addView(layout);
        return layout;
    }
}