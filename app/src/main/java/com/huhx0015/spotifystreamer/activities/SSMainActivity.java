package com.huhx0015.spotifystreamer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.fragments.SSArtistsFragment;
import com.huhx0015.spotifystreamer.fragments.SSPlayerFragment;
import com.huhx0015.spotifystreamer.fragments.SSTracksFragment;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.huhx0015.spotifystreamer.services.SSMusicService;
import com.huhx0015.spotifystreamer.ui.layouts.SSUnbind;
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
    private static WeakReference<SSMainActivity> weakRefActivity = null; // Used to maintain a weak reference to the activity.

    // DATA VARIABLES
    private static final String CURRENT_FRAGMENT = "currentFragment"; // Used for restoring the proper fragment for rotation change events.
    private static final String ARTIST_INPUT = "artistInput"; // Used for restoring the artist input value for rotation change events.
    private static final String ARTIST_NAME = "artistName"; // Used for restoring the artist name value for rotation change events.
    private static final String ARTIST_LIST = "artistListResult"; // Parcelable key value for the artist list.
    private static final String TRACK_LIST = "trackListResult"; // Parcelable key value for the track list.

    // FRAGMENT VARIABLES
    private String currentFragment = ""; // Used to determine which fragment is currently active.
    private String currentArtist = ""; // Used to determine the current artist name.
    private String currentInput = ""; // Used to determine the current artist input.

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

    /** ACTIVITY LIFECYCLE METHODS _____________________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only
    // runs when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creates a weak reference of this activity.
        weakRefActivity = new WeakReference<SSMainActivity>(this);

        // Checks to see if there is a saved instance that was saved prior, from events such as
        // rotation changes.
        if (savedInstanceState != null) {

            // Restores the saved instance values.
            currentFragment = savedInstanceState.getString(CURRENT_FRAGMENT);
            artistListResult = savedInstanceState.getParcelableArrayList(ARTIST_LIST);
            trackListResult = savedInstanceState.getParcelableArrayList(TRACK_LIST);
            currentArtist = savedInstanceState.getString(ARTIST_NAME);
            currentInput = savedInstanceState.getString(ARTIST_INPUT);
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
        recycleMemory(); // Recycles all services and View objects to free up memory resources.
        super.onDestroy();
    }

    /** ACTIVITY EXTENSION METHODS _____________________________________________________________ **/

    /* TODO: Disabled for P1.
    // onCreateOptionsMenu(): Inflates the menu when the menu key is pressed. This adds items to
    // the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ss_main_activity_menu, menu);
        return true;
    }
    */

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
        savedInstanceState.putString(CURRENT_FRAGMENT, currentFragment);
        savedInstanceState.putParcelableArrayList(ARTIST_LIST, artistListResult);
        savedInstanceState.putParcelableArrayList(TRACK_LIST, trackListResult);
        savedInstanceState.putString(ARTIST_INPUT, currentInput);
        savedInstanceState.putString(ARTIST_NAME, currentArtist);

        // Always calls the superclass, so it can save the view hierarchy state.
        super.onSaveInstanceState(savedInstanceState);
    }

    /** PHYSICAL BUTTON METHODS ________________________________________________________________ **/

    // BACK KEY:
    // onBackPressed(): Defines the action to take when the physical back button key is pressed.
    @Override
    public void onBackPressed() {

        // If the SSPlayerFragment is currently being displayed, the fragment is removed and
        // switched with the SSTracksFragmentView.
        if (currentFragment.equals("PLAYER")) {
            displayPlayerFragment(false, trackListResult, listPosition);
        }

        // If the SSTracksFragment is currently being displayed, the fragment is removed and
        // switched with the SSArtistsFragment view.
        else if (currentFragment.equals("TRACKS")) {
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

    /** LAYOUT METHODS _________________________________________________________________________ **/

    // setupLayout(): Sets up the layout for the activity.
    private void setupLayout() {

        setContentView(R.layout.ss_main_activity); // Sets the XML layout file for the activity.
        ButterKnife.bind(this); // ButterKnife view injection initialization.

        setupFragment(); // Initializes the fragment view for the layout.
    }

    // setupFragment(): Initializes the fragment view for the layout.
    private void setupFragment() {

        // Checks to see if there are any retained fragments when the activity is re-created from a
        // screen rotation event.
        FragmentManager fragManager = getSupportFragmentManager();
        Fragment artistsFragment = fragManager.findFragmentByTag("ARTISTS");
        Fragment tracksFragment = fragManager.findFragmentByTag("TRACKS");
        Fragment playerFragment = fragManager.findFragmentByTag("PLAYER");

        // SSPLAYERFRAGMENT: If the SSPlayerFragment was in focus before the screen rotation event, the retained
        // SSPlayerFragment is re-added instead.
        if ( (playerFragment != null) && (currentFragment.equals("PLAYER"))) {

            // Checks to see if the playerFragment already exists in the layout. If not, the
            // fragment is added.
            if (!playerFragment.isInLayout()) {
                addFragment(playerFragment, "PLAYER", false); // Adds the fragment without transition.
                setupActionBar("PLAYER", currentArtist); // Sets up the action bar attributes.
                Log.d(LOG_TAG, "setupFragment(): Adding SSPlayerFragment to the layout.");
            }

            else {
                Log.d(LOG_TAG, "setupFragment(): Restoring the SSPlayerFragment from a rotation change event.");
            }
        }

        // SSTRACKSFRAGMENT: If the SSTracksFragment was in focus before the screen rotation event, the retained
        // SSTracksFragment is re-added instead.
        else if ( (tracksFragment != null) && (currentFragment.equals("TRACKS"))) {

            // Checks to see if the tracksFragment already exists in the layout. If not, the
            // fragment is added.
            if (!tracksFragment.isInLayout()) {
                addFragment(tracksFragment, "TRACKS", false); // Adds the fragment without transition.
                setupActionBar("TRACKS", currentArtist); // Sets up the action bar attributes.
                Log.d(LOG_TAG, "setupFragment(): Adding SSTracksFragment to the layout.");
            }

            else {
                Log.d(LOG_TAG, "setupFragment(): Restoring the SSTracksFragment from a rotation change event.");
            }
        }

        // SSARTISTSFRAGMENT: The SSArtistsFragment is setup as the primary fragment in focus.
        else {

            // If the fragment is null, it indicates that it is not on the fragment stack. The fragment
            // is initialized.
            if (artistsFragment == null) {
                artistsFragment = new SSArtistsFragment(); // Initializes the SSArtistsFragment class.
            }

            // Checks to see if the artistFragment already exists in the layout. If not, the
            // fragment is added.
            if (!artistsFragment.isInLayout()) {
                addFragment(artistsFragment, "ARTISTS", false); // Adds the fragment without transition.
                setupActionBar("ARTISTS", null); // Sets up the action bar attributes.
                Log.d(LOG_TAG, "setupFragment(): Adding the SSArtistsFragment to the layout.");
            }

            else {
                Log.d(LOG_TAG, "setupFragment(): Restoring the SSArtistsFragment from a rotation change event.");
            }
        }
    }

    // setupActionBar(): Sets up the action bar attributes for the activity.
    private void setupActionBar(String actionType, String subtitle) {

        ActionBar actionBar = getSupportActionBar(); // References the action bar.

        // TRACKS:
        if (actionType.equals("TRACKS")) {

            if (actionBar != null) {
                actionBar.setTitle("Top 10 Tracks"); // Sets the title of the action bar.
                actionBar.setSubtitle(subtitle); // Sets the name of the current artist as the subtitle.
                actionBar.setDisplayHomeAsUpEnabled(true); // Enables the back button in the action bar.
            }
        }

        // PLAYER:
        else if (actionType.equals("PLAYER")) {

            if (actionBar != null) {
                actionBar.setTitle("Now Playing"); // Sets the title of the action bar.
                actionBar.setSubtitle(currentArtist + " - " + subtitle); // Sets the name of the track as the subtitle.
                actionBar.setDisplayHomeAsUpEnabled(true); // Enables the back button in the action bar.
            }
        }

        // DEFAULT:
        else {

            if (actionBar != null) {
                actionBar.setTitle(R.string.app_name); // Sets the title of the action bar.
                actionBar.setSubtitle(null); // Disables the subtitles of the action bar.
                actionBar.setDisplayHomeAsUpEnabled(false); // Disables the back button in the action bar.
            }
        }
    }

    /** FRAGMENT METHODS _______________________________________________________________________ **/

    // addFragment(): Sets up the fragment view.
    private void addFragment(Fragment fragment, final String fragType, Boolean isAnimated) {

        if ((weakRefActivity.get() != null) && (!weakRefActivity.get().isFinishing())) {

            // Initializes the manager and transaction objects for the fragments.
            android.support.v4.app.FragmentManager fragMan = weakRefActivity.get().getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragTrans = fragMan.beginTransaction();
            fragTrans.replace(R.id.ss_main_activity_fragment_container, fragment, fragType);

            // Makes the changes to the fragment manager and transaction objects.
            fragTrans.commitAllowingStateLoss();

            // Sets up the transition animation.
            if (isAnimated) {
                setFragmentTransition(fragType, true); // Sets the fragment transition animation.
            }

            // Displays the fragment view without any transition animations.
            else {
                fragmentContainer.setVisibility(View.VISIBLE); // Displays the fragment.
            }
        }
    }

    // changeFragment(): Removes the previously existing fragment and adds a new fragment in it's
    // place.
    private void changeFragment(Fragment frag, String fragToAdd, String fragToRemove, String subtitle,
                                Boolean isAnimated) {

        removeFragment(fragToRemove, false); // Removes the SSArtistsFragment from the stack.

        // Adds the fragment with the transition animation.
        addFragment(frag, fragToAdd, isAnimated);

        setupActionBar(fragToAdd, subtitle); // Sets the name of the action bar.
        currentFragment = fragToAdd; // Sets the current active fragment.
    }

    // removeFragment(): This method is responsible for removing the fragment view.
    private void removeFragment(final String fragType, Boolean isAnimated) {

        if ((weakRefActivity.get() != null) && (!weakRefActivity.get().isFinishing())) {

            // Animates the fragment transition.
            if (isAnimated) {
                setFragmentTransition(fragType, false); // Sets the fragment transition animation.
            }

            // The fragment is removed from the view layout.
            else {

                // Initializes the manager and transaction objects for the fragments.
                FragmentManager fragMan = getSupportFragmentManager();
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragType);
                fragMan.beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                fragMan.popBackStack(); // Pops the fragment from the stack.
                fragmentContainer.removeAllViews(); // Removes all views in the layout.
                fragmentContainer.setVisibility(View.INVISIBLE); // Hides the fragment.

                Log.d(LOG_TAG, "removeFragment(): Fragment has been removed.");
            }
        }
    }

    // setFragmentTransition(): Sets the fragment transition animation, based on the specified
    // fragment type.
    private void setFragmentTransition(final String fragType, final Boolean isAppearing) {

        int animationResource; // References the animation XML resource file.

        // Sets the animation XML resource file, based on the fragment type.
        // TRACKS:
        if (fragType.equals("TRACKS")) {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {
                animationResource = R.anim.slide_down; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {
                animationResource = R.anim.slide_up; // Sets the animation XML resource file.
            }
        }

        // PLAYER:
        else if (fragType.equals("PLAYER")) {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {
                animationResource = R.anim.slide_right; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {
                animationResource = R.anim.slide_left; // Sets the animation XML resource file.
            }
        }

        // ARTISTS:
        else {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {
                animationResource = R.anim.slide_up; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {
                animationResource = R.anim.slide_down; // Sets the animation XML resource file.
            }
        }

        // Loads the animation from the XML animation resource file.
        Animation fragmentAnimation = AnimationUtils.loadAnimation(this, animationResource);

        // Sets the AnimationListener for the animation.
        fragmentAnimation.setAnimationListener(new Animation.AnimationListener() {

            // onAnimationStart(): Runs when the animation is started.
            @Override
            public void onAnimationStart(Animation animation) {

                // FRAGMENT APPEARANCE ANIMATION:
                if (isAppearing) {
                    fragmentContainer.setVisibility(View.VISIBLE); // Displays the fragment.
                }
            }

            // onAnimationEnd(): The fragment is removed after the animation ends.
            @Override
            public void onAnimationEnd(Animation animation) {

                Log.d(LOG_TAG, "setFragmentTransition(): Fragment animation has ended.");

                // FRAGMENT REMOVAL ANIMATION:
                if (!isAppearing) {
                    removeFragment(fragType, false); // Removes the fragment from the view.
                }
            }

            // onAnimationRepeat(): Runs when the animation is repeated.
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        fragmentContainer.startAnimation(fragmentAnimation); // Starts the animation.
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

    /** RECYCLE METHODS ________________________________________________________________________ **/

    // recycleMemory(): Recycles background services and View objects to clear up resources prior to
    // Activity destruction.
    private void recycleMemory() {

        try {

            // Stops the SSMusicService running in the background.
            if (audioIntent != null) {
                stopService(audioIntent); // Stops the service.
                musicService = null;
            }

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

            // Removes the previous fragment and adds the new fragment.
            changeFragment(tracksFragment, "TRACKS", "ARTISTS", name, true);

            Log.d(LOG_TAG, "displayTracksFragment(): SSTracksFragment now being displayed.");

            currentArtist = name; // Sets the name of the current artist.
        }

        // Removes the SSTracksFragment in the view layout and replaces it with a SSArtistsFragment.
        else {

            // Adds a new SSArtistsFragment onto the fragment stack and is made visible in the view
            // layout.
            SSArtistsFragment artistFragment = new SSArtistsFragment();
            artistFragment.initializeFragment(name, true);

            // Removes the previous fragment and adds the new fragment.
            changeFragment(artistFragment, "ARTISTS", "TRACKS", name, false);

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

            // Removes the previous fragment and adds the new fragment.
            changeFragment(playerFragment, "PLAYER", "TRACKS", list.get(position).getSong(), true);

            Log.d(LOG_TAG, "displayPlayerFragment(): SSPlayerFragment now being displayed.");
        }

        // Removes the SSPlayerFragment in the view layout and replaces it with a SSTracksFragment.
        else {

            // Adds a new SSTracksFragment onto the fragment stack and is made visible in the view
            // layout.
            SSTracksFragment tracksFragment = new SSTracksFragment();
            tracksFragment.initializeFragment(list.get(position).getArtist(), true);

            // Removes the previous fragment and adds the new fragment.
            changeFragment(tracksFragment, "TRACKS", "PLAYER", list.get(position).getArtist(), false);

            Log.d(LOG_TAG, "displayPlayerFragment(): SSTracksFragment now being displayed.");
        }
    }

    // pauseTrack(): Invoked by SSPlayerFragment to signal the SSMusicService to pause the song
    // stream.
    @Override
    public void pauseTrack() {
        musicService.pauseTrack(); // Signals the SSMusicService to pause the song stream.
    }

    // playTrack(): Invoked by SSPlayerFragment to signal the SSMusicService to play the selected
    // stream.
    @Override
    public void playTrack(String url, Boolean loop) {
        musicService.playTrack(url, loop); // Signals the SSMusicService to play the song stream.
    }

    // updateArtistInput(): Invoked by SSArtistsFragment to keep an update of the user's artist
    // input.
    @Override
    public void updateArtistInput(String name) {
        currentInput = name; // Sets the current artist input name.
    }
}