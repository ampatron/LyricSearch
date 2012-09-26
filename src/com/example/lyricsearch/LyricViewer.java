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
		Handler handler = new Handler() {
			public void handleMessage(Message message) {
				switch (message.what) {
				case Lyrics.DID_TRY:
					Toast.makeText(getApplicationContext(),
							(((String) message.obj)) + " failed!",
							Toast.LENGTH_SHORT).show();
					break;
				case Lyrics.DID_LOAD:
					setLyrics();
					break;
				case Lyrics.DID_FAIL:
					Toast.makeText(getApplicationContext(),
							"Lyrics not found!", Toast.LENGTH_SHORT).show();
					setLyrics();
					break;
				case Lyrics.DID_ERROR:
					Toast.makeText(getApplicationContext(),
							"An error occured!", Toast.LENGTH_SHORT).show();
					setLyrics();
					break;
				case Lyrics.IS_TRYING:
					Toast.makeText(getApplicationContext(),
							"Trying " + ((String) message.obj),
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		return handler;
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