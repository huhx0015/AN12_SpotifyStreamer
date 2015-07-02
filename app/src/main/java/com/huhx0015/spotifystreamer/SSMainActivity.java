package com.huhx0015.spotifystreamer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class SSMainActivity extends AppCompatActivity {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSMainActivity.class.getSimpleName();

    /** ACTIVITY LIFECYCLE METHODS _____________________________________________________________ **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_main_activity);

        // SPOTIFY ASYNCTASK INITIALIZATION:
        SearchSpotifyTask task = new SearchSpotifyTask();
        task.execute(); // Executes the AsyncTask.
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

    /** ASYNCTASK METHODS ______________________________________________________________________ **/

    public class SearchSpotifyTask extends AsyncTask<Void, Void, Void>
    {

        // doInBackground(): AsyncTask method which runs in the background.
        @Override
        protected Void doInBackground(Void... strings) {

            // Initializes the Spotify API and background service.
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();

            // Accesses the background service to search for a specific artist.
            ArtistsPager results = service.searchArtists("Paul");

            List<Artist> artists = results.artists.items;

            // Retrieves the list of Artists found and sets it in the list.
            for (int i = 0; i < artists.size(); i++) {
                Artist artist = artists.get(i);
                Log.i(LOG_TAG, i + " " + artist.name);
            }

            return null;
        }
    }
}
