package com.bestdealfinance.bdfpartner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.UI.CustomTextViewDomine;

/**
 * Created by disha on 23/2/16.
 */
public class PhilosophyAdapter extends RecyclerView.Adapter<PhilosophyAdapter.ViewHolder> {
    String[] tag, content;
    public PhilosophyAdapter(String[] tag, String[] content) {
        this.tag = tag;
        this.content = content;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CustomTextViewDomine philosophy_tag, philosophy_content;
        public ViewHolder(View v) {
            super(v);
            philosophy_tag = (CustomTextViewDomine)v.findViewById(R.id.philosophy_tag);
            philosophy_content = (CustomTextViewDomine)v.findViewById(R.id.philosophy_content);
        }
    }
    @Override
    public PhilosophyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.philosophy_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.philosophy_tag.setText(tag[position]);
        holder.philosophy_content.setText(content[position]);
    }
    @Override
    public int getItemCount() {
        return tag.length;
    }
}
