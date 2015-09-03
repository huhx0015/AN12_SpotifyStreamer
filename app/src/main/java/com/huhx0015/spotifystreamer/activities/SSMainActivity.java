package com.huhx0015.spotifystreamer.activities;

import android.content.res.Configuration;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.fragments.SSArtistsFragment;
import com.huhx0015.spotifystreamer.fragments.SSPlayerFragment;
import com.huhx0015.spotifystreamer.fragments.SSSettingsFragment;
import com.huhx0015.spotifystreamer.fragments.SSTracksFragment;
import com.huhx0015.spotifystreamer.intent.SSShareIntent;
import com.huhx0015.spotifystreamer.interfaces.OnMusicPlayerListener;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.huhx0015.spotifystreamer.interfaces.OnTrackInfoUpdateListener;
import com.huhx0015.spotifystreamer.ui.actionbar.SSActionBar;
import com.huhx0015.spotifystreamer.ui.layouts.SSUnbind;
import com.huhx0015.spotifystreamer.ui.notifications.SSNotificationPlayer;
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
    private static final String SETTINGS_FRAGMENT = "settingsFragment"; // Used for restoring the isSettings value for rotation change events.
    private static final String TRACK_LIST = "trackListResult"; // Parcelable key value for the track list.

    // FRAGMENT VARIABLES
    private Boolean isRotationEvent = false; // Used to determine if a screen orientation change event has occurred.
    private Boolean isSettings = false; // Used to determine if the SSSettingsFragment is in focus.
    private SSPlayerFragment playFragment; // Keeps a reference to the SSPlayerFragment.
    private String currentFragment = ""; // Used to determine which fragment is currently active.
    private String currentArtist = ""; // Used to determine the current artist name.
    private String currentInput = ""; // Used to determine the current artist input.
    private String currentTrack = ""; // Used to determine the current track name.

    // LAYOUT VARIABLES
    private ActionBarDrawerToggle drawerToggle; // References the ActionBar drawer toggle object.
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
    @Bind(R.id.ss_main_activity_drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.ss_main_activity_fragment_container) FrameLayout fragmentContainer;
    @Bind(R.id.ss_main_activity_secondary_fragment_container) FrameLayout fragmentSecondaryContainer;
    @Bind(R.id.ss_main_activity_settings_fragment_container) FrameLayout settingsFragmentContainer;
    @Bind(R.id.ss_main_activity_left_drawer_container) LinearLayout leftDrawerContainer;
    @Bind(R.id.ss_main_activity_toolbar) Toolbar activityToolbar;

    /** ACTIVITY LIFECYCLE METHODS _____________________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only
    // runs when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "ACTIVITY LIFECYCLE (onCreate): onCreate invoked.");

        // Since this is a single activity application, any activity duplicates will be immediately
        // finished. This prevents having multiple instances of the SSMainActivity running.
        if (!isTaskRoot()) {
            Log.d(LOG_TAG, "onCreate(): Activity duplicate detected. Finishing activity.");
            finish();
        }

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
            isSettings = savedInstanceState.getBoolean(SETTINGS_FRAGMENT);
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

    // onConfigurationChanged(): Called by the system when the device configuration changes while
    // your activity is running.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    // onCreateOptionsMenu(): Inflates the menu when the menu key is pressed. This adds items to
    // the action bar if it is present. Required to populate the menu items in the Toolbar object.
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

            // PLAY BUTTON:
            case R.id.ss_action_play_button:

                // Displays the last selected song that was played.
                if ( (trackListResult != null) && (listPosition != -1) && !(currentFragment.equals("PLAYER")) ) {
                    displayPlayerFragment(true, currentFragment, trackListResult, listPosition, false);
                }

                // If the SSPlayerFragment is already in focus, it begins playback of the song if
                // it is not already playing or being prepared for playback.
                else if (currentFragment.equals("PLAYER")) {
                    playCurrentSong(); // Plays the current track song.
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
                    displaySettingsFragment(true, false); // Displays the SSSettingsFragment.
                }

                // Hides the SSSettingsFragment view.
                else {
                    displaySettingsFragment(false, false); // Hides the SSSettingsFragment.
                }

                return true;

            // DEFAULT:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // onPostCreate(): Called when activity start-up is complete (after onStart() and
    // onRestoreInstanceState(Bundle) have been called).
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    // onPrepareOptionsMenu(): Called whenever invalidateOptionsMenu() is invoked.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    // onSaveInstanceState(): Called to retrieve per-instance state from an activity before being
    // killed so that the state can be restored in onCreate() or onRestoreInstanceState().
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Saves current state values into the instance. These values are restored upon re-creation
        // of the activity.
        savedInstanceState.putBoolean(ROTATION_CHANGE, true);
        savedInstanceState.putBoolean(SETTINGS_FRAGMENT, isSettings);
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
            displaySettingsFragment(false, false); // Removes the SSSettingsFragment from view.
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
            SSNotificationPlayer.removeNotifications(this); // Removes all active notifications.
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

        setupDrawer(); // Initializes the drawer view for the layout.
        setupFragment(); // Initializes the fragment view for the layout.

        // If a rotation event occurred, the isRotationEvent value is reset.
        if (isRotationEvent) {
            isRotationEvent = false;
        }
    }

    // setupDrawer(): Sets up the drawer and the Toolbar for the activity layout.
    private void setupDrawer() {

        // Sets the activity Toolbar object as the main ActionBar for this activity.
        setSupportActionBar(activityToolbar);

        // Initializes the drawer toggle and sets it to the activityToolbar.
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                activityToolbar, R.string.drawer_open, R.string.drawer_close) {

            // onDrawerClosed(): Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // Creates a call to onPrepareOptionsMenu().
            }

            // onDrawerOpened(): Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // Creates a call to onPrepareOptionsMenu().
            }
        };

        drawerLayout.setDrawerListener(drawerToggle); // Sets the drawer listener to the drawerLayout.

        // Sets a listener to the drawer toggle. If the drawer indicator is not enabled,
        // onBackPressed() is invoked. This is in replacement of the old case android.R.id.home key
        // press in onOptionsItemSelected() method.
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // If the drawer indicator is not enabled, onBackPressed() is invoked.
                if (!drawerToggle.isDrawerIndicatorEnabled()) {
                    onBackPressed();
                }
            }
        });

        // Retrieves the DrawerLayout to set the status bar color. This only takes effect on
        // Lollipop, or when using translucentStatusBar on KitKat.
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.ss_toolbar_dark_color));
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
                    R.id.ss_main_activity_fragment_container, currentArtist, currentTrack,
                    activityToolbar, drawerToggle, weakRefActivity);

            // SSTracksFragment: If a screen orientation event has occurred and the fragment that
            // was in focus was SSTracksFragment, a new SSTracksFragment is created and is made
            // visible in the view layout.
            if ( (isRotationEvent) && (currentFragment.equals("TRACKS")) ) {
                SSTracksFragment newTracksFragment = new SSTracksFragment();
                newTracksFragment.initializeFragment(currentTrack, true);
                SSFragmentView.addFragment(newTracksFragment, fragmentSecondaryContainer,
                        R.id.ss_main_activity_secondary_fragment_container, "TRACKS", false,
                        activityToolbar, drawerToggle, weakRefActivity);

                // Sets up the action bar.
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, "TRACKS", currentArtist, currentArtist, false);
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

            // SSSettingsFragment: Reloads the SSSettingsFragment into focus, if the
            // SSSettingsFragment was displayed prior to screen rotation.
            if ( (isRotationEvent) && (isSettings) ) {
                displaySettingsFragment(true, true);
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, "SETTINGS", null, null, true);
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
                        currentArtist, currentTrack, activityToolbar, drawerToggle, weakRefActivity);

                // SSArtistsFragment: Attempts to reload the SSArtistFragment, if the
                // SSTracksFragment was in prior focus.
                if (!isReloaded) {
                    SSFragmentView.reloadFragment(artistsFragment, "ARTISTS", "ARTISTS",
                            fragmentContainer, R.id.ss_main_activity_fragment_container, currentArtist,
                            currentTrack, activityToolbar, drawerToggle, weakRefActivity);
                }
            }

            // SSSettingsFragment: Reloads the SSSettingsFragment into focus, if the
            // SSSettingsFragment was displayed prior to screen rotation.
            if ( (isRotationEvent) && (isSettings) ) {
                displaySettingsFragment(true, true);
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, "SETTINGS", null, null, true);
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
            SSFragmentView.removeFragment(fragmentContainer, fragToRemove, false, activityToolbar,
                    drawerToggle, weakRefActivity);
        }

        // Adds the fragment to the primary fragment container.
        SSFragmentView.addFragment(frag, fragmentContainer, R.id.ss_main_activity_fragment_container,
                fragToAdd, isAnimated, activityToolbar, drawerToggle, weakRefActivity);

        // Sets the name of the action bar.
        SSActionBar.setupActionBar(activityToolbar, drawerToggle, fragToAdd, currentArtist, subtitle, true);
        currentFragment = fragToAdd; // Sets the current active fragment.

        Log.d(LOG_TAG, "changeFragment(): Fragment changed.");
    }

    // displayFragmentDialog(): Displays the DialogFragment view for the specified fragment.
    private void displayFragmentDialog(DialogFragment frag, String fragType) {
        FragmentManager fragMan = getSupportFragmentManager(); // Sets up the FragmentManager.
        frag.show(fragMan, fragType); // Displays the DialogFragment.
    }

    // displaySettingsFragment(): Displays or hides the SSSettingsFragment view.
    private void displaySettingsFragment(Boolean isShow, Boolean isRotation) {

        // Displays the SSSettingsFragment view.
        if (isShow) {

            isSettings = true; // Indicates that the SSSettingsFragment is active.
            settingsFragmentContainer.setVisibility(View.VISIBLE);

            // If the SSSettingsFragment is being re-added after a screen orientation change,
            // fragment animations are disabled.
            Boolean isAnimate = true;
            if (isRotation) {
                isAnimate = false;
            }

            // Sets up the SSSettingsFragment.
            SSSettingsFragment settingsFragment = new SSSettingsFragment();
            SSFragmentView.addFragment(settingsFragment, settingsFragmentContainer,
                    R.id.ss_main_activity_settings_fragment_container, "SETTINGS", isAnimate,
                    activityToolbar, drawerToggle, weakRefActivity);
        }

        // Hides the SSSettingsFragment view.
        else {

            SSFragmentView.removeFragment(settingsFragmentContainer, "SETTINGS", true,
                    activityToolbar, drawerToggle, weakRefActivity);

            // SSTracksFragment: Sets up the action bar attributes.
            if (currentFragment.equals("TRACKS")) {

                // TABLET: Updates the action bar without the BACK button.
                if (isTablet) {
                    SSActionBar.setupActionBar(activityToolbar, drawerToggle, "TRACKS", currentArtist, currentArtist, false);
                }

                // MOBILE: Updates the action bar with the BACK button present.
                else {
                    SSActionBar.setupActionBar(activityToolbar, drawerToggle, "TRACKS", currentArtist, currentArtist, true);
                }
            }

            // SSArtistsFragment | SSPlayerFragment: Sets up the action bar attributes.
            else {
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, currentFragment, currentArtist, currentTrack, true);
            }

            isSettings = false; // Indicates that the SSSettingsFragment is inactive.
        }
    }

    /** RECYCLE METHODS ________________________________________________________________________ **/

    // recycleView(): Recycles the View objects to clear up resources prior to Activity destruction.
    private void recycleView() {

        // Unbinds all Drawable objects attached to the current layout.
        try {
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

                SSFragmentView.addFragment(tracksFragment, fragmentSecondaryContainer,
                        R.id.ss_main_activity_secondary_fragment_container, "TRACKS", true,
                        activityToolbar, drawerToggle, weakRefActivity);

                // Sets the name of the action bar.
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, "TRACKS", currentArtist, name, false);

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
            playFragment = new SSPlayerFragment();
            playFragment.initializeFragment(list, position);

            // TABLET: Displays the SSPlayerFragment as a DialogFragment.
            if (isTablet) {
                displayFragmentDialog(playFragment, "PLAYER");
            }

            // MOBILE: Removes the previous fragment and adds the new fragment.
            else {
                changeFragment(playFragment, "PLAYER", fragToRemove, list.get(position).getSong(), true);
            }

            // Stops any track playing in the background and resets the track position.
            if (isReset) {

                // Removes any active notification player.
                SSNotificationPlayer.removeNotifications(this);

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

    // setCurrentTrack(): Invoked by SSPlayerFragment to update the activity on the track name, the
    // Spotify URL of the selected music track, and the current list position.
    @Override
    public void setCurrentTrack(String songName, String trackUrl, int position) {
        currentTrack = songName;
        spotifyUrl = trackUrl;
        listPosition = position;
    }

    // updateActionBar(): Invoked by SSPlayerFragment to update the ActionBar title with the current
    // track song title.
    @Override
    public void updateActionBar(String songName) {

        // Only updates the ActionBar if the device is not a tablet device.
        if (!isTablet) {
            SSActionBar.setupActionBar(activityToolbar, drawerToggle, "PLAYER", currentArtist, songName, true);
        }
    }

    // updateArtistInput(): Invoked by SSArtistsFragment to keep an update of the user's artist
    // input.
    @Override
    public void updateArtistInput(String name) {
        currentInput = name; // Sets the current artist input name.
    }

    // pauseTrack(): Signals the attached class to invoke the SSMusicService to pause playback
    // of the streamed Spotify track.
    private void pauseTrack(Boolean isStop) {
        try { ((OnMusicServiceListener) getApplication()).pauseTrack(isStop); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // playCurrentSong(): Signals the SSPlayerFragment to play the current tracklist song.
    private void playCurrentSong() {

        if (playFragment != null) {
            try { ((OnMusicPlayerListener) playFragment).playCurrentSong(); }
            catch (ClassCastException cce) {} // Catch for class cast exception errors.
        }
    }

    // removeAudioService(): Signals the SSApplication class to unbind and remove the SSMusicService
    // running in the background.
    private void removeAudioService() {
        try { ((OnMusicServiceListener) getApplication()).removeAudioService(); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // setPosition(): Signals the attached class to invoke the SSMusicService to update the song
    // position.
    private void setPosition(int position) {
        try { ((OnMusicServiceListener) getApplication()).setPosition(position); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // removeAudioService(): Signals the SSApplication class to setup the SSMusicService service for
    // playing audio from the SSMusicEngine class in the background.
    private void setUpAudioService() {
        try { ((OnMusicServiceListener) getApplication()).setUpAudioService(); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }
}