// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface KeyPairGenDSA
{
    void init(final int p0) throws Exception;
    
    byte[] getX();
    
    byte[] getY();
    
    byte[] getP();
    
    byte[] getQ();
    
    byte[] getG();
}
