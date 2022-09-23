// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto;

import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;

public interface EncTypeHandler extends CryptoTypeHandler
{
    EncryptionType eType();
    
    int keyInputSize();
    
    int keySize();
    
    int confounderSize();
    
    int checksumSize();
    
    int prfSize();
    
    byte[] prf(final byte[] p0, final byte[] p1) throws KrbException;
    
    int paddingSize();
    
    byte[] str2key(final String p0, final String p1, final byte[] p2) throws KrbException;
    
    byte[] random2Key(final byte[] p0) throws KrbException;
    
    CheckSumType checksumType();
    
    byte[] encrypt(final byte[] p0, final byte[] p1, final int p2) throws KrbException;
    
    byte[] encrypt(final byte[] p0, final byte[] p1, final byte[] p2, final int p3) throws KrbException;
    
    byte[] decrypt(final byte[] p0, final byte[] p1, final int p2) throws KrbException;
    
    byte[] decrypt(final byte[] p0, final byte[] p1, final byte[] p2, final int p3) throws KrbException;
}
