package com.musicjunky.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 
 * @author Nav
 */
public class Download extends AsyncTask<String, Integer, Integer> {

	private final static String TAG=Download.class.getName();
	
	public final static int MALFORMED_URL = 0;
	public final static int DOWNLOADING = 1;
	public final static int FINISHED = 2;
	public final static int COULD_NOT_CREATE_FILE = 3;
	public final static int STOPPED = 4;
	public final static int REMOVING = 5;

	private String fileName;
	private String url;
	private String LOCK="LOCK";

	private int status;
	private int percentComplete;
	private int fileSize;
	private int numOfBytesWritten;

	private RandomAccessFile downloadFile;
	private File downloadDirectory;

	public Download(String downloadDirPath, String fileName) {
		this.downloadDirectory=new File(downloadDirPath);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
		
	public int status() {
		return status;
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setProgressPercent(int percentage) {
		this.percentComplete = percentage;
	}

	public RandomAccessFile getDownloadedFile() {
		return downloadFile;
	}
	
	public void onPause() {
		if(status==Download.DOWNLOADING){
			setStatus(Download.STOPPED);
		}
	}
	
	public synchronized void onRemove(){
		setStatus(Download.REMOVING);
		if(downloadFile!=null){
			try {
				//Clean up and delete the file
				downloadFile.close();
				File file=new File(this.downloadDirectory, this.fileName);
				Log.e("On Remove",""+file.delete());
			} catch (IOException e) {
				Log.e(TAG, "IOException", e);
			}
		}
	}
	
	public void onResume(){
		if(status==Download.STOPPED){
			synchronized(LOCK){
				LOCK.notify();
			}
		}
	}

	@Override
	protected void onPreExecute() {
		try {
			//open file and seek to the end of it
			downloadFile=new RandomAccessFile(new File(this.downloadDirectory, this.fileName),"rwd");
			downloadFile.seek(numOfBytesWritten);
			setStatus(Download.DOWNLOADING);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException", e);
			setStatus(Download.COULD_NOT_CREATE_FILE);
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
			setStatus(Download.COULD_NOT_CREATE_FILE);
		}

	}

	@Override
	protected Integer doInBackground(String... params) {
		url=params[0];
		int status = downloadData(url);
		return status;
	}

	private int downloadData(String url) {
		InputStream is = null;
		BufferedInputStream reader = null;
		try {
			//The url to download
			URL downloadURL = new URL(url);
			
			//Connect to the url and set the range for where the download should start from
			// in most cases it will be [0 to contentLength]
			HttpURLConnection conn = (HttpURLConnection) downloadURL
					.openConnection();
			conn.setRequestProperty("Range",
                    "bytes=" + numOfBytesWritten + "-");
			conn.connect();
			
			//only start downloading if the HTTP response code is 200
			int responseCode = conn.getResponseCode();
			if (responseCode/100 == 2) {
				
				//This is a status check in case we forget to check the status before we use AsyncTask.execute
				if (status == Download.DOWNLOADING) {
					fileSize = conn.getContentLength();
					
					//Check for valid content length
					if(fileSize < 1){
						return Download.MALFORMED_URL;
					}
					
					//Prep the assets for download
					is = downloadURL.openStream();
					reader = new BufferedInputStream(is);
					byte[] buffer = new byte[1024];
					int numOfBytesRead = 0;
					
					//Read bytes from url and write to file
					while ((numOfBytesRead = reader.read(buffer)) != -1 && status == Download.DOWNLOADING) {
						downloadFile.write(buffer, 0, numOfBytesRead);
						numOfBytesWritten += numOfBytesRead;
						publishProgress(Download.DOWNLOADING,
								(int) (((float)numOfBytesWritten / (float)fileSize) * 100));						
						Thread.sleep(100);
						if(status==Download.STOPPED){
							synchronized(LOCK){
								LOCK.wait();
								setStatus(Download.DOWNLOADING);
								Log.e("test", "test lock");
							}
						}
					}
					
					//Close the stream and the random access file
					is.close();
					downloadFile.close();
					
					//Tell the AsyncTask the download completed successfully
					return Download.FINISHED;
				}else{
					return Download.STOPPED;
				}
			} else {
				return Download.MALFORMED_URL;
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "MalformedURLException", e);
			return Download.MALFORMED_URL;
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
			return Download.COULD_NOT_CREATE_FILE;
		} catch (InterruptedException e) {
			Log.e(TAG, "InterruptedException", e);
			return Download.MALFORMED_URL;
		}finally{
			if(is !=null){
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, "IOException", e);
				}
			}
			
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					Log.e(TAG, "IOException", e);
				}
			}
			
			if(downloadFile !=null){
				try {
					downloadFile.close();
				} catch (IOException e) {
					Log.e(TAG, "IOException", e);
				}
			}
		}

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		setStatus(values[0]);
		setProgressPercent(values[1]);
		Log.e(TAG,values[1]+"percent downloaded");
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		setStatus(result);
	}

}
