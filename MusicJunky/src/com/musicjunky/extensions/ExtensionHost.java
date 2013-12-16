package com.musicjunky.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

public class ExtensionHost {

	private static final String TAG=ExtensionHost.class.getName();
	
	private Context mContext;
	private HandlerThread handlerThread;
	private Looper mLooper;
	private	Handler mHandler;
	
	private Map<ComponentName,Connection> mConnections=new HashMap<ComponentName, Connection>();
	private List<ComponentName> list=new ArrayList<ComponentName>();
	private ExtensionManager manager;
	
	public ExtensionHost(Context context) {
		this.mContext=context.getApplicationContext();
		handlerThread=new HandlerThread("ExtensionHost");
		handlerThread.start();
		mLooper=handlerThread.getLooper();
		mHandler=new Handler(mLooper);
		manager=ExtensionManager.getInstance(this.mContext);
		establishAndDestroyConnections(manager.getAllActiveExtensions());
	}
	
	public void destroy(){
		establishAndDestroyConnections(new ArrayList<ComponentName>());
		mLooper.quit();
	}
	
	private void establishAndDestroyConnections(List<ComponentName> newActiveExtensionNames){
		Set<ComponentName> activeSet=new HashSet<ComponentName>();
		activeSet.addAll(newActiveExtensionNames);
		
		Set<ComponentName> connectedSet=new HashSet<ComponentName>();
		connectedSet.addAll(mConnections.keySet());
		
		Iterator<ComponentName> i=activeSet.iterator();
		while(i.hasNext()){
			ComponentName cn=i.next();
			if(connectedSet.contains(cn)){
				 continue;
			}
			final Connection connection=createConnection(cn, false);
			Intent intent=new Intent();
			intent.setComponent(connection.componentName);
			if(mContext.bindService(intent, connection.serviceConnection, Context.BIND_AUTO_CREATE)){
				mConnections.put(connection.componentName,connection);
				list.add(connection.componentName);
			}
		}
		
		connectedSet.removeAll(activeSet);
		i=connectedSet.iterator();
		while(i.hasNext()){
			ComponentName cn=i.next();
			Connection connection=mConnections.get(cn);
			mContext.unbindService(connection.serviceConnection);
			
			//clean up connection
			connection.pluginBinder=null;
			connection.hostApp=null;
			connection.serviceConnection=null;
			
			mConnections.remove(cn);
		}
	}
	
	private Connection createConnection(ComponentName componentName, final boolean isReconnect){
		final Connection conn=new Connection();
		conn.componentName=componentName;
		conn.hostApp=getHostInterface();
		conn.serviceConnection =new ServiceConnection(){

			@Override
			public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				conn.ready=true;
				conn.pluginBinder=IPlugin.Stub.asInterface(arg1);
				asyncExecute(conn,new Operation(){

					@Override
					public void run() throws RemoteException {
						conn.pluginBinder.onInitialize(conn.hostApp, isReconnect);
					}
					
				});
				
				Iterator<Operation> i=conn.deferredOpperations.iterator();
				while(i.hasNext()){
					if(conn.ready){
						asyncExecute(conn, i.next());
						i.remove();
					}
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				conn.ready=false;
				conn.pluginBinder=null;
				conn.hostApp=null;
				mHandler.post(new Runnable(){
					@Override
					public void run() {
						mConnections.remove(conn.componentName);
						
					}
				});
				
			}
			
		};
		return conn;
	}
	
	private IHostApp getHostInterface(){
		IHostApp.Stub host=new IHostApp.Stub() {
			
			@Override
			public void publishUpdate(Letter letter) throws RemoteException {
				
				
			}
			
			@Override
			public Intent authHandshake(Intent intent) throws RemoteException {
				ComponentName request=(ComponentName)intent.getParcelableExtra("request_plugin_name");
				if(request==null){
					return null;
				}	
				Connection conn=mConnections.get(request);
				if(conn==null){
					return null;
				}
				ComponentName from=(ComponentName)intent.getParcelableExtra("from");
				if(from==null){
					return null;
				}
				//Security to prevent Intent poisoning
				String[] pkg=mContext.getPackageManager().getPackagesForUid(getCallingUid());
				if(pkg.length==1){
					if(pkg[0].equals(from.getPackageName())){
						Intent bindIntent=conn.pluginBinder.auth(from, getCallingUid());
						if(bindIntent!=null && from.flattenToString().equals(bindIntent.getStringExtra("className"))){
							return bindIntent;
						}
					}
				}
				return null;
			}
		};
		return host;
	}
	
	private void asyncExecute(final Connection conn,final Operation opps){
		final Runnable runnable=new Runnable(){
			@Override
			public void run() {
				try {
					if(conn.pluginBinder==null){
						throw new NullPointerException("The plugin binder is null");
					}
					opps.run();
				} catch (RemoteException e) {
					Log.e(TAG,"Could not run the opp",e);
					conn.deferredOpperations.addLast(opps);
				}
			}
		};
		if(conn.ready){
			mHandler.post(runnable);
		}else{
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					conn.deferredOpperations.addLast(opps);
				}
			});
		}
	}
	
	private static class Connection{
		public ComponentName componentName;
		public ServiceConnection serviceConnection;
		public boolean ready;
		public IHostApp hostApp;
		public IPlugin pluginBinder;
		
		//first-in-first-out
		public LinkedList<Operation> deferredOpperations=new LinkedList<Operation>();
	}
	
}
