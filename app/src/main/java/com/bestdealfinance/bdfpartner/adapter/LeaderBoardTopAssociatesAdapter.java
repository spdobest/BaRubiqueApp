package com.bestdealfinance.bdfpartner.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;

public class LeaderBoardTopAssociatesAdapter extends RecyclerView.Adapter<LeaderBoardTopAssociatesAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private Context context;
    String[] name = {"Amit", "Sandeep", "Harshit", "Shailendra", "Ramesh", "Kavita", "Anita", "Jonita", "Henita"};
    String[] joined_month = {"Joined in Sept 2015", "Joined in May 2015", "Joined in June 2015", "Joined in Sept 2015", "Joined in May 2015", "Joined in June 2015", "Joined in Sept 2015", "Joined in May 2015", "Joined in June 2015"};
    String[] city = {"Mumbai", "Delhi", "Banglore", "Mumbai", "Delhi", "Banglore", "Mumbai", "Delhi", "Banglore"};
    String[] points = {"1200", "900", "890", "550", "490", "380", "120", "90", "80"};

    public LeaderBoardTopAssociatesAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.leader_board_list_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvBuyerName.setText(name[position]);
        holder.tvBuyerCity.setText(city[position]);
        holder.tvBuyerJoinedMonth.setText(joined_month[position]);
        holder.tvBuyerPoints.setText(points[position]);
        holder.tvBuyerRanking.setText("" + (position + 1));
    }

    @Override
    public int getItemCount() {
        if (name != null || name.length != 0) {
            return name.length;
        } else {
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvBuyerName, tvBuyerJoinedMonth, tvBuyerPoints, tvBuyerCity, tvBuyerRanking;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvBuyerName = (TextView) itemView.findViewById(R.id.leader_board_user_name);
            tvBuyerJoinedMonth = (TextView) itemView.findViewById(R.id.leader_board_user_joined_date);
            tvBuyerPoints = (TextView) itemView.findViewById(R.id.leader_board_user_points);
            tvBuyerCity = (TextView) itemView.findViewById(R.id.leader_board_user_city);
            tvBuyerRanking = (TextView) itemView.findViewById(R.id.leader_board_user_ranking);
            imageView = (ImageView) itemView.findViewById(R.id.leader_board_user_image);
        }

    }


}
