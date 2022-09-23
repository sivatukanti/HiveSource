// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.async;

public interface AsyncMethodCallback<T>
{
    void onComplete(final T p0);
    
    void onError(final Exception p0);
}
