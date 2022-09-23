// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface GSSContext
{
    void create(final String p0, final String p1) throws JSchException;
    
    boolean isEstablished();
    
    byte[] init(final byte[] p0, final int p1, final int p2) throws JSchException;
    
    byte[] getMIC(final byte[] p0, final int p1, final int p2);
    
    void dispose();
}
