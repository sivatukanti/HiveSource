// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum.provider;

import org.apache.kerby.kerberos.kerb.crypto.AbstractCryptoTypeHandler;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.AbstractCheckSumTypeHandler;

public abstract class AbstractUnkeyedCheckSumTypeHandler extends AbstractCheckSumTypeHandler
{
    public AbstractUnkeyedCheckSumTypeHandler(final HashProvider hashProvider, final int computeSize, final int outputSize) {
        super(null, hashProvider, computeSize, outputSize);
    }
    
    @Override
    public byte[] checksum(final byte[] data, final int start, final int len) throws KrbException {
        final int outputSize = this.outputSize();
        final HashProvider hp = this.hashProvider();
        hp.hash(data, start, len);
        final byte[] workBuffer = hp.output();
        if (outputSize < workBuffer.length) {
            final byte[] output = new byte[outputSize];
            System.arraycopy(workBuffer, 0, output, 0, outputSize);
            return output;
        }
        return workBuffer;
    }
    
    @Override
    public boolean verify(final byte[] data, final int start, final int len, final byte[] checksum) throws KrbException {
        final byte[] newCksum = this.checksum(data, start, len);
        return AbstractCryptoTypeHandler.checksumEqual(newCksum, checksum);
    }
}
