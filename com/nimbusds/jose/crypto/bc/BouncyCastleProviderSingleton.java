// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto.bc;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class BouncyCastleProviderSingleton
{
    private static BouncyCastleProvider bouncyCastleProvider;
    
    private BouncyCastleProviderSingleton() {
    }
    
    public static BouncyCastleProvider getInstance() {
        if (BouncyCastleProviderSingleton.bouncyCastleProvider != null) {
            return BouncyCastleProviderSingleton.bouncyCastleProvider;
        }
        return BouncyCastleProviderSingleton.bouncyCastleProvider = new BouncyCastleProvider();
    }
}
