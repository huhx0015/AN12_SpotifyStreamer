package com.huhx0015.spotifystreamer.fragments;

import android.app.Activity;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.activities.SSMainActivity;
import com.huhx0015.spotifystreamer.preferences.SSPreferences;
import com.huhx0015.spotifystreamer.ui.notifications.SSNotificationPlayer;

/** ------------------------------------------------------------------------------------------------
 *  [SSSettingsFragment] CLASS
 *  DESCRIPTION: SSSettingsFragment is a fragment class that displays the SharedPreference settings
 *  for users to change on the fly.
 *  ------------------------------------------------------------------------------------------------
 */

public class SSSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private SSMainActivity currentActivity; // Used to determine the activity class this fragment is currently attached to.

    // SHARED PREFERENCE VARIABLES
    private SharedPreferences SS_prefs; // References the application's SharedPreferences object.
    private static final String SS_OPTIONS = "ss_options"; // Used to reference the name of the preference XML file.

    /** FRAGMENT LIFECYCLE METHODS _____________________________________________________________ **/

    // onAttach(): The initial function that is called when the Fragment is run. The activity is
    // attached to the fragment.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.currentActivity = (SSMainActivity) activity; // Sets the currentActivity to attached activity object.
    }

    // onCreate(): The initial function that is called when the fragment is run. onCreate() only
    // runs when the fragment is first started.
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ss_options); // Loads the preferences from the "ss_options.xml" file.

        // Initializes the SharedPreferences object.
        SS_prefs = SSPreferences.initializePreferences(SS_OPTIONS, currentActivity);

        onSharedPreferenceChanged(null, ""); // Initializes the call to on onSharedPreferenceChanged function.
    }

    // onResume(): This function runs immediately after onCreate() finishes and is always re-run
    // whenever the fragment is resumed from an onPause() state.
    @Override
    public void onResume() {
        super.onResume();

        // Registers a listener for the preferences when the fragment is resumed.
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    // onPause(): This function is called whenever the fragment is suspended.
    @Override
    public void onPause() {
        super.onPause();

        // Un-registers the listener for the preferences when the fragment is suspended.
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /** FRAGMENT EXTENSION METHODS _____________________________________________________________ **/

    // onSharedPreferenceChanged(): This function is called whenever the preference options are
    // changed.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Assigns the references to the preference objects.
        ListPreference countryListPref = (ListPreference) findPreference("ss_country_code");
        CheckBoxPreference notificationsPref = (CheckBoxPreference) findPreference("ss_notifications");

        // COUNTRY CODE: Updates the country code settings.
        String currentCode = countryListPref.getValue();
        countryListPref.setTitle("Current Country Code: " + currentCode);
        SSPreferences.setCountryCode(currentCode, SS_prefs); // Sets the new value in SharedPreferences.

        // NOTIFICATION PLAYER: Updates the notification settings.
        // ON:
        if (notificationsPref.isChecked()) {
            notificationsPref.setTitle("Display Notification Player: ON");
            SSPreferences.setNotifications(true, SS_prefs); // Sets the new value in SharedPreferences.
        }

        // OFF:
        else {
            notificationsPref.setTitle("Display Notification Player: OFF");
            SSPreferences.setNotifications(false, SS_prefs); // Sets the new value in SharedPreferences.
            SSNotificationPlayer.removeNotifications(currentActivity); // Removes all active notifications.
        }
    }
}