// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.JOSEException;
import java.security.Provider;
import com.nimbusds.jose.util.Container;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class AESGCMKW
{
    public static AuthenticatedCipherText encryptCEK(final SecretKey cek, final Container<byte[]> iv, final SecretKey kek, final Provider provider) throws JOSEException {
        return AESGCM.encrypt(kek, iv, cek.getEncoded(), new byte[0], provider);
    }
    
    public static SecretKey decryptCEK(final SecretKey kek, final byte[] iv, final AuthenticatedCipherText authEncrCEK, final int keyLength, final Provider provider) throws JOSEException {
        final byte[] keyBytes = AESGCM.decrypt(kek, iv, authEncrCEK.getCipherText(), new byte[0], authEncrCEK.getAuthenticationTag(), provider);
        if (ByteUtils.safeBitLength(keyBytes) != keyLength) {
            throw new KeyLengthException("CEK key length mismatch: " + ByteUtils.safeBitLength(keyBytes) + " != " + keyLength);
        }
        return new SecretKeySpec(keyBytes, "AES");
    }
    
    private AESGCMKW() {
    }
}
