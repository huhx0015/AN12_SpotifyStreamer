package com.huhx0015.spotifystreamer.model;

/**
 * Created by Michael Yoon Huh on 7/2/2015.
 */
public class SSSpotifyModel {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private String artist; // Stores the name of the artist.
    private String album; // Stores the name of the album.
    private String song; // Stores the name of the song.

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // SSSpotifyModel(): Constructor method for the class.
    public SSSpotifyModel(String artist, String album, String song) {
        this.artist = artist;
        this.album = album;
        this.song = song;
    }

    /** GET / SET METHODS ______________________________________________________________________ **/

    // getArtist(): Returns the artist.
    public String getArtist() {
        return artist;
    }

    // getAlbum(): Returns the album.
    public String getAlbum() {
        return album;
    }

    // getSong(): Returns the song.
    public String getSong() {
        return song;
    }

    // setArtist(): Sets the artist name for the class.
    public void setArtist(String artist) {
        this.artist = artist;
    }

    // setAlbum(): Sets the album name for the class.
    public void setAlbum(String album) {
        this.album = album;
    }

    // setSong(): Sets the song name for the class.
    public void setSong(String song) {
        this.song = song;
    }
}