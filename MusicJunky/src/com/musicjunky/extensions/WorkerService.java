package com.musicjunky.extensions;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

public class WorkerService extends Service {
		
	private HandlerThread mThread;
	private Looper mLooper;
	private WorkHandler mHandler;
	
	private final class WorkHandler extends Handler{
		public WorkHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			WorkerService.this.api((LetterWrapper)msg.obj);
		}
	}
	
	private static class LetterWrapper{
		public Letter letter;
		public Callback callback;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mThread=new HandlerThread("WorkerThread");
		mThread.start();
		mLooper=mThread.getLooper();
		mHandler=new WorkHandler(mLooper);
	}
	
	private IWorkBinder.Stub worker=new IWorkBinder.Stub() {

		@Override
		public void api(Letter letter, Callback callback) throws RemoteException {
			Message message=mHandler.obtainMessage();
			LetterWrapper letterWrapper=new LetterWrapper();
			letterWrapper.letter=letter;
			letterWrapper.callback=callback;
			message.obj=letterWrapper;
			mHandler.sendMessage(message);
		}
		
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return worker;
	}
	
	protected void api(LetterWrapper letter) {
		//Call the callback once you are done with the logic code, 
		//therefore the other extension/plugin can be notified that the work is done
		//on our end
	}
	
}
