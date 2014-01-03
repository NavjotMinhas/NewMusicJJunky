package com.musicjunky;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import com.musicjunky.player.android.IMediaPlaybackService;

public class MediaUtils {

//	public static final HashMap<Long,Drawable> sCachedArtWork=new HashMap<Long, Drawable>();
//	private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//	private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
//	
//	private static String TAG=MediaUtils.class.getName();
//	
//	static{
//		sBitmapOptionsCache.inJustDecodeBounds=false;
//		sBitmapOptionsCache.inPreferredConfig=Bitmap.Config.RGB_565;
//		sBitmapOptionsCache.inDither=false;
//	}
//	
//	
//	/*
//	 *  
//	 * LINK: http://stackoverflow.com/questions/2641726/decoding-bitmaps-in-android-with-the-right-size
//	 * 
//	 * Problem:
//	 * I decode bitmaps from the SD card using BitmapFactory.decodeFile. 
//	 * Sometimes the bitmaps are bigger than what the application needs 
//	 * or that the heap allows, so I use BitmapFactory.Options.inSampleSize 
//	 * to request a subsampled (smaller) bitmap.The problem is that the 
//	 * platform does not enforce the exact value of inSampleSize, and 
//	 * I sometimes end up with a bitmap either too small, or still 
//	 * too big for the available memory.
//	 * 
//	 * Solution:
//	 * The first step is to read the file to a Bitmap slightly bigger than 
//	 * you require, using BitmapFactory.Options.inSampleSize to ensure that 
//	 * you do not consume excessive memory reading a large bitmap when all 
//	 * you want is a smaller thumbnail or screen resolution image.
//	 * 
//	 * The second step is to call Bitmap.createScaledBitmap() to create a new bitmap 
//	 * to the exact resolution you require.
//	 * 
//	 */
//	public static Drawable getCachedArwork(Context context,long songID, long albumID){
//		Drawable drawable=null;
//		ParcelFileDescriptor pfd=null;
//		if(albumID>-1){
//			synchronized (sCachedArtWork) {
//				drawable=sCachedArtWork.get(albumID);
//			}
//			if(drawable==null){
//				Uri uri=ContentUris.withAppendedId(sArtworkUri, albumID);
//				ContentResolver res=context.getContentResolver();
//				try {
//					pfd=res.openFileDescriptor(uri, "r");
//					BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, sBitmapOptionsCache);
//					
//					int requiredWidth=context.getResources().getDimensionPixelSize(R.dimen.album_art_list_item_width);
//					int requiredHeight=context.getResources().getDimensionPixelSize(R.dimen.album_art_list_item_height);
//					
//					int sampleSize=1;
//					int bitmapWidth=sBitmapOptionsCache.outWidth;
//					int bitmapHeight=sBitmapOptionsCache.outHeight;
//					
//					bitmapWidth >>= 1;
//					bitmapHeight >>= 1;
//					
//					if(bitmapWidth>requiredWidth && bitmapHeight>requiredHeight){
//						bitmapWidth >>= 1;
//						bitmapHeight >>= 1;
//						sampleSize <<=1;
//					}
//					sBitmapOptionsCache.inSampleSize=sampleSize;
//					sBitmapOptionsCache.inJustDecodeBounds=false;
//					Bitmap bitmap=BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, sBitmapOptionsCache);
//					if(bitmap!=null){
//						bitmap=Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true);
//						FastBitmapDrawable fastDrawable=new FastBitmapDrawable(bitmap);
//						sCachedArtWork.put(albumID, fastDrawable);
//						return fastDrawable;
//					}
//				} catch (FileNotFoundException e) {
//					return getDefaultArtwork(context);
//				}finally{
//					if(pfd!=null){
//						try {
//							pfd.close();
//						} catch (IOException e) {
//							Log.e(TAG, "IOException", e);
//						}
//					}
//				}
//				
//			}else{
//				return drawable;
//			}
//		}else{
//			return getDefaultArtwork(context);
//		}
//		return null;
//	}
//	
//	
//	private static Drawable getDefaultArtwork(Context context){
//		return context.getResources().getDrawable(R.drawable.fallback_cover);
//	}
//
//    // A really simple BitmapDrawable-like class, that doesn't do
//    // scaling, dithering or filtering.
//    private static class FastBitmapDrawable extends Drawable {
//        private Bitmap mBitmap;
//        public FastBitmapDrawable(Bitmap b) {
//            mBitmap = b;
//        }
//        @Override
//        public void draw(Canvas canvas) {
//            canvas.drawBitmap(mBitmap, 0, 0, null);
//        }
//        @Override
//        public int getOpacity() {
//            return PixelFormat.OPAQUE;
//        }
//        @Override
//        public void setAlpha(int alpha) {
//        }
//        @Override
//        public void setColorFilter(ColorFilter cf) {
//        }
//    }
	
	
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final HashMap<Long, Bitmap> sArtCache = new HashMap<Long, Bitmap>();
    private static int sArtCacheId = -1;

