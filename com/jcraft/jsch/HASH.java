// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface HASH
{
    void init() throws Exception;
    
    int getBlockSize();
    
    void update(final byte[] p0, final int p1, final int p2) throws Exception;
    
    byte[] digest() throws Exception;
}
