package com.bestdealfinance.bdfpartner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;

/**
 * Created by disha on 23/2/16.
 */
public class BoardingOfferAdapter extends RecyclerView.Adapter<BoardingOfferAdapter.ViewHolder> {
    String[] emi, interest, tenure, fees, bank_url, eligible_amount;
    public BoardingOfferAdapter(String[] emi, String[] interest,String[] tenure, String[] fees, String[] bank_url, String[] eligible_amount) {
        this.emi = emi;
        this.interest = interest;
        this.tenure = tenure;
        this.fees = fees;
        this.bank_url = bank_url;
        this.eligible_amount = eligible_amount;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ofr_emi, ofr_interest, ofr_tenure, ofr_fees, ofr_bank_url, ofr_eligible_amount;
        public ViewHolder(View v) {
            super(v);
            ofr_emi = (TextView)v.findViewById(R.id.ofr_emi);
            ofr_interest = (TextView)v.findViewById(R.id.ofr_interest);
            ofr_tenure= (TextView)v.findViewById(R.id.ofr_tenure);
            ofr_fees= (TextView)v.findViewById(R.id.ofr_fees);
//            ofr_bank_url= (TextView)v.findViewById(R.id.ofr_bank_url);
            ofr_eligible_amount= (TextView)v.findViewById(R.id.ofr_eligible_amount);
        }
    }
    @Override
    public BoardingOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.boarding_offer_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ofr_emi.setText(emi[position]);
        holder.ofr_interest.setText(interest[position]);
        holder.ofr_tenure.setText(tenure[position]);
        holder.ofr_fees.setText(fees[position]);
        holder.ofr_bank_url.setText(bank_url[position]);
        holder.ofr_eligible_amount.setText(eligible_amount[position]);
    }
    @Override
    public int getItemCount() {
        return emi.length;
    }
}
