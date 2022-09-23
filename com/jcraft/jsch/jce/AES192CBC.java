// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.jcraft.jsch.Cipher;

public class AES192CBC implements Cipher
{
    private static final int ivsize = 16;
    private static final int bsize = 24;
    private javax.crypto.Cipher cipher;
    
    public int getIVSize() {
        return 16;
    }
    
    public int getBlockSize() {
        return 24;
    }
    
    public void init(final int mode, byte[] key, byte[] iv) throws Exception {
        final String pad = "NoPadding";
        if (iv.length > 16) {
            final byte[] tmp = new byte[16];
            System.arraycopy(iv, 0, tmp, 0, tmp.length);
            iv = tmp;
        }
        if (key.length > 24) {
            final byte[] tmp = new byte[24];
            System.arraycopy(key, 0, tmp, 0, tmp.length);
            key = tmp;
        }
        try {
            final SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
            this.cipher = javax.crypto.Cipher.getInstance("AES/CBC/" + pad);
            synchronized (javax.crypto.Cipher.class) {
                this.cipher.init((mode == 0) ? 1 : 2, keyspec, new IvParameterSpec(iv));
            }
        }
        catch (Exception e) {
            this.cipher = null;
            throw e;
        }
    }
    
    public void update(final byte[] foo, final int s1, final int len, final byte[] bar, final int s2) throws Exception {
        this.cipher.update(foo, s1, len, bar, s2);
    }
    
    public boolean isCBC() {
        return true;
    }
}
