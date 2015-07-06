package com.huhx0015.spotifystreamer.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import java.util.LinkedList;

/** -----------------------------------------------------------------------------------------------
 *  [SSMusicEngine] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSMusicEngine class is used to support music playback for the application.
 *  Code imported from my own HuhX Game Sound Engine project here:
 *  https://github.com/huhx0015/HuhX_Game_Sound_Engine
 *  -----------------------------------------------------------------------------------------------
 */

public class SSMusicEngine {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES:
    private MediaPlayer backgroundSong; // MediaPlayer variable for background song.
    private String currentSong; // Used for determining what song is playing in the background.
    private boolean isPaused; // Used for determining if a song has been paused.
    public int songPosition; // Used for resuming playback on a song that was paused.
    public boolean musicOn; // Used for determining whether music is playing in the background.

    // SYSTEM VARIABLES:
    private Context context; // Context for the instance in which this class is used.
    private static final String TAG = SSMusicEngine.class.getSimpleName(); // Used for logging output to logcat.

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // SSMusicEngine(): Constructor for SSMusicEngine class.
    private final static SSMusicEngine ss_music = new SSMusicEngine();

    // SSMusicEngine(): Deconstructor for SSMusicEngine class.
    private SSMusicEngine() {}

    // getInstance(): Returns the ss_sounds instance.
    public static SSMusicEngine getInstance() { return ss_music; }

    // initializeAudio(): Initializes the SSMusicEngine class variables.
    public void initializeAudio(Context con) {

        Log.d(TAG, "INITIALIZING: Initializing music engine.");

        context = con; // Context for the instance in which this class is used.
        backgroundSong = new MediaPlayer(); // Instantiates the main MediaPlayer object.
        isPaused = false; // Indicates that the song is not paused by default.
        musicOn = true; // Indicates that music playback is enabled by default.
        currentSong = "STOPPED"; // Sets the "STOPPED" condition for the song name string.
        songPosition = 0; // Sets the song position to the beginning of the song by default.

        Log.d(TAG, "INITIALIZING: Music engine initialization complete.");
    }

    /** MUSIC FUNCTIONALITY ____________________________________________________________________ **/

    // playSongName(): Plays the music file based on the specified String songName. The song is
    // changed only if the specified song does not match the current song that is playing.
    // Set loop variable to true to enable infinite song looping.
    // TRUE: Loops the song infinitely.
    // FALSE: Disables song looping.
    public void playSongName(String songName, Boolean loop) {

        boolean musicFound = false; // Used for determining if a corresponding song for songName was found or not.
        int songID = 0; // Used for storing the reference ID to the raw music resource object.

        // If the music option has been enabled, a song is selected based on the passed in songName string.
        if (musicOn == true) {

            LinkedList<SSSong> songList = SSMusicList.ssMusicList(); // Retrieves the list of songs.
            final int NUM_SONGS = songList.size(); // Retrieves the number of songs in the list.

            // Checks to see if the song list is valid or not.
            if (NUM_SONGS < 1) {
                Log.d(TAG, "ERROR: The songlist doesn't contain any valid song objects. Has the song list been populated?");
                return;
            }

            // Loops through the song list to find the specified song in the pre-defined music list.
            for (int i = 0; i < NUM_SONGS; i++) {

                String retrievedSong = songList.get(i).getSongName(); // Retrieves the song name string.

                // Compares the specified song name against the current song name in the list.
                if (retrievedSong.equals(songName)) {

                    // Checks to see if the specified song is already playing.
                    if (!currentSong.equals(songName)) {

                        songID = songList.get(i).getMusicRes(); // Sets the music resource file reference.

                        // Checks to see if the songID is a valid reference ID.
                        if (songID == 0) {

                            Log.d(TAG, "ERROR: Invalid song reference ID was found. ID was " + songID + ".");
                            return;
                        }

                        // Sets the currentSong to be the name of the specified song and sets the
                        // value of musicFound to be true.
                        else {

                            currentSong = songName;
                            musicFound = true;

                            Log.d(TAG, "PREPARING: Specified song " + songName + " was found.");
                        }
                    }

                    // Indicates that the specified song is already playing and the operation is
                    // cancelled.
                    else {
                        Log.d(TAG, "ERROR: Specified song " + songName + " is already playing!");
                        return;
                    }
                }
            }

            // If a song match was found, play the music file from resources.
            if ( (musicFound) || (isPaused) ) {
                playSong(songID, loop); // Calls playSong to create a MediaPlayer object and play the song.
            }

            // Outputs a message to logcat indicating that the song could not be found.
            else {
                Log.d(TAG, "ERROR: Specified song " + songName + " was not found. Please specify a valid song name.");
            }
        }

        // Outputs a message to logcat indicating that the song cannot be played.
        else {
            Log.d(TAG, "ERROR: Song cannot be played. Music engine is currently disabled.");
        }
    }

