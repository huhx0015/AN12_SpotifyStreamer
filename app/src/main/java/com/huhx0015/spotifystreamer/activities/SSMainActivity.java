package com.huhx0015.spotifystreamer.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.huhx0015.spotifystreamer.R;
import butterknife.ButterKnife;

public class SSMainActivity extends AppCompatActivity {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSMainActivity.class.getSimpleName();

    /** ACTIVITY LIFECYCLE METHODS _____________________________________________________________ **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // LAYOUT SETUP:
        setupLayout();
    }

    /** ACTIVITY EXTENSION METHODS _____________________________________________________________ **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ss_main_activity_menu, menu);
        return true;
    }

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

    /** LAYOUT METHODS _________________________________________________________________________ **/

    // setupLayout(): Sets up the layout for the activity.
    private void setupLayout() {

        setContentView(R.layout.ss_main_activity); // Sets the XML layout file for the activity.
        ButterKnife.bind(this); // ButterKnife view injection initialization.

        setupActionBar(); // Sets up the action bar attributes.
    }

    // setupActionBar(): Sets up the action bar attributes for the activity.
    private void setupActionBar() {

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);
    }
}
