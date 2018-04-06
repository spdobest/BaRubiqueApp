package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by rbq on 10/20/16.
 */

public class BankAndPayoutAdapter extends RecyclerView.Adapter<BankAndPayoutAdapter.ViewHolder> {
    private static final String TAG = "BankAndPayoutAdapter";
    private Context context;
    private LayoutInflater inflater;
    private JSONArray payoutsArray;
    private JSONObject stepObject;
    private int product_type;
    private int step;
    private double payoutsPercent;

    public BankAndPayoutAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public BankAndPayoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_bank_and_payout, parent, false);
        return new BankAndPayoutAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BankAndPayoutAdapter.ViewHolder holder, int position) {

        holder.bankName.setText(payoutsArray.optJSONObject(position).optString("name"));
        if(product_type==11)
        {
            holder.bankName.setVisibility(View.GONE);
            holder.bankPayout.setText("Rs "+Math.round((Math.round(payoutsArray.optJSONObject(position).optDouble("payout")*payoutsPercent)/100.00)*1000));
            holder.bankSlab.setVisibility(View.GONE);
            holder.textViewBankNote.setVisibility(View.VISIBLE);
            holder.textViewBankNote.setText(payoutsArray.optJSONObject(position).optString("note"));
        }
        else{
            holder.bankName.setVisibility(View.VISIBLE);
            holder.bankPayout.setText((Math.round(payoutsArray.optJSONObject(position).optDouble("payout")*payoutsPercent)/100.00) + " %");
            holder.bankSlab.setVisibility(View.VISIBLE);
            holder.textViewBankNote.setVisibility(View.GONE);
        }

        Glide.with(context).load(payoutsArray.optJSONObject(position).optString("logo_url")).into(holder.bankLogo);

        if (payoutsArray.optJSONObject(position).optString("slab_max").equals("999999999")) {
            holder.bankSlab.setText("( > " + payoutsArray.optJSONObject(position).optString("slab_min") + " Lac )");
        } else
            holder.bankSlab.setText("(" + payoutsArray.optJSONObject(position).optString("slab_min") + " Lac - " + payoutsArray.optJSONObject(position).optString("slab_max") + " Lac)");

    }

    @Override
    public int getItemCount() {
        if (payoutsArray != null && payoutsArray.length() > 0) {
            return payoutsArray.length();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView bankSlab;
        private ImageView bankLogo;
        private TextView bankName;
        private TextView bankPayout;
        private TextView textViewBankNote;

        public ViewHolder(View itemView) {
            super(itemView);
            bankName = (TextView) itemView.findViewById(R.id.bank_name);
            bankLogo = (ImageView) itemView.findViewById(R.id.bank_logo);
            bankPayout = (TextView) itemView.findViewById(R.id.bank_payout);
            bankSlab = (TextView) itemView.findViewById(R.id.bank_slab);
            textViewBankNote = (TextView) itemView.findViewById(R.id.textViewBankNote);
        }
    }

    public void updateData(JSONArray payoutsArray, JSONObject stepObject, int step,int product_type) {
        Log.i(TAG, "onUpdatePayout: callback 2  ");
        this.payoutsArray = payoutsArray;
        this.stepObject = stepObject;
        this.product_type = product_type;
        if(stepObject!=null) {
            double referralPayout = Integer.parseInt(stepObject.optString("step1"));
            double appFillPayout = Integer.parseInt(stepObject.optString("step2"));
            double docPickPayout = Integer.parseInt(stepObject.optString("step3"));
            double disbursePayout = Integer.parseInt(stepObject.optString("step4"));

            Log.i(TAG, "updateData: "+referralPayout+"\n"+appFillPayout+" \n"+docPickPayout+"\n "+disbursePayout);

        this.step = step;
        payoutsPercent = 0;
        switch (step) {
            case 4:
                payoutsPercent += disbursePayout;
            case 3:
                payoutsPercent += docPickPayout;
            case 2:
                payoutsPercent += appFillPayout;
            case 1:
                payoutsPercent += referralPayout;
            default:
                payoutsPercent += 0;
                break;
        }
        }
        notifyDataSetChanged();
    }
}
