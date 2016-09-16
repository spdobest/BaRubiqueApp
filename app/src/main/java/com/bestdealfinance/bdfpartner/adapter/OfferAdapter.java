package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.CustomTextViewDomine;
import com.bestdealfinance.bdfpartner.UI.CustomTextViewProxima;

/**
 *
 * Created by disha on 16/3/16.
 */
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    String[] interest, emi, tenure, fees, payout;
    Context mContext;
    public OfferAdapter(Context mContext, String[] interest, String[] emi, String[] tenure, String[] fees, String[] payout) {
        this.mContext = mContext;
        this.interest = interest;
        this.emi = emi;
        this.tenure = tenure;
        this.fees = fees;
        this.payout = payout;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_interest;
        public TextView txt_emi, txt_tenure, txt_fees, txt_payout;
        Button btn_boarding_apply;
        public ViewHolder(View v) {
            super(v);
            txt_interest = (TextView)v.findViewById(R.id.txt_interest);
            txt_emi = (TextView)v.findViewById(R.id.txt_emi);
            txt_tenure = (TextView)v.findViewById(R.id.txt_tenure);
            txt_fees = (TextView)v.findViewById(R.id.txt_fees);
            txt_payout = (TextView)v.findViewById(R.id.txt_payout);
            btn_boarding_apply = (Button)v.findViewById(R.id.btn_boarding_apply);
        }
    }
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_one_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txt_interest.setText(interest[position]);
        holder.txt_emi.setText(emi[position]);
        holder.txt_tenure.setText(tenure[position]);
        holder.txt_fees.setText(fees[position]);
        holder.txt_payout.setText(payout[position]);
    }
    @Override
    public int getItemCount() {
        return interest.length;
    }
}
