// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.io.InputStream;
import java.io.IOException;

public interface Message extends MessageLite, MessageOrBuilder
{
    Parser<? extends Message> getParserForType();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
    
    Builder newBuilderForType();
    
    Builder toBuilder();
    
    public interface Builder extends MessageLite.Builder, MessageOrBuilder
    {
        Builder clear();
        
        Builder mergeFrom(final Message p0);
        
        Message build();
        
        Message buildPartial();
        
        Builder clone();
        
        Builder mergeFrom(final CodedInputStream p0) throws IOException;
        
        Builder mergeFrom(final CodedInputStream p0, final ExtensionRegistryLite p1) throws IOException;
        
        Descriptors.Descriptor getDescriptorForType();
        
        Builder newBuilderForField(final Descriptors.FieldDescriptor p0);
        
        Builder getFieldBuilder(final Descriptors.FieldDescriptor p0);
        
        Builder setField(final Descriptors.FieldDescriptor p0, final Object p1);
        
        Builder clearField(final Descriptors.FieldDescriptor p0);
        
        Builder setRepeatedField(final Descriptors.FieldDescriptor p0, final int p1, final Object p2);
        
        Builder addRepeatedField(final Descriptors.FieldDescriptor p0, final Object p1);
        
        Builder setUnknownFields(final UnknownFieldSet p0);
        
        Builder mergeUnknownFields(final UnknownFieldSet p0);
        
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
