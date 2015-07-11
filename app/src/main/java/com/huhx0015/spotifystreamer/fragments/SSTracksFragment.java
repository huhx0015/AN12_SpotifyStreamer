package com.huhx0015.spotifystreamer.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.data.SSSpotifyAccessors;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.huhx0015.spotifystreamer.ui.adapters.SSResultsAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

/** -----------------------------------------------------------------------------------------------
 *  [SSTracksFragment] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSTracksFragment is a fragment class that is responsible for displaying the
 *  specified artist's top tracks via Spotify. The results are then loaded into the RecyclerView
 *  object that is handled by this fragment.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSTracksFragment extends Fragment {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private Activity currentActivity; // Used to determine the activity class this fragment is currently attached to.

    // FRAGMENT VARIABLES
    private String artistName = ""; // Stores the name of the artist.

    // LIST VARIABLES
    private List<SSSpotifyModel> songListResult = new ArrayList<>(); // Stores the track list result that is to be used for the adapter.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSTracksFragment.class.getSimpleName();

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_tracks_progress_indicator) ProgressBar progressIndicator;
    @Bind(R.id.ss_tracks_recycler_view) RecyclerView resultsList;
    @Bind(R.id.ss_tracks_status_text) TextView statusText;

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // SSTracksFragment(): Default constructor for the SSTracksFragment fragment class.
    private final static SSTracksFragment tracks_fragment = new SSTracksFragment();

    // SSTracksFragment(): Deconstructor method for the SSTracksFragment fragment class.
    public SSTracksFragment() {}

    // getInstance(): Returns the tracks_fragment instance.
    public static SSTracksFragment getInstance() { return tracks_fragment; }
    
    // initializeFragment(): Sets the initial values for the fragment.
    public void initializeFragment(String name) {
        this.artistName = name;
    }

    /** FRAGMENT LIFECYCLE METHODS _____________________________________________________________ **/

    // onAttach(): The initial function that is called when the Fragment is run. The activity is
    // attached to the fragment.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.currentActivity = activity; // Sets the currentActivity to attached activity object.
    }

    // onCreate(): Runs when the fragment is first started.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true); // Retains this fragment during runtime changes.
    }

    // onCreateView(): Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View ss_fragment_view = (ViewGroup) inflater.inflate(R.layout.ss_tracks_fragment, container, false);
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

        setUpRecyclerView(); // Sets up the RecyclerView object.

        // SPOTIFY ASYNCTASK INITIALIZATION: Searches for the artist's top 10 tracks if the ID is
        // valid.
        if (!artistName.isEmpty()) {
            SSSpotifyTrackSearchTask task = new SSSpotifyTrackSearchTask();
            task.execute(artistName); // Executes the AsyncTask.
        }
    }

    /** RECYCLERVIEW METHODS ___________________________________________________________________ **/

    // setListAdapter(): Sets the recycler list adapter based on the songList.
    private void setListAdapter(List<SSSpotifyModel> songList){
        SSResultsAdapter adapter = new SSResultsAdapter(songList, false, currentActivity);
        resultsList.setAdapter(adapter);
    }

    // setUpRecyclerView(): Sets up the RecyclerView object.
    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(currentActivity);
        resultsList.setLayoutManager(layoutManager);
    }

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // displayPreviousView(): Signals attached activity to display the SSArtistFragment view.
    private void displayPreviousView(String name, String id) {
        try { ((OnSpotifySelectedListener) currentActivity).displayTracksFragment(false, name); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    /** SUBCLASSES _____________________________________________________________________________ **/

    /**
     * --------------------------------------------------------------------------------------------
     * [SSSpotifyTrackSearchTask] CLASS
     * DESCRIPTION: This is an AsyncTask-based class that accesses and queries the Spotify Service
     * API in the background.
     * --------------------------------------------------------------------------------------------
     */

    public class SSSpotifyTrackSearchTask extends AsyncTask<String, Void, Void> {

        /** SUBCLASS VARIABLES _________________________________________________________________ **/

        // TRACK VARIABLES
        Boolean isError = false; // Used to determine if an error has occurred or not.
        Boolean tracksRetrieved = false; // Used to determine if track retrieval was successful or not.

        /** ASYNCTASK METHODS __________________________________________________________________ **/

        // onPreExecute(): This method runs on the UI thread just before the doInBackground method
        // executes.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            statusText.setVisibility(View.GONE); // Hides the status result TextView object.
            resultsList.setVisibility(View.GONE); // Hides the RecyclerView object.

            // Displays the progress indicator object.
            progressIndicator.setVisibility(View.VISIBLE);
        }

        // doInBackground(): This method constantly runs in the background while AsyncTask is
        // running.
        @Override
        protected Void doInBackground(final String... params) {

            Log.d(LOG_TAG, "SSSpotifyTrackSearchTask(): Beginning Spotify top tracks query...");

            // Initializes the Spotify API and background service.
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // Retrieves the artist's Spotify ID based on the search input.
            String artistId = SSSpotifyAccessors.queryArtist(params[0], service);

            // Retrieves the artist's top tracks as long as the artist ID is valid.
            if (artistId != null) {

                // Retrieves the artist's top tracks data from the Spotify background service.
                Tracks topTracks = SSSpotifyAccessors.retrieveArtistTopTracks(artistId, service);

                // If the track size is not empty, the top tracks are added into the songListResult
                // List object.
                if (topTracks.tracks.size() > 0) {

                    songListResult = new ArrayList<>(); // Creates a new ArrayList of song tracks.

                    // Adds the artist's top tracks into the List object.
                    songListResult = SSSpotifyAccessors.addArtistTopTracks(params[0], topTracks, songListResult);

                    // If the songListResult object is null, it indicates an error has occurred and
                    // that the artist's top track retrieval was a failure.
                    if (songListResult == null) {
                        isError = true;
                        tracksRetrieved = false;
                        return null;
                    }

                    // Indicates that the artist's top track retrieval was successful.
                    else {
                        tracksRetrieved = true;
                    }
                }

                // Indicates that the artist's top track retrieval was a failure.
                else {
                    tracksRetrieved = false;
                }
            }

            // Indicates that the artist's top track retrieval failed.
            else {
                tracksRetrieved = false;
            }

            return null;
        }

        // onPostExecute(): This method runs on the UI thread after the doInBackground operation has
        // completed.
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressIndicator.setVisibility(View.GONE); // Hides the progress indicator object.

            // Sets the list adapter for the RecyclerView object if the artist's top tracks data
            // retrieval was successful.
            if (tracksRetrieved) {

                // The RecyclerView object is made visible.
                resultsList.setVisibility(View.VISIBLE);

                setListAdapter(songListResult); // Sets the adapter for the RecyclerView object.
            }

            // Displays the status TextView object.
            else {

                // Sets an error message for the status TextView object.
                if (isError) {
                    statusText.setText(R.string.error_message); // Sets the text for the TextView object.
                }

                // Sets a "No results found." message for the status TextView object.
                else {
                    statusText.setText(R.string.no_results_tracks); // Sets the text for the TextView object.
                }

                statusText.setVisibility(View.VISIBLE); // Displays the TextView object.
            }
        }
    }
}