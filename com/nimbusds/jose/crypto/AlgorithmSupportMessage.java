// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Collection;

class AlgorithmSupportMessage
{
    private static String itemize(final Collection collection) {
        final StringBuilder sb = new StringBuilder();
        final Object[] items = collection.toArray();
        for (int i = 0; i < items.length; ++i) {
            if (i != 0) {
                if (i < items.length - 1) {
                    sb.append(", ");
                }
                else if (i == items.length - 1) {
                    sb.append(" or ");
                }
            }
            sb.append(items[i].toString());
        }
        return sb.toString();
    }
    
    public static String unsupportedJWSAlgorithm(final JWSAlgorithm unsupported, final Collection<JWSAlgorithm> supported) {
        return "Unsupported JWS algorithm " + unsupported + ", must be " + itemize(supported);
    }
    
    public static String unsupportedJWEAlgorithm(final JWEAlgorithm unsupported, final Collection<JWEAlgorithm> supported) {
        return "Unsupported JWE algorithm " + unsupported + ", must be " + itemize(supported);
    }
    
    public static String unsupportedEncryptionMethod(final EncryptionMethod unsupported, final Collection<EncryptionMethod> supported) {
        return "Unsupported JWE encryption method " + unsupported + ", must be " + itemize(supported);
    }
    
    public static String unsupportedEllipticCurve(final ECKey.Curve unsupported, final Collection<ECKey.Curve> supported) {
        return "Unsupported elliptic curve " + unsupported + ", must be " + itemize(supported);
    }
    
    private AlgorithmSupportMessage() {
    }
}
