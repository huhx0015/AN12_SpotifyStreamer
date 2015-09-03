package com.huhx0015.spotifystreamer.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.huhx0015.spotifystreamer.interfaces.OnMusicPlayerListener;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.services.SSMusicService;
import com.huhx0015.spotifystreamer.ui.toast.SSToast;

/** -----------------------------------------------------------------------------------------------
 *  [SSApplication] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSApplication class is a top-level class that runs when the application is launched
 *  and provides access to the SSMusicService throughout the life of the application.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSApplication extends Application implements OnMusicServiceListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // FRAGMENT VARIABLES
    private Fragment playerFragment; // References the player fragment attached to this service.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSApplication.class.getSimpleName();

    // SERVICE VARIABLES
    private Boolean serviceBound = false; // Used to determine if the SSMusicService is currently bound.
    private Intent audioIntent; // An Intent object that references the Intent for the SSMusicService.
    private SSMusicService musicService; // A service that handles the control of audio playback in the background.

    // SYSTEM VARIABLES
    private final int api_level = android.os.Build.VERSION.SDK_INT; // Used to determine the device's Android API version.

    // THREAD VARIABLES
    private Handler readySongTimerHandler = new Handler(); // Handler for the ready song timer thread.
    private static final int TIMEOUT_VALUE = 10; // Number of seconds until a timeout message is displayed.
    private int currentTimer = 0; // Number of seconds that has elapsed for the readySongTimerThread.

    /** SERVICE METHODS ________________________________________________________________________ **/

    // musicConnection(): A ServiceConnection object for managing the service connection states for
    // the SSMusicService service.
    private ServiceConnection musicConnection = new ServiceConnection() {

        // onServiceConnected(): Runs when the service is connected to the activity.
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            // Sets the binder for the service.
            SSMusicService.SSMusicBinder binder = (SSMusicService.SSMusicBinder) service;
            musicService = binder.getService();
            serviceBound = true; // Indicates that the service is bounded.
        }

        // onServiceDisconnected: Runs when the service is disconnected from the activity.
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false; // Indicates that the service is no longer bound.
        }
    };

    /** THREAD METHODS _________________________________________________________________________ **/

    // readySongTimerThread(): A threaded function which is run when playback of a song has been
    // initialized. It runs for a given time, where if the song has not already started playback,
    // a timeout error Toast message will be displayed.
    private Runnable readySongTimerThread = new Runnable() {

        public void run() {

            // Compares the current timer value with the TIMEOUT_VALUE. If it has not exceeded the
            // TIMEOUT_VALUE, the thread is looped again.
            if (currentTimer <= TIMEOUT_VALUE) {
                currentTimer++; // Increments the timer value.
                readySongTimerHandler.postDelayed(this, 1000); // Thread is run again in 1000 ms.
            }

            // Displays an timeout error message and stops the ready song timer thread.
            else {
                SSToast.toastyPopUp("The track could not be played due to a time-out error.", getApplicationContext());
                stopSongPrepare(); // Signals the SSPlayerFragment to stop song preparation conditions.
                currentTimer = 0; // Resets the current timer value.
                readySongTimerHandler.removeCallbacks(this);
            }
        }
    };

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // attachFragment(): Invoked by SSPlayerFragment to attach it to the SSMusicService.
    @Override
    public void attachFragment(Fragment fragment) {

        this.playerFragment = fragment; // Attaches the playerFragment to this class.

        // If the service has disconnected, the SSMusicService is restarted.
        if (!serviceBound) {
            setUpAudioService(); // Sets up the SSMusicService.
        }

        // Attaches the SSPlayerFragment.
        else {
            musicService.attachPlayerFragment(fragment);
        }
    }

    // pauseTrack(): Invoked by SSPlayerFragment to signal the SSMusicService to pause the song
    // stream.
    @Override
    public void pauseTrack(Boolean isStop) {

        // If the service has disconnected, the SSMusicService is restarted.
        if (!serviceBound) {
            setUpAudioService(); // Sets up the SSMusicService.
        }

        // Signals the SSMusicService to pause the song stream.
        else {
            musicService.pauseTrack(isStop);
        }
    }

    // stopSongPrepare(): Signals the SSPlayerFragment to hide the song preparation progress bar.
    private void stopSongPrepare() {

        if (playerFragment != null) {
            try { ((OnMusicPlayerListener) playerFragment).stopSongPrepare(); }
            catch (ClassCastException cce) {} // Catch for class cast exception errors.
        }
    }

    // playTrack(): Invoked by SSPlayerFragment to signal the SSMusicService to play the selected
    // stream.
    @Override
    public void playTrack(String url, Boolean loop, Bitmap albumImage, Boolean notiOn, String artist, String track) {

        // If the service has disconnected, the SSMusicService is restarted.
        if (!serviceBound) {
            setUpAudioService(); // Sets up the SSMusicService.
        }

        // Signals the SSMusicService to begin music playback of the selected track.
        else {
            musicService.playTrack(url, loop, albumImage, notiOn, artist, track);
        }
    }

    // removeAudioService(): Invoked by the SSMainActivity to stops the SSMusicService running in
    // the background.
    @Override
    public void removeAudioService() {

        if (serviceBound && audioIntent != null) {

            // Notifies SSMusicService to signal SSMusicEngine to release all resources used by the
            // internal MediaPlayer object.
            musicService.releaseMedia();

            stopService(audioIntent); // Stops the service.
            audioIntent = null;
            musicService = null;
        }
    }

    // setPosition(): Invoked by SSPlayerFragment to signal the SSMusicService to skip to the
    // selected position in the song.
    @Override
    public void setPosition(int position) {

        // If the service has disconnected, the SSMusicService is restarted.
        if (!serviceBound) {
            setUpAudioService(); // Sets up the SSMusicService.
        }

        // Signals the SSMusicService to set the song position.
        else {
            musicService.setPosition(position);
        }
    }

    // setUpAudioService(): Sets up the SSMusicService service for playing audio from the
    // SSMusicEngine class in the background.
    @Override
    public void setUpAudioService() {

        if (!serviceBound) {

            Log.d(LOG_TAG, "setUpAudioService(): Setting up SSMusicService...");

            // Sets up the service intent and begins the service.
            audioIntent = new Intent(this, SSMusicService.class); // Sets a Intent to the service.
            bindService(audioIntent, musicConnection, Context.BIND_AUTO_CREATE); // Binds the service.
            startService(audioIntent); // Starts the service.
        }
    }

    // startSongTimer(): Invoked by the SSPlayerFragment to start/stop the song timer thread.
    @Override
    public void startStopSongTimer(Boolean isStart) {

        // Starts the song timer thread.
        if (isStart) {
            readySongTimerHandler.postDelayed(readySongTimerThread, 1000);
        }

        // Stops the song timer thread.
        else {
            currentTimer = 0; // Resets the current timer value.
            readySongTimerHandler.removeCallbacks(readySongTimerThread);
        }
    }

    // updateNotification(): Invoked by the SSPlayerFragment to signal the SSMusicService to update
    // the notification player when the next/previous button is pressed from SSPlayerFragment.
    @Override
    public void updateNotification(String songUrl, Boolean notiOn, Bitmap albumImage, String artist, String track) {

        // A new notification player is only displayed if the device is running on Android API level
        // 21 (LOLLIPOP) or higher.
        if (serviceBound && notiOn && (api_level >= 21)) {
            musicService.initializeMediaSession(songUrl, albumImage, artist, track);
        }
    }

    // updatePlayer(): Invoked by the SSPlayerFragment to signal the SSMusicService & SSMusicEngine
    // to update the attached player fragment of the current song status and max song duration.
    @Override
    public void updatePlayer() {

        // If the service has disconnected, the SSMusicService is restarted.
        if (!serviceBound) {
            setUpAudioService(); // Sets up the SSMusicService.
        }

        // The SSPlayerFragment is updated of the current song status and max song duration via
        // SSMusicService & SSMusicEngine.
        else {
            musicService.updatePlayer();
        }
    }
}