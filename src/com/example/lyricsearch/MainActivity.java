package com.example.lyricsearch;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.jlyr.util.ProvidersCollection;

public class MainActivity extends Activity {

	private EditText artistInput;
	private EditText titleInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        titleInput = (EditText) findViewById(R.id.titleInput);
        artistInput = (EditText) findViewById(R.id.artistInput);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	public void onClick(View v) {
		String artist = null;
		String title = null;
		
		artist = artistInput.getText().toString();
		title = titleInput.getText().toString();
		
		String[] sources = getSources();
		Intent intent = new Intent(this, LyricViewer.class);
    	intent.putExtra("Track.title", title);
		intent.putExtra("Track.artist", artist);
		intent.putExtra("LyricsSources", sources);
    	startActivity(intent);
	}
	
	private String[] getSources() {
		//sourcesList.toArray(new String[] {});
		List<String> providersCollection = ProvidersCollection.getAll();
		int providersCount = providersCollection.size();
		String[] sources = new String[providersCount];
		for (int i = 0; i < providersCount; ++i)
			sources[i] = providersCollection.get(i); 
		return sources;
	}
}
