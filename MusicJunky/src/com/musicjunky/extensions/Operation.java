package com.musicjunky.extensions;

import android.os.RemoteException;

public interface Operation {
	public void run() throws RemoteException;
}
