// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Map;
import java.util.List;

public interface MessageOrBuilder extends MessageLiteOrBuilder
{
    Message getDefaultInstanceForType();
    
    List<String> findInitializationErrors();
    
    String getInitializationErrorString();
    
    Descriptors.Descriptor getDescriptorForType();
    
    Map<Descriptors.FieldDescriptor, Object> getAllFields();
    
    boolean hasField(final Descriptors.FieldDescriptor p0);
    
    Object getField(final Descriptors.FieldDescriptor p0);
    
    int getRepeatedFieldCount(final Descriptors.FieldDescriptor p0);
    
    Object getRepeatedField(final Descriptors.FieldDescriptor p0, final int p1);
    
    UnknownFieldSet getUnknownFields();
}
