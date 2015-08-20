package com.huhx0015.spotifystreamer.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.activities.SSMainActivity;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.interfaces.OnMusicPlayerListener;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.ui.graphics.SSImages;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

/** -----------------------------------------------------------------------------------------------
 *  [SSPlayerFragment] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSPlayerFragment is a dialog fragment class that is responsible for displaying the music
 *  player in which a user can interact with to listen to streaming Spotify songs.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSPlayerFragment extends DialogFragment implements OnMusicPlayerListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private SSMainActivity currentActivity; // Used to determine the activity class this fragment is currently attached to.

    // AUDIO VARIABLES
    private String currentSong = "NONE"; // Sets the default song for the activity.
    private Boolean musicOn = true; // Used to determine if music has been enabled or not.
    private Boolean isPlaying = false; // Indicates that a song is currently playing in the background.
    private Boolean isLoop = true; // Indicates the song will be looped infinitely.

    // DATA VARIABLES
    private static final String PLAYER_STATE = "playerState"; // Parcelable key value for the SSMusicEngine state.

    // FRAGMENT VARIABLES
    private Boolean isRotationEvent = false; // Used to determine if a rotation event is going on.
    private String artistName = ""; // Stores the name of the artist.
    private String songId = ""; // Stores the song ID value.
    private String songName = ""; // Stores the name of the song.
    private String albumName = ""; // Stores the name of the album.
    private String albumImageURL = ""; // Stores the image URL of the album.
    private String streamURL = ""; // Stores the music stream URL of the song.

    // LAYOUT VARIABLES
    private Boolean isDestroyed = false; // Used to determine if the fragment is being destroyed or not.
    private float curDensity; // References the density value of the current device.

    // LIST VARIABLES
    private ArrayList<SSSpotifyModel> trackList = new ArrayList<>(); // References the track list.
    private int selectedPosition = 0; // References the selected position in the track list.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSPlayerFragment.class.getSimpleName();

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_play_pause_button) ImageButton playPauseButton;
    @Bind(R.id.ss_rewind_button) ImageButton rewindButton;
    @Bind(R.id.ss_forward_button) ImageButton forwardButton;
    @Bind(R.id.ss_next_button) ImageButton nextButton;
    @Bind(R.id.ss_previous_button) ImageButton previousButton;
    @Bind(R.id.ss_player_album_image) ImageView albumImage;
    @Bind(R.id.ss_player_seekbar) SeekBar playerBar;
    @Bind(R.id.ss_player_song_name_text) TextView songNameText;
    @Bind(R.id.ss_player_artist_album_name_text) TextView artistAlbumNameText;

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // SSPlayerFragment(): Default constructor for the SSPlayerFragment fragment class.
    private final static SSPlayerFragment player_fragment = new SSPlayerFragment();

    // SSPlayerFragment(): Deconstructor method for the SSPlayerFragment fragment class.
    public SSPlayerFragment() {}

    // getInstance(): Returns the player_fragment instance.
    public static SSPlayerFragment getInstance() { return player_fragment; }

    // initializeFragment(): Sets the initial values for the fragment.
    public void initializeFragment(ArrayList<SSSpotifyModel> list, int position) {
        this.trackList = list;
        this.selectedPosition = position;
        this.artistName = list.get(position).getArtist();
        this.songId = list.get(position).getSongId();
        this.songName = list.get(position).getSong();
        this.albumName = list.get(position).getAlbum();
        this.albumImageURL = list.get(position).getAlbumImage();
        this.streamURL = list.get(position).getSongURL();
    }
    
    /** FRAGMENT LIFECYCLE METHODS _____________________________________________________________ **/

    // onAttach(): The initial function that is called when the Fragment is run. The activity is
    // attached to the fragment.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.currentActivity = (SSMainActivity) activity; // Sets the currentActivity to attached activity object.
    }

    // onCreate(): Runs when the fragment is first started.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Retains this fragment during runtime changes.
    }

    // onResume(): This function runs immediately after onCreate() finishes and is always re-run
    // whenever the fragment is resumed from an onPause() state.
    @Override
    public void onResume() {
        super.onResume();

        // Indicates that a rotation change event is no longer occurring.
        if (isRotationEvent) {

            // TODO: Re-set up the layout.
            isRotationEvent = false;
        }

        Log.d(LOG_TAG, "onResume(): Fragment resumed.");
    }

    // onCreateView(): Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View ss_fragment_view = (ViewGroup) inflater.inflate(R.layout.ss_player_fragment, container, false);
        ButterKnife.bind(this, ss_fragment_view); // ButterKnife view injection initialization.

        setUpLayout(); // Sets up the layout for the fragment.

        return ss_fragment_view;
    }

    // onPause(): This function is called whenever the fragment is suspended.
    @Override
    public void onPause(){
        super.onPause();

        // Sets the isPlaying variable to determine if the song is currently playing.
        //isPlaying = ss_music.getInstance().isSongPlaying();

        /*
        // Pauses any song that is playing in the background.
        if (!isRotationEvent) {
            ss_music.getInstance().pauseSong();
        }
        */

        Log.d(LOG_TAG, "onPause(): Fragment paused.");
    }

    // onDestroyView(): This function runs when the screen is no longer visible and the view is
    // destroyed.
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        isDestroyed = true; // Indicates that the fragment is in the process of being destroyed.
        ButterKnife.unbind(this); // Sets all injected views to null.

        Log.d(LOG_TAG, "onDestroyView(): Fragment view destroyed.");
    }

    // onDestroy(): This function runs when the fragment has terminated and is being destroyed.
    @Override
    public void onDestroy() {
        super.onDestroy();

        pauseTrack(); // Pauses the track, if currently playing in the background.

        // Resets the current track value and the isPlaying value in SSMainActivity is set to false.
        currentActivity.setCurrentTrack(null, false);

        Log.d(LOG_TAG, "onDestroy(): Fragment destroyed.");
    }

    // onDetach(): This function is called immediately prior to the fragment no longer being
    // associated with its activity.
    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(LOG_TAG, "onDetach(): Fragment detached.");
    }

    /** FRAGMENT EXTENSION METHOD ______________________________________________________________ **/

    // onSaveInstanceState(): Called to retrieve per-instance state from an fragment before being
    // killed so that the state can be restored in onCreate() or onRestoreInstanceState().
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        isRotationEvent = true; // Indicates that the rotation event has occurred.

        Log.d(LOG_TAG, "onSaveInstanceState(): The Parcelable data has been saved.");

        super.onSaveInstanceState(savedInstanceState);
    }

    /** LAYOUT METHODS _________________________________________________________________________ **/

    // setUpLayout(): Sets up the layout for the fragment.
    private void setUpLayout() {

        // Retrieves the device's density attributes.
        curDensity = getResources().getDisplayMetrics().density;

        // Sets the current track name for the SSMainActivity activity.
        currentActivity.setCurrentTrack(songName, true);

        setUpButtons(); // Sets up the button listeners for the fragment.
        setUpImage(); // Sets up the images for the ImageView objects for the fragment.
        setUpText(); // Sets up the text for the TextView objects for the fragment.
    }

    // setUpButtons(): Sets up the button listeners for the fragment.
    private void setUpButtons() {

        // PLAYER BUTTONS:
        // -----------------------------------------------------------------------------------------

        // PLAY / PAUSE: Sets up the listener and the actions for the PLAY / PAUSE button.
        playPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // PAUSE: Pauses the song if the song is currently playing.
                if (isPlaying) {

                    // Signals the activity to signal the SSMusicService to pause the song.
                    pauseTrack();
                }

                // PLAY: Plays the song if no song is currently playing in the background.
                else {

                    // Signals the activity to signal the SSMusicService to begin streaming playback of
                    // the current track.
                    playTrack(streamURL, isLoop);
                    currentSong = streamURL; // Sets the current song playing in the background.
                }
            }
        });

        // REWIND: Sets up the listener and the actions for the REWIND button.
        rewindButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: Rewind action here.
            }
        });
    }

    // setUpImage(): Sets up the images for the ImageView objects in the fragment.
    private void setUpImage() {

        // ALBUM COVER: Loads the image from the image URL into the albumImage ImageView object.
        Picasso.with(currentActivity)
                .load(albumImageURL)
                .into(albumImage);

        // PREVIOUS BUTTON: Sets a circular icon into the ImageButton object.
        SSImages.setCircularImage(android.R.drawable.ic_media_previous, previousButton,
                (int) (48 * curDensity), (int) (48 * curDensity), currentActivity);

        // REWIND BUTTON: Sets a circular icon into the ImageButton object.
        SSImages.setCircularImage(android.R.drawable.ic_media_rew, rewindButton,
                (int) (56 * curDensity), (int) (56 * curDensity), currentActivity);

        // PLAY/PAUSE BUTTON: Sets a circular icon into the ImageButton object.
        SSImages.setCircularImage(android.R.drawable.ic_media_play, playPauseButton,
                (int) (64 * curDensity), (int) (64 * curDensity), currentActivity);

        // FORWARD BUTTON: Sets a circular icon into the ImageButton object.
        SSImages.setCircularImage(android.R.drawable.ic_media_ff, forwardButton,
                (int) (56 * curDensity), (int) (56 * curDensity), currentActivity);

        // NEXT BUTTON: Sets a circular icon into the ImageButton object.
        SSImages.setCircularImage(android.R.drawable.ic_media_next, nextButton,
                (int) (48 * curDensity), (int) (48 * curDensity), currentActivity);
    }

    // setUpText(): Sets up the texts for the TextView objects in the fragment.
    private void setUpText() {
        artistAlbumNameText.setText(artistName + " - " + albumName); // Sets the artist and album name for the TextView object.
        songNameText.setText(songName); // Sets the song name for the TextView object.
    }

    // updateControlButtons(): Updates the graphics of the playback control buttons.
    private void updateControlButtons(Boolean isPlay) {

        // PLAYING:
        if (isPlay) {
            SSImages.setCircularImage(android.R.drawable.ic_media_pause, playPauseButton,
                    (int) (64 * curDensity), (int) (64 * curDensity), currentActivity);
        }

        // STOP / PAUSED:
        else {
            SSImages.setCircularImage(android.R.drawable.ic_media_play, playPauseButton,
                    (int) (64 * curDensity), (int) (64 * curDensity), currentActivity);
        }
    }

    /** INTERFACE METHODS __________________________________________________________________ **/

    // playbackStatus(): An interface method invoked by the SSMusicEngine on the current playback
    // status of the song.
    @Override
    public void playbackStatus(Boolean isPlay) {

        if (!isDestroyed) {
            isPlaying = isPlay; // Updates the current playback status of the streaming song.
            updateControlButtons(isPlaying); // Updates the player control buttons.
            Log.d(LOG_TAG, "playbackStatus(): Current playback status: " + isPlaying);
        }
    }

    // seekbarStatus(): An interface method invoked by the SSMusicEngine to update the player
    // seekbar position.
    @Override
    public void seekbarStatus(int position) {

        if (!isDestroyed) {
            playerBar.setProgress(position); // Sets the current position for the player seekbar.
            Log.d(LOG_TAG, "seekbarStatus(): Setting the seekbar position: " + position);
        }
    }

    // setDuration(): An interface method invoked by the SSMusicEngine to set the player seekbar
    // max duration.
    @Override
    public void setDuration(int duration) {

        if (!isDestroyed) {
            playerBar.setMax(duration); // Sets the maximum duration of the player seekbar.
            Log.d(LOG_TAG, "setDuration(): Sets the maximum duration of the player seekbar.");
        }
    }

    // pauseTrack(): Signals the attached activity to invoke the SSMusicService to pause playback
    // of the streamed Spotify track.
    private void pauseTrack() {
        try { ((OnMusicServiceListener) currentActivity).pauseTrack(false); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // playTrack(): Signals the attached activity to invoke the SSMusicService to begin playback
    // of the streamed Spotify track.
    private void playTrack(String url, Boolean loop) {
        try { ((OnMusicServiceListener) currentActivity).playTrack(url, loop); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }
}