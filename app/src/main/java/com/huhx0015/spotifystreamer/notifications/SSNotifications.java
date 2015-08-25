package com.huhx0015.spotifystreamer.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.huhx0015.spotifystreamer.R;

/** -----------------------------------------------------------------------------------------------
 *  [SSNotifications] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huhx0015)
 *  DESCRIPTION: This class is responsible for handling notifications for mobile and wear devices.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSNotifications {

    /** NOTIFICATION FUNCTIONALITY _____________________________________________________________ **/

    // createNotification(): Creates the notification to display.
    public void createNotification(Context con, Class<?> activity, String notiText) {

        int notificationId = 001; // Sets the notification ID tag.

        // Intent to launch the splash.
        Intent newIntent = new Intent(con, activity);
        PendingIntent pIntent = PendingIntent.getActivity(con, 0, newIntent, 0);

        // ANDROID-WEAR:
        // Specifies the 'big view' content to display the long event description that may not fit
        // the normal content text.
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(notiText);

        // ANDROID WEAR NOTIFICATION: Build the notifications.
        Notification noti =
                new NotificationCompat.Builder(con)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("SPOTIFY STREAMER")
                        .setContentText(notiText).setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(bigStyle)
                        .setContentIntent(pIntent)
                        .build();

        // Retrieves an instance of the NotificationManager service.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(con);

        // ANDROID WEAR SPECIFIC MANAGER NOTIFY:
        // Builds the notification and issues it with notification manager.
        notificationManager.notify(notificationId, noti);
    }
}

