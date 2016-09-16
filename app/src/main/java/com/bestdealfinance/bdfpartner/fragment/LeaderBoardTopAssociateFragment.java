package com.bestdealfinance.bdfpartner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.adapter.LeaderBoardTopAssociatesAdapter;

public class LeaderBoardTopAssociateFragment extends Fragment {

    private RecyclerView recyclerViewTopAssociates, recyclerViewTopReferrals;
    private LeaderBoardTopAssociatesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader_board, container, false);

        recyclerViewTopAssociates = (RecyclerView) rootView.findViewById(R.id.leader_board_recycler_view_top_associates);
        recyclerViewTopReferrals = (RecyclerView) rootView.findViewById(R.id.leader_board_recycler_view_top_referrals);
        adapter = new LeaderBoardTopAssociatesAdapter(getActivity());
        recyclerViewTopAssociates.setAdapter(adapter);
        recyclerViewTopAssociates.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopReferrals.setAdapter(adapter);
        recyclerViewTopReferrals.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;

    }
}
