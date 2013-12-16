package com.musicjunky.extensions;

import com.musicjunky.extensions.Letter;

interface IHostApp{ 	
 	Intent authHandshake(in Intent intent);
 	oneway void publishUpdate(in Letter letter);
}