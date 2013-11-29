package com.musicjunky.extensions.lib;

import com.musicjunky.extensions.lib.Letter;

interface IApi{

	oneway void asyncApi(in String str);
	void syncApi(in String str);
	Letter api(in String str);

}