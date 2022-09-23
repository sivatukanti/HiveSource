// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface Signature
{
    void init() throws Exception;
    
    void update(final byte[] p0) throws Exception;
    
    boolean verify(final byte[] p0) throws Exception;
    
    byte[] sign() throws Exception;
}
