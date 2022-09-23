// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import java.security.Provider;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class CipherHelper
{
    public static Cipher getInstance(final String name, final Provider provider) throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (provider == null) {
            return Cipher.getInstance(name);
        }
        return Cipher.getInstance(name, provider);
    }
}
