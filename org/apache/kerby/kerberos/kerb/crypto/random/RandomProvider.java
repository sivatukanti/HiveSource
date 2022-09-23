// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.random;

public interface RandomProvider
{
    void init();
    
    void setSeed(final byte[] p0);
    
    void nextBytes(final byte[] p0);
    
    void destroy();
}
