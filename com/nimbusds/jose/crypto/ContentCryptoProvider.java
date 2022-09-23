// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.util.Container;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.jca.JWEJCAContext;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.util.IntegerOverflowException;
import com.nimbusds.jose.KeyLengthException;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import com.nimbusds.jose.EncryptionMethod;
import java.util.Set;

class ContentCryptoProvider
{
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    public static final Map<Integer, Set<EncryptionMethod>> COMPATIBLE_ENCRYPTION_METHODS;
    
    static {
        final Set<EncryptionMethod> methods = new LinkedHashSet<EncryptionMethod>();
        methods.add(EncryptionMethod.A128CBC_HS256);
        methods.add(EncryptionMethod.A192CBC_HS384);
        methods.add(EncryptionMethod.A256CBC_HS512);
        methods.add(EncryptionMethod.A128GCM);
        methods.add(EncryptionMethod.A192GCM);
        methods.add(EncryptionMethod.A256GCM);
        methods.add(EncryptionMethod.A128CBC_HS256_DEPRECATED);
        methods.add(EncryptionMethod.A256CBC_HS512_DEPRECATED);
        SUPPORTED_ENCRYPTION_METHODS = Collections.unmodifiableSet((Set<? extends EncryptionMethod>)methods);
        final Map<Integer, Set<EncryptionMethod>> encsMap = new HashMap<Integer, Set<EncryptionMethod>>();
        final Set<EncryptionMethod> bit128Encs = new HashSet<EncryptionMethod>();
        final Set<EncryptionMethod> bit192Encs = new HashSet<EncryptionMethod>();
        final Set<EncryptionMethod> bit256Encs = new HashSet<EncryptionMethod>();
        final Set<EncryptionMethod> bit384Encs = new HashSet<EncryptionMethod>();
        final Set<EncryptionMethod> bit512Encs = new HashSet<EncryptionMethod>();
        bit128Encs.add(EncryptionMethod.A128GCM);
        bit192Encs.add(EncryptionMethod.A192GCM);
        bit256Encs.add(EncryptionMethod.A256GCM);
        bit256Encs.add(EncryptionMethod.A128CBC_HS256);
        bit256Encs.add(EncryptionMethod.A128CBC_HS256_DEPRECATED);
        bit384Encs.add(EncryptionMethod.A192CBC_HS384);
        bit512Encs.add(EncryptionMethod.A256CBC_HS512);
        bit512Encs.add(EncryptionMethod.A256CBC_HS512_DEPRECATED);
        encsMap.put(128, Collections.unmodifiableSet((Set<? extends EncryptionMethod>)bit128Encs));
        encsMap.put(192, Collections.unmodifiableSet((Set<? extends EncryptionMethod>)bit192Encs));
        encsMap.put(256, Collections.unmodifiableSet((Set<? extends EncryptionMethod>)bit256Encs));
        encsMap.put(384, Collections.unmodifiableSet((Set<? extends EncryptionMethod>)bit384Encs));
        encsMap.put(512, Collections.unmodifiableSet((Set<? extends EncryptionMethod>)bit512Encs));
        COMPATIBLE_ENCRYPTION_METHODS = Collections.unmodifiableMap((Map<? extends Integer, ? extends Set<EncryptionMethod>>)encsMap);
    }
    
