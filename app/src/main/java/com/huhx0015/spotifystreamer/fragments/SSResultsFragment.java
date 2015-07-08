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
import android.widget.TextView;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.data.SSSpotifyAccessors;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.ui.adapters.SSResultsAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

/** -----------------------------------------------------------------------------------------------
 *  [SSResultsFragment] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSResultsFragment is a fragment class that is responsible for displaying the input
 *  field in which a user can search for an artist's top tracks via Spotify. The results are then
 *  loaded into the RecyclerView object that is handled by this fragment.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSResultsFragment extends Fragment {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private Activity currentActivity; // Used to determine the activity class this fragment is currently attached to.

    // LAYOUT VARIABLES
    private Boolean isInputEmpty = true; // Used to determine if the EditText input field is empty or not.

    // LIST VARIABLES
    private List<SSSpotifyModel> songListResult = new ArrayList<>(); // Stores the track list result that is to be used for the adapter.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSResultsFragment.class.getSimpleName();

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_results_search_input) EditText searchInput;
    @Bind(R.id.ss_results_progress_indicator) ProgressBar progressIndicator;
    @Bind(R.id.ss_results_recycler_view) RecyclerView resultsList;
    @Bind(R.id.ss_results_status_text) TextView statusText;

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

            // afterTextChanged(): This method is run after the EditText input has changed.
            public void afterTextChanged(Editable s) {}

            // beforeTextChanged(): This method is runs just before the EditText input changes.
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // onTextChanged(): This method is run when the EditText input changes.
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Retrieves the current string from the EditText object.
                String currentSearchInput = s.toString();

                // Performs a Spotify service search request as long as the input string is not
                // empty.
                if (!currentSearchInput.isEmpty()) {

                    isInputEmpty = false; // Indicates that the input field is not empty.

                    // SPOTIFY ASYNCTASK INITIALIZATION:
                    SSSearchSpotifyTask task = new SSSearchSpotifyTask();
                    task.execute(currentSearchInput); // Executes the AsyncTask.
                }

                // The visibility of the RecyclerView object is set to be hidden.
                else {
                    isInputEmpty = true; // Indicates that the input field is empty.
                    resultsList.setVisibility(View.GONE); // Hides the RecyclerView object.
                }
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

    /** SUBCLASSES _____________________________________________________________________________ **/

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

                // Outputs an error logcat message, indicating that the Tracks object size was
                // invalid.
                else {

                    // Indicates that the artist's top track retrieval failed.
                    isError = true;
                    tracksRetrieved = false;
                    Log.e(LOG_TAG, "ERROR: SSSearchSpotifyTask(): The size of the Tracks object was invalid.");
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
            if (tracksRetrieved && !isInputEmpty) {

                // The RecyclerView object is made visible.
                resultsList.setVisibility(View.VISIBLE);

                setListAdapter(songListResult); // Sets the adapter for the RecyclerView object.
            }

            // If the user clears the input string before the artist's top track query is completed,
            // the adapter for the RecyclerView is not set and the RecyclerView is hidden.
            else if (isInputEmpty) {
                resultsList.setVisibility(View.GONE);
            }

            // Displays the status TextView object.
            else {

                // Sets an error message for the status TextView object.
                if (isError) {
                    statusText.setText(R.string.error_message); // Sets the text for the TextView object.
                }

                // Sets a "No results found." message for the status TextView object.
                else {
                    statusText.setText(R.string.no_results); // Sets the text for the TextView object.
                }

                statusText.setVisibility(View.VISIBLE); // Displays the TextView object.
            }
        }
    }
}