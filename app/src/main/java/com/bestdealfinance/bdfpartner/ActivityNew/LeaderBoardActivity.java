package com.bestdealfinance.bdfpartner.ActivityNew;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.FragmentNew.LeaderBoardFragmentNew;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;

import org.json.JSONArray;
import org.json.JSONException;

public class LeaderBoardActivity extends AppCompatActivity {


    private JSONArray leaderBoardJsonArray;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ToolbarHelper.initializeToolbar(this, toolbar, "Leaderboard", false, true, true);

        Intent intent = getIntent();
        String jsonArray = intent.getStringExtra("leaderBoardJsonArray");

        try {
            leaderBoardJsonArray = new JSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        LeaderBoardFragmentNew leaderBoardFragmentNew = new LeaderBoardFragmentNew();

        if (leaderBoardJsonArray != null && leaderBoardJsonArray.length() > 0) {
            Bundle bundle = new Bundle();
            bundle.putString("leaderBoardJsonArray", leaderBoardJsonArray.toString());
            leaderBoardFragmentNew.setArguments(bundle);
        }

        fragmentTransaction.replace(R.id.leader_board_main_layout, leaderBoardFragmentNew, "LeaderBoardFragmentNew");
        fragmentTransaction.commit();
    }
}
