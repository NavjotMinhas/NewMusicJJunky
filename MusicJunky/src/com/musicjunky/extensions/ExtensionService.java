package com.musicjunky.extensions;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;


public class ExtensionService extends Service {

	private static final String TAG=ExtensionService.class.getName();
	
	private static final int CURRENT_PROTOCOL_VERSION=1;
	
	private IHostApp mHost;
	private int mProtcoolVersion;
	private boolean mIsWorldReadable;
	private boolean mIsInitialized;
	
	private HandlerThread mHandlerThread;
	private Looper mLooper;
	private Handler mHandler;
	
	
	public static Map<Integer,ComponentName> authenticatedNames=new HashMap<Integer,ComponentName>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		loadMetadata();
		mHandlerThread=new HandlerThread("MusicJunky Extension"+ExtensionService.class.getSimpleName());
		mHandlerThread.start();
		mLooper=mHandlerThread.getLooper();
		mHandler=new Handler(mLooper);
	}
	
	private void loadMetadata(){
		try {
			ComponentName componentName=new ComponentName(getPackageName(),ExtensionService.class.getName());
			ServiceInfo info=getPackageManager().getServiceInfo(componentName, PackageManager.GET_META_DATA);
			Bundle metaData=info.metaData;
			if(metaData!=null){
				mProtcoolVersion=metaData.getInt("protocolVersion", 1);
				mIsWorldReadable=metaData.getBoolean("worldReadable", false);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG,"NameNotFoundException",e);
		}
	}
	
	IPlugin.Stub plugin=new IPlugin.Stub() {
		
		@Override
		public void onUpdate(int reason) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onInitialize(IHostApp host, boolean isReconnect)
				throws RemoteException {
			
			//Check Signature
			boolean isVerfied=false;
			if(!isVerfied){
				PackageManager pm=getPackageManager();
				String [] packages=pm.getPackagesForUid(getCallingUid());
				if(packages!=null && packages.length>0){
					try {
						PackageInfo packageInfo=pm.getPackageInfo(packages[0], PackageManager.GET_SIGNATURES);
						Signature[] signature=packageInfo.signatures;
						if(signature !=null){
							if(signature.length==1){
								if(signature.equals(MUSIC_JUNKY_SIGNATURE)){
									isVerfied=true;
								}else{
									throw new SecurityException("Security error, unauthorized app tried to connect with the extension");
								}
							}
						}
					} catch (NameNotFoundException e) {
						Log.e(TAG,"NameNotFoundException",e);
					}
				}
			}
			if(isVerfied){
				if(!mIsInitialized){
					mHost=host;
					ExtensionService.this.onInitialize(isReconnect);
					mIsInitialized=true;
				}
			}
			
		}
		
		@Override
		public Intent auth(ComponentName name, int uid) throws RemoteException {
			return ExtensionService.this.auth(name, uid);
		}
	};
	
	protected void onInitialize(boolean isReconnect){
		
	}
	
	protected Intent auth(ComponentName name, int uid){
		
		//remove the map.put code because thats up to the developer to decide not us
		if(authenticatedNames.get(uid)==null){
			authenticatedNames.put(uid, name);
		}else{
			Log.e(TAG,name.flattenToString()+" is already authenticated");
		}
		
		
		Intent intent=new Intent();
		ComponentName componentName=new ComponentName(getApplicationContext(), ExtensionService.class);
		intent.setComponent(componentName);
		intent.putExtra("className",name.flattenToString());
		return intent;
	}
	
	protected final void publishUpdate(Letter letter){
		try{
			mHost.publishUpdate(letter);
		}catch(RemoteException e){
			
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return plugin;
	}

	public static final String MUSIC_JUNKY_SIGNATURE="";
}
