// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.KrbException;

public interface EncryptProvider
{
    int keyInputSize();
    
    int keySize();
    
    int blockSize();
    
    void encrypt(final byte[] p0, final byte[] p1, final byte[] p2) throws KrbException;
    
    void decrypt(final byte[] p0, final byte[] p1, final byte[] p2) throws KrbException;
    
    void encrypt(final byte[] p0, final byte[] p1) throws KrbException;
    
    void decrypt(final byte[] p0, final byte[] p1) throws KrbException;
    
    byte[] cbcMac(final byte[] p0, final byte[] p1, final byte[] p2) throws KrbException;
    
    boolean supportCbcMac();
}
