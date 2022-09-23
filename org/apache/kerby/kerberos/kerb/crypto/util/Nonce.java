// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

import java.security.SecureRandom;

public class Nonce
{
    private static SecureRandom srand;
    
    public static synchronized int value() {
        final int value = Nonce.srand.nextInt();
        return value & Integer.MAX_VALUE;
    }
    
    static {
        Nonce.srand = new SecureRandom();
    }
}
