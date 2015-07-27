package com.huhx0015.spotifystreamer.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.audio.SSMusicEngine;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/** -----------------------------------------------------------------------------------------------
 *  [SSPlayerFragment] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSPlayerFragment is a fragment class that is responsible for displaying the music
 *  player in which a user can interact with to listen to streaming Spotify songs.
 *  TODO: Reserved for use in P2.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSPlayerFragment extends Fragment {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private Activity currentActivity; // Used to determine the activity class this fragment is currently attached to.

    // TODO: Access the top tracks data from currentActivity.getTrackData method.

    // AUDIO VARIABLES
    private SSMusicEngine ss_music; // SSMusicEngine class object that is used for music functionality.
    private String currentSong = "NONE"; // Sets the default song for the activity.
    private Boolean musicOn = true; // Used to determine if music has been enabled or not.
    private Boolean isPlaying = false; // Indicates that a song is currently playing in the background.

    // FRAGMENT VARIABLES
    private String artistName = ""; // Stores the name of the artist.
    private String artistId = ""; // Stores the Artist ID value.
    private String songName = ""; // Stores the name of the song.
    private String albumName = ""; // Stores the name of the album.
    private String albumImageURL = ""; // Stores the image URL of the album.
    private String streamURL = ""; // Stores the music stream URL of the song.

    // LIST VARIABLES
    private ArrayList<SSSpotifyModel> trackList = new ArrayList<>(); // References the track list.
    private int selectedPosition = 0; // References the selected position in the track list.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSPlayerFragment.class.getSimpleName();

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_play_button) ImageButton playButton;
    @Bind(R.id.ss_pause_button) ImageButton pauseButton;
    @Bind(R.id.ss_rewind_button) ImageButton rewindButton;
    @Bind(R.id.ss_forward_button) ImageButton forwardButton;
    @Bind(R.id.ss_next_button) ImageButton nextButton;
    @Bind(R.id.ss_previous_button) ImageButton previousButton;
    @Bind(R.id.ss_player_album_image) ImageView albumImage;
    @Bind(R.id.ss_player_song_name_text) TextView songNameText;
    @Bind(R.id.ss_player_artist_name_text) TextView artistNameText;

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
        this.artistId = "NaN";
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
        this.currentActivity = activity; // Sets the currentActivity to attached activity object.
    }

    // onCreate(): Runs when the fragment is first started.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true); // Retains this fragment during runtime changes.

        // AUDIO CLASS INITIALIZATION:
        ss_music.getInstance().initializeAudio();
    }

    // onResume(): This function runs immediately after onCreate() finishes and is always re-run
    // whenever the fragment is resumed from an onPause() state.
    @Override
    public void onResume() {
        super.onResume();

        // Checks to see if songs were playing in the background previously; this call resumes
        // the audio playback.
        resumeAudioState();
    }

    // onPause(): This function is called whenever the fragment is suspended.
    @Override
    public void onPause(){
        super.onPause();

        // Sets the isPlaying variable to determine if the song is currently playing.
        isPlaying = ss_music.getInstance().isSongPlaying();
        ss_music.getInstance().pauseSong(); // Pauses any song that is playing in the background.
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

    // onDestroyView(): This function runs when the screen is no longer visible and the view is
    // destroyed.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this); // Sets all injected views to null.
    }

    // onDestroy(): This function runs when the fragment has terminated and is being destroyed.
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Releases all audio-related instances if the application is terminating.
        ss_music.getInstance().releaseMedia();
    }

    /** LAYOUT METHODS _________________________________________________________________________ **/

    // setUpLayout(): Sets up the layout for the fragment.
    private void setUpLayout() {
        setUpButtons(); // Sets up the button listeners for the fragment.
        setUpImage(); // Sets up the images for the ImageView objects for the fragment.
        setUpText(); // Sets up the text for the TextView objects for the fragment.
    }

    // setUpButtons(): Sets up the button listeners for the fragment.
    private void setUpButtons() {

        // PLAYER BUTTONS:
        // -----------------------------------------------------------------------------------------

        // PLAY: Sets up the listener and the actions for the PLAY button.
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // If no song is currently playing, the song from the stream URL is loaded.
                if (currentSong.equals("NONE")) {

                    // Plays the song immediately if music is enabled.
                    if (musicOn) {
                        currentSong = streamURL; // Sets the stream URL.
                        ss_music.getInstance().playSongUrl(currentSong, true);

                        Log.d(LOG_TAG, "Now playing: " + currentSong);
                    }
                }

                // Plays the last selected song.
                else {
                    ss_music.getInstance().playSongUrl(currentSong, true);
                }
            }
        });

        // PAUSE: Sets up the listener and the actions for the PAUSE button.
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Pauses the song that is currently playing in the background.
                if (ss_music.getInstance().isSongPlaying()) {
                    ss_music.getInstance().pauseSong();
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

        // Loads the image from the image URL into the albumImage ImageView object.
        Picasso.with(currentActivity)
                .load(albumImageURL)
                .into(albumImage);
    }

    // setUpText(): Sets up the texts for the TextView objects in the fragment.
    private void setUpText() {
        artistNameText.setText(artistName); // Sets the artist name for the TextView object.
        songNameText.setText(songName); // Sets the song name for the TextView object.
    }

    /** AUDIO FUNCTIONALITY ____________________________________________________________________ **/

    // resumeAudioState(): If music was playing in the background prior to the fragment from being
    // paused, the song is resumed.
    private void resumeAudioState() {

        // Checks to see if the song was playing prior to the activity from being
        if (isPlaying) {
            ss_music.getInstance().playSongUrl(currentSong, true);
        }
    }
}