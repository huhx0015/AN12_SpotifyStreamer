package com.huhx0015.spotifystreamer.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

    // SERVICE VARIABLES
    private final IBinder audioBind = new SSMusicBinder(); // IBinder object that is used to bind this service to an activity.

    // THREADING VARIABLES
    private Handler seekHandler = new Handler(); // Handler for the seekbar update thread.

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private MediaSessionManager streamerMediaSessionManager;
    private MediaSession streamerMediaSession;
    private MediaController streamerMediaController;
    
    /** SERVICE LIFECYCLE METHODS ______________________________________________________________ **/

    // onCreate(): The system calls this method when the service is first created, to perform
    // one-time setup procedures (before it calls either onStartCommand() or onBind()).
    @Override
    public void onCreate() {
        super.onCreate();

        // AUDIO CLASS INITIALIZATION:
        ss_music.getInstance().initializeAudio(getApplicationContext());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( streamerMediaSessionManager == null ) {
           initializeMediaSession();
            //initMediaSessions();
        }

        handleIntent( intent );
        return super.onStartCommand(intent, flags, startId);
    }


    /** SERVICE EXTENSION METHODS ______________________________________________________________ **/

    @Override
    public void onPrepared(MediaPlayer mp) {}

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

    /** SERVICE METHODS ________________________________________________________________________ **/

    // attachPlayerFragment(): Attaches the SSPlayerFragment to the SSMusicEngine class.
    public void attachPlayerFragment(Fragment fragment) {
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
            }

            // PAUSE:
            else {
                ss_music.getInstance().pauseSong();
            }
        }

        seekHandler.removeCallbacks(seekbarThread); // Stops the seekbar update thread.
    }

    // playTrack(): Accesses the SSMusicEngine instance to play the streaming song track.
    public void playTrack(String songUrl, Boolean loop) {

        // Initiates music playback in SSMusicEngine.
        ss_music.getInstance().playSongUrl(songUrl, loop);
        this.songURL = songUrl;

        initializeMediaSession(); // TODO: TEST!

        seekHandler.postDelayed(seekbarThread, 1000); // Begins the seekbar update thread.
    }

    // setPosition(): Accesses the SSMusicEngine instance to update the song position.
    public void setPosition(int position){
        ss_music.getInstance().setSongPosition(position);
    }

    /** MEDIA SESSION METHODS __________________________________________________________________ **/

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase(ACTION_PLAY) ) {
            streamerMediaController.getTransportControls().play();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            streamerMediaController.getTransportControls().pause();
        } else if( action.equalsIgnoreCase( ACTION_FAST_FORWARD ) ) {
            streamerMediaController.getTransportControls().fastForward();
        } else if( action.equalsIgnoreCase( ACTION_REWIND ) ) {
            streamerMediaController.getTransportControls().rewind();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            streamerMediaController.getTransportControls().skipToPrevious();
        } else if( action.equalsIgnoreCase(ACTION_NEXT ) ) {
            streamerMediaController.getTransportControls().skipToNext();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            streamerMediaController.getTransportControls().stop();
        }
    }

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
            case 4:
                //fast forward tracks
                action = new Intent(ACTION_FAST_FORWARD);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 4, action, 0);
                return pendingIntent;
            case 5:
                //rewind tracks
                action = new Intent(ACTION_REWIND);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 5, action, 0);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initializeMediaSession() {

        // Creates a new MediaSession.
        streamerMediaSession = new MediaSession(getApplicationContext(), "SPOTIFY STREAMER");
        streamerMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        streamerMediaController = new MediaController(getApplicationContext(), streamerMediaSession.getSessionToken());

        // Updates the current metadata for the track currently playing.
        streamerMediaSession.setMetadata(new MediaMetadata.Builder()
                //.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, artwork)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "Pink Floyd")
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "Dark Side of the Moon")
                .putString(MediaMetadata.METADATA_KEY_TITLE, "SPOTIFY STREAMER")
                .build());

        streamerMediaSession.setActive(true); // Indicates that media commands can be received.

        // Attaches a new Callback to receive the MediaSession updates.
        streamerMediaSession.setCallback(new MediaSession.Callback() {

            @Override
            public void onPlay() {
                super.onPlay();
                playTrack(songURL, false);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseTrack(false);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                playNextSong(true);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                playNextSong(false);
            }

            @Override
            public void onStop() {
                super.onStop();
                //Log.e(Constants.LOG_TAG, "onStop");
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                Intent intent = new Intent(getApplicationContext(), SSMusicService.class);
                stopService(intent);
            }
        });

        // Enables the ability to receive transport controls via the MediaSession Callback.
        streamerMediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        createNotificationPlayer(streamerMediaSession);
    }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNotificationPlayer(MediaSession streamerMediaSession) {

        // Create a new Notification
        final Notification noti = new Notification.Builder(this)
                // Hide the timestamp
                .setShowWhen(false)
                        // Set the Notification style
                .setStyle(new Notification.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(streamerMediaSession.getSessionToken())
                                // Show our playback controls in the compat view
                        .setShowActionsInCompactView(0, 1, 2))
                        // Set the Notification color
                .setColor(0xFFDB4437)
                        // Set the large and small icons
                //.setLargeIcon(artwork)
                .setSmallIcon(R.mipmap.ic_launcher)
                        // Set Notification content information
                .setContentText("Coldplay")
                .setContentInfo("A Sky Full of Stars")
                .setContentTitle("Spotify Streamer")
                        // Add some playback controls
                .addAction(android.R.drawable.ic_media_previous, "Previous", triggerPlaybackAction(3))
                .addAction(android.R.drawable.ic_media_play, "Play", triggerPlaybackAction(0))
                .addAction(android.R.drawable.ic_media_pause, "Pause", triggerPlaybackAction(1))
                .addAction(android.R.drawable.ic_media_next, "Next", triggerPlaybackAction(2))
                .build();

        // Do something with your TransportControls
        final MediaController.TransportControls controls = streamerMediaSession.getController().getTransportControls();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, noti);
    }

    /** THREADING METHODS ______________________________________________________________________ **/

    // seekbarThread(): A threaded function which updates the player seekbar in the
    // SSPlayerFragment.
    private Runnable seekbarThread = new Runnable() {

        public void run() {

            // Retrieves the current song position.
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
    }

    //
    private void playNextSong(Boolean isNext) {

        if (playerFragment != null) {
            try { ((OnMusicPlayerListener) playerFragment).playNextSong(isNext); }
            catch (ClassCastException cce) {} // Catch for class cast exception errors.
        }

    }
}