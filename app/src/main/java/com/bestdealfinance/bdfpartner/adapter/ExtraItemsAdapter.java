package com.bestdealfinance.bdfpartner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;

public class ExtraItemsAdapter extends RecyclerView.Adapter<ExtraItemsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_PADDING = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private final int mNumItems;
    String[] product_type;

    public ExtraItemsAdapter(String[] product_type_name) {
        mNumItems = product_type_name.length;
        product_type = product_type_name;
    }

    @Override
    public int getItemCount() {
        return mNumItems+2; // We have to add 2 paddings
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == getItemCount()-1) {
            return VIEW_TYPE_PADDING;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_padding, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            // We bind the item to the view
            holder.text.setText(product_type[position]);
            holder.img_prod_type.setImageResource(R.drawable.login_now);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView img_prod_type;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.item_text);
            img_prod_type = (ImageView)itemView.findViewById(R.id.img_prod_type);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!Dishha "+text.getText().toString());
//                    MainActivity.moveCameraToMarker(title.getText().toString());



                }
            });
        }
    }
}
