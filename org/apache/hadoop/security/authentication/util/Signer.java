// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import java.security.MessageDigest;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Signer
{
    private static final String SIGNATURE = "&s=";
    private static final String SIGNING_ALGORITHM = "HmacSHA256";
    private SignerSecretProvider secretProvider;
    
    public Signer(final SignerSecretProvider secretProvider) {
        if (secretProvider == null) {
            throw new IllegalArgumentException("secretProvider cannot be NULL");
        }
        this.secretProvider = secretProvider;
    }
    
    public synchronized String sign(final String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("NULL or empty string to sign");
        }
        final byte[] secret = this.secretProvider.getCurrentSecret();
        final String signature = this.computeSignature(secret, str);
        return str + "&s=" + signature;
    }
    
    public String verifyAndExtract(final String signedStr) throws SignerException {
        final int index = signedStr.lastIndexOf("&s=");
        if (index == -1) {
            throw new SignerException("Invalid signed text: " + signedStr);
        }
        final String originalSignature = signedStr.substring(index + "&s=".length());
        final String rawValue = signedStr.substring(0, index);
        this.checkSignatures(rawValue, originalSignature);
        return rawValue;
    }
    
    protected String computeSignature(final byte[] secret, final String str) {
        try {
            final SecretKeySpec key = new SecretKeySpec(secret, "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            final byte[] sig = mac.doFinal(StringUtils.getBytesUtf8(str));
            return new Base64(0).encodeToString(sig);
        }
        catch (NoSuchAlgorithmException | InvalidKeyException ex3) {
            final GeneralSecurityException ex2;
            final GeneralSecurityException ex = ex2;
            throw new RuntimeException("It should not happen, " + ex.getMessage(), ex);
        }
    }
    
    protected void checkSignatures(final String rawValue, final String originalSignature) throws SignerException {
        final byte[] orginalSignatureBytes = StringUtils.getBytesUtf8(originalSignature);
        boolean isValid = false;
        final byte[][] secrets = this.secretProvider.getAllSecrets();
        for (int i = 0; i < secrets.length; ++i) {
            final byte[] secret = secrets[i];
            if (secret != null) {
                final String currentSignature = this.computeSignature(secret, rawValue);
                if (MessageDigest.isEqual(orginalSignatureBytes, StringUtils.getBytesUtf8(currentSignature))) {
                    isValid = true;
                    break;
                }
            }
        }
        if (!isValid) {
            throw new SignerException("Invalid signature");
        }
    }
}
