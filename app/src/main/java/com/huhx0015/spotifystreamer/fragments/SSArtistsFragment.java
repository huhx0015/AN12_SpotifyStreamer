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
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.huhx0015.spotifystreamer.ui.adapters.SSResultsAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/** -----------------------------------------------------------------------------------------------
 *  [SSArtistsFragment] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSArtistsFragment is a fragment class that is responsible for displaying the input
 *  field in which a user can search for an artist via Spotify. The results are then loaded into the
 *  RecyclerView object that is handled by this fragment.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSArtistsFragment extends Fragment {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private Activity currentActivity; // Used to determine the activity class this fragment is currently attached to.

    // FRAGMENT VARIABLES
    private String artistName = ""; // Stores the name of the artist.

    // LAYOUT VARIABLES
    private Boolean isInputEmpty = true; // Used to determine if the EditText input field is empty or not.

    // LIST VARIABLES
    private List<SSSpotifyModel> artistListResult = new ArrayList<>(); // Stores the artist list result that is to be used for the adapter.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSArtistsFragment.class.getSimpleName();

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_artist_search_search_input) EditText searchInput;
    @Bind(R.id.ss_artist_search_progress_indicator) ProgressBar progressIndicator;
    @Bind(R.id.ss_artist_search_recycler_view) RecyclerView resultsList;
    @Bind(R.id.ss_artist_search_status_text) TextView statusText;

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // SSArtistsFragment(): Default constructor for the SSArtistsFragment fragment class.
    private final static SSArtistsFragment artists_fragment = new SSArtistsFragment();

    // SSArtistsFragment(): Deconstructor method for the SSArtistsFragment fragment class.
    public SSArtistsFragment() {}

    // getInstance(): Returns the artists_fragment instance.
    public static SSArtistsFragment getInstance() { return artists_fragment; }

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

        View ss_fragment_view = (ViewGroup) inflater.inflate(R.layout.ss_artist_search_fragment, container, false);
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

        // If the artist name value is not empty, this indicates that the SSTracksFragment was
        // previously active. The previous artist search is conducted.
        if (!artistName.isEmpty()) {

            // SPOTIFY ASYNCTASK INITIALIZATION:
            SSSpotifyArtistSearchTask task = new SSSpotifyArtistSearchTask();
            task.execute(artistName); // Executes the AsyncTask.
        }
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
                    SSSpotifyArtistSearchTask task = new SSSpotifyArtistSearchTask();
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

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // displayTopTracks(): Signals attached activity to display the SSTracksFragment view.
    private void displayTopTracks(String name, String id) {
        try { ((OnSpotifySelectedListener) currentActivity).displayTracksFragment(true, name); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    /** RECYCLERVIEW METHODS ___________________________________________________________________ **/

    // setListAdapter(): Sets the recycler list adapter based on the artistList.
    private void setListAdapter(List<SSSpotifyModel> artistList){
        SSResultsAdapter adapter = new SSResultsAdapter(artistList, true, currentActivity);
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
     * [SSSpotifyArtistSearchTask] CLASS
     * DESCRIPTION: This is an AsyncTask-based class that accesses and queries the Spotify Service
     * API in the background.
     * --------------------------------------------------------------------------------------------
     */

    public class SSSpotifyArtistSearchTask extends AsyncTask<String, Void, Void> {

        /** SUBCLASS VARIABLES _________________________________________________________________ **/

        // TRACK VARIABLES
        Boolean isError = false; // Used to determine if an error has occurred or not.
        Boolean artistsRetrieved = false; // Used to determine if artist retrieval was successful or not.

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

            Log.d(LOG_TAG, "SSSpotifyArtistSearchTask(): Beginning Spotify artist query...");

            // Initializes the Spotify API and background service.
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            artistListResult = new ArrayList<>(); // Creates a new ArrayList of artists.

            // Retrieves the list of artists.
            artistListResult = SSSpotifyAccessors.retrieveArtists(params[0], artistListResult, service);

            // If the artistListResult object is null, it indicates an error has occurred and
            // that the retrieval of the list of artists was a failure.
            if (artistListResult == null) {
                isError = true;
                artistsRetrieved = false;
                Log.e(LOG_TAG, "ERROR: SSSpotifyArtistSearchTask(): The artist list result was invalid.");
            }

            // Indicates that the retrieval of the list of artists was a failure.
            else if (artistListResult.size() == 0){
                artistsRetrieved = false; // Indicates a failure at list of artists retrieval.
            }

            // Otherwise, the retrieval of the list of artists was successful.
            else {
                artistsRetrieved = true; // Indicates a success at list of artists retrieval.
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
            if (artistsRetrieved && !isInputEmpty) {

                // The RecyclerView object is made visible.
                resultsList.setVisibility(View.VISIBLE);

                setListAdapter(artistListResult); // Sets the adapter for the RecyclerView object.
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

                // Sets a status message for the status TextView object.
                else {
                    statusText.setText(R.string.no_results_artists); // Sets the text for the TextView object.
                }

                statusText.setVisibility(View.VISIBLE); // Displays the TextView object.
            }
        }
    }
}