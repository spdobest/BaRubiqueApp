package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;

/**
 * Created by disha on 16/3/16.
 */
public class LeaderItemAdapter extends RecyclerView.Adapter<LeaderItemAdapter.ViewHolder> {
    String[] name, join_date, location, amount;
    Context mContext;
    public LeaderItemAdapter(Context mContext, String[] name, String[] join_date, String[] location, String[] amount) {
        this.mContext = mContext;
        this.name = name;
        this.join_date = join_date;
        this.location = location;
        this.amount = amount;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView leader_name, leader_join_date, leader_location, leader_amount;
        public ViewHolder(View v) {
            super(v);
            leader_name = (TextView)v.findViewById(R.id.leader_name);
            leader_join_date = (TextView)v.findViewById(R.id.leader_join_date);
            leader_location = (TextView)v.findViewById(R.id.leader_location);
            leader_amount = (TextView)v.findViewById(R.id.leader_amount);
        }
    }
    @Override
    public LeaderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leader_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.leader_name.setText(name[position]);
        holder.leader_join_date.setText(join_date[position]);
        holder.leader_location.setText(location[position]);
        holder.leader_amount.setText(amount[position]);
    }
    @Override
    public int getItemCount() {
        return name.length;
    }
}
