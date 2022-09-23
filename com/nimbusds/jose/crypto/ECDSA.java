// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.security.Signature;
import java.security.Provider;
import com.nimbusds.jose.JOSEException;
import java.security.spec.ECParameterSpec;
import com.nimbusds.jose.JWSAlgorithm;
import java.security.interfaces.ECKey;

class ECDSA
{
    public static JWSAlgorithm resolveAlgorithm(final ECKey ecKey) throws JOSEException {
        final ECParameterSpec ecParameterSpec = ecKey.getParams();
        return resolveAlgorithm(com.nimbusds.jose.jwk.ECKey.Curve.forECParameterSpec(ecParameterSpec));
    }
    
    public static JWSAlgorithm resolveAlgorithm(final com.nimbusds.jose.jwk.ECKey.Curve curve) throws JOSEException {
        if (curve == null) {
            throw new JOSEException("The EC key curve is not supported, must be P256, P384 or P521");
        }
        if (com.nimbusds.jose.jwk.ECKey.Curve.P_256.equals(curve)) {
            return JWSAlgorithm.ES256;
        }
        if (com.nimbusds.jose.jwk.ECKey.Curve.P_384.equals(curve)) {
            return JWSAlgorithm.ES384;
        }
        if (com.nimbusds.jose.jwk.ECKey.Curve.P_521.equals(curve)) {
            return JWSAlgorithm.ES512;
        }
        throw new JOSEException("Unexpected curve: " + curve);
    }
    
    public static Signature getSignerAndVerifier(final JWSAlgorithm alg, final Provider jcaProvider) throws JOSEException {
        String jcaAlg;
        if (alg.equals(JWSAlgorithm.ES256)) {
            jcaAlg = "SHA256withECDSA";
        }
        else if (alg.equals(JWSAlgorithm.ES384)) {
            jcaAlg = "SHA384withECDSA";
        }
        else {
            if (!alg.equals(JWSAlgorithm.ES512)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, ECDSAProvider.SUPPORTED_ALGORITHMS));
            }
            jcaAlg = "SHA512withECDSA";
        }
        try {
            if (jcaProvider != null) {
                return Signature.getInstance(jcaAlg, jcaProvider);
            }
            return Signature.getInstance(jcaAlg);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Unsupported ECDSA algorithm: " + e.getMessage(), e);
        }
    }
    
    public static int getSignatureByteArrayLength(final JWSAlgorithm alg) throws JOSEException {
        if (alg.equals(JWSAlgorithm.ES256)) {
            return 64;
        }
        if (alg.equals(JWSAlgorithm.ES384)) {
            return 96;
        }
        if (alg.equals(JWSAlgorithm.ES512)) {
            return 132;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, ECDSAProvider.SUPPORTED_ALGORITHMS));
    }
    
    public static byte[] transcodeSignatureToConcat(final byte[] derSignature, final int outputLength) throws JOSEException {
        if (derSignature.length < 8 || derSignature[0] != 48) {
            throw new JOSEException("Invalid ECDSA signature format");
        }
        int offset;
        if (derSignature[1] > 0) {
            offset = 2;
        }
        else {
            if (derSignature[1] != -127) {
                throw new JOSEException("Invalid ECDSA signature format");
            }
            offset = 3;
        }
        int i;
        byte rLength;
        for (rLength = (byte)(i = derSignature[offset + 1]); i > 0 && derSignature[offset + 2 + rLength - i] == 0; --i) {}
        int j;
        byte sLength;
        for (sLength = (byte)(j = derSignature[offset + 2 + rLength + 1]); j > 0 && derSignature[offset + 2 + rLength + 2 + sLength - j] == 0; --j) {}
        int rawLen = Math.max(i, j);
        rawLen = Math.max(rawLen, outputLength / 2);
        if ((derSignature[offset - 1] & 0xFF) != derSignature.length - offset || (derSignature[offset - 1] & 0xFF) != 2 + rLength + 2 + sLength || derSignature[offset] != 2 || derSignature[offset + 2 + rLength] != 2) {
            throw new JOSEException("Invalid ECDSA signature format");
        }
        final byte[] concatSignature = new byte[2 * rawLen];
        System.arraycopy(derSignature, offset + 2 + rLength - i, concatSignature, rawLen - i, i);
        System.arraycopy(derSignature, offset + 2 + rLength + 2 + sLength - j, concatSignature, 2 * rawLen - j, j);
        return concatSignature;
    }
    
    public static byte[] transcodeSignatureToDER(final byte[] jwsSignature) throws JOSEException {
        int i;
        int rawLen;
        for (rawLen = (i = jwsSignature.length / 2); i > 0 && jwsSignature[rawLen - i] == 0; --i) {}
        int j = i;
        if (jwsSignature[rawLen - i] < 0) {
            ++j;
        }
        int k;
        for (k = rawLen; k > 0 && jwsSignature[2 * rawLen - k] == 0; --k) {}
        int l = k;
        if (jwsSignature[2 * rawLen - k] < 0) {
            ++l;
        }
        final int len = 2 + j + 2 + l;
        if (len > 255) {
            throw new JOSEException("Invalid ECDSA signature format");
        }
        byte[] derSignature;
        int offset;
        if (len < 128) {
            derSignature = new byte[4 + j + 2 + l];
            offset = 1;
        }
        else {
            derSignature = new byte[5 + j + 2 + l];
            derSignature[1] = -127;
            offset = 2;
        }
        derSignature[0] = 48;
        derSignature[offset++] = (byte)len;
        derSignature[offset++] = 2;
        derSignature[offset++] = (byte)j;
        System.arraycopy(jwsSignature, rawLen - i, derSignature, offset + j - i, i);
        offset += j;
        derSignature[offset++] = 2;
        derSignature[offset++] = (byte)l;
        System.arraycopy(jwsSignature, 2 * rawLen - k, derSignature, offset + l - k, k);
        return derSignature;
    }
    
    private ECDSA() {
    }
}
