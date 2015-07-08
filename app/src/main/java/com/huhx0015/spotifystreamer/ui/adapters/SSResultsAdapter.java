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
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by Michael Yoon Huh on 7/3/2015.
 */
public class SSResultsAdapter extends RecyclerView.Adapter<SSResultsAdapter.SSSongResultViewHolder> {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private Activity currentActivity;

    // LIST VARIABLES
    private List<SSSpotifyModel> songListResult;

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    public SSResultsAdapter(List<SSSpotifyModel> songListResult, Activity act){
        this.currentActivity = act;
        this.songListResult = songListResult;
    }

    /** SUBCLASSES _____________________________________________________________________________ **/

    public static class SSSongResultViewHolder extends RecyclerView.ViewHolder {

        CardView songCardView;
        TextView songName;
        TextView artistName;
        TextView albumName;
        ImageView albumImage;

        SSSongResultViewHolder(View itemView) {
            super(itemView);

            songCardView = (CardView) itemView.findViewById(R.id.ss_song_result_cardview_container);
            songName = (TextView) itemView.findViewById(R.id.ss_song_name_text);
            artistName = (TextView) itemView.findViewById(R.id.ss_artist_name_text);
            albumName = (TextView) itemView.findViewById(R.id.ss_album_name_text);
            albumImage = (ImageView) itemView.findViewById(R.id.ss_album_image);
        }
    }

    /** EXTENSION METHODS ______________________________________________________________________ **/

    // As its name suggests, this method is called when the custom ViewHolder needs to be initialized.
    // We specify the layout that each item of the RecyclerView should use. This is done by inflating
    // the layout using LayoutInflater, passing the output to the constructor of the custom ViewHolder.
    @Override
    public SSSongResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ss_song_result_card, parent, false);
        return new SSSongResultViewHolder(view);
    }

    // Override the onBindViewHolder to specify the contents of each item of the RecyclerView.
    // This method is very similar to the getView method of a ListView's adapter.
    @Override
    public void onBindViewHolder(SSSongResultViewHolder holder, int position) {

        // Sets the song, album, and artist name into the TextView objects.
        holder.songName.setText(songListResult.get(position).getSong());
        holder.albumName.setText(songListResult.get(position).getAlbum());
        holder.artistName.setText(songListResult.get(position).getArtist());

        // Loads the image into the ImageView object.
        Picasso.with(currentActivity)
                .load(songListResult.get(position).getAlbumImage())
                .into(holder.albumImage);
    }

    // This should return the number of items present in the data. As our data is in the form of a
    // List, we only need to call the size method on the List object:
    @Override
    public int getItemCount() {
        return songListResult.size();
    }

    // Finally, you need to override the onAttachedToRecyclerView method. For now, we can simply use
    // the superclass' implementation of this method as shown below.
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}