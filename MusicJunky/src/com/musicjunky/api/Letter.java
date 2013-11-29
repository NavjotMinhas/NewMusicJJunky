package com.musicjunky.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Letter implements Parcelable {

	private String sender;
	private String from;
	private Message message;
	
	private Letter(Parcel in) {
		this.sender=in.readString();
		this.from=in.readString();
		this.message=in.readParcelable(Message.class.getClassLoader());
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	}
	
	public static final Parcelable.Creator<Letter> CREATOR=new Parcelable.Creator<Letter>(){

		@Override
		public Letter createFromParcel(Parcel source) {
			return new Letter(source);
		}

		@Override
		public Letter[] newArray(int size) {
			return new Letter[size];
		}
		
		
	};
	
}
