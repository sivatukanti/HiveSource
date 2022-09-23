// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import javax.crypto.ShortBufferException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import com.jcraft.jsch.MAC;

abstract class HMAC implements MAC
{
    protected String name;
    protected int bsize;
    protected String algorithm;
    private Mac mac;
    private final byte[] tmp;
    
    HMAC() {
        this.tmp = new byte[4];
    }
    
    public int getBlockSize() {
        return this.bsize;
    }
    
    public void init(byte[] key) throws Exception {
        if (key.length > this.bsize) {
            final byte[] tmp = new byte[this.bsize];
            System.arraycopy(key, 0, tmp, 0, this.bsize);
            key = tmp;
        }
        final SecretKeySpec skey = new SecretKeySpec(key, this.algorithm);
        (this.mac = Mac.getInstance(this.algorithm)).init(skey);
    }
    
    public void update(final int i) {
        this.tmp[0] = (byte)(i >>> 24);
        this.tmp[1] = (byte)(i >>> 16);
        this.tmp[2] = (byte)(i >>> 8);
        this.tmp[3] = (byte)i;
        this.update(this.tmp, 0, 4);
    }
    
    public void update(final byte[] foo, final int s, final int l) {
        this.mac.update(foo, s, l);
    }
    
    public void doFinal(final byte[] buf, final int offset) {
        try {
            this.mac.doFinal(buf, offset);
        }
        catch (ShortBufferException e) {
            System.err.println(e);
        }
    }
    
    public String getName() {
        return this.name;
    }
}
