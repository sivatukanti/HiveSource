// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

public class Des
{
    static final byte[][] WEAK_KEYS;
    
    public static boolean isWeakKey(final byte[] key, final int offset, final int len) {
        for (final byte[] weakKey : Des.WEAK_KEYS) {
            boolean match = true;
            if (weakKey.length == len) {
                for (int i = 0; i < len; ++i) {
                    if (weakKey[i] != key[i]) {
                        match = false;
                        break;
                    }
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }
    
    public static void fixKey(final byte[] key, final int offset, final int len) {
        if (isWeakKey(key, offset, len)) {
            final int n = offset + 7;
            key[n] ^= 0xFFFFFFF0;
        }
    }
    
    static {
        WEAK_KEYS = new byte[][] { { 1, 1, 1, 1, 1, 1, 1, 1 }, { -2, -2, -2, -2, -2, -2, -2, -2 }, { 31, 31, 31, 31, 14, 14, 14, 14 }, { -32, -32, -32, -32, -15, -15, -15, -15 }, { 1, -2, 1, -2, 1, -2, 1, -2 }, { -2, 1, -2, 1, -2, 1, -2, 1 }, { 31, -32, 31, -32, 14, -15, 14, -15 }, { -32, 31, -32, 31, -15, 14, -15, 14 }, { 1, -32, 1, -32, 1, -15, 1, -15 }, { -32, 1, -32, 1, -15, 1, -15, 1 }, { 31, -2, 31, -2, 14, -2, 14, -2 }, { -2, 31, -2, 31, -2, 14, -2, 14 }, { 1, 31, 1, 31, 1, 14, 1, 14 }, { 31, 1, 31, 1, 14, 1, 14, 1 }, { -32, -2, -32, -2, -15, -2, -15, -2 }, { -2, -32, -2, -32, -2, -15, -2, -15 } };
    }
}
