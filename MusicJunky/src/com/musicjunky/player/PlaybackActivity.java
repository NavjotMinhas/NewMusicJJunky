package com.musicjunky.player;

import java.io.File;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
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
	
	private long mPosOverride;
	private long mDuration;
	
	private ProgressUpdater mProgressUpdater;
	
	private IMediaPlaybackService mPlaybackService;
	
	private final static String TAG = PlaybackActivity.class.getName();
	
	private final static int REFRESH=0;
	
	
	class ProgressUpdater extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			long refresh=PlaybackActivity.this.refreshNow();
			queueNextRefresh(refresh);
		}
		
	}
	
	
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
		mSeekBar.setMax(1000);
		mSeekBar.setOnSeekBarChangeListener(mSeekListener);
		
		mPreviousButton=(ImageButton)findViewById(R.id.previous_btn);
		mPreviousButton.setOnClickListener(onPrevious);
		
		mNextButton=(ImageButton)findViewById(R.id.next_btn);
		mNextButton.setOnClickListener(onNext);
		
		
		mProgressUpdater=new ProgressUpdater();
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
		super.onStop();
		if(mPlaybackService!=null){
			getApplicationContext().unbindService(connection);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private View.OnClickListener onNext=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mPlaybackService!=null){
				try {
					mPlaybackService.next();
					updateInfo();
				} catch (RemoteException e) {
					Log.e(TAG,"RemoteException",e);
				}
			}
		}
	};
	
	private View.OnClickListener onPrevious= new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			if(mPlaybackService!=null){
				try {
					if(mPlaybackService.position()<2000){
						mPlaybackService.prev();
						updateInfo();
					}else{
						mPlaybackService.seek(0);
						if(!mPlaybackService.isPlaying()){
							mPlaybackService.play();
						}
					}
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
	
	private boolean mUsingTouch;
	
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser){
				mPosOverride= mDuration*progress/1000;
				try {
					mPlaybackService.seek(mPosOverride);
					queueNextRefresh(0);
				} catch (RemoteException e) {
					Log.e(TAG,"RemoteException",e);
				}
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mUsingTouch=true;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mUsingTouch=false;
			mPosOverride=-1;
		}
			
	};
	
	private ServiceConnection connection=new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mPlaybackService=IMediaPlaybackService.Stub.asInterface(service);
			startPlayback();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mPlaybackService=null;
			
		}
		
	};
	
	private void startPlayback(){
		try {
			File file=new File(Environment.getExternalStorageDirectory(),"test.m4a");
			Uri.Builder builder=new Uri.Builder();
			builder.path(file.getPath());
			Uri uri= builder.build();
			mPlaybackService.stop();
			mPlaybackService.openFile(uri.toString());
			mPlaybackService.play();
			long next=refreshNow();
			queueNextRefresh(next);
			updateInfo();
		} catch (RemoteException e) {
			Log.e(TAG,"RemoteException",e);
		}
	}
	
	private void queueNextRefresh(long next){
		try {
			if(mPlaybackService.isPlaying()){
				Message message=mProgressUpdater.obtainMessage(REFRESH);
			
				//remove anything that was in queue previously
				mProgressUpdater.removeMessages(REFRESH);
				mProgressUpdater.sendMessageDelayed(message, next);
			}
		} catch (RemoteException e) {
			Log.e(TAG,"RemoteException",e);
		}
	}
	
	private long refreshNow(){
		if(mPlaybackService==null){
			return 500;
		}
		try {
			long pos=mPosOverride < 0? mPlaybackService.position(): mPosOverride;
			if(pos>=0 && mDuration>0){
				int progress= (int)(1000 * pos/mDuration);
				mSeekBar.setProgress(progress);
				if(mPlaybackService.isPlaying()){
					mCurrentPosition.setText(makeTimeString(mPlaybackService.position()));
					mCurrentPosition.setVisibility(View.VISIBLE);
				}else{
					mCurrentPosition.setVisibility(mCurrentPosition.getVisibility()==View.INVISIBLE?View.VISIBLE:View.INVISIBLE);
					return 500;
				}
			}else{
				mCurrentPosition.setText("--:--");
				mSeekBar.setProgress(1000);
			}
			
			long numOfMilliSecondsRemaining=1000-(pos%1000);
			int width =mSeekBar.getWidth();
			if(width==0){
				width=320;
			}
			
			long refreshRatePerUnit=mDuration/width;
			 
			if(refreshRatePerUnit>numOfMilliSecondsRemaining){
				return numOfMilliSecondsRemaining;
			}else{
				return refreshRatePerUnit;
			}
			
		} catch (RemoteException e) {
			Log.e(TAG, "RemoteException", e);
		}
		
		return 500;
	}
	
	private void updateInfo(){
		if(mPlaybackService==null){
			return;
		}
		try {
			String path=mPlaybackService.getPath();
			if(path==null){
				return;
			}
			((View)mArtistName.getParent()).setVisibility(View.VISIBLE);
			String trackName= mPlaybackService.getTrackName();
			if(trackName.length()>32){
				trackName=trackName.substring(0,32);
			}
			mSongTitle.setText(trackName);
			String artistName=mPlaybackService.getArtistName();
			if(artistName.equals(MediaStore.UNKNOWN_STRING)){
				artistName="<unknown>";
			}
			mArtistName.setText(mPlaybackService.getArtistName());
			long albumID=mPlaybackService.getAlbumId();
			mDuration=mPlaybackService.duration();
			mEndPosition.setText(makeTimeString(mDuration));
			mPosOverride=-1;
		} catch (RemoteException e) {
			Log.e(TAG,"RemoteException",e);
		}
	}
	
	private String makeTimeString(long duration){
		duration=duration/1000;
		if(duration>3600){
			long hours=duration/3600;
			long minutes=(duration-hours*3600)/60;
			long seconds=duration-(hours*3600+minutes*60);
			String time=hours+":";
			if(minutes<=9){
				time=time+"0"+minutes+":";
			}else{
				time=time+minutes+":";
			}
			if(seconds<=9){
				time=time+"0"+seconds;
			}else{
				time=time+seconds;
			}
			return time;
		}else{
			long minutes=duration/60;
			long seconds=duration-(minutes*60);
			String time=minutes+":";
			if(seconds<=9){
				time=time+"0"+seconds;
			}else{
				time=time+seconds;
			}
			return time;
		}
	}
	
}
