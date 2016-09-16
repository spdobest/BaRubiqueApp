package com.bestdealfinance.bdfpartner.fragment;

import android.content.Intent;
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
import com.bestdealfinance.bdfpartner.activity.HomeActivity;
import com.bestdealfinance.bdfpartner.activity.RegisterActivity;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bumptech.glide.Glide;

public class Earn_know_Fragment extends Fragment {

    Button btn_earn_continue;
    ImageView img_earn_banner;
    private TextView heading;
    private TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earn_know, container, false);
        heading=(TextView)view.findViewById(R.id.heading);
        message=(TextView)view.findViewById(R.id.message);
        btn_earn_continue = (Button)view.findViewById(R.id.btn_earn_continue);
        img_earn_banner = (ImageView)view.findViewById(R.id.img_earn_banner);

        btn_earn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
                getActivity().finish();
            }
        });



        Glide.with(getActivity())
                .load(R.drawable.earn_bg)
                .into(img_earn_banner);

        return view;
    }


}
