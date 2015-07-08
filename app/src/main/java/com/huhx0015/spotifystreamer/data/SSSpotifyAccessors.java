package com.huhx0015.spotifystreamer.data;

import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/** -----------------------------------------------------------------------------------------------
 *  [SSSpotifyAccessors] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSSpotifyAccessors class contains methods for accessing the Spotify service.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSSpotifyAccessors {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSSpotifyAccessors.class.getSimpleName();

    /** SPOTIFY METHODS ________________________________________________________________________ **/

    // addArtistTopTracks(): Adds the artist's top track data from the Track data object into the
    // List object.
    public static List<SSSpotifyModel> addArtistTopTracks(String artist,
                                                          Tracks topTracks,
                                                          List<SSSpotifyModel> songListResult) {

        // Retrieves the list of Tracks found and sets it in the list.
        for (int i = 0; i < topTracks.tracks.size(); i++) {

            // Sets the current Track object.
            Track currentTrack = topTracks.tracks.get(i);

            try {

                // Retrieves the song name, album name, and album image URL.
                String songName = currentTrack.name;
                String albumName = currentTrack.album.name;
                String albumURL = currentTrack.album.images.get(0).url;

                //Log.d(LOG_TAG, "Track " + i + " Song Name: " + songName);
                //Log.d(LOG_TAG, "Track " + i + " Album Name: " + albumName);
                //Log.d(LOG_TAG, "Track " + i + " Album URL: " + albumURL);

                // Adds the current track into the ArrayList object.
                songListResult.add(new SSSpotifyModel(artist, albumName, songName, albumURL));
            }

            // NullPointerException handler.
            catch (NullPointerException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "ERROR: addArtistTopTracks(): A null pointer exception occurred.");
                return null;
            }
        }

        return songListResult;
    }

    // queryArtist(): Queries the Spotify service to retrieve the artist's Spotify ID based on the
    // search input by the user.
    public static String queryArtist(String artist, SpotifyService service) {

        String artistId; // Stores artist ID value.

        // Accesses the Spotify service to search for a specific artist.
        ArtistsPager results = service.searchArtists(artist);

        // Retrieves the List of Artists returned from the search query.
        List<Artist> artists = results.artists.items;

        // Retrieves the top artist match from the list and retrieves the artist's ID string value.
        if (artists.size() > 0) {
            Artist currentArtist = artists.get(0); // Retrieves the first artist match in the List.
            artistId = currentArtist.id; // Sets the artist ID value from the first artist match.
        }

        // If the List of artists is empty, the artistId value is set to null.
        else {
            Log.d(LOG_TAG, "queryArtist(): The returned List<Artist> object contained no objects.");
            artistId = null;
        }

        return artistId;
    }

    // retrieveArtistTopTracks(): Retrieves the artist's top tracks data from the Spotify background
    // service.
    public static Tracks retrieveArtistTopTracks(String id, SpotifyService service) {

        // Creates a new HashMap object containing Spotify identifiers needed for querying for the
        // artist's top song tracks.
        Map<String, Object> artistInfo = new HashMap<>();
        artistInfo.put("country", "US"); // Sets the country identifier.

        // Retrieves the artist's top tracks from the Spotify background service.
        return service.getArtistTopTrack(id, artistInfo);
    }
}
