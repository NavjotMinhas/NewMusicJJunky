package com.musicjunky;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.musicjunky.adpater.AlbumAdapter;
import com.musicjunky.adpater.SongAdapter;
import com.musicjunky.adpater.ViewPageAdapter;
import com.musicjunky.fragment.PageFragment;
import com.viewpagerindicator.TabPageIndicator;

public class Navbar extends SherlockFragmentActivity {

	private ViewPager mViewPager;
	private ViewPageAdapter pageAdapter;

	private Cursor mSongCursor;
	private Cursor mAlbumCursor;
	private Cursor mArtistCursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navbar);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		pageAdapter = new ViewPageAdapter(this, mViewPager);

		mSongCursor = Database.getAllItems(getContentResolver(),
				Database.SONG, MediaStore.Audio.Media.TITLE + " ASC",
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ARTIST);
		
		mArtistCursor = Database.getAllItems(getContentResolver(),
				Database.ARTIST, MediaStore.Audio.Artists.ARTIST + " ASC",
				MediaStore.Audio.Artists._ID,
				MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
				MediaStore.Audio.Media.ARTIST);
		
		mAlbumCursor = Database.getAllItems(getContentResolver(),
				Database.ALBUM, MediaStore.Audio.Albums.ALBUM + " ASC",
				MediaStore.Audio.Albums._ID,
				MediaStore.Audio.Albums.NUMBER_OF_SONGS,
				MediaStore.Audio.Albums.ARTIST,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Albums.ALBUM);
		
		SongAdapter songAdapter = SongAdapter.getInstance(
				getApplicationContext(), mSongCursor);
		
		AlbumAdapter albumAdapter=AlbumAdapter.getInstance(
				getApplicationContext(), mAlbumCursor);
	
		
		pageAdapter.addTab(null, "Songs", songAdapter, PageFragment.LIST_VIEW);
		pageAdapter.addTab(null, "Artists", songAdapter, PageFragment.GRID_VIEW);
		pageAdapter.addTab(null, "Albums", albumAdapter, PageFragment.GRID_VIEW);
		TabPageIndicator tabIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		tabIndicator.setViewPager(mViewPager);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSongCursor.close();
		mArtistCursor.close();
		mAlbumCursor.close();
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.menu.action_bar_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
