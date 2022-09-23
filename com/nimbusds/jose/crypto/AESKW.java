// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.security.GeneralSecurityException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import com.nimbusds.jose.JOSEException;
import java.security.Key;
import javax.crypto.Cipher;
import java.security.Provider;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class AESKW
{
    public static byte[] wrapCEK(final SecretKey cek, final SecretKey kek, final Provider provider) throws JOSEException {
        try {
            Cipher cipher;
            if (provider != null) {
                cipher = Cipher.getInstance("AESWrap", provider);
            }
            else {
                cipher = Cipher.getInstance("AESWrap");
            }
            cipher.init(3, kek);
            return cipher.wrap(cek);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException("Couldn't wrap AES key: " + e.getMessage(), e);
        }
    }
    
    public static SecretKey unwrapCEK(final SecretKey kek, final byte[] encryptedCEK, final Provider provider) throws JOSEException {
        try {
            Cipher cipher;
            if (provider != null) {
                cipher = Cipher.getInstance("AESWrap", provider);
            }
            else {
                cipher = Cipher.getInstance("AESWrap");
            }
            cipher.init(4, kek);
            return (SecretKey)cipher.unwrap(encryptedCEK, "AES", 3);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException("Couldn't unwrap AES key: " + e.getMessage(), e);
        }
    }
    
    private AESKW() {
    }
}
