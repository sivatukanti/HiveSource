// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb;

import org.apache.kerby.kerberos.kerb.provider.TokenProvider;

public class KrbRuntime
{
    private static TokenProvider tokenProvider;
    
    public static synchronized TokenProvider getTokenProvider() {
        if (KrbRuntime.tokenProvider == null) {
            throw new RuntimeException("No token provider is available");
        }
        return KrbRuntime.tokenProvider;
    }
    
    public static synchronized void setTokenProvider(final TokenProvider tokenProvider) {
        KrbRuntime.tokenProvider = tokenProvider;
    }
}
