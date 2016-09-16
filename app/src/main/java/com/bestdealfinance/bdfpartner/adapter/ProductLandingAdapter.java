package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.model.LandingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikas on 18/3/16.
 */
public class ProductLandingAdapter extends RecyclerView.Adapter<ProductLandingAdapter.ViewHolder> {

    private List<LandingModel> items;
    private Context mcontext;

    public ProductLandingAdapter(Context context) {
        this.mcontext=context;
        items=new ArrayList<>();

    }

    private int itemLayout;

    public ProductLandingAdapter(Context context, List<LandingModel> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
        this.mcontext=context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        LandingModel item = items.get(position);
        holder.text.setText(item.getText());
        holder.itemView.setTag(item);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.rowIcon);
            text = (TextView) itemView.findViewById(R.id.rowText);
        }
    }
}