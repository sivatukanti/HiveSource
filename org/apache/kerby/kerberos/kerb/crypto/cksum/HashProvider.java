// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.KrbException;

public interface HashProvider
{
    int hashSize();
    
    int blockSize();
    
    void hash(final byte[] p0, final int p1, final int p2) throws KrbException;
    
    void hash(final byte[] p0) throws KrbException;
    
    byte[] output();
}
