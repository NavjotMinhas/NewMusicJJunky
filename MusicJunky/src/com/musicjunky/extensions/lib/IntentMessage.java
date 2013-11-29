package com.musicjunky.extensions.lib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class IntentMessage implements Parcelable{

	
	public static final int NONE=0;
	public static final int BACK_TO_PLUGIN=1;
	public static final int SPECIFIED_INTENT=2;
	public static final int BACK_TO_PLUGIN_AND_SPECIFIED_INTENT=3;
	
	public static final String KEY_PAYLOAD_CLASS="payloadClass";
	public static final String KEY_PAYLOAD="payload";
	public static final String KEY_ACTION="action";
	public static final String KEY_BUNDLE="bundle";
	public static final String KEY_RETURN_TYPE="handle_return";
	public static final String KEY_FINISHED_INTENT="finished_intent";
	public static final String KEY_NAME_OF_VALUE_RETURNED="name_of_value_returned";
	
	private static final int VERSION=2;
	
	private Parcelable payload;
	private String payloadClass;
	private String action; // this only required in the creation of the intent, therefore do not put it in the parcel
	private Bundle bundle;
	private int handle_return;
	private Intent finished_intent;
	private String name_of_value_returned;

	public IntentMessage() {
		//do nothing
	}

	private IntentMessage(Parcel in){
		payload=in.readParcelable(Parcelable.class.getClassLoader());
		payloadClass=in.readString();
		bundle=in.readBundle();
		handle_return=in.readInt();
		finished_intent= in.readParcelable(Intent.class.getClassLoader());
		name_of_value_returned=in.readString();
	}
	
	public Parcelable getPayload() {
		return payload;
	}

	public void setPayload(Parcelable payload) {
		this.payload = payload;
	}

	public String getPayloadClass() {
		return payloadClass;
	}

	public void setPayloadClass(String payloadClass) {
		this.payloadClass = payloadClass;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	public int getHandle_return() {
		return handle_return;
	}

	public void setHandle_return(int handle_return) {
		this.handle_return = handle_return;
	}

	public Intent getFinished_intent() {
		return finished_intent;
	}

	public void setFinished_intent(Intent finished_intent) {
		this.finished_intent = finished_intent;
	}

	public String getName_of_value_returned() {
		return name_of_value_returned;
	}

	public void setName_of_value_returned(String name_of_value_returned) {
		this.name_of_value_returned = name_of_value_returned;
	}
	
	public Bundle toBundle(){
		Bundle bundle=new Bundle();
		bundle.putString(KEY_PAYLOAD_CLASS, payloadClass);
		bundle.putParcelable(KEY_PAYLOAD, payload);
		bundle.putString(KEY_ACTION, action);
		bundle.putBundle(KEY_BUNDLE, bundle);
		bundle.putInt(KEY_RETURN_TYPE, handle_return);
		bundle.putParcelable(KEY_FINISHED_INTENT, finished_intent);
		bundle.putString(KEY_NAME_OF_VALUE_RETURNED,name_of_value_returned);
		return bundle;
	}
	
	public void fromBundle(Bundle src) throws ClassNotFoundException{
		payloadClass=src.getString(KEY_PAYLOAD_CLASS,"object");
		if(payloadClass.equals("object")){
			throw new IllegalArgumentException("you need to specify the class type of the payload as a string using the key payloadClass");
		}
		payload=src.getParcelable(KEY_PAYLOAD);
		action=src.getString(KEY_ACTION);
		bundle=src.getBundle(KEY_BUNDLE);
		handle_return=src.getInt(KEY_RETURN_TYPE, 0);
		finished_intent= (Intent)src.getParcelable(KEY_FINISHED_INTENT);
		name_of_value_returned=src.getString(KEY_NAME_OF_VALUE_RETURNED,null);
		if(handle_return<=BACK_TO_PLUGIN_AND_SPECIFIED_INTENT && handle_return>=NONE){
			if(handle_return>NONE){
				if(finished_intent==null){
					throw new IllegalArgumentException("You need to provide a finished_intent or change the value of your handle_return");
				}else if(name_of_value_returned==null){
					throw new IllegalArgumentException("You need to provide a name_of_value_returned so we can attach it to the intent");
				}
			}
		}else{
			throw new IllegalArgumentException("Invalid handle_return code, use the constants defined in the IntentMessage Class");
		}
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeParcelable(payload, 0);
		parcel.writeString(payloadClass);
		parcel.writeBundle(bundle);
		parcel.writeInt(handle_return);
		parcel.writeParcelable(finished_intent, 0);
		parcel.writeString(name_of_value_returned);
		
	}
	
	public static final Parcelable.Creator<IntentMessage> CREATOR=new Parcelable.Creator<IntentMessage>(){
		@Override
		public IntentMessage createFromParcel(Parcel source) {
			return new IntentMessage(source);
		}
		
		@Override
		public IntentMessage[] newArray(int size) {
			return new IntentMessage[size];
		}
	};

}
