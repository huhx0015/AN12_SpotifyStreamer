package com.huhx0015.spotifystreamer.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.activities.SSMainActivity;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.interfaces.OnMusicPlayerListener;
import com.huhx0015.spotifystreamer.interfaces.OnMusicServiceListener;
import com.huhx0015.spotifystreamer.interfaces.OnSnackbarDisplayListener;
import com.huhx0015.spotifystreamer.interfaces.OnTrackInfoUpdateListener;
import com.huhx0015.spotifystreamer.preferences.SSPreferences;
import com.huhx0015.spotifystreamer.ui.graphics.SSBlurBuilder;
import com.huhx0015.spotifystreamer.ui.notifications.SSNotificationPlayer;
import com.huhx0015.spotifystreamer.ui.toast.SSSnackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
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
    private Boolean isPaused = false; // Indicates that a song is currently paused.
    private Boolean isPlaying = false; // Indicates that a song is currently playing in the background.
    private Boolean isPreparing = false; // Used to determine if a song is currently being prepared for playback.

    // BITMAP VARIABLES
    private Bitmap albumBitmap; // Stores the Bitmap for the album image.

    // FRAGMENT VARIABLES
    private String artistName = ""; // Stores the name of the artist.
    private String songId = ""; // Stores the song ID value.
    private String songName = ""; // Stores the name of the song.
    private String albumName = ""; // Stores the name of the album.
    private String albumImageURL = ""; // Stores the image URL of the album.
    private String streamURL = ""; // Stores the music stream URL of the song.

    // LAYOUT VARIABLES
    private Boolean isDestroyed = false; // Used to determine if the fragment is being destroyed or not.
    private Boolean isTablet = false; // Used to determine if the current device is a mobile or tablet device.
    private float curDensity; // References the density value of the current device.

    // LIST VARIABLES
    private ArrayList<SSSpotifyModel> trackList = new ArrayList<>(); // References the track list.
    private int selectedPosition = 0; // References the selected position in the track list.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSPlayerFragment.class.getSimpleName();

    // SYSTEM VARIABLES
    private final int api_level = android.os.Build.VERSION.SDK_INT; // Used to determine the device's Android API version.

    // SHARED PREFERENCE VARIABLES
    private SharedPreferences SS_prefs; // SharedPreferences object for the application.
    private static final String SS_OPTIONS = "ss_options"; // Used to reference the name of the preference XML file.
    private Boolean autoPlayOn = false; // Used to determine if auto play is enabled or not.
    private Boolean isLoop = false; // Used to determine if the song will be looped infinitely.
    private Boolean notificationsOn = true; // Used to determine if notification display is enabled or not.

    // VIEW INJECTION VARIABLES
    @Bind(R.id.ss_player_fragment_progress_layer) FrameLayout progressLayer;
    @Bind(R.id.ss_player_fragment_parent_container) LinearLayout playerContainer;
    @Bind(R.id.ss_player_album_image) ImageView albumImage;
    @Bind(R.id.ss_autoplay_button) ImageButton autoPlayButton;
    @Bind(R.id.ss_forward_button) ImageButton forwardButton;
    @Bind(R.id.ss_next_button) ImageButton nextButton;
    @Bind(R.id.ss_play_pause_button) ImageButton playPauseButton;
    @Bind(R.id.ss_previous_button) ImageButton previousButton;
    @Bind(R.id.ss_repeat_button) ImageButton repeatButton;
    @Bind(R.id.ss_rewind_button) ImageButton rewindButton;
    @Bind(R.id.ss_player_seekbar) SeekBar playerBar;
    @Bind(R.id.ss_player_artist_album_name_text) TextView artistAlbumNameText;
    @Bind(R.id.ss_player_seekbar_min_duration_text) TextView minDurationText;
    @Bind(R.id.ss_player_seekbar_max_duration_text) TextView maxDurationText;
    @Bind(R.id.ss_player_song_name_text) TextView songNameText;

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

        Log.d(LOG_TAG, "FRAGMENT LIFECYCLE (onAttach): onAttach() invoked.");

        this.currentActivity = (SSMainActivity) activity; // Sets the currentActivity to attached activity object.
        attachPlayerFragment(); // Attaches this fragment to the music service.
    }

    // onCreate(): Runs when the fragment is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "FRAGMENT LIFECYCLE (onCreate): onCreate() invoked.");

        // Updates the isTablet value to determine if the device is a mobile or tablet device.
        isTablet = getResources().getBoolean(R.bool.isTablet);

        setRetainInstance(true); // Retains this fragment during runtime changes.
    }

    // onStart(): This method runs immediately after the onCreate() method.
    @Override
    public void onStart() {
        super.onStart();

        // TABLET: Disables the dark transparent overlay if this fragment is displayed as a
        // DialogFragment.
        if (isTablet) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(windowParams);
        }
    }

    // onCreateView(): Creates and returns the view hierarchy associated with the fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "FRAGMENT LIFECYCLE (onCreateView): onCreateView() invoked.");

        View ss_fragment_view = (ViewGroup) inflater.inflate(R.layout.ss_player_fragment, container, false);
        ButterKnife.bind(this, ss_fragment_view); // ButterKnife view injection initialization.

        loadPreferences(); // Loads values from SharedPreferences.
        setUpLayout(); // Sets up the layout for the fragment.

        return ss_fragment_view;
    }

    // onDestroyView(): This function runs when the screen is no longer visible and the view is
    // destroyed.
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(LOG_TAG, "FRAGMENT LIFECYCLE (onDestroyView): onDestroyView() invoked.");

        isDestroyed = true; // Indicates that the fragment is in the process of being destroyed.
        ButterKnife.unbind(this); // Sets all injected views to null.
    }

    /** LAYOUT METHODS _________________________________________________________________________ **/

    // setUpLayout(): Sets up the layout for the fragment.
    private void setUpLayout() {

        // Retrieves the device's density attributes.
        curDensity = getResources().getDisplayMetrics().density;

        setUpButtons(); // Sets up the button listeners for the fragment.
        setUpSeekbar(); // Sets up the seekbar listener for the player bar.
        setUpImage(); // Sets up the images for the ImageView objects for the fragment.
        setUpText(); // Sets up the text for the TextView objects for the fragment.

        // Retrieves the current song status and max duration of the song from
        // SSApplication/SSMusicService/SSMusicEngine.
        updatePlayer();

        // Sets the current track information for the SSMainActivity activity.
        updateCurrentTrack(songName, streamURL, selectedPosition);
    }

    // setUpButtons(): Sets up the button listeners for the fragment.
    private void setUpButtons() {

        // PLAYER BUTTONS:
        // -----------------------------------------------------------------------------------------

        // AUTO PLAY: Sets up the listener and the actions for the AUTO PLAY button.
        autoPlayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isPreparing) {

                    // Toggles on/off the auto play feature.
                    if (autoPlayOn) {
                        autoPlayToggle(false, true); // Enables the auto play feature.
                    }

                    else {
                        autoPlayToggle(true, true); // Disables the auto play feature.
                    }
                }
            }
        });

        // FORWARD: Sets up the listener and the actions for the FORWARD button.
        forwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Retrieves the current seekbar progress and sets the new seekbar position value.
                if (!isPreparing) {
                    int newPosition = playerBar.getProgress() + 6;
                    playerBar.setProgress(newPosition); // Sets the new seekbar position.
                    setPosition(newPosition); // Sets the new position of the song.
                }
            }
        });

        // NEXT: Sets up the listener and the actions for the NEXT button.
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Sets the song to the next track in the list.
                if (!isPreparing) {
                    playNextSong(true, false);
                }
            }
        });

        // PLAY / PAUSE: Sets up the listener and the actions for the PLAY / PAUSE button.
        playPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isPreparing) {

                    // PAUSE: Pauses the song if the song is currently playing.
                    if (isPlaying) {

                        isPaused = true;

                        // Removes any active notification player.
                        SSNotificationPlayer.removeNotifications(currentActivity);

                        // Signals the activity to signal the SSMusicService to pause the song.
                        pauseTrack(false);
                    }

                    // PLAY: Plays the song if no song is currently playing in the background.
                    else {
                        initializeSongPlay();
                    }
                }
            }
        });

        // PREVIOUS: Sets up the listener and the actions for the PREVIOUS button.
        previousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Sets the song to the previous track in the list.
                if (!isPreparing) {
                    playNextSong(false, false);
                }
            }
        });

        // REPEAT: Sets up the listener and the actions for the REPEAT button.
        repeatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isPreparing) {

                    // Toggles on/off the repeat playback feature.
                    if (isLoop) {
                        repeatToggle(false, true); // Enables the repeat playback feature.
                    }

                    else {
                        repeatToggle(true, true); // Disables the repeat playback feature.
                    }
                }
            }
        });

        // REWIND: Sets up the listener and the actions for the REWIND button.
        rewindButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isPreparing) {

                    // Retrieves the current seekbar progress and sets the new seekbar position value.
                    int newPosition = playerBar.getProgress() - 6;

                    // If the new position is less than 0, the value is set at 0.
                    if (newPosition < 0) {
                        newPosition = 0;
                    }

                    playerBar.setProgress(newPosition); // Sets the new seekbar position.
                    setPosition(newPosition); // Sets the new position of the song.
                }
            }
        });
    }

    // setUpImage(): Sets up the images for the ImageView objects in the fragment.
    private void setUpImage() {

        // FORWARD BUTTON:
        Picasso.with(currentActivity)
                .load(android.R.drawable.ic_media_ff)
                .resize((int) (56 * curDensity), (int) (56 * curDensity))
                .into(forwardButton);

        // NEXT BUTTON:
        Picasso.with(currentActivity)
                .load(android.R.drawable.ic_media_next)
                .resize((int) (48 * curDensity), (int) (48 * curDensity))
                .into(nextButton);

        // PREVIOUS BUTTON:
        Picasso.with(currentActivity)
                .load(android.R.drawable.ic_media_previous)
                .resize((int) (48 * curDensity), (int) (48 * curDensity))
                .into(previousButton);

        // REWIND BUTTON:
        Picasso.with(currentActivity)
                .load(android.R.drawable.ic_media_rew)
                .resize((int) (56 * curDensity), (int) (56 * curDensity))
                .into(rewindButton);

        updateAlbumImage(albumImageURL); // ALBUM IMAGE
        updateAutoPlayButton(autoPlayOn); // AUTOPLAY BUTTON
        updatePlayPauseButton(isPlaying); // PLAY/PAUSE BUTTON
        updateRepeatButton(isLoop); // REPEAT BUTTON
    }

    // setUpSeekbar(): Sets up a listener for the Seekbar object.
    private void setUpSeekbar() {

        playerBar.setMax(30); // Sets the maximum duration to 30 seconds by default.

        playerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // onProgressChanged(): Called when the seekbar progress has changed.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // If the progress change is from user input, the position of the song is changed.
                if (fromUser) {

                    // Updates the minimum duration TextView object.
                    if (progress < 10) {
                        minDurationText.setText("0:0" + progress);
                    } else {
                        minDurationText.setText("0:" + progress);
                    }

                    setPosition(progress); // Sets the new position of the song.
                }
            }

            // onStartTrackingTouch(): Called when a touch event on the Seekbar object has started.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            // onStopTrackingTouch: Called when a touch event on the Seekbar object has ended.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // setUpText(): Sets up the texts for the TextView objects in the fragment.
    private void setUpText() {

        // Sets the artist and album name for the TextView object.
        artistAlbumNameText.setText(artistName + " - " + albumName);
        artistAlbumNameText.setShadowLayer(8, 2, 2, Color.BLACK); // Sets the shadow layer effect.

        // Sets the song name for the TextView object.
        songNameText.setText(songName);
        songNameText.setShadowLayer(8, 2, 2, Color.BLACK); // Sets the shadow layer effect.
    }

    // updateAlbumImage(): Updates the album ImageView object with the specified image URL.
    private void updateAlbumImage(String albumUrl) {

        // ALBUM COVER: Loads the image from the image URL into the albumImage ImageView object and
        // stores a reference to the loaded bitmap.
        Target target = new Target() {

            // onBitmapLoaded(): Runs when the bitmap is loaded.
            @SuppressLint("NewApi")
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                albumBitmap = bitmap; // Stores the reference to the album bitmap.

                // Sets the album image and the blurred background image as long as this fragment
                // view is not destroyed.
                if (!isDestroyed) {

                    albumImage.setImageBitmap(bitmap); // Sets the album image bitmap.

                    // Android API Level 16+: Sets a highly blurred version of the album image as
                    // the background for the fragment layout.
                    if (api_level >= 16) {
                        playerContainer.setBackground(SSBlurBuilder.createBlurDrawable(currentActivity, bitmap));
                    }

                    // Android API Level 1-15: Sets a highly blurred version of the album image as
                    // the background for the fragment layout.
                    else {
                        playerContainer.setBackgroundDrawable(SSBlurBuilder.createBlurDrawable(currentActivity, bitmap));
                    }
                }
            }

            // onBitmapFailed(): Runs when the bitmap failed to load.
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(LOG_TAG, "onBitmapFailed(): ERROR: Bitmap failed to load.");
            }

            // onPrepareLoad(): Runs prior to loading the bitmap.
            @SuppressLint("NewApi")
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

                // Clears ImageView resource to free up memory if the device is running on
                // GINGERBREAD.
                if (api_level < 12) {
                    albumImage.setImageDrawable(null); // Sets the ImageView Drawable to be null.
                    albumImage.setBackgroundDrawable(null); // Sets the ImageView background to be null.
                }
            }
        };

        // Loads the album image from the URL into the target.
        Picasso.with(currentActivity)
                .load(albumUrl)
                .into(target);
    }

    // updateAutoPlayButton(): Updates the graphics of the auto play button.
    private void updateAutoPlayButton(Boolean isAutoPlay) {

        // Checks to see if the fragment has been destroyed or not.
        if (!isDestroyed) {

            // AUTO PLAY ENABLED:
            if (isAutoPlay) {

                Picasso.with(currentActivity)
                        .load(R.drawable.ss_forward_on)
                        .resize((int) (36 * curDensity), (int) (36 * curDensity))
                        .into(autoPlayButton);
            }

            // AUTO PLAY DISABLED:
            else {

                Picasso.with(currentActivity)
                        .load(R.drawable.ss_forward_off)
                        .resize((int) (36 * curDensity), (int) (36 * curDensity))
                        .into(autoPlayButton);
            }
        }
    }

    // updateRepeatButton(): Updates the graphics of the repeat button.
    private void updateRepeatButton(Boolean isRepeat) {

        // Checks to see if the fragment has been destroyed or not.
        if (!isDestroyed) {

            // REPEAT ENABLED:
            if (isRepeat) {

                Picasso.with(currentActivity)
                        .load(R.drawable.ss_repeat_on)
                        .resize((int) (36 * curDensity), (int) (36 * curDensity))
                        .into(repeatButton);
            }

            // REPEAT DISABLED:
            else {

                Picasso.with(currentActivity)
                        .load(R.drawable.ss_repeat_off)
                        .resize((int) (36 * curDensity), (int) (36 * curDensity))
                        .into(repeatButton);
            }
        }
    }

    // updatePlayPauseButton(): Updates the graphics of the playback control buttons.
    private void updatePlayPauseButton(Boolean isPlay) {

        // Checks to see if the fragment has been destroyed or not.
        if (!isDestroyed) {

            // PLAYING:
            if (isPlay) {

                Picasso.with(currentActivity)
                        .load(android.R.drawable.ic_media_pause)
                        .resize((int) (64 * curDensity), (int) (64 * curDensity))
                        .into(playPauseButton);
            }

            // STOP / PAUSED:
            else {

                Picasso.with(currentActivity)
                        .load(android.R.drawable.ic_media_play)
                        .resize((int) (64 * curDensity), (int) (64 * curDensity))
                        .into(playPauseButton);
            }
        }
    }

    /** MUSIC PLAYER METHODS ___________________________________________________________________ **/

    // autoPlayToggle(): This method toggles on/off the auto play property.
    private void autoPlayToggle(Boolean isAutoPlay, Boolean showMessage) {

        // AUTO PLAY OFF: Turns off the auto play feature.
        if (!isAutoPlay) {
            autoPlayOn = false;
            updateAutoPlayButton(false); // Updates the auto play button ImageView.
            SSPreferences.setAutoPlay(false, SS_prefs); // Updates the SharedPreferences.

            // Displays a Toast of the updated settings.
            if (showMessage) {
                displaySnackbarMessage("TRACKLIST AUTO PLAY has been disabled.");
            }
        }

        // AUTO PLAY ON: Turns on the auto play feature.
        else {
            autoPlayOn = true;
            updateAutoPlayButton(true); // Updates the auto play button ImageView.
            SSPreferences.setAutoPlay(true, SS_prefs); // Updates the SharedPreferences.

            repeatToggle(false, false); // Disables repeat playback settings.

            // Displays a Toast of the updated settings.
            if (showMessage) {
                displaySnackbarMessage("TRACKLIST AUTO PLAY has been enabled.");
            }
        }
    }

    // repeatToggle(): This method toggles on/off the repeat playback property.
    private void repeatToggle(Boolean looping, Boolean showMessage) {

        // REPEAT OFF: Turns off song repeat.
        if (!looping) {
            isLoop = false;
            updateRepeatButton(false); // Updates the repeat button ImageView.
            SSPreferences.setRepeat(false, SS_prefs); // Updates the SharedPreferences.

            // Displays a Toast of the updated settings.
            if (showMessage) {
                displaySnackbarMessage("TRACK PLAYBACK REPEAT disabled.");
            }
        }

        // REPEAT ON: Turns on infinite playback of the song.
        else {
            isLoop = true;
            updateRepeatButton(true); // Updates the repeat button ImageView.
            SSPreferences.setRepeat(true, SS_prefs); // Updates the SharedPreferences.

            autoPlayToggle(false, false); // Disables auto playback settings.

            // Displays a Toast of the updated settings.
            if (showMessage) {
                displaySnackbarMessage("TRACK PLAYBACK REPEAT enabled.");
            }
        }
    }

    // initializeSongPlay(): Prepares the selected track for music playback.
    private void initializeSongPlay() {

        loadPreferences(); // Loads values from SharedPreferences.

        // Signals the activity to signal the SSMusicService to begin streaming playback of
        // the current track.
        playTrack(streamURL, false, albumBitmap, notificationsOn, artistName, songName);

        // Sets the current track name for the SSMainActivity activity.
        updateCurrentTrack(songName, streamURL, selectedPosition);

        if (!isDestroyed) {

            // Displays the progress indicator container. This will be shown until music
            // playback is fully ready.
            progressLayer.setVisibility(View.VISIBLE);

            updateActionBar(songName); // Updates the ActionBar title.

            // Sets the message to display as a Snackbar message.
            String snackMessage = "NOW PLAYING: " + artistName + " - " + songName;

            // TABLET: Displays the Snackbar within this fragment layout container.
            if (isTablet) {
                SSSnackbar.snackOnThis(snackMessage, playerContainer);
            }

            // MOBILE: Displays the Snackbar within the activity layout container.
            else {
                displaySnackbarMessage(snackMessage);
            }

            // Starts the song timer thread. This will display a time out error Toast message if the
            // song is not ready in a given amount of time.
            startStopSongTimer(true);
        }

        isPaused = false; // Indicates that the song is not paused.
        isPreparing = true; // Indicates that the song is currently being prepared for playback.
    }

    // updateTrack(): Updates the song track details based on the set position.
    private Boolean updateTrack(int position) {

        // Checks to see if the position has not exceeded the size of the trackList array or is a
        // non-negative value.
        if ( (position < trackList.size()) && (position >= 0)) {

            // Updates the track details based on the current position.
            selectedPosition = position;
            artistName = trackList.get(position).getArtist();
            songId = trackList.get(position).getSongId();
            songName = trackList.get(position).getSong();
            albumName = trackList.get(position).getAlbum();
            albumImageURL = trackList.get(position).getAlbumImage();
            streamURL = trackList.get(position).getSongURL();

            updateAlbumImage(albumImageURL); // Updates the album image.

            if (!isDestroyed) {
                setUpText(); // Updates the artist and song name TextView objects.
                minDurationText.setText("0:00"); // Resets the minimum duration TextView object.
                playerBar.setProgress(0); // Resets the seekbar.
            }

            setPosition(0); // Resets the song track position.

            // Sets the current track name for the SSMainActivity activity.
            updateCurrentTrack(songName, streamURL, selectedPosition);

            return true; // Indicates that the track has changed.
        }

        // Displays a Toast informing the user that the beginning of the tracklist has been reached.
        else if (position < 0){
            displaySnackbarMessage("Reached the beginning of the tracklist.");
            return false; // Indicates that the track has not changed.
        }

        // Displays a Toast informing the user that the end of the tracklist has been reached.
        else if (position >= trackList.size()) {
            displaySnackbarMessage("Reached the end of the tracklist.");
            return false; // Indicates that the track has not changed.
        }

        // Returns false, as it should never reach this point.
        else {
            Log.e(LOG_TAG, "updateTrack(): An unknown error has occurred while attempting to update the track.");
            return false; // Indicates that the track has not changed.
        }
    }

    /** PREFERENCE METHODS _____________________________________________________________________ **/

    // loadPreferences(): Loads the SharedPreference values from the stored SharedPreferences object.
    private void loadPreferences() {

        // Initializes the SharedPreferences object.
        SS_prefs = SSPreferences.initializePreferences(SS_OPTIONS, currentActivity);

        // Retrieves the current auto play setting.
        autoPlayOn = SSPreferences.getAutoPlay(SS_prefs);

        // Retrieves the repeat setting.
        isLoop = SSPreferences.getRepeat(SS_prefs);

        // Retrieves the current notification player setting.
        notificationsOn = SSPreferences.getNotifications(SS_prefs);
    }

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // playbackStatus(): An interface method invoked by the SSMusicEngine on the current playback
    // status of the song.
    @Override
    public void playbackStatus(Boolean isPlay) {

        Log.d(LOG_TAG, "playbackStatus(): Current playback status: " + isPlaying);

        isPlaying = isPlay; // Updates the current playback status of the streaming song.
        updatePlayPauseButton(isPlaying); // Updates the player control buttons.

        // PLAYING:
        if (isPlay) {

            startStopSongTimer(false); // Turns off the song timer.
            isPreparing = false; // Indicates that the song is no longer being prepared.

            // Hides the progress indicator container as long as the fragment is not destroyed.
            if (!isDestroyed) {
                progressLayer.setVisibility(View.INVISIBLE);
            }
        }
    }

    // playCurrentSong(): An interface method invoked by SSMainActivity to play the current song
    // in the tracklist.
    @Override
    public void playCurrentSong() {

        // Plays the current track song.
        if (!isPlaying && !isPreparing) {
            initializeSongPlay();
        }

        // Displays a Toast informing the user that a song is already playing in the background.
        else {
            displaySnackbarMessage(songName + " by " + artistName + " is already playing.");
        }
    }

    // playNextSong(): An interface method invoked by the SSMusicEngine to play the next or previous
    // song in the tracklist.
    @Override
    public void playNextSong(Boolean isNext, Boolean fromNotification) {

        int newPosition = selectedPosition;

        // NEXT TRACK:
        if (isNext) {
            newPosition++;
        }

        // PREVIOUS TRACK:
        else {
            newPosition--;
        }

        // Sets the song to the next track in the list.
        Boolean isUpdate = updateTrack(newPosition);

        // If the previous track was playing when the next button was pressed, the new track
        // is automatically played.
        if (isUpdate && isPlaying) {
            initializeSongPlay();
        }

        // If the song is changed from the notification player while the song is not playing, the
        // notification player is updated with the updated track.
        else if (isUpdate && fromNotification && !isPlaying) {
            updateNotification(streamURL, notificationsOn, albumBitmap, artistName, songName);
        }
    }

    // seekbarStatus(): An interface method invoked by the SSMusicEngine to update the player
    // seekbar position.
    @Override
    public void seekbarStatus(int position) {

        if (!isDestroyed && !isPreparing) {

            // Updates the seekbar as long as the song is playing.
            if (position != -1) {

                Log.d(LOG_TAG, "seekbarStatus(): Setting the seekbar position: " + position);

                playerBar.setProgress(position); // Sets the current position for the player seekbar.

                // Prepares the formatting of the text to set for the minimum duration TextView.
                String curDuration;
                if (position < 10) {
                    curDuration = "0:0" + position;
                }

                else {
                    curDuration = "0:" + position;
                }

                // Sets the current position into the minimum duration TextView object.
                minDurationText.setText(curDuration);
            }

            // Updates the player control button states to reflect that the song is no longer playing.
            else {

                isPlaying = false; // Indicates that the song is no longer being played.
                updatePlayPauseButton(isPlaying); // Updates the player control buttons.

                // If the song has stopped and is not paused, the seek bar and the current position
                // text is reset.
                if (!isPaused) {
                    pauseTrack(true); // Indicates that the song has stopped playback.
                    playerBar.setProgress(0); // Resets the player seek bar.
                    minDurationText.setText("0:00"); // Resets the minimum duration TextView object.
                    SSNotificationPlayer.removeNotifications(currentActivity); // Removes any active notifications.

                    if (!isPreparing) {

                        // REPEAT ENABLED: If the repeat playback feature has been enabled, the
                        // current song will be repeated.
                        if (isLoop) {
                            initializeSongPlay(); // Plays the current song.
                        }

                        // AUTO PLAY ENABLED: If the auto play feature has been enabled, the next
                        // song in the tracklist will automatically be played.
                        else if (autoPlayOn) {

                            // Checks to see if the end of the tracklist has been reached first.
                            if ( (selectedPosition + 1) < trackList.size()) {
                                playNextSong(true, false); // Sets the next song.
                                playCurrentSong(); // Plays the current song.
                            }
                        }
                    }
                }
            }
        }
    }

    // setDuration(): An interface method invoked by the SSMusicEngine to set the player seekbar
    // max duration.
    @Override
    public void setDuration(int duration) {

        if (!isDestroyed) {
            playerBar.setMax(duration); // Sets the maximum duration of the player seekbar.
            maxDurationText.setText("0:" + duration);
            Log.d(LOG_TAG, "setDuration(): Maximum duration of the seekbar set.");
        }
    }

    // stopSongPrepare(): An interface method invoked by SSApplication to stop song preparation in
    // the case of a time out error.
    @Override
    public void stopSongPrepare(Boolean isStop) {

        // Stops preparation of the song track.
        if (isStop) {

            if (!isDestroyed) {
                progressLayer.setVisibility(View.INVISIBLE); // Hides the progress indicator container.
                SSNotificationPlayer.removeNotifications(currentActivity); // Removes all active notifications.
            }

            isPlaying = false; // Indicates that the song is not being played.
            isPreparing = false; // Indicates that the song is no longer being prepared.
        }

        // Displays the progress indicator container.
        else {

            if (!isDestroyed) {
                progressLayer.setVisibility(View.VISIBLE); // Displays the progress indicator container.
            }
        }
    }

    // attachPlayerFragment(): Signals the attached class to attach this fragment to the
    // SSMusicService.
    private void attachPlayerFragment() {

        Log.d(LOG_TAG, "attachPlayerFragment(): Player fragment attached.");

        try { ((OnMusicServiceListener) currentActivity.getApplication()).attachFragment(this); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // displaySnackbarMessage(): An interface method that signals the attached activity to display a
    // Snackbar message.
    private void displaySnackbarMessage(String message) {
        try { ((OnSnackbarDisplayListener) currentActivity).displaySnackbar(message); }
        catch (ClassCastException cce) { } // Catch for class cast exception errors.
    }

    // pauseTrack(): Signals the attached class to invoke the SSMusicService to pause playback
    // of the streamed Spotify track.
    private void pauseTrack(Boolean isStop) {
        try { ((OnMusicServiceListener) currentActivity.getApplication()).pauseTrack(isStop); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // playTrack(): Signals the attached class to invoke the SSMusicService to begin playback
    // of the streamed Spotify track.
    private void playTrack(String url, Boolean loop, Bitmap albumImage, Boolean notiOn, String artist, String track) {
        try { ((OnMusicServiceListener) currentActivity.getApplication()).playTrack(url, loop, albumImage, notiOn, artist, track); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // setPosition(): Signals the attached class to invoke the SSMusicService to update the song
    // position.
    private void setPosition(int position) {
        try { ((OnMusicServiceListener) currentActivity.getApplication()).setPosition(position); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // startStopSongTimer(): Signals the attached class to start/stop the song timer that is used
    // to display a time out error if the song is not ready for playback by a certain amount of time.
    private void startStopSongTimer(Boolean isStart) {
        try { ((OnMusicServiceListener) currentActivity.getApplication()).startStopSongTimer(isStart); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // updateActionBar(): Signals the attached activity to update the ActionBar title.
    private void updateActionBar(String name) {
        try { ((OnTrackInfoUpdateListener) currentActivity).updateActionBar(name); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // updateCurrentTrack(): Signals the attached activity to update the track name and Spotify track URL.
    private void updateCurrentTrack(String name, String url, int position) {
        try { ((OnTrackInfoUpdateListener) currentActivity).setCurrentTrack(name, url, position); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // updateNotification(): Signals the attached class to invoke the SSMusicService to update the
    // notification player when the next/previous button is pressed.
    private void updateNotification(String songUrl, Boolean notiOn, Bitmap image, String artist, String track) {
        try { ((OnMusicServiceListener) currentActivity.getApplication()).updateNotification(songUrl, notiOn, image, artist, track); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // updatePlayer(): Signals the attached class to invoke the SSMusicService to begin playback
    // of the streamed Spotify track.
    private void updatePlayer() {
        try { ((OnMusicServiceListener) currentActivity.getApplication()).updatePlayer(); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }
}