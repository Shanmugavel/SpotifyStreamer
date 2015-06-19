package com.shanmugavel.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shanmugavel.spotifystreamer.adapter.TracksAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private static final String LOG_TAG = TopTracksActivityFragment.class.getName();
    private SpotifyApi mSpotifyApi = null;
    private SpotifyService mSpotifySvc = null;
    private TracksAdapter mTracksAdapter = null;
    private String artistName = null;
    private String artistId = null;
    public TopTracksActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Inside onCreate");
        mSpotifyApi = new SpotifyApi();
        mSpotifySvc = mSpotifyApi.getService();
        mTracksAdapter = new TracksAdapter(getActivity(), new ArrayList<Track>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        if (null == savedInstanceState) {
            artistId = getActivity().getIntent().getStringExtra(Constants.ARTIST_ID);
            artistName = getActivity().getIntent().getStringExtra(Constants.ARTIST_NAME);
            new FetchTopTracksTask().execute(artistId);
        } else {
            Log.i(LOG_TAG, "Populating from savedInstance");
            artistId = savedInstanceState.getString(Constants.ARTIST_ID);
            artistName = savedInstanceState.getString(Constants.ARTIST_NAME);
            List<Track> lstTracks =  (List<Track>) savedInstanceState.getSerializable(Constants.LST_TRACKS);
            mTracksAdapter.clear();
            mTracksAdapter.addAll(lstTracks);
        }


        Log.i(LOG_TAG, "ArtistID:" + artistId);
        Log.i(LOG_TAG, "ArtistName:"+artistName);
    ;

        ListView lstTracksView = (ListView) rootView.findViewById(R.id.lstTracks);
        lstTracksView.setAdapter(mTracksAdapter);
        lstTracksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = (Track) parent.getAdapter().getItem(position);
                Toast.makeText(getActivity(), track.album.name+"::"+track.name+"::"+track.preview_url, Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ARTIST_ID, artistId);
        outState.putString(Constants.ARTIST_NAME, artistName);
        outState.putSerializable(Constants.LST_TRACKS, (Serializable) mTracksAdapter.getValues());
    }

    public void updateTracksListView(final List<Track> lstTracks) {
        Log.i(LOG_TAG, "Inside updateTracksListView.");
        if (null != mTracksAdapter) {
            mTracksAdapter.clear();
            mTracksAdapter.addAll(lstTracks);
            Log.i(LOG_TAG, "Added Records in Adapter!!!");
        }
    }

    public class FetchTopTracksTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchTopTracksTask.class.getName();

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(LOG_TAG, "After exection!!");
        }
        @Override
        protected Void doInBackground(String... params) {
            if (0 == params.length) {
                Log.e(LOG_TAG, "Empty/Null Arguments.");
            } else if (params.length > 1) {
                Log.e(LOG_TAG, "Invalid # of Arguments.");
            }
            Log.i(LOG_TAG, params[0]);
            Map<String, Object > queryParams = new HashMap<String, Object>();
            queryParams.put("country", "US");
            mSpotifySvc.getArtistTopTrack(params[0], queryParams, new Callback<Tracks>() {
                @Override
                public void success(Tracks tracks, Response response) {
                    {
                        final List<Track> lstTracks = tracks.tracks;
                        Log.i(LOG_TAG, "Size::" + lstTracks.size());
                        if (lstTracks.size() > 0) {
                            Log.i(LOG_TAG, "Got Matching Records!!!");
                            getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                updateTracksListView(lstTracks);
                                                            }
                                                        }
                            );
                        } else {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(LOG_TAG, "Inside Handler Thread!");
                                    mTracksAdapter.clear();
                                    Toast.makeText(getActivity(), getString(R.string.err_no_album_tracks_found), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(LOG_TAG, "Error in Fetching Artists:" + error.getMessage());
                }
            });
            return null;
        }
    }
}
