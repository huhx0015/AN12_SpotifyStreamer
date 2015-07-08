package com.huhx0015.spotifystreamer.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.model.SSSpotifyAccessors;
import com.huhx0015.spotifystreamer.model.SSSpotifyModel;
import com.huhx0015.spotifystreamer.ui.SSResultsAdapter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Michael Yoon Huh on 7/2/2015.
 */
public class SSResultsFragment extends Fragment {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private Activity currentActivity; // Used to determine the activity class this fragment is currently attached to.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSResultsFragment.class.getSimpleName();

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_results_search_input) EditText searchInput;
    @Bind(R.id.ss_results_progress_indicator) ProgressBar progressIndicator;
    @Bind(R.id.ss_results_recycler_view) RecyclerView resultsList;

    /** FRAGMENT LIFECYCLE METHODS _____________________________________________________________ **/

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

        View ss_fragment_view = (ViewGroup) inflater.inflate(R.layout.ss_results_fragment, container, false);
        ButterKnife.bind(this, ss_fragment_view); // ButterKnife view injection initialization.

        setUpLayout(); // Sets up the layout for the fragment.

        return ss_fragment_view;
    }

    // onDestroyView(): This function runs when the screen is no longer visible and the view is
    // destroyed.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this); // Sets all injected views to null.
    }

    /** LAYOUT METHODS _________________________________________________________________________ **/

    // setUpLayout(): Sets up the layout for the fragment.
    private void setUpLayout() {
        setUpTextListener(); // Sets up the EditText listener for the fragment.
        setUpRecyclerView(); // Sets up the RecyclerView object.
    }

    // setUpTextListener(): Sets up the EditText listener for the fragment.
    private void setUpTextListener() {

        searchInput.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Retrieves the current string from the EditText object.
                String currentSearchInput = s.toString();

                // SPOTIFY ASYNCTASK INITIALIZATION:
                SSSearchSpotifyTask task = new SSSearchSpotifyTask();
                task.execute(currentSearchInput); // Executes the AsyncTask.
            }
        });
    }

    /** RECYCLERVIEW METHODS ___________________________________________________________________ **/

    // setListAdapter(): Sets the recycler list adapter based on the songList.
    private void setListAdapter(List<SSSpotifyModel> songList){
        SSResultsAdapter adapter = new SSResultsAdapter(songList, currentActivity);
        resultsList.setAdapter(adapter);
    }

    // setUpRecyclerView(): Sets up the RecyclerView object.
    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(currentActivity);
        resultsList.setLayoutManager(layoutManager);
    }

    /** ASYNCTASK METHODS ______________________________________________________________________ **/

    /**
     * --------------------------------------------------------------------------------------------
     * [SSSearchSpotifyTask] CLASS
     * DESCRIPTION: This is an AsyncTask-based class that accesses and queries the Spotify Service
     * API in the background.
     * --------------------------------------------------------------------------------------------
     */

    public class SSSearchSpotifyTask extends AsyncTask<String, Void, Void> {

        /** SUBCLASS VARIABLES _________________________________________________________________ **/

        // TRACK VARIABLES
        Boolean tracksRetrieved = false; // Used to determine if track retrieval was successful or not.
        List<SSSpotifyModel> songListResult = new ArrayList<>(); // Stores the track list result that is to be used for the adapter.

        /** ASYNCTASK METHODS __________________________________________________________________ **/

        // onPostExecute(): This method runs on the UI thread after the doInBackground operation has
        // completed.
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Sets the list adapter for the RecyclerView object if the artist's top tracks data
            // retrieval was successful.
            if (tracksRetrieved) {
                setListAdapter(songListResult); // Sets the adapter for the RecyclerView object.
            }
        }

        // doInBackground(): This method constantly runs in the background while AsyncTask is
        // running.
        @Override
        protected Void doInBackground(final String... params) {

            // Initializes the Spotify API and background service.
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // Retrieves the artist's Spotify ID based on the search input.
            String artistId = SSSpotifyAccessors.queryArtist(params[0], service);

            // Retrieves the artist's top tracks as long as the artist ID is valid.
            if (artistId != null) {

                // Retrieves the artist's top tracks data from the Spotify background service.
                Tracks topTracks = SSSpotifyAccessors.retrieveArtistTopTracks(artistId, service);

                if (topTracks.tracks.size() > 0) {

                    // Retrieves the list of Tracks found and sets it in the list.
                    for (int i = 0; i < topTracks.tracks.size(); i++) {

                        // Sets the current Track object.
                        Track currentTrack = topTracks.tracks.get(i);

                        try {

                            // Retrieves the song name, album name, and album image URL.
                            String songName = currentTrack.name;
                            String albumName = currentTrack.album.name.toString();
                            String albumURL = currentTrack.album.images.get(0).url.toString();

                            Log.i(LOG_TAG, "Track " + i + " Song Name: " + songName);
                            Log.i(LOG_TAG, "Track " + i + " Album Name: " + albumName);
                            Log.i(LOG_TAG, "Track " + i + " Album URL: " + albumURL);

                            // Adds the current track into the ArrayList object.
                            songListResult.add(new SSSpotifyModel(params[0], albumName, songName, albumURL));
                        }

                        // NullPointerException handler.
                        catch (NullPointerException e) {
                            e.printStackTrace();
                            Log.e(LOG_TAG, "SSSearchSpotifyTask(): ERROR: A null pointer exception occurred.");
                            return null;
                        }

                        // Indicates that the artist's top track retrieval was successful.
                        tracksRetrieved = true;
                    }
                }

                // Outputs an error logcat message, indicating that the Tracks object size was
                // invalid.
                else {
                    Log.e(LOG_TAG, "SSSearchSpotifyTask(): ERROR: The size of the Tracks object was invalid.");
                }
            }

            return null;
        }
    }
}