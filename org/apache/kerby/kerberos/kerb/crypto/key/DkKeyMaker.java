// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.key;

import org.apache.kerby.kerberos.kerb.crypto.util.Nfold;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public abstract class DkKeyMaker extends AbstractKeyMaker
{
    public DkKeyMaker(final EncryptProvider encProvider) {
        super(encProvider);
    }
    
    public byte[] dk(final byte[] key, final byte[] constant) throws KrbException {
        return this.random2Key(this.dr(key, constant));
    }
    
    protected byte[] dr(final byte[] key, final byte[] constant) throws KrbException {
        final int blocksize = this.encProvider().blockSize();
        final int keyInuptSize = this.encProvider().keyInputSize();
        final byte[] keyBytes = new byte[keyInuptSize];
        byte[] ki;
        if (constant.length != blocksize) {
            ki = Nfold.nfold(constant, blocksize);
        }
        else {
            ki = new byte[constant.length];
            System.arraycopy(constant, 0, ki, 0, constant.length);
        }
        for (int n = 0; n < keyInuptSize; n += blocksize) {
            this.encProvider().encrypt(key, ki);
            if (n + blocksize >= keyInuptSize) {
                System.arraycopy(ki, 0, keyBytes, n, keyInuptSize - n);
                break;
            }
            System.arraycopy(ki, 0, keyBytes, n, blocksize);
        }
        return keyBytes;
    }
}
