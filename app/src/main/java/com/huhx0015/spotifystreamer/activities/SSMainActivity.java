package com.huhx0015.spotifystreamer.activities;

import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.fragments.SSArtistsFragment;
import com.huhx0015.spotifystreamer.fragments.SSPlayerFragment;
import com.huhx0015.spotifystreamer.fragments.SSSettingsFragment;
import com.huhx0015.spotifystreamer.fragments.SSTracksFragment;
import com.huhx0015.spotifystreamer.intent.SSShareIntent;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.huhx0015.spotifystreamer.interfaces.OnTrackInfoUpdateListener;
import com.huhx0015.spotifystreamer.ui.actionbar.SSActionBar;
import com.huhx0015.spotifystreamer.ui.layouts.SSUnbind;
import com.huhx0015.spotifystreamer.ui.toast.SSToast;
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

public class SSMainActivity extends AppCompatActivity implements OnSpotifySelectedListener,
        OnTrackInfoUpdateListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private static WeakReference<AppCompatActivity> weakRefActivity = null; // Used to maintain a weak reference to the activity.

    // DATA VARIABLES
    private static final String ARTIST_INPUT = "artistInput"; // Used for restoring the artist input value for rotation change events.
    private static final String ARTIST_LIST = "artistListResult"; // Parcelable key value for the artist list.
    private static final String ARTIST_NAME = "artistName"; // Used for restoring the artist name value for rotation change events.
    private static final String CURRENT_FRAGMENT = "currentFragment"; // Used for restoring the proper fragment for rotation change events.
    private static final String CURRENT_TRACK = "currentTrack"; // Used for restoring the proper track name value for rotation change events.
    private static final String CURRENT_TRACK_POS = "currentTrackPosition"; // Used for restoring the proper track position value for rotation change events.
    private static final String ROTATION_CHANGE = "rotationChange"; // Used for restoring the rotationChange value for rotation change events.
    private static final String TRACK_LIST = "trackListResult"; // Parcelable key value for the track list.

    // FRAGMENT VARIABLES
    private Boolean isRotationEvent = false; // Used to determine if a screen orientation change event has occurred.
    private Boolean isSettings = false; // Used to determine if the SSSettingsFragment is in focus.
    private String currentFragment = ""; // Used to determine which fragment is currently active.
    private String currentArtist = ""; // Used to determine the current artist name.
    private String currentInput = ""; // Used to determine the current artist input.
    private String currentTrack = ""; // Used to determine the current track name.

    // LAYOUT VARIABLES
    private Boolean isTablet = false; // Used to determine if the current device is a mobile or tablet device.

    // LIST VARIABLES
    private ArrayList<SSSpotifyModel> artistListResult = new ArrayList<>(); // Stores the artist list result.
    private ArrayList<SSSpotifyModel> trackListResult = new ArrayList<>(); // Stores the track list result.
    private int listPosition = -1; // Used to determine the current position in the top tracks list.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSMainActivity.class.getSimpleName();

    // SHARE VARIABLES
    private String spotifyUrl = ""; // Used to reference the Spotify track URL of the current track.

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_main_activity_fragment_container) FrameLayout fragmentContainer;
    @Bind(R.id.ss_main_activity_secondary_fragment_container) FrameLayout fragmentSecondaryContainer;
    @Bind(R.id.ss_main_activity_settings_fragment_container) FrameLayout settingsFragmentContainer;

    /** ACTIVITY LIFECYCLE METHODS _____________________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only
    // runs when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "ACTIVITY LIFECYCLE (onCreate): onCreate invoked.");

        // Creates a weak reference of this activity.
        weakRefActivity = new WeakReference<AppCompatActivity>(this);

        // Checks to see if there is a saved instance that was saved prior, from events such as
        // rotation changes.
        if (savedInstanceState != null) {

            // Restores the saved instance values.
            artistListResult = savedInstanceState.getParcelableArrayList(ARTIST_LIST);
            currentArtist = savedInstanceState.getString(ARTIST_NAME);
            currentInput = savedInstanceState.getString(ARTIST_INPUT);
            currentFragment = savedInstanceState.getString(CURRENT_FRAGMENT);
            currentTrack = savedInstanceState.getString(CURRENT_TRACK);
            listPosition = savedInstanceState.getInt(CURRENT_TRACK_POS);
            isRotationEvent = savedInstanceState.getBoolean(ROTATION_CHANGE);
            trackListResult = savedInstanceState.getParcelableArrayList(TRACK_LIST);
        }

        // Signals the SSApplication class to start up the SSMusicService.
        else {
            setUpAudioService();
        }

        // LAYOUT SETUP:
        setupLayout(); // Sets up the layout for the activity.
    }

    // onPause(): This function is called whenever the fragment is suspended.
    @Override
    public void onPause(){
        super.onPause();

        Log.d(LOG_TAG, "ACTIVITY LIFECYCLE (onPause): onPause invoked.");
    }

    // onDestroy(): This function runs when the activity has terminated and is being destroyed.
    // Calls recycleMemory() to free up memory allocation.
    @Override
    public void onDestroy() {

        Log.d(LOG_TAG, "ACTIVITY LIFECYCLE (onDestroy): onDestroy invoked.");

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

                // SSSettingsFragment: Hides the SSSettingsFragment view.
                if (isSettings) {
                    displaySettingsFragment(false); // Hides the SSSettingsFragment.
                }

                // SSPlayerFragment: Removes the SSPlayerFragment and displays the main
                // SSTracksFragment view.
                else if (currentFragment.equals("PLAYER")) {
                    displayPlayerFragment(false, "TRACKS", trackListResult, listPosition, false);
                }

                // SSTracksFragment: Removes the SSTracksFragment and displays the main
                // SSArtistsFragment view.
                else if (currentFragment.equals("TRACKS")) {
                    displayTracksFragment(false, currentInput);
                }

                return true;

            // PLAY BUTTON:
            case R.id.ss_action_play_button:

                // Displays the last selected song that was played.
                if ( (trackListResult != null) && (listPosition != -1) && !(currentFragment.equals("PLAYER")) ) {
                    displayPlayerFragment(true, currentFragment, trackListResult, listPosition, false);
                }

                // Displays a Toast message, indicating that the player fragment view is already
                // being shown.
                else if (currentFragment.equals("PLAYER")) {
                    SSToast.toastyPopUp("NOW PLAYING is already visible.", this);
                }

                // Displays a Toast message, indicating that no track has been selected.
                else {
                    SSToast.toastyPopUp("No previous track has been selected.", this);
                }

                return true;

            // SHARE BUTTON:
            case R.id.ss_action_share_button:
                SSShareIntent.shareIntent(currentTrack, currentArtist, spotifyUrl, this);
                return true;

            // SETTINGS:
            case R.id.action_settings:

                // Displays the SSSettingsFragment view.
                if (!isSettings) {
                    displaySettingsFragment(true); // Displays the SSSettingsFragment.
                }

                // Hides the SSSettingsFragment view.
                else {
                    displaySettingsFragment(false); // Hides the SSSettingsFragment.
                }

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

        // Saves current state values into the instance. These values are restored upon re-creation
        // of the activity.
        savedInstanceState.putBoolean(ROTATION_CHANGE, true);
        savedInstanceState.putInt(CURRENT_TRACK_POS, listPosition);
        savedInstanceState.putString(ARTIST_INPUT, currentInput);
        savedInstanceState.putString(ARTIST_NAME, currentArtist);
        savedInstanceState.putString(CURRENT_FRAGMENT, currentFragment);
        savedInstanceState.putString(CURRENT_TRACK, currentTrack);
        savedInstanceState.putParcelableArrayList(ARTIST_LIST, artistListResult);
        savedInstanceState.putParcelableArrayList(TRACK_LIST, trackListResult);

        // Always calls the superclass, so it can save the view hierarchy state.
        super.onSaveInstanceState(savedInstanceState);
    }

    /** PHYSICAL BUTTON METHODS ________________________________________________________________ **/

    // BACK KEY:
    // onBackPressed(): Defines the action to take when the physical back button key is pressed.
    @Override
    public void onBackPressed() {

        // SSSettingsFragment: Hides the SSSettingsFragment view.
        if (isSettings) {
            displaySettingsFragment(false); // Removes the SSSettingsFragment from view.
        }

        // If the SSPlayerFragment is currently being displayed, the fragment is removed and
        // switched with the SSTracksFragmentView.
        else if ( (currentFragment.equals("PLAYER")) && !(isTablet) ) {
            displayPlayerFragment(false, "TRACKS", trackListResult, listPosition, false);
        }

        // If the SSTracksFragment is currently being displayed, the fragment is removed and
        // switched with the SSArtistsFragment view.
        else if ( (currentFragment.equals("TRACKS")) && !(isTablet) ) {
            displayTracksFragment(false, currentInput);
        }

        // The activity is finished if the SSArtistsFragment is in focus.
        else {
            pauseTrack(true); // Stops any track playing in the background.
            //removeAudioService(); // Stops the SSMusicService running in the background.
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

            SSFragmentView.reloadFragment(artistsFragment, "ARTISTS", "ARTISTS", fragmentContainer,
                    R.id.ss_main_activity_fragment_container, currentArtist, currentTrack, weakRefActivity);

            // SSTracksFragment: If a screen orientation event has occurred and the fragment that
            // was in focus was SSTracksFragment, a new SSTracksFragment is created and is made
            // visible in the view layout.
            if ( (isRotationEvent) && (currentFragment.equals("TRACKS")) ) {
                SSTracksFragment newTracksFragment = new SSTracksFragment();
                newTracksFragment.initializeFragment(currentTrack, true);
                SSFragmentView.addFragment(newTracksFragment, fragmentSecondaryContainer,
                        R.id.ss_main_activity_secondary_fragment_container, "TRACKS", false, weakRefActivity);

                // Sets up the action bar.
                SSActionBar.setupActionBar("TRACKS", currentArtist, currentArtist, false, this);
            }

            // SSPlayerFragment: If a screen orientation event has occurred and the SSPlayerFragment
            // DialogFragment was displayed prior, the SSPlayerFragment DialogFragment is re-shown.
            if ( (isRotationEvent) && (playerFragment != null)) {

                try {
                    displayPlayerFragment(true, "TRACKS", trackListResult, listPosition, false);
                }

                // Null pointer exception handler.
                catch (NullPointerException e) {
                    Log.e(LOG_TAG, "setupFragment(): A null pointer exception has occurred while trying to restore the SSPlayerFragment DialogFragment.");
                }
            }
        }

        // MOBILE: Reloads the fragment that was in focus prior to the screen orientation change.
        else {

            Log.d(LOG_TAG, "setupFragment(): Reloading fragments for mobile view...");

            // SSPlayerFragment: Attempts to reload the SSPlayerFragment, if the SSPlayerFragment
            // was in prior focus.
            if ( (isRotationEvent) && (playerFragment != null) && (currentFragment.equals("PLAYER")) ) {

                try {
                    displayPlayerFragment(true, "TRACKS", trackListResult, listPosition, false);
                }

                // Null pointer exception handler.
                catch (NullPointerException e) {
                    Log.e(LOG_TAG, "setupFragment(): A null pointer exception has occurred while trying to restore the SSPlayerFragment DialogFragment.");
                }
            }

            else {

                // SSTracksFragment: Attempts to reload the SSTracksFragment, if the SSTracksFragment
                // was in prior focus.
                Boolean isReloaded = SSFragmentView.reloadFragment(tracksFragment, currentFragment,
                        "TRACKS", fragmentContainer, R.id.ss_main_activity_fragment_container,
                        currentArtist, currentTrack, weakRefActivity);

                // SSArtistsFragment: Attempts to reload the SSArtistFragment, if the
                // SSTracksFragment was in prior focus.
                if (!isReloaded) {
                    SSFragmentView.reloadFragment(artistsFragment, "ARTISTS", "ARTISTS",
                            fragmentContainer, R.id.ss_main_activity_fragment_container, currentArtist,
                            currentTrack, weakRefActivity);
                }
            }
        }
    }

    /** FRAGMENT METHODS _______________________________________________________________________ **/

    // changeFragment(): Changes the fragment views.
    private void changeFragment(Fragment frag, String fragToAdd, String fragToRemove, String subtitle,
                                Boolean isAnimated) {

        // SPECIAL CASE: If changeFragment() is invoked from setupFragment() after a screen
        // orientation change event, the specified fragment is added without any transition animations.
        if (isRotationEvent) {
            isAnimated = false; // Indicates that the fragment animations are not to be utilized.
        }

        // Removes the specified fragment from the stack.
        else {
            SSFragmentView.removeFragment(fragmentContainer, fragToRemove, false, weakRefActivity);
        }

        // Adds the fragment to the primary fragment container.
        SSFragmentView.addFragment(frag, fragmentContainer, R.id.ss_main_activity_fragment_container,
                fragToAdd, isAnimated, weakRefActivity);

        // Sets the name of the action bar.
        SSActionBar.setupActionBar(fragToAdd, currentArtist, subtitle, true, this);
        currentFragment = fragToAdd; // Sets the current active fragment.

        Log.d(LOG_TAG, "changeFragment(): Fragment changed.");
    }

    // displayFragmentDialog(): Displays the DialogFragment view for the specified fragment.
    private void displayFragmentDialog(DialogFragment frag, String fragType) {
        FragmentManager fragMan = getSupportFragmentManager(); // Sets up the FragmentManager.
        frag.show(fragMan, fragType); // Displays the DialogFragment.
    }

    // displaySettingsFragment(): Displays or hides the SSSettingsFragment view.
    private void displaySettingsFragment(Boolean isShow) {

        // Displays the SSSettingsFragment view.
        if (isShow) {
            isSettings = true; // Indicates that the SSSettingsFragment is active.
            settingsFragmentContainer.setVisibility(View.VISIBLE);
            SSSettingsFragment settingsFragment = new SSSettingsFragment();
            SSFragmentView.addFragment(settingsFragment, settingsFragmentContainer,
                    R.id.ss_main_activity_settings_fragment_container, "SETTINGS", true, weakRefActivity);
        }

        // Hides the SSSettingsFragment view.
        else {

            SSFragmentView.removeFragment(settingsFragmentContainer, "SETTINGS", true, weakRefActivity);

            // SSTracksFragment: Sets up the action bar attributes.
            if (currentFragment.equals("TRACKS")) {

                // TABLET: Updates the action bar without the BACK button.
                if (isTablet) {
                    SSActionBar.setupActionBar("TRACKS", currentArtist, currentArtist, false, this);
                }

                // MOBILE: Updates the action bar with the BACK button present.
                else {
                    SSActionBar.setupActionBar("TRACKS", currentArtist, currentArtist, true, this);
                }
            }

            // SSArtistsFragment | SSPlayerFragment: Sets up the action bar attributes.
            else {
                SSActionBar.setupActionBar(currentFragment, currentArtist, currentTrack, true, this);
            }

            isSettings = false; // Indicates that the SSSettingsFragment is inactive.
        }
    }

    /** RECYCLE METHODS ________________________________________________________________________ **/

    // recycleView(): Recycles the View objects to clear up resources prior to Activity destruction.
    private void recycleView() {

        try {

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

                // Sets the name of the action bar.
                SSActionBar.setupActionBar("TRACKS", currentArtist, name, false, this);

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

            // MOBILE: Removes the previous fragment and adds the new fragment.
            if (!isTablet) {
                changeFragment(artistFragment, "ARTISTS", "TRACKS", name, false);
            }

            Log.d(LOG_TAG, "displayTracksFragment(): SSArtistsFragment now being displayed.");
        }
    }

    // displayPlayerFragment(): Displays or removes the SSPlayerFragment from the view layout.
    @Override
    public void displayPlayerFragment(Boolean isShow, String fragToRemove, ArrayList<SSSpotifyModel> list, int position, Boolean isReset) {

        // Displays the SSPlayerFragment in the view layout.
        if (isShow) {

            listPosition = position; // Sets the selected position in the top tracks list.

            // Adds a new SSPlayerFragment onto the fragment stack and is made visible in the view
            // layout.
            SSPlayerFragment playerFragment = new SSPlayerFragment();
            playerFragment.initializeFragment(list, position);

            // TABLET: Displays the SSPlayerFragment as a DialogFragment.
            if (isTablet) {
                displayFragmentDialog(playerFragment, "PLAYER");
            }

            // MOBILE: Removes the previous fragment and adds the new fragment.
            else {
                changeFragment(playerFragment, "PLAYER", fragToRemove, list.get(position).getSong(), true);
            }

            // Stops any track playing in the background and resets the track position.
            if (isReset) {
                pauseTrack(true); // Stops any song playing in the background.
                setPosition(0); // Resets the position of the track to the beginning.
            }

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
            }

            Log.d(LOG_TAG, "displayPlayerFragment(): SSTracksFragment now being displayed.");
        }
    }

    // pauseTrack(): Signals the attached class to invoke the SSMusicService to pause playback
    // of the streamed Spotify track.
    private void pauseTrack(Boolean isStop) {
        try { ((OnMusicServiceListener) getApplication()).pauseTrack(isStop); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // setPosition(): Signals the attached class to invoke the SSMusicService to update the song
    // position.
    private void setPosition(int position) {
        try { ((OnMusicServiceListener) getApplication()).setPosition(position); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // removeAudioService(): Signals the SSApplication class to unbind and remove the SSMusicService
    // running in the background.
    private void removeAudioService() {
        try { ((OnMusicServiceListener) getApplication()).removeAudioService(); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // setCurrentTrack(): Invoked by SSPlayerFragment to update the activity on the album bitmap,
    // track name, the Spotify URL of the selected music track, and the current list position.
    @Override
    public void setCurrentTrack(Bitmap albumImage, String songName, String trackUrl, int position) {
        this.currentTrack = songName;
        this.spotifyUrl = trackUrl;
        this.listPosition = position;
    }

    // removeAudioService(): Signals the SSApplication class to setup the SSMusicService service for
    // playing audio from the SSMusicEngine class in the background.
    private void setUpAudioService() {
        try { ((OnMusicServiceListener) getApplication()).setUpAudioService(); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // updateArtistInput(): Invoked by SSArtistsFragment to keep an update of the user's artist
    // input.
    @Override
    public void updateArtistInput(String name) {
        currentInput = name; // Sets the current artist input name.
    }
}