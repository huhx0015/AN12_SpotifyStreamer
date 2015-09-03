package com.huhx0015.spotifystreamer.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.huhx0015.spotifystreamer.R;

/** ------------------------------------------------------------------------------------------------
 *  [SSPreferences] CLASS
 *  PROGRAMMER: Michael Yoon Huh (HUHX0015)
 *  DESCRIPTION: This class contains functionality that pertains to the use and manipulation of
 *  shared preferences data.
 *  ------------------------------------------------------------------------------------------------
 */
public class SSPreferences {

    /** SHARED PREFERENCES FUNCTIONALITY _______________________________________________________ **/

    // getPreferenceResource(): Selects the appropriate resource based on the shared preference type.
    private static int getPreferenceResource() {

        // Main preferences resource file.
        return R.xml.ss_options;
    }

    // initializePreferences(): Initializes and returns the SharedPreferences object.
    public static SharedPreferences initializePreferences(String prefType, Context context) {
        return context.getSharedPreferences(prefType, Context.MODE_PRIVATE);
    }

    // setDefaultPreferences(): Sets the shared preference values to default values.
    public static void setDefaultPreferences(String prefType, Boolean isReset, Context context) {

        // Determines the appropriate resource file to use.
        int prefResource = getPreferenceResource();

        // Resets the preference values to default values.
        if (isReset) {
            SharedPreferences preferences = initializePreferences(prefType, context);
            preferences.edit().clear().apply();
        }

        // Sets the default values for the SharedPreferences object.
        PreferenceManager.setDefaultValues(context, prefType, Context.MODE_PRIVATE, prefResource, true);
    }

    /** GET PREFERENCES FUNCTIONALITY __________________________________________________________ **/

    // getAutoPlay(): Retrieves the "ss_auto_play" value from preferences.
    public static Boolean getAutoPlay(SharedPreferences preferences) {
        return preferences.getBoolean("ss_auto_play", false); // Retrieves the ss_auto_play value.
    }

    // getCountryCode(): Retrieves the current country code value from preferences.
    public static String getCountryCode(SharedPreferences preferences) {
        return preferences.getString("ss_country_code", "US"); // Retrieves the country code setting.
    }

    // getNotifications(): Retrieves the "ss_notifications" value from preferences.
    public static Boolean getNotifications(SharedPreferences preferences) {
        return preferences.getBoolean("ss_notifications", true); // Retrieves the ss_notifications value.
    }

    /** SET PREFERENCES FUNCTIONALITY __________________________________________________________ **/

    // setAutoPlay(): Sets the "ss_auto_play" value to preferences.
    public static void setAutoPlay(Boolean isAutoPlay, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putBoolean("ss_auto_play", isAutoPlay); // Sets the auto play setting.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }

    // setCountryCode(): Sets the "ss_country_code" value to preferences.
    public static void setCountryCode(String code, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putString("ss_country_code", code); // Sets the country code setting.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }

    // setNotifications(): Sets the "ss_notifications" value to preferences.
    public static void setNotifications(Boolean isNotificationsOn, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putBoolean("ss_notifications", isNotificationsOn); // Sets the notification setting.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }
}