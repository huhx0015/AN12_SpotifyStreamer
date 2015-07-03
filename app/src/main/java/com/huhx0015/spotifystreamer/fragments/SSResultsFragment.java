package com.huhx0015.spotifystreamer.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.huhx0015.spotifystreamer.R;
import java.util.List;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Michael Yoon Huh on 7/2/2015.
 */
public class SSResultsFragment extends Fragment {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private Activity currentActivity; // Used to determine the activity class this fragment is currently attached to.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSResultsFragment.class.getSimpleName();

    /** FRAGMENT FUNCTIONALITY _________________________________________________________________ **/

    // onAttach(): The initial function that is called when the Fragment is run. The activity is
    // attached to the fragment.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.currentActivity = activity; // Sets the currentActivity to attached activity object.
    }

    // onCreateView(): Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View tg_fragment_view = (ViewGroup) inflater.inflate(R.layout.ss_results_fragment, container, false);
        ButterKnife.bind(this, tg_fragment_view); // ButterKnife view injection initialization.

        setUpLayout(); // Sets up the layout for the fragment.

        // SPOTIFY ASYNCTASK INITIALIZATION:
        SSSearchSpotifyTask task = new SSSearchSpotifyTask();
        task.execute(); // Executes the AsyncTask.

        return tg_fragment_view;
    }

    // onDestroyView(): This function runs when the screen is no longer visible and the view is
    // destroyed.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this); // Sets all injected views to null.
    }

    /** LAYOUT FUNCTIONALITY ___________________________________________________________________ **/

    // setUpLayout(): Sets up the layout for the fragment.
    private void setUpLayout() {

    }

    /** ASYNCTASK FUNCTIONALITY ________________________________________________________________ **/

    /**
     * --------------------------------------------------------------------------------------------
     * [SSSearchSpotifyTask] CLASS
     * DESCRIPTION: This is an AsyncTask-based class that accesses and queries the Spotify API in
     * the background.
     * --------------------------------------------------------------------------------------------
     */

    public class SSSearchSpotifyTask extends AsyncTask<Void, Void, Void> {

        // doInBackground(): AsyncTask method which runs in the background.
        @Override
        protected Void doInBackground(Void... strings) {

            // Initializes the Spotify API and background service.
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // Accesses the background service to search for a specific artist.
            ArtistsPager results = service.searchArtists("Paul");

            List<Artist> artists = results.artists.items;

            // Retrieves the list of Artists found and sets it in the list.
            for (int i = 0; i < artists.size(); i++) {
                Artist artist = artists.get(i);
                Log.i(LOG_TAG, i + " " + artist.name);
            }

            return null;
        }
    }
}
