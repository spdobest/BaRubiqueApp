package com.bestdealfinance.bdfpartner;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.bestdealfinance.bdfpartner.activity.HomeActivity;
import com.bestdealfinance.bdfpartner.activity.ReferralDetailActivity;
import com.bestdealfinance.bdfpartner.activity.SplashActivity;
import com.bestdealfinance.bdfpartner.application.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "GCM Demo";
    Context context;
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        Bundle extras = intent.getExtras();
        Logs.LogD("Bundle", extras.toString());
        String msg = intent.getStringExtra("message");
        String type = intent.getStringExtra("type");
        String subtype=intent.getStringExtra("subtype");
        String title = intent.getStringExtra("title");

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(), "");
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(), "");
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                //sendNotification("Received: " + extras.toString());

                if (type != null) {
                    Logs.LogD("type", type);
                    switch (type) {
                        case "text":
                            if (subtype==null){
                                sendNotificationText(msg, title);
                                Logs.LogD("Notify", "Sending Text Notification");
                                break;
                            }
                            if (subtype.equals("status") | subtype.equals("payout")){
                                String lead_id=intent.getStringExtra("lead_id");
                                sendNotificationTextStatus(msg, title, lead_id);
                            }

                            else {
                                sendNotificationText(msg, title);
                                Logs.LogD("Notify", "Sending Text Notification");
                            }
                            break;
                        case "app":
                            sendNotificationUpdate(msg, title);
                            Logs.LogD("Notify", "Sending Update Notification");
                            break;
                        case "weblink":
                            sendNotificationUpdate(msg, title);
                            Logs.LogD("Notify", "Sending Update Notification");
                            break;
                        case "feedback":
                            sendNotificationFeedback(msg, title);
                            Logs.LogD("Notify", "Sending Feedback Notification");
                            break;
                        case "installs":
                            sendNotificationInstall(msg, title);
                            Logs.LogD("Notify", "Fetching Installs Information");
                            break;
                        case "updateimage":
                            String image2 = extras.getString("image_url");
                            if (image2 != null) {
                                Logs.LogD("Notify", "ImageURL Found," + image2);
                                sendNotificationImage(msg, title, image2, true);
                            } else {
                                Logs.LogD("Notify", "ImageURL not Found, sending text");
                                sendNotification(msg, title);
                            }
                            break;
                        case "image":
                            String image = extras.getString("image_url");
                            if (image != null) {
                                Logs.LogD("Notify", "ImageURL Found," + image);
                                sendNotificationImage(msg, title, image, false);
                            } else {
                                Logs.LogD("Notify", "ImageURL not Found, sending text");
                                sendNotification(msg, title);
                            }
                            Logs.LogD("Notify", "Sending Banner Notification");
                            break;
                        default:
                            sendNotification(msg, title);
                            Logs.LogD("Notify", "Sending Default Notification");
                            break;
                    }
                } else {
                    Logs.LogD("GCMIntent", "Type is Null, Not Sending Notification");
                }
                Logs.LogD(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotificationTextStatus(String msg, String title, String lead_id){
        Intent myintent;
        //TODO Formating title
        Spanned new_title=Html.fromHtml(URLDecoder.decode(title));
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BDF", 0);
        Logs.LogD("Status", "Notification Visible");
        String valid = pref.getString("utoken", null);
        if (valid == null) {
            myintent = new Intent(this, SplashActivity.class);
        } else {
            myintent = new Intent(this, ReferralDetailActivity.class);
            myintent.putExtra("lead_id",lead_id);
        }
        myintent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setLargeIcon(icon)
                        .setContentTitle(new_title)
                        .setAutoCancel(true)
                                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        mBuilder.setVibrate(new long[]{1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationsound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Logs.LogD(TAG, "Notification sent successfully.");
    }
    private void sendNotificationInstall(String msg, String title) {
        SendFeedback task = new SendFeedback();
        task.execute();
    }

    private void sendNotificationFeedback(String msg, String title) {
        Intent myintent;
        //TODO Formating title
        Spanned new_title=Html.fromHtml(URLDecoder.decode(title));
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Util.MY_PREFERENCES, 0);
        pref.edit().putString("Code-0", "true").apply();
        Logs.LogD("Feedback", "Notification Visible");
        String valid = pref.getString("utoken", null);
        if (valid == null) {
            myintent = new Intent(this, SplashActivity.class);
        } else {
            myintent = new Intent(this, HomeActivity.class);
        }
        myintent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setLargeIcon(icon)
                        .setContentTitle(new_title)
                        .setAutoCancel(true)
                                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        mBuilder.setVibrate(new long[]{1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationsound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Logs.LogD(TAG, "Notification sent successfully.");
    }

    private void sendNotification(String msg, String title) {
        Intent myintent;
        //TODO Formating title
        Spanned new_title=Html.fromHtml(URLDecoder.decode(title));
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BDF", 0);
        String valid = pref.getString("utoken", null);
        if (valid == null) {
            myintent = new Intent(this, SplashActivity.class);
        } else {
            myintent = new Intent(this, HomeActivity.class);
        }
        myintent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setLargeIcon(icon)
                        .setColor(getResources().getColor(R.color.Red))
                        .setContentTitle(new_title)
                        .setAutoCancel(true)
                                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        mBuilder.setVibrate(new long[]{1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationsound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Logs.LogD(TAG, "Notification sent successfully.");
    }

    private void sendNotificationText(String msg, String title) {
        Intent myintent;
        Logs.LogD("mesage", msg);

        //TODO Changing Here to Unicode for Smiley Compatiblity.
        Spanned total=Html.fromHtml(URLDecoder.decode(msg));
        //TODO Formating title
        Spanned new_title=Html.fromHtml(URLDecoder.decode(title));
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BDF", 0);
        String valid = pref.getString("utoken", null);
        if (valid == null) {
            myintent = new Intent(this, SplashActivity.class);
        } else {
            myintent = new Intent(this, HomeActivity.class);
        }
        myintent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setLargeIcon(icon)
                        .setColor(getResources().getColor(R.color.Red))
                        .setContentTitle(new_title)
                        .setAutoCancel(true)
                                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(total))
                        .setContentText(total);
        mBuilder.setVibrate(new long[]{1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationsound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Logs.LogD(TAG, "Notification sent successfully.");
    }

    private void sendNotificationUpdate(String msg, String title) {
        Intent myintent;
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BDF", 0);
        String valid = pref.getString("utoken", null);
        //TODO Formating title
        Spanned new_title=Html.fromHtml(URLDecoder.decode(title));
        myintent = new Intent(this, GetNotifyActivity.class);
        myintent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setLargeIcon(icon)
                        .setColor(getResources().getColor(R.color.Red))
                        .setContentTitle(new_title)
                        .setAutoCancel(true)
                                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        mBuilder.setVibrate(new long[]{1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationsound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Logs.LogD(TAG, "Notification sent successfully.");
    }

    private void sendNotificationImage(String msg, String title, String image, Boolean update) {
        DownloadImageTask task = new DownloadImageTask(msg, title, image, update);
        task.execute();

    }
    private void supplementNotificationUpdateImage(Bitmap result, String msg, String title) {
        Intent myintent;
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Util.MY_PREFERENCES, 0);
        String valid = pref.getString("utoken", null);
        //TODO Formating title
        Spanned new_title=Html.fromHtml(URLDecoder.decode(title));
        myintent = new Intent(this, GetNotifyActivity.class);
        myintent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(getNotificationIcon())
                        .setLargeIcon(icon)
                        .setContentTitle(new_title)
                        .setContentText(msg)
                        .setColor(getResources().getColor(R.color.Red))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result));
        mBuilder.setVibrate(new long[]{1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationsound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Logs.LogD(TAG, "Notification sent successfully.");
    }
    private void supplementNotification(Bitmap result, String msg, String title) {
        Intent myintent;
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Util.MY_PREFERENCES, 0);
        Spanned new_title=Html.fromHtml(URLDecoder.decode(title));
        String valid = pref.getString("utoken", null);
        if (valid == null) {
            myintent = new Intent(this, SplashActivity.class);
        } else {
            myintent = new Intent(this, HomeActivity.class);
        }
        myintent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(getNotificationIcon())
                        .setLargeIcon(result)
                        .setContentTitle(new_title)
                        .setContentText(new_title)
                        .setColor(getResources().getColor(R.color.Red))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result));
        mBuilder.setVibrate(new long[]{1000});
        mBuilder.setLights(Color.RED, 3000, 3000);
        Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationsound);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Logs.LogD(TAG, "Notification sent successfully.");
    }

    private int getNotificationIcon() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return R.mipmap.ic_stat_transparent;
        } else {
            return R.mipmap.ic_launcher;
        }

    }

    // DownloadImage AsyncTask
    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
        String msg;
        String title;
        String image_url;
        Boolean up;

        public DownloadImageTask(String msg, String title, String image, Boolean update) {
            this.msg = msg;
            this.title = title;
            this.image_url = image;
            this.up=update;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            String imageURL = image_url;

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (up) {
                supplementNotification(result, msg, title);
            }
            else {
                supplementNotificationUpdateImage(result, msg, title);
            }
        }
    }

    private class SendFeedback extends AsyncTask<Void, Void, String> {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BDF", 0);
        String UID = pref.getString("uid", "");
        String email = pref.getString("email", "");
        private String feedback_URL = "https://www.bestdealfinance.com/layout/addEdit";

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            // Create a new HttpClient and Post Header
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(feedback_URL);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("feedback_type_id", UID));
                nameValuePairs.add(new BasicNameValuePair("feeedBack_experience", email));
                nameValuePairs.add(new BasicNameValuePair("feedback_type", "InstallCount"));
                nameValuePairs.add(new BasicNameValuePair("feedback_type_cp", "CON"));
                nameValuePairs.add(new BasicNameValuePair("andi", "fonew8274683kfelw-few93-fmwe"));
                Logs.LogD("Request", nameValuePairs.toString());
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse execute = client.execute(httppost);
                InputStream content = execute.getEntity().getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {

            }
            Logs.LogD("Result", response);
            return response;
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject response = new JSONObject(result);
                if (response.getString("msg") != null) {
                    String msg = response.getString("msg");
                    if (msg.equals("success")) {
                        Logs.LogD("Feedback", "Send Info to Server");
                    } else {
                    }
                }
            } catch (JSONException e) {
                Logs.LogD("Feedback", "Server Delayed the Response, Catching JSONException");
                e.printStackTrace();
            }
            Logs.LogD("Feedback", "Recieved Response from Server, Closing the Activity.");
        }
    }
}