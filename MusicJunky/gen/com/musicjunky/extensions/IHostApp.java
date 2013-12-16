/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Nav\\workspace\\MusicJunky\\src\\com\\musicjunky\\extensions\\IHostApp.aidl
 */
package com.musicjunky.extensions;
public interface IHostApp extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.musicjunky.extensions.IHostApp
{
private static final java.lang.String DESCRIPTOR = "com.musicjunky.extensions.IHostApp";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.musicjunky.extensions.IHostApp interface,
 * generating a proxy if needed.
 */
public static com.musicjunky.extensions.IHostApp asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.musicjunky.extensions.IHostApp))) {
return ((com.musicjunky.extensions.IHostApp)iin);
}
return new com.musicjunky.extensions.IHostApp.Stub.Proxy(obj);
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
case TRANSACTION_authHandshake:
{
data.enforceInterface(DESCRIPTOR);
android.content.Intent _arg0;
if ((0!=data.readInt())) {
_arg0 = android.content.Intent.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.content.Intent _result = this.authHandshake(_arg0);
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
case TRANSACTION_publishUpdate:
{
data.enforceInterface(DESCRIPTOR);
com.musicjunky.extensions.Letter _arg0;
if ((0!=data.readInt())) {
_arg0 = com.musicjunky.extensions.Letter.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.publishUpdate(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.musicjunky.extensions.IHostApp
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
@Override public android.content.Intent authHandshake(android.content.Intent intent) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.Intent _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((intent!=null)) {
_data.writeInt(1);
intent.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_authHandshake, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.content.Intent.CREATOR.createFromParcel(_reply);
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
@Override public void publishUpdate(com.musicjunky.extensions.Letter letter) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_publishUpdate, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_authHandshake = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_publishUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public android.content.Intent authHandshake(android.content.Intent intent) throws android.os.RemoteException;
public void publishUpdate(com.musicjunky.extensions.Letter letter) throws android.os.RemoteException;
}
