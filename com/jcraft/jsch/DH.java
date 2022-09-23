// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface DH
{
    void init() throws Exception;
    
    void setP(final byte[] p0);
    
    void setG(final byte[] p0);
    
    byte[] getE() throws Exception;
    
    void setF(final byte[] p0);
    
    byte[] getK() throws Exception;
    
    void checkRange() throws Exception;
}
