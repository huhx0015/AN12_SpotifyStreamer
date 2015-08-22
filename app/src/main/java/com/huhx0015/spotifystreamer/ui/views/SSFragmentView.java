package com.huhx0015.spotifystreamer.ui.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.fragments.SSArtistsFragment;
import com.huhx0015.spotifystreamer.ui.actionbar.SSActionBar;
import java.lang.ref.WeakReference;

/** -----------------------------------------------------------------------------------------------
 *  [SSFragmentView] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSFragmentView class contains methods for adding and removing Fragment-related
 *  views, as well as animating fragment transitions.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSFragmentView {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSFragmentView.class.getSimpleName();

    /** FRAGMENT VIEW METHODS __________________________________________________________________ **/

    // addFragment(): Sets up the fragment view.
    public static void addFragment(Fragment fragment, ViewGroup container, int containerId,
                                   final String fragType, Boolean isAnimated,
                                   WeakReference<AppCompatActivity> refActivity) {

        if ((refActivity.get() != null) && (!refActivity.get().isFinishing())) {

            // Initializes the manager and transaction objects for the fragments.
            FragmentManager fragMan = refActivity.get().getSupportFragmentManager();
            FragmentTransaction fragTrans = fragMan.beginTransaction();
            fragTrans.replace(containerId, fragment, fragType);
            fragTrans.addToBackStack(fragType); // Adds fragment to the fragment stack.

            // Makes the changes to the fragment manager and transaction objects.
            fragTrans.commitAllowingStateLoss();

            // Sets up the transition animation.
            if (isAnimated) {
                setFragmentTransition(fragType, container, true, refActivity);
            }

            // Displays the fragment view without any transition animations.
            else {
                container.setVisibility(View.VISIBLE); // Displays the fragment.
            }
        }
    }

    // removeFragment(): This method is responsible for removing the fragment view.
    public static void removeFragment(ViewGroup container, final String fragType, Boolean isAnimated,
                                WeakReference<AppCompatActivity> refActivity) {

        if ((refActivity.get() != null) && (!refActivity.get().isFinishing())) {

            // Animates the fragment transition.
            if (isAnimated) {
                setFragmentTransition(fragType, container, false, refActivity);
            }

            // The fragment is removed from the view layout.
            else {

                // Initializes the manager and transaction objects for the fragments.
                FragmentManager fragMan = refActivity.get().getSupportFragmentManager();
                Fragment currentFragment = refActivity.get().getSupportFragmentManager().findFragmentByTag(fragType);
                fragMan.beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                fragMan.popBackStack(); // Pops the fragment from the stack.
                container.removeAllViews(); // Removes all views in the layout.
                container.setVisibility(View.INVISIBLE); // Hides the fragment.

                Log.d(LOG_TAG, "removeFragment(): Fragment has been removed.");
            }
        }
    }

    // reloadFragment(): Reloads the specified fragment into the specified container. This method is
    // typically called after a screen orientation change in the SSMainActivity activity class.
    public static Boolean reloadFragment(Fragment fragment, String curFragment, String fragType,
                                         ViewGroup container, int containerID, String currentArtist,
                                         String currentTrack, AppCompatActivity activity,
                                         WeakReference<AppCompatActivity> refActivity) {

        // SSArtistsFragment: If the fragment is null, it indicates that it is not on the fragment
        // stack. The fragment is initialized. This is needed to ensure that the SSArtistFragment
        // is shown when the application is first launched.
        if (fragType.equals("ARTISTS")) {

            if (fragment == null) {
                fragment = new SSArtistsFragment(); // Initializes the SSArtistsFragment class.
            }
        }

        // Checks to see if the playerFragment already exists in the layout. If not, the fragment is
        // added.
        if ( (fragment != null) && (curFragment.equals(fragType)) ) {

            if (!fragment.isInLayout()) {

                addFragment(fragment, container, containerID, fragType, false, refActivity);

                // SSTracksFragment: Sets up the action bar attributes.
                if (fragType.equals("TRACKS")) {
                    SSActionBar.setupActionBar(fragType, null, currentArtist, activity);
                }

                // SSArtistsFragment | SSPlayerFragment: Sets up the action bar attributes.
                else {
                    SSActionBar.setupActionBar(fragType, currentArtist, currentTrack, activity);
                }

                Log.d(LOG_TAG, "reloadFragment(): Reloading " + fragType + " fragment into the container.");

                return true; // Indicates that the fragment was found in the layout and was reloaded.
            }
        }

        return false; // Indicates that the fragment was not found and was not reloaded.
    }

    // setFragmentTransition(): Sets the fragment transition animation, based on the specified
    // fragment type.
    public static void setFragmentTransition(final String fragType,
                                              final ViewGroup container,
                                              final Boolean isAppearing,
                                              final WeakReference<AppCompatActivity> refActivity) {

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
        Animation fragmentAnimation = AnimationUtils.loadAnimation(refActivity.get().getBaseContext(), animationResource);

        // Sets the AnimationListener for the animation.
        fragmentAnimation.setAnimationListener(new Animation.AnimationListener() {

            // onAnimationStart(): Runs when the animation is started.
            @Override
            public void onAnimationStart(Animation animation) {

                // FRAGMENT APPEARANCE ANIMATION:
                if (isAppearing) {
                    container.setVisibility(View.VISIBLE); // Displays the fragment.
                }
            }

            // onAnimationEnd(): The fragment is removed after the animation ends.
            @Override
            public void onAnimationEnd(Animation animation) {

                Log.d(LOG_TAG, "setFragmentTransition(): Fragment animation has ended.");

                // FRAGMENT REMOVAL ANIMATION:
                if (!isAppearing) {

                    // Removes the fragment from the view.
                    removeFragment(container, fragType, false, refActivity);
                }
            }

            // onAnimationRepeat(): Runs when the animation is repeated.
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        container.startAnimation(fragmentAnimation); // Starts the animation.
    }
}