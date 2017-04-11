package com.huntloc.handheldvehiclecontrol;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {

    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setColor(Color.parseColor("#ff33b5e5"))
                .setAutoCancel(true);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notification);
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.setAction("android.intent.action.MAIN");
        Log.d("onMessageReceived", remoteMessage.getData().get("plate"));
        Log.d("onMessageReceived",remoteMessage.hashCode()+"");
        myIntent.putExtra("plate", remoteMessage.getData().get("plate"));
        PendingIntent intent2 = PendingIntent.getActivity(this, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent2);
        notificationManager.notify(remoteMessage.hashCode(), mBuilder.build());
    }
}
