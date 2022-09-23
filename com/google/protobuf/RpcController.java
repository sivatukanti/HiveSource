// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

public interface RpcController
{
    void reset();
    
    boolean failed();
    
    String errorText();
    
    void startCancel();
    
    void setFailed(final String p0);
    
    boolean isCanceled();
    
    void notifyOnCancel(final RpcCallback<Object> p0);
}
