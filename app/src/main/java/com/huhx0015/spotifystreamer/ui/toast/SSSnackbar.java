package com.huhx0015.spotifystreamer.ui.toast;

import android.support.design.widget.Snackbar;
import android.view.View;

/** -----------------------------------------------------------------------------------------------
 *  [SSSnackbar] CLASS
 *  DESCRIPTION: SSSnackbar class contains methods for displaying a Snackbar message.
 *  -----------------------------------------------------------------------------------------------
 */
public class SSSnackbar {

    /** SNACKBAR METHODS _______________________________________________________________________ **/

    // snackOnThis(): Displays a Snackbar popup in the referenced container.
    public static void snackOnThis(String message, View container) {
        Snackbar
                .make(container, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
