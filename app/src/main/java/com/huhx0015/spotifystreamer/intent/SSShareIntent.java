package com.huhx0015.spotifystreamer.intent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

    // shareIntent(): Prepares an Intent to share image data with external activities.
    public static void shareIntent(String trackName, String artistName, Context context) {

        Log.d(LOG_TAG, "shareIntent(): Preparing content to share to external activites...");

        // References the directory path where the image is stored.
        final String uploadFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/";
        String fullFilePath = uploadFilePath + "" + filename; // Sets the full file path.
        Bitmap albumBitmap; // References the food bitmap.

        // Retrieves the bitmap data from the file
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            albumBitmap = BitmapFactory.decodeFile(fullFilePath, options);
        }

        // Exception handler.
        catch (Exception e) {
            Log.e(LOG_TAG, "shareIntent(): ERROR: File could not be found.");
            return;
        }

        // Checks to see if the albumBitmap is null first.
        if (albumBitmap != null) {

            // Prepares the album bitmap to be shared via an Intent.
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            albumBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    albumBitmap, "SpotifyStreamer", null);
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
}