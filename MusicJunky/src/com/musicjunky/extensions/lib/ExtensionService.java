package com.musicjunky.extensions.lib;

import java.util.HashMap;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

public class ExtensionService extends Service {

	private static final String TAG=ExtensionService.class.getName();
	private IHostApp mHost;
	private boolean mInitialized;
	private boolean mIsWorldReadable=true;
	private HandlerThread mHandlerThread;
	private Looper mServiceLooper;
	private Handler mHandler;
	public static HashMap<Integer, AuthToken> cache=new HashMap<Integer, AuthToken>();
	
	private IPlugin.Stub plugin=new IPlugin.Stub() {
		
		
		@Override
		public void onInitialize(IHostApp host, boolean isReconnect)
				throws RemoteException {
			mHost=host;
			if(!mInitialized){
				ExtensionService.this.onInitialize(isReconnect);
				mInitialized=true;
			}
			
		}

		@Override
		public void onUpdate(final int reason) throws RemoteException {
			if(!mInitialized){
				return;
			}
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					ExtensionService.this.onUpdate(reason);
				}
			});
			
		}

		@Override
		public Intent auth(ComponentName name, int pid) throws RemoteException {
			if(!mInitialized){
				return null;
			}
			if(!mIsWorldReadable){
				return null;
			}
			if(getApiServiceName()==null){
				return null;
			}
			AuthToken authToken=new AuthToken();
			authToken.pid=pid;
			authToken.componentName=name;
						
			Intent intent=new Intent(getApplicationContext(), getApiServiceName());
			intent.putExtra("callingPid", pid);
			if(cache.get(pid)!=null){
				cache.put(pid, authToken);
			}
			return intent;
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return plugin;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG,"onCreate");
		mHandlerThread=new HandlerThread("Extension: "+TAG);
		mHandlerThread.start();
		mServiceLooper=mHandlerThread.getLooper();
		mHandler=new Handler(mServiceLooper);
		
	}
	
	protected void onInitialize(boolean isReconnect){

	}
	
	protected void onUpdate(int reason){
		
	}
	
	protected Class<ExtensionApiService> getApiServiceName(){
		return ExtensionApiService.class;
	}
	
	public static class AuthToken{
		int pid;
		ComponentName componentName;
	}
}
