package com.huhx0015.spotifystreamer.ui.actionbar;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import com.huhx0015.spotifystreamer.R;

/** -----------------------------------------------------------------------------------------------
 *  [SSActionBar] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSActionBar class contains methods for modifying the ActionBar properties.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSActionBar {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // TAG VARIABLES
    private static final String PLAYER_TAG = "PLAYER"; // Tag for SSPlayerFragment.
    private static final String SETTINGS_TAG = "SETTINGS"; // Tag for SSSettingsFragment.
    private static final String TRACKS_TAG = "TRACKS"; // Tag for SSTracksFragment.

    /** ACTION BAR METHODS _____________________________________________________________________ **/

    // setupActionBar(): Sets up the action bar attributes for the activity.
    public static void setupActionBar(Toolbar actionBar, ActionBarDrawerToggle drawerToggle,
                                      String actionType, String currentArtist, String subtitle,
                                      Boolean isBack) {

        if (actionBar != null) {

            // TRACKS:
            if (actionType.equals(TRACKS_TAG)) {
                actionBar.setTitle("Top 10 Tracks"); // Sets the title of the action bar.
                actionBar.setSubtitle(subtitle); // Sets the name of the current artist as the subtitle.
                setBackCarat(drawerToggle, isBack); // Displays/hides the back carat button.
            }

            // PLAYER:
            else if (actionType.equals(PLAYER_TAG)) {
                actionBar.setTitle("Now Playing"); // Sets the title of the action bar.
                actionBar.setSubtitle(currentArtist + " - " + subtitle); // Sets the name of the track as the subtitle.
                setBackCarat(drawerToggle, isBack); // Displays/hides the back carat button.
            }

            // SETTINGS:
            else if (actionType.equals(SETTINGS_TAG)) {
                actionBar.setTitle("Settings"); // Sets the title of the action bar.
                actionBar.setSubtitle(null); // Disables the subtitles of the action bar.
                setBackCarat(drawerToggle, isBack); // Displays/hides the back carat button.
            }

            // DEFAULT:
            else {
                actionBar.setTitle(R.string.app_name); // Sets the title of the action bar.
                actionBar.setSubtitle(null); // Disables the subtitles of the action bar.
                setBackCarat(drawerToggle, false); // Enables the drawer indicator.
            }
        }
    }

    // setBackCarat(): This method displays/hides the drawer indicator.
    private static void setBackCarat(final ActionBarDrawerToggle drawerToggle, Boolean isBack) {

        // Sets the back carat icon and disables the drawer indicator.
        if (isBack) {
            drawerToggle.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            drawerToggle.setDrawerIndicatorEnabled(false);
        }

        // Enables the drawer indicator.
        else {
            drawerToggle.setDrawerIndicatorEnabled(true); // Displays the drawer indicator.
        }
    }
}