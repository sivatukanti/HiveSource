// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.key;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.BytesUtil;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public abstract class AbstractKeyMaker implements KeyMaker
{
    static final byte[] KERBEROS_CONSTANT;
    private EncryptProvider encProvider;
    
    public AbstractKeyMaker(final EncryptProvider encProvider) {
        this.encProvider = encProvider;
    }
    
    public static byte[] makePasswdSalt(final String password, final String salt) {
        final char[] chars = new char[password.length() + salt.length()];
        System.arraycopy(password.toCharArray(), 0, chars, 0, password.length());
        System.arraycopy(salt.toCharArray(), 0, chars, password.length(), salt.length());
        return new String(chars).getBytes(StandardCharsets.UTF_8);
    }
    
    protected static int getIterCount(final byte[] param, final int defCount) {
        int iterCount = defCount;
        if (param != null) {
            if (param.length != 4) {
                throw new IllegalArgumentException("Invalid param to str2Key");
            }
            iterCount = BytesUtil.bytes2int(param, 0, true);
        }
        return iterCount;
    }
    
    protected static byte[] getSaltBytes(final String salt, final String pepper) {
        final byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        if (pepper != null && !pepper.isEmpty()) {
            final byte[] pepperBytes = pepper.getBytes(StandardCharsets.UTF_8);
            int len = saltBytes.length;
            len += 1 + pepperBytes.length;
            final byte[] results = new byte[len];
            System.arraycopy(pepperBytes, 0, results, 0, pepperBytes.length);
            results[pepperBytes.length] = 0;
            System.arraycopy(saltBytes, 0, results, pepperBytes.length + 1, saltBytes.length);
            return results;
        }
        return saltBytes;
    }
    
    protected EncryptProvider encProvider() {
        return this.encProvider;
    }
    
    @Override
    public byte[] random2Key(final byte[] randomBits) throws KrbException {
        return new byte[0];
    }
    
    static {
        KERBEROS_CONSTANT = "kerberos".getBytes(StandardCharsets.UTF_8);
    }
}