    public static SecretKey generateCEK(final EncryptionMethod enc, final SecureRandom randomGen) throws JOSEException {
        if (!ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS.contains(enc)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedEncryptionMethod(enc, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS));
        }
        final byte[] cekMaterial = new byte[ByteUtils.byteLength(enc.cekBitLength())];
        randomGen.nextBytes(cekMaterial);
        return new SecretKeySpec(cekMaterial, "AES");
    }
    
    private static void checkCEKLength(final SecretKey cek, final EncryptionMethod enc) throws KeyLengthException {
        try {
            if (enc.cekBitLength() != ByteUtils.safeBitLength(cek.getEncoded())) {
                throw new KeyLengthException("The Content Encryption Key (CEK) length for " + enc + " must be " + enc.cekBitLength() + " bits");
            }
        }
        catch (IntegerOverflowException e) {
            throw new KeyLengthException("The Content Encryption Key (CEK) is too long: " + e.getMessage());
        }
    }
    
    public static JWECryptoParts encrypt(final JWEHeader header, final byte[] clearText, final SecretKey cek, final Base64URL encryptedKey, final JWEJCAContext jcaProvider) throws JOSEException {
        checkCEKLength(cek, header.getEncryptionMethod());
        final byte[] plainText = DeflateHelper.applyCompression(header, clearText);
        final byte[] aad = AAD.compute(header);
        byte[] iv;
        AuthenticatedCipherText authCipherText;
        if (header.getEncryptionMethod().equals(EncryptionMethod.A128CBC_HS256) || header.getEncryptionMethod().equals(EncryptionMethod.A192CBC_HS384) || header.getEncryptionMethod().equals(EncryptionMethod.A256CBC_HS512)) {
            iv = AESCBC.generateIV(jcaProvider.getSecureRandom());
            authCipherText = AESCBC.encryptAuthenticated(cek, iv, plainText, aad, jcaProvider.getContentEncryptionProvider(), jcaProvider.getMACProvider());
        }
        else if (header.getEncryptionMethod().equals(EncryptionMethod.A128GCM) || header.getEncryptionMethod().equals(EncryptionMethod.A192GCM) || header.getEncryptionMethod().equals(EncryptionMethod.A256GCM)) {
            final Container<byte[]> ivContainer = new Container<byte[]>(AESGCM.generateIV(jcaProvider.getSecureRandom()));
            authCipherText = AESGCM.encrypt(cek, ivContainer, plainText, aad, jcaProvider.getContentEncryptionProvider());
            iv = ivContainer.get();
        }
        else {
            if (!header.getEncryptionMethod().equals(EncryptionMethod.A128CBC_HS256_DEPRECATED) && !header.getEncryptionMethod().equals(EncryptionMethod.A256CBC_HS512_DEPRECATED)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedEncryptionMethod(header.getEncryptionMethod(), ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS));
            }
            iv = AESCBC.generateIV(jcaProvider.getSecureRandom());
            authCipherText = AESCBC.encryptWithConcatKDF(header, cek, encryptedKey, iv, plainText, jcaProvider.getContentEncryptionProvider(), jcaProvider.getMACProvider());
        }
        return new JWECryptoParts(header, encryptedKey, Base64URL.encode(iv), Base64URL.encode(authCipherText.getCipherText()), Base64URL.encode(authCipherText.getAuthenticationTag()));
    }
    
    public static byte[] decrypt(final JWEHeader header, final Base64URL encryptedKey, final Base64URL iv, final Base64URL cipherText, final Base64URL authTag, final SecretKey cek, final JWEJCAContext jcaProvider) throws JOSEException {
        checkCEKLength(cek, header.getEncryptionMethod());
        final byte[] aad = AAD.compute(header);
        byte[] plainText;
        if (header.getEncryptionMethod().equals(EncryptionMethod.A128CBC_HS256) || header.getEncryptionMethod().equals(EncryptionMethod.A192CBC_HS384) || header.getEncryptionMethod().equals(EncryptionMethod.A256CBC_HS512)) {
            plainText = AESCBC.decryptAuthenticated(cek, iv.decode(), cipherText.decode(), aad, authTag.decode(), jcaProvider.getContentEncryptionProvider(), jcaProvider.getMACProvider());
        }
        else if (header.getEncryptionMethod().equals(EncryptionMethod.A128GCM) || header.getEncryptionMethod().equals(EncryptionMethod.A192GCM) || header.getEncryptionMethod().equals(EncryptionMethod.A256GCM)) {
            plainText = AESGCM.decrypt(cek, iv.decode(), cipherText.decode(), aad, authTag.decode(), jcaProvider.getContentEncryptionProvider());
        }
        else {
            if (!header.getEncryptionMethod().equals(EncryptionMethod.A128CBC_HS256_DEPRECATED) && !header.getEncryptionMethod().equals(EncryptionMethod.A256CBC_HS512_DEPRECATED)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedEncryptionMethod(header.getEncryptionMethod(), ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS));
            }
            plainText = AESCBC.decryptWithConcatKDF(header, cek, encryptedKey, iv, cipherText, authTag, jcaProvider.getContentEncryptionProvider(), jcaProvider.getMACProvider());
        }
        return DeflateHelper.applyDecompression(header, plainText);
    }
}
