package com.musicjunky.player;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.musicjunky.R;
import com.musicjunky.player.android.IMediaPlaybackService;
import com.musicjunky.player.android.MediaPlaybackService;

public class PlaybackActivity extends Activity {

	private ImageButton mPlayPauseButton;
	private ImageButton mPreviousButton;
	private ImageButton mNextButton;
	private ImageButton mRepeatButton;
	private ImageButton mShuffleButton;
	
	private TextView mSongTitle;
	private TextView mArtistName;
	private TextView mCurrentPosition;
	private TextView mEndPosition;
	
	private ImageView mAlbumArt;
	
	private SeekBar mSeekBar;
	
	
	private IMediaPlaybackService mPlaybackService;
	
	private final static String TAG = PlaybackActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.player);
		init();

	}

	private void init() {
		Typeface font = Typeface.createFromAsset(getAssets(),
				"fonts/robotolight.ttf");

		mPlayPauseButton=(ImageButton)findViewById(R.id.play_btn);
		mPlayPauseButton.setOnClickListener(onPlayPauseListener);
		
		mSongTitle = (TextView) findViewById(R.id.song_title);
		mSongTitle.setTypeface(font);

		mArtistName = (TextView) findViewById(R.id.artist_name);
		mArtistName.setTypeface(font);

		mCurrentPosition = (TextView) findViewById(R.id.currentPos);
		mEndPosition = (TextView) findViewById(R.id.endPos);

		mAlbumArt = (ImageView) findViewById(R.id.album_art);
		
		mSeekBar=(SeekBar)findViewById(R.id.media_seekbar);
		mSeekBar.setOnSeekBarChangeListener(mSeekListener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getApplicationContext().startService(new Intent(getApplicationContext(),MediaPlaybackService.class));
		if(getApplicationContext().bindService(new Intent(getApplicationContext(),MediaPlaybackService.class), connection, Service.BIND_AUTO_CREATE)){
			Log.e(TAG, "bind service");
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		if(connection !=null){
			unbindService(connection);
			mPlaybackService=null;
		}
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private View.OnClickListener onPreviousButtonListener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mPlaybackService!=null){
				try {
					mPlaybackService.prev();
				} catch (RemoteException e) {
					Log.e(TAG,"RemoteException",e);
				}
			}
		}
	};
	
	private View.OnClickListener onPlayPauseListener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mPlaybackService!=null){
				try {
					if(mPlaybackService.isPlaying()){
						mPlaybackService.pause();
						Log.e(TAG, "pause");
					}else{
						mPlaybackService.play();
						Log.e(TAG, "play");
					}
				} catch (RemoteException e) {
					Log.e(TAG,"RemoteException",e);
				}
			}
		}
	};
	
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
			
	};
	
	private ServiceConnection connection=new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mPlaybackService=IMediaPlaybackService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mPlaybackService=null;
			
		}
		
	};
}
