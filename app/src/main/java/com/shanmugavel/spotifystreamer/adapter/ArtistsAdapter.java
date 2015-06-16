package com.shanmugavel.spotifystreamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shanmugavel.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by shanmugavelsundaramoorthy on 6/15/15.
 */
public class ArtistsAdapter extends ArrayAdapter<Artist> {


    private static final String LOG_TAG = ArtistsAdapter.class.getName();

    public ArtistsAdapter(Context ctxt, List<Artist> lstArtists) {
        super(ctxt, R.layout.single_artist, lstArtists );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int imgCnt = 0;
        Artist artist = getItem(position);

        if (null  == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_artist, parent, false);
        }
        TextView artistName = (TextView) convertView.findViewById(R.id.txtArtistName);
        artistName.setText(artist.name);

        ImageView artistImg = (ImageView) convertView.findViewById(R.id.imgArtist);
        imgCnt = artist.images.size();
        Log.i(LOG_TAG, "Image Count::" + artist.images.size());
        if (imgCnt != 0) {
            imgCnt--;
            Picasso.with(getContext()).load(artist.images.get(imgCnt).url).placeholder(R.drawable.ic_loading).error(R.drawable.ic_error).into(artistImg);
        } else {
            Log.i(LOG_TAG, "No images found!");
            Picasso.with(getContext()).load(R.drawable.ic_error).resize(64, 64).into(artistImg);
        }
        return convertView;
    }
}
