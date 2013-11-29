package com.musicjunky;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class ExtensionActivity extends Activity {

	private ServiceConnection connection=new ServiceConnection(){
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			

		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
			
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*PackageManager pm=getPackageManager();
		List<ResolveInfo> list=pm.queryIntentServices(new Intent(ExtensionManager.ACTION_EXTENSION), 0);
		for(ResolveInfo resolveInfo: list){
			Intent intent=new Intent();
			intent.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName,resolveInfo.serviceInfo.name));
			bindService(intent, connection, Activity.BIND_AUTO_CREATE);
		}*/
		/*SongTimeline timeline=SongTimeline.getSongInstance(getContentResolver());
		SongTimelineListener listener=new SongTimelineListener(){
			@Override
			public void onSongChanged(SongToPosition currentSong,
					SongToPosition nextSong, SongToPosition previousSong) {
				Log.e("-------------", "--------------------------");
				Log.e("Current Song", currentSong.song.getTitle());
				Log.e("Previous Song", previousSong.song.getTitle());
				Log.e("Next Song", nextSong.song.getTitle());
				Log.e("-------------", "--------------------------");
			}
		};
		timeline.setShuffleOn(true);
		timeline.addListener(listener);
		timeline.changeSongs(SongTimeline.ACTION_NEXT);
		timeline.changeSongs(SongTimeline.ACTION_NEXT);
		timeline.changeSongs(SongTimeline.ACTION_NEXT);
		timeline.changeSongs(SongTimeline.ACTION_NEXT);
		
		timeline.changeSongs(SongTimeline.ACTION_PREVIOUS);
		timeline.changeSongs(SongTimeline.ACTION_PREVIOUS);
		timeline.changeSongs(SongTimeline.ACTION_PREVIOUS);
		timeline.changeSongs(SongTimeline.ACTION_PREVIOUS);*/

	}
	
}
