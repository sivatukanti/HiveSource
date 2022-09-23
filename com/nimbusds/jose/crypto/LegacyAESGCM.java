// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import org.bouncycastle.crypto.InvalidCipherTextException;
import com.nimbusds.jose.JOSEException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.engines.AESEngine;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class LegacyAESGCM
{
    public static final int AUTH_TAG_BIT_LENGTH = 128;
    
    public static AESEngine createAESCipher(final SecretKey secretKey, final boolean forEncryption) {
        final AESEngine cipher = new AESEngine();
        final CipherParameters cipherParams = (CipherParameters)new KeyParameter(secretKey.getEncoded());
        cipher.init(forEncryption, cipherParams);
        return cipher;
    }
    
    private static GCMBlockCipher createAESGCMCipher(final SecretKey secretKey, final boolean forEncryption, final byte[] iv, final byte[] authData) {
        final BlockCipher cipher = (BlockCipher)createAESCipher(secretKey, forEncryption);
        final GCMBlockCipher gcm = new GCMBlockCipher(cipher);
        final AEADParameters aeadParams = new AEADParameters(new KeyParameter(secretKey.getEncoded()), 128, iv, authData);
        gcm.init(forEncryption, (CipherParameters)aeadParams);
        return gcm;
    }
    
    public static AuthenticatedCipherText encrypt(final SecretKey secretKey, final byte[] iv, final byte[] plainText, final byte[] authData) throws JOSEException {
        final GCMBlockCipher cipher = createAESGCMCipher(secretKey, true, iv, authData);
        final int outputLength = cipher.getOutputSize(plainText.length);
        final byte[] output = new byte[outputLength];
        int outputOffset = cipher.processBytes(plainText, 0, plainText.length, output, 0);
        try {
            outputOffset += cipher.doFinal(output, outputOffset);
        }
        catch (InvalidCipherTextException e) {
            throw new JOSEException("Couldn't generate GCM authentication tag: " + e.getMessage(), (Throwable)e);
        }
        final int authTagLength = 16;
        final byte[] cipherText = new byte[outputOffset - authTagLength];
        final byte[] authTag = new byte[authTagLength];
        System.arraycopy(output, 0, cipherText, 0, cipherText.length);
        System.arraycopy(output, outputOffset - authTagLength, authTag, 0, authTag.length);
        return new AuthenticatedCipherText(cipherText, authTag);
    }
    
    public static byte[] decrypt(final SecretKey secretKey, final byte[] iv, final byte[] cipherText, final byte[] authData, final byte[] authTag) throws JOSEException {
        final GCMBlockCipher cipher = createAESGCMCipher(secretKey, false, iv, authData);
        final byte[] input = new byte[cipherText.length + authTag.length];
        System.arraycopy(cipherText, 0, input, 0, cipherText.length);
        System.arraycopy(authTag, 0, input, cipherText.length, authTag.length);
        final int outputLength = cipher.getOutputSize(input.length);
        final byte[] output = new byte[outputLength];
        int outputOffset = cipher.processBytes(input, 0, input.length, output, 0);
        try {
            outputOffset += cipher.doFinal(output, outputOffset);
        }
        catch (InvalidCipherTextException e) {
            throw new JOSEException("Couldn't validate GCM authentication tag: " + e.getMessage(), (Throwable)e);
        }
        return output;
    }
    
    private LegacyAESGCM() {
    }
}
