package com.musicjunky.adpater;

import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import com.musicjunky.fragment.PageFragment;

public class TabInfo {

	private	PageFragment fragment;
	private Bundle bundle;
	private String title;
	
	public TabInfo(Bundle bundle, String title, SimpleCursorAdapter adapter) {
		this.fragment=PageFragment.newInstance(bundle, adapter);
		this.bundle=bundle;
		this.title=title;
	}
	
	public PageFragment getFragment() {
		return fragment;
	}
	
	public Bundle getBundle() {
		return bundle;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setFragment(PageFragment fragment) {
		this.fragment = fragment;
	}
	
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
}
