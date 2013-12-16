package com.musicjunky;

import android.app.Activity;
import android.os.Bundle;

import com.musicjunky.extensions.ExtensionHost;

public class ExtensionActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExtensionHost host=new ExtensionHost(getApplicationContext());
		
	}
	
}
