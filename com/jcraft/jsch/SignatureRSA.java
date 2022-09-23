// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface SignatureRSA extends Signature
{
    void setPubKey(final byte[] p0, final byte[] p1) throws Exception;
    
    void setPrvKey(final byte[] p0, final byte[] p1) throws Exception;
}
