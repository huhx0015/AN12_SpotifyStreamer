package com.huhx0015.spotifystreamer.intent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by Michael Yoon Huh on 9/4/2015.
 */
public class SSWebIntent {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // FLAG VARIABLES
    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";

    /** WEB INTENT METHODS _____________________________________________________________________ **/

    /*
    public static void launchWebIntent(String url, Context context) {

        // Using a VIEW intent for compatibility with any other browsers on device.
        // Caller should not be setting FLAG_ACTIVITY_NEW_TASK or
        // FLAG_ACTIVITY_NEW_DOCUMENT.
        url = "https://paul.kinlan.me/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        //  Must have. Extra used to match the session. Its value is an IBinder passed
        //  whilst creating a news session. See newSession() below. Even if the service is not
        //  used and there is no valid session id to be provided, this extra has to be present
        //  with a null value to launch a custom tab.

        Bundle extras = new Bundle;
        extras.putBinder(EXTRA_CUSTOM_TABS_SESSION,
                sessionICustomTabsCallback.asBinder());
        intent.putExtras(extras);

    }
*/
}
