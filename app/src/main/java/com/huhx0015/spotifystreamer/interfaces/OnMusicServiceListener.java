package com.huhx0015.spotifystreamer.interfaces;

/**
 * -------------------------------------------------------------------------------------------------
 * [OnMusicServiceListener] INTERFACE
 * PROGRAMMER: Michael Yoon Huh (Huh X0015)
 * DESCRIPTION: This is an interface class that is used as a signalling conduit between the
 * SSMainActivity class and the SSPlayerFragment class for mananging the SSMusicService service
 * running in the background.
 * -------------------------------------------------------------------------------------------------
 */

public interface OnMusicServiceListener {

    // pauseTrack(): Interface method that signals the attached activity to pause the playback of a
    // Spotify track in the SSMusicService class.
    void pauseTrack(Boolean isStop);

    // playTrack(): Interface method that signals the attached activity to begin the playback of a
    // Spotify track in the SSMusicService class.
    void playTrack(String url, Boolean loop);
}
