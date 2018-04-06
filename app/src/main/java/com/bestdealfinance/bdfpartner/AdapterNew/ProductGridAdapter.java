package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestdealfinance.bdfpartner.ActivityNew.LeadFlowActivityNew;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.fragment.SelectedProductFragment;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Harshit.Gupta on 03-Oct-16.
 */

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> {
    private static final String TAG = "ProductGridAdapter";
    private Context context;
    private LayoutInflater inflater;
    private JSONArray productArray = null;

    private JSONObject productCountObject = null;
    private JSONObject productPayoutObject;
    private boolean isNewLead = false;

    public ProductGridAdapter(Context context, boolean isNewLead) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.isNewLead = isNewLead;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_product_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final JSONObject productObject = productArray.optJSONObject(position);
        String count = productCountObject != null ? productCountObject.optString(productObject.optString("id"), "0") : "0";
        Double payout = productPayoutObject != null ? productPayoutObject.optDouble(productObject.optString("id"), 0) : 0;

        Log.i(TAG, "onBindViewHolder: "+payout);
        
        switch (productObject.optString("id")) {
            case "11":
                Glide.with(context).load(R.drawable.ic_product_credit_card).into(holder.productIcon);
                break;
            case "22":
                Glide.with(context).load(R.drawable.ic_product_car_loan).into(holder.productIcon);
                break;
            case "23":
                Glide.with(context).load(R.drawable.ic_product_two_wheeler_loan).into(holder.productIcon);
                break;
            case "24":
                Glide.with(context).load(R.drawable.ic_product_credit_card).into(holder.productIcon);
                break;
            case "25":
                Glide.with(context).load(R.drawable.ic_product_personal_loan).into(holder.productIcon);
                break;
            case "26":
                Glide.with(context).load(R.drawable.ic_product_home_loan).into(holder.productIcon);
                break;
            case "28":
                Glide.with(context).load(R.drawable.ic_product_loan_against_property).into(holder.productIcon);
                break;
            case "29":
                Glide.with(context).load(R.drawable.ic_product_car_loan).into(holder.productIcon);
                break;
            case "51":
                Glide.with(context).load(R.drawable.ic_product_life_insurance).into(holder.productIcon);
                break;
            case "52":
                Glide.with(context).load(R.drawable.ic_product_general_insurance).into(holder.productIcon);
                break;
            case "53":
                Glide.with(context).load(R.drawable.ic_product_health_insurance).into(holder.productIcon);
                break;
            case "31":
                Glide.with(context).load(R.drawable.ic_product_equipment).into(holder.productIcon);
                break;
            default:
                Glide.with(context).load(R.drawable.ic_product_business_loan).into(holder.productIcon);
                break;
        }


        holder.productName.setText(productObject.optString("name"));
        holder.productCount.setText(count + " Offers");
        holder.productPayout.setVisibility(View.INVISIBLE);
        if (productObject.optString("id").equals("11")) {
            holder.productPayout.setText("*upto Rs " + Math.round(payout*1000));
            holder.productCount.setText(count + " Offers");
        } else {
            holder.productPayout.setText("*upto " + payout + " %");

        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (productCountObject == null) {
                    Toast.makeText(context, "Network connection error!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (productCountObject.optInt(productObject.optString("id")) > 0) {
                    if (isNewLead) {
                        Bundle bundle = new Bundle();
                        bundle.putString("product_type_sought", productObject.optString("id"));
                        bundle.putString("incomplete","0");
                        bundle.putString("product_type_name", productObject.optString("name"));
                        context.startActivity(new Intent(context, LeadFlowActivityNew.class).putExtra("bundle", bundle));
                    } else {

                        Bundle bundle = new Bundle();
                        bundle.putString("type", productObject.optString("id"));
                        bundle.putString("name", productObject.optString("name"));
                        SelectedProductFragment selectedProductFragment = new SelectedProductFragment();
                        selectedProductFragment.setArguments(bundle);
                        FragmentTransaction manager = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().addToBackStack(null);
                        manager.replace(R.id.product_activity_main_layout, selectedProductFragment, "SelectedProductFragment").commit();
                    }
                } else {
                    Toast.makeText(context, "No Products Available", Toast.LENGTH_LONG).show();
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
        private final ImageView productIcon;
        private final TextView productName;
        private final TextView productCount;
        private final TextView productPayout;
        private final LinearLayout layout;
        private RelativeLayout relativeLayoutRootProductItem;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.grid_layout);
            productIcon = (ImageView) itemView.findViewById(R.id.product_icon);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productCount = (TextView) itemView.findViewById(R.id.product_number);
            productPayout = (TextView) itemView.findViewById(R.id.product_payout);
            relativeLayoutRootProductItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutRootProductItem);


        }
    }

    public void updatePayoutDetails(JSONObject body) {
        try {
            JSONArray jsonArray = body.optJSONArray("products");
            productArray = new JSONArray();
            if(jsonArray!=null && jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String[] visible_to = jsonArray.getJSONObject(i).optString("visible_to", "").split("|");

                    for (String v : visible_to) {
                        if (v.equals("A")) {
                            productArray.put(jsonArray.getJSONObject(i));
                        }
                    }
                }


            JSONArray productCount = body.getJSONArray("count");
            JSONArray productPayout = body.getJSONArray("payout");

            productCountObject = new JSONObject();
            productPayoutObject = new JSONObject();
            try {
                for (int i = 0; i < productCount.length(); i++)
                    productCountObject.put(productCount.getJSONObject(i).optString("product_type_id"), productCount.getJSONObject(i).optString("count"));

                for (int i = 0; i < productPayout.length(); i++)
                    productPayoutObject.put(productPayout.getJSONObject(i).optString("product_type"), Math.round(productPayout.getJSONObject(i).optDouble("payout") * 100.0) / 100.0);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
