// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import com.nimbusds.jose.JOSEException;
import java.security.Key;
import javax.crypto.Mac;
import java.security.Provider;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class HMAC
{
    public static Mac getInitMac(final SecretKey secretKey, final Provider provider) throws JOSEException {
        Mac mac;
        try {
            if (provider != null) {
                mac = Mac.getInstance(secretKey.getAlgorithm(), provider);
            }
            else {
                mac = Mac.getInstance(secretKey.getAlgorithm());
            }
            mac.init(secretKey);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Unsupported HMAC algorithm: " + e.getMessage(), e);
        }
        catch (InvalidKeyException e2) {
            throw new JOSEException("Invalid HMAC key: " + e2.getMessage(), e2);
        }
        return mac;
    }
    
    public static byte[] compute(final String alg, final byte[] secret, final byte[] message, final Provider provider) throws JOSEException {
        return compute(new SecretKeySpec(secret, alg), message, provider);
    }
    
    public static byte[] compute(final SecretKey secretKey, final byte[] message, final Provider provider) throws JOSEException {
        final Mac mac = getInitMac(secretKey, provider);
        mac.update(message);
        return mac.doFinal();
    }
}
