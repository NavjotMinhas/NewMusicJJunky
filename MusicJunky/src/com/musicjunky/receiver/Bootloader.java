package com.musicjunky.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.musicjunky.services.HeadphoneBluetoothService;

public class Bootloader extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, HeadphoneBluetoothService.class));
		
	}
	
}
