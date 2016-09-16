package com.bestdealfinance.bdfpartner.adapter;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.ReferralDetailActivity;
import com.bestdealfinance.bdfpartner.application.Util;
import com.bestdealfinance.bdfpartner.model.ReferralListModel;
import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.List;
import java.util.Random;

/**
 * Created by disha on 23/2/16.
 */
public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.ViewHolder> {
    private List<ReferralListModel> referralLists;
    Context mContext;
    private static final Random RANDOM = new Random();
    private int[] material_colors;
    int curr_clr;

    public ReferralAdapter(Context mContext, List<ReferralListModel> referralList) {
        this.mContext = mContext;
        this.referralLists = referralList;
        this.material_colors = mContext.getResources().getIntArray(R.array.colors);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView payout, referral_product_type, referral_phone, referral_name, referral_lead_state, loan_key, loan_value, eligible_amount, loan_eligible, txtdate, referral_id;
        public CardView card_view;
        public MaterialLetterIcon profile_image;

        public ViewHolder(View v) {
            super(v);
            payout = (TextView) v.findViewById(R.id.payout);
            card_view = (CardView) v.findViewById(R.id.card_view);
            txtdate = (TextView) v.findViewById(R.id.textViewdate);
            referral_id = (TextView) v.findViewById(R.id.referral_id);
            referral_product_type = (TextView) v.findViewById(R.id.referral_product_type);
            referral_phone = (TextView) v.findViewById(R.id.referral_phone);
            referral_name = (TextView) v.findViewById(R.id.referral_name);
            referral_lead_state = (TextView) v.findViewById(R.id.referral_lead_state);
//            loan_key = (TextView) v.findViewById(R.id.loan_key);
//            loan_value = (TextView) v.findViewById(R.id.loan_value);
            eligible_amount = (TextView) v.findViewById(R.id.loan_eligible);
            loan_eligible = (TextView) v.findViewById(R.id.loan_eligible);
            profile_image = (MaterialLetterIcon) v.findViewById(R.id.profile_image);
        }
    }

    @Override
    public ReferralAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.referral_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ReferralListModel model = referralLists.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        curr_clr = material_colors[RANDOM.nextInt(material_colors.length)];
        holder.profile_image.setInitials(true);
        holder.profile_image.setLettersNumber(1);
        holder.profile_image.setShapeColor(curr_clr);
        try {
            holder.profile_image.setLetter(model.getName());
        }
        catch (Exception e){
            holder.profile_image.setLetter("NA");
        }

        holder.referral_id.setText("ID: " + model.getRefID());
        holder.txtdate.setText(model.getDate_created());
        holder.referral_product_type.setText(model.getProduct_name());
        holder.referral_phone.setText(model.getPhone());
        holder.referral_name.setText(model.getName());
//        if (model.getPayout().equals("-1")||model.getPayout().equals("null")){
//            holder.payout.setText("Disbursal Pending");
//        }
//        else{
        String state=model.getLead_state();
        if (state.equals("Closed")|| state.equals("Failed")){
            holder.payout.setText("Not Applicable");
        }
        else if(model.getPayout().equals("0")){
            holder.payout.setText("Not Applicable");
        }
        else {
            if (state==null ||state.equals("")){
                holder.payout.setText("Not Applicable");
            }
            else {
                holder.payout.setText("Up to " + mContext.getResources().getString(R.string.rupee_symbol) + " " + Util.parseRs(model.getPayout()));
            }

        }
//        }

        holder.referral_lead_state.setText(model.getLead_state());
//        if(!loan_key[position].equals("0")) {
//            holder.loan_key.setText("Rs."+loan_key[position]);
//        } else {
//            holder.loan_key.setVisibility(View.GONE);
//        }
//        if(!loan_value[position].equals("0")) {
//            holder.loan_value.setText("Rs."+loan_value[position]);
//        } else {
//            holder.loan_value.setVisibility(View.GONE);
//        }
        if (model.getLoan_eligible() == null) {
            holder.eligible_amount.setVisibility(View.GONE);
            holder.loan_eligible.setVisibility(View.GONE);

        } else {
            try {
                if (model.getProduct_type().equals("11")) {
                    holder.eligible_amount.setVisibility(View.GONE);
                    holder.loan_eligible.setVisibility(View.GONE);
                } else {
                    holder.loan_eligible.setText(mContext.getResources().getString(R.string.rupee_symbol) + " " + Util.parseRs(model.getLoan_eligible()));
                }
            } catch (Exception e) {
                //DO Nothing
            }

        }
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ReferralDetailActivity.class);
                i.putExtra("lead_id", model.getLead_id());
                mContext.startActivity(i);
            }
        });
        holder.referral_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(android.os.Build.VERSION.SDK_INT >= 23) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(mContext., new String[]{Manifest.permission.CALL_PHONE},
//                                0);
                            return;
                        }
                    }
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + model.getPhone()));
                    mContext.startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return referralLists.size();
    }
}
