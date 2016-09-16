package com.bestdealfinance.bdfpartner.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.fragment.LeaderBoardTopAssociateFragment;

public class LeaderBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LeaderBoardTopAssociateFragment leaderBoardTopAssociateFragment = new LeaderBoardTopAssociateFragment();
        fragmentTransaction.replace(R.id.leader_board_main_layout, leaderBoardTopAssociateFragment, "GenderFragment");
        fragmentTransaction.commit();
    }
}
