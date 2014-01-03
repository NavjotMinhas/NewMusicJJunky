package com.musicjunky.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.musicjunky.R;

public class PageFragment extends Fragment{

	public static final int LIST_VIEW=0;
	public static final int GRID_VIEW=1;
	
	private SimpleCursorAdapter mAdapter;
	private int mType;
	
	public static PageFragment newInstance(Bundle bundle, SimpleCursorAdapter listAdpater, int type){
		PageFragment fragment=new PageFragment();
		fragment.setAdapter(listAdpater);
		fragment.setType(type);
		return fragment;
	}
	
	public void setType(int type){
		mType=type;
	}
	
	public void setAdapter(SimpleCursorAdapter listAdpater){
		mAdapter=listAdpater;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=null;
		switch(mType){
			case LIST_VIEW:
				view=inflater.inflate(R.layout.list_tab, container, false);
				ListView listView=(ListView)view.findViewById(R.id.list);
				listView.setAdapter(mAdapter);
				return view;
			case GRID_VIEW:
				view=inflater.inflate(R.layout.grid_tab, container, false);
				GridView gridView=(GridView)view.findViewById(R.id.grid);
				gridView.setAdapter(mAdapter);
				return view;
				default:
					throw new IllegalArgumentException();
			
		}
	}
	
}
