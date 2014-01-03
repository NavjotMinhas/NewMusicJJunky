package com.musicjunky.adpater;

import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import com.musicjunky.fragment.PageFragment;

public class ListTab {

	private	PageFragment fragment;
	private Bundle bundle;
	private String title;
	
	public ListTab(Bundle bundle, String title, SimpleCursorAdapter adapter, int type) {
		this.fragment=PageFragment.newInstance(bundle, adapter, type);
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
