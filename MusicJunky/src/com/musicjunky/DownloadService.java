package com.musicjunky;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends IntentService {

	private ArrayList<Download> mDownloads=new ArrayList<Download>();
	private static DownloadService sInstance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance=this;
	}
	
	public DownloadService get(){
		return sInstance;
	}
	
	public DownloadService() {
		super("Download");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Download download=new Download();
		download.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intent);
		mDownloads.add(download);

	}
	
	public void resumeDownload(Download download){
		download.resume();
	}
	
	public void pauseDownload(Download download){
		download.pause();
	}
	
	public void removeDownload(Download download){
		if(download.getDownloadStatus()!=Download.DOWNLOADING){
			download.cancel();
		}
		if(!mDownloads.remove(download)){
			Toast.makeText(this, "Failed to delete "+download.getFile().getName(), Toast.LENGTH_LONG).show();
		}
	}
	
	public ArrayList<Download> getDownloads(){
		return mDownloads;
	}
	
}class Download extends AsyncTask<Intent,Long,Boolean>{

	private String TAG=Download.class.getName();
	private static final int MAX_BUFFER_SIZE=1024;
		
	public static final String STATUSES[] = { "Downloading", "Paused", "Complete", "Cancelled", "Error" };
	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;
	
	private int mStatus;
	private long numOfBytesDownloaded=0;
	private long mFileSize;
	private RandomAccessFile mRandomAccessFile;
	private File mFile;
	private URL mUrl;
	private Intent mIntent;
	private float mProgress;
	private ArrayList<DownloadListener> mListeners=new ArrayList<DownloadListener>();
	private Handler handler=new Handler(Looper.getMainLooper());
	
		
	private void deleteFile(){
		if(mFile.exists()){
			mFile.delete();
		}
	}
	
	public void pause(){
		mStatus=PAUSED;
		notifyStatusChanged();
	}
	
	public void resume(){
		if(mStatus!=DOWNLOADING){
			mStatus= DOWNLOADING;
			this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mIntent);
		}
	}
	
	public void cancel(){
		if(mStatus==DOWNLOADING){
			mStatus= CANCELLED;
			deleteFile();
			notifyStatusChanged();
		}
	}
	
	private void error(){
		mStatus=ERROR;
		notifyStatusChanged();
	}
	
	public long getFileSize() {
		return mFileSize;
	}

	public void setFileSize(long mFileSize) {
		this.mFileSize = mFileSize;
	}

	public File getFile() {
		return mFile;
	}

	public void setFile(File mFile) {
		this.mFile = mFile;
	}

	public Intent getIntent() {
		return mIntent;
	}

	public void setIntent(Intent mIntent) {
		this.mIntent = mIntent;
	}
	
	public float getProgress(){
		return mProgress;
	}
	
	public int getDownloadStatus(){
		return mStatus;
	}
	
	public String getTextStatus(){
		return STATUSES[mStatus];
	}

	@Override
	protected Boolean doInBackground(Intent... params) {
		if(mFile==null || mUrl==null){
			notifyOnStart();
			mIntent=params[0];
			mUrl=(URL)params[0].getSerializableExtra("downloadURL");
			mFile=(File)params[0].getSerializableExtra("downloadFile");
		}
		byte[]buffer=new byte[MAX_BUFFER_SIZE];
		int numOfBytesRead=-1;
		try {
			HttpURLConnection connection=(HttpURLConnection)mUrl.openConnection();
			connection.setRequestProperty("Range","bytes=" + numOfBytesRead + "-");
			connection.connect();
			if(connection.getResponseCode()/100 !=2){
				error();
			}
			mFileSize=connection.getContentLength();
			if(mFileSize<1){
				 error();
			 }
			mRandomAccessFile=new RandomAccessFile(mFile,"rw");
			if(mRandomAccessFile.length()>0){
				mRandomAccessFile.seek(numOfBytesDownloaded);
			}
			InputStream input=connection.getInputStream();
			mStatus=DOWNLOADING;
			notifyStatusChanged();
			while(mStatus==DOWNLOADING){
				numOfBytesRead=input.read(buffer);
				if(numOfBytesRead==-1){
					break;
				}
				mRandomAccessFile.write(buffer,0,numOfBytesRead);
				numOfBytesDownloaded+=numOfBytesRead;
				publishProgress();
			}
			input.close();
			mRandomAccessFile.close();
			return true;
		} catch (IOException e) {
			Log.e(TAG,"IOException",e);
		}
		return false;
	}
	
	@Override
	protected void onProgressUpdate(Long... values) {
		mProgress=((float)numOfBytesDownloaded)/((float)mFileSize);
		for(DownloadListener listener:mListeners){
			listener.onUpdate(this, mProgress);
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result){
			mStatus=COMPLETE;
			notifyOnComplete();
		}else{
			error();
		}
	}
	
	private void notifyOnStart(){
		handler.post(new Runnable(){
			@Override
			public void run() {				
				for(DownloadListener listener:mListeners){
					listener.onStart(Download.this);
				}
			}
		});
	}
	
	private void notifyOnComplete(){
		handler.post(new Runnable(){
			@Override
			public void run() {				
				for(DownloadListener listener:mListeners){
					listener.onComplete(Download.this);
				}
			}
		});
	}
	
	private void notifyStatusChanged(){
		handler.post(new Runnable(){
			@Override
			public void run() {				
				for(DownloadListener listener:mListeners){
					listener.onStatusChanged(Download.this);
				}
			}
		});
	}
	
	public void addListeners(DownloadListener listener){
		mListeners.add(listener);
	}
}
