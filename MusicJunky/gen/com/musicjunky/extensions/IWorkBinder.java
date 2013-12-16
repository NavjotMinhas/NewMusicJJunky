/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Nav\\workspace\\MusicJunky\\src\\com\\musicjunky\\extensions\\IWorkBinder.aidl
 */
package com.musicjunky.extensions;
public interface IWorkBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.musicjunky.extensions.IWorkBinder
{
private static final java.lang.String DESCRIPTOR = "com.musicjunky.extensions.IWorkBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.musicjunky.extensions.IWorkBinder interface,
 * generating a proxy if needed.
 */
public static com.musicjunky.extensions.IWorkBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.musicjunky.extensions.IWorkBinder))) {
return ((com.musicjunky.extensions.IWorkBinder)iin);
}
return new com.musicjunky.extensions.IWorkBinder.Stub.Proxy(obj);
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
case TRANSACTION_api:
{
data.enforceInterface(DESCRIPTOR);
com.musicjunky.extensions.Letter _arg0;
if ((0!=data.readInt())) {
_arg0 = com.musicjunky.extensions.Letter.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.musicjunky.extensions.Callback _arg1;
_arg1 = com.musicjunky.extensions.Callback.Stub.asInterface(data.readStrongBinder());
this.api(_arg0, _arg1);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.musicjunky.extensions.IWorkBinder
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
@Override public void api(com.musicjunky.extensions.Letter letter, com.musicjunky.extensions.Callback callback) throws android.os.RemoteException
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
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_api, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_api = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void api(com.musicjunky.extensions.Letter letter, com.musicjunky.extensions.Callback callback) throws android.os.RemoteException;
}
