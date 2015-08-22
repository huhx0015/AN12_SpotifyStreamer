package com.huhx0015.spotifystreamer.ui.actionbar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.huhx0015.spotifystreamer.R;

/** -----------------------------------------------------------------------------------------------
 *  [SSActionBar] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSActionBar class contains methods for modifying the actionbar properties.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSActionBar {

    // setupActionBar(): Sets up the action bar attributes for the activity.
    public static void setupActionBar(String actionType, String currentArtist, String subtitle,
                                      AppCompatActivity activity) {

        ActionBar actionBar = activity.getSupportActionBar(); // References the action bar.

        // TRACKS:
        if (actionType.equals("TRACKS")) {

            if (actionBar != null) {
                actionBar.setTitle("Top 10 Tracks"); // Sets the title of the action bar.
                actionBar.setSubtitle(subtitle); // Sets the name of the current artist as the subtitle.
                actionBar.setDisplayHomeAsUpEnabled(true); // Enables the back button in the action bar.
            }
        }

        // PLAYER:
        else if (actionType.equals("PLAYER")) {

            if (actionBar != null) {
                actionBar.setTitle("Now Playing"); // Sets the title of the action bar.
                actionBar.setSubtitle(currentArtist + " - " + subtitle); // Sets the name of the track as the subtitle.
                actionBar.setDisplayHomeAsUpEnabled(true); // Enables the back button in the action bar.
            }
        }

        // DEFAULT:
        else {

            if (actionBar != null) {
                actionBar.setTitle(R.string.app_name); // Sets the title of the action bar.
                actionBar.setSubtitle(null); // Disables the subtitles of the action bar.
                actionBar.setDisplayHomeAsUpEnabled(false); // Disables the back button in the action bar.
            }
        }
    }
}
