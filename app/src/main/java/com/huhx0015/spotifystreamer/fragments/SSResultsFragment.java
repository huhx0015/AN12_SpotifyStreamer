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
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.model.SSSpotifyModel;
import com.huhx0015.spotifystreamer.ui.SSResultsAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
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

    // LAYOUT VARIABLES
    private String currentSearchInput = "";

    // LIST VARIABLES
    private List<SSSpotifyModel> songListResult;

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSResultsFragment.class.getSimpleName();

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_results_search_input) EditText searchInput;
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

        setUpTextListener();
        setUpList();
    }

    // setUpTextListener(): Sets up the EditText listener for the fragment.
    private void setUpTextListener() {

        searchInput.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Retrieves the current string from the EditText object.
                currentSearchInput = s.toString();

                // SPOTIFY ASYNCTASK INITIALIZATION:
                SSSearchSpotifyTask task = new SSSearchSpotifyTask();
                task.execute(); // Executes the AsyncTask.
            }
        });
    }

    /** RECYCLERVIEW METHODS ___________________________________________________________________ **/

    private void setUpList() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(currentActivity);
        resultsList.setLayoutManager(layoutManager);

        // TODO: Experimental
        initializeData();
        setListAdapter();
    }

    // This method creates an ArrayList that has three SSSpotifyModel objects
    // Checkout the project associated with this tutorial on Github if
    // you want to use the same images.
    private void initializeData(){

        songListResult = new ArrayList<>();
        songListResult.add(new SSSpotifyModel("Coldplay", "X & Y", "Fix You", R.drawable.sample_cover_1));
        songListResult.add(new SSSpotifyModel("Coldplay", "Ghost Stories", "Oceans", R.drawable.sample_cover_2));
        songListResult.add(new SSSpotifyModel("Coldplay", "Parachutes", "Yellow", R.drawable.sample_cover_3));
    }

    private void setListAdapter(){
        SSResultsAdapter adapter = new SSResultsAdapter(songListResult, currentActivity);
        resultsList.setAdapter(adapter);
    }

    /** ASYNCTASK METHODS ______________________________________________________________________ **/

    /**
     * --------------------------------------------------------------------------------------------
     * [SSSearchSpotifyTask] CLASS
     * DESCRIPTION: This is an AsyncTask-based class that accesses and queries the Spotify API in
     * the background.
     * --------------------------------------------------------------------------------------------
     */

    public class SSSearchSpotifyTask extends AsyncTask<Void, Void, Void> {

        // doInBackground(): This method constantly runs in the background while AsyncTask is
        // running.
        @Override
        protected Void doInBackground(Void... strings) {

            // Initializes the Spotify API and background service.
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // Accesses the background service to search for a specific artist.
            ArtistsPager results = service.searchArtists(currentSearchInput);

            List<Artist> artists = results.artists.items;

            // Retrieves the list of Artists found and sets it in the list.
            for (int i = 0; i < artists.size(); i++) {
                Artist artist = artists.get(i);
                Log.i(LOG_TAG, i + " " + artist.name);
                Log.i(LOG_TAG, i + " " + artist.href);
                Log.i(LOG_TAG, i + " " + artist.type);
            }

            return null;
        }
    }
}