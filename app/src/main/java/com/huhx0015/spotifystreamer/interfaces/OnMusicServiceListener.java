package com.huhx0015.spotifystreamer.interfaces;

/**
 * Created by Michael Yoon Huh on 7/28/2015.
 */
public interface OnMusicServiceListener {

    void playTrack(String url, Boolean loop);

    void pauseTrack();

}
