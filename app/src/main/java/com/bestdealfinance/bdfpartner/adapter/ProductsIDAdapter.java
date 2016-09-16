package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.Logs;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.fragment.Select_product;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsIDAdapter extends RecyclerView.Adapter<ProductsIDAdapter.ViewHolder> {
    ArrayList<HashMap<String, String>> tlist;
    private static Select_product.OnRecycleClickListener listener;
    List<String>imglist;
    Context context;
    String product_type_id;
    public ProductsIDAdapter(Context con,ArrayList<HashMap<String, String>> list,List<String>imglist, String type,Select_product.OnRecycleClickListener itemListener) {
        this.tlist = list;
        this.context=con;
        this.product_type_id=type;
        this.imglist=imglist;
        this.listener=itemListener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView product_name;
        public ImageView image;
        public ViewHolder(View v) {
            super(v);
            product_name = (TextView)v.findViewById(R.id.product_name);
            image= (ImageView) v.findViewById(R.id.bank_logo);
            product_name.setOnClickListener(this);
            image.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Logs.LogD("Clicked","Item Clicked");
            int position  =   getAdapterPosition();
            listener.onItemClick(position);

        }
    }
    @Override
    public ProductsIDAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products, parent, false);
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
}