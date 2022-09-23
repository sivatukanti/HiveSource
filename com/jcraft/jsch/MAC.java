// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface MAC
{
    String getName();
    
    int getBlockSize();
    
    void init(final byte[] p0) throws Exception;
    
    void update(final byte[] p0, final int p1, final int p2);
    
    void update(final int p0);
    
    void doFinal(final byte[] p0, final int p1);
}
