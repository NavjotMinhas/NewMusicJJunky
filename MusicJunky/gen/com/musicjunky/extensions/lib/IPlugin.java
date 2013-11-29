/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Nav\\workspace\\MusicJunky\\src\\com\\musicjunky\\extensions\\lib\\IPlugin.aidl
 */
package com.musicjunky.extensions.lib;
public interface IPlugin extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.musicjunky.extensions.lib.IPlugin
{
private static final java.lang.String DESCRIPTOR = "com.musicjunky.extensions.lib.IPlugin";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.musicjunky.extensions.lib.IPlugin interface,
 * generating a proxy if needed.
 */
public static com.musicjunky.extensions.lib.IPlugin asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.musicjunky.extensions.lib.IPlugin))) {
return ((com.musicjunky.extensions.lib.IPlugin)iin);
}
return new com.musicjunky.extensions.lib.IPlugin.Stub.Proxy(obj);
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
case TRANSACTION_onInitialize:
{
data.enforceInterface(DESCRIPTOR);
com.musicjunky.extensions.lib.IHostApp _arg0;
_arg0 = com.musicjunky.extensions.lib.IHostApp.Stub.asInterface(data.readStrongBinder());
boolean _arg1;
_arg1 = (0!=data.readInt());
this.onInitialize(_arg0, _arg1);
return true;
}
case TRANSACTION_onUpdate:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onUpdate(_arg0);
return true;
}
case TRANSACTION_auth:
{
data.enforceInterface(DESCRIPTOR);
android.content.ComponentName _arg0;
if ((0!=data.readInt())) {
_arg0 = android.content.ComponentName.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _arg1;
_arg1 = data.readInt();
android.content.Intent _result = this.auth(_arg0, _arg1);
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
private static class Proxy implements com.musicjunky.extensions.lib.IPlugin
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
@Override public void onInitialize(com.musicjunky.extensions.lib.IHostApp host, boolean isReconnect) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((host!=null))?(host.asBinder()):(null)));
_data.writeInt(((isReconnect)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_onInitialize, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void onUpdate(int reason) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(reason);
mRemote.transact(Stub.TRANSACTION_onUpdate, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public android.content.Intent auth(android.content.ComponentName name, int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.Intent _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((name!=null)) {
_data.writeInt(1);
name.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_auth, _data, _reply, 0);
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
}
static final int TRANSACTION_onInitialize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_auth = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void onInitialize(com.musicjunky.extensions.lib.IHostApp host, boolean isReconnect) throws android.os.RemoteException;
public void onUpdate(int reason) throws android.os.RemoteException;
public android.content.Intent auth(android.content.ComponentName name, int uid) throws android.os.RemoteException;
}
