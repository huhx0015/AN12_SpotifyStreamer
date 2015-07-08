package com.huhx0015.spotifystreamer.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.fragments.SSResultsFragment;
import com.huhx0015.spotifystreamer.ui.layouts.SSUnbind;
import java.lang.ref.WeakReference;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SSMainActivity extends AppCompatActivity {

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

        //setupActionBar(); // Sets up the action bar attributes.

        setupFragment(); // Initializes the fragment view for the layout.
    }

    // setupFragment(): Initializes the fragment view for the layout.
    private void setupFragment() {

        // Checks to see if there are any retained fragments when the activity is re-created from a
        // screen rotation event.
        FragmentManager fragManager = getSupportFragmentManager();
        SSResultsFragment resultsFragment = (SSResultsFragment) fragManager.findFragmentByTag("RESULTS");

        // If the fragment is null, it indicates that it is not on the fragment stack. The fragment
        // is initialized.
        if (resultsFragment == null) {
            resultsFragment = new SSResultsFragment(); // Initializes the SSResultsFragment class.
        }

        // Sets up SSResultsFragment for the initial view without a transition animation.
        addFragment(resultsFragment, "RESULTS", false);
    }

    /* TODO: Disabled for P1.
    // setupActionBar(): Sets up the action bar attributes for the activity.
    private void setupActionBar() {

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);
    }
    */

    /** FRAGMENT METHODS _______________________________________________________________________ **/

    // addFragment(): Sets up the fragment view.
    private void addFragment(Fragment fragment, final String fragType, Boolean isAnimated) {

        if ((weakRefActivity.get() != null) && (!weakRefActivity.get().isFinishing())) {

            // Initializes the manager and transaction objects for the fragments.
            android.support.v4.app.FragmentManager fragMan = weakRefActivity.get().getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragTrans = fragMan.beginTransaction();
            fragTrans.replace(R.id.ss_main_activity_fragment_container, fragment, "RESULTS");

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
    private void removeFragment(final String fragType) {

        if ((weakRefActivity.get() != null) && (!weakRefActivity.get().isFinishing())) {
            setFragmentTransition(fragType, false); // Sets the fragment transition animation.
        }
    }

    // setFragmentTransition(): Sets the fragment transition animation, based on the specified
    // fragment type.
    private void setFragmentTransition(String fragType, final Boolean isAppearing) {

        int animationResource; // References the animation XML resource file.

        // Sets the animation XML resource file, based on the fragment type.
        // RESULTS:
        if (fragType.equals("RESULTS")) {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {
                animationResource = R.anim.bottom_up; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {
                animationResource = R.anim.bottom_down; // Sets the animation XML resource file.
            }
        }

        // MISCELLANEOUS:
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

        Animation fragmentAnimation = AnimationUtils.loadAnimation(this, animationResource);

        // Sets the AnimationListener for the animation.
        fragmentAnimation.setAnimationListener(new Animation.AnimationListener() {

            // onAnimationStart(): Runs when the animation is started.
            @Override
            public void onAnimationStart(Animation animation) {

                if (isAppearing) {
                    fragmentContainer.setVisibility(View.VISIBLE); // Displays the fragment.
                }
            }

            // onAnimationEnd(): The fragment is removed after the animation ends.
            @Override
            public void onAnimationEnd(Animation animation) {

                Log.d(LOG_TAG, "setUpFragment(): Fragment animation has ended.");

                if (!isAppearing) {

                    // Initializes the manager and transaction objects for the fragments.
                    FragmentManager fragMan = getSupportFragmentManager();
                    fragMan.popBackStack(); // Pops the fragment from the stack.
                    fragmentContainer.removeAllViews(); // Removes all views in the layout.
                    fragmentContainer.setVisibility(View.INVISIBLE); // Hides the fragment.

                    Log.d(LOG_TAG, "removeFragment(): Fragment has been removed.");
                }
            }

            // onAnimationRepeat(): Runs when the animation is repeated.
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        fragmentContainer.startAnimation(fragmentAnimation); // Starts the animation.
    }

    /** RECYCLE FUNCTIONALITY __________________________________________________________________ **/

    // recycleMemory(): Recycles View objects to clear up resources prior to Activity destruction.
    private void recycleMemory() {

        // Unbinds all Drawable objects attached to the current layout.
        try { SSUnbind.unbindDrawables(findViewById(R.id.ss_main_activity_layout)); }
        catch (NullPointerException e) { e.printStackTrace(); } // Prints error message.
    }
}