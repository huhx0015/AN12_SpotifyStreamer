package com.huhx0015.spotifystreamer.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.huhx0015.spotifystreamer.R;
import com.huhx0015.spotifystreamer.data.SSSpotifyModel;
import com.huhx0015.spotifystreamer.interfaces.OnSpotifySelectedListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/** -----------------------------------------------------------------------------------------------
 *  [SSResultsAdapter] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SSResultsAdapter is a RecyclerView adapter class that is used for setting up and
 *  loading Spotify data into a RecyclerView list object.
 *  -----------------------------------------------------------------------------------------------
 */

public class SSResultsAdapter extends RecyclerView.Adapter<SSResultsAdapter.SSResultViewHolder> {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private Activity currentActivity; // References the attached activity.

    // LAYOUT VARIABLES:
    private Boolean isClickable = true; // Used to determine if the items are clickable or not.

    // LIST VARIABLES
    private Boolean isTrack = false; // Used to determine if the current list is a list of tracks.
    private ArrayList<SSSpotifyModel> listResult; // References the ArrayList of SSSpotifyModels object.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SSResultsAdapter.class.getSimpleName();

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // SSResultsAdapter(): Constructor method for SSResultsAdapter.
    public SSResultsAdapter(ArrayList<SSSpotifyModel> list, Boolean clickable, Boolean tracks, Activity act){
        this.currentActivity = act;
        this.isClickable = clickable;
        this.isTrack = tracks;
        this.listResult = list;
    }

    /** EXTENSION METHODS ______________________________________________________________________ **/

    // onCreateViewHolder: This method is called when the custom ViewHolder needs to be initialized.
    // The layout of each item of the RecyclerView is inflated using LayoutInflater, passing the
    // output to the constructor of the custom ViewHolder.
    @Override
    public SSResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflates the layout given the XML layout file for the item view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ss_song_result_card, parent, false);

        // Sets the action if the RecyclerView item property is set to be clickable.
        if (isClickable) {

            // Sets the view holder for the item view. This is needed to handle the individual item
            // clicks.
            final SSResultViewHolder viewHolder = new SSResultViewHolder(view, isTrack, new SSResultViewHolder.OnResultViewHolderClick() {

                // onItemClick(): Defines an action to take when the item in the list is clicked.
                @Override
                public void onItemClick(View caller, int position) {

                    // SSTracksFragment: Signals the attached activity to switch the fragment to the
                    // SSPlayerFragment.
                    if (isTrack) {
                        displayPlayer(listResult, position);
                    }

                    // SSArtistsFragment: Signals the attached activity to switch the fragment to
                    // SSTracksFragment, as well as passing the artist image URL back to the parent
                    // activity.
                    else {
                        displayTopTracks(listResult.get(position).getArtist(), listResult.get(position).getAlbumImage());
                    }
                }
            });

            return viewHolder;
        }

        return new SSResultViewHolder(view, isTrack, null);
    }

    // onBindViewHolder(): Overrides the onBindViewHolder to specify the contents of each item of
    // the RecyclerView. This method is similar to the getView method of a ListView's adapter.
    @Override
    public void onBindViewHolder(SSResultViewHolder holder, int position) {

        // Sets the song, album, and artist name into the TextView objects.
        holder.songName.setText(listResult.get(position).getSong());
        holder.albumName.setText(listResult.get(position).getAlbum());
        holder.artistName.setText(listResult.get(position).getArtist());

        // Retrieves the image URL at the referenced position.
        String albumImage = listResult.get(position).getAlbumImage();

        // Loads the referenced image into the ImageView object.
        if (albumImage != null) {

            Picasso.with(currentActivity)
                    .load(listResult.get(position).getAlbumImage())
                    .into(holder.albumImage);
        }

        // If no referenced image exists, the application icon is set instead.
        else {

            Picasso.with(currentActivity)
                    .load(R.drawable.ic_launcher)
                    .into(holder.albumImage);
        }
    }

    // getItemCount(): Returns the number of items present in the data.
    @Override
    public int getItemCount() {
        return listResult.size();
    }

    // onAttachedToRecyclerView(): Overrides the onAttachedToRecyclerView method.
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // displayPlayer(): Signals attached activity to display the SSPlayerFragment view.
    private void displayPlayer(ArrayList<SSSpotifyModel> list, int position) {
        try { ((OnSpotifySelectedListener) currentActivity).displayPlayerFragment(true, "TRACKS", list, position, true); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    // displayTopTracks(): Signals attached activity to display the SSTracksFragment view.
    private void displayTopTracks(String name, String artistImageUrl) {
        try { ((OnSpotifySelectedListener) currentActivity).displayTracksFragment(true, name, artistImageUrl); }
        catch (ClassCastException cce) {} // Catch for class cast exception errors.
    }

    /** SUBCLASSES _____________________________________________________________________________ **/

    /**
     * --------------------------------------------------------------------------------------------
     * [SSResultViewHolder] CLASS
     * DESCRIPTION: This subclass is responsible for referencing the view for an item in the
     * RecyclerView list view object.
     * --------------------------------------------------------------------------------------------
     */
    public static class SSResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /** SUBCLASS VARIABLES _________________________________________________________________ **/

        // LAYOUT VARIABLES:
        CardView songCardView;
        ImageView albumImage;
        TextView songName;
        TextView artistName;
        TextView albumName;

        // LIST VARIABLES
        Boolean isTrack = false; // Used to determine if this is view holder is for an artist or track list.

        // LISTENER VARIABLES
        public OnResultViewHolderClick resultItemListener; // Interface on-click listener variable.

        /** SUBCLASS METHODS ___________________________________________________________________ **/

        SSResultViewHolder(View itemView, Boolean tracks, OnResultViewHolderClick listener) {

            super(itemView);

            // Sets the references for the View objects in the adapter layout.
            songCardView = (CardView) itemView.findViewById(R.id.ss_song_result_cardview_container);
            albumImage = (ImageView) itemView.findViewById(R.id.ss_album_image);
            songName = (TextView) itemView.findViewById(R.id.ss_song_name_text);
            artistName = (TextView) itemView.findViewById(R.id.ss_artist_name_text);
            albumName = (TextView) itemView.findViewById(R.id.ss_album_name_text);

            isTrack = tracks; // Sets the list type for the view holder.

            // Sets the listener for the item view.
            if (listener != null) {
                resultItemListener = listener; // Sets the OnResultViewHolderClick listener.
                itemView.setOnClickListener(this);
            }
        }

        // onClick(): Defines an action to take when an item is clicked.
        @Override
        public void onClick(View v) {

            int itemPos = getAdapterPosition(); // Retrieves the clicked item position.
            resultItemListener.onItemClick(v, itemPos); // Sets the item listener.
        }

        /** INTERFACE METHODS __________________________________________________________________ **/

        /**
         * -----------------------------------------------------------------------------------------
         * [OnResultViewHolderClick] INTERFACE
         * DESCRIPTION: This is an interface subclass that is used to provide methods to call when
         * the RecyclerView items are clicked.
         * -----------------------------------------------------------------------------------------
         */
        public interface OnResultViewHolderClick {

            // onItemClick(): The method that is called when an item in the RecyclerView is clicked.
            void onItemClick(View caller, int position);
        }
    }
}