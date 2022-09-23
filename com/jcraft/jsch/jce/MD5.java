// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.MessageDigest;
import com.jcraft.jsch.HASH;

public class MD5 implements HASH
{
    MessageDigest md;
    
    public int getBlockSize() {
        return 16;
    }
    
    public void init() throws Exception {
        try {
            this.md = MessageDigest.getInstance("MD5");
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
    
    public void update(final byte[] foo, final int start, final int len) throws Exception {
        this.md.update(foo, start, len);
    }
    
    public byte[] digest() throws Exception {
        return this.md.digest();
    }
}
