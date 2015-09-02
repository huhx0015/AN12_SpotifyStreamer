package com.huhx0015.spotifystreamer.interfaces;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

/**
 * -------------------------------------------------------------------------------------------------
 * [OnMusicServiceListener] INTERFACE
 * PROGRAMMER: Michael Yoon Huh (Huh X0015)
 * DESCRIPTION: This is an interface class that is used as a signalling conduit between the
 * SSApplication class and the SSPlayerFragment class for managing the SSMusicService service
 * running in the background.
 * -------------------------------------------------------------------------------------------------
 */

public interface OnMusicServiceListener {

    // attachFragment(): Interface method that signals the attached class to associate the specified
    // fragment to the SSMusicService class.
    void attachFragment(Fragment fragment);

    // pauseTrack(): Interface method that signals the attached class to pause the playback of a
    // Spotify track in the SSMusicService class.
    void pauseTrack(Boolean isStop);

    // playTrack(): Interface method that signals the attached class to begin the playback of a
    // Spotify track in the SSMusicService class.
    void playTrack(String url, Boolean loop, Bitmap albumImage, Boolean notiOn, String artist, String track);

    // removeAudioService(): Interface method that signals the attached class to signal
    // SSMusicEngine to remove all resources used by it's internal MediaPlayer object and to unbind
    // the SSMusicService altogether.
    void removeAudioService();

    // setPosition(): Interface method that signals the attached class to skip to the position of
    // a Spotify track in the SSMusicService class.
    void setPosition(int position);

    // setUpAudioService(): Interface method that signals the attached class to setup the
    // SSMusicService service for playing audio from the SSMusicEngine class in the background.
    void setUpAudioService();

    // startStopSongtimer(): Interface method that signals the attached class to start/stop the song
    // timer thread, which will display a timeout message if the song is not ready by a given time.
    void startStopSongTimer(Boolean isStart);

    // updateNotification(): Interface method that signals the attached class to update the
    // notification player when the next/previous button is pressed from SSPlayerFragment.
    void updateNotification(String songUrl, Boolean notiOn, Bitmap albumImage, String artist, String track);

    // updatePlayer(): Interface method that signals the attached class to update the player on the
    // current song playback status and max song duration.
    void updatePlayer();
}