    static {
            // for the cache,
            // 565 is faster to decode and display
            // and we don't want to dither here because the image will be scaled
            // down later
            sBitmapOptionsCache.inPreferredConfig = Bitmap.Config.RGB_565;
            sBitmapOptionsCache.inDither = false;

            sBitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            sBitmapOptions.inDither = false;
    }

    public static IMediaPlaybackService sService;
    
    public static void initAlbumArtCache(IMediaPlaybackService service) {
    	
            try {
            		sService=service;
                    int id = sService.getMediaMountedCount();
                    if (id != sArtCacheId) {
                            clearAlbumArtCache();
                            sArtCacheId = id;
                    }
            } catch (RemoteException e) {
                    e.printStackTrace();
            }
    }

    public static void clearAlbumArtCache() {

            synchronized (sArtCache) {
                    sArtCache.clear();
            }
    }

    public static Bitmap getCachedArtwork(Context context, long artIndex, int width, int height) {

            Bitmap bitmap = null;
            synchronized (sArtCache) {
                    bitmap = sArtCache.get(artIndex);
            }
            if (bitmap == null) {
                    bitmap = MediaUtils.getArtworkQuick(context, artIndex, width, height);
                    if (bitmap != null) {
                            synchronized (sArtCache) {
                                    sArtCache.put(artIndex, bitmap);
                            }
                    }
            }
            return bitmap;
    }

    // Get album art for specified album. This method will not try to
    // fall back to getting artwork directly from the file, nor will
    // it attempt to repair the database.
    public static Bitmap getArtworkQuick(Context context, long album_id, int w, int h) {

            // NOTE: There is in fact a 1 pixel border on the right side in the
            // ImageView
            // used to display this drawable. Take it into account now, so we don't
            // have to
            // scale later.
            w -= 1;
            ContentResolver res = context.getContentResolver();
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            if (uri != null) {
                    ParcelFileDescriptor fd = null;
                    try {
                            fd = res.openFileDescriptor(uri, "r");
                            int sampleSize = 1;

                            // Compute the closest power-of-two scale factor
                            // and pass that to sBitmapOptionsCache.inSampleSize, which will
                            // result in faster decoding and better quality
                            sBitmapOptionsCache.inJustDecodeBounds = true;
                            BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null,
                                            sBitmapOptionsCache);
                            int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                            int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                            while (nextWidth > w && nextHeight > h) {
                                    sampleSize <<= 1;
                                    nextWidth >>= 1;
                                    nextHeight >>= 1;
                            }

                            sBitmapOptionsCache.inSampleSize = sampleSize;
                            sBitmapOptionsCache.inJustDecodeBounds = false;
                            Bitmap b = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null,
                                            sBitmapOptionsCache);

                            if (b != null) {
                                    // finally rescale to exactly the size we need
                                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                                            Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                                            // Bitmap.createScaledBitmap() can return the same
                                            // bitmap
                                            if (tmp != b) b.recycle();
                                            b = tmp;
                                    }
                            }

