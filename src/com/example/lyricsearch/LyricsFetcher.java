package com.example.lyricsearch;

import android.content.Context;
import android.os.Message;

import com.jlyr.util.Lyrics;
import com.jlyr.util.Track;

public class LyricsFetcher extends Lyrics {

	public static final int DID_LOAD = Lyrics.DID_LOAD;
	public static final int DID_TRY = Lyrics.DID_TRY;
	public static final int DID_ERROR = Lyrics.DID_ERROR;
	public static final int DID_FAIL = Lyrics.DID_FAIL;
	public static final int IS_TRYING = Lyrics.IS_TRYING;
	public static final int NO_CONNECTION = Lyrics.IS_TRYING+1;

	enum LyricSearchResults {SUCCESS, NO_CONNECTION, NO_LYRICS};

	
	public LyricsFetcher(Context context, Track track, boolean fromCache) {
		super(context, track);
		setReader(new LyricsCacheReader(context, track, fromCache));
	}

	public LyricsFetcher(Context context, Track track, boolean autoSave, boolean fromCache) {
		super(context, track, autoSave);
		setReader(new LyricsCacheReader(context, track, fromCache));
	}

	public LyricsFetcher(Context context, Track track, String source, boolean fromCache) {
		super(context, track, source);
		setReader(new LyricsCacheReader(context, track, fromCache));
	}

	public LyricsFetcher(Context context, Track track, String source,
			Boolean autoSave, boolean fromCache) {
		super(context, track, source, autoSave);
		setReader(new LyricsCacheReader(context, track, fromCache));
	}

	public LyricsFetcher(Context context, Track track, String[] sources, boolean fromCache) {
		super(context, track, sources);
		setReader(new LyricsCacheReader(context, track, fromCache));
	}

	public LyricsFetcher(Context context, Track track, String[] sources,
			Boolean autoSave, boolean fromCache) {
		super(context, track, sources, autoSave);
		setReader(new LyricsCacheReader(context, track, fromCache));
	}

	@Override
	public void fetchLyrics() {
		if (!canFetchLyrics()) {
			Message message = Message.obtain(mLyrHandler , NO_CONNECTION);
    		mLyrHandler.sendMessage(message);
			return;
		}
		mProviderIndex = -1;
		useNextProvider();
	}
}
