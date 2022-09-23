// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.SecureRandom;

public class Random implements com.jcraft.jsch.Random
{
    private byte[] tmp;
    private SecureRandom random;
    
    public Random() {
        this.tmp = new byte[16];
        this.random = null;
        this.random = new SecureRandom();
    }
    
    public void fill(final byte[] foo, final int start, final int len) {
        if (len > this.tmp.length) {
            this.tmp = new byte[len];
        }
        this.random.nextBytes(this.tmp);
        System.arraycopy(this.tmp, 0, foo, start, len);
    }
}
