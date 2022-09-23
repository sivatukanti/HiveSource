// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Pbkdf
{
    public static byte[] pbkdf2(final char[] secret, final byte[] salt, final int count, final int keySize) throws GeneralSecurityException {
        final PBEKeySpec ks = new PBEKeySpec(secret, salt, count, keySize * 8);
        final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final SecretKey key = skf.generateSecret(ks);
        return key.getEncoded();
    }
}