                            return b;
                    } catch (FileNotFoundException e) {
                    } finally {
                            try {
                                    if (fd != null) fd.close();
                            } catch (IOException e) {
                            }
                    }
            }
            return null;
    }

    /**
     * Get album art for specified album. You should not pass in the album id
     * for the "unknown" album here (use -1 instead) This method always returns
     * the default album art icon when no album art is found.
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id) {

            return getArtwork(context, song_id, album_id, true);
    }

    /**
     * Get album art for specified album. You should not pass in the album id
     * for the "unknown" album here (use -1 instead)
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id,
                    boolean allowdefault) {

            if (album_id < 0) {
                    // This is something that is not in the database, so get the album
                    // art directly
                    // from the file.
                    if (song_id >= 0) {
                            Bitmap bm = getArtworkFromFile(context, song_id, -1);
                            if (bm != null) {
                                    return bm;
                            }
                    }
                    if (allowdefault) {
                            return getDefaultArtwork(context);
                    }
                    return null;
            }

            ContentResolver res = context.getContentResolver();
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            if (uri != null) {
                    InputStream in = null;
                    try {
                            in = res.openInputStream(uri);
                            return BitmapFactory.decodeStream(in, null, sBitmapOptions);
                    } catch (FileNotFoundException ex) {
                            // The album art thumbnail does not actually exist. Maybe the
                            // user deleted it, or
                            // maybe it never existed to begin with.
                            Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                            if (bm != null) {
                                    if (bm.getConfig() == null) {
                                            bm = bm.copy(Bitmap.Config.ARGB_8888, false);
                                            if (bm == null && allowdefault) {
                                                    return getDefaultArtwork(context);
                                            }
                                    }
                            } else if (allowdefault) {
                                    bm = getDefaultArtwork(context);
                            }
                            return bm;
                    } finally {
                            try {
                                    if (in != null) {
                                            in.close();
                                    }
                            } catch (IOException ex) {
                            }
                    }
            }

            return null;
    }

    // get album art for specified file
    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {

            Bitmap bm = null;

            if (albumid < 0 && songid < 0) {
                    throw new IllegalArgumentException("Must specify an album or a song id");
            }

            try {
                    if (albumid < 0) {
                            Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                            ParcelFileDescriptor pfd = context.getContentResolver()
                                            .openFileDescriptor(uri, "r");
                            if (pfd != null) {
                                    FileDescriptor fd = pfd.getFileDescriptor();
                                    bm = BitmapFactory.decodeFileDescriptor(fd);
                            }
                    } else {
                            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                            ParcelFileDescriptor pfd = context.getContentResolver()
                                            .openFileDescriptor(uri, "r");
                            if (pfd != null) {
                                    FileDescriptor fd = pfd.getFileDescriptor();
                                    bm = BitmapFactory.decodeFileDescriptor(fd);
                            }
                    }
            } catch (IllegalStateException ex) {
            } catch (FileNotFoundException ex) {
            }
            return bm;
    }

    private static Bitmap getDefaultArtwork(Context context) {

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeStream(
                            context.getResources().openRawResource(R.drawable.fallback_cover), null,
                            opts);
    }

    public static Uri getArtworkUri(Context context, long song_id, long album_id) {

            if (album_id < 0) {
                    // This is something that is not in the database, so get the album
                    // art directly
                    // from the file.
                    if (song_id >= 0) {
                            return getArtworkUriFromFile(context, song_id, -1);
                    }
                    return null;
            }

            ContentResolver res = context.getContentResolver();
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            if (uri != null) {
                    InputStream in = null;
                    try {
                            in = res.openInputStream(uri);
                            return uri;
                    } catch (FileNotFoundException ex) {
                            // The album art thumbnail does not actually exist. Maybe the
                            // user deleted it, or
                            // maybe it never existed to begin with.
                            return getArtworkUriFromFile(context, song_id, album_id);
                    } finally {
                            try {
                                    if (in != null) {
                                            in.close();
                                    }
                            } catch (IOException ex) {
                            }
                    }
            }
            return null;
    }

    private static Uri getArtworkUriFromFile(Context context, long songid, long albumid) {

            if (albumid < 0 && songid < 0) {
                    return null;
            }

            try {
                    if (albumid < 0) {
                            Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                            ParcelFileDescriptor pfd = context.getContentResolver()
                                            .openFileDescriptor(uri, "r");
                            if (pfd != null) {
                                    return uri;
                            }
                    } else {
                            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                            ParcelFileDescriptor pfd = context.getContentResolver()
                                            .openFileDescriptor(uri, "r");
                            if (pfd != null) {
                                    return uri;
                            }
                    }
            } catch (FileNotFoundException ex) {
                    //
            }
            return null;
    }
	
}
