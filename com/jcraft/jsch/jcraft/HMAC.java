// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jcraft;

import java.security.MessageDigest;

class HMAC
{
    private static final int B = 64;
    private byte[] k_ipad;
    private byte[] k_opad;
    private MessageDigest md;
    private int bsize;
    private final byte[] tmp;
    
    HMAC() {
        this.k_ipad = null;
        this.k_opad = null;
        this.md = null;
        this.bsize = 0;
        this.tmp = new byte[4];
    }
    
    protected void setH(final MessageDigest md) {
        this.md = md;
        this.bsize = md.getDigestLength();
    }
    
    public int getBlockSize() {
        return this.bsize;
    }
    
    public void init(byte[] key) throws Exception {
        this.md.reset();
        if (key.length > this.bsize) {
            final byte[] tmp = new byte[this.bsize];
            System.arraycopy(key, 0, tmp, 0, this.bsize);
            key = tmp;
        }
        if (key.length > 64) {
            this.md.update(key, 0, key.length);
            key = this.md.digest();
        }
        System.arraycopy(key, 0, this.k_ipad = new byte[64], 0, key.length);
        System.arraycopy(key, 0, this.k_opad = new byte[64], 0, key.length);
        for (int i = 0; i < 64; ++i) {
            final byte[] k_ipad = this.k_ipad;
            final int n = i;
            k_ipad[n] ^= 0x36;
            final byte[] k_opad = this.k_opad;
            final int n2 = i;
            k_opad[n2] ^= 0x5C;
        }
        this.md.update(this.k_ipad, 0, 64);
    }
    
    public void update(final int i) {
        this.tmp[0] = (byte)(i >>> 24);
        this.tmp[1] = (byte)(i >>> 16);
        this.tmp[2] = (byte)(i >>> 8);
        this.tmp[3] = (byte)i;
        this.update(this.tmp, 0, 4);
    }
    
    public void update(final byte[] foo, final int s, final int l) {
        this.md.update(foo, s, l);
    }
    
    public void doFinal(final byte[] buf, final int offset) {
        final byte[] result = this.md.digest();
        this.md.update(this.k_opad, 0, 64);
        this.md.update(result, 0, this.bsize);
        try {
            this.md.digest(buf, offset, this.bsize);
        }
        catch (Exception ex) {}
        this.md.update(this.k_ipad, 0, 64);
    }
}
