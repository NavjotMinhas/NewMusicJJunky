package com.musicjunky.fragment;

import java.io.File;

import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.musicjunky.R;
import com.musicjunky.player.android.IMediaPlaybackService;
import com.musicjunky.player.android.MediaPlaybackService;

public class PlaybackPlayerFragment extends Fragment {

	private ImageButton mPlayPauseButton;
	private ImageButton mPreviousButton;
	private ImageButton mNextButton;
	private ImageButton mRepeatButton;
	
	private TextView mSongTitle;
	private TextView mArtistName;
	private TextView mCurrentPosition;
	private TextView mBigCurrentPosition;
	private TextView mEndPosition;
	
	private ImageView mAlbumArt;
		
	private SeekBar mSeekBar;
	
	private long mPosOverride;
	private long mDuration;
	private int mRepeatMode;
	
	private Handler mHandler;
	
	private HandlerThread mHandlerThread;
	private Looper mLooper;
	private AsyncHandler mAsyncHandler;
	
	private IMediaPlaybackService mPlaybackService;
	
	private final static String TAG = PlaybackPlayerFragment.class.getName();
	
	private final static int REFRESH=0;
	
	class AsyncHandler extends Handler{
		
		public AsyncHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			long refresh=PlaybackPlayerFragment.this.refreshNow();
			queueNextRefresh(refresh);
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.player_fragment, container, true);
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/robotolight.ttf");
		
		Typeface thinFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/robotothin.ttf");
		
		Typeface boldFont = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/robotobold.ttf");
		
		mPlayPauseButton=(ImageButton)view.findViewById(R.id.play_btn);
		mPlayPauseButton.setOnClickListener(onPlayPauseListener);
		
		mSongTitle = (TextView) view.findViewById(R.id.song_title);
		mSongTitle.setTypeface(font);

		mArtistName = (TextView) view.findViewById(R.id.artist_name);
		mArtistName.setTypeface(boldFont);
		
		mBigCurrentPosition=(TextView)view.findViewById(R.id.big_song_pos);
		mBigCurrentPosition.setTypeface(thinFont);
		mCurrentPosition = (TextView) view.findViewById(R.id.currentPos);
		mEndPosition = (TextView) view.findViewById(R.id.endPos);
		
		mAlbumArt = (ImageView) view.findViewById(R.id.album_art);
		
		mSeekBar=(SeekBar)view.findViewById(R.id.media_seekbar);
		mSeekBar.setMax(1000);
		mSeekBar.setOnSeekBarChangeListener(mSeekListener);
		
		mPreviousButton=(ImageButton)view.findViewById(R.id.previous_btn);
		mPreviousButton.setOnClickListener(onPrevious);
		
		mNextButton=(ImageButton)view.findViewById(R.id.next_btn);
		mNextButton.setOnClickListener(onNext);
		
		mHandler=new Handler();
		
		mHandlerThread=new HandlerThread("AsyncWorkerThread");
		mHandlerThread.start();
		mLooper=mHandlerThread.getLooper();
		mAsyncHandler=new AsyncHandler(mLooper);
		
		mRepeatButton=(ImageButton)view.findViewById(R.id.repeat_btn);
		mRepeatButton.setOnClickListener(toggleRepeat);
				
		return view;
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		
		getActivity().getApplicationContext().startService(new Intent(getActivity().getApplicationContext(),MediaPlaybackService.class));
		if(getActivity().getApplicationContext().bindService(new Intent(getActivity().getApplicationContext(),MediaPlaybackService.class), connection, Service.BIND_AUTO_CREATE)){
			Log.e(TAG, "bind service");
		}
		IntentFilter filter=new IntentFilter();
		filter.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
		filter.addAction(MediaPlaybackService.META_CHANGED);
		getActivity().registerReceiver(songReceiver, filter);
		updateInfo();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(mPlaybackService!=null){
			getActivity().getApplicationContext().unbindService(connection);
		}
		mPlaybackService=null;
		if(songReceiver!=null){
			getActivity().unregisterReceiver(songReceiver);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLooper.quit();
	}
	
