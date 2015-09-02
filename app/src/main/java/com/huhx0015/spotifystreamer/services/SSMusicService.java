package com.huhx0015.spotifystreamer.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
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
import com.huhx0015.spotifystreamer.audio.SSMusicEngine;
import com.huhx0015.spotifystreamer.interfaces.OnMusicPlayerListener;
import com.huhx0015.spotifystreamer.ui.notifications.SSNotificationPlayer;
import com.huhx0015.spotifystreamer.ui.toast.SSToast;

/** -----------------------------------------------------------------------------------------------
 *  [SSMusicService] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSMusicService is a service class that handles the playback of streaming Spotify
 *  tracks in the background.
 *  -----------------------------------------------------------------------------------------------
 */
public class SSMusicService extends Service {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES
    private SSMusicEngine ss_music; // SSMusicEngine class object that is used for music functionality.

    // FRAGMENT VARIABLES
    private Fragment playerFragment; // References the player fragment attached to this service.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = SSMusicService.class.getSimpleName(); // Used for logging output to logcat.

    // MEDIA ACTION VARIABLES:
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_REMOVE = "action_remove";
    public static final String ACTION_STOP = "action_stop";

    // MEDIA SESSION VARIABLES:
    private MediaController streamerMediaController;
    private MediaSession streamerMediaSession;
    private MediaSessionManager streamerMediaSessionManager;

    // SERVICE VARIABLES
    private final IBinder audioBind = new SSMusicBinder(); // IBinder object that is used to bind this service to an activity.

    // SYSTEM VARIABLES
    private final int api_level = android.os.Build.VERSION.SDK_INT; // Used to determine the device's Android API version.

    // THREAD VARIABLES
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

        // Processes the incoming intent as long as the streamerMediaSessionManager is not null.
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

    /** MUSIC ENGINE METHODS ___________________________________________________________________ **/

    // attachPlayerFragment(): Attaches the SSPlayerFragment to the SSMusicEngine class.
    public void attachPlayerFragment(Fragment fragment) {

        Log.d(LOG_TAG, "attachPlayerFragment(): Attaching the SSPlayerFragment to the service.");

        this.playerFragment = fragment; // Sets the SSPlayerFragment to this class.

        // Attaches the SSPlayerFragment to the SSMusicEngine instance.
        if (ss_music.getInstance() != null) {
            ss_music.getInstance().attachFragment(fragment);
        }
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

        seekHandler.removeCallbacks(seekbarThread); // Stops the seekbar update thread.
    }

    // playTrack(): Accesses the SSMusicEngine instance to play the streaming song track.
    public void playTrack(String songUrl, Boolean loop, Bitmap albumImage, Boolean notiOn,
                          String albumArtist, String albumTrack) {

        // Initiates music playback in SSMusicEngine.
        ss_music.getInstance().playSongUrl(songUrl, loop);

        // ANDROID API 21+: If notification playback has been enabled, the notification media player
        // is built and displayed for devices running ANDROID API 21 (LOLLIPOP) and above.
        if (notiOn && api_level >= 21) {
            initializeMediaSession(songUrl, albumImage, albumArtist, albumTrack);
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

    // intializeMediaSession(): Builds a new MediaSession for displaying a notification that users
    // can interact with and be able to control audio playback.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initializeMediaSession(final String songUrl, final Bitmap albumImage,
                                       final String albumArtist, final String albumTrack) {

        // Creates a new MediaSession.
        streamerMediaSession = new MediaSession(getApplicationContext(), "SPOTIFY STREAMER");
        streamerMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        streamerMediaController = new MediaController(getApplicationContext(), streamerMediaSession.getSessionToken());

        // Updates the current metadata for the track currently playing.
        streamerMediaSession.setMetadata(new MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, albumImage)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, albumArtist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, albumTrack)
                .putString(MediaMetadata.METADATA_KEY_TITLE, "Spotify Streamer")
                .build());

        streamerMediaSession.setActive(true); // Indicates that media commands can be received.

        // Attaches a new Callback to receive the MediaSession updates.
        streamerMediaSession.setCallback(new MediaSession.Callback() {

            // PLAY: Runs when the play action has been initiated.
            @Override
            public void onPlay() {
                super.onPlay();

                // Displays a Toast message, notifying the user that the song is already playing.
                if (ss_music.getInstance().isSongPlaying()) {
                    SSToast.toastyPopUp(albumTrack + " by " + albumArtist + " currently playing.", getApplicationContext());
                }

                // Plays the song track.
                else {
                    playTrack(songUrl, false, albumImage, true, albumArtist, albumTrack);
                }
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

            // STOP: Runs when the stop action has been initiated.
            @Override
            public void onStop() {
                super.onStop();
                pauseTrack(true); // Stops the currently playing song track.
            }
        });

        // Enables the ability to receive transport controls via the MediaSession Callback.
        streamerMediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Creates the notification with the media player controls.
        SSNotificationPlayer.createNotificationPlayer(streamerMediaSession, albumImage, albumArtist, albumTrack, this);
    }

    // processIntent(): If this service is invoked by external audio controls in the notification
    // menu, the audio player state is changed accordingly.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void processIntent(Intent intent) {

        // If the intent or the associated action is null, the method is terminated early.
        if (intent == null || intent.getAction() == null) {
            return;
        }

        // Determines the defined action to take from the incoming intent.
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

        // STOP:
        else if (trigger.equalsIgnoreCase(ACTION_STOP)) {
            streamerMediaController.getTransportControls().stop();
        }

        // REMOVE:
        else if (trigger.equalsIgnoreCase(ACTION_REMOVE)) {
            streamerMediaController.getTransportControls().stop();
            SSNotificationPlayer.removeNotifications(this); // Removes any active notification player.
        }
    }

    /** THREAD METHODS _________________________________________________________________________ **/

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