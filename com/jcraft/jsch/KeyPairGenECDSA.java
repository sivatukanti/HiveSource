// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface KeyPairGenECDSA
{
    void init(final int p0) throws Exception;
    
    byte[] getD();
    
    byte[] getR();
    
    byte[] getS();
}
