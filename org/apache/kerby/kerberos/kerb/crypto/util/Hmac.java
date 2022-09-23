// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

import java.util.Arrays;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;

public class Hmac
{
    public static byte[] hmac(final HashProvider hashProvider, final byte[] key, final byte[] data, final int outputSize) throws KrbException {
        return hmac(hashProvider, key, data, 0, data.length, outputSize);
    }
    
    public static byte[] hmac(final HashProvider hashProvider, final byte[] key, final byte[] data, final int start, final int len, final int outputSize) throws KrbException {
        final byte[] hash = hmac(hashProvider, key, data, start, len);
        final byte[] output = new byte[outputSize];
        System.arraycopy(hash, 0, output, 0, outputSize);
        return output;
    }
    
    public static byte[] hmac(final HashProvider hashProvider, final byte[] key, final byte[] data) throws KrbException {
        return hmac(hashProvider, key, data, 0, data.length);
    }
    
    public static byte[] hmac(final HashProvider hashProvider, final byte[] key, final byte[] data, final int start, final int len) throws KrbException {
        final int blockLen = hashProvider.blockSize();
        final byte[] innerPaddedKey = new byte[blockLen];
        final byte[] outerPaddedKey = new byte[blockLen];
        Arrays.fill(innerPaddedKey, (byte)54);
        for (int i = 0; i < key.length; ++i) {
            final byte[] array = innerPaddedKey;
            final int n = i;
            array[n] ^= key[i];
        }
        Arrays.fill(outerPaddedKey, (byte)92);
        for (int i = 0; i < key.length; ++i) {
            final byte[] array2 = outerPaddedKey;
            final int n2 = i;
            array2[n2] ^= key[i];
        }
        hashProvider.hash(innerPaddedKey);
        hashProvider.hash(data, start, len);
        final byte[] tmp = hashProvider.output();
        hashProvider.hash(outerPaddedKey);
        hashProvider.hash(tmp);
        return hashProvider.output();
    }
}
