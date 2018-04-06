package com.bestdealfinance.bdfpartner.AdapterNew;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;

import org.json.JSONArray;

public class LeaderBoardAdapterNew extends RecyclerView.Adapter<LeaderBoardAdapterNew.MyViewHolder> {

    private final LayoutInflater inflater;
    private Context context;
    private JSONArray leaderBoardArray;
    private int numberOfTopBA;

    public LeaderBoardAdapterNew(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_leader_board, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String name = leaderBoardArray.optJSONObject(position).optString("name");

        String city = leaderBoardArray.optJSONObject(position).optString("city");
        String leadCount = leaderBoardArray.optJSONObject(position).optString("lead_count");

        if (city != null && city != "null" && !city.isEmpty()) {
            holder.userCity.setText(city);
        }

        holder.userName.setText(msskString(name));
        holder.userLeadCount.setText(leadCount);
        holder.ranking.setText("" + (position + 1));

        // TODO later holder.userImage.setImageDrawable();
    }

    @Override
    public int getItemCount() {
        if (leaderBoardArray != null && leaderBoardArray.length() > 0) {
            if (numberOfTopBA > 0 && numberOfTopBA < leaderBoardArray.length()) {
                return numberOfTopBA;
            }
            return leaderBoardArray.length();
        } else {
            return 0;
        }
    }

    public void updateData(JSONArray leaderBoardArray, int numberOfTopBA) {
        this.leaderBoardArray = leaderBoardArray;
        this.numberOfTopBA = numberOfTopBA;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userCity, userLeadCount, ranking;
        ImageView userImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            ranking = (TextView) itemView.findViewById(R.id.leader_board_user_ranking);

            userName = (TextView) itemView.findViewById(R.id.leader_board_user_name);
            userCity = (TextView) itemView.findViewById(R.id.leader_board_user_city);

            userLeadCount = (TextView) itemView.findViewById(R.id.leader_board_user_leads);
            userImage = (ImageView) itemView.findViewById(R.id.leader_board_user_image);
        }

    }
    private String msskString(String name){

        if(name.length()>0)
        {
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append(name.charAt(0));
            if(name.contains(" ")) {
                for(int i = 1;i<name.length();i++){
                    if(i<name.lastIndexOf(" "))
                        stringbuffer.append("*");
                    else
                        stringbuffer.append(name.charAt(i));
                }
            }
            else{
                for(int i = 1;i<name.length();i++)
                    stringbuffer.append("*");
            }

            return stringbuffer.toString().trim();
        }
        else
        {
            return "*";
        }


    }

}
