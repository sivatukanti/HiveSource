// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface Cipher
{
    public static final int ENCRYPT_MODE = 0;
    public static final int DECRYPT_MODE = 1;
    
    int getIVSize();
    
    int getBlockSize();
    
    void init(final int p0, final byte[] p1, final byte[] p2) throws Exception;
    
    void update(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws Exception;
    
    boolean isCBC();
}
