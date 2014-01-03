package com.musicjunky.adpater;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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

public class AlbumAdapter extends SimpleCursorAdapter {

private Context mContext;
	
	private Handler mHandler;
	
	private HandlerThread mHandlerThread;
	private Looper mLooper;
	private AlbumHandler mAsyncHandler;
	
	private final static int ALBUM_HANDLER=0;
	
	private Typeface font;
	
	public AlbumAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		this.mContext = context;
		font = Typeface.createFromAsset(context.getAssets(),
				"fonts/robotothin.ttf");
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
	
	public static AlbumAdapter getInstance(Context context, Cursor cursor){
		AlbumAdapter adapter=new AlbumAdapter(context, R.layout.grid_item, cursor, cursor.getColumnNames(), new int[]{R.id.item_title});
		return adapter;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView = (View) inflater.inflate(R.layout.grid_item, parent, false);
		ImageView albumArtView = (ImageView) gridView
				.findViewById(R.id.item_image);
		TextView albumTextView = (TextView) gridView
				.findViewById(R.id.album_title);
		TextView artistTextView = (TextView) gridView
				.findViewById(R.id.artist_name);
		TextView descriptionTextView = (TextView) gridView
				.findViewById(R.id.description);
		
		String albumName=getCursor().getString(getCursor().getColumnIndex(MediaStore.Audio.Albums.ALBUM));
		int numOfSongs=getCursor().getInt(getCursor().getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
		long albumID=getCursor().getLong(getCursor().getColumnIndex(MediaStore.Audio.Albums._ID));
		
		Bitmap img;
		if(albumID>=0){
			img = MediaUtils.getArtwork(mContext, -1, albumID,false);
			if(img == null){
				img = MediaUtils.getArtwork(mContext, -1, -1);
			}
		}else{
			img = MediaUtils.getArtwork(mContext, -1, -1);
		}
		albumArtView.setImageBitmap(img);
	
		if (albumName != null) {
			albumTextView.setText(albumName);
		}
		
		return gridView;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView albumArtView = (ImageView) view
				.findViewById(R.id.item_image);
		TextView albumTextView = (TextView) view
				.findViewById(R.id.album_title);
		TextView artistTextView = (TextView) view
				.findViewById(R.id.artist_name);
		TextView descriptionTextView = (TextView) view
				.findViewById(R.id.description);
		
		String albumName=getCursor().getString(getCursor().getColumnIndex(MediaStore.Audio.Albums.ALBUM));
		int numOfSongs=getCursor().getInt(getCursor().getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
		long albumID=getCursor().getLong(getCursor().getColumnIndex(MediaStore.Audio.Albums._ID));
		
		Bitmap img;
		if(albumID>=0){
			img = MediaUtils.getArtwork(mContext, -1, albumID,false);
			if(img == null){
				img = MediaUtils.getArtwork(mContext, -1, -1);
			}
		}else{
			img = MediaUtils.getArtwork(mContext, -1, -1);
		}
		albumArtView.setImageBitmap(img);
	
		if (albumName != null) {
			titleView.setText(albumName);
		}
	}
	
}
