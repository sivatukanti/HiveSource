// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.crypto.utils.ConstantTimeUtils;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWEHeader;
import java.util.Arrays;
import java.nio.ByteBuffer;
import com.nimbusds.jose.JOSEException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import java.security.Provider;
import javax.crypto.SecretKey;
import com.nimbusds.jose.util.ByteUtils;
import java.security.SecureRandom;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class AESCBC
{
    public static final int IV_BIT_LENGTH = 128;
    
    public static byte[] generateIV(final SecureRandom randomGen) {
        final byte[] bytes = new byte[ByteUtils.byteLength(128)];
        randomGen.nextBytes(bytes);
        return bytes;
    }
    
    private static Cipher createAESCBCCipher(final SecretKey secretKey, final boolean forEncryption, final byte[] iv, final Provider provider) throws JOSEException {
        Cipher cipher;
        try {
            cipher = CipherHelper.getInstance("AES/CBC/PKCS5Padding", provider);
            final SecretKeySpec keyspec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            final IvParameterSpec ivSpec = new IvParameterSpec(iv);
            if (forEncryption) {
                cipher.init(1, keyspec, ivSpec);
            }
            else {
                cipher.init(2, keyspec, ivSpec);
            }
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
        return cipher;
    }
    
    public static byte[] encrypt(final SecretKey secretKey, final byte[] iv, final byte[] plainText, final Provider provider) throws JOSEException {
        final Cipher cipher = createAESCBCCipher(secretKey, true, iv, provider);
        try {
            return cipher.doFinal(plainText);
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }
    
    public static AuthenticatedCipherText encryptAuthenticated(final SecretKey secretKey, final byte[] iv, final byte[] plainText, final byte[] aad, final Provider ceProvider, final Provider macProvider) throws JOSEException {
        final CompositeKey compositeKey = new CompositeKey(secretKey);
        final byte[] cipherText = encrypt(compositeKey.getAESKey(), iv, plainText, ceProvider);
        final byte[] al = AAD.computeLength(aad);
        final int hmacInputLength = aad.length + iv.length + cipherText.length + al.length;
        final byte[] hmacInput = ByteBuffer.allocate(hmacInputLength).put(aad).put(iv).put(cipherText).put(al).array();
        final byte[] hmac = HMAC.compute(compositeKey.getMACKey(), hmacInput, macProvider);
        final byte[] authTag = Arrays.copyOf(hmac, compositeKey.getTruncatedMACByteLength());
        return new AuthenticatedCipherText(cipherText, authTag);
    }
    
    public static AuthenticatedCipherText encryptWithConcatKDF(final JWEHeader header, final SecretKey secretKey, final Base64URL encryptedKey, final byte[] iv, final byte[] plainText, final Provider ceProvider, final Provider macProvider) throws JOSEException {
        byte[] epu = null;
        if (header.getCustomParam("epu") instanceof String) {
            epu = new Base64URL((String)header.getCustomParam("epu")).decode();
        }
        byte[] epv = null;
        if (header.getCustomParam("epv") instanceof String) {
            epv = new Base64URL((String)header.getCustomParam("epv")).decode();
        }
        final SecretKey altCEK = LegacyConcatKDF.generateCEK(secretKey, header.getEncryptionMethod(), epu, epv);
        final byte[] cipherText = encrypt(altCEK, iv, plainText, ceProvider);
        final SecretKey cik = LegacyConcatKDF.generateCIK(secretKey, header.getEncryptionMethod(), epu, epv);
        final String macInput = String.valueOf(header.toBase64URL().toString()) + "." + encryptedKey.toString() + "." + Base64URL.encode(iv).toString() + "." + Base64URL.encode(cipherText);
        final byte[] mac = HMAC.compute(cik, macInput.getBytes(), macProvider);
        return new AuthenticatedCipherText(cipherText, mac);
    }
    
    public static byte[] decrypt(final SecretKey secretKey, final byte[] iv, final byte[] cipherText, final Provider provider) throws JOSEException {
        final Cipher cipher = createAESCBCCipher(secretKey, false, iv, provider);
        try {
            return cipher.doFinal(cipherText);
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }
    
    public static byte[] decryptAuthenticated(final SecretKey secretKey, final byte[] iv, final byte[] cipherText, final byte[] aad, final byte[] authTag, final Provider ceProvider, final Provider macProvider) throws JOSEException {
        final CompositeKey compositeKey = new CompositeKey(secretKey);
        final byte[] al = AAD.computeLength(aad);
        final int hmacInputLength = aad.length + iv.length + cipherText.length + al.length;
        final byte[] hmacInput = ByteBuffer.allocate(hmacInputLength).put(aad).put(iv).put(cipherText).put(al).array();
        final byte[] hmac = HMAC.compute(compositeKey.getMACKey(), hmacInput, macProvider);
        final byte[] expectedAuthTag = Arrays.copyOf(hmac, compositeKey.getTruncatedMACByteLength());
        if (!ConstantTimeUtils.areEqual(expectedAuthTag, authTag)) {
            throw new JOSEException("MAC check failed");
        }
        return decrypt(compositeKey.getAESKey(), iv, cipherText, ceProvider);
    }
    
    public static byte[] decryptWithConcatKDF(final JWEHeader header, final SecretKey secretKey, final Base64URL encryptedKey, final Base64URL iv, final Base64URL cipherText, final Base64URL authTag, final Provider ceProvider, final Provider macProvider) throws JOSEException {
        byte[] epu = null;
        if (header.getCustomParam("epu") instanceof String) {
            epu = new Base64URL((String)header.getCustomParam("epu")).decode();
        }
        byte[] epv = null;
        if (header.getCustomParam("epv") instanceof String) {
            epv = new Base64URL((String)header.getCustomParam("epv")).decode();
        }
        final SecretKey cik = LegacyConcatKDF.generateCIK(secretKey, header.getEncryptionMethod(), epu, epv);
        final String macInput = String.valueOf(header.toBase64URL().toString()) + "." + encryptedKey.toString() + "." + iv.toString() + "." + cipherText.toString();
        final byte[] mac = HMAC.compute(cik, macInput.getBytes(), macProvider);
        if (!ConstantTimeUtils.areEqual(authTag.decode(), mac)) {
            throw new JOSEException("MAC check failed");
        }
        final SecretKey cekAlt = LegacyConcatKDF.generateCEK(secretKey, header.getEncryptionMethod(), epu, epv);
        return decrypt(cekAlt, iv.decode(), cipherText.decode(), ceProvider);
    }
    
    private AESCBC() {
    }
}
