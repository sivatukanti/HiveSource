// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum.provider;

import org.apache.kerby.kerberos.kerb.KrbException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class MessageDigestHashProvider extends AbstractHashProvider
{
    private String algorithm;
    protected MessageDigest messageDigest;
    
    public MessageDigestHashProvider(final int hashSize, final int blockSize, final String algorithm) {
        super(hashSize, blockSize);
        this.algorithm = algorithm;
        this.init();
    }
    
    @Override
    protected void init() {
        try {
            this.messageDigest = MessageDigest.getInstance(this.algorithm);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to init JCE provider", e);
        }
    }
    
    @Override
    public void hash(final byte[] data, final int start, final int len) throws KrbException {
        this.messageDigest.update(data, start, len);
    }
    
    @Override
    public byte[] output() {
        return this.messageDigest.digest();
    }
}
