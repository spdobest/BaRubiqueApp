package com.bestdealfinance.bdfpartner.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bestdealfinance.bdfpartner.ActivityNew.LeadDetailActivityNew;
import com.bestdealfinance.bdfpartner.ActivityNew.ReminderActivity;
import com.bestdealfinance.bdfpartner.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    //database initialization
    private DB snappyDB;

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        String notificationType = data.getString("type");
        Log.i(TAG, "onMessageReceived: " + data.toString() + " \n " + message + "\n type " + notificationType + " ");

//        {"lead_id":"8202","status":"INCOMPLETE","message":"Test message"}

        if (!TextUtils.isEmpty(Helper.getStringSharedPreference(Constant.UTOKEN, this))) {

            if (notificationType.equalsIgnoreCase("STATUS")) {
                Intent intent = new Intent(getApplicationContext(), LeadDetailActivityNew.class);
                intent.putExtra("lead_id", data.getString("lead_id"));
                Log.i(TAG, "onMessageReceived: "+ data.getString("lead_id"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                sendNotification("Status Changed", message, intent);
            } else if (notificationType.equalsIgnoreCase("BA_REMINDER")) {
                insertReminderToDatabase(message);
                Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
                intent.putExtra("reminder", "");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                String leadId = "", notificationMsg = "", notificationStatus = "";
                try {
//                 {"lead_id":"12306","status":"INCOMPLETE","message":"Test message"}
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.has("lead_id")) {
                        leadId = jsonObject.optString("lead_id");
                    }
                    if (jsonObject.has("message")) {
                        notificationMsg = jsonObject.optString("message");
                    }
                    if (jsonObject.has("status")) {
                        notificationStatus = jsonObject.optString("status");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                 {"lead_id":"12306","status":"INCOMPLETE","message":"Test message"}

                sendNotification("Lead Reminder", "Lead is Expiring Soon \nLead " + leadId + " is still " + notificationStatus, intent);

            }
        }
    }

    private void sendNotification(String heading, String message, Intent intent) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, 0);


         NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(heading)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVibrate(pattern)
                .setAutoCancel(true)
                .setContentText(message)
                .setSmallIcon(getNotificationIcon());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue500));
        }
        else{

        }
//            mNotifyBuilder.setSmallIcon(R.drawable.notification_icon);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /*ID of notification */, notificationBuilder.build());
    }

    private void insertReminderToDatabase(final String strReminder) {
        if (!TextUtils.isEmpty(strReminder)) {
            try {
                JSONObject jsonObjectReminder = new JSONObject(strReminder);
                jsonObjectReminder.put("isApicallDone", false);
                Log.i(TAG, "insertReminderToDatabase: " + jsonObjectReminder.toString());
                snappyDB = DBFactory.open(getApplicationContext());
                Log.i(TAG, "insertReminderToDatabase: 11 "+jsonObjectReminder.optString("lead_id"));
                if (snappyDB.exists(Constant.DB_REMINDER)) {
                    Log.i(TAG, "insertReminderToDatabase: 12 ");
                    JSONArray jsonArrayFromDb = new JSONArray(snappyDB.get(Constant.DB_REMINDER));
                    Log.i(TAG, "insertReminderToDatabase: 13 " + jsonArrayFromDb.toString());
                    jsonArrayFromDb.put(jsonObjectReminder);
                    snappyDB.put(Constant.DB_REMINDER, jsonArrayFromDb.toString());
                    Log.i(TAG, "insertReminderToDatabase: 13 " + jsonArrayFromDb.toString());
                      for(int i = 0;i<jsonArrayFromDb.length();i++){
                        if(jsonArrayFromDb.optJSONObject(i).optString("lead_id").equals(jsonObjectReminder.optString("lead_id"))){
                            jsonArrayFromDb.put(i,jsonObjectReminder);
                            break;
                        }
                    }
                } else {
                    Log.i(TAG, "insertReminderToDatabase: 17 ");

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObjectReminder);
                    snappyDB.put(Constant.DB_REMINDER, jsonArray.toString());
                }
                snappyDB.close();
            } catch (SnappydbException e) {
                e.getMessage();
            } catch (JSONException e) {
                e.getMessage();
            } finally {
                try {
                    if (snappyDB != null && snappyDB.isOpen())
                        snappyDB.close();
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.notification_lollipop : R.drawable.notification_icon;
    }
}





