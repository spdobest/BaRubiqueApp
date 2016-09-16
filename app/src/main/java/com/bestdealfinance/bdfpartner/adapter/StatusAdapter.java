package com.bestdealfinance.bdfpartner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.model.FeedItemCC;
import com.bestdealfinance.bdfpartner.model.StatusModel;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by vikas on 16/6/16.
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {


    private List<StatusModel> feedItemList;
    private Context mContext;
    public StatusAdapter(Context context, List<StatusModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;

    }

    @Override
    public StatusAdapter.StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_layout,parent,false);
        StatusViewHolder viewHolder = new StatusViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StatusAdapter.StatusViewHolder holder, int position) {
        StatusModel feedItem = feedItemList.get(position);
        holder.name.setText(feedItem.getStatus()+" : ");
        holder.count.setText(feedItem.getCount());
        holder.imageView.setBackgroundColor(Color.parseColor(feedItem.getColor()));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView name;
        protected TextView count;
        public StatusViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.color);
            this.name= (TextView) view.findViewById(R.id.name);
            this.count= (TextView) view.findViewById(R.id.count);


//            this.roi=(TextView) view.findViewById(R.id.interest);

        }
    }
}
