package com.huhx0015.spotifystreamer.audio;

import android.util.Log;
import java.util.LinkedList;

/** -----------------------------------------------------------------------------------------------
 *  [SSMusicList] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class is responsible for providing methods to return the list of songs
 *  available for playback in this application.
 *  Code imported from my own HuhX Game Sound Engine project here:
 *  https://github.com/huhx0015/HuhX_Game_Sound_Engine
 *  -----------------------------------------------------------------------------------------------
 */

public class SSMusicList {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private static final String TAG = SSMusicList.class.getSimpleName(); // Used for logging output to logcat.

    /** CLASS FUNCTIONALITY ____________________________________________________________________ **/

    // ssMusicList(): Creates and returns an LinkedList of SSSong objects.
    public static LinkedList<SSSong> ssMusicList() {

        LinkedList<SSSong> musicList = new LinkedList<>(); // Creates a new LinkedList<SSSong> object.

        Log.d(TAG, "INITIALIZATION: List of songs has been constructed successfully.");

        return musicList;
    }
}
