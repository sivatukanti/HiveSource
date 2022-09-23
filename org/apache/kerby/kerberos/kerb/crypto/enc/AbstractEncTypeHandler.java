// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.EncTypeHandler;
import org.apache.kerby.kerberos.kerb.crypto.AbstractCryptoTypeHandler;

public abstract class AbstractEncTypeHandler extends AbstractCryptoTypeHandler implements EncTypeHandler
{
    private int prfSize;
    private KeyMaker keyMaker;
    
    public AbstractEncTypeHandler(final EncryptProvider encProvider, final HashProvider hashProvider, final int prfSize) {
        super(encProvider, hashProvider);
        this.prfSize = prfSize;
    }
    
    protected void keyMaker(final KeyMaker keyMaker) {
        this.keyMaker = keyMaker;
    }
    
    protected KeyMaker keyMaker() {
        return this.keyMaker;
    }
    
    @Override
    public int prfSize() {
        return this.prfSize;
    }
    
    @Override
    public String name() {
        return this.eType().getName();
    }
    
    @Override
    public String displayName() {
        return this.eType().getDisplayName();
    }
    
    protected abstract int paddingLength(final int p0);
    
    @Override
    public int keyInputSize() {
        return this.encProvider().keyInputSize();
    }
    
    @Override
    public int keySize() {
        return this.encProvider().keySize();
    }
    
    @Override
    public int confounderSize() {
        return this.encProvider().blockSize();
    }
    
    @Override
    public int checksumSize() {
        return this.hashProvider().hashSize();
    }
    
    @Override
    public int paddingSize() {
        return this.encProvider().blockSize();
    }
    
    @Override
    public byte[] str2key(final String string, final String salt, final byte[] param) throws KrbException {
        return this.keyMaker.str2key(string, salt, param);
    }
    
    @Override
    public byte[] random2Key(final byte[] randomBits) throws KrbException {
        return this.keyMaker.random2Key(randomBits);
    }
    
    @Override
    public byte[] encrypt(final byte[] data, final byte[] key, final int usage) throws KrbException {
        final byte[] iv = new byte[this.encProvider().blockSize()];
        return this.encrypt(data, key, iv, usage);
    }
    
    @Override
    public byte[] encrypt(final byte[] data, final byte[] key, final byte[] iv, final int usage) throws KrbException {
        final int confounderLen = this.confounderSize();
        final int checksumLen = this.checksumSize();
        final int headerLen = confounderLen + checksumLen;
        final int inputLen = data.length;
        final int paddingLen = this.paddingLength(inputLen);
        final int workLength = headerLen + inputLen + paddingLen;
        final byte[] workBuffer = new byte[workLength];
        System.arraycopy(data, 0, workBuffer, headerLen, data.length);
        final int[] workLens = { confounderLen, checksumLen, inputLen, paddingLen };
        this.encryptWith(workBuffer, workLens, key, iv, usage);
        return workBuffer;
    }
    
    protected void encryptWith(final byte[] workBuffer, final int[] workLens, final byte[] key, final byte[] iv, final int usage) throws KrbException {
    }
    
    @Override
    public byte[] decrypt(final byte[] cipher, final byte[] key, final int usage) throws KrbException {
        final byte[] iv = new byte[this.encProvider().blockSize()];
        return this.decrypt(cipher, key, iv, usage);
    }
    
    @Override
    public byte[] decrypt(final byte[] cipher, final byte[] key, final byte[] iv, final int usage) throws KrbException {
        final int totalLen = cipher.length;
        final int confounderLen = this.confounderSize();
        final int checksumLen = this.checksumSize();
        final int dataLen = totalLen - (confounderLen + checksumLen);
        final int[] workLens = { confounderLen, checksumLen, dataLen };
        return this.decryptWith(cipher, workLens, key, iv, usage);
    }
    
    protected byte[] decryptWith(final byte[] workBuffer, final int[] workLens, final byte[] key, final byte[] iv, final int usage) throws KrbException {
        return null;
    }
}
