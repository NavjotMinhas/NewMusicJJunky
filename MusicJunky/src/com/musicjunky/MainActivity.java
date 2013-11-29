package com.musicjunky;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.musicjunky.download.DownloadService;
import com.musicjunky.extensions.ExtensionManager;

public class MainActivity extends Activity {
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.player);
		/*Typeface font=Typeface.createFromAsset(getAssets(), "fonts/robotolight.ttf");
		TextView song_title=(TextView)findViewById(R.id.song_title);
		song_title.setTypeface(font);
		
		TextView artist_name=(TextView)findViewById(R.id.artist_name);
		artist_name.setTypeface(font);*/
		
		//new Database(getContentResolver());
		
		ExtensionManager manager=ExtensionManager.getInstance(getApplicationContext());
		manager.getInstalledExtensions();
		
		/*try {
			File file=new File(Environment.getExternalStorageDirectory(),"test.m4a");
			Log.e("test", ""+file.exists());
			Intent intent=new Intent(PlaybackService.ACTION_PLAY);
			Song song=new Song(file);
			intent.putExtra("song",song);
			startService(intent);
		} catch (Exception e) {
			Log.e("test", "Exception", e);
		}*/
	}
	
	public void onCancelDownload(View v){
		Intent stopIntent=new Intent(DownloadService.ACTION_REMOVE);
		stopIntent.putExtra("listPos", 0);
		startService(stopIntent);
	}
	
	public void onStopDownload(View v){
		Intent stopIntent=new Intent(DownloadService.ACTION_PAUSE);
		stopIntent.putExtra("listPos", 0);
		startService(stopIntent);
	}
	
	public void onResumeDownload(View v){
		Intent resumeIntent=new Intent(DownloadService.ACTION_RESUME);
		resumeIntent.putExtra("listPos", 0);
		startService(resumeIntent);
	}
	
	public void onDownload(View v){
		Log.e("Test","On Download");
		Intent intent=new Intent(DownloadService.ACTION_DOWNLOAD);
		intent.putExtra("url","http://www.hollywoodreporter.com/sites/default/files/2013/05/the_great_gatsby.jpg");
		intent.putExtra("downloadDirectory",GlobalSettings.DOWNLOAD_DIRECTORY_PATH);
		intent.putExtra("fileName","test.jpg");
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
}
