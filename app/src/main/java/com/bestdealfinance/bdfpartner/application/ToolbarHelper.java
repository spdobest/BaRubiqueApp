package com.bestdealfinance.bdfpartner.application;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;

public class ToolbarHelper {

    public static void initializeToolbar(final Activity activity, Toolbar toolbar, String title, boolean isDashBoardFragment, boolean showBackButton, final boolean isActivity) {

        //Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_new);
        View dashBoardLayout = toolbar.findViewById(R.id.toolbar_account_layout);
        View otherActivityLayout = toolbar.findViewById(R.id.toolbar_activity_layout);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_activity_title);
        ImageButton backButton = (ImageButton) toolbar.findViewById(R.id.toolbar_back_button);
        if (isDashBoardFragment) {
            otherActivityLayout.setVisibility(View.GONE);
            dashBoardLayout.setVisibility(View.VISIBLE);
        } else {
            otherActivityLayout.setVisibility(View.VISIBLE);
            if (showBackButton) {
                backButton.setVisibility(View.VISIBLE);
            } else {
                backButton.setVisibility(View.GONE);
            }
            dashBoardLayout.setVisibility(View.GONE);
            toolbarTitle.setText(title);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActivity) {
                    activity.finish();
                } else {
                    FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    }
                }
            }
        });
    }

}
