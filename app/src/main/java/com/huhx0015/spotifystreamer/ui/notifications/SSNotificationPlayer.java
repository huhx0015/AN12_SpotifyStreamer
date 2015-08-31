package com.huhx0015.spotifystreamer.ui.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.Build;

import com.huhx0015.spotifystreamer.R;

/**
 * Created by Michael Yoon Huh on 8/30/2015.
 */
public class SSNotificationPlayer {

    /*
    public static void createNotificationPlayer(MediaSession streamerMediaSession, Bitmap albumImage,
                                          String artist, String track, Context context) {

        // Creates a new Notification with audio controls.
        final Notification noti = new Notification.Builder(context)

                .setShowWhen(false) // Disables timestamp display.
                .setStyle(new Notification.MediaStyle() // Sets the notification style to media style.

                        // Attaches the current MediaSession token.
                        .setMediaSession(streamerMediaSession.getSessionToken()))

                        //.setColor(0xFFFFFF) // Sets the notification color.
                .setLargeIcon(albumImage) // Sets the album bitmap image.
                .setSmallIcon(R.mipmap.ic_launcher) // Sets the application icon image.

                        // Sets the Notification content information.
                .setContentText(artist)
                .setContentInfo(track)
                .setContentTitle("Spotify Streamer")

                        // Add some playback controls.
                .addAction(android.R.drawable.ic_media_previous, "Previous", triggerPlaybackAction(3))
                .addAction(android.R.drawable.ic_media_play, "Play", triggerPlaybackAction(0))
                .addAction(android.R.drawable.ic_media_pause, "Pause", triggerPlaybackAction(1))
                .addAction(android.R.drawable.ic_media_next, "Next", triggerPlaybackAction(2))
                .build();

        // Do something with your TransportControls.
        final MediaController.TransportControls controls = streamerMediaSession.getController().getTransportControls();

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, noti);
    }
    */
}
