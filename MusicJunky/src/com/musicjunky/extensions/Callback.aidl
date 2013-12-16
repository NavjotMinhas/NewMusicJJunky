package com.musicjunky.extensions;

import com.musicjunky.extensions.Letter;

interface Callback{

	oneway void callback(inout Letter letter);

}