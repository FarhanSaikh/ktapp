package in.kolkatatailor.kolkatatailor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.content.Intent.getIntent;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
private static final String TAG = "NOTIFICATION";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from_user_id = remoteMessage.getData().get("from_user_id");
        String notification_title=remoteMessage.getNotification().getTitle();
        String notification_message=remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification_title)
                .setContentText(notification_message)
                .setSound(defaultSoundUri)
               .setAutoCancel(true);


        Intent resultintent = new Intent(click_action);
        resultintent.putExtra("customeruid", from_user_id);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    resultintent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            int mNotificationId=(int) System.currentTimeMillis();
    NotificationManager mNotifymgr=
        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mNotifymgr.notify(mNotificationId,mBuilder.build());



    }



}