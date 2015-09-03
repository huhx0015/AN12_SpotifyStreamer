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

    // playCurrentSong(): Interface method that is used to signal the SSPlayerFragment to play the
    // current selected song in the tracklist.
    void playCurrentSong();

    // playNextSong(): Interface method that is used to signal the SSPlayerFragment to play the
    // next or previous song in the tracklist.
    void playNextSong(Boolean isNext, Boolean fromNotification);

    // seekbarStatus(): Interface method that is used to update the seekbar in the SSPlayerFragment
    // based on the current playback status of the Spotify streaming song.
    void seekbarStatus(int position);

    // setDuration(): Interface method that is used to determine the max song duration of the
    // Spotify streaming song.
    void setDuration(int duration);

    // stopSongPrepare(): Interface method that is used to signal SSPlayerFragment to stop preparing
    // the song.
    void stopSongPrepare(Boolean isStop);
}

