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
import java.util.List;

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
    private List<SSSpotifyModel> listResult; // References the List of SSSpotifyModels object.

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // SSResultsAdapter(): Constructor method for SSResultsAdapter.
    public SSResultsAdapter(List<SSSpotifyModel> list, Boolean clickable, Activity act){
        this.currentActivity = act;
        this.isClickable = clickable;
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

        if (isClickable) {

            // Sets the view holder for the item view. This is needed to handle the individual item
            // clicks.
            final SSResultViewHolder viewHolder = new SSResultViewHolder(view, new SSResultViewHolder.OnResultViewHolderClick() {

                // onItemClick(): Defines an action to take when the item in the list is clicked.
                @Override
                public void onItemClick(View caller, String artist) {

                    // Signals the attached activity to switch the fragment to SSTracksFragment.
                    displayTopTracks(artist);
                }
            });

            return viewHolder;
        }

        return new SSResultViewHolder(view, null);
    }

    // onBindViewHolder(): Overrides the onBindViewHolder to specify the contents of each item of
    // the RecyclerView. This method is similar to the getView method of a ListView's adapter.
    @Override
    public void onBindViewHolder(SSResultViewHolder holder, int position) {

        // Sets the song, album, and artist name into the TextView objects.
        holder.songName.setText(listResult.get(position).getSong());
        holder.albumName.setText(listResult.get(position).getAlbum());
        holder.artistName.setText(listResult.get(position).getArtist());

        // Loads the image into the ImageView object.
        Picasso.with(currentActivity)
                .load(listResult.get(position).getAlbumImage())
                .into(holder.albumImage);
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

    // displayTopTracks(): Signals attached activity to display the SSTracksFragment view.
    private void displayTopTracks(String name) {
        try { ((OnSpotifySelectedListener) currentActivity).displayTracksFragment(true, name); }
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

        public OnResultViewHolderClick resultItemListener; // Interface on-click listener variable.

        // LAYOUT VARIABLES:
        CardView songCardView;
        ImageView albumImage;
        TextView songName;
        TextView artistName;
        TextView albumName;

        SSResultViewHolder(View itemView, OnResultViewHolderClick listener) {

            super(itemView);

            // Sets the references for the View objects in the adapter layout.
            songCardView = (CardView) itemView.findViewById(R.id.ss_song_result_cardview_container);
            albumImage = (ImageView) itemView.findViewById(R.id.ss_album_image);
            songName = (TextView) itemView.findViewById(R.id.ss_song_name_text);
            artistName = (TextView) itemView.findViewById(R.id.ss_artist_name_text);
            albumName = (TextView) itemView.findViewById(R.id.ss_album_name_text);

            // Sets the listener for the item view.
            if (listener != null) {
                resultItemListener = listener; // Sets the OnResultViewHolderClick listener.
                itemView.setOnClickListener(this);
            }
        }

        // onClick(): Defines an action to take when an item is clicked.
        @Override
        public void onClick(View v) {

            // Retrieves the the artist string from the clicked item.
            String artistNameString = artistName.getText().toString();

            // Triggers the onItemClick interface method to onCreateViewHolder.
            resultItemListener.onItemClick(v, artistNameString);
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
            void onItemClick(View caller, String artist);
        }
    }
}