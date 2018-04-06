package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bestdealfinance.bdfpartner.ActivityNew.ReminderActivity;
import com.bestdealfinance.bdfpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siba.prasad on 22-12-2016.
 */

public class AdapterReminder extends RecyclerView.Adapter<AdapterReminder.ViewHolderMain> {

    private static final String TAG = "AdapterReminder";
    private Context context;
    private LayoutInflater layoutInflater;
    JSONArray jsonArrayData;

    public AdapterReminder(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArrayData = jsonArray;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolderMain onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRow = layoutInflater.inflate(R.layout.list_item_reminder, parent, false);
        ViewHolderMain viewHolderMain = new ViewHolderMain(viewRow);
        return viewHolderMain;
    }

    @Override
    public void onBindViewHolder(ViewHolderMain holder, final int position) {

        if(jsonArrayData!=null && jsonArrayData.length() >0){
            try {
               final JSONObject jsonObject = jsonArrayData.getJSONObject(position);
                holder.textViewLeadId_remminderItem.setText("LEAD ID : "+jsonObject.optString("lead_id"));
                holder.textViewMessage_remminderItem.setText("MESSAGE : "+jsonObject.optString("message"));
                holder.textViewStatus_remminderItem.setText("STATUS : "+jsonObject.optString("status"));

                if(jsonObject.has("isApicallDone")){
                    if(jsonObject.getBoolean("isApicallDone"))
                        holder.relatieLayoutActionReminderItem.setVisibility(View.GONE);
                    else
                        holder.relatieLayoutActionReminderItem.setVisibility(View.VISIBLE);
                }

                holder.buttonYesReminderItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: abcd "+jsonObject.optString("ba_reminder_id"));
                        ((ReminderActivity)context ).doReminderApiCall(jsonObject.optString("ba_reminder_id"),position,jsonObject.optString("lead_id"));
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public int getItemCount() {
        return jsonArrayData.length();
    }

    public class ViewHolderMain extends RecyclerView.ViewHolder {
        private AppCompatTextView textViewLeadId_remminderItem;
        private AppCompatTextView textViewMessage_remminderItem;
        private AppCompatTextView textViewStatus_remminderItem;
        private LinearLayout linearLayoutItemRootReminder;
        private AppCompatButton buttonYesReminderItem;
        private RelativeLayout relatieLayoutActionReminderItem;

        public ViewHolderMain(View itemView) {
            super(itemView);
            textViewLeadId_remminderItem = (AppCompatTextView) itemView.findViewById(R.id.textViewLeadId_remminderItem);
            textViewMessage_remminderItem = (AppCompatTextView) itemView.findViewById(R.id.textViewMessage_remminderItem);
            textViewStatus_remminderItem = (AppCompatTextView) itemView.findViewById(R.id.textViewStatus_remminderItem);
            linearLayoutItemRootReminder = (LinearLayout) itemView.findViewById(R.id.linearLayoutItemRootReminder);
            buttonYesReminderItem = (AppCompatButton) itemView.findViewById(R.id.buttonYesReminderItem);
            relatieLayoutActionReminderItem = (RelativeLayout) itemView.findViewById(R.id.relatieLayoutActionReminderItem);
        }
    }
    public void refreshAfterRemove(int position){
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,jsonArrayData.length());
    }
}
