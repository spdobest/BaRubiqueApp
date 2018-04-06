package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.FragmentNew.CongratulationFragment;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Harshit.Gupta on 03-Oct-16.
 */

public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.ViewHolder> {

    private Context context;
    private Bundle productBundle;
    private LayoutInflater inflater;
    private JSONArray productArray = null;

    private JSONObject productCountObject = null;
    private JSONObject productPayoutObject;
    private boolean isNewLead = false;
    JSONObject productObject;
    private JSONObject productInfo;

    private String productApplied;

    public OfferListAdapter(Context context, Bundle productBundle) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.productBundle = productBundle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_offer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        productObject = productArray.optJSONObject(position);
        JSONObject disbursalList = productObject.optJSONObject("disbursal_field_list");
        productInfo = productObject.optJSONObject("product_info");

        holder.tvProductName.setText(productInfo.optString("product_name", ""));
        holder.bottomBar.setBackgroundColor(Helper.getBankColor(productInfo.optString("finbank_id","0")));

        productApplied = productInfo.optString("product_name", "");

        if (productBundle.getString("product_type_sought").equals("11")) {
            holder.loanLayout.setVisibility(View.GONE);
            holder.loan2Layout.setVisibility(View.GONE);
            holder.ccLayout.setVisibility(View.VISIBLE);
            holder.tvInterestRate.setVisibility(View.INVISIBLE);
            holder.tvAtTheRateIcon.setVisibility(View.INVISIBLE);

            if (disbursalList.optJSONObject("183") != null) {
                holder.tvAnnualFees.setText("Rs " + disbursalList.optJSONObject("183").optString("value"));
            } else
                holder.tvAnnualFees.setText("Rs 0");

            if (disbursalList.optJSONObject("184") != null) {
                holder.tvRenewalFees.setText("Rs " + disbursalList.optJSONObject("184").optString("value"));
            } else
                holder.tvRenewalFees.setText("Rs 0");


        } else {
            holder.ccLayout.setVisibility(View.GONE);
            holder.loanLayout.setVisibility(View.VISIBLE);
            holder.loan2Layout.setVisibility(View.VISIBLE);
            holder.tvInterestRate.setVisibility(View.VISIBLE);
            holder.tvAtTheRateIcon.setVisibility(View.VISIBLE);

            if (disbursalList.optJSONObject("165") != null) {
                holder.tvInterestRate.setText("" + Math.round(disbursalList.optJSONObject("165").optDouble("value") * 10000) / 100F + "%");
            } else
                holder.tvInterestRate.setText("0%");

            if (disbursalList.optJSONObject("166") != null) {
                holder.tvProcessing.setText("Rs " + disbursalList.optJSONObject("166").optString("value"));
            } else
                holder.tvProcessing.setText("--");

            if (disbursalList.optJSONObject("167") != null) {
                holder.tvEmi.setText("Rs " + Helper.formatRs("" + Math.round(disbursalList.optJSONObject("167").optDouble("value"))));
            } else
                holder.tvEmi.setText("Rs 0");

            if (disbursalList.optJSONObject("116") != null) {
                holder.tvTenure.setText("" + disbursalList.optJSONObject("116").optString("value") + " months");
            } else
                holder.tvTenure.setText("0 months");

            holder.tvEligibleAmount.setText(Helper.formatRs(productBundle.getString("amount")));

        }

        if (productInfo.optInt("stage", 0) >= 3) {
            holder.tvStage3Text.setTextColor(context.getResources().getColor(R.color.Green500));
            holder.tvStage3Icon.setImageResource(R.drawable.ic_check);
            holder.tvStage3Icon.setColorFilter(context.getResources().getColor(R.color.Green500));
        } else {
            holder.tvStage3Text.setTextColor(context.getResources().getColor(R.color.Red500));
            holder.tvStage3Icon.setImageResource(R.drawable.ic_close);
            holder.tvStage3Icon.setColorFilter(context.getResources().getColor(R.color.Red500));
        }

        if (productInfo.optInt("stage", 0) == 4) {
            holder.tvStage4Text.setTextColor(context.getResources().getColor(R.color.Green500));
            holder.tvStage4Icon.setImageResource(R.drawable.ic_check);
            holder.tvStage4Icon.setColorFilter(context.getResources().getColor(R.color.Green500));
        } else {
            holder.tvStage4Text.setTextColor(context.getResources().getColor(R.color.Red500));
            holder.tvStage4Icon.setImageResource(R.drawable.ic_close);
            holder.tvStage4Icon.setColorFilter(context.getResources().getColor(R.color.Red500));
        }


        Glide.with(context).load(productInfo.optString("img_url")).into(holder.bankLogo);

        holder.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(productInfo.optInt("stage", 0) >= 3) {
                    openCongradulationPage();
                }else{
                    String dialogMessage = "";
                    if(productInfo.optInt("stage", 0) < 3)
                        dialogMessage = "Doc Pick up is not Available , Do you want to proceed ?";
                    else if(productInfo.optInt("stage", 0) < 4)
                        dialogMessage = "Disburse is not Available , Do you want to proceed ?";


                    Helper.showAlertDialog(context, "", dialogMessage, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            openCongradulationPage();
                        }
                    });
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (productArray == null) return 0;
        else
            return productArray.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView bankLogo;
        private final TextView tvInterestRate;
        private final Button btnApply;
        private final TextView tvEligibleAmount;
        private final TextView tvProcessing;
        private final TextView tvEmi;
        private final TextView tvTenure;
        private final ImageView tvStage1Icon;
        private final TextView tvStage1Text;
        private final ImageView tvStage2Icon;
        private final TextView tvStage2Text;
        private final ImageView tvStage3Icon;
        private final TextView tvStage3Text;

        private  ImageView tvStage4Icon;
        private  TextView tvStage4Text;

        private final TextView tvDetail;
        private final TextView tvProductName;
        private final TextView tvAnnualFees;
        private final TextView tvRenewalFees;
        private final RelativeLayout ccLayout;
        private final RelativeLayout loanLayout;
        private final RelativeLayout loan2Layout;
        private final TextView tvAtTheRateIcon;
        private final View bottomBar;

        public ViewHolder(View itemView) {
            super(itemView);

            ccLayout = (RelativeLayout) itemView.findViewById(R.id.cc);
            loanLayout = (RelativeLayout) itemView.findViewById(R.id.loan);
            loan2Layout = (RelativeLayout) itemView.findViewById(R.id.loan2);

            bankLogo = (ImageView) itemView.findViewById(R.id.bank_logo);
            tvInterestRate = (TextView) itemView.findViewById(R.id.interest_rate);
            tvAtTheRateIcon = (TextView) itemView.findViewById(R.id.at_the_rate_icon);
            btnApply = (Button) itemView.findViewById(R.id.apply_button);
            tvDetail = (TextView) itemView.findViewById(R.id.view_detail_button);
            tvProductName = (TextView) itemView.findViewById(R.id.product_name);
            tvEligibleAmount = (TextView) itemView.findViewById(R.id.eligible_amount);
            tvProcessing = (TextView) itemView.findViewById(R.id.processing);
            tvEmi = (TextView) itemView.findViewById(R.id.emi);
            tvTenure = (TextView) itemView.findViewById(R.id.tenure);
            tvAnnualFees = (TextView) itemView.findViewById(R.id.annual_fees);
            tvRenewalFees = (TextView) itemView.findViewById(R.id.renewal);
            tvStage1Icon = (ImageView) itemView.findViewById(R.id.stage1_icon);
            tvStage1Text = (TextView) itemView.findViewById(R.id.stage1_text);
            tvStage2Icon = (ImageView) itemView.findViewById(R.id.stage2_icon);
            tvStage2Text = (TextView) itemView.findViewById(R.id.stage2_text);
            tvStage3Icon = (ImageView) itemView.findViewById(R.id.stage3_icon);
            tvStage3Text = (TextView) itemView.findViewById(R.id.stage3_text);

            tvStage4Icon = (ImageView) itemView.findViewById(R.id.stage4_icon);
            tvStage4Text = (TextView) itemView.findViewById(R.id.stage4_text);

            bottomBar = itemView.findViewById(R.id.bottom_bar);

        }
    }

    public void updateProduct(JSONArray data) {
        this.productArray = data;
        notifyDataSetChanged();
    }

    private void openCongradulationPage(){
        if(productObject!=null) {
            productBundle.putString("product_id", productObject.optString("product_id"));
            productBundle.putString("product_name",productInfo.optString("product_name", ""));
            productBundle.putString("force_select", "0");
            if (productObject.optJSONArray("more_criteria_list").length() > 0) {
                productBundle.putString("more_criteria_list", productObject.optJSONArray("more_criteria_list").toString());

            } else {
                productBundle.putString("more_criteria_list", "");

            }

            CongratulationFragment aNew = new CongratulationFragment();
            productBundle.putString("product_name", productApplied);
            aNew.setArguments(productBundle);
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.lead_flow_fragment_layout, aNew, "AppfillRemainingFragment").commit();
        }
    }
}
