package com.huhx0015.spotifystreamer.model;

import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Michael Yoon Huh on 7/7/2015.
 */

public class SSSpotifyAccessors {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSSpotifyAccessors.class.getSimpleName();

    /** SPOTIFY METHODS ________________________________________________________________________ **/

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

        // Creates a new HashMap object containing Spotify identifiers needed for querying
        // for the artist's top song tracks.
        Map<String, Object> artistInfo = new HashMap<>();
        artistInfo.put("country", "US"); // Sets the country identifier.

        // Retrieves the artist's top tracks from the Spotify background service.
        return service.getArtistTopTrack(id, artistInfo);
    }
}
