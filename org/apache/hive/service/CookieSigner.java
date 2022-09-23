// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

import org.apache.commons.logging.LogFactory;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import java.security.MessageDigest;
import org.apache.commons.logging.Log;

public class CookieSigner
{
    private static final String SIGNATURE = "&s=";
    private static final String SHA_STRING = "SHA";
    private byte[] secretBytes;
    private static final Log LOG;
    
    public CookieSigner(final byte[] secret) {
        if (secret == null) {
            throw new IllegalArgumentException(" NULL Secret Bytes");
        }
        this.secretBytes = secret.clone();
    }
    
    public String signCookie(final String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("NULL or empty string to sign");
        }
        final String signature = this.getSignature(str);
        if (CookieSigner.LOG.isDebugEnabled()) {
            CookieSigner.LOG.debug("Signature generated for " + str + " is " + signature);
        }
        return str + "&s=" + signature;
    }
    
    public String verifyAndExtract(final String signedStr) {
        final int index = signedStr.lastIndexOf("&s=");
        if (index == -1) {
            throw new IllegalArgumentException("Invalid input sign: " + signedStr);
        }
        final String originalSignature = signedStr.substring(index + "&s=".length());
        final String rawValue = signedStr.substring(0, index);
        final String currentSignature = this.getSignature(rawValue);
        if (CookieSigner.LOG.isDebugEnabled()) {
            CookieSigner.LOG.debug("Signature generated for " + rawValue + " inside verify is " + currentSignature);
        }
        if (!originalSignature.equals(currentSignature)) {
            throw new IllegalArgumentException("Invalid sign, original = " + originalSignature + " current = " + currentSignature);
        }
        return rawValue;
    }
    
    private String getSignature(final String str) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(str.getBytes());
            md.update(this.secretBytes);
            final byte[] digest = md.digest();
            return new Base64(0).encodeToString(digest);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Invalid SHA digest String: SHA " + ex.getMessage(), ex);
        }
    }
    
    static {
        LOG = LogFactory.getLog(CookieSigner.class);
    }
}
