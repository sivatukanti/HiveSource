// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.security.cert.X509Certificate;
import java.text.ParseException;

public enum KeyUse
{
    SIGNATURE("SIGNATURE", 0, "sig"), 
    ENCRYPTION("ENCRYPTION", 1, "enc");
    
    private final String identifier;
    
    private KeyUse(final String name, final int ordinal, final String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("The key use identifier must not be null");
        }
        this.identifier = identifier;
    }
    
    public String identifier() {
        return this.identifier;
    }
    
    @Override
    public String toString() {
        return this.identifier();
    }
    
    public static KeyUse parse(final String s) throws ParseException {
        if (s == null) {
            return null;
        }
        KeyUse[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final KeyUse use = values[i];
            if (s.equals(use.identifier)) {
                return use;
            }
        }
        throw new ParseException("Invalid JWK use: " + s, 0);
    }
    
    public static KeyUse from(final X509Certificate cert) {
        if (cert.getKeyUsage() == null) {
            return null;
        }
        if (cert.getKeyUsage()[1]) {
            return KeyUse.SIGNATURE;
        }
        if (cert.getKeyUsage()[0] && cert.getKeyUsage()[2]) {
            return KeyUse.ENCRYPTION;
        }
        if (cert.getKeyUsage()[0] && cert.getKeyUsage()[4]) {
            return KeyUse.ENCRYPTION;
        }
        if (cert.getKeyUsage()[2] || cert.getKeyUsage()[3] || cert.getKeyUsage()[4]) {
            return KeyUse.ENCRYPTION;
        }
        if (cert.getKeyUsage()[5] || cert.getKeyUsage()[6]) {
            return KeyUse.SIGNATURE;
        }
        return null;
    }
}
