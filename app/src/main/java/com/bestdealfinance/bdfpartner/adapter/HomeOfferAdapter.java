package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.Product_Landing;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

/**
 * Created by vikas on 7/4/16.
 */
public class HomeOfferAdapter extends PagerAdapter {
    List<String>type1;
    List<String>id1;
    List<String>image1;
    Context mContext;
    LayoutInflater mLayoutInflater;
    public HomeOfferAdapter(Context context,List<String>type,List<String>id,List<String>image ) {
        mContext = context;
        this.type1=type;
        this.id1=id;
        this.image1=image;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return image1.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Glide.with(mContext)
                .load(image1.get(position))
                .into(imageView);

        container.addView(itemView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.LogD("Click","BoomBoom");
                Bundle bundle1=new Bundle();
                bundle1.putString("type",type1.get(position));
                bundle1.putString("id",id1.get(position));
                Logs.LogD("Bundle",bundle1.toString());
                mContext.startActivity(new Intent(mContext, Product_Landing.class).putExtras(bundle1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
