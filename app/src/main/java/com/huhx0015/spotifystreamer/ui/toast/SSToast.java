package com.huhx0015.spotifystreamer.ui.toast;

import android.content.Context;
import android.widget.Toast;

/** -----------------------------------------------------------------------------------------------
 *  [SSToast] CLASS
 *  DESCRIPTION: SSToast class contains methods for displaying a Toast message.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSToast {

    /** TOAST FUNCTIONALITY ____________________________________________________________________ **/

    // toastyPopUp(): Creates and displays a Toast popup.
    public static void toastyPopUp(String message, Context con) {
        Toast.makeText(con, message, Toast.LENGTH_SHORT).show();
    }
}
