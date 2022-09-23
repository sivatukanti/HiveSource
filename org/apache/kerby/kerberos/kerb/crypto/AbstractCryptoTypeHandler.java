// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto;

import java.util.Arrays;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public abstract class AbstractCryptoTypeHandler implements CryptoTypeHandler
{
    private EncryptProvider encProvider;
    private HashProvider hashProvider;
    
    public AbstractCryptoTypeHandler(final EncryptProvider encProvider, final HashProvider hashProvider) {
        this.encProvider = encProvider;
        this.hashProvider = hashProvider;
    }
    
    @Override
    public EncryptProvider encProvider() {
        return this.encProvider;
    }
    
    @Override
    public HashProvider hashProvider() {
        return this.hashProvider;
    }
    
    protected static boolean checksumEqual(final byte[] cksum1, final byte[] cksum2) {
        return Arrays.equals(cksum1, cksum2);
    }
    
    protected static boolean checksumEqual(final byte[] cksum1, final byte[] cksum2, final int cksum2Start, final int len) {
        if (cksum1 == cksum2) {
            return true;
        }
        if (cksum1 == null || cksum2 == null) {
            return false;
        }
        if (len <= cksum2.length && len <= cksum1.length) {
            for (int i = 0; i < len; ++i) {
                if (cksum1[i] != cksum2[cksum2Start + i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
