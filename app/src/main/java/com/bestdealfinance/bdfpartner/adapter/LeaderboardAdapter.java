package com.bestdealfinance.bdfpartner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bestdealfinance.bdfpartner.fragment.LeaderFragment;

/**
 * Created by disha on 16/2/16.
 */
public class LeaderboardAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT;
    public LeaderboardAdapter(FragmentManager fm, int PAGE_COUNT) {
        super(fm);
        this.PAGE_COUNT = PAGE_COUNT;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                LeaderFragment myFragment = new LeaderFragment();
                return myFragment;
            case 1:
                LeaderFragment myFragment1 = new LeaderFragment();
                return myFragment1;
            case 2:
                LeaderFragment myFragment2 = new LeaderFragment();
                return myFragment2;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PAGE_COUNT;
    }

}