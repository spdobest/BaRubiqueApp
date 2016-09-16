package com.bestdealfinance.bdfpartner.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.KnowMore;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bumptech.glide.Glide;

public class Register_know_Fragment extends Fragment {

    Button btn_regi_continue;
    ImageView img_regi_banner;
    private TextView heading;
    private TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_regi_know, container, false);


        heading=(TextView)view.findViewById(R.id.heading);
        message=(TextView)view.findViewById(R.id.message);
        btn_regi_continue = (Button)view.findViewById(R.id.btn_regi_continue);
        img_regi_banner = (ImageView)view.findViewById(R.id.img_regi_banner);
        btn_regi_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KnowMore.mPager.setCurrentItem(1);
            }
        });

        Glide.with(getActivity())
                .load(R.drawable.register_bg)
                .into(img_regi_banner);

        return view;



    }





}
