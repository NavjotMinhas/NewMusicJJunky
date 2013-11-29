package com.musicjunky.extensions.lib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ExtensionActivity extends Activity {

	private static final String TAG=ExtensionActivity.class.getName();
	
	private ServiceConnection serviceConnection=new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			Log.e(TAG, "connected");
			IPlugin plugin=IPlugin.Stub.asInterface(arg1);
			try {
				plugin.onInitialize(getHostInterface(), true);
				//Step 2
				Intent intent=plugin.auth(getComponentName(),android.os.Process.myUid());
				bindService(intent, serviceConnection2, BIND_AUTO_CREATE);
			} catch (RemoteException e) {
				Log.e(TAG,"RemoteException",e);
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
		
	
	private ServiceConnection serviceConnection2=new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			Log.e(TAG, "connected2");
			IApi api=IApi.Stub.asInterface(arg1);
			try {
				api.api("Letter");
				//Step 2
				
			} catch (RemoteException e) {
				Log.e(TAG,"RemoteException",e);
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private IHostApp getHostInterface(){
		return new IHostApp.Stub() {
			
			@Override
			public String getMsg() throws RemoteException {
				return "Hello Message";
			}
		};
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=new Intent(getApplicationContext(),ExtensionService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		
	}

	@Override
	protected void onDestroy() {
		unbindService(serviceConnection);
		unbindService(serviceConnection2);
		super.onDestroy();
	}
	
}
