package com.example.lyricsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.jlyr.util.LyricReader;
import com.jlyr.util.Track;

public class LyricsCacheReader extends LyricReader {
	private static final int MAX_SAVED_LYRICS = 3;
	private Context mContext;
	private File indexFile;

	public LyricsCacheReader(Context context, Track track, boolean fromCache) {
		super(track, context, fromCache);
		this.mContext = context;
		indexFile = new File(getLyricsDir(mContext), "JLyr/lyrIndex.str");
	}
	
	@Override
	public String[] getContent() {//overriden to reindex last viewed lyrics
		String[] content = super.getContent();
		if (content[1] != null) {//has cached lyrics
			reindex();
		}
		return content;
	}

	@Override
	public boolean save(String response, String source) {
		boolean isSaved = super.save(response, source);
		if (isSaved) {
			reindex();
		}
		return isSaved;
	}

	private void reindex() {
		List<String> lyricsFile = null;// store titles from file
		String savedFile = null;
		int savedIndex = 0;
		Log.v(TAG, "file saved");
		savedFile = getFileNameForTrack(getTrack());
		lyricsFile = listCachedFiles();
		Log.v(TAG, "indexing... ");
		for (String filename : lyricsFile) {
			Log.v(TAG, "file "+filename);
		}
		savedIndex = lyricsFile.indexOf(savedFile);
		if (savedIndex != -1) {// if in list
			Log.v(TAG, "in list");
			lyricsFile.remove(savedIndex);
		}
		if (lyricsFile.size() == MAX_SAVED_LYRICS) {
			Log.v(TAG, "deleting first file: "+lyricsFile.get(0));
			lyricsFile.remove(0); // deleteFirst
			File toDelete = new File(getLyricsDir(mContext), "JLyr/"+lyricsFile.get(0)+ ".txt");
			if (toDelete.delete())
				Log.v(TAG, "file deleted");
		}
		lyricsFile.add(savedFile);
		Log.v(TAG, "reindexing... ");
		for (String filename : lyricsFile) {
			Log.v(TAG, "file "+filename);
		}
		saveIndexFile(lyricsFile);
	}

	private List<String> listCachedFiles() {
		List<String> cachedFiles = new LinkedList<String>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(
					indexFile));
			try {
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					cachedFiles.add(line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return cachedFiles;
	}

	private void saveIndexFile(List<String> toSave) {
		BufferedWriter bufferedWriter = null;
		try {
			// Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(indexFile));
			for (String savedFile : toSave) {
				bufferedWriter.write(savedFile);
				bufferedWriter.newLine();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}