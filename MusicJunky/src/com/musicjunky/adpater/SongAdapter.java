package com.musicjunky.adpater;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
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
	
	private Handler mHandler;
	
	private HandlerThread mHandlerThread;
	private Looper mLooper;
	private AlbumHandler mAsyncHandler;
	
	private final static int ALBUM_HANDLER=0;

	public SongAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		this.mContext = context;
		mHandler=new Handler();
		mHandlerThread=new HandlerThread("AlbumLoader");
		mHandlerThread.start();
		mLooper=mHandlerThread.getLooper();
		mAsyncHandler=new AlbumHandler(mLooper);
	}
	
	class AlbumHandler extends Handler{
		
		private ImageView albumArtView;
		private Bitmap img ;
		private int albumID;
		
		public AlbumHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == ALBUM_HANDLER && albumID!=msg.arg2){
				removeMessages(ALBUM_HANDLER);
				
				if(msg.arg2>=0){
					img = MediaUtils.getArtwork(mContext, msg.arg1, msg.arg2,false);
					if(img == null){
						img = MediaUtils.getArtwork(mContext, msg.arg1, -1);
					}
				}else{
					img = MediaUtils.getArtwork(mContext, msg.arg1, -1);
				}
						
				albumArtView=(ImageView)msg.obj;
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						albumArtView.setImageBitmap(img);
						
					}
				});
			}
		}
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

		Bitmap img;
		if(albumID>=0){
			img = MediaUtils.getArtwork(mContext, songID, albumID,false);
			if(img == null){
				img = MediaUtils.getArtwork(mContext, songID, -1);
			}
		}else{
			img = MediaUtils.getArtwork(mContext, songID, -1);
		}
		albumArtView.setImageBitmap(img);
		
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

		Message message=mAsyncHandler.obtainMessage(ALBUM_HANDLER);
		message.obj=albumArtView;
		message.arg1=(int)songID;
		message.arg2=(int)albumID;
		mAsyncHandler.sendMessage(message);
		
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
