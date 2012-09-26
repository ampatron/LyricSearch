package com.example.lyricsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.jlyr.util.Lyrics;
import com.jlyr.util.Track;

public class LyricViewer extends Activity {

	TextView mText = null;
	Menu mMenu = null;

	Track mTrack = null;
	Lyrics mLyrics = null;
	boolean isLoading = false;

	String[] mSources = null;
	String[] mAllSources = null;
	boolean[] mSelectedSources = null;

	int mSearchEngine = 0;

	int mScrollY = 0;

	public static final String TAG = "JLyrViewer";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "launched viewer");
		setContentView(R.layout.viewer);

		mText = (TextView) findViewById(R.id.text);
		mText.setMovementMethod(new ScrollingMovementMethod());

		fillLyrics();
	}

	private void fillLyrics() {
		if (mTrack == null) {
			mTrack = getTrackFromIntent();
			if (mTrack == null) {
					mText.setText(getText(R.string.no_track_specified));
					return;
			}
		}

		String trackInfoStr = mTrack.toString();
		mText.setText("Loading lyrics for " + trackInfoStr + " ...");

		if (mSources == null) {
			mSources = getSourcesFromIntent();
		}

		loadLyrics();
	}

	private void loadLyrics() {
		if (mMenu != null) {
			mMenu.setGroupEnabled(0, false);
		}

		mLyrics = new LyricsFetcher(getBaseContext(), mTrack, mSources, true, false);

		isLoading = true;
		mLyrics.loadLyrics(getLoadHandler());
	}

	private Handler getLoadHandler() {
		return new Handler() {
			boolean connectionError = false; 
			public void handleMessage(Message message) {
				switch (message.what) {
				case LyricsFetcher.DID_TRY://failed: a specific provider
					Log.v(TAG, "did try");
					Toast.makeText(getApplicationContext(),
							(((String) message.obj)) + " failed!",
							Toast.LENGTH_SHORT).show();
					break;
				case LyricsFetcher.DID_LOAD://has lyrics
					Log.v(TAG, "did load");
					setLyrics();
					break;
				case LyricsFetcher.DID_FAIL:
					Log.v(TAG, "did fail");
					message.getData();
					if (!connectionError)
						mText.setText("no lyrics");
					else
						mText.setText("error connection");
					break;
				case LyricsFetcher.NO_CONNECTION:
					Log.v(TAG, "no connection");
					message.getData();
					mText.setText("error connection");
					connectionError = true;
					break;
				case LyricsFetcher.DID_ERROR:
					Log.v(TAG, "did error");
					connectionError = true;
					break;
				case Lyrics.IS_TRYING://trying a provider
					Log.v(TAG, "is trying");
					connectionError = false;
					break;
				}
			}
		};
	}

	private void setLyrics() {
		if (mMenu != null) {
			mMenu.setGroupEnabled(0, true);
		}

		isLoading = false;
		String trackInfoStr = mTrack.toString();
		String lyricsStr = mLyrics.getLyrics();

		mText.setText(trackInfoStr
				+ "\n"
				+ ((lyricsStr == null) ? getText(R.string.lyrics_not_found)
						: lyricsStr));

		mText.scrollTo(0, mScrollY);
	}

	private Track getTrackFromIntent() {
		Intent i = getIntent();

		String title = i.getStringExtra("Track.title");
		String artist = i.getStringExtra("Track.artist");
		String album = i.getStringExtra("Track.album");
		String year = i.getStringExtra("Track.year");

		if (title == null && artist == null) {
			return null;
		} else {
			Track track = new Track(artist, title, album, year);
			return track;
		}
	}

	private String[] getSourcesFromIntent() {
		Intent i = getIntent();

		String[] sources = i.getStringArrayExtra("LyricsSources");

		return sources;
	}
}