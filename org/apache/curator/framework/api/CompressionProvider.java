// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

public interface CompressionProvider
{
    byte[] compress(final String p0, final byte[] p1) throws Exception;
    
    byte[] decompress(final String p0, final byte[] p1) throws Exception;
}
