package com.bestdealfinance.bdfpartner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bestdealfinance.bdfpartner.fragment.DashboardFragment;
import com.bestdealfinance.bdfpartner.fragment.PayoutFragment;
import com.bestdealfinance.bdfpartner.fragment.ReferralFragment;

/**
 * Created by disha on 16/2/16.
 */
public class DashboardAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT;
    public DashboardAdapter(FragmentManager fm, int PAGE_COUNT) {
        super(fm);
        this.PAGE_COUNT = PAGE_COUNT;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                DashboardFragment myFragment = new DashboardFragment();
                return myFragment;
            case 1:
                ReferralFragment myFragment1 = new ReferralFragment();
                return myFragment1;
            case 2:
                PayoutFragment myFragment2 = new PayoutFragment();
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