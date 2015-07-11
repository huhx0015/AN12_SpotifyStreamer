package com.huhx0015.spotifystreamer.interfaces;

/**
 * -------------------------------------------------------------------------------------------------
 * [OnSpotifySelectedListener] INTERFACE
 * PROGRAMMER: Michael Yoon Huh (Huh X0015)
 * DESCRIPTION: This is an interface class that is used as a signalling conduit between the
 * SSMainActivity class and the SSArtistFragment and SSTracksFragment class.
 * -------------------------------------------------------------------------------------------------
 */

public interface OnSpotifySelectedListener {

    // updateArtistInput(): Interface method that is used to keep an update of the user's input in
    // the SSArtistsFragment view.
    void updateArtistInput(String name);

    // displayTracksFragment(): Interface method which signals the attached activity to switch the
    // fragment view between SSTracksFragment and SSArtistsFragment.
    void displayTracksFragment(Boolean isShow, String name);

    // displayPlayerFragment(): Interface method which signals the attached activity to switch the
    // fragment view between SSTracksFragment and the SSPlayerFragment.
    // TODO: Reserved for P2.
    void displayPlayerFragment(Boolean isShow, String artistName, String id, String songName,
                               String albumName, String imageURL, String streamURL);
}