    // isSongPlaying(): Determines if a song is currently playing in the background.
    public Boolean isSongPlaying() {
        if (backgroundSong.isPlaying()) { return true; }
        else { return false; }
    }

    // pauseSong(): Pauses any songs playing in the background and returns it's position.
    public void pauseSong() {

        Log.d(TAG, "MUSIC: Music playback has been paused.");

        // Checks to see if mapSong has been initialized first before saving the song position and pausing the song.
        if (backgroundSong != null) {

            songPosition = backgroundSong.getCurrentPosition(); // Retrieves the current song position and saves it.

            // Pauses the song only if there is a song is currently playing.
            if (backgroundSong.isPlaying()) { backgroundSong.pause(); } // Pauses the song.

            isPaused = true; // Indicates that the song is currently paused.
            currentSong = "PAUSED";
        }
    }

    //  playSong(): Sets up a MediaPlayer object and begins playing the song in the background thread.
    private void playSong(final int songName, boolean loop) {

        // Checks to see if the MediaPlayer class has been instantiated first before playing a song.
        // This is to prevent a rare null pointer exception bug.
        if (backgroundSong == null) {

            Log.d(TAG, "WARNING: MediaPlayer object was null. Re-initializing MediaPlayer object.");
            backgroundSong = new MediaPlayer();
        }

        else {

            // Stops any songs currently playing in the background.
            if (backgroundSong.isPlaying()) {
                Log.d(TAG, "PREPARING: Song currently playing in the background. Stopping playback before switching to a new song.");
                backgroundSong.stop();
            }

            // Sets up the MediaPlayer object for the song to be played.
            releaseMedia(); // Releases MediaPool resources.
            backgroundSong = new MediaPlayer(); // Initializes the MediaPlayer.
            backgroundSong.setAudioStreamType(AudioManager.STREAM_MUSIC); // Sets the audio type for the MediaPlayer object.

            Log.d(TAG, "PREPARING: MediaPlayer stream type set to STREAM_MUSIC.");

            backgroundSong = MediaPlayer.create(context, songName); // Sets up the MediaPlayer for the song.
            backgroundSong.setLooping(loop); // Enables infinite looping of music.

            Log.d(TAG, "PREPARING: Loop condition has been set to " + loop + ".");

            // If the song was previously paused, resume the song at it's previous location.
            if (isPaused) {

                Log.d(TAG, "PREPARING: Song was previously paused, resuming song playback.");

                backgroundSong.seekTo(songPosition); // Jumps to the position where the song left off.
                songPosition = 0; // Resets songPosition variable after song's position has been set.
                isPaused = false; // Indicates that the song is no longer paused.
            }

            // Sets up the listener for the MediaPlayer object. Song playback begins immediately
            // once the MediaPlayer object is ready.
            backgroundSong.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    Log.d(TAG, "MUSIC: Song playback has begun.");
                    mediaPlayer.start(); // Begins playing the song.
                }
            });
        }
    }

    // releaseMedia(): Used to release the resources being used by mediaPlayer objects.
    public void releaseMedia() {

        // Releases MediaPool resources.
        if (backgroundSong != null) {

            backgroundSong.reset();
            backgroundSong.release();
            backgroundSong = null;

            Log.d(TAG, "RELEASE: MediaPlayer object has been released.");
        }

        else {
            Log.d(TAG, "ERROR: MediaPlayer object is null and cannot be released.");
        }
    }

    //  stopSong(): Stops any songs playing in the background.
    public void stopSong() {

        // Checks to see if mapSong has been initiated first before stopping song playback.
        if ( (backgroundSong != null) && (musicOn) ) {
            backgroundSong.stop(); // Stops any songs currently playing in the background.
            currentSong = "STOPPED";
            Log.d(TAG, "MUSIC: Song playback has been stopped.");
        }

        else {
            Log.d(TAG, "ERROR: Cannot stop song, as MediaPlayer object is already null.");
        }
    }
}