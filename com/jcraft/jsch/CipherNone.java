// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class CipherNone implements Cipher
{
    private static final int ivsize = 8;
    private static final int bsize = 16;
    
    public int getIVSize() {
        return 8;
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    public void init(final int mode, final byte[] key, final byte[] iv) throws Exception {
    }
    
    public void update(final byte[] foo, final int s1, final int len, final byte[] bar, final int s2) throws Exception {
    }
    
    public boolean isCBC() {
        return false;
    }
}
