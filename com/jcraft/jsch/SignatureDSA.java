// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface SignatureDSA extends Signature
{
    void setPubKey(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3) throws Exception;
    
    void setPrvKey(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3) throws Exception;
}
