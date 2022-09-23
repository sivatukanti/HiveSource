// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.crypto.AbstractCryptoTypeHandler;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;

public abstract class AbstractKeyedCheckSumTypeHandler extends AbstractCheckSumTypeHandler
{
    private KeyMaker keyMaker;
    
    public AbstractKeyedCheckSumTypeHandler(final EncryptProvider encProvider, final HashProvider hashProvider, final int computeSize, final int outputSize) {
        super(encProvider, hashProvider, computeSize, outputSize);
    }
    
    protected void keyMaker(final KeyMaker keyMaker) {
        this.keyMaker = keyMaker;
    }
    
    protected KeyMaker keyMaker() {
        return this.keyMaker;
    }
    
    @Override
    public byte[] checksumWithKey(final byte[] data, final byte[] key, final int usage) throws KrbException {
        return this.checksumWithKey(data, 0, data.length, key, usage);
    }
    
    @Override
    public byte[] checksumWithKey(final byte[] data, final int start, final int len, final byte[] key, final int usage) throws KrbException {
        final int outputSize = this.outputSize();
        final byte[] tmp = this.doChecksumWithKey(data, start, len, key, usage);
        if (outputSize < tmp.length) {
            final byte[] output = new byte[outputSize];
            System.arraycopy(tmp, 0, output, 0, outputSize);
            return output;
        }
        return tmp;
    }
    
    protected byte[] doChecksumWithKey(final byte[] data, final int start, final int len, final byte[] key, final int usage) throws KrbException {
        return new byte[0];
    }
    
    @Override
    public boolean verifyWithKey(final byte[] data, final byte[] key, final int usage, final byte[] checksum) throws KrbException {
        final byte[] newCksum = this.checksumWithKey(data, key, usage);
        return AbstractCryptoTypeHandler.checksumEqual(checksum, newCksum);
    }
}
