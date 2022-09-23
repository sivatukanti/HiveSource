// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.nio.charset.Charset;
import com.nimbusds.jose.JWEHeader;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import com.nimbusds.jose.JWEAlgorithm;

class ECDH
{
    static AlgorithmMode resolveAlgorithmMode(final JWEAlgorithm alg) throws JOSEException {
        if (alg.equals(JWEAlgorithm.ECDH_ES)) {
            return AlgorithmMode.DIRECT;
        }
        if (alg.equals(JWEAlgorithm.ECDH_ES_A128KW) || alg.equals(JWEAlgorithm.ECDH_ES_A192KW) || alg.equals(JWEAlgorithm.ECDH_ES_A256KW)) {
            return AlgorithmMode.KW;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, ECDHCryptoProvider.SUPPORTED_ALGORITHMS));
    }
    
    static int sharedKeyLength(final JWEAlgorithm alg, final EncryptionMethod enc) throws JOSEException {
        if (alg.equals(JWEAlgorithm.ECDH_ES)) {
            final int length = enc.cekBitLength();
            if (length == 0) {
                throw new JOSEException("Unsupported JWE encryption method " + enc);
            }
            return length;
        }
        else {
            if (alg.equals(JWEAlgorithm.ECDH_ES_A128KW)) {
                return 128;
            }
            if (alg.equals(JWEAlgorithm.ECDH_ES_A192KW)) {
                return 192;
            }
            if (alg.equals(JWEAlgorithm.ECDH_ES_A256KW)) {
                return 256;
            }
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, ECDHCryptoProvider.SUPPORTED_ALGORITHMS));
        }
    }
    
    static SecretKey deriveSharedSecret(final ECPublicKey publicKey, final ECPrivateKey privateKey, final Provider provider) throws JOSEException {
        KeyAgreement keyAgreement;
        try {
            if (provider != null) {
                keyAgreement = KeyAgreement.getInstance("ECDH", provider);
            }
            else {
                keyAgreement = KeyAgreement.getInstance("ECDH");
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't get an ECDH key agreement instance: " + e.getMessage(), e);
        }
        try {
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
        }
        catch (InvalidKeyException e2) {
            throw new JOSEException("Invalid key for ECDH key agreement: " + e2.getMessage(), e2);
        }
        return new SecretKeySpec(keyAgreement.generateSecret(), "AES");
    }
    
    static SecretKey deriveSharedKey(final JWEHeader header, final SecretKey Z, final ConcatKDF concatKDF) throws JOSEException {
        final int sharedKeyLength = sharedKeyLength(header.getAlgorithm(), header.getEncryptionMethod());
        final AlgorithmMode algMode = resolveAlgorithmMode(header.getAlgorithm());
        String algID;
        if (algMode == AlgorithmMode.DIRECT) {
            algID = header.getEncryptionMethod().getName();
        }
        else {
            if (algMode != AlgorithmMode.KW) {
                throw new JOSEException("Unsupported JWE ECDH algorithm mode: " + algMode);
            }
            algID = header.getAlgorithm().getName();
        }
        return concatKDF.deriveKey(Z, sharedKeyLength, ConcatKDF.encodeDataWithLength(algID.getBytes(Charset.forName("ASCII"))), ConcatKDF.encodeDataWithLength(header.getAgreementPartyUInfo()), ConcatKDF.encodeDataWithLength(header.getAgreementPartyVInfo()), ConcatKDF.encodeIntData(sharedKeyLength), ConcatKDF.encodeNoData());
    }
    
    private ECDH() {
    }
    
    public enum AlgorithmMode
    {
        DIRECT("DIRECT", 0), 
        KW("KW", 1);
        
        private AlgorithmMode(final String name, final int ordinal) {
        }
    }
}
