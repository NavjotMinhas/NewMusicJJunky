package com.musicjunky;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class MediaUtils {

	public static final HashMap<Long,Drawable> sCachedArtWork=new HashMap<Long, Drawable>();
	private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
	private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
	
	private static String TAG=MediaUtils.class.getName();
	
	static{
		sBitmapOptionsCache.inJustDecodeBounds=false;
		sBitmapOptionsCache.inPreferredConfig=Bitmap.Config.RGB_565;
		sBitmapOptionsCache.inDither=false;
	}
	
	
	/*
	 *  
	 * LINK: http://stackoverflow.com/questions/2641726/decoding-bitmaps-in-android-with-the-right-size
	 * 
	 * Problem:
	 * I decode bitmaps from the SD card using BitmapFactory.decodeFile. 
	 * Sometimes the bitmaps are bigger than what the application needs 
	 * or that the heap allows, so I use BitmapFactory.Options.inSampleSize 
	 * to request a subsampled (smaller) bitmap.The problem is that the 
	 * platform does not enforce the exact value of inSampleSize, and 
	 * I sometimes end up with a bitmap either too small, or still 
	 * too big for the available memory.
	 * 
	 * Solution:
	 * The first step is to read the file to a Bitmap slightly bigger than 
	 * you require, using BitmapFactory.Options.inSampleSize to ensure that 
	 * you do not consume excessive memory reading a large bitmap when all 
	 * you want is a smaller thumbnail or screen resolution image.
	 * 
	 * The second step is to call Bitmap.createScaledBitmap() to create a new bitmap 
	 * to the exact resolution you require.
	 * 
	 */
	public static Drawable getCachedArwork(Context context,long songID, long albumID){
		Drawable drawable=null;
		ParcelFileDescriptor pfd=null;
		if(albumID>-1){
			synchronized (sCachedArtWork) {
				drawable=sCachedArtWork.get(albumID);
			}
			if(drawable==null){
				Uri uri=ContentUris.withAppendedId(sArtworkUri, albumID);
				ContentResolver res=context.getContentResolver();
				try {
					pfd=res.openFileDescriptor(uri, "r");
					BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, sBitmapOptionsCache);
					
					int requiredWidth=context.getResources().getDimensionPixelSize(R.dimen.album_art_list_item_width);
					int requiredHeight=context.getResources().getDimensionPixelSize(R.dimen.album_art_list_item_height);
					
					int sampleSize=1;
					int bitmapWidth=sBitmapOptionsCache.outWidth;
					int bitmapHeight=sBitmapOptionsCache.outHeight;
					
					bitmapWidth >>= 1;
					bitmapHeight >>= 1;
					
					if(bitmapWidth>requiredWidth && bitmapHeight>requiredHeight){
						bitmapWidth >>= 1;
						bitmapHeight >>= 1;
						sampleSize <<=1;
					}
					sBitmapOptionsCache.inSampleSize=sampleSize;
					sBitmapOptionsCache.inJustDecodeBounds=false;
					Bitmap bitmap=BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, sBitmapOptionsCache);
					if(bitmap!=null){
						bitmap=Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true);
						FastBitmapDrawable fastDrawable=new FastBitmapDrawable(bitmap);
						sCachedArtWork.put(albumID, fastDrawable);
						return fastDrawable;
					}
				} catch (FileNotFoundException e) {
					return getDefaultArtwork(context);
				}finally{
					if(pfd!=null){
						try {
							pfd.close();
						} catch (IOException e) {
							Log.e(TAG, "IOException", e);
						}
					}
				}
				
			}else{
				return drawable;
			}
		}else{
			return getDefaultArtwork(context);
		}
		return null;
	}
	
	
	private static Drawable getDefaultArtwork(Context context){
		return context.getResources().getDrawable(R.drawable.fallback_cover);
	}

    // A really simple BitmapDrawable-like class, that doesn't do
    // scaling, dithering or filtering.
    private static class FastBitmapDrawable extends Drawable {
        private Bitmap mBitmap;
        public FastBitmapDrawable(Bitmap b) {
            mBitmap = b;
        }
        @Override
        public void draw(Canvas canvas) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
        @Override
        public void setAlpha(int alpha) {
        }
        @Override
        public void setColorFilter(ColorFilter cf) {
        }
    }
}
