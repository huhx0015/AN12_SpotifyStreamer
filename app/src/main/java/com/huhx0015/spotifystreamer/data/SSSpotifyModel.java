package com.huhx0015.spotifystreamer.data;

import android.os.Parcel;
import android.os.Parcelable;

/** -----------------------------------------------------------------------------------------------
 *  [SSSpotifyModel] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSSpotifyModel is an object class that stores the artist song track data.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSSpotifyModel implements Parcelable {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private String album_image; // Stores the image URL of the album.
    private String artist; // Stores the name of the artist.
    private String album; // Stores the name of the album.
    private String song; // Stores the name of the song.
    private String songId; // Stores the id of the song.
    private String songURL; // Stores the URL of the song.

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // SSSpotifyModel(): Constructor method for the class.
    public SSSpotifyModel(String artist, String album, String song, String id, String sUrl, String image) {
        this.artist = artist;
        this.album = album;
        this.song = song;
        this.songId = id;
        this.songURL = sUrl;
        this.album_image = image;
    }

    /** PARCELABLE METHODS _____________________________________________________________________ **/

    // SSSpotifyModel(): A parcelable method for reading in data for the class.
    protected SSSpotifyModel(Parcel in) {
        album_image = in.readString();
        artist = in.readString();
        album = in.readString();
        song = in.readString();
        songId = in.readString();
        songURL = in.readString();
    }

    // Creator(): An interface that must be implemented and provided as a public CREATOR field that
    // generates instances of your Parcelable class from a Parcel.
    public static final Creator<SSSpotifyModel> CREATOR = new Creator<SSSpotifyModel>() {

        // createFromParcel(): Creates a new instance of the Parcelable class, instantiating it from
        // the given Parcel whose data had previously been written by Parcelable.writeToParcel().
        @Override
        public SSSpotifyModel createFromParcel(Parcel in) {
            return new SSSpotifyModel(in);
        }

        // newArray(): Creates a new array of the Parcelable class.
        @Override
        public SSSpotifyModel[] newArray(int size) {
            return new SSSpotifyModel[size];
        }
    };

    // describeContents(): Describe the kinds of special objects contained in this Parcelable's
    // marshalled representation.
    @Override
    public int describeContents() {
        return 0;
    }

    // writeToParcel(): Flattens this object in to a Parcel.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(album_image);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(song);
        dest.writeString(songId);
        dest.writeString(songURL);
    }

    /** GET / SET METHODS ______________________________________________________________________ **/

    // getAlbumImage(): Returns the album image URL.
    public String getAlbumImage() { return album_image; }

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

    // getSongId(): Returns the song ID.
    public String getSongId() {
        return songId;
    }

    // getSongURL(): Returns the song.
    public String getSongURL() {
        return songURL;
    }

    // setAlbumImage(): Sets the album image URL.
    public void setAlbumImage(String image) { this.album_image = image; }

    // setArtist(): Sets the artist name for the class.
    public void setArtist(String artist) { this.artist = artist; }

    // setAlbum(): Sets the album name for the class.
    public void setAlbum(String album) {
        this.album = album;
    }

    // setSong(): Sets the song name for the class.
    public void setSong(String song) {
        this.song = song;
    }

    // setSongId(): Sets the song ID for the class.
    public void setSongId(String id) { this.songId = id; }

    // setSongURL(): Sets the song URL for the class.
    public void setSongURL(String url) {
        this.songURL = url;
    }
}