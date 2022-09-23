// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.CheckSumTypeHandler;
import org.apache.kerby.kerberos.kerb.crypto.AbstractCryptoTypeHandler;

public abstract class AbstractCheckSumTypeHandler extends AbstractCryptoTypeHandler implements CheckSumTypeHandler
{
    private int computeSize;
    private int outputSize;
    
    public AbstractCheckSumTypeHandler(final EncryptProvider encProvider, final HashProvider hashProvider, final int computeSize, final int outputSize) {
        super(encProvider, hashProvider);
        this.computeSize = computeSize;
        this.outputSize = outputSize;
    }
    
    @Override
    public String name() {
        return this.cksumType().getName();
    }
    
    @Override
    public String displayName() {
        return this.cksumType().getDisplayName();
    }
    
    @Override
    public int computeSize() {
        return this.computeSize;
    }
    
    @Override
    public int outputSize() {
        return this.outputSize;
    }
    
    @Override
    public boolean isSafe() {
        return false;
    }
    
    @Override
    public int cksumSize() {
        return 4;
    }
    
    @Override
    public int keySize() {
        return 0;
    }
    
    @Override
    public int confounderSize() {
        return 0;
    }
    
    @Override
    public byte[] checksum(final byte[] data) throws KrbException {
        return this.checksum(data, 0, data.length);
    }
    
    @Override
    public byte[] checksum(final byte[] data, final int start, final int size) throws KrbException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean verify(final byte[] data, final byte[] checksum) throws KrbException {
        return this.verify(data, 0, data.length, checksum);
    }
    
    @Override
    public boolean verify(final byte[] data, final int start, final int size, final byte[] checksum) throws KrbException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public byte[] checksumWithKey(final byte[] data, final byte[] key, final int usage) throws KrbException {
        return this.checksumWithKey(data, 0, data.length, key, usage);
    }
    
    @Override
    public byte[] checksumWithKey(final byte[] data, final int start, final int size, final byte[] key, final int usage) throws KrbException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean verifyWithKey(final byte[] data, final byte[] key, final int usage, final byte[] checksum) throws KrbException {
        throw new UnsupportedOperationException();
    }
}
