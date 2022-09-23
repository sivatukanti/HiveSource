// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

public interface Buffers
{
    Buffer getHeader();
    
    Buffer getBuffer();
    
    Buffer getBuffer(final int p0);
    
    void returnBuffer(final Buffer p0);
    
    public enum Type
    {
        BYTE_ARRAY, 
        DIRECT, 
        INDIRECT;
    }
}
