package com.musicjunky;

public interface DownloadListener {

	public void onStart(Download download);
	public void onStatusChanged(Download download);
	public void onUpdate(Download download, float progress);
	public void onComplete(Download download);
	
}
