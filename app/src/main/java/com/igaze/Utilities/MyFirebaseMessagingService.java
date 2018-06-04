package com.igaze.Utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.igaze.Activities.MainActivity;
import com.igaze.R;

import java.util.Random;

import static com.igaze.Utilities.Constants.showLog;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String  TAG = "My Firebase Messaging Service";
    NotificationHelper notificationHelper;

    public void onMessageReceived(RemoteMessage remoteMessage) {

  /*      showLog("reached here 1");
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("signedUp", "0").equals("1")){
            notificationHelper = new NotificationHelper(this);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder builder = notificationHelper.getNotificationChannel(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"),remoteMessage.getData().get("click_action"));
                showLog("reached here 2");
                notificationHelper.getManager().notify(new Random().nextInt(),builder.build());
                showLog("reached here 5");
            }else{
                showLog("message "+remoteMessage.getData());
                showLog("title "+remoteMessage.getData().get("title"));

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("notification","1");
                intent.putExtra("title",remoteMessage.getData().get("title"));
                intent.putExtra("body",remoteMessage.getData().get("body"));
                intent.putExtra("location",remoteMessage.getData().get("click_action"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
                Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"CHANNEL_ID")
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("body"))
                        .setAutoCancel(true)
                        .setSound(notificationSound)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(remoteMessage.getData().get("body")))
                        .setContentIntent(pendingIntent);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setSmallIcon(R.drawable.brezie_notification_symbol);
                    notificationBuilder.setColor(getResources().getColor(R.color.colorSecondary));
                } else {
                    notificationBuilder.setSmallIcon(R.drawable.brezie_icon);
                }

                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(11,notificationBuilder.build());
            }

        }else{

        }
        Constants.userId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("userId", "0");
    }

    private void sendNotification(String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(11,notificationBuilder.build()); */
    }
}

