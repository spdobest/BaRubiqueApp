package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.fragment.SelectedProductFragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsIDAdapter extends RecyclerView.Adapter<ProductsIDAdapter.ViewHolder> {
    private static SelectedProductFragment.OnRecycleClickListener listener;
    ArrayList<HashMap<String, String>> tlist;
    List<String> imglist;
    Context context;
    String product_type_id;

    public ProductsIDAdapter(Context con, ArrayList<HashMap<String, String>> list, List<String> imglist, String type, SelectedProductFragment.OnRecycleClickListener itemListener) {
        this.tlist = list;
        this.context = con;
        this.product_type_id = type;
        this.imglist = imglist;
        listener = itemListener;
    }

    @Override
    public ProductsIDAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_products, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.product_name.setText(tlist.get(position).get("name"));
        Glide.with(context).load(imglist.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return tlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView product_name;
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            product_name = v.findViewById(R.id.product_name);
            image = v.findViewById(R.id.bank_logo);
            product_name.setOnClickListener(this);
            image.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            listener.onItemClick(position);
        }
    }
}