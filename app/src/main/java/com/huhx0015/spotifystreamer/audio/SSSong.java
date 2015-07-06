package com.huhx0015.spotifystreamer.audio;

/** -----------------------------------------------------------------------------------------------
 *  [SSSong] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class stores the song attributes, such as it's name and it's associated
 *  resource file.
 *  Code imported from my own HuhX Game Sound Engine project here:
 *  https://github.com/huhx0015/HuhX_Game_Sound_Engine
 *  -----------------------------------------------------------------------------------------------
 */

public class SSSong {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private int musicRes; // Stores the raw ID reference for the song resource.
    private String songName; // Stores the String name of the song.

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // SSSong(): Constructor method that initializes the SSSong object.
    public SSSong(String name, int resource) {
        musicRes = resource;
        songName = name;
    }

    // SSSong(): Deconstructor for SSSong class.
    public SSSong() {
        musicRes = 0;
        songName = null;
    }

    /** GET FUNCTIONALITY ______________________________________________________________________ **/

    // setMusicRes(): Sets the reference ID for the song.
    public void setMusicRes(int resource) { musicRes = resource; }

    // setSongName(): Sets the name of the song.
    public void setSongName(String name) { songName = name; }

    /** SET FUNCTIONALITY ______________________________________________________________________ **/

    // getMusicRes(): Retrieves the song reference ID.
    public int getMusicRes() { return musicRes; }

    // getSongName(): Retrieves the name of the song.
    public String getSongName() { return songName; }
}
