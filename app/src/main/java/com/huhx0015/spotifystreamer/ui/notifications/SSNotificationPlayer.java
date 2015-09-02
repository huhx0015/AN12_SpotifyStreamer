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
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_REMOVE = "action_remove";
    public static final String ACTION_STOP = "action_stop";

    // NOTIFICATION VARIABLES
    private static final int NOTIFICATION_ID = 1995; // Unique identifier for this application's notifications.

    /** NOTIFICATION METHODS ___________________________________________________________________ **/

    // createNotificationPlayer(): Creates a media style notification audio player.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void createNotificationPlayer(MediaSession streamerMediaSession, Bitmap albumImage,
                                          String artist, String track, Context context) {

        // Sets up the PendingIntent for the ContentIntent property, which launches an Intent to
        // open the SSMainActivity activity class.
        //Intent contentIntent = new Intent(context, SSMainActivity.class);
        //contentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //PendingIntent contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, 0);

        // Sets up the PendingIntent for the DeleteIntent property, which signals the SSMusicEngine
        // to stop audio playback when this notification is dismissed.
        Intent dismissIntent = new Intent(context, SSMusicService.class);
        dismissIntent.setAction(ACTION_STOP); // Defines this intent action to be ACTION_STOP.
        PendingIntent dismissPendingIntent = PendingIntent.getService(context, 1, dismissIntent, 0);

        // Creates a new Notification with audio controls.
        final Notification notiPlay = new Notification.Builder(context)

                // Sets the notification style to media style.
                .setStyle(new Notification.MediaStyle()

                        // Attaches the current MediaSession token.
                        .setMediaSession(streamerMediaSession.getSessionToken()))

                .setLargeIcon(albumImage) // Sets the album bitmap image.
                .setSmallIcon(R.drawable.ic_launcher) // Sets the application icon image.
                .setShowWhen(false) // Disables timestamp display.
                //.setContentIntent(contentPendingIntent) // Launches the SSMainActivity activity when the notification is pressed.
                .setDeleteIntent(dismissPendingIntent) // Stops music playback when notification is dismissed.
                .setVisibility(Notification.VISIBILITY_PUBLIC) // Sets the notification to be publicly viewable.

                // Sets the application name, artist, and track name as the notification content.
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(artist) // Sets the artist name as the content text.
                .setContentInfo(track) // Sets the track name as the content info.

                // Forces the notification to have maximum priority. This is needed to address an
                // issue where the notification buttons will be hidden when it is below other
                // active notifications.
                .setPriority(Notification.PRIORITY_MAX) // Sets this notification to have max priority.
                .setWhen(0)

                // Adds the playback controls for the media player.
                .addAction(android.R.drawable.ic_media_previous, "Previous", triggerPlaybackAction(3, context))
                .addAction(android.R.drawable.ic_media_play, "Play", triggerPlaybackAction(0, context))
                .addAction(android.R.drawable.ic_media_pause, "Pause", triggerPlaybackAction(1, context))
                .addAction(android.R.drawable.ic_media_next, "Next", triggerPlaybackAction(2, context))
                .addAction(android.R.drawable.ic_notification_clear_all, "Remove", triggerPlaybackAction(4, context))
                .build();

        // Makes this notification an on-going event. With this enabled, the notification cannot be
        // dismissed manually.
        notiPlay.flags = Notification.FLAG_ONGOING_EVENT;

        // Retrieves the transport controls from the current media session.
        streamerMediaSession.getController().getTransportControls();

        // Sets the constructed notification.
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notiPlay);
    }

    // removeNotifications(): This method is used to remove any active notifications displayed from
    // this application.
    public static void removeNotifications(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    /** PENDING INTENT METHODS _________________________________________________________________ **/

    // triggerPlaybackAction(): This method is invoked whenever the controls in the media player
    // notification are pressed.
    private static PendingIntent triggerPlaybackAction(int actionId, Context context) {

        // Sets up the intent objects for invoking SSMusicService for media player playback control.
        Intent actionIntent;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(context, SSMusicService.class);

        // Defines an intent to invoke based on the incoming actionId value.
        switch (actionId) {

            // PLAY: Sets an intent to the SSMusicService to initiate the playback of the current track.
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

            // REMOVE: Sets an intent to the SSMusicService to remove any active notification player.
            case 4:
                actionIntent = new Intent(ACTION_REMOVE);
                actionIntent.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 5, actionIntent, 0);
                return pendingIntent;

            // DEFAULT: Does nothing.
            default:
                break;
        }

        return null;
    }
}