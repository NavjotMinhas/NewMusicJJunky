package com.musicjunky.download;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class DownloadService extends Service {

	private ArrayList<Download> downloads=new ArrayList<Download>();
	
	public static final String ACTION_DOWNLOAD="com.musicjunky.download.intent.action.DOWNLOAD";
	public static final String ACTION_PAUSE="com.musicjunky.download.intent.action.PAUSE";
	public static final String ACTION_REMOVE="com.musicjunky.download.intent.action.REMOVE";
	public static final String ACTION_RESUME="com.musicjunky.download.intent.action.RESUME";
	public static final String ACTION_CLEAR_ALL="com.musicjunky.download.intent.action.CLEAR_ALL";
	
	public class DownloadServiceBinder extends Binder{
		
		public DownloadService getBinder(){
			return DownloadService.this;
		}
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new DownloadServiceBinder();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action=intent.getAction();
		if(action !=null){
			if(action.equals(DownloadService.ACTION_DOWNLOAD)){
				String url=intent.getStringExtra("url");
				String dowloadDir=intent.getStringExtra("downloadDirectory");
				String fileName=intent.getStringExtra("fileName");
				downloadFile(url,dowloadDir, fileName);
			}else if(action.equals(DownloadService.ACTION_PAUSE)){
				int listPos=intent.getIntExtra("listPos", -1);
				pause(listPos);
			}else if(action.equals(DownloadService.ACTION_REMOVE)){
				int listPos=intent.getIntExtra("listPos", -1);
				remove(listPos);
			}else if(action.equals(DownloadService.ACTION_RESUME)){
				int listPos=intent.getIntExtra("listPos", -1);
				resume(listPos);
			}else if(action.equals(DownloadService.ACTION_CLEAR_ALL)){
				clearAll();
			}
		}
		return START_NOT_STICKY;
	}

	public void downloadFile(String url, String downloadDir, String fileName){
		Download download=new Download(downloadDir, fileName);
		download.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
		downloads.add(download);
	}
	
	public ArrayList<Download> getDownloads(){
		return downloads;
	}
	
	public void pause(int listPos){
		if(listPos+1<=downloads.size()){
			downloads.get(listPos).onPause();
		}
	}
	
	public void remove(int listPos){
		if(listPos+1<=downloads.size()){
			downloads.get(listPos).onRemove();
			downloads.remove(listPos);
		}
	}
	
	public void resume(int listPos){
		if(listPos+1<=downloads.size()){
			downloads.get(listPos).onResume();
		}
	}
	
	public void clearAll(){
		for(Download download: downloads){
			if(download.status()!=Download.DOWNLOADING){
				downloads.remove(download);
			}
		}
	}
}
