// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

public interface Service
{
    Descriptors.ServiceDescriptor getDescriptorForType();
    
    void callMethod(final Descriptors.MethodDescriptor p0, final RpcController p1, final Message p2, final RpcCallback<Message> p3);
    
    Message getRequestPrototype(final Descriptors.MethodDescriptor p0);
    
    Message getResponsePrototype(final Descriptors.MethodDescriptor p0);
}
