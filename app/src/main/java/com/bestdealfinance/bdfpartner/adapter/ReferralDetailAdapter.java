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
public class ReferralDetailAdapter extends RecyclerView.Adapter<ReferralDetailAdapter.ViewHolder> {
    String[] status, lead_status, status_char;
    Context mContext;
    public ReferralDetailAdapter(Context mContext, String[] status, String[] lead_status, String[] status_char) {
        this.mContext = mContext;
        this.status = status;
        this.lead_status = lead_status;
        this.status_char = status_char;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView detail_status, detail_lead_status, status_character;
        public ViewHolder(View v) {
            super(v);
            detail_status = (TextView)v.findViewById(R.id.leader_name);
            status_character = (TextView)v.findViewById(R.id.status_character);
            detail_lead_status = (TextView)v.findViewById(R.id.leader_join_date);
        }
    }
    @Override
    public ReferralDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.referral_detail_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.detail_status.setText(status[position]);
        holder.detail_lead_status.setText(lead_status[position]);
        holder.status_character.setText(status_char[position]);
    }
    @Override
    public int getItemCount() {
        return lead_status.length;
    }
}
