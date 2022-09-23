// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto.utils;

import java.security.spec.EllipticCurve;
import java.security.spec.ECFieldFp;
import java.math.BigInteger;
import java.security.spec.ECPoint;
import java.security.spec.ECParameterSpec;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class ECChecks
{
    public static boolean isPointOnCurve(final ECPublicKey publicKey, final ECPrivateKey privateKey) {
        return isPointOnCurve(publicKey, privateKey.getParams());
    }
    
    public static boolean isPointOnCurve(final ECPublicKey publicKey, final ECParameterSpec ecParameterSpec) {
        final ECPoint point = publicKey.getW();
        return isPointOnCurve(point.getAffineX(), point.getAffineY(), ecParameterSpec);
    }
    
    public static boolean isPointOnCurve(final BigInteger x, final BigInteger y, final ECParameterSpec ecParameterSpec) {
        final EllipticCurve curve = ecParameterSpec.getCurve();
        final BigInteger a = curve.getA();
        final BigInteger b = curve.getB();
        final BigInteger p = ((ECFieldFp)curve.getField()).getP();
        final BigInteger leftSide = y.pow(2).mod(p);
        final BigInteger rightSide = x.pow(3).add(a.multiply(x)).add(b).mod(p);
        return leftSide.equals(rightSide);
    }
    
    private ECChecks() {
    }
}
