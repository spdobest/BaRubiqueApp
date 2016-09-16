package com.bestdealfinance.bdfpartner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bestdealfinance.bdfpartner.R;

import java.util.Arrays;

public class Product_Type extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Bundle product_type_id=new Bundle();




    public Product_Type() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_product_type, container, false);

        ImageView back_arrow = (ImageView)view.findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        CardView cc= (CardView) view.findViewById(R.id.cc_product);
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_type_id.putString("type","11");
                Select_product fragment1 = new Select_product();
                fragment1.setArguments(product_type_id);
                android.support.v4.app.FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);
                manager.replace(R.id.fragmnet, fragment1).commit();
            }
        });
        CardView pl= (CardView) view.findViewById(R.id.pl_product);
        pl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_type_id.putString("type","25");
                Select_product fragment1 = new Select_product();
                fragment1.setArguments(product_type_id);
                android.support.v4.app.FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);
                manager.replace(R.id.fragmnet, fragment1).commit();
            }
        });
        CardView hl= (CardView) view.findViewById(R.id.hl_product);
        hl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_type_id.putString("type","26");
                Select_product fragment1 = new Select_product();
                fragment1.setArguments(product_type_id);
                android.support.v4.app.FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);
                manager.replace(R.id.fragmnet, fragment1).commit();
            }
        });
        CardView lap= (CardView) view.findViewById(R.id.lap_product);
        lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_type_id.putString("type","28");
                Select_product fragment1 = new Select_product();
                fragment1.setArguments(product_type_id);
                android.support.v4.app.FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);
                manager.replace(R.id.fragmnet, fragment1).commit();
            }
        });
        CardView cl_product= (CardView) view.findViewById(R.id.cl_product);
        cl_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_type_id.putString("type","22");
                Select_product fragment1 = new Select_product();
                fragment1.setArguments(product_type_id);
                android.support.v4.app.FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);
                manager.replace(R.id.fragmnet, fragment1).commit();
            }
        });
        CardView tw_product= (CardView) view.findViewById(R.id.tw_product);
        tw_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_type_id.putString("type","23");
                Select_product fragment1 = new Select_product();
                fragment1.setArguments(product_type_id);
                android.support.v4.app.FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);
                manager.replace(R.id.fragmnet, fragment1).commit();
            }
        });
        CardView bl= (CardView) view.findViewById(R.id.bl_product);
        bl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_type_id.putString("type","39");
                Select_product fragment1 = new Select_product();
                fragment1.setArguments(product_type_id);
                android.support.v4.app.FragmentTransaction manager = getFragmentManager().beginTransaction().addToBackStack(null);
                manager.replace(R.id.fragmnet, fragment1).commit();
            }
        });
        int x[]=new int[3];
        int j=x.length;
        
        return view;
    }


}





