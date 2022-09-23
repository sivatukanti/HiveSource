// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

interface BackgroundOperation<T>
{
    void performBackgroundOperation(final OperationAndData<T> p0) throws Exception;
}
