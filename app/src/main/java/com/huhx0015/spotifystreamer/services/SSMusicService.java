package com.huhx0015.spotifystreamer.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.huhx0015.spotifystreamer.audio.SSMusicEngine;

/**
 * Created by Michael Yoon Huh on 7/28/2015.
 */
public class SSMusicService extends Service implements MediaPlayer.OnPreparedListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES
    private SSMusicEngine ss_music; // SSMusicEngine class object that is used for music functionality.
    private String currentSong = "NONE"; // Sets the default song for the activity.
    private Boolean musicOn = true; // Used to determine if music has been enabled or not.
    private Boolean isPlaying = false; // Indicates that a song is currently playing in the background.

    /** SERVICE LIFECYCLE METHODS ______________________________________________________________ **/

    @Override
    public void onCreate() {
        super.onCreate();

        setUpPlayer();
    }

    /** MEDIAPLAYER EXTENSION METHODS __________________________________________________________ **/

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return audioBind;
        //return null;
    }

    @Override
    public boolean onUnbind(Intent intent){

        // Releases all audio-related instances if the service is unbound.
        ss_music.getInstance().releaseMedia();

        return false;
    }

    /** SERVICE METHODS ________________________________________________________________________ **/

    private void setUpPlayer() {

        // AUDIO CLASS INITIALIZATION:
        ss_music.getInstance().initializeAudio(getApplicationContext());


    }

    /** SUBCLASS METHODS **/

    /*

    Base class for a remotable object, the core part of a lightweight remote procedure call mechanism
    defined by IBinder. This class is an implementation of IBinder that provides standard local
    implementation of such an object. Most developers will not implement this class directly, instead
    using the aidl tool to describe the desired interface, having it generate the appropriate Binder
    subclass. You can, however, derive directly from Binder to implement your own custom RPC protocol
    or simply instantiate a raw Binder object directly to use as a token that can be shared across
    processes. This class is just a basic IPC primitive; it has no impact on an application's
    lifecycle, and is valid only as long as the process that created it continues to run. To use this
    correctly, you must be doing so within the context of a top-level application component (a Service,
    Activity, or ContentProvider) that lets the system know your process should remain running. You
    must keep in mind the situations in which your process could go away, and thus require that you
    later re-create a new Binder and re-attach it when the process starts again. For example, if you
     are using this within an Activity, your activity's process may be killed any time the activity
     is not started; if the activity is later re-created you will need to create a new Binder and hand
     it back to the correct place again; you need to be aware that your process may be started for
      another reason (for example to receive a broadcast) that will not involve re-creating the activity
       and thus run its code to create a new Binder.

    http://developer.android.com/reference/android/os/Binder.html
     */

    public class SSMusicBinder extends Binder {

        public SSMusicService getService() {
            return SSMusicService.this;
        }
    }

    private final IBinder audioBind = new SSMusicBinder();


    // playTrack():
    public void playTrack(String songUrl, Boolean loop){

        ss_music.getInstance().playSongUrl(songUrl, loop); //play a song
    }

    // pauseTrack():
    public void pauseTrack() {

        // Pauses the song that is currently playing in the background.
        if (ss_music.getInstance().isSongPlaying()) {
            ss_music.getInstance().pauseSong();
        }

    }
}
