package com.musicjunky.extensions.lib;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

public class ExtensionApiService extends Service {

	private static final String TAG=ExtensionApiService.class.getName();
	private HandlerThread mHandlerThread;
	private Looper mServiceLooper;
	private Handler mHandler;
	
	private IApi.Stub api=new IApi.Stub() {

		@Override
		public void asyncApi(final String str) throws RemoteException {
			int pid=getCallingPid();
			if(ExtensionService.cache.containsKey(pid)){
				//it means it was a approved process, therefore make the api call
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						ExtensionApiService.this.apiV(str);
					}
				});

			}else{
				throw new SecurityException("Unauthorized access was made to the plugin");
			}
			
		}

		@Override
		public void syncApi(String str) throws RemoteException {
			int pid=getCallingPid();
			if(ExtensionService.cache.containsKey(pid)){
				ExtensionApiService.this.apiV(str);
			}else{
				throw new SecurityException("Unauthorized access was made to the plugin");
			}
			
		}

		@Override
		public Letter api(String str) throws RemoteException {
			int pid=getCallingPid();
			if(ExtensionService.cache.containsKey(pid)){
				return ExtensionApiService.this.apiR(str);
			}else{
				return null;
			}
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		int pid=intent.getIntExtra("callingPid",-1);
		if(ExtensionService.cache.containsKey(pid)){
			return api;
		}else{
			return null;
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mHandlerThread=new HandlerThread("Extension: "+TAG);
		mHandlerThread.start();
		mServiceLooper=mHandlerThread.getLooper();
		mHandler=new Handler(mServiceLooper);
	}
	
	protected Letter apiR(String str){
		return null;
	}
	
	protected void apiV(String str){
		return;
	}
}
