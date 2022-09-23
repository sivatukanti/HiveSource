// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.util.ByteUtils;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import com.nimbusds.jose.JOSEException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.SecretKey;
import java.security.interfaces.RSAPublicKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class RSA1_5
{
    public static byte[] encryptCEK(final RSAPublicKey pub, final SecretKey cek, final Provider provider) throws JOSEException {
        try {
            final Cipher cipher = CipherHelper.getInstance("RSA/ECB/PKCS1Padding", provider);
            cipher.init(1, pub);
            return cipher.doFinal(cek.getEncoded());
        }
        catch (IllegalBlockSizeException e) {
            throw new JOSEException("RSA block size exception: The RSA key is too short, try a longer one", e);
        }
        catch (Exception e2) {
            throw new JOSEException("Couldn't encrypt Content Encryption Key (CEK): " + e2.getMessage(), e2);
        }
    }
    
    public static SecretKey decryptCEK(final PrivateKey priv, final byte[] encryptedCEK, final int keyLength, final Provider provider) throws JOSEException {
        try {
            final Cipher cipher = CipherHelper.getInstance("RSA/ECB/PKCS1Padding", provider);
            cipher.init(2, priv);
            final byte[] secretKeyBytes = cipher.doFinal(encryptedCEK);
            if (ByteUtils.safeBitLength(secretKeyBytes) != keyLength) {
                return null;
            }
            return new SecretKeySpec(secretKeyBytes, "AES");
        }
        catch (Exception e) {
            throw new JOSEException("Couldn't decrypt Content Encryption Key (CEK): " + e.getMessage(), e);
        }
    }
    
    private RSA1_5() {
    }
}
