package com.huhx0015.spotifystreamer.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.services.SSMusicService;

/** -----------------------------------------------------------------------------------------------
 *  [SSApplication] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSApplication class is a top-level class that runs when the application is launched
 *  and provides access to the SSMusicService throughout the life of the application.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSApplication extends Application implements OnMusicServiceListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSApplication.class.getSimpleName();

    // SERVICE VARIABLES
    private Boolean serviceBound = false; // Used to determine if the SSMusicService is currently bound.
    private Intent audioIntent; // An Intent object that references the Intent for the SSMusicService.
    private SSMusicService musicService; // A service that handles the control of audio playback in the background.

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

        // onServiceDisconnnected: Runs when the service is disconnected from the activity.
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false; // Indicates that the service is no longer bound.
        }
    };

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // attachFragment(): Invoked by SSPlayerFragment to attach it to the SSMusicService.
    @Override
    public void attachFragment(Fragment fragment) {

        if (musicService != null) {
            musicService.attachPlayerFragment(fragment); // Attaches the SSPlayerFragment.
        }

        else {
            Log.e(LOG_TAG, "attachFragment(): SSPlayerFragment failed to attach.");
        }
    }

    // pauseTrack(): Invoked by SSPlayerFragment to signal the SSMusicService to pause the song
    // stream.
    @Override
    public void pauseTrack(Boolean isStop) {
        musicService.pauseTrack(isStop); // Signals the SSMusicService to pause the song stream.
    }

    // playTrack(): Invoked by SSPlayerFragment to signal the SSMusicService to play the selected
    // stream.
    @Override
    public void playTrack(String url, Boolean loop, Bitmap albumImage, Boolean notiOn, String artist, String track) {

        // Signals the SSMusicService to begin music playback of the selected track.
        musicService.playTrack(url, loop, albumImage, notiOn, artist, track);
    }

    // setPosition(): Invoked by SSPlayerFragment to signal the SSMusicService to skip to the
    // selected position in the song.
    @Override
    public void setPosition(int position) {
        musicService.setPosition(position); // Signals the SSMusicService to set the song position.
    }

    // removeAudioService(): Invoked by the SSMainActivity to stops the SSMusicService running in
    // the background.
    @Override
    public void removeAudioService() {

        if (audioIntent != null) {

            // Notifies SSMusicService to signal SSMusicEngine to release all resources used by the
            // internal MediaPlayer object.
            musicService.releaseMedia();

            stopService(audioIntent); // Stops the service.
            audioIntent = null;
            musicService = null;
        }
    }

    // setUpAudioService(): Sets up the SSMusicService service for playing audio from the
    // SSMusicEngine class in the background.
    @Override
    public void setUpAudioService() {

        if (audioIntent == null) {

            Log.d(LOG_TAG, "setUpAudioService(): Setting up SSMusicService...");

            // Sets up the service intent and begins the service.
            audioIntent = new Intent(this, SSMusicService.class); // Sets a Intent to the service.
            bindService(audioIntent, musicConnection, Context.BIND_AUTO_CREATE); // Binds the service.
            startService(audioIntent); // Starts the service.
        }
    }

    // updatePlayer(): Invoked by the SSPlayerFragment to signal the SSMusicService & SSMusicEngine
    // to update the attached player fragment of the current song status and max song duration.
    @Override
    public void updatePlayer() {
        musicService.updatePlayer();
    }
}
