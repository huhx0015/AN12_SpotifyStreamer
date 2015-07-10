package com.huhx0015.spotifystreamer.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.fragments.SSArtistsFragment;
import com.huhx0015.spotifystreamer.fragments.SSPlayerFragment;
import com.huhx0015.spotifystreamer.fragments.SSTracksFragment;
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.huhx0015.spotifystreamer.ui.layouts.SSUnbind;
import java.lang.ref.WeakReference;
import butterknife.Bind;
import butterknife.ButterKnife;

/** -----------------------------------------------------------------------------------------------
 *  [SSMainActivity] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSMainActivity class is the primary Activity class that is loaded when the
 *  application is launched and is responsible for managing the fragment views for the application.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSMainActivity extends AppCompatActivity implements OnSpotifySelectedListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private static WeakReference<SSMainActivity> weakRefActivity = null; // Used to maintain a weak reference to the activity.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSMainActivity.class.getSimpleName();

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

        // LAYOUT SETUP:
        setupLayout();
    }

    // onDestroy(): This function runs when the activity has terminated and is being destroyed.
    // Calls recycleMemory() to free up memory allocation.
    @Override
    public void onDestroy() {
        recycleMemory(); // Recycles all View objects to free up memory resources.
        super.onDestroy();
    }

    /** ACTIVITY EXTENSION METHODS _____________________________________________________________ **/

    /* TODO: Action bar buttons disabled for P1.
    // onCreateOptionsMenu(): Inflates the menu when the menu key is pressed. This adds items to
    // the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ss_main_activity_menu, menu);
        return true;
    }

    // onOptionsItemSelected(): Defines the action to take when the menu options are selected. The
    // GTN_Preferences preference activity is launched when "Settings" is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    /** PHYSICAL BUTTON METHODS ________________________________________________________________ **/

    // BACK KEY:
    // onBackPressed(): Defines the action to take when the physical back button key is pressed.
    @Override
    public void onBackPressed() {
        finish(); // Finishes the activity.
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
        SSArtistsFragment artistsFragment = (SSArtistsFragment) fragManager.findFragmentByTag("ARTISTS");
        SSTracksFragment tracksFragment = (SSTracksFragment) fragManager.findFragmentByTag("TRACKS");

        // If the SSTracksFragment was in focus before the screen rotation event, the retained
        // SSTracksFragment is re-added instead.
        if (tracksFragment != null) {
            addFragment(tracksFragment, "TRACKS", false);
            setupActionBar("TRACKS"); // Sets up the action bar attributes.
        }

        // The SSArtistFragment is setup as the primary fragment in focus.
        else {

            // If the fragment is null, it indicates that it is not on the fragment stack. The fragment
            // is initialized.
            if (artistsFragment == null) {
                artistsFragment = new SSArtistsFragment(); // Initializes the SSArtistsFragment class.
            }

            // Sets up SSArtistsFragment for the initial view without a transition animation.
            addFragment(artistsFragment, "ARTISTS", false);
            setupActionBar("ARTISTS"); // Sets up the action bar attributes.
        }
    }

    // setupActionBar(): Sets up the action bar attributes for the activity.
    private void setupActionBar(String actionType) {

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);

        // TRACKS:
        if (actionType.equals("TRACKS")) {
            actionBar.setTitle("Top 10 Tracks"); // Sets the title of the action bar.
        }

        // PLAYER:
        else if (actionType.equals("PLAYER")) {
            actionBar.setTitle("Spotify Streamer Player"); // Sets the title of the action bar.
        }

        // DEFAULT:
        else {
            actionBar.setTitle(R.string.app_name); // Sets the title of the action bar.
        }
    }

    /** FRAGMENT METHODS _______________________________________________________________________ **/

    // addFragment(): Sets up the fragment view.
    private void addFragment(Fragment fragment, final String fragType, Boolean isAnimated) {

        if ((weakRefActivity.get() != null) && (!weakRefActivity.get().isFinishing())) {

            // Initializes the manager and transaction objects for the fragments.
            android.support.v4.app.FragmentManager fragMan = weakRefActivity.get().getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragTrans = fragMan.beginTransaction();
            fragTrans.replace(R.id.ss_main_activity_fragment_container, fragment, "ARTISTS");

            // Makes the changes to the fragment manager and transaction objects.
            fragTrans.addToBackStack(null);
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
        // TRACKS & PLAYER:
        if ( (fragType.equals("TRACKS")) || (fragType.equals("PLAYER")) ) {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {

                // TODO: Change to a slide right animation.
                animationResource = R.anim.bottom_up; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {

                // TODO: Change to a slide left animation.
                animationResource = R.anim.bottom_down; // Sets the animation XML resource file.
            }
        }

        // ARTISTS:
        else {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {
                animationResource = R.anim.slide_down; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {
                animationResource = R.anim.slide_up; // Sets the animation XML resource file.
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

    /** RECYCLE METHODS ________________________________________________________________________ **/

    // recycleMemory(): Recycles View objects to clear up resources prior to Activity destruction.
    private void recycleMemory() {

        // Unbinds all Drawable objects attached to the current layout.
        try { SSUnbind.unbindDrawables(findViewById(R.id.ss_main_activity_layout)); }
        catch (NullPointerException e) { e.printStackTrace(); } // Prints error message.
    }

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // displayTracksFragment(): Displays or removes the SSTracksFragment from the view layout.
    @Override
    public void displayTracksFragment(Boolean isShow, String name, String id) {

        // Displays the SSTracksFragment in the view layout.
        if (isShow) {

            // Removes the existing SSArtistsFragment from the fragment stack and the view layout.
            //removeFragment("ARTISTS", false);

            // Adds a new SSTracksFragment onto the fragment stack and is made visible in the view
            // layout.
            SSTracksFragment tracksFragment = new SSTracksFragment();
            tracksFragment.initializeFragment(name, id);

            // Adds the fragment with the transition animation.
            addFragment(tracksFragment, "TRACKS", true);

            setupActionBar("TRACKS"); // Sets the name of the action bar.
        }

        // Removes the SSTracksFragment in the view layout and replaces it with a SSTracksFragment.
        else {

            //removeFragment("TRACKS", false);

            // Adds a new SSArtistsFragment onto the fragment stack and is made visible in the view
            // layout.
            SSArtistsFragment artistFragment = new SSArtistsFragment();
            artistFragment.initializeFragment(name);

            // Adds the fragment with the transition animation.
            addFragment(artistFragment, "ARTISTS", true);

            setupActionBar("ARTISTS"); // Sets the name of the action bar.
        }
    }

    // displayPlayerFragment(): Displays or removes the SSPlayerFragment from the view layout.
    // TODO: Reserved for P2.
    @Override
    public void displayPlayerFragment(Boolean isShow, String artistName, String id, String songName,
                                      String albumName, String imageURL, String streamURL) {

        // Displays the SSPlayerFragment in the view layout.
        if (isShow) {

            // Removes the existing SSArtistsFragment from the fragment stack and the view layout.
            //removeFragment("PLAYER", false);

            // Adds a new SSPlayerFragment onto the fragment stack and is made visible in the view
            // layout.
            SSPlayerFragment playerFragment = new SSPlayerFragment();
            playerFragment.initializeFragment(artistName, id, songName, albumName, imageURL, streamURL);

            // Adds the fragment with the transition animation.
            addFragment(playerFragment, "PLAYER", true);

            setupActionBar("PLAYER"); // Sets the name of the action bar.
        }

        // Removes the SSTracksFragment in the view layout and replaces it with a SSTracksFragment.
        else {

            // Removes the existing SSArtistsFragment from the fragment stack and the view layout.
            //removeFragment("ARTISTS", false);

            // Adds a new SSTracksFragment onto the fragment stack and is made visible in the view
            // layout.
            SSTracksFragment tracksFragment = new SSTracksFragment();
            tracksFragment.initializeFragment(artistName, id);

            // Adds the fragment with the transition animation.
            addFragment(tracksFragment, "TRACKS", true);

            setupActionBar("TRACKS"); // Sets the name of the action bar.
        }
    }
}