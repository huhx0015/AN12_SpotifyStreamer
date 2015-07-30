package com.huhx0015.spotifystreamer.ui.layouts;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

/** -----------------------------------------------------------------------------------------------
 *  [SSUnbind] CLASS
 *  DESCRIPTION: SSUnbind class is a class that contains methods which unbind View groups that are
 *  no longer needed by activities. This is done to avoid possible memory leaks produced by
 *  Activity classes. This code is adapted from Roman Guy at:
 *  http://www.curious-creature.com/2008/12/18/avoid-memory-leaks-on-android/.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSUnbind {

    /** RECYCLE FUNCTIONALITY __________________________________________________________________ **/

    // unbindDrawables(): Unbinds all Drawable objects attached to the view layout by setting them
    // to null, freeing up memory resources and preventing Context-related memory leaks.
    public static void unbindDrawables(View view) {

        // If the View object's background is not null, a Callback is set to render them null.
        if (view.getBackground() != null) { view.getBackground().setCallback(null); }

        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }

            ((ViewGroup) view).removeAllViews(); // Removes all View objects in the ViewGroup.
        }
    }
}
