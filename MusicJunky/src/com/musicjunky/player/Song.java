package com.musicjunky.player;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

public class Song implements Parcelable {

	private String title;
	private String artistName;
	private String albumName;
	private String format;
	private String genre;
	private String bitrate;

	private int playType;
	private int year;
	private int trackNumber;
	private int duration;

	private Bitmap albumArt;

	private Uri fileLocation;

	private final static String TAG = Song.class.getName();

	public final static int PLAYLIST = 0;
	public final static int SONG = 1;
	public final static int ARTIST = 2;
	public final static int ALBUM = 3;

	public Song(String path) throws MalformedURLException {
		Uri.Builder uriBuilder=new Uri.Builder();
		uriBuilder.path(path);
		fileLocation=uriBuilder.build();
		init();
	}

	public Song(File audioFile) throws MalformedURLException {
		this(audioFile.getAbsolutePath());
	}

	public Song(Uri uri) {
		this(uri, Song.SONG);
	}

	/**
	 * 
	 * @param url
	 *            location of the song file
	 * @param contentResolver
	 * @param playType
	 *            only possible values are playlist, song artist, and album
	 */
	public Song(Uri uri, int playType) {
		this.fileLocation = uri;
		switch (playType) {
		case PLAYLIST:
			this.playType = playType;
			break;
		case SONG:
			this.playType = playType;
			break;
		case ARTIST:
			this.playType = playType;
			break;
		case ALBUM:
			this.playType = playType;
			break;
		default:
			throw new IllegalArgumentException();
		}
		init();
	}

	private Song(Parcel in) {
		title = in.readString();
		artistName = in.readString();
		albumName = in.readString();
		format = in.readString();
		genre = in.readString();
		bitrate = in.readString();
		playType = in.readInt();
		year = in.readInt();
		trackNumber = in.readInt();
		duration = in.readInt();
		albumArt = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());
		fileLocation = (Uri) in.readParcelable(Uri.class.getClassLoader());
	}

	private void init() {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(fileLocation.getPath());
		setTitle(retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
		setArtistName(retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
		setAlbumName(retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));

		String getFile = fileLocation.getPath();
		setFormat(getFile.substring(getFile.lastIndexOf(".") + 1));

		setGenre(retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
		setBitrate(retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));

		setYear(retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
		setTrackNumber(retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));

		byte[] byteData = retriever.getEmbeddedPicture();
		if(byteData != null){
			Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, 0,
					byteData.length);
			setAlbumArt(bitmap);
		}
		retriever.release();
	}

	/**
	 * 
	 * @param keyName
	 *            the ID3 tag field you want change. Use the
	 *            android.mediastore.audio class to specify the field you want
	 *            to change
	 * @param value
	 *            the value of the id3tag that you want to change
	 * @return returns true if the tag was successfully written
	 */
	public boolean editID3Tag(String keyName, String value) {
		try {
			AudioFile audioFile = AudioFileIO.read(new File(fileLocation
					.getPath()));
			Tag tag = audioFile.getTag();
			if (keyName.equals(MediaStore.Audio.Media.TITLE)) {
				tag.setField(FieldKey.TITLE, value);
			} else if (keyName.equals(MediaStore.Audio.Media.ARTIST)) {
				tag.setField(FieldKey.ARTIST, value);
			} else if (keyName.equals(MediaStore.Audio.Media.ALBUM)) {
				tag.setField(FieldKey.ALBUM, value);
			} else if (keyName.equals(MediaStore.Audio.Genres.NAME)) {
				tag.setField(FieldKey.GENRE, value);
			} else if (keyName.equals(MediaStore.Audio.Media.YEAR)) {
				tag.setField(FieldKey.YEAR, value);
			} else {
				throw new IllegalArgumentException();
			}
			audioFile.commit();
			return true;
		} catch (CannotReadException e) {
			Log.e(TAG, "CannotReadException", e);
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (TagException e) {
			Log.e(TAG, "TagException", e);
		} catch (ReadOnlyFileException e) {
			Log.e(TAG, "ReadOnlyFileException", e);
		} catch (InvalidAudioFrameException e) {
			Log.e(TAG, "InvalidAudioFrameException", e);
		} catch (CannotWriteException e) {
			Log.e(TAG, "CannotWriteException", e);
		}
		return false;
	}

	public int getPlayType() {
		return playType;
	}

	public void setPlayType(int playType) {
		this.playType = playType;
	}

	public Uri getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(Uri fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if(title==null){
			String getFile = fileLocation.getPath();
			this.title=getFile.substring(getFile.lastIndexOf("/")+1,getFile.lastIndexOf("."));
			return;
		}
		this.title = title;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public int getYear() {
		return year;
	}

	public void setYear(String year) {
		if (year != null) {
			this.year = Integer.parseInt(year);
		}
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(String trackNumber) {
		if (trackNumber != null) {
			this.trackNumber = Integer.parseInt(trackNumber);
		}
	}

	public String getBitrate() {
		return bitrate;
	}

	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		if (duration != null) {
			this.duration = Integer.parseInt(duration);
		}
	}

	public Bitmap getAlbumArt() {
		return albumArt;
	}

	public void setAlbumArt(Bitmap albumArt) {
		this.albumArt = albumArt;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(artistName);
		dest.writeString(albumName);
		dest.writeString(format);
		dest.writeString(genre);
		dest.writeString(bitrate);

		dest.writeInt(playType);
		dest.writeInt(year);
		dest.writeInt(trackNumber);
		dest.writeInt(duration);
		dest.writeParcelable(albumArt, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeParcelable(fileLocation,
				Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
	}

	public boolean equals(Song song) {
		if(title.equals(song.getTitle())){
			if(fileLocation.getPath().equals(song.getFileLocation().getPath())){
				if(bitrate.equals(song.getBitrate())){
					return true;
				}
				return false;
			}
			return false;
		}
		return false;
	}
	
	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
		@Override
		public Song createFromParcel(Parcel source) {
			return new Song(source);
		}

		@Override
		public Song[] newArray(int size) {
			return new Song[size];
		}

	};
}
