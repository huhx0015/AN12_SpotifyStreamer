package com.huhx0015.spotifystreamer.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.fragments.SSArtistsFragment;
import com.huhx0015.spotifystreamer.fragments.SSPlayerFragment;
import com.huhx0015.spotifystreamer.fragments.SSSettingsFragment;
import com.huhx0015.spotifystreamer.fragments.SSTracksFragment;
import com.huhx0015.spotifystreamer.intent.SSShareIntent;
import com.huhx0015.spotifystreamer.interfaces.OnMusicPlayerListener;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.interfaces.OnSnackbarDisplayListener;
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.huhx0015.spotifystreamer.interfaces.OnTrackInfoUpdateListener;
import com.huhx0015.spotifystreamer.ui.actionbar.SSActionBar;
import com.huhx0015.spotifystreamer.ui.layouts.SSUnbind;
import com.huhx0015.spotifystreamer.ui.notifications.SSNotificationPlayer;
import com.huhx0015.spotifystreamer.ui.toast.SSSnackbar;
import com.huhx0015.spotifystreamer.ui.toast.SSToast;
import com.huhx0015.spotifystreamer.ui.views.SSFragmentView;
import com.squareup.picasso.Picasso;
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
        OnSnackbarDisplayListener, OnTrackInfoUpdateListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private static WeakReference<AppCompatActivity> weakRefActivity = null; // Used to maintain a weak reference to the activity.
    private Boolean isActivityPaused = false; // Used to determine if the activity is currently in an onPause() state.
    private Boolean isFinishing = false; // Used to determine if the activity is currently finishing.

    // DATA VARIABLES
    private static final String ARTIST_IMAGE_URL = "artistImageUrl"; // Used for restoring the artist image URL value for rotation change events.
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
    private String currentArtist = "Spotify Streamer M"; // Used to determine the current artist name.
    private String currentArtistUrl = null; // Used to determine the current artist image URL.
    private String currentInput = ""; // Used to determine the current artist input.
    private String currentTrack = ""; // Used to determine the current track name.

    // LAYOUT VARIABLES
    private ActionBarDrawerToggle drawerToggle; // References the ActionBar drawer toggle object.
    private Boolean isTablet = false; // Used to determine if the current device is a mobile or tablet device.
    private float curDensity; // References the density value of the current device.

    // LIST VARIABLES
    private ArrayList<SSSpotifyModel> artistListResult = new ArrayList<>(); // Stores the artist list result.
    private ArrayList<SSSpotifyModel> trackListResult = new ArrayList<>(); // Stores the track list result.
    private int listPosition = -1; // Used to determine the current position in the top tracks list.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSMainActivity.class.getSimpleName();

    // SHARE VARIABLES
    private String spotifyUrl = ""; // Used to reference the Spotify track URL of the current track.

    // TAG VARIABLES
    private static final String ARTISTS_TAG = "ARTISTS"; // Tag for SSArtistsFragment.
    private static final String PLAYER_TAG = "PLAYER"; // Tag for SSPlayerFragment.
    private static final String SETTINGS_TAG = "SETTINGS"; // Tag for SSSettingsFragment.
    private static final String TRACKS_TAG = "TRACKS"; // Tag for SSTracksFragment.

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_main_activity_drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.ss_main_activity_fragment_container) FrameLayout fragmentContainer;
    @Bind(R.id.ss_main_activity_secondary_fragment_container) FrameLayout fragmentSecondaryContainer;
    @Bind(R.id.ss_main_activity_settings_fragment_container) FrameLayout settingsFragmentContainer;
    @Bind(R.id.ss_drawer_next_button) ImageButton drawerNextButton;
    @Bind(R.id.ss_drawer_play_button) ImageButton drawerPlayButton;
    @Bind(R.id.ss_drawer_pause_button) ImageButton drawerPauseButton;
    @Bind(R.id.ss_drawer_previous_button) ImageButton drawerPreviousButton;
    @Bind(R.id.ss_main_left_drawer_artist_image) ImageView drawerArtistImage;
    @Bind(R.id.ss_drawer_player_controls_container) LinearLayout drawerPlayerContainer;
    @Bind(R.id.ss_main_activity_layout) LinearLayout mainLayout;
    @Bind(R.id.ss_main_left_drawer_artist_name) TextView drawerArtistNameText;
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
            currentArtistUrl = savedInstanceState.getString(ARTIST_IMAGE_URL);
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

    // onResume(): This function is called immediately after onStart() runs and also runs after
    // focus has returned to this activity.
    @Override
    public void onResume() {
        super.onResume();

        isActivityPaused = false; // Indicates that this activity is no longer in an onPause state.

        Log.d(LOG_TAG, "ACTIVITY LIFECYCLE (onResume): onResume invoked.");
    }

    // onPause(): This function is called whenever the fragment is suspended.
    @Override
    public void onPause() {
        super.onPause();

        isActivityPaused = true; // Indicates that this activity has entered an onPause state.

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
                if ( (trackListResult != null) && (listPosition != -1) && !(currentFragment.equals(PLAYER_TAG)) ) {
                    displayPlayerFragment(true, currentFragment, trackListResult, listPosition, false);
                }

                // If the SSPlayerFragment is already in focus, it begins playback of the song if
                // it is not already playing or being prepared for playback.
                else if (currentFragment.equals(PLAYER_TAG)) {
                    playCurrentSong(); // Plays the current track song.
                }

                // Displays a Snackbar/Toast message, indicating that no track has been selected.
                else {
                    displaySnackbar("No previous track has been selected.");
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
        savedInstanceState.putString(ARTIST_IMAGE_URL, currentArtistUrl);
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
        else if ( (currentFragment.equals(PLAYER_TAG)) && !(isTablet) ) {
            displayPlayerFragment(false, TRACKS_TAG, trackListResult, listPosition, false);
        }

        // If the SSTracksFragment is currently being displayed, the fragment is removed and
        // switched with the SSArtistsFragment view.
        else if ( (currentFragment.equals(TRACKS_TAG)) && !(isTablet) ) {
            displayTracksFragment(false, currentInput, currentArtistUrl);
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

        // Retrieves the device's density attributes.
        curDensity = getResources().getDisplayMetrics().density;

        // Updates the isTablet value to determine if the device is a mobile or tablet device.
        isTablet = getResources().getBoolean(R.bool.isTablet);
        Log.d(LOG_TAG, "setupLayout(): isTablet: " + isTablet);

        setContentView(R.layout.ss_main_activity); // Sets the XML layout file for the activity.
        ButterKnife.bind(this); // ButterKnife view injection initialization.

        setupDrawer(); // Initializes the drawer view for the layout.
        setupFragment(); // Initializes the fragment view for the layout.
        setupImages(false); // Sets up the images for the ImageView and ImageButton objects in this activity layout.
        setupButtons(); // Sets up the button listeners for the Button objects in this activity layout.
        setupText(); // Sets up the TextView objects for the layout.

        // If a rotation event occurred, the isRotationEvent value is reset.
        if (isRotationEvent) {
            isRotationEvent = false;
        }
    }

    // setupButtons(): Sets up the button listeners for the activity layout.
    private void setupButtons() {

        // PLAYER BUTTONS:
        // -----------------------------------------------------------------------------------------

        // NEXT: Sets up the listener and the actions for the NEXT button.
        drawerNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playNextSong(true); // Signals SSPlayerFragment to play the next song in the tracklist.
            }
        });

        // PLAY: Sets up the listener and the actions for the PLAY button.
        drawerPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playCurrentSong(); // Begins playback of the current song track.
            }
        });

        // PAUSE: Sets up the listener and the actions for the PAUSE button.
        drawerPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pauseTrack(false); // Pauses the currently playing song track.
            }
        });

        // PREVIOUS: Sets up the listener and the actions for the PREVIOUS button.
        drawerPreviousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playNextSong(false); // Signals SSPlayerFragment to play the previous song in the tracklist.
            }
        });
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
        Fragment artistsFragment = fragManager.findFragmentByTag(ARTISTS_TAG);
        Fragment tracksFragment = fragManager.findFragmentByTag(TRACKS_TAG);
        Fragment playerFragment = fragManager.findFragmentByTag(PLAYER_TAG);

        // TABLET: Reloads the SSTracksFragment (if it was in focus previously) and the
        // SSArtistsFragment in their respective containers.
        if (isTablet) {

            Log.d(LOG_TAG, "setupFragment(): Reloading fragments for tablet view...");

            SSFragmentView.reloadFragment(artistsFragment, ARTISTS_TAG, ARTISTS_TAG, fragmentContainer,
                    R.id.ss_main_activity_fragment_container, currentArtist, currentTrack,
                    activityToolbar, drawerToggle, weakRefActivity);

            // SSTracksFragment: If a screen orientation event has occurred and the fragment that
            // was in focus was SSTracksFragment, a new SSTracksFragment is created and is made
            // visible in the view layout.
            if ( (isRotationEvent) && (currentFragment.equals(TRACKS_TAG)) ) {
                SSTracksFragment newTracksFragment = new SSTracksFragment();
                newTracksFragment.initializeFragment(currentTrack, true);
                SSFragmentView.addFragment(newTracksFragment, fragmentSecondaryContainer,
                        R.id.ss_main_activity_secondary_fragment_container, TRACKS_TAG, false,
                        activityToolbar, drawerToggle, weakRefActivity);

                // Sets up the action bar.
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, TRACKS_TAG, currentArtist, currentArtist, false);
            }

            // SSPlayerFragment: If a screen orientation event has occurred and the SSPlayerFragment
            // DialogFragment was displayed prior, the SSPlayerFragment DialogFragment is re-shown.
            if ( (isRotationEvent) && (playerFragment != null)) {

                try {
                    displayPlayerFragment(true, TRACKS_TAG, trackListResult, listPosition, false);
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
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, SETTINGS_TAG, null, null, true);
            }
        }

        // MOBILE: Reloads the fragment that was in focus prior to the screen orientation change.
        else {

            Log.d(LOG_TAG, "setupFragment(): Reloading fragments for mobile view...");

            // SSPlayerFragment: Attempts to reload the SSPlayerFragment, if the SSPlayerFragment
            // was in prior focus.
            if ( (isRotationEvent) && (playerFragment != null) && (currentFragment.equals(PLAYER_TAG)) ) {

                try {
                    displayPlayerFragment(true, TRACKS_TAG, trackListResult, listPosition, false);
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
                        TRACKS_TAG, fragmentContainer, R.id.ss_main_activity_fragment_container,
                        currentArtist, currentTrack, activityToolbar, drawerToggle, weakRefActivity);

                // SSArtistsFragment: Attempts to reload the SSArtistFragment, if the
                // SSTracksFragment was in prior focus.
                if (!isReloaded) {
                    SSFragmentView.reloadFragment(artistsFragment, ARTISTS_TAG, ARTISTS_TAG,
                            fragmentContainer, R.id.ss_main_activity_fragment_container, currentArtist,
                            currentTrack, activityToolbar, drawerToggle, weakRefActivity);
                }
            }

            // SSSettingsFragment: Reloads the SSSettingsFragment into focus, if the
            // SSSettingsFragment was displayed prior to screen rotation.
            if ( (isRotationEvent) && (isSettings) ) {
                displaySettingsFragment(true, true);
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, SETTINGS_TAG, null, null, true);
            }
        }
    }

    // setupImages(): Sets up the images for the ImageView and ImageButton objects in this activity
    // layout.
    private void setupImages(Boolean onlyArtist) {

        // CURRENT ARTIST IMAGE:
        // If the currentArtistUrl value is null, the standard application image is loaded instead.
        if (currentArtistUrl == null) {

            Picasso.with(this)
                    .load(R.drawable.ic_launcher)
                    .into(drawerArtistImage);
        }

        // Loads the current artist image into the drawer ImageView object.
        else {

            Picasso.with(this)
                    .load(currentArtistUrl)
                    .into(drawerArtistImage);
        }

        // Loads the image resources for the rest of the ImageButton objects.
        if (!onlyArtist) {

            // NEXT BUTTON:
            Picasso.with(this)
                    .load(android.R.drawable.ic_media_next)
                    .resize((int) (48 * curDensity), (int) (48 * curDensity))
                    .into(drawerNextButton);

            // PLAY BUTTON:
            Picasso.with(this)
                    .load(android.R.drawable.ic_media_play)
                    .resize((int) (48 * curDensity), (int) (48 * curDensity))
                    .into(drawerPlayButton);

            // PAUSE BUTTON:
            Picasso.with(this)
                    .load(android.R.drawable.ic_media_pause)
                    .resize((int) (48 * curDensity), (int) (48 * curDensity))
                    .into(drawerPauseButton);

            // PREVIOUS BUTTON:
            Picasso.with(this)
                    .load(android.R.drawable.ic_media_previous)
                    .resize((int) (48 * curDensity), (int) (48 * curDensity))
                    .into(drawerPreviousButton);
        }
    }

    // setupText(): Sets up the TextView objects for this layout.
    private void setupText() {

        // Sets the current selected artist's name.
        drawerArtistNameText.setText(currentArtist.toUpperCase());
        drawerArtistNameText.setShadowLayer(8, 2, 2, Color.BLACK); // Sets the shadow layer effect.
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
                    R.id.ss_main_activity_settings_fragment_container, SETTINGS_TAG, isAnimate,
                    activityToolbar, drawerToggle, weakRefActivity);
        }

        // Hides the SSSettingsFragment view.
        else {

            SSFragmentView.removeFragment(settingsFragmentContainer, SETTINGS_TAG, true,
                    activityToolbar, drawerToggle, weakRefActivity);

            // SSTracksFragment: Sets up the action bar attributes.
            if (currentFragment.equals(TRACKS_TAG)) {

                // TABLET: Updates the action bar without the BACK button.
                if (isTablet) {
                    SSActionBar.setupActionBar(activityToolbar, drawerToggle, TRACKS_TAG, currentArtist, currentArtist, false);
                }

                // MOBILE: Updates the action bar with the BACK button present.
                else {
                    SSActionBar.setupActionBar(activityToolbar, drawerToggle, TRACKS_TAG, currentArtist, currentArtist, true);
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
    public void displayTracksFragment(Boolean isShow, String name, String artistImageUrl) {

        // Displays the SSTracksFragment in the view layout.
        if (isShow) {

            currentArtistUrl = artistImageUrl; // Sets the selected artist image URL.
            setupImages(true); // Loads the selected artist image resource into the drawer.

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
                        R.id.ss_main_activity_secondary_fragment_container, TRACKS_TAG, true,
                        activityToolbar, drawerToggle, weakRefActivity);

                // Sets the name of the action bar.
                SSActionBar.setupActionBar(activityToolbar, drawerToggle, TRACKS_TAG, currentArtist, name, false);

                currentFragment = TRACKS_TAG; // Sets the current active fragment.
            }

            // MOBILE: Removes the previous fragment and adds the new fragment.
            else {
                changeFragment(tracksFragment, TRACKS_TAG, ARTISTS_TAG, name, true);
            }

            currentArtist = name; // Sets the name of the current artist.
            setupText(); // Updates the selected artist name on the drawer.
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
                changeFragment(artistFragment, ARTISTS_TAG, TRACKS_TAG, name, false);
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
                displayFragmentDialog(playFragment, PLAYER_TAG);
            }

            // MOBILE: Removes the previous fragment and adds the new fragment.
            else {
                changeFragment(playFragment, PLAYER_TAG, fragToRemove, list.get(position).getSong(), true);
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
                changeFragment(tracksFragment, TRACKS_TAG, PLAYER_TAG, list.get(position).getArtist(), false);
            }

            Log.d(LOG_TAG, "displayPlayerFragment(): SSTracksFragment now being displayed.");
        }
    }

    // displaySnackbar(): An interface method that is invoked via the OnSnackbarDisplayListener
    // interface class, which displays a Snackbar message.
    @Override
    public void displaySnackbar(String message) {

        // Displays a snackbar message display at the bottom of the screen, as long as this activity
        // is not currently paused.
        if (!isActivityPaused) {
            SSSnackbar.snackOnThis(message, mainLayout);
        }

        // Displays a Toast message instead if the activity is currently in an onPause state.
        else {
            SSToast.toastyPopUp(message, this);
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
            SSActionBar.setupActionBar(activityToolbar, drawerToggle, PLAYER_TAG, currentArtist, songName, true);
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

        // Displays a Snackbar/Toast message, indicating that no track has been selected.
        if (playFragment == null && !isFinishing) {
            displaySnackbar("No previous track has been selected.");
        }

        try { ((OnMusicServiceListener) getApplication()).pauseTrack(isStop); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // playCurrentSong(): Signals the SSPlayerFragment to play the current tracklist song.
    private void playCurrentSong() {

        if (playFragment != null) {
            try { ((OnMusicPlayerListener) playFragment).playCurrentSong(); }
            catch (ClassCastException cce) {} // Catch for class cast exception errors.
        }

        // Displays a Snackbar/Toast message, indicating that no track has been selected.
        else {
            displaySnackbar("No previous track has been selected.");
        }
    }

    // playNextSong(): Signals the SSPlayerFragment to play the previous or next song in the
    // tracklist.
    private void playNextSong(Boolean isNext) {

        if (playFragment != null) {
            try { ((OnMusicPlayerListener) playFragment).playNextSong(isNext, true); }
            catch (ClassCastException cce) {} // Catch for class cast exception errors.
        }

        // Displays a Snackbar/Toast message, indicating that no track has been selected.
        else {
            displaySnackbar("No previous track has been selected.");
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