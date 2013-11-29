package com.musicjunky.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class PageFragment extends ListFragment{

	
	public static PageFragment newInstance(Bundle bundle, SimpleCursorAdapter mListAdpater){
		PageFragment fragment=new PageFragment();
		fragment.setListAdapter(mListAdpater);
		//fragment.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		return fragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
}
