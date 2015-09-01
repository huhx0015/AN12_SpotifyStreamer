package com.huhx0015.spotifystreamer.intent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** -----------------------------------------------------------------------------------------------
 *  [SSShareIntent] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSShareIntent class contains methods for creating an Intent for the purpose of
 *  sharing content to external applications.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSShareIntent {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSShareIntent.class.getSimpleName();

    /** INTENT FUNCTIONALITY ___________________________________________________________________ **/

    // shareIntent(): Prepares an Intent to share text data with external activities.
    public static void shareIntent(String trackName, String artistName, String spotifyUrl, Context context) {

        Log.d(LOG_TAG, "shareIntent(): Preparing content to share to external activites...");

        // Used for setting the subject and the message to display when sharing to external text
        // applications.
        String shareSubject;
        String shareMessage = "I'm currently checking out ";

        // Sets up an Intent to share the shortcut data with external activities.
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain"); // Specifies that this is a text type.

        // Generates the appropriate message to display for the share intent.
        if ( (artistName != null) && !(artistName.equals("")) ) {

            // If an artist and a track has been selected, both the track and artist names are
            // displayed in the share intent message.
            if ( (trackName != null) && !(trackName.equals("")) && (spotifyUrl != null) && !(spotifyUrl.equals("")) ) {

                // Sets the intent subject and message URL.
                shareSubject = "I'm currently listening to " + trackName + " by " + artistName + " on SPOTIFY STREAMER";
                shareMessage = spotifyUrl;

                // Adds the data to the intent.
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareSubject + ":\n" + shareMessage);
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(sendIntent, "Share my Spotify Streamer experience with: "));

                return;
            }

            // Displays only the artist name in the share intent message.
            else {
                shareMessage = "I'm currently checking out " + artistName + " on ";
            }
        }

        shareMessage = shareMessage + "SPOTIFY STREAMER.";

        // Adds the data to the intent.
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareMessage);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(sendIntent, "Share my Spotify Streamer experience with: "));
    }
}