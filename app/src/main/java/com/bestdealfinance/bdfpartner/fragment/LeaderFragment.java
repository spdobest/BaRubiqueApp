package com.bestdealfinance.bdfpartner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.LeaderItemAdapter;

public class LeaderFragment extends Fragment {

    RecyclerView recyclerview_earning, recyclerview_refferal, recyclerview_disbursal;
    RecyclerView.LayoutManager mLayoutManagerEarning, mLayoutManagerReferral, mLayoutManagerDisbursal;
    LeaderItemAdapter earningAdapter, referralAdapter, disbursalAdapter;
    String[] name = {"Ramesh", "Kavita", "Anita"}, join_date = {"Joined at Sept 2015", "Joined at May 2015", "Joined at June 2015"};
    String[] location = {"Mumbai", "Mumbai", "Mumbai"}, amount = {"Rs, 1,00,000", "Rs, 90,000", "Rs, 80,000"};
    String[] referral = {"120", "90", "80"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leader, container, false);

        initialization(view);

        mLayoutManagerEarning = new LinearLayoutManager(getActivity());
        recyclerview_earning.setHasFixedSize(true);
        recyclerview_earning.setLayoutManager(mLayoutManagerEarning);

        earningAdapter = new LeaderItemAdapter(getActivity(), name, join_date, location, amount);
        recyclerview_earning.setAdapter(earningAdapter);

        mLayoutManagerReferral = new LinearLayoutManager(getActivity());
        recyclerview_refferal.setHasFixedSize(true);
        recyclerview_refferal.setLayoutManager(mLayoutManagerReferral);

        referralAdapter = new LeaderItemAdapter(getActivity(), name, join_date, location, referral);
        recyclerview_refferal.setAdapter(referralAdapter);

        mLayoutManagerDisbursal = new LinearLayoutManager(getActivity());
        recyclerview_disbursal.setHasFixedSize(true);
        recyclerview_disbursal.setLayoutManager(mLayoutManagerDisbursal);

        disbursalAdapter = new LeaderItemAdapter(getActivity(), name, join_date, location, amount);
        recyclerview_disbursal.setAdapter(disbursalAdapter);

        return view;
    }

    private void initialization(View view) {
        recyclerview_earning = (RecyclerView)view.findViewById(R.id.recyclerview_earning);
        recyclerview_refferal = (RecyclerView)view.findViewById(R.id.recyclerview_refferal);
        recyclerview_disbursal = (RecyclerView)view.findViewById(R.id.recyclerview_disbursal);
    }
}
