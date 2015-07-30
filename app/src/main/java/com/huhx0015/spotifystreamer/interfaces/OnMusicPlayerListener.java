package com.huhx0015.spotifystreamer.interfaces;

/**
 * -------------------------------------------------------------------------------------------------
 * [OnMusicPlayerListener] INTERFACE
 * PROGRAMMER: Michael Yoon Huh (Huh X0015)
 * DESCRIPTION: This is an interface class that is used as a signalling conduit between the
 * SSMusicService class and the SSPlayerFragment class and contains methods for determining the
 * status of the SSMusicEngine component.
 * -------------------------------------------------------------------------------------------------
 */

public interface OnMusicPlayerListener {

    // playbackStatus(): Interface method that is used to determine the current song playback status
    // (whether it is currently playing in the background or not).
    void playbackStatus(Boolean isPlay);
}
