package com.bestdealfinance.bdfpartner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bestdealfinance.bdfpartner.fragment.Earn_know_Fragment;
import com.bestdealfinance.bdfpartner.fragment.Refer_know_Fragment;
import com.bestdealfinance.bdfpartner.fragment.Register_know_Fragment;

/**
 * Created by disha on 1/3/16.
 */
public class TestFragmentAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Register_know_Fragment myFragment = new Register_know_Fragment();
                return myFragment;
            case 1:
                Refer_know_Fragment myFragment1 = new Refer_know_Fragment();
                return myFragment1;
            case 2:
                Earn_know_Fragment myFragment2 = new Earn_know_Fragment();
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