	private BroadcastReceiver songReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(action.equals(MediaPlaybackService.META_CHANGED)){
				updateInfo();
				onUpdateButtons();
				queueNextRefresh(1);
			}else if(action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)){
				onUpdateButtons();
			}
			
		}
		
	};
	
	private View.OnClickListener onNext=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mPlaybackService!=null){
				try {
					mPlaybackService.next();
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
					if(mPlaybackService.position()<3000){
						mPlaybackService.prev();
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
					}else{
						mPlaybackService.play();
						queueNextRefresh(0);
					}
				} catch (RemoteException e) {
					Log.e(TAG,"RemoteException",e);
				}
			}
		}
	};
		
	private View.OnClickListener toggleRepeat=new View.OnClickListener() {
		
		private int repeatCounter=0;
		
		@Override
		public void onClick(View v) {
			if(mPlaybackService!=null){
				try {
					repeatCounter++;
					mRepeatMode=repeatCounter%3;
					mPlaybackService.setRepeatMode(mRepeatMode);
					switch(mRepeatMode){
					case MediaPlaybackService.REPEAT_NONE:
						mRepeatButton.setBackgroundColor(getResources().getColor(R.color.secondary_media_controller));
						break;
					case MediaPlaybackService.REPEAT_CURRENT:
						mRepeatButton.setBackgroundColor(Color.parseColor("#f7912a"));
						break;
					case MediaPlaybackService.REPEAT_ALL:
						mRepeatButton.setBackgroundColor(Color.parseColor("#f7912a"));
						break;
				}
				} catch (RemoteException e) {
					Log.e(TAG,"RemoteException",e);
				}
			}
			
		}
	};
		
	private void onUpdateButtons(){
		if(mPlaybackService!=null){
			try {
				if(mPlaybackService.isPlaying()){
					mPlayPauseButton.setImageResource(R.drawable.pause);
				}else{
					mPlayPauseButton.setImageResource(R.drawable.play);
				}
			} catch (RemoteException e) {
				Log.e(TAG,"RemoteException",e);
			}
		}
	}
	
	
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
		if(mPlaybackService==null){
			return;
		}
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
			onUpdateButtons();
		} catch (RemoteException e) {
			Log.e(TAG,"RemoteException",e);
		}
	}
	
	private void queueNextRefresh(long next){
		if(mPlaybackService==null){
			return;
		}
		try {
			if(mPlaybackService.isPlaying()){
				Message message=mAsyncHandler.obtainMessage(REFRESH);
			
				//remove anything that was in queue previously
				mAsyncHandler.removeMessages(REFRESH);
				mAsyncHandler.sendMessageDelayed(message, next);
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
				final int progress= (int)(1000 * pos/mDuration);
				if(mPlaybackService.isPlaying()){
					final String text=makeTimeString(mPlaybackService.position());
					mHandler.post(new Runnable(){
						@Override
						public void run() {
							mSeekBar.setProgress(progress);
							mCurrentPosition.setText(text);
							mBigCurrentPosition.setText(text);	
						}
					});
				}else{
					return 500;
				}
			}else{
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						mBigCurrentPosition.setText("--:--");
						mCurrentPosition.setText("--:--");
						mSeekBar.setProgress(1000);
					}
				});
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
			String trackName= mPlaybackService.getTrackName();
			if(trackName.length()>28){
				trackName=trackName.substring(0,28);
			}
			mSongTitle.setText(trackName);
			String artistName=mPlaybackService.getArtistName();
			if(artistName.equals(MediaStore.UNKNOWN_STRING)){
				artistName="<unknown>";
			}
			mArtistName.setText(mPlaybackService.getArtistName());
			long albumID=mPlaybackService.getAlbumId();
			if(albumID!=-1){
				
			}else{
				
			}
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
