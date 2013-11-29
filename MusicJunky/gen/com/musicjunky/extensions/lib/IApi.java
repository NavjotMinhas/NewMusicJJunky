/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Nav\\workspace\\MusicJunky\\src\\com\\musicjunky\\extensions\\lib\\IApi.aidl
 */
package com.musicjunky.extensions.lib;
public interface IApi extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.musicjunky.extensions.lib.IApi
{
private static final java.lang.String DESCRIPTOR = "com.musicjunky.extensions.lib.IApi";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.musicjunky.extensions.lib.IApi interface,
 * generating a proxy if needed.
 */
public static com.musicjunky.extensions.lib.IApi asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.musicjunky.extensions.lib.IApi))) {
return ((com.musicjunky.extensions.lib.IApi)iin);
}
return new com.musicjunky.extensions.lib.IApi.Stub.Proxy(obj);
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
case TRANSACTION_asyncApi:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.asyncApi(_arg0);
return true;
}
case TRANSACTION_syncApi:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.syncApi(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_api:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.musicjunky.extensions.lib.Letter _result = this.api(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.musicjunky.extensions.lib.IApi
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
@Override public void asyncApi(java.lang.String str) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(str);
mRemote.transact(Stub.TRANSACTION_asyncApi, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void syncApi(java.lang.String str) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(str);
mRemote.transact(Stub.TRANSACTION_syncApi, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public com.musicjunky.extensions.lib.Letter api(java.lang.String str) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.musicjunky.extensions.lib.Letter _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(str);
mRemote.transact(Stub.TRANSACTION_api, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.musicjunky.extensions.lib.Letter.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_asyncApi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_syncApi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_api = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void asyncApi(java.lang.String str) throws android.os.RemoteException;
public void syncApi(java.lang.String str) throws android.os.RemoteException;
public com.musicjunky.extensions.lib.Letter api(java.lang.String str) throws android.os.RemoteException;
}
