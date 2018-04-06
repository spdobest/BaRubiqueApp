package com.bestdealfinance.bdfpartner.AdapterNew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.ActivityNew.DocumentListActivity;
import com.bestdealfinance.bdfpartner.ActivityNew.SelectRfcActivityNew;
import com.bestdealfinance.bdfpartner.R;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Harshit.Gupta on 03-Oct-16.
 */

public class RfcListAdapter extends RecyclerView.Adapter<RfcListAdapter.ViewHolder> {

    private Context context;
    private Bundle productBundle;
    private LayoutInflater inflater;
    private JSONArray productArray = null;

    private JSONObject productCountObject = null;
    private JSONObject productPayoutObject;
    private boolean isNewLead = false;
    private String address;

    String lat,lang;

    public RfcListAdapter(Context context, Bundle productBundle) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.productBundle = productBundle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_nearby_rfc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final JSONObject productObject = productArray.optJSONObject(position);
        holder.tvRfcName.setText(productObject.optString("name"));
        holder.tvRfcAddress.setText(productObject.optString("address"));
        address = productObject.optString("address");
        holder.tvRfcCity.setText(productObject.optString("city"));
        holder.tvRfcDistance.setText("Distance :"+Math.round(productObject.optDouble("distance"))/1000F+" km");

        try {
            lat = productObject.getString("lat");
            lang = productObject.getString("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + productObject.getString("lat") + "," + productObject.getString("lng")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);
            }
        });

        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DocumentListActivity.class);
                productBundle.putString("rfc_id",productObject.optString("id","0"));
                productBundle.putString("address", holder.tvRfcAddress.getText().toString()+","+holder.tvRfcCity.getText().toString());
                productBundle.putString("lat",lat);
                productBundle.putString("lang",lang);
                intent.putExtra("productBundle",productBundle);
                ((Activity)context).finish();
                context.startActivity(intent);
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


        private final TextView tvRfcName;
        private final TextView tvRfcAddress;
        private final TextView tvRfcCity;
        private final TextView tvRfcDistance;
        private final Button btnSelect;
        private final Button btnDirection;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRfcName = (TextView) itemView.findViewById(R.id.rfc_name);
            tvRfcAddress = (TextView) itemView.findViewById(R.id.rfc_address);
            tvRfcCity = (TextView) itemView.findViewById(R.id.rfc_city);
            tvRfcDistance = (TextView) itemView.findViewById(R.id.rfc_distance);
            btnSelect = (Button)itemView.findViewById(R.id.btn_select);
            btnDirection=(Button)itemView.findViewById(R.id.btn_directions);


        }
    }

    public void updateProduct(JSONArray data) {
        this.productArray = data;
        notifyDataSetChanged();
    }


}
