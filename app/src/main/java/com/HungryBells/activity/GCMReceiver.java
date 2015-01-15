package com.HungryBells.activity;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.HungryBells.DTO.GCMNotification;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Arunkumar on 24/11/2014.
 */
public class GCMReceiver extends IntentService {

    public static final int NOTIFICATION_ID = 101;
    Intent intent;
    NotificationCompat.Builder mBuilder;
    GCMNotification gcmMessage;

    public GCMReceiver() {
        super("hb-prod-1");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        Bundle extras = intent.getExtras();
        Log.e("Message", "Insise the intent");
        if (!extras.isEmpty()) {
            String message = intent.getExtras().getString("MESSAGE");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            gcmMessage = new GCMNotification();
            /*System.out.println("message:"+message);
            sendNotification("");*/
            try {
                if(message!=null) {
                    Log.e("Message", message);
                    gcmMessage = gson.fromJson(message, GCMNotification.class);
                    Log.e("Message", "GCMNotification");
                    if (gcmMessage.getNotificationType().equals("PB")
                            || gcmMessage.getNotificationType().equals("PL")
                            || gcmMessage.getNotificationType().equals("PD")) {
                        SharedPreferences sharedPreferencesLocation = getSharedPreferences("HB", MODE_PRIVATE);
                        findLocation(Double.parseDouble(sharedPreferencesLocation.getString("lattitude", "0.0")), Double.parseDouble(sharedPreferencesLocation.getString("longitude", "0.0")), gcmMessage);
                        Log.e("Location connection", "Location connection not available");
                    } else {
                        sendNotification("");
                    }
                }
            }catch (Exception e){
                Log.e("Error",e.toString(),e);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        mBuilder = new NotificationCompat.Builder(
                this);
        mBuilder.setSmallIcon(R.drawable.hblogos);
        mBuilder.setContentTitle(gcmMessage.getNotificationMessage());
        mBuilder.setAutoCancel(true);
        mBuilder.setContentText(gcmMessage.getNotificationDescription());
        mBuilder.setContentIntent(contentIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
       /* Uri.parse("android.resource://" +getPackageName()
                + "/" + R.raw.swordsheath)*/
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        String url = getString(R.string.serverurl_test)
                + "gcmdealacknowledgement/" + gcmMessage.getId();
        Ion.with(this).load(url).setJsonPojoBody(gcmMessage).asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                    }
                });
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }



    private void findLocation(double  latitude,double  longitude,
                              final GCMNotification gcmMessage) {

        try {
            GCMNotification gcmMessage2 = new GCMNotification();
            gcmMessage2.setLatitude(latitude);
            gcmMessage2.setLongitude(longitude);
           /* SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            gcmMessage2.setResponseMobileAppTime(sdf.format(new Date()));*/
            gcmMessage2.setCustomerId(gcmMessage.getCustomerId());
            gcmMessage2.setId(gcmMessage.getId());

            String url = getString(R.string.serverurl_test)
                    + "updategcmmessage/" + gcmMessage2.getId();
            Ion.with(this).load(url).setJsonPojoBody(gcmMessage2).asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            GcmBroadcastReceiver.completeWakefulIntent(intent);
                        }
                    });
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }

    }


}
