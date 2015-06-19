package com.shanmugavel.spotifystreamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shanmugavel.spotifystreamer.Constants;
import com.shanmugavel.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by shanmugavelsundaramoorthy on 6/17/15.
 */
public class TracksAdapter extends ArrayAdapter<Track > {

    private static final String LOG_TAG = TracksAdapter.class.getName();

    public TracksAdapter(Context ctxt, List<Track> lstTracks) {
        super(ctxt, R.layout.top_tracks, lstTracks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int imgCnt = 0;
        Track track = getItem(position);

        if (null  == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.top_tracks, parent, false);
        }
        TextView artistName = (TextView) convertView.findViewById(R.id.txtAlbumTrackName);
        artistName.setText(track.album.name + System.getProperty(Constants.LINE_SEP) +track.name);

        ImageView albumImg = (ImageView) convertView.findViewById(R.id.imgAlbum);
        imgCnt = track.album.images.size();
        Log.i(LOG_TAG, "Image Count::" + imgCnt);
        if (imgCnt != 0) {
            imgCnt--;
            Picasso.with(getContext()).load(track.album.images.get(imgCnt).url).placeholder(R.drawable.ic_loading).error(R.drawable.ic_error).into(albumImg);
        } else {
            Log.i(LOG_TAG, "No images found!");
            Picasso.with(getContext()).load(R.drawable.ic_error).resize(64, 64).into(albumImg);
        }
        return convertView;
    }

    public List<Track> getValues() {
        List<Track> lstTracks = new ArrayList<Track>();
        for (int i =0; i < getCount(); i++) {
            lstTracks.add(getItem(i));
        }
        return lstTracks;
    }
}
