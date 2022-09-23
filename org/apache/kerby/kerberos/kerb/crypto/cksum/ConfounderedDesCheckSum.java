// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum;

import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.crypto.AbstractCryptoTypeHandler;
import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Confounder;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.DesProvider;
import org.slf4j.Logger;

public abstract class ConfounderedDesCheckSum extends AbstractKeyedCheckSumTypeHandler
{
    private static final Logger LOG;
    
    public ConfounderedDesCheckSum(final HashProvider hashProvider, final int computeSize, final int outputSize) {
        super(new DesProvider(), hashProvider, computeSize, outputSize);
    }
    
    @Override
    protected byte[] doChecksumWithKey(final byte[] data, final int start, final int len, final byte[] key, final int usage) throws KrbException {
        final int computeSize = this.computeSize();
        final int blockSize = this.encProvider().blockSize();
        final int hashSize = this.hashProvider().hashSize();
        final byte[] workBuffer = new byte[computeSize];
        final byte[] conf = Confounder.makeBytes(blockSize);
        final byte[] toHash = new byte[blockSize + len];
        System.arraycopy(conf, 0, toHash, 0, blockSize);
        System.arraycopy(data, start, toHash, blockSize, len);
        final HashProvider hashProvider = this.hashProvider();
        hashProvider.hash(toHash);
        final byte[] hash = hashProvider.output();
        System.arraycopy(conf, 0, workBuffer, 0, blockSize);
        System.arraycopy(hash, 0, workBuffer, blockSize, hashSize);
        final byte[] newKey = this.deriveKey(key);
        this.encProvider().encrypt(newKey, workBuffer);
        return workBuffer;
    }
    
    protected byte[] deriveKey(final byte[] key) {
        return this.fixKey(this.xorKey(key));
    }
    
    protected byte[] xorKey(final byte[] key) {
        final byte[] xorKey = new byte[this.encProvider().keySize()];
        System.arraycopy(key, 0, xorKey, 0, key.length);
        for (int i = 0; i < xorKey.length; ++i) {
            xorKey[i] ^= (byte)240;
        }
        return xorKey;
    }
    
    private byte[] fixKey(final byte[] key) {
        boolean isWeak = true;
        try {
            isWeak = DESKeySpec.isWeak(key, 0);
        }
        catch (InvalidKeyException e) {
            ConfounderedDesCheckSum.LOG.error("Invalid key found. ");
        }
        if (isWeak) {
            key[7] ^= (byte)240;
        }
        return key;
    }
    
    @Override
    public boolean verifyWithKey(final byte[] data, final byte[] key, final int usage, final byte[] checksum) throws KrbException {
        final int blockSize = this.encProvider().blockSize();
        final int hashSize = this.hashProvider().hashSize();
        final byte[] newKey = this.deriveKey(key);
        this.encProvider().decrypt(newKey, checksum);
        final byte[] decrypted = checksum;
        final byte[] toHash = new byte[blockSize + data.length];
        System.arraycopy(decrypted, 0, toHash, 0, blockSize);
        System.arraycopy(data, 0, toHash, blockSize, data.length);
        final HashProvider hashProvider = this.hashProvider();
        hashProvider.hash(toHash);
        final byte[] newHash = hashProvider.output();
        return AbstractCryptoTypeHandler.checksumEqual(newHash, decrypted, blockSize, hashSize);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConfounderedDesCheckSum.class);
    }
}
