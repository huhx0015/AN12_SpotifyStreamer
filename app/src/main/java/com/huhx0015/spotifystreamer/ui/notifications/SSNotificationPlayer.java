package com.huhx0015.spotifystreamer.ui.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.session.MediaSession;
import android.os.Build;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.services.SSMusicService;

/** -----------------------------------------------------------------------------------------------
 *  [SSNotificationPlayer] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSNotificationPlayer is a class that provides methods for constructing a
 *  media-style notification for controlling media playback from the notification bar.
 *  -----------------------------------------------------------------------------------------------
 */
public class SSNotificationPlayer {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // MEDIA ACTION VARIABLES:
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";

    /** NOTIFICATION METHODS ___________________________________________________________________ **/

    // createNotificationPlayer(): Creates a media style notification audio player.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void createNotificationPlayer(MediaSession streamerMediaSession, Bitmap albumImage,
                                          String artist, String track, Context context) {

        // Creates a new Notification with audio controls.
        final Notification notiPlay = new Notification.Builder(context)

                // Sets the notification style to media style.
                .setStyle(new Notification.MediaStyle()

                        // Attaches the current MediaSession token.
                        .setMediaSession(streamerMediaSession.getSessionToken()))

                .setLargeIcon(albumImage) // Sets the album bitmap image.
                .setSmallIcon(R.drawable.ic_launcher) // Sets the application icon image.
                .setShowWhen(false) // Disables timestamp display.

                // Sets the application name, artist, and track name as the notification content.
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(artist) // Sets the artist name as the content text.
                .setContentInfo(track) // Sets the track name as the content info.

                // Adds the playback controls for the media player.
                .addAction(android.R.drawable.ic_media_previous, "Previous", triggerPlaybackAction(3, context))
                .addAction(android.R.drawable.ic_media_play, "Play", triggerPlaybackAction(0, context))
                .addAction(android.R.drawable.ic_media_pause, "Pause", triggerPlaybackAction(1, context))
                .addAction(android.R.drawable.ic_media_next, "Next", triggerPlaybackAction(2, context))
                .build();

        // Retrieves the transport controls from the current media session.
        streamerMediaSession.getController().getTransportControls();

        // Sets the constructed notification.
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, notiPlay);
    }

    /** PLAYBACK METHODS _______________________________________________________________________ **/

    // triggerPlaybackAction(): This method is invoked whenever the controls in the media player
    // notification are pressed.
    private static PendingIntent triggerPlaybackAction(int actionId, Context context) {

        // Sets up the intent objects for invoking SSMusicService for media player playback control.
        Intent actionIntent;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(context, SSMusicService.class);

        // Defines an intent to invoke based on the incoming actionId value.
        switch (actionId) {

            // PLAY: Sets an intent to the SSMusicService to initate the playback of the current track.
            case 0:
                actionIntent = new Intent(ACTION_PLAY);
                actionIntent.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0);
                return pendingIntent;

            // PAUSE: Sets an intent to the SSMusicService to pause the playback of the current track.
            case 1:
                actionIntent = new Intent(ACTION_PAUSE);
                actionIntent.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 1, actionIntent, 0);
                return pendingIntent;

            // NEXT: Sets an intent to the SSMusicService to signal SSPlayerFragment to skip to the next track.
            case 2:
                actionIntent = new Intent(ACTION_NEXT);
                actionIntent.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 2, actionIntent, 0);
                return pendingIntent;

            // PREVIOUS: Sets an intent to the SSMusicService to signal SSPlayerFragment to skip to the previous track.
            case 3:
                actionIntent = new Intent(ACTION_PREVIOUS);
                actionIntent.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 3, actionIntent, 0);
                return pendingIntent;

            // DEFAULT: Does nothing.
            default:
                break;
        }

        return null;
    }
}