// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.security.GeneralSecurityException;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.KeyException;
import java.security.spec.RSAPublicKeySpec;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.DSAPublicKeySpec;
import java.security.interfaces.DSAPrivateKey;
import java.security.PublicKey;
import java.security.PrivateKey;

public class PublicKeyDeriver
{
    public static PublicKey derivePublicKey(final PrivateKey key) throws GeneralSecurityException {
        if (key instanceof DSAPrivateKey) {
            final DSAPrivateKey dsaKey = (DSAPrivateKey)key;
            final DSAParams keyParams = dsaKey.getParams();
            final BigInteger g = keyParams.getG();
            final BigInteger p = keyParams.getP();
            final BigInteger q = keyParams.getQ();
            final BigInteger x = dsaKey.getX();
            final BigInteger y = q.modPow(x, p);
            final DSAPublicKeySpec keySpec = new DSAPublicKeySpec(y, p, q, g);
            return KeyFactory.getInstance("DSA").generatePublic(keySpec);
        }
        if (key instanceof RSAPrivateCrtKey) {
            final RSAPrivateCrtKey rsaKey = (RSAPrivateCrtKey)key;
            final BigInteger modulus = rsaKey.getModulus();
            final BigInteger exponent = rsaKey.getPublicExponent();
            final RSAPublicKeySpec keySpec2 = new RSAPublicKeySpec(modulus, exponent);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec2);
        }
        throw new KeyException("Private key was not a DSA or RSA key");
    }
}
