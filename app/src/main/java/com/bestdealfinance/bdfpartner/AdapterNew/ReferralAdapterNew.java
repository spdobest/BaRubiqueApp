package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;


public class ReferralAdapterNew extends RecyclerView.Adapter<ReferralAdapterNew.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public ReferralAdapterNew(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ReferralAdapterNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_all_lead, parent, false);
        return new ReferralAdapterNew.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReferralAdapterNew.ViewHolder holder, int position) {


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public int getItemCount() {
        //TODO
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productIcon;
        private final TextView productName;
        private final TextView productCount;
        private final TextView productPayout;
        private final LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.grid_layout);
            productIcon = (ImageView) itemView.findViewById(R.id.product_icon);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productCount = (TextView) itemView.findViewById(R.id.product_number);
            productPayout = (TextView) itemView.findViewById(R.id.product_payout);


        }
    }
}
