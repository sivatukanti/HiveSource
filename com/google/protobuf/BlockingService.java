// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

public interface BlockingService
{
    Descriptors.ServiceDescriptor getDescriptorForType();
    
    Message callBlockingMethod(final Descriptors.MethodDescriptor p0, final RpcController p1, final Message p2) throws ServiceException;
    
    Message getRequestPrototype(final Descriptors.MethodDescriptor p0);
    
    Message getResponsePrototype(final Descriptors.MethodDescriptor p0);
}
