package com.musicjunky.extensions;

import com.musicjunky.extensions.Letter;
import com.musicjunky.extensions.Callback;

interface IWorkBinder{

	oneway void api(in Letter letter, in Callback callback);

}