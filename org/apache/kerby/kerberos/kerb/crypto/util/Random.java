// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

import java.security.SecureRandom;

public final class Random
{
    private static SecureRandom instance;
    
    public static byte[] makeBytes(final int size) {
        final byte[] data = new byte[size];
        Random.instance.nextBytes(data);
        return data;
    }
    
    static {
        Random.instance = new SecureRandom();
    }
}
