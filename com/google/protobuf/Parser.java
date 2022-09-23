// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.io.InputStream;

public interface Parser<MessageType>
{
    MessageType parseFrom(final CodedInputStream p0) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final CodedInputStream p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final CodedInputStream p0) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final CodedInputStream p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final ByteString p0) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final ByteString p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final ByteString p0) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final ByteString p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final byte[] p0, final int p1, final int p2) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final byte[] p0, final int p1, final int p2, final ExtensionRegistryLite p3) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final byte[] p0) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final byte[] p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final byte[] p0, final int p1, final int p2) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final byte[] p0, final int p1, final int p2, final ExtensionRegistryLite p3) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final byte[] p0) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final byte[] p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final InputStream p0) throws InvalidProtocolBufferException;
    
    MessageType parseFrom(final InputStream p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final InputStream p0) throws InvalidProtocolBufferException;
    
    MessageType parsePartialFrom(final InputStream p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parseDelimitedFrom(final InputStream p0) throws InvalidProtocolBufferException;
    
    MessageType parseDelimitedFrom(final InputStream p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
    
    MessageType parsePartialDelimitedFrom(final InputStream p0) throws InvalidProtocolBufferException;
    
    MessageType parsePartialDelimitedFrom(final InputStream p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
}
