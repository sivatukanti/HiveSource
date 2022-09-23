// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jca;

import com.nimbusds.jose.EncryptionMethod;
import javax.crypto.NoSuchPaddingException;
import com.nimbusds.jose.JWEAlgorithm;
import java.security.Provider;
import java.security.Security;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;

public final class JCASupport
{
    public static boolean isUnlimitedStrength() {
        try {
            return Cipher.getMaxAllowedKeyLength("AES") >= 256;
        }
        catch (NoSuchAlgorithmException ex) {
            return false;
        }
    }
    
    public static boolean isSupported(final JWSAlgorithm alg) {
        if (alg.getName().equals(Algorithm.NONE.getName())) {
            return true;
        }
        Provider[] providers;
        for (int length = (providers = Security.getProviders()).length, i = 0; i < length; ++i) {
            final Provider p = providers[i];
            if (isSupported(alg, p)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSupported(final JWSAlgorithm alg, final Provider provider) {
        if (JWSAlgorithm.Family.HMAC_SHA.contains(alg)) {
            String jcaName;
            if (alg.equals(JWSAlgorithm.HS256)) {
                jcaName = "HMACSHA256";
            }
            else if (alg.equals(JWSAlgorithm.HS384)) {
                jcaName = "HMACSHA384";
            }
            else {
                if (!alg.equals(JWSAlgorithm.HS512)) {
                    return false;
                }
                jcaName = "HMACSHA512";
            }
            return provider.getService("KeyGenerator", jcaName) != null;
        }
        if (JWSAlgorithm.Family.RSA.contains(alg)) {
            String jcaName;
            if (alg.equals(JWSAlgorithm.RS256)) {
                jcaName = "SHA256withRSA";
            }
            else if (alg.equals(JWSAlgorithm.RS384)) {
                jcaName = "SHA384withRSA";
            }
            else if (alg.equals(JWSAlgorithm.RS512)) {
                jcaName = "SHA512withRSA";
            }
            else if (alg.equals(JWSAlgorithm.PS256)) {
                jcaName = "SHA256withRSAandMGF1";
            }
            else if (alg.equals(JWSAlgorithm.PS384)) {
                jcaName = "SHA384withRSAandMGF1";
            }
            else {
                if (!alg.equals(JWSAlgorithm.PS512)) {
                    return false;
                }
                jcaName = "SHA512withRSAandMGF1";
            }
            return provider.getService("Signature", jcaName) != null;
        }
        if (JWSAlgorithm.Family.EC.contains(alg)) {
            String jcaName;
            if (alg.equals(JWSAlgorithm.ES256)) {
                jcaName = "SHA256withECDSA";
            }
            else if (alg.equals(JWSAlgorithm.ES384)) {
                jcaName = "SHA384withECDSA";
            }
            else {
                if (!alg.equals(JWSAlgorithm.ES512)) {
                    return false;
                }
                jcaName = "SHA512withECDSA";
            }
            return provider.getService("Signature", jcaName) != null;
        }
        return false;
    }
    
    public static boolean isSupported(final JWEAlgorithm alg) {
        Provider[] providers;
        for (int length = (providers = Security.getProviders()).length, i = 0; i < length; ++i) {
            final Provider p = providers[i];
            if (isSupported(alg, p)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSupported(final JWEAlgorithm alg, final Provider provider) {
        if (JWEAlgorithm.Family.RSA.contains(alg)) {
            String jcaName;
            if (alg.equals(JWEAlgorithm.RSA1_5)) {
                jcaName = "RSA/ECB/PKCS1Padding";
            }
            else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
                jcaName = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
            }
            else {
                if (!alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
                    return false;
                }
                jcaName = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
            }
            try {
                Cipher.getInstance(jcaName, provider);
            }
            catch (NoSuchAlgorithmException ex) {
                return false;
            }
            catch (NoSuchPaddingException ex2) {
                return false;
            }
            return true;
        }
        if (JWEAlgorithm.Family.AES_KW.contains(alg)) {
            return provider.getService("Cipher", "AESWrap") != null;
        }
        if (JWEAlgorithm.Family.ECDH_ES.contains(alg)) {
            return provider.getService("KeyAgreement", "ECDH") != null;
        }
        if (JWEAlgorithm.Family.AES_GCM_KW.contains(alg)) {
            try {
                Cipher.getInstance("AES/GCM/NoPadding", provider);
            }
            catch (NoSuchAlgorithmException ex3) {
                return false;
            }
            catch (NoSuchPaddingException ex4) {
                return false;
            }
            return true;
        }
        if (JWEAlgorithm.Family.PBES2.contains(alg)) {
            String hmac;
            if (alg.equals(JWEAlgorithm.PBES2_HS256_A128KW)) {
                hmac = "HmacSHA256";
            }
            else if (alg.equals(JWEAlgorithm.PBES2_HS384_A192KW)) {
                hmac = "HmacSHA384";
            }
            else {
                hmac = "HmacSHA512";
            }
            return provider.getService("KeyGenerator", hmac) != null;
        }
        return JWEAlgorithm.DIR.equals(alg);
    }
    
    public static boolean isSupported(final EncryptionMethod enc) {
        Provider[] providers;
        for (int length = (providers = Security.getProviders()).length, i = 0; i < length; ++i) {
            final Provider p = providers[i];
            if (isSupported(enc, p)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSupported(final EncryptionMethod enc, final Provider provider) {
        if (EncryptionMethod.Family.AES_CBC_HMAC_SHA.contains(enc)) {
            try {
                Cipher.getInstance("AES/CBC/PKCS5Padding", provider);
            }
            catch (NoSuchAlgorithmException ex) {
                return false;
            }
            catch (NoSuchPaddingException ex2) {
                return false;
            }
            String hmac;
            if (enc.equals(EncryptionMethod.A128CBC_HS256)) {
                hmac = "HmacSHA256";
            }
            else if (enc.equals(EncryptionMethod.A192CBC_HS384)) {
                hmac = "HmacSHA384";
            }
            else {
                hmac = "HmacSHA512";
            }
            return provider.getService("KeyGenerator", hmac) != null;
        }
        if (EncryptionMethod.Family.AES_GCM.contains(enc)) {
            try {
                Cipher.getInstance("AES/GCM/NoPadding", provider);
            }
            catch (NoSuchAlgorithmException ex3) {
                return false;
            }
            catch (NoSuchPaddingException ex4) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    private JCASupport() {
    }
}
