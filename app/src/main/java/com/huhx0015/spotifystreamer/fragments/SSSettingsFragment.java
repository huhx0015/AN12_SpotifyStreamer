package com.huhx0015.spotifystreamer.fragments;

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.huhx0015.spotifystreamer.R;

/** ------------------------------------------------------------------------------------------------
 *  [SSSettingsFragment] CLASS
 *  DESCRIPTION: SSSettingsFragment is a fragment class that displays the SharedPreference settings
 *  for users to change on the fly.
 *  ------------------------------------------------------------------------------------------------
 */

public class SSSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    /** FRAGMENT LIFECYCLE METHODS _____________________________________________________________ **/

    // onCreate(): The initial function that is called when the fragment is run. onCreate() only
    // runs when the fragment is first started.
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ss_options); // Loads the preferences from the "ss_options.xml" file.
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

        // Updates the country code setting title.
        String currentCode = countryListPref.getValue();
        countryListPref.setTitle("Current Country Code: " + currentCode);

        // Updates the notification setting title.
        // ON:
        if (notificationsPref.isChecked()) {
            notificationsPref.setTitle("Display Notifications: ON");
        }

        // OFF:
        else {
            notificationsPref.setTitle("Display Notifications: OFF");
        }
    }
}