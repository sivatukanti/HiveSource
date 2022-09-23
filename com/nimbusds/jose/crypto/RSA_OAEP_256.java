// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import java.security.AlgorithmParameters;
import javax.crypto.IllegalBlockSizeException;
import com.nimbusds.jose.JOSEException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.spec.MGF1ParameterSpec;
import java.security.Provider;
import javax.crypto.SecretKey;
import java.security.interfaces.RSAPublicKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class RSA_OAEP_256
{
    public static byte[] encryptCEK(final RSAPublicKey pub, final SecretKey cek, final Provider provider) throws JOSEException {
        try {
            final AlgorithmParameters algp = AlgorithmParametersHelper.getInstance("OAEP", provider);
            final AlgorithmParameterSpec paramSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            algp.init(paramSpec);
            final Cipher cipher = CipherHelper.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", provider);
            cipher.init(1, pub, algp);
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
            final AlgorithmParameters algp = AlgorithmParametersHelper.getInstance("OAEP", provider);
            final AlgorithmParameterSpec paramSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            algp.init(paramSpec);
            final Cipher cipher = CipherHelper.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", provider);
            cipher.init(2, priv, algp);
            return new SecretKeySpec(cipher.doFinal(encryptedCEK), "AES");
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }
    
    private RSA_OAEP_256() {
    }
}
