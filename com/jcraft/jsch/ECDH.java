// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface ECDH
{
    void init(final int p0) throws Exception;
    
    byte[] getSecret(final byte[] p0, final byte[] p1) throws Exception;
    
    byte[] getQ() throws Exception;
    
    boolean validate(final byte[] p0, final byte[] p1) throws Exception;
}
