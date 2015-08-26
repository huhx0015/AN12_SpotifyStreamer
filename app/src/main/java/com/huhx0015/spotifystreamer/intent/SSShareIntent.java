package com.huhx0015.spotifystreamer.intent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import java.io.ByteArrayOutputStream;

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
    public static void shareIntent(String trackName, String artistName, Bitmap currentBitmap, Context context) {

        Log.d(LOG_TAG, "shareIntent(): Preparing content to share to external activites...");

        // Used for setting the message to display when sharing to external text applications.
        String shareMessage = "I'm currently checking out ";

        // Generates the appropriate message to display for the share intent.
        if ( (artistName != null) && !(artistName.equals("")) ) {

            // If an artist and a track has been selected, both the track and artist names are
            // displayed in the share intent message.
            if ( (trackName != null) && !(trackName.equals("")) ) {

                // Creates an Intent to share the current album image with external activities.
                shareBitmapIntent(trackName, artistName, currentBitmap, context);
                return;
            }

            // Displays only the artist name in the share intent message.
            else {
                shareMessage = "I'm currently checking out " + artistName + " on ";
            }
        }

        shareMessage = shareMessage + "SPOTIFY STREAMER.";

        // Sets up an Intent to share the shortcut data with external activities.
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain"); // Specifies that this is a text type.
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(sendIntent, "Share my Spotify Streamer experience with: "));
    }

    // shareBitmapIntent(): Creates an Intent to share image data with external activities.
    public static void shareBitmapIntent(String trackName, String artistName, Bitmap imageBitmap, Context context) {

        // Checks to see if the imageBitmap is null first.
        if (imageBitmap != null) {

            // Prepares the image bitmap to be shared via an Intent.
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    imageBitmap, "SpotifyStreamer", null);
            Uri imageUri = Uri.parse(path);

            // Sets up an Intent to share the shortcut data with external activities.
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("image/jpg"); // Specifies that this is a image type.
            sendIntent.putExtra(Intent.EXTRA_TEXT, "I'm currently listening to " + trackName + " by " + artistName + " on SPOTIFY STREAMER.");
            sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(sendIntent, "Share my Spotify Streamer experience with: "));
        }
    }

    public static void shareUrlIntent(Context context) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text shared.</p>"));
        context.startActivity(Intent.createChooser(sharingIntent,"Share using"));
    }
}