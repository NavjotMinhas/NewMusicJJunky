package com.musicjunky.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {

	private String type;
	private String ContentURI;
	private String [] uriProjections;
	private Object [] values;
	private Parcelable returnData;
	
	private Message(Parcel in) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

	public static final Parcelable.Creator<Message> CREATOR=new Parcelable.Creator<Message>(){
		
		@Override
		public Message createFromParcel(Parcel source) {
			return new Message(source);
		}
		@Override
		public Message[] newArray(int size) {
			return new Message[size];
		}
		
	};
	
	
}
