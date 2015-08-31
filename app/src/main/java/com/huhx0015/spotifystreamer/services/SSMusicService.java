package com.huhx0015.spotifystreamer.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.audio.SSMusicEngine;
import com.huhx0015.spotifystreamer.interfaces.OnMusicPlayerListener;

/** -----------------------------------------------------------------------------------------------
 *  [SSMusicService] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSMusicService is a service class that handles the playback of streaming Spotify
 *  tracks in the background.
 *  -----------------------------------------------------------------------------------------------
 */
public class SSMusicService extends Service implements MediaPlayer.OnPreparedListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES
    private SSMusicEngine ss_music; // SSMusicEngine class object that is used for music functionality.
    private String currentSong = ""; // Sets the default song title.
    private String songURL = ""; // References the song URL.
    private Boolean musicOn = true; // Used to determine if music has been enabled or not.
    private Boolean isPlaying = false; // Indicates that a song is currently playing in the background.

    // FRAGMENT VARIABLES
    private Fragment playerFragment; // References the player fragment attached to this service.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = SSMusicService.class.getSimpleName(); // Used for logging output to logcat.

    // MEDIA ACTION VARIABLES:
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";

    // MEDIA SESSION VARIABLES:
    private MediaController streamerMediaController;
    private MediaSession streamerMediaSession;
    private MediaSessionManager streamerMediaSessionManager;

    // SERVICE VARIABLES
    private final IBinder audioBind = new SSMusicBinder(); // IBinder object that is used to bind this service to an activity.

    // SYSTEM VARIABLES
    private final int api_level = android.os.Build.VERSION.SDK_INT; // Used to determine the device's Android API version.

    // THREADING VARIABLES
    private Handler seekHandler = new Handler(); // Handler for the seekbar update thread.

    /** SERVICE LIFECYCLE METHODS ______________________________________________________________ **/

    // onCreate(): The system calls this method when the service is first created, to perform
    // one-time setup procedures (before it calls either onStartCommand() or onBind()).
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(LOG_TAG, "SERVICE LIFECYCLE (onCreate): onCreate() invoked.");

        // AUDIO CLASS INITIALIZATION:
        ss_music.getInstance().initializeAudio(getApplicationContext());
    }

    /** SERVICE EXTENSION METHODS ______________________________________________________________ **/

    // onStartCommand(): Runs when this service is directly invoked.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (streamerMediaSessionManager != null) {
            processIntent(intent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // onBind(): Runs when this service is successfully bound to the application.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return audioBind;
    }

    // onUnbind(): Runs when this service is unbound from the application.
    @Override
    public boolean onUnbind(Intent intent){

        // If a song is currently playing in the background, the song playback is stopped.
        if (ss_music.getInstance().isSongPlaying()) {
            ss_music.getInstance().stopSong(); // Stops the playback of the song.
        }

        // Releases all audio-related instances if the service is unbound.
        ss_music.getInstance().releaseMedia();

        return false;
    }

    // onPrepared(): Runs when the MediaPlayer object is ready for audio playback.
    @Override
    public void onPrepared(MediaPlayer mp) {}

    /** MUSIC ENGINE METHODS ___________________________________________________________________ **/

    // attachPlayerFragment(): Attaches the SSPlayerFragment to the SSMusicEngine class.
    public void attachPlayerFragment(Fragment fragment) {

        Log.d(LOG_TAG, "attachPlayerFragment(): Attaching the SSPlayerFragment to the service.");

        this.playerFragment = fragment;
        ss_music.getInstance().attachFragment(fragment);
    }

    // pauseTrack(): Accesses the SSMusicEngine instance to pause the streaming song track.
    public void pauseTrack(Boolean isStop) {

        // Pauses the song if a song is currently playing in the background.
        if (ss_music.getInstance().isSongPlaying()) {

            // STOP:
            if (isStop) {
                ss_music.getInstance().stopSong();
                Log.d(LOG_TAG, "pauseTrack(): Song was stopped.");
            }

            // PAUSE:
            else {
                ss_music.getInstance().pauseSong();
                Log.d(LOG_TAG, "pauseTrack(): Song was paused.");
            }
        }

        isPlaying = false; // Indicates that the song is no longer playing.
        seekHandler.removeCallbacks(seekbarThread); // Stops the seekbar update thread.
    }

    // playTrack(): Accesses the SSMusicEngine instance to play the streaming song track.
    public void playTrack(String songUrl, Boolean loop, Bitmap albumImage, Boolean notiOn,
                          String albumArtist, String albumTrack) {

        // Initiates music playback in SSMusicEngine.
        ss_music.getInstance().playSongUrl(songUrl, loop);
        isPlaying = true; // Indicates that the song is playing.
        this.songURL = songUrl; // Sets the current song URL.

        // If notification playback has been enabled, the notification media player is built and
        // displayed.
        if (notiOn) {

            // Checks the API LEVEL first. If the device is running an Android API level less than
            // 21 (LOLLIPOP), no notification player controls are displayed.
            if (api_level >= 21) {
                initializeMediaSession(albumImage, albumArtist, albumTrack);
            }
        }

        seekHandler.postDelayed(seekbarThread, 1000); // Begins the seekbar update thread.
    }

    // releaseMedia(): Accesses the SSMusicEngine instance to release all resources used by the
    // MediaPlayer object.
    public void releaseMedia() {
        ss_music.getInstance().releaseMedia();
    }

    // setPosition(): Accesses the SSMusicEngine instance to update the song position.
    public void setPosition(int position){
        ss_music.getInstance().setSongPosition(position);
    }

    // updatePlayer(): Signals the SSMusicEngine instance to update the attached player fragment of
    // the current song status, as well as the song's max duration.
    public void updatePlayer() {
        ss_music.getInstance().updatePlayer();
    }

    /** MEDIA PLAYER NOTIFICATION METHODS ______________________________________________________ **/

    // processIntent(): If this service is invoked by external audio controls in the notification
    // menu, the audio player state is changed accordingly.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void processIntent(Intent intent) {

        // If the intent or the associated action is null, the method is terminated early.
        if( intent == null || intent.getAction() == null ) {
            return;
        }

        // Determines the defined action from the intent.
        String trigger = intent.getAction();

        // PLAY:
        if (trigger.equalsIgnoreCase(ACTION_PLAY) ) {
            streamerMediaController.getTransportControls().play();
        }

        // PAUSE:
        else if (trigger.equalsIgnoreCase(ACTION_PAUSE)) {
            streamerMediaController.getTransportControls().pause();
        }

        // PREVIOUS:
        else if (trigger.equalsIgnoreCase(ACTION_PREVIOUS)) {
            streamerMediaController.getTransportControls().skipToPrevious();
        }

        // NEXT:
        else if (trigger.equalsIgnoreCase(ACTION_NEXT)) {
            streamerMediaController.getTransportControls().skipToNext();
        }
    }

    // triggerPlaybackAction():
    private PendingIntent triggerPlaybackAction(int which) {
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(this, SSMusicService.class);
        switch (which) {

            case 0:

                // Play
                action = new Intent(ACTION_PLAY);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 0, action, 0);
                return pendingIntent;

            case 1:

                // Pause
                action = new Intent(ACTION_PAUSE);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 1, action, 0);
                return pendingIntent;

            case 2:

                // Skip tracks
                action = new Intent(ACTION_NEXT);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 2, action, 0);
                return pendingIntent;

            case 3:

                // Previous tracks
                action = new Intent(ACTION_PREVIOUS);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 3, action, 0);
                return pendingIntent;

            default:
                break;
        }

        return null;
    }

    // intializeMediaSession(): Builds a new MediaSession for displaying a notification that users
    // can interact with and be able to control audio playback.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initializeMediaSession(final Bitmap albumImage, final String albumArtist, final String albumTrack) {

        // Creates a new MediaSession.
        streamerMediaSession = new MediaSession(getApplicationContext(), "SPOTIFY STREAMER");
        streamerMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        streamerMediaController = new MediaController(getApplicationContext(), streamerMediaSession.getSessionToken());

        // Updates the current metadata for the track currently playing.
        streamerMediaSession.setMetadata(new MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, albumImage)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, albumArtist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, albumTrack)
                .putString(MediaMetadata.METADATA_KEY_TITLE, "SPOTIFY STREAMER")
                .build());

        streamerMediaSession.setActive(true); // Indicates that media commands can be received.

        // Attaches a new Callback to receive the MediaSession updates.
        streamerMediaSession.setCallback(new MediaSession.Callback() {

            // PLAY: Runs when the play action has been initiated.
            @Override
            public void onPlay() {
                super.onPlay();
                playTrack(songURL, false, albumImage, true, albumArtist, albumTrack);
            }

            // PAUSE: Runs when the pause action has been initiated.
            @Override
            public void onPause() {
                super.onPause();
                pauseTrack(false); // Pauses the currently playing song track.
            }

            // NEXT: Runs when the next action has been initiated.
            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                playNextSong(true); // Signals SSPlayerFragment to play the next song in the tracklist.
            }

            // PREVIOUS: Runs when the previous action has been initiated.
            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                playNextSong(false); // Signals SSPlayerFragment to play the previous song in the tracklist.
            }
        });

        // Enables the ability to receive transport controls via the MediaSession Callback.
        streamerMediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Creates the notification with the media player controls.
        createNotificationPlayer(streamerMediaSession, albumImage, albumArtist, albumTrack);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNotificationPlayer(MediaSession streamerMediaSession, Bitmap albumImage,
                                          String artist, String track) {

        // Creates a new Notification with audio controls.
        final Notification noti = new Notification.Builder(this)

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

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, noti);
    }

    /** THREADING METHODS ______________________________________________________________________ **/

    // seekbarThread(): A threaded function which updates the player seekbar in the
    // SSPlayerFragment.
    private Runnable seekbarThread = new Runnable() {

        public void run() {
            int currentPosition = ss_music.getInstance().getSongPosition();
            seekbarStatus(currentPosition); // Relays the song position value to SSPlayerFragment.
            seekHandler.postDelayed(this, 1000);
        }
    };

    /** SUBCLASSES _____________________________________________________________________________ **/

    /**
     * --------------------------------------------------------------------------------------------
     * [SSMusicBinder] CLASS
     * DESCRIPTION: This is a Binder-type subclass that is used to bind the SSMusicService to an
     * activity.
     * --------------------------------------------------------------------------------------------
     */

    public class SSMusicBinder extends Binder {

        // getService(): Returns the SSMusicBinder service.
        public SSMusicService getService() {
            return SSMusicService.this;
        }
    }

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // seekbarStatus(): Signals the SSPlayerFragment on the current song position for updating the
    // player seekbar.
    private void seekbarStatus(int position) {

        if (playerFragment != null) {
            try { ((OnMusicPlayerListener) playerFragment).seekbarStatus(position); }
            catch (ClassCastException cce) {} // Catch for class cast exception errors.
        }

        else {
            Log.d(LOG_TAG, "seekbarStatus(): SSPlayerFragment was null.");
        }
    }

    // playNextSong(): Signals the SSPlayerFragment to play the previous or next song in the
    // tracklist.
    private void playNextSong(Boolean isNext) {

        if (playerFragment != null) {
            try { ((OnMusicPlayerListener) playerFragment).playNextSong(isNext); }
            catch (ClassCastException cce) {} // Catch for class cast exception errors.
        }

        else {
            Log.d(LOG_TAG, "playNextSong(): SSPlayerFragment was null.");
        }
    }
}