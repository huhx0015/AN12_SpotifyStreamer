package com.huhx0015.spotifystreamer.fragments;

import android.preference.PreferenceFragment;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;

import com.huhx0015.spotifystreamer.R;

/** --------------------------------------------------------------------------------------------
 *  [SSSettingsFragment] CLASS
 *  DESCRIPTION: SSSettingsFragment is a subclass that references the PreferenceFragment type
 *  for loading preferences. This subclass supplements the GTN_Prefrences class and is only
 *  utilized if the Android API is 11 or above.
 *  --------------------------------------------------------------------------------------------
 */

public class SSSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    /** FRAGMENT LIFECYCLE FUNCTIONALITY ___________________________________________________ **/

    // onCreate(): The initial function that is called when the fragment is run. onCreate() only
    // runs when the fragment is first started.
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ss_options); // Loads the preferences from the "gtn_options.xml" file.
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

    /** FRAGMENT EXTENSION FUNCTIONALITY ___________________________________________________ **/

    // onSharedPreferenceChanged(): This function is called whenever the preference options are
    // changed. Based on the language selected, the language text will be updated to the proper
    // language format.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Assigns the references to the preference objects.
        PreferenceCategory prefCat = (PreferenceCategory) findPreference("gtn_options");
        ListPreference listPref = (ListPreference) findPreference("gtn_language");
        String lang = listPref.getValue();

        //updatePreferencesLanguage(prefCat, listPref, lang); // Updates all the text objects to the current selected language.
    }
}