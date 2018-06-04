package com.igaze.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.igaze.Activities.MainActivity;
import com.igaze.R;

import static com.igaze.Utilities.Constants.showLog;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.ascentbrezie.brezie";
    private static final String CHANNEL_NAME = "Brezie channel";
    NotificationChannel brezieChannel;
    private NotificationManager notificationManager;

    public NotificationHelper(Context base){
        super(base);
        createChannels();
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showLog("creating channel");
            brezieChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            brezieChannel.enableLights(true);
            brezieChannel.enableVibration(true);
            brezieChannel.setLightColor(getResources().getColor(R.color.colorPrimary));
            brezieChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
    }

    public NotificationManager getManager(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager==null){
                notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                getManager().createNotificationChannel(brezieChannel);
            }
        }
        return notificationManager;
    }

    public Notification.Builder getNotificationChannel(String title,String body,String clickAction){

        showLog("reached here 3");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("notification","1");
            intent.putExtra("title",title);
            intent.putExtra("body",body);
            intent.putExtra("location",clickAction);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            showLog("reached here 4");

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);



            return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                    .setContentTitle(title)
    //                .setSmallIcon(R.drawable.brezie_notification_symbol)
    //                .setColor(getResources().getColor(R.color.colorSecondary))
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setStyle(new Notification.BigTextStyle().bigText(body))
                    .setContentIntent(pendingIntent);
        }else {
            return null;
        }
    }
}
