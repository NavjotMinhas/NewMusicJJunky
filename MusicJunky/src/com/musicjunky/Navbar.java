package com.musicjunky;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.musicjunky.adpater.SongAdapter;
import com.musicjunky.adpater.ViewPageAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class Navbar extends SherlockFragmentActivity {

	private ViewPager mViewPager;
	private ViewPageAdapter pageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navbar);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		pageAdapter = new ViewPageAdapter(this, mViewPager);

		Cursor cursor = Database.getAllItems(getContentResolver(),
				Database.SONG, MediaStore.Audio.Media.TITLE + " ASC",
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ARTIST);
		/*while(cursor.moveToNext()){
			Log.e("TAG", cursor.getString(0)+" "+cursor.getString(1));
		}*/
		SongAdapter songAdapter = SongAdapter.getInstance(
				getApplicationContext(), cursor);

		pageAdapter.addTab(null, "Songs", songAdapter);
		pageAdapter.addTab(null, "Artists", songAdapter);
		pageAdapter.addTab(null, "Albums", songAdapter);
		TabPageIndicator tabIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		tabIndicator.setViewPager(mViewPager);

	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.menu.action_bar_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
