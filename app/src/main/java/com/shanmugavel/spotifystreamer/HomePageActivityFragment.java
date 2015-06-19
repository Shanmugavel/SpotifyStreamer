package com.shanmugavel.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.shanmugavel.spotifystreamer.adapter.ArtistsAdapter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomePageActivityFragment extends Fragment {

    private static final String LOG_TAG = HomePageActivityFragment.class.getName();
    private SpotifyApi mSpotifyApi = null;
    private SpotifyService mSpotifySvc = null;
    private ArtistsAdapter mArtistsAdapter = null;
    private String mArtistSrchString = null;
    private boolean isDataFromCache = false;
    public HomePageActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Inside onCreate");
        mSpotifyApi = new SpotifyApi();
        mSpotifySvc = mSpotifyApi.getService();
        mArtistsAdapter = new ArtistsAdapter(getActivity(), new ArrayList<Artist>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Inside onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
        EditText mTxtSearch = (EditText) rootView.findViewById(R.id.txtSearchArtist);
        isDataFromCache = false;
        if (null != savedInstanceState) {
            Log.i(LOG_TAG, "Populating from savedInstance!");
            mArtistSrchString = savedInstanceState.getString(Constants.ARTIST_SEARCH_STRING);
            List<String> lstArtistsName = (List<String>) savedInstanceState.getStringArrayList(Constants.LST_ARTISTS_NAME);
            List<String> lstArtistsImg = (List<String>) savedInstanceState.getStringArrayList(Constants.LST_ARTISTS_IMG);
            List<String> lstArtistsId = (List<String>) savedInstanceState.getStringArrayList(Constants.LST_ARTISTS_ID);

            List<Artist> lstArtists = new ArrayList<Artist>();

            for(int i=0; i < lstArtistsName.size(); i++) {
                Artist artist = new Artist();
                artist.name = lstArtistsName.get(i);
                artist.id = lstArtistsId.get(i);
                artist.images = new ArrayList<Image>();
                if ( !"".equals(lstArtistsImg.get(i)) ) {
                    Image img = new Image();
                    img.url = lstArtistsImg.get(i);
                    artist.images.add(img);
                }
                lstArtists.add(artist);
            }

            mTxtSearch.setText(mArtistSrchString);
            mArtistsAdapter.clear();
            mArtistsAdapter.addAll(lstArtists);
            isDataFromCache = true;
            Log.i(LOG_TAG, lstArtistsName.toString());
            Log.i(LOG_TAG, lstArtistsImg.toString());

        }
        mTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(LOG_TAG, "Text Changed::" + s.toString());
                if (s.length() > 0 && !isDataFromCache) {
                    Log.i(LOG_TAG, "DATA Loaded!");
                    mArtistSrchString = s.toString();
                    new FetchArtistsTask().execute(mArtistSrchString);
                }
                isDataFromCache = false;
            }
        });

        ListView lstArtistsView = (ListView) rootView.findViewById(R.id.lstArtists);
        lstArtistsView.setAdapter(mArtistsAdapter);
        lstArtistsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = (Artist) parent.getAdapter().getItem(position);
                Intent topTracksIntent = new Intent(getActivity(), TopTracksActivity.class);
                topTracksIntent.putExtra(Constants.ARTIST_ID,artist.id);
                topTracksIntent.putExtra(Constants.ARTIST_NAME,artist.name);
                startActivity(topTracksIntent);
                //Toast.makeText(getActivity(),artist.name , Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "Saving homepage fragment state!!");
        outState.putString(Constants.ARTIST_SEARCH_STRING, mArtistSrchString);
        outState.putStringArrayList(Constants.LST_ARTISTS_NAME, mArtistsAdapter.getArtistNames());
        outState.putStringArrayList(Constants.LST_ARTISTS_IMG,  mArtistsAdapter.getArtistImages());
        outState.putStringArrayList(Constants.LST_ARTISTS_ID,  mArtistsAdapter.getArtistIds());
        Log.i(LOG_TAG, "Homepage Fragment state saved!!!");
    }

    public void updateArtistListView(final List<Artist> lstArtists) {
            Log.i(LOG_TAG, "Inside updateArtistListView.");
            if (null != mArtistsAdapter) {
                mArtistsAdapter.clear();
                mArtistsAdapter.addAll(lstArtists);
                Log.i(LOG_TAG, "Added Records in Adapter!!!");
            }
        }

    public class FetchArtistsTask extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = FetchArtistsTask.class.getName();

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
            mSpotifySvc.searchArtists(params[0], new Callback<ArtistsPager>() {

                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    final List<Artist> lstArtists = artistsPager.artists.items;
                    Log.i(LOG_TAG, "Size::" + lstArtists.size());
                    if (lstArtists.size() > 0) {
                        Log.i(LOG_TAG, "Got Matching Records!!!");
                        getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateArtistListView(lstArtists);
                                }
                            }
                        );
                    } else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(LOG_TAG, "Inside Handler Thread!");
                                mArtistsAdapter.clear();
                                Toast.makeText(getActivity(), "No matching Artists found. Please refine your search criteria.", Toast.LENGTH_LONG).show();
                            }
                        });

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
