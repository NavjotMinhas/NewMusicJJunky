package com.musicjunky.adpater;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.SimpleCursorAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ViewPageAdapter extends FragmentStatePagerAdapter implements OnPageChangeListener {

	private final static String[] TABS_TITLE={"Song", "Artist", "Album"};
	
	private ArrayList<TabInfo> tabs=new ArrayList<TabInfo>();
	
	private ViewPager mViewPager;
	
	public ViewPageAdapter(SherlockFragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		this.mViewPager=pager;
		this.mViewPager.setAdapter(this);
		this.mViewPager.setOnPageChangeListener(this);
	}
	
	public void addTab(Bundle bundle, String title, SimpleCursorAdapter adapter){
		TabInfo tabInfo=new TabInfo(bundle,title, adapter);
		tabs.add(tabInfo);
	}
	
	@Override
	public Fragment getItem(int pos) {
		TabInfo tabInfo=tabs.get(pos);
		return tabInfo.getFragment();
	}

	@Override
	public int getCount() {
		return TABS_TITLE.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TABS_TITLE[position];
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
		
	}

	@Override
	public void onPageSelected(int arg0) {
		
		
	}

	

}
