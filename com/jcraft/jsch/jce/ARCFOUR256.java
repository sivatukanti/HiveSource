// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import com.jcraft.jsch.Cipher;

public class ARCFOUR256 implements Cipher
{
    private static final int ivsize = 8;
    private static final int bsize = 32;
    private static final int skip = 1536;
    private javax.crypto.Cipher cipher;
    
    public int getIVSize() {
        return 8;
    }
    
    public int getBlockSize() {
        return 32;
    }
    
    public void init(final int mode, byte[] key, final byte[] iv) throws Exception {
        if (key.length > 32) {
            final byte[] tmp = new byte[32];
            System.arraycopy(key, 0, tmp, 0, tmp.length);
            key = tmp;
        }
        try {
            this.cipher = javax.crypto.Cipher.getInstance("RC4");
            final SecretKeySpec _key = new SecretKeySpec(key, "RC4");
            synchronized (javax.crypto.Cipher.class) {
                this.cipher.init((mode == 0) ? 1 : 2, _key);
            }
            final byte[] foo = { 0 };
            for (int i = 0; i < 1536; ++i) {
                this.cipher.update(foo, 0, 1, foo, 0);
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
        return false;
    }
}
