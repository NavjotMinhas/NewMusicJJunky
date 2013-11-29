package com.musicjunky;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class Database {

	public static final int SONG=0;
	public static final int ARTIST=1;
	public static final int ALBUM=2;
	public static final int GENRE=3;
	

	public Database(ContentResolver resolver) {
		Cursor cursor = getAllItems(resolver,Database.ARTIST,MediaStore.Audio.Artists.ARTIST +" ASC", MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
		while (cursor.moveToNext()) {
			Log.e("TAG", cursor.getString(0)+" "+cursor.getString(1));
		}
	}

	public static Cursor getSongData(ContentResolver resolver, String songName, String... args) {
		if(resolver==null || songName==null || args==null ){
			throw new IllegalArgumentException();
		}
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, args,
				MediaStore.Audio.Media.TITLE + " = ?",
				new String[] { songName }, null);
		return cursor;
	}
	
	public static Cursor getSongsFromAlbum(ContentResolver resolver,String artistName, String albumName,String sortOrder, String... args) {
		if(resolver==null || artistName==null || albumName==null|| sortOrder==null || args==null ){
			throw new IllegalArgumentException();
		}
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, args,
				MediaStore.Audio.Media.ALBUM + " = ? AND "+MediaStore.Audio.Media.ARTIST +" = ?",
				new String[] { albumName, artistName }, sortOrder);
		return cursor;
	}
	
	public static Cursor getSongsFromArist(ContentResolver resolver,String artistName,String sortOrder, String... args) {
		if(resolver==null || artistName==null || sortOrder==null || args==null ){
			throw new IllegalArgumentException();
		}
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, args,
				MediaStore.Audio.Media.ARTIST +" = ?",
				new String[] { artistName }, sortOrder);
		return cursor;
	}
	
	public static Cursor getAlbumData(ContentResolver resolver,String albumName, String... args){
		if(resolver==null || albumName==null || args==null ){
			throw new IllegalArgumentException();
		}
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, args,
				MediaStore.Audio.Media.ALBUM + " = ?",
				new String[] { albumName }, null);
		return cursor;
	}
	
	public static Cursor getAllItems(ContentResolver resolver, int type, String sortOrder, String... args){
		if(resolver==null || args==null ){
			throw new IllegalArgumentException();
		}
		Cursor cursor=null;
		switch(type){
			case SONG:
				cursor=resolver.query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, args,
						MediaStore.Audio.Media.IS_MUSIC + " != 0", null, sortOrder);
				break;
			case ARTIST:
				cursor=resolver.query(
						MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, args,
						null, null, sortOrder);
				break;
			case ALBUM:
				cursor=resolver.query(
						MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, args,
						null, null, sortOrder);
				break;
			case GENRE:
				cursor=resolver.query(
						MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, args,
						null, null, sortOrder);
				break;
			default:
				throw new IllegalArgumentException();
		}
		return cursor;
	}
	
	public static void dumpDatabase(Cursor cursor){
		while(cursor.moveToNext()){
			Log.e("*****","****************");
			for(int i=0;i<cursor.getColumnCount();i++){
				Log.e(cursor.getPosition()+"",cursor.getString(i));
			}
			Log.e("*****","****************");
		}
	}
}
