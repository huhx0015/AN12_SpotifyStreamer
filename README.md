AN12_SpotifyStreamer
====================

DEVELOPER: huhx0015

### SPOTIFY STREAMER M
![spotifystreamerm_preview](https://cloud.githubusercontent.com/assets/1645482/12526533/2622477c-c123-11e5-9172-0f016ed3ca9d.gif)

## Description

Android Nanodegree - Project 1-2: Spotify Streamer M: An application that utilizes the Spotify API to allow users to search for and listen to preview clips of popular music artists' top 10 tracks.

Spotify Streamer M utilizes a single activity class and manages multiple fragments to display different parts of the app. It supports separate layouts for mobile and tablet devices. Spotify Streamer M uses a Service for handling audio playback of the music engine (which is derived from https://github.com/huhx0015/HuhX_Game_Sound_Engine). In addition, it extends the Application class to manage the interactions between the Activity & Fragments and the music Service.

To access the Spotify API, Spotify Streamer M relies on the Spotify Web API for Android library (https://github.com/kaaes/spotify-web-api-android). In addition, Spotify Streamer M makes full use of the Android Design Support Library to employ a rich, Material Design-compliant appearance. Spotify Streamer M also utilizes popular third-party Android libraries such as Square's Picasso, RetroFit, and OkHttp libraries, along with ButterKnife for view dependency injection support.

## Rubric

Link: https://docs.google.com/document/d/18GcAc70wUlllHUDoAAVF36fX9YSkuMyX07OWVQ2tDWI/pub?embedded=true


### Spotify Streamer, Stage 1 Rubric

#### Required Components

To "meet specifications", your app must fulfill all the criteria listed in this section of the rubric.

#### User Interface - Layout

- [x] [Phone] UI contains a screen for searching for an artist and displaying a list of artist results
Individual artist result layout contains - Artist Thumbnail , Artist name
- [x] [Phone] UI contains a screen for displaying the top tracks for a selected artist
- [x] Individual track layout contains - Album art thumbnail, track name, album name

#### User Interface - Function

- [x] App contains a search field that allows the user to enter in the name of an artist to search for
- [x] When an artist name is entered, app displays list of artist results
- [x] App displays a message (for example, a toast) if the artist name/top tracks list for an artist is not found (asks to refine search)
- [x] When an artist is selected, app launches the “Top Tracks” View
- [x] App displays a list of top tracks

#### Network API Implementation

- [x] App implements Artist Search + GetTopTracks API Requests (Using the Spotify wrapper or by making a HTTP request and deserializing the JSON data)
- [x] App stores the most recent top tracks query results and their respective metadata (track name, artist name, album name) locally in list.
- [x] The queried results are retained on rotation.

#### Common Project Requirements

To “meet specifications”, your app must fulfill all the criteria listed in this section of the rubric.

#### General Project Guidelines

- [x] App conforms to common standards found in the Android Nanodegree General Project Guidelines

#### Optional Components

There are no optional components for stage 1.


### Spotify Streamer, Stage 2 Rubric

#### Required Components

To “meet specifications”, your app must fulfill all the criteria listed in this section of the rubric.

#### User Interface - Layout:

- [x] [Phone] UI contains a screen for searching for an artist and displaying a list of artist results
- [x] Individual artist result layout contains - Artist Thumbnail , Artist name
- [x] [Phone] UI contains a screen for displaying the top tracks for a selected artist
- [x] Individual track layout contains - Album art thumbnail, track name , album name
- [x] [Phone] UI contains a screen that represents the player. It contains track info (e.g., track duration, elapsed time, artist name, album name, album artwork, and track name). It also contains playback controls (e.g., play/pause/previous track/next track buttons and scrub bar) for the currently selected track.
- [x] Tablet UI uses a Master-Detail layout implemented using fragments. The left fragment is for searching artists and the right fragment is for displaying top tracks of a selected artist. The Now Playing controls are displayed in a DialogFragment.

#### User Interface - Function:

- [x] App contains a search field that allows the user to enter in the name of an artist to search for
- [x] When an artist name is entered, app displays list of artist results in a ListView
- [x] App displays a message (for example, a toast) if the artist name/top tracks list for an artist is not found (asks to refine search)
- [x] When an artist is selected, app launches the “Top Tracks” View
- [x] App displays a list of top tracks
- [x] When a track is selected, the app displays the Now Playing screen and starts playing the track.

#### Network API Implementation:

- [x] App implements Artist Search + GetTopTracks API Requests (Using the Spotify wrapper or by making a HTTP request and deserializing the JSON data)
- [x] App stores the most recent top tracks query results and their respective metadata (track name , artist name, album name) locally in list.
- [x] The queried results are retained on rotation.

#### Media Playback:

- [x] App implements streaming playback of tracks
- [x] User is able to advance to the previous track
- [x] User is able to advance to the next track
- [x] Play button starts/resumes playback of currently selected track
- [x] Pause button pauses playback of currently selected track
- [x] If a user taps on another track while one is currently playing, playback is stopped on the currently playing track and the newly selected track (in other words, the tracks should not mix)
- [x] Scrub bar displays the current playback position in time and is scrubbable. Scrubbing changes the track position appropriately.

#### Common Project Requirements:

To “meet specifications”, your app must fulfill all the criteria listed in this section of the rubric.

#### General Project Guidelines:

- [x] App conforms to common standards found in the Android Nanodegree General Project Guidelines

#### Optional Components:

To receive “exceeds specifications”, your app must fully implement all the criteria listed in this section of the rubric.

#### User Interface - Function:

- [x] App displays a “Now Playing” Button in the ActionBar that serves to reopen the player UI should the user navigate back to browse content and then want to resume control over playback.

#### Notifications:

- [x] App implements a notification with playback controls ( Play, pause , next & previous track )
- [x] Notification media controls are usable on the lockscreen and drawer
- [x] Notification displays track name and album art thumbnail

#### Sharing Functionality:

- [x] App adds a menu for sharing the currently playing track
- [x] App uses a shareIntent to expose the external Spotify URL for the current track

#### Settings Menu:

- [x] App has a menu item to select the country code (which is automatically passed into the get Top Tracks query )
- [x] App has menu item to toggle showing notification controls on the drawer and lock screen
