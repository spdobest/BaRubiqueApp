package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import com.bestdealfinance.bdfpartner.R;

import java.util.List;

/**
 * Created by siba.prasad on 12-01-2017.
 */


public class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private List<Fragment> listFragment;

    public HomeFragmentPagerAdapter(Context context, FragmentManager fragmentManager, List<Fragment> listFragment){
        super(fragmentManager);
        this.context = context;
        this.listFragment = listFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }
}