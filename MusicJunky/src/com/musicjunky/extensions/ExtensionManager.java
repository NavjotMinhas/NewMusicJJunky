package com.musicjunky.extensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;

public class ExtensionManager {

	private Context mApplicationcontext;
	private List<Extension> allActiveExtensions=new ArrayList<Extension>();
	private Map<ComponentName, Extension> mActiveExtension=new HashMap<ComponentName,Extension>();
	private SharedPreferences pluginConfig;

	private static ExtensionManager sInstance;

	public final static String ACTION_EXTENSION = "com.musicjunky.extensions.PLUGIN";
	
	private final static String TURNED_ON_EXTENSIONS="active_extensions";

	private final static String TAG = ExtensionManager.class.getName();

	
	public static ExtensionManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new ExtensionManager(context);
		}
		return sInstance;
	}

	private ExtensionManager(Context context) {
		this.mApplicationcontext = context.getApplicationContext();
		this.pluginConfig = this.mApplicationcontext.getSharedPreferences("pluginConfig", 0);
		loadActiveExtension();
	}
	
	public void loadActiveExtension(){
		String allActivePlugins=pluginConfig.getString(ExtensionManager.TURNED_ON_EXTENSIONS, "");
		if(!TextUtils.isEmpty(allActivePlugins)){
			String[]flattenedPluginNames= TextUtils.split(allActivePlugins,",");
			for(String flattenedName: flattenedPluginNames){
				ComponentName componentName=ComponentName.unflattenFromString(flattenedName);
				//Inflate extension from component name
				Intent intent=new Intent();
				intent.setComponent(componentName);
				ResolveInfo resolveInfo=mApplicationcontext.getPackageManager().resolveService(intent, 0);
				Extension extension=new Extension(resolveInfo, mApplicationcontext.getPackageManager());
				allActiveExtensions.add(extension);
			}
		}
	}

	
	public boolean addExtension(ComponentName name){
		String allActivivePlugins=pluginConfig.getString(ExtensionManager.TURNED_ON_EXTENSIONS, "");
		if(!TextUtils.isEmpty(allActivivePlugins)){
			allActivivePlugins+=","+name.flattenToString();
		}else{
			allActivivePlugins+=name.flattenToString();
		}
		return pluginConfig.edit().putString(ExtensionManager.TURNED_ON_EXTENSIONS, allActivivePlugins).commit();
	}
	
	public boolean batchAddExtension(ComponentName [] names){
		String allActivivePlugins=pluginConfig.getString(ExtensionManager.TURNED_ON_EXTENSIONS, "");
		String [] temp=new String[names.length];
		for(int i=0;i<names.length;i++){
			temp[i]=names[i].flattenToString();
		}
		if(!TextUtils.isEmpty(allActivivePlugins)){
			allActivivePlugins+=","+ TextUtils.join(",", temp);
		}else{
			allActivivePlugins+=TextUtils.join(",", temp);
		}
		return pluginConfig.edit().putString(ExtensionManager.TURNED_ON_EXTENSIONS, allActivivePlugins).commit();
	}
	
	public boolean removeExtension(ComponentName name){
		String allActivivePlugins=pluginConfig.getString(ExtensionManager.TURNED_ON_EXTENSIONS, "");
		if(!TextUtils.isEmpty(allActivivePlugins)){
			String[]temp=TextUtils.split(allActivivePlugins, ",");
			//remove point of interest
			String flattenedName=name.flattenToString();
			List<String> list=Arrays.asList(temp);
			list.remove(flattenedName);
			temp=list.toArray(new String[list.size()]);
			allActivivePlugins=TextUtils.join(",", temp);
		}else{
			throw new IllegalArgumentException("There are no active extensions");
		}
		return pluginConfig.edit().putString(ExtensionManager.TURNED_ON_EXTENSIONS, allActivivePlugins).commit();
	}
	
	public void setActiveExtensions(List<ComponentName> newActiveComponentNameList){
		//Reconstruct the hashmap cache
		List<Extension> installedExtensions=getInstalledExtensions();
		
		List<Extension> newActiveExtensionList=new ArrayList<Extension>();
		
		for(Extension extension:installedExtensions){
			if(newActiveComponentNameList.contains(extension.componentName)){
				newActiveExtensionList.add(extension);
			}
		}
		synchronized(mActiveExtension){
			mActiveExtension.clear();
			for(Extension extension: newActiveExtensionList){
				mActiveExtension.put(extension.componentName,extension);
			}
		}
	}
	
	/**
	 * Gets all the installed plugins on the device by querying the entrie
	 * device for services which have an intent filter containg the following
	 * action ExtensionManager.ACTION_EXTENSION
	 * 
	 * @return
	 */
	public List<Extension> getInstalledExtensions() {
		PackageManager pm = mApplicationcontext.getPackageManager();
		Intent intent = new Intent(ExtensionManager.ACTION_EXTENSION);
		List<ResolveInfo> list = pm.queryIntentServices(intent,
				PackageManager.GET_META_DATA);
		List<Extension> extensions = new ArrayList<Extension>();
		if (list != null) {
			for (ResolveInfo resolveInfo : list) {
				Extension extension=new Extension(resolveInfo,pm);
				extensions.add(extension);
			}
		}
		return extensions;
	}

	public static class Extension {
		public String title;
		public String description;
		public ComponentName componentName;
		public int protocoolVersion;
		public Drawable icon;
		public boolean requiresMenuTab;
		public ComponentName settingsActivity;
		
		public Extension(){
			//Do nothing
		}
		
		public Extension(ResolveInfo resolveInfo, PackageManager pm){
			title = resolveInfo.serviceInfo.name;	
			componentName = new ComponentName(
					resolveInfo.serviceInfo.packageName,
					resolveInfo.serviceInfo.name);
			icon = resolveInfo.loadIcon(pm);
			
			Bundle metaData = resolveInfo.serviceInfo.metaData;
			if (metaData != null) {
				description = metaData.getString("description",
						"");
				protocoolVersion = metaData
						.getInt("protocool_version",0);
				requiresMenuTab = metaData.getBoolean(
						"requires_menu_tab", false);
				String settingsActivityFlattenedName = metaData.getString(
						"settingsActivity", "");
				if (!TextUtils.isEmpty(settingsActivityFlattenedName)) {
					settingsActivity = ComponentName
							.unflattenFromString(resolveInfo.serviceInfo.packageName
									+ "/" + settingsActivityFlattenedName);
				}
			}
		}
	}
}
