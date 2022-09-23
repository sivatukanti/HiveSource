// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.ssl;

public class SslContextFactory extends org.eclipse.jetty.util.ssl.SslContextFactory
{
    public SslContextFactory() {
    }
    
    public SslContextFactory(final boolean trustAll) {
        super(trustAll);
    }
    
    public SslContextFactory(final String keyStorePath) {
        super(keyStorePath);
    }
}
