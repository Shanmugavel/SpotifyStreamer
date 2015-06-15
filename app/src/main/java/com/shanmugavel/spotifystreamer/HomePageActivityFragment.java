package com.shanmugavel.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
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
    public HomePageActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "Inside onCreate");
        mSpotifyApi = new SpotifyApi();
        mSpotifySvc = mSpotifyApi.getService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Inside onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
        EditText mTxtSearch = (EditText) rootView.findViewById(R.id.txtSearchArtist);
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
                if (s.length() > 0) {
                    new FetchArtistsTask().execute(s.toString());
                }
            }
        });
        return rootView;
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
                    List<Artist> lstArtists = artistsPager.artists.items;
                    Log.i(LOG_TAG, "Size::" + lstArtists.size());
                    if (lstArtists.size() > 0) {
                        Log.i(LOG_TAG, lstArtists.get(0).toString());
                        Log.i(LOG_TAG, lstArtists.get(0).name);
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
