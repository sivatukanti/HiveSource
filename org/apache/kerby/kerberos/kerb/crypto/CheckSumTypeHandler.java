// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;

public interface CheckSumTypeHandler extends CryptoTypeHandler
{
    int confounderSize();
    
    CheckSumType cksumType();
    
    int computeSize();
    
    int outputSize();
    
    boolean isSafe();
    
    int cksumSize();
    
    int keySize();
    
    byte[] checksum(final byte[] p0) throws KrbException;
    
    byte[] checksum(final byte[] p0, final int p1, final int p2) throws KrbException;
    
    boolean verify(final byte[] p0, final byte[] p1) throws KrbException;
    
    boolean verify(final byte[] p0, final int p1, final int p2, final byte[] p3) throws KrbException;
    
    byte[] checksumWithKey(final byte[] p0, final byte[] p1, final int p2) throws KrbException;
    
    byte[] checksumWithKey(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws KrbException;
    
    boolean verifyWithKey(final byte[] p0, final byte[] p1, final int p2, final byte[] p3) throws KrbException;
}
