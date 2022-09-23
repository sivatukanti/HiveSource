// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

public interface PathAndBytesable<T>
{
    T forPath(final String p0, final byte[] p1) throws Exception;
    
    T forPath(final String p0) throws Exception;
}
