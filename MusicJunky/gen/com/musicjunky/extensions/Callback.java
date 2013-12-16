/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Nav\\workspace\\MusicJunky\\src\\com\\musicjunky\\extensions\\Callback.aidl
 */
package com.musicjunky.extensions;
public interface Callback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.musicjunky.extensions.Callback
{
private static final java.lang.String DESCRIPTOR = "com.musicjunky.extensions.Callback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.musicjunky.extensions.Callback interface,
 * generating a proxy if needed.
 */
public static com.musicjunky.extensions.Callback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.musicjunky.extensions.Callback))) {
return ((com.musicjunky.extensions.Callback)iin);
}
return new com.musicjunky.extensions.Callback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_callback:
{
data.enforceInterface(DESCRIPTOR);
com.musicjunky.extensions.Letter _arg0;
if ((0!=data.readInt())) {
_arg0 = com.musicjunky.extensions.Letter.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.callback(_arg0);
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.musicjunky.extensions.Callback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void callback(com.musicjunky.extensions.Letter letter) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((letter!=null)) {
_data.writeInt(1);
letter.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_callback, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_callback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void callback(com.musicjunky.extensions.Letter letter) throws android.os.RemoteException;
}
