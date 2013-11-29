package com.musicjunky.services;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class HeadphoneBluetoothService extends Service {

	private final static String TAG= HeadphoneBluetoothService.class.getName();
	
	private IBinder headphoneBinder = new HeadphoneBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return headphoneBinder;
	}

	class HeadphoneBinder extends Binder {

		public HeadphoneBluetoothService getService() {
			return HeadphoneBluetoothService.this;
		}

	}

	@Override
	public void onCreate() {
		Log.e(TAG,"on Create");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
		registerReceiver(headphoneReceiver, intentFilter);
		
		IntentFilter bluetoothIntentFilter = new IntentFilter();
		bluetoothIntentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
		registerReceiver(bluetoothReceiver, bluetoothIntentFilter);
		
	}
	
	@Override
	public void onDestroy() {
		Log.e(TAG,"onDestroy");
		unregisterReceiver(headphoneReceiver);
		unregisterReceiver(bluetoothReceiver);
	}

	private BroadcastReceiver headphoneReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int state= intent.getIntExtra("state", 0);
			if (state == 1) {
				AudioManager aM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				if (aM != null && !aM.isMusicActive()) {		
					Toast.makeText(getApplicationContext(),
							"Headphone connected", Toast.LENGTH_LONG).show();
				}
			}
		}

	};

	private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			int deviceType = device.getBluetoothClass().getDeviceClass();
			if (deviceType == BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER
					|| deviceType == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES
					|| deviceType == BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO
					|| deviceType == BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO
					|| deviceType == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE) {
				AudioManager aM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				if (aM != null && !aM.isMusicActive()) {
					
					Toast.makeText(getApplicationContext(),
							"BlueTooth connected", Toast.LENGTH_LONG).show();
				}
			}
		}

	};
	
}
