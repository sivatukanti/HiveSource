// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public interface MessageLite extends MessageLiteOrBuilder
{
    void writeTo(final CodedOutputStream p0) throws IOException;
    
    int getSerializedSize();
    
    Parser<? extends MessageLite> getParserForType();
    
    ByteString toByteString();
    
    byte[] toByteArray();
    
    void writeTo(final OutputStream p0) throws IOException;
    
    void writeDelimitedTo(final OutputStream p0) throws IOException;
    
    Builder newBuilderForType();
    
    Builder toBuilder();
    
    public interface Builder extends MessageLiteOrBuilder, Cloneable
    {
        Builder clear();
        
        MessageLite build();
        
        MessageLite buildPartial();
        
        Builder clone();
        
        Builder mergeFrom(final CodedInputStream p0) throws IOException;
        
        Builder mergeFrom(final CodedInputStream p0, final ExtensionRegistryLite p1) throws IOException;
        
        Builder mergeFrom(final ByteString p0) throws InvalidProtocolBufferException;
        
        Builder mergeFrom(final ByteString p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
        
        Builder mergeFrom(final byte[] p0) throws InvalidProtocolBufferException;
        
        Builder mergeFrom(final byte[] p0, final int p1, final int p2) throws InvalidProtocolBufferException;
        
        Builder mergeFrom(final byte[] p0, final ExtensionRegistryLite p1) throws InvalidProtocolBufferException;
        
        Builder mergeFrom(final byte[] p0, final int p1, final int p2, final ExtensionRegistryLite p3) throws InvalidProtocolBufferException;
        
        Builder mergeFrom(final InputStream p0) throws IOException;
        
        Builder mergeFrom(final InputStream p0, final ExtensionRegistryLite p1) throws IOException;
        
        boolean mergeDelimitedFrom(final InputStream p0) throws IOException;
        
        boolean mergeDelimitedFrom(final InputStream p0, final ExtensionRegistryLite p1) throws IOException;
    }
}
