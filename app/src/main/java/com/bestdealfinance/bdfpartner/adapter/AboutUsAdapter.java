package com.bestdealfinance.bdfpartner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bestdealfinance.bdfpartner.fragment.AboutUsContent;
import com.bestdealfinance.bdfpartner.fragment.AboutUsPhilosophy;
import com.bestdealfinance.bdfpartner.fragment.AboutUsTeam;

/**
 * Created by disha on 16/2/16.
 */
public class AboutUsAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT;
    public AboutUsAdapter(FragmentManager fm, int PAGE_COUNT) {
        super(fm);
        this.PAGE_COUNT = PAGE_COUNT;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                AboutUsContent myFragment = new AboutUsContent();
                return myFragment;
            case 1:
                AboutUsPhilosophy myFragment1 = new AboutUsPhilosophy();
                return myFragment1;
            case 2:
                AboutUsTeam myFragment2 = new AboutUsTeam();
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