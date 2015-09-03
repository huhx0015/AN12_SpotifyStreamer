package com.huhx0015.spotifystreamer.interfaces;

/**
 * -------------------------------------------------------------------------------------------------
 * [OnSnackbarDisplayListener] INTERFACE
 * PROGRAMMER: Michael Yoon Huh (Huh X0015)
 * DESCRIPTION: This is an interface class that is used to signal the SSMainActivity class to
 * display a Snackbar or Toast message, depending on the state of the activity when the method is
 * invoked.
 * -------------------------------------------------------------------------------------------------
 */
public interface OnSnackbarDisplayListener {

    // displaySnackbar(): An interface method that signals the SSMainActivity to display either
    // a Snackbar or Toast message, depending if the activity is currently active or in a paused
    // state.
    void displaySnackbar(String message);
}
