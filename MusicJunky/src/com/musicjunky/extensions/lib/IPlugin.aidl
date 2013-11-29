package com.musicjunky.extensions.lib;

import com.musicjunky.extensions.lib.IHostApp;

interface IPlugin{

	oneway void onInitialize(in IHostApp host, boolean isReconnect );
	oneway void onUpdate(int reason);
	Intent auth(in ComponentName name, int uid);
}