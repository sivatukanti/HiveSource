// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface KeyPairGenRSA
{
    void init(final int p0) throws Exception;
    
    byte[] getD();
    
    byte[] getE();
    
    byte[] getN();
    
    byte[] getC();
    
    byte[] getEP();
    
    byte[] getEQ();
    
    byte[] getP();
    
    byte[] getQ();
}
