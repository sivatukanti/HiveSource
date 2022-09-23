// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc.provider;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public abstract class AbstractEncryptProvider implements EncryptProvider
{
    private int blockSize;
    private int keyInputSize;
    private int keySize;
    
    public AbstractEncryptProvider(final int blockSize, final int keyInputSize, final int keySize) {
        this.blockSize = blockSize;
        this.keyInputSize = keyInputSize;
        this.keySize = keySize;
    }
    
    @Override
    public int keyInputSize() {
        return this.keyInputSize;
    }
    
    @Override
    public int keySize() {
        return this.keySize;
    }
    
    @Override
    public int blockSize() {
        return this.blockSize;
    }
    
    @Override
    public void encrypt(final byte[] key, final byte[] cipherState, final byte[] data) throws KrbException {
        this.doEncrypt(data, key, cipherState, true);
    }
    
    @Override
    public void decrypt(final byte[] key, final byte[] cipherState, final byte[] data) throws KrbException {
        this.doEncrypt(data, key, cipherState, false);
    }
    
    @Override
    public void encrypt(final byte[] key, final byte[] data) throws KrbException {
        final byte[] cipherState = new byte[this.blockSize()];
        this.encrypt(key, cipherState, data);
    }
    
    @Override
    public void decrypt(final byte[] key, final byte[] data) throws KrbException {
        final byte[] cipherState = new byte[this.blockSize()];
        this.decrypt(key, cipherState, data);
    }
    
    protected abstract void doEncrypt(final byte[] p0, final byte[] p1, final byte[] p2, final boolean p3) throws KrbException;
    
    @Override
    public byte[] cbcMac(final byte[] key, final byte[] iv, final byte[] data) throws KrbException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean supportCbcMac() {
        return false;
    }
}
