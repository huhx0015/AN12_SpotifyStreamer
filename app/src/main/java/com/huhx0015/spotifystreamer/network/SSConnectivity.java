package com.huhx0015.spotifystreamer.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.net.InetAddress;

/** -----------------------------------------------------------------------------------------------
 *  [SSConnectivity] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSConnectivity is a class that contains methods for determining if the device has
 *  an active network and Internet connection.
 *  -----------------------------------------------------------------------------------------------
 */
public class SSConnectivity {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSConnectivity.class.getSimpleName();

    /** CONNECTIVITY METHODS ___________________________________________________________________ **/

    // checkConnectivity(): This method checks the current network state of the device and tests to
    // see if it is able to connect to the Internet.
    public static Boolean checkConnectivity(Context context) {

        // Sets up the ConnectivityManager for determining the current network state of the device.
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo(); // Retrieves the current network state.

        // An active network state was detected on the device, so Internet connectivity will be then
        // checked.
        if (info != null) {

            try {

                // Queries the Spotify API website to see if an active connection is available.
                InetAddress ipAddr = InetAddress.getByName("api.spotify.com");

                // Indicates that there is no active internet connection.
                if (ipAddr.equals("")) {
                    Log.e(LOG_TAG, "ERROR: checkConnectivity(): No active internet connection detected.");
                    return false;
                }

                // Indicates that an active internet connection is available.
                else {
                    Log.d(LOG_TAG, "checkConnectivity(): Internet handshake was successful.");
                    return true;
                }
            }

            // Error exception handler.
            catch (Exception e) {
                Log.e(LOG_TAG, "ERROR: checkConnectivity(): " + e);
                return false;
            }
        }

        // Indicates that there is no active network state on the device.
        else {
            Log.e(LOG_TAG, "ERROR: checkConnectivity(): No active network connection detected.");
            return false;
        }
    }
}