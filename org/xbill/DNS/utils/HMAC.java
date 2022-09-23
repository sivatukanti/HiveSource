// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS.utils;

import java.util.Arrays;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class HMAC
{
    private MessageDigest digest;
    private int blockLength;
    private byte[] ipad;
    private byte[] opad;
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    
    private void init(byte[] key) {
        if (key.length > this.blockLength) {
            key = this.digest.digest(key);
            this.digest.reset();
        }
        this.ipad = new byte[this.blockLength];
        this.opad = new byte[this.blockLength];
        int i;
        for (i = 0; i < key.length; ++i) {
            this.ipad[i] = (byte)(key[i] ^ 0x36);
            this.opad[i] = (byte)(key[i] ^ 0x5C);
        }
        while (i < this.blockLength) {
            this.ipad[i] = 54;
            this.opad[i] = 92;
            ++i;
        }
        this.digest.update(this.ipad);
    }
    
    public HMAC(final MessageDigest digest, final int blockLength, final byte[] key) {
        digest.reset();
        this.digest = digest;
        this.blockLength = blockLength;
        this.init(key);
    }
    
    public HMAC(final String digestName, final int blockLength, final byte[] key) {
        try {
            this.digest = MessageDigest.getInstance(digestName);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("unknown digest algorithm " + digestName);
        }
        this.blockLength = blockLength;
        this.init(key);
    }
    
    public HMAC(final MessageDigest digest, final byte[] key) {
        this(digest, 64, key);
    }
    
    public HMAC(final String digestName, final byte[] key) {
        this(digestName, 64, key);
    }
    
    public void update(final byte[] b, final int offset, final int length) {
        this.digest.update(b, offset, length);
    }
    
    public void update(final byte[] b) {
        this.digest.update(b);
    }
    
    public byte[] sign() {
        final byte[] output = this.digest.digest();
        this.digest.reset();
        this.digest.update(this.opad);
        return this.digest.digest(output);
    }
    
    public boolean verify(final byte[] signature) {
        return this.verify(signature, false);
    }
    
    public boolean verify(final byte[] signature, final boolean truncation_ok) {
        byte[] expected = this.sign();
        if (truncation_ok && signature.length < expected.length) {
            final byte[] truncated = new byte[signature.length];
            System.arraycopy(expected, 0, truncated, 0, truncated.length);
            expected = truncated;
        }
        return Arrays.equals(signature, expected);
    }
    
    public void clear() {
        this.digest.reset();
        this.digest.update(this.ipad);
    }
    
    public int digestLength() {
        return this.digest.getDigestLength();
    }
}
