package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bumptech.glide.Glide;

/**
 * Created by disha on 23/2/16.
 */
public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {
    String[] member_img, name, content1, content2;
    Context mContext;
    public TeamAdapter(Context mContext, String[] member_img, String[] name, String[] content1, String[] content2) {
        this.mContext = mContext;
        this.member_img = member_img;
        this.name = name;
        this.content1 = content1;
        this.content2 = content2;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView member_content1, member_content2;
        public TextView member_name;
        public ImageView member_image;
        public ViewHolder(View v) {
            super(v);
            member_image = (ImageView)v.findViewById(R.id.member_image);
            member_name = (TextView)v.findViewById(R.id.member_name);
            member_content1 = (TextView)v.findViewById(R.id.member_content1);
            member_content2 = (TextView)v.findViewById(R.id.member_content2);
        }
    }

//    // Provide a suitable constructor (depends on the kind of dataset)
//    public ReferralAdapter(ArrayList<HashMap<String,String>> myDataset) {
//        mDataset = myDataset;
//    }

    // Create new views (invoked by the layout manager)
    @Override
    public TeamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Glide.with(mContext).load(member_img[position]).into(holder.member_image);
//        holder.member_image.setImageBitmap(Util.getBitmapFromURL(member_img[position]));
        holder.member_name.setText(name[position]);
        holder.member_content1.setText(content1[position]);
        holder.member_content2.setText(content2[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return name.length;
    }
}
