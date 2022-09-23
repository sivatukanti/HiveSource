// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.security.AlgorithmParameters;
import java.security.spec.InvalidParameterSpecException;
import java.security.GeneralSecurityException;
import com.nimbusds.jose.util.ByteUtils;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import com.nimbusds.jose.JOSEException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.Cipher;
import java.security.Provider;
import com.nimbusds.jose.util.Container;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class AESGCM
{
    public static final int IV_BIT_LENGTH = 96;
    public static final int AUTH_TAG_BIT_LENGTH = 128;
    
    public static byte[] generateIV(final SecureRandom randomGen) {
        final byte[] bytes = new byte[12];
        randomGen.nextBytes(bytes);
        return bytes;
    }
    
    public static AuthenticatedCipherText encrypt(final SecretKey secretKey, final Container<byte[]> ivContainer, final byte[] plainText, final byte[] authData, final Provider provider) throws JOSEException {
        final byte[] iv = ivContainer.get();
        Cipher cipher;
        try {
            if (provider != null) {
                cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
            }
            else {
                cipher = Cipher.getInstance("AES/GCM/NoPadding");
            }
            final GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(1, secretKey, gcmSpec);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex3) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException("Couldn't create AES/GCM/NoPadding cipher: " + e.getMessage(), e);
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            return LegacyAESGCM.encrypt(secretKey, iv, plainText, authData);
        }
        cipher.updateAAD(authData);
        byte[] cipherOutput;
        try {
            cipherOutput = cipher.doFinal(plainText);
        }
        catch (IllegalBlockSizeException | BadPaddingException ex4) {
            final GeneralSecurityException ex2;
            final GeneralSecurityException e2 = ex2;
            throw new JOSEException("Couldn't encrypt with AES/GCM/NoPadding: " + e2.getMessage(), e2);
        }
        final int tagPos = cipherOutput.length - ByteUtils.byteLength(128);
        final byte[] cipherText = ByteUtils.subArray(cipherOutput, 0, tagPos);
        final byte[] authTag = ByteUtils.subArray(cipherOutput, tagPos, ByteUtils.byteLength(128));
        ivContainer.set(actualIVOf(cipher));
        return new AuthenticatedCipherText(cipherText, authTag);
    }
    
    private static byte[] actualIVOf(final Cipher cipher) throws JOSEException {
        final GCMParameterSpec actualParams = actualParamsOf(cipher);
        final byte[] iv = actualParams.getIV();
        final int tLen = actualParams.getTLen();
        validate(iv, tLen);
        return iv;
    }
    
    private static void validate(final byte[] iv, final int authTagLength) throws JOSEException {
        if (ByteUtils.safeBitLength(iv) != 96) {
            throw new JOSEException(String.format("IV length of %d bits is required, got %d", 96, ByteUtils.safeBitLength(iv)));
        }
        if (authTagLength != 128) {
            throw new JOSEException(String.format("Authentication tag length of %d bits is required, got %d", 128, authTagLength));
        }
    }
    
    private static GCMParameterSpec actualParamsOf(final Cipher cipher) throws JOSEException {
        final AlgorithmParameters algorithmParameters = cipher.getParameters();
        if (algorithmParameters == null) {
            throw new JOSEException("AES GCM ciphers are expected to make use of algorithm parameters");
        }
        try {
            return algorithmParameters.getParameterSpec(GCMParameterSpec.class);
        }
        catch (InvalidParameterSpecException shouldNotHappen) {
            throw new JOSEException(shouldNotHappen.getMessage(), shouldNotHappen);
        }
    }
    
    public static byte[] decrypt(final SecretKey secretKey, final byte[] iv, final byte[] cipherText, final byte[] authData, final byte[] authTag, final Provider provider) throws JOSEException {
        Cipher cipher;
        try {
            if (provider != null) {
                cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
            }
            else {
                cipher = Cipher.getInstance("AES/GCM/NoPadding");
            }
            final GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(2, secretKey, gcmSpec);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex3) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException("Couldn't create AES/GCM/NoPadding cipher: " + e.getMessage(), e);
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            return LegacyAESGCM.decrypt(secretKey, iv, cipherText, authData, authTag);
        }
        cipher.updateAAD(authData);
        try {
            return cipher.doFinal(ByteUtils.concat(new byte[][] { cipherText, authTag }));
        }
        catch (IllegalBlockSizeException | BadPaddingException ex4) {
            final GeneralSecurityException ex2;
            final GeneralSecurityException e = ex2;
            throw new JOSEException("AES/GCM/NoPadding decryption failed: " + e.getMessage(), e);
        }
    }
    
    private AESGCM() {
    }
}
