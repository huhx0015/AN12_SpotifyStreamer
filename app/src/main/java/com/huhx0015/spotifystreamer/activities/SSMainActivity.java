package com.huhx0015.spotifystreamer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.fragments.SSArtistsFragment;
import com.huhx0015.spotifystreamer.fragments.SSPlayerFragment;
import com.huhx0015.spotifystreamer.fragments.SSTracksFragment;
import com.huhx0015.spotifystreamer.intent.SSShareIntent;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.huhx0015.spotifystreamer.services.SSMusicService;
import com.huhx0015.spotifystreamer.ui.actionbar.SSActionBar;
import com.huhx0015.spotifystreamer.ui.layouts.SSUnbind;
import com.huhx0015.spotifystreamer.ui.views.SSFragmentView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

/** -----------------------------------------------------------------------------------------------
 *  [SSMainActivity] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSMainActivity class is the primary Activity class that is loaded when the
 *  application is launched and is responsible for managing the fragment views for the application.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSMainActivity extends AppCompatActivity implements OnMusicServiceListener,
        OnSpotifySelectedListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private static WeakReference<AppCompatActivity> weakRefActivity = null; // Used to maintain a weak reference to the activity.

    // DATA VARIABLES
    private static final String CURRENT_FRAGMENT = "currentFragment"; // Used for restoring the proper fragment for rotation change events.
    private static final String CURRENT_TRACK = "currentTrack"; // Used for restoring the proper track name value for rotation change events.
    private static final String ARTIST_INPUT = "artistInput"; // Used for restoring the artist input value for rotation change events.
    private static final String ARTIST_NAME = "artistName"; // Used for restoring the artist name value for rotation change events.
    private static final String ARTIST_LIST = "artistListResult"; // Parcelable key value for the artist list.
    private static final String ROTATION_CHANGE = "rotationChange"; // Used for restoring the rotationChange value for rotation change events.
    private static final String TRACK_LIST = "trackListResult"; // Parcelable key value for the track list.

    // FRAGMENT VARIABLES
    private Boolean isPlaying = false; // Used to determine if the SSPlayerFragment is in focus.
    private Boolean isRotationEvent = false; // Used to determine if a screen orientation change event has occurred.
    private String currentFragment = ""; // Used to determine which fragment is currently active.
    private String currentArtist = ""; // Used to determine the current artist name.
    private String currentInput = ""; // Used to determine the current artist input.
    private String currentTrack = ""; // Used to determine the current track name.

    // LAYOUT VARIABLES
    private Boolean isTablet = false; // Used to determine if the current device is a mobile or tablet device.

    // LIST VARIABLES
    private ArrayList<SSSpotifyModel> artistListResult = new ArrayList<>(); // Stores the artist list result.
    private ArrayList<SSSpotifyModel> trackListResult = new ArrayList<>(); // Stores the track list result.
    private int listPosition; // Used to determine the current position in the top tracks list.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSMainActivity.class.getSimpleName();

    // SERVICE VARIABLES
    private Boolean serviceBound = false; // Used to determine if the SSMusicService is currently bound to the activity.
    private Intent audioIntent; // An Intent object that references the Intent for the SSMusicService.
    private SSMusicService musicService; // A service that handles the control of audio playback in the background.

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_main_activity_fragment_container) FrameLayout fragmentContainer;
    @Bind(R.id.ss_main_activity_secondary_fragment_container) FrameLayout fragmentSecondaryContainer;
    @Bind(R.id.ss_main_activity_secondary_progress_indicator) ProgressBar secondaryProgressBar;

    /** ACTIVITY LIFECYCLE METHODS _____________________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only
    // runs when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creates a weak reference of this activity.
        weakRefActivity = new WeakReference<AppCompatActivity>(this);

        // Checks to see if there is a saved instance that was saved prior, from events such as
        // rotation changes.
        if (savedInstanceState != null) {

            // Restores the saved instance values.
            isRotationEvent = savedInstanceState.getBoolean(ROTATION_CHANGE);
            currentArtist = savedInstanceState.getString(ARTIST_NAME);
            currentInput = savedInstanceState.getString(ARTIST_INPUT);
            currentTrack = savedInstanceState.getString(CURRENT_TRACK);
            currentFragment = savedInstanceState.getString(CURRENT_FRAGMENT);
            artistListResult = savedInstanceState.getParcelableArrayList(ARTIST_LIST);
            trackListResult = savedInstanceState.getParcelableArrayList(TRACK_LIST);
        }

        // LAYOUT SETUP:
        setupLayout(); // Sets up the layout for the activity.
    }

    // onStart(): Called when the activity is made visible.
    @Override
    public void onStart() {
        super.onStart();
        setUpAudioService(); // Sets up the background audio service.
    }

    // onDestroy(): This function runs when the activity has terminated and is being destroyed.
    // Calls recycleMemory() to free up memory allocation.
    @Override
    public void onDestroy() {
        recycleView(); // Recycles all services and View objects to free up memory resources.
        super.onDestroy();
    }

    /** ACTIVITY EXTENSION METHODS _____________________________________________________________ **/

    // onCreateOptionsMenu(): Inflates the menu when the menu key is pressed. This adds items to
    // the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ss_main_activity_menu, menu);
        return true;
    }

    // onOptionsItemSelected(): Defines the action to take when the menu options are selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            // BACK BUTTON:
            case android.R.id.home:

                // SSPlayerFragment: Removes the SSPlayerFragment and displays the main
                // SSTracksFragment view.
                if (currentFragment.equals("PLAYER")) {
                    displayPlayerFragment(false, trackListResult, listPosition);
                }

                // SSTracksFragment: Removes the SSTracksFragment and displays the main
                // SSArtistsFragment view.
                else if (currentFragment.equals("TRACKS")) {
                    displayTracksFragment(false, currentInput);
                }

                return true;

            // OPTIONS:
            case R.id.action_settings:
                return true;

            // DEFAULT:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // onSaveInstanceState(): Called to retrieve per-instance state from an activity before being
    // killed so that the state can be restored in onCreate() or onRestoreInstanceState().
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Saves the current fragment state and current artist and track list values into the
        // instance. Used to determine which fragment should be shown when the activity is
        // re-created after the rotation change event.
        savedInstanceState.putBoolean(ROTATION_CHANGE, true);
        savedInstanceState.putString(CURRENT_FRAGMENT, currentFragment);
        savedInstanceState.putString(CURRENT_TRACK, currentTrack);
        savedInstanceState.putString(ARTIST_INPUT, currentInput);
        savedInstanceState.putString(ARTIST_NAME, currentArtist);
        savedInstanceState.putParcelableArrayList(ARTIST_LIST, artistListResult);
        savedInstanceState.putParcelableArrayList(TRACK_LIST, trackListResult);

        // Always calls the superclass, so it can save the view hierarchy state.
        super.onSaveInstanceState(savedInstanceState);
    }

    // onShareAction(): Defines the action to take if the Share menu option is selected.
    public void onShareAction(MenuItem item) {
        SSShareIntent.shareIntent(currentTrack, currentArtist, this);
    }

    /** PHYSICAL BUTTON METHODS ________________________________________________________________ **/

    // BACK KEY:
    // onBackPressed(): Defines the action to take when the physical back button key is pressed.
    @Override
    public void onBackPressed() {

        // If the SSPlayerFragment is currently being displayed, the fragment is removed and
        // switched with the SSTracksFragmentView.
        if ( (currentFragment.equals("PLAYER")) && !(isTablet) ) {
            displayPlayerFragment(false, trackListResult, listPosition);
        }

        // If the SSTracksFragment is currently being displayed, the fragment is removed and
        // switched with the SSArtistsFragment view.
        else if ( (currentFragment.equals("TRACKS")) && !(isTablet) ) {
            displayTracksFragment(false, currentInput);
        }

        // The activity is finished if the SSArtistsFragment is in focus.
        else {
            finish(); // Finishes the activity.
        }
    }

    /** DATA RETENTION METHODS _________________________________________________________________ **/

    // getArtistResults(): Retrieves the existing artist list result from this activity.
    public ArrayList<SSSpotifyModel> getArtistResults() {
        return artistListResult;
    }

    // getTrackResults(): Retrieves the existing track list result from this activity.
    public ArrayList<SSSpotifyModel> getTrackResults() {
        return trackListResult;
    }

    // setArtistResults(): Sets the artist list result for this activity.
    public void setArtistResults(ArrayList<SSSpotifyModel> list) {
        this.artistListResult = list;
    }

    // setTrackResults(): Sets the track list result for this activity.
    public void setTrackResults(ArrayList<SSSpotifyModel> list) {
        this.trackListResult = list;
    }

    // setCurrentTrack(): Sets the current track playing in the background, as well as setting the
    // the isPlaying value for determining if a song is currently playing.
    public void setCurrentTrack(String track, Boolean isPlay) {
        this.currentTrack = track;
        this.isPlaying = isPlay;
    }

    /** LAYOUT METHODS _________________________________________________________________________ **/

    // setupLayout(): Sets up the layout for the activity.
    private void setupLayout() {

        // Updates the isTablet value to determine if the device is a mobile or tablet device.
        isTablet = getResources().getBoolean(R.bool.isTablet);
        Log.d(LOG_TAG, "setupLayout(): isTablet: " + isTablet);

        setContentView(R.layout.ss_main_activity); // Sets the XML layout file for the activity.
        ButterKnife.bind(this); // ButterKnife view injection initialization.

        setupFragment(); // Initializes the fragment view for the layout.

        // If a rotation event occurred, the isRotationEvent value is reset.
        if (isRotationEvent) {
            isRotationEvent = false;
        }
    }

    // setupFragment(): Initializes the fragment view for the layout.
    private void setupFragment() {

        // Checks to see if there are any retained fragments when the activity is re-created from a
        // screen rotation event.
        FragmentManager fragManager = getSupportFragmentManager();
        Fragment artistsFragment = fragManager.findFragmentByTag("ARTISTS");
        Fragment tracksFragment = fragManager.findFragmentByTag("TRACKS");
        Fragment playerFragment = fragManager.findFragmentByTag("PLAYER");

        // TABLET: Reloads the SSTracksFragment (if it was in focus previously) and the
        // SSArtistsFragment in their respective containers.
        if (isTablet) {

            Log.d(LOG_TAG, "setupFragment(): Reloading fragments for tablet view...");

            SSFragmentView.reloadFragment(artistsFragment, "ARTISTS", "ARTISTS", fragmentContainer, R.id.ss_main_activity_fragment_container, currentArtist, currentTrack, this, weakRefActivity);

            // If a screen orientation event has occurred and the fragment that was in focus was
            // SSTracksFragment, a new SSTracksFragment is created and is made visible in the
            // view layout.
            if ( (isRotationEvent) && (currentFragment.equals("TRACKS")) ) {
                SSTracksFragment newTracksFragment = new SSTracksFragment();
                newTracksFragment.initializeFragment(currentTrack, true);
                SSFragmentView.addFragment(newTracksFragment, fragmentSecondaryContainer, R.id.ss_main_activity_secondary_fragment_container, "TRACKS", false, weakRefActivity);
            }
        }

        // MOBILE: Reloads the fragment that was in focus prior to the screen orientation change.
        else {

            Log.d(LOG_TAG, "setupFragment(): Reloading fragments for mobile view...");

            // SSPlayerFragment: Attempts to reload the SSPlayerFragment, if the SSPlayerFragment
            // was in prior focus.
            Boolean isReloaded = SSFragmentView.reloadFragment(playerFragment, currentFragment, "PLAYER", fragmentContainer, R.id.ss_main_activity_fragment_container, currentArtist, currentTrack, this, weakRefActivity);

            // SSTracksFragment: Attempts to reload the SSTracksFragment, if the SSTracksFragment
            // was in prior focus.
            if (!isReloaded) {
                isReloaded = SSFragmentView.reloadFragment(tracksFragment, currentFragment, "TRACKS", fragmentContainer, R.id.ss_main_activity_fragment_container, currentArtist, currentTrack, this, weakRefActivity);
            }

            // SSArtistsFragment: Attempts to reload the SSArtistFragment, if the SSTracksFragment
            // was in prior focus.
            if (!isReloaded) {
                SSFragmentView.reloadFragment(artistsFragment, "ARTISTS", "ARTISTS", fragmentContainer, R.id.ss_main_activity_fragment_container, currentArtist, currentTrack, this, weakRefActivity);
            }
        }
    }

    /** FRAGMENT METHODS _______________________________________________________________________ **/

    // changeFragment(): Changes the fragment views.
    private void changeFragment(Fragment frag, String fragToAdd, String fragToRemove, String subtitle,
                                Boolean isAnimated) {

        Log.d(LOG_TAG, "changeFragment(): Fragment changed.");

        // Removes the SSArtistsFragment from the stack.
        SSFragmentView.removeFragment(fragmentContainer, fragToRemove, false, weakRefActivity);

        // Adds the fragment to the primary fragment container.
        SSFragmentView.addFragment(frag, fragmentContainer, R.id.ss_main_activity_fragment_container, fragToAdd, isAnimated, weakRefActivity);

        // Sets the name of the action bar.
        SSActionBar.setupActionBar(fragToAdd, currentArtist, subtitle, this);
        currentFragment = fragToAdd; // Sets the current active fragment.
    }

    // displayFragmentDialog(): Displays the DialogFragment view for the specified fragment.
    private void displayFragmentDialog(DialogFragment frag, String fragType) {
        FragmentManager fragMan = getSupportFragmentManager(); // Sets up the FragmentManager.
        frag.show(fragMan, fragType); // Displays the DialogFragment.
    }

    /** SERVICE METHODS ________________________________________________________________________ **/

    // musicConnection(): A ServiceConnection object for managing the service connection states for
    // the SSMusicService service.
    private ServiceConnection musicConnection = new ServiceConnection() {

        // onServiceConnected(): Runs when the service is connected to the activity.
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            // Sets the binder for the service.
            SSMusicService.SSMusicBinder binder = (SSMusicService.SSMusicBinder) service;
            musicService = binder.getService();
            serviceBound = true; // Indicates that the service is bounded.
        }

        // onServiceDisconnnected: Runs when the service is disconnected from the activity.
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false; // Indicates that the service is no longer bound.
        }
    };

    // setUpAudioService(): Sets up the SSMusicService service for playing audio from the
    // SSMusicEngine class in the background.
    private void setUpAudioService() {

        if (audioIntent == null) {

            // Sets up the service intent and begins the service.
            audioIntent = new Intent(this, SSMusicService.class); // Sets a Intent to the service.
            bindService(audioIntent, musicConnection, Context.BIND_AUTO_CREATE); // Binds the service.
            startService(audioIntent); // Starts the service.
        }
    }

    // removeAudioService(): Stops the SSMusicService running in the background.
    private void removeAudioService() {

        if (audioIntent != null) {
            stopService(audioIntent); // Stops the service.
            musicService = null;
        }
    }

    // attachPlayerFragment(): Attaches a player fragment with
    private void attachPlayerFragment(Fragment fragment) {
        musicService.attachPlayerFragment(fragment);
    }

    /** RECYCLE METHODS ________________________________________________________________________ **/

    // recycleView(): Recycles background services and View objects to clear up resources prior to
    // Activity destruction.
    private void recycleView() {

        try {

            removeAudioService(); // Stops the SSMusicService running in the background.

            // Unbinds all Drawable objects attached to the current layout.
            SSUnbind.unbindDrawables(findViewById(R.id.ss_main_activity_layout));
        }

        // NullPointerException error handler.
        catch (NullPointerException e) {
            e.printStackTrace(); // Prints error message.
            Log.e(LOG_TAG, "ERROR: recycleMemory(): Null pointer exception occurred: " + e);
        }
    }

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // displayTracksFragment(): Displays or removes the SSTracksFragment from the view layout.
    @Override
    public void displayTracksFragment(Boolean isShow, String name) {

        // Displays the SSTracksFragment in the view layout.
        if (isShow) {

            // Adds a new SSTracksFragment onto the fragment stack and is made visible in the view
            // layout.
            SSTracksFragment tracksFragment = new SSTracksFragment();

            // If the current artist name matches the name from the previous selected artist,
            // SSTracksFragment will load the previous track result list.
            if (currentArtist.equals(name)) {
                tracksFragment.initializeFragment(name, true);
            }

            // The Spotify API will be queried for the newly selected artist.
            else {
                tracksFragment.initializeFragment(name, false);
            }

            // TABLET: Loads the SSTracksFragment into the secondary fragment container.
            if (isTablet) {
                SSFragmentView.addFragment(tracksFragment, fragmentSecondaryContainer, R.id.ss_main_activity_secondary_fragment_container, "TRACKS", true, weakRefActivity);
                currentFragment = "TRACKS"; // Sets the current active fragment.
            }

            // MOBILE: Removes the previous fragment and adds the new fragment.
            else {
                changeFragment(tracksFragment, "TRACKS", "ARTISTS", name, true);
            }

            currentArtist = name; // Sets the name of the current artist.
            Log.d(LOG_TAG, "displayTracksFragment(): SSTracksFragment now being displayed.");
        }

        // Removes the SSTracksFragment in the view layout and replaces it with a SSArtistsFragment.
        else {

            // Adds a new SSArtistsFragment onto the fragment stack and is made visible in the view
            // layout.
            SSArtistsFragment artistFragment = new SSArtistsFragment();
            artistFragment.initializeFragment(name, true);

            // TABLET: Removes the SSTracksFragment from the secondary fragment container.
            if (isTablet) {
                // TODO: Do nothing for now, need to think about the flow for the tablet version.
            }

            // MOBILE: Removes the previous fragment and adds the new fragment.
            else {
                changeFragment(artistFragment, "ARTISTS", "TRACKS", name, false);
            }

            Log.d(LOG_TAG, "displayTracksFragment(): SSArtistsFragment now being displayed.");
        }
    }

    // displayPlayerFragment(): Displays or removes the SSPlayerFragment from the view layout.
    @Override
    public void displayPlayerFragment(Boolean isShow, ArrayList<SSSpotifyModel> list, int position) {

        // Displays the SSPlayerFragment in the view layout.
        if (isShow) {

            this.listPosition = position; // Sets the selected position in the top tracks list.

            // Adds a new SSPlayerFragment onto the fragment stack and is made visible in the view
            // layout.
            SSPlayerFragment playerFragment = new SSPlayerFragment();
            playerFragment.initializeFragment(list, position);

            // TABLET: Displays the SSPlayerFragment as a DialogFragment.
            if (isTablet) {

                // TODO: Add a DialogFragment listener for monitoring dialog dismiss events.
                displayFragmentDialog(playerFragment, "PLAYER");
            }

            // MOBILE: Removes the previous fragment and adds the new fragment.
            else {
                changeFragment(playerFragment, "PLAYER", "TRACKS", list.get(position).getSong(), true);
            }

            attachPlayerFragment(playerFragment); // Attaches the SSPlayerFragment to the SSMusicEngine.

            Log.d(LOG_TAG, "displayPlayerFragment(): SSPlayerFragment now being displayed.");
        }

        // Removes the SSPlayerFragment in the view layout and replaces it with a SSTracksFragment.
        else {

            // Adds a new SSTracksFragment onto the fragment stack and is made visible in the view
            // layout.
            SSTracksFragment tracksFragment = new SSTracksFragment();
            tracksFragment.initializeFragment(list.get(position).getArtist(), true);

            // MOBILE: Removes the previous fragment and adds the new fragment.
            if (!isTablet) {
                changeFragment(tracksFragment, "TRACKS", "PLAYER", list.get(position).getArtist(), false);
                pauseTrack(true); // Stops music playback.
            }

            Log.d(LOG_TAG, "displayPlayerFragment(): SSTracksFragment now being displayed.");
        }
    }

    // pauseTrack(): Invoked by SSPlayerFragment to signal the SSMusicService to pause the song
    // stream.
    @Override
    public void pauseTrack(Boolean isStop) {
        musicService.pauseTrack(isStop); // Signals the SSMusicService to pause the song stream.
    }

    // playTrack(): Invoked by SSPlayerFragment to signal the SSMusicService to play the selected
    // stream.
    @Override
    public void playTrack(String url, Boolean loop) {
        musicService.playTrack(url, loop); // Signals the SSMusicService to play the song stream.
    }

    // setPosition(): Invoked by SSPlayerFragment to signal the SSMusicService to skip to the
    // selected position in the song.
    @Override
    public void setPosition(int position) {
        musicService.setPosition(position); // Signals the SSMusicService to set the song position.
    }

    // updateArtistInput(): Invoked by SSArtistsFragment to keep an update of the user's artist
    // input.
    @Override
    public void updateArtistInput(String name) {
        currentInput = name; // Sets the current artist input name.
    }
}