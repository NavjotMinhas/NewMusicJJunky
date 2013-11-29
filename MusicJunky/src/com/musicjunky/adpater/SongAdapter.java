package com.musicjunky.adpater;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.musicjunky.MediaUtils;
import com.musicjunky.R;

public class SongAdapter extends SimpleCursorAdapter {

	private Context mContext;

	@SuppressWarnings("deprecation")
	public SongAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to);
		this.mContext = context;
	}
	
	public static SongAdapter getInstance(Context context, Cursor curosr){
		SongAdapter adapter=new SongAdapter(context, R.layout.list_item, curosr, new String[]{MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST}, new int[]{R.id.song_title_list_item,R.id.artist_name_list_item});
		return adapter;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View listView = (View) inflater.inflate(R.layout.list_item, parent, false);
		ImageView albumArtView = (ImageView) listView
				.findViewById(R.id.album_art_list_item);
		TextView songTitleView = (TextView) listView
				.findViewById(R.id.song_title_list_item);
		TextView artistNameView = (TextView) listView
				.findViewById(R.id.artist_name_list_item);
		
		long songID=getCursor().getLong(getCursor().getColumnIndex(MediaStore.Audio.Media._ID));
		long albumID=getCursor().getLong(getCursor().getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
		if (albumID >= 0) {
			Drawable img = MediaUtils.getCachedArwork(mContext, songID, albumID);
			albumArtView.setImageDrawable(img);
		}
		String songTitle = getCursor().getString(
				getCursor().getColumnIndex(MediaStore.Audio.Media.TITLE));
		songTitleView.setText(songTitle);
		String artistName = getCursor().getString(
				getCursor().getColumnIndex(MediaStore.Audio.Media.ARTIST));
		if (artistName != null) {
			artistNameView.setText(artistName);
		}
		
		return listView;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView albumArtView = (ImageView) view
				.findViewById(R.id.album_art_list_item);
		TextView songTitleView = (TextView) view
				.findViewById(R.id.song_title_list_item);
		TextView artistNameView = (TextView) view
				.findViewById(R.id.artist_name_list_item);
		
		long songID=getCursor().getLong(getCursor().getColumnIndex(MediaStore.Audio.Media._ID));
		long albumID=getCursor().getLong(getCursor().getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
		if (albumID >= 0) {
			Drawable img = MediaUtils.getCachedArwork(mContext, songID, albumID);
			albumArtView.setImageDrawable(img);
		}
		String songTitle = getCursor().getString(
				getCursor().getColumnIndex(MediaStore.Audio.Media.TITLE));
		songTitleView.setText(songTitle);
		String artistName = getCursor().getString(
				getCursor().getColumnIndex(MediaStore.Audio.Media.ARTIST));
		if (artistName != null) {
			artistNameView.setText(artistName);
		}
	}
}
