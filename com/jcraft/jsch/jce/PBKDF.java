// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PBKDF implements com.jcraft.jsch.PBKDF
{
    public byte[] getKey(final byte[] _pass, final byte[] salt, final int iterations, final int size) {
        final char[] pass = new char[_pass.length];
        for (int i = 0; i < _pass.length; ++i) {
            pass[i] = (char)(_pass[i] & 0xFF);
        }
        try {
            final PBEKeySpec spec = new PBEKeySpec(pass, salt, iterations, size * 8);
            final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final byte[] key = skf.generateSecret(spec).getEncoded();
            return key;
        }
        catch (InvalidKeySpecException e) {}
        catch (NoSuchAlgorithmException ex) {}
        return null;
    }
}
