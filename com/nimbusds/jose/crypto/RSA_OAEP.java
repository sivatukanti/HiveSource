// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import com.nimbusds.jose.JOSEException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Provider;
import javax.crypto.SecretKey;
import java.security.interfaces.RSAPublicKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class RSA_OAEP
{
    public static byte[] encryptCEK(final RSAPublicKey pub, final SecretKey cek, final Provider provider) throws JOSEException {
        try {
            final Cipher cipher = CipherHelper.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", provider);
            cipher.init(1, pub, new SecureRandom());
            return cipher.doFinal(cek.getEncoded());
        }
        catch (IllegalBlockSizeException e) {
            throw new JOSEException("RSA block size exception: The RSA key is too short, try a longer one", e);
        }
        catch (Exception e2) {
            throw new JOSEException(e2.getMessage(), e2);
        }
    }
    
    public static SecretKey decryptCEK(final PrivateKey priv, final byte[] encryptedCEK, final Provider provider) throws JOSEException {
        try {
            final Cipher cipher = CipherHelper.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", provider);
            cipher.init(2, priv);
            return new SecretKeySpec(cipher.doFinal(encryptedCEK), "AES");
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }
    
    private RSA_OAEP() {
    }
}
