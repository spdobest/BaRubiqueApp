package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by rbq on 10/23/16.
 */

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private JSONArray meetingsArray;
    private long currentDate;


    /*
        "id": "2",
        "customer_id": "12",
        "customer_name": " testing",
        "lead_id": "0",
        "location": "fdfdfdf",
        "summary": "dfdfdfdf",
        "title": "dfdfdfdfdf",
        "startDateTime": "2016-10-19 10:54:28",
        "endDateTime": "2016-10-19 11:54:28"
    */

    public MeetingAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public MeetingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_meeting, parent, false);
        return new MeetingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeetingAdapter.ViewHolder holder, int position) {
        /*
        {
        "id": "4",
        "customer_name": "  testing",
        "customer_contact": "676867686",
        "lead_id": "0",
        "location": "fdfdfdf",
        "startDateTime": "2016-10-21 16:54:28",
        "endDateTime": "2016-10-21 17:54:28"
        },
         */
        holder.customerName.setText(meetingsArray.optJSONObject(position).optString("customer_name"));
        holder.leadId = meetingsArray.optJSONObject(position).optString("lead_id");
        String location = meetingsArray.optJSONObject(position).optString("location");
        holder.meetingAddress.setText(location);
        String startDate = meetingsArray.optJSONObject(position).optString("startDateTime");
        String meetingDate = Helper.timestampDate(startDate);
        holder.meetingDate.setText(meetingDate);
        String meetingStartTime = Helper.timestampTime(startDate);
        String meetingEndTime = Helper.timestampTime(meetingsArray.optJSONObject(position).optString("endDateTime"));
        holder.meetingTime.setText(meetingStartTime);
        if (currentDate > Helper.convertDateToInt(startDate)) {
            holder.meetingDateBackground.setBackgroundColor(context.getResources().getColor(R.color.progress_gray));
        }

        final String customerPhoneNumber = meetingsArray.optJSONObject(position).optString("customer_contact");

        holder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customerPhoneNumber));
                context.startActivity(intent);
            }
        });

        holder.ivDelete.setVisibility(View.GONE);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.ivEdit.setVisibility(View.GONE);
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.ivMap.setVisibility(View.GONE);
        holder.ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    @Override
    public int getItemCount() {
        if (meetingsArray != null && meetingsArray.length() > 0) {
            return meetingsArray.length();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName, meetingTime, meetingAddress, meetingDate;
        private ImageView ivCall, ivEdit, ivDelete, ivMap;
        private String leadId;
        private View meetingDateBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            customerName = (TextView) itemView.findViewById(R.id.meeting_customer_name);
            meetingTime = (TextView) itemView.findViewById(R.id.meeting_time);
            meetingAddress = (TextView) itemView.findViewById(R.id.meeting_address);
            meetingDate = (TextView) itemView.findViewById(R.id.meeting_date);
            meetingDateBackground = itemView.findViewById(R.id.meeting_date_background);

            ivCall = (ImageView) itemView.findViewById(R.id.meeting_call);
            ivEdit = (ImageView) itemView.findViewById(R.id.meeting_edit);
            ivDelete = (ImageView) itemView.findViewById(R.id.meeting_map);
            ivMap = (ImageView) itemView.findViewById(R.id.meeting_delete);
        }
    }

    public void updateData(JSONArray meetingsArray) {
        this.meetingsArray = meetingsArray;
        sortMeetingArray();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDate = Helper.convertDateToInt(sdf.format(c.getTime())); //20161020105428L;
        notifyDataSetChanged();
    }

    private void sortMeetingArray() {
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        for (int i = 0; i < meetingsArray.length(); i++) {
            try {
                array.add(meetingsArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(array, new Comparator<JSONObject>() {

            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lhsDate = null, rhsDate = null;
                try {
                    lhsDate = lhs.getString("startDateTime");
                    rhsDate = rhs.getString("startDateTime");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return (int) (Helper.convertDateToInt(rhsDate) - Helper.convertDateToInt(lhsDate));

            }
        });

        meetingsArray = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            meetingsArray.put(array.get(i));
        }

    }
}
