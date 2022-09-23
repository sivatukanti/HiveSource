// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ssl;

import javax.net.ssl.SSLEngine;
import java.util.List;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLException;
import java.io.File;

public abstract class SslContext
{
    private final SslBufferPool bufferPool;
    
    public static SslProvider defaultServerProvider() {
        if (OpenSsl.isAvailable()) {
            return SslProvider.OPENSSL;
        }
        return SslProvider.JDK;
    }
    
    public static SslProvider defaultClientProvider() {
        return SslProvider.JDK;
    }
    
    public static SslContext newServerContext(final File certChainFile, final File keyFile) throws SSLException {
        return newServerContext(null, null, certChainFile, keyFile, null, null, null, 0L, 0L);
    }
    
    public static SslContext newServerContext(final File certChainFile, final File keyFile, final String keyPassword) throws SSLException {
        return newServerContext(null, null, certChainFile, keyFile, keyPassword, null, null, 0L, 0L);
    }
    
    public static SslContext newServerContext(final SslBufferPool bufPool, final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final Iterable<String> nextProtocols, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        return newServerContext(null, bufPool, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }
    
    public static SslContext newServerContext(final SslProvider provider, final File certChainFile, final File keyFile) throws SSLException {
        return newServerContext(provider, null, certChainFile, keyFile, null, null, null, 0L, 0L);
    }
    
    public static SslContext newServerContext(final SslProvider provider, final File certChainFile, final File keyFile, final String keyPassword) throws SSLException {
        return newServerContext(provider, null, certChainFile, keyFile, keyPassword, null, null, 0L, 0L);
    }
    
    public static SslContext newServerContext(SslProvider provider, final SslBufferPool bufPool, final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final Iterable<String> nextProtocols, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        if (provider == null) {
            provider = (OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK);
        }
        switch (provider) {
            case JDK: {
                return new JdkSslServerContext(bufPool, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
            }
            case OPENSSL: {
                return new OpenSslServerContext(bufPool, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
            }
            default: {
                throw new Error(provider.toString());
            }
        }
    }
    
    public static SslContext newClientContext() throws SSLException {
        return newClientContext(null, null, null, null, null, null, 0L, 0L);
    }
    
    public static SslContext newClientContext(final File certChainFile) throws SSLException {
        return newClientContext(null, null, certChainFile, null, null, null, 0L, 0L);
    }
    
    public static SslContext newClientContext(final TrustManagerFactory trustManagerFactory) throws SSLException {
        return newClientContext(null, null, null, trustManagerFactory, null, null, 0L, 0L);
    }
    
    public static SslContext newClientContext(final File certChainFile, final TrustManagerFactory trustManagerFactory) throws SSLException {
        return newClientContext(null, null, certChainFile, trustManagerFactory, null, null, 0L, 0L);
    }
    
    public static SslContext newClientContext(final SslBufferPool bufPool, final File certChainFile, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final Iterable<String> nextProtocols, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        return newClientContext(null, bufPool, certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }
    
    public static SslContext newClientContext(final SslProvider provider) throws SSLException {
        return newClientContext(provider, null, null, null, null, null, 0L, 0L);
    }
    
    public static SslContext newClientContext(final SslProvider provider, final File certChainFile) throws SSLException {
        return newClientContext(provider, null, certChainFile, null, null, null, 0L, 0L);
    }
    
    public static SslContext newClientContext(final SslProvider provider, final TrustManagerFactory trustManagerFactory) throws SSLException {
        return newClientContext(provider, null, null, trustManagerFactory, null, null, 0L, 0L);
    }
    
    public static SslContext newClientContext(final SslProvider provider, final File certChainFile, final TrustManagerFactory trustManagerFactory) throws SSLException {
        return newClientContext(provider, null, certChainFile, trustManagerFactory, null, null, 0L, 0L);
    }
    
    public static SslContext newClientContext(final SslProvider provider, final SslBufferPool bufPool, final File certChainFile, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final Iterable<String> nextProtocols, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        if (provider != null && provider != SslProvider.JDK) {
            throw new SSLException("client context unsupported for: " + provider);
        }
        return new JdkSslClientContext(bufPool, certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }
    
    SslContext(final SslBufferPool bufferPool) {
        this.bufferPool = ((bufferPool == null) ? this.newBufferPool() : bufferPool);
    }
    
    SslBufferPool newBufferPool() {
        return new SslBufferPool(false, false);
    }
    
    public final boolean isServer() {
        return !this.isClient();
    }
    
    public final SslBufferPool bufferPool() {
        return this.bufferPool;
    }
    
    public abstract boolean isClient();
    
    public abstract List<String> cipherSuites();
    
    public abstract long sessionCacheSize();
    
    public abstract long sessionTimeout();
    
    public abstract List<String> nextProtocols();
    
    public abstract SSLEngine newEngine();
    
    public abstract SSLEngine newEngine(final String p0, final int p1);
    
    public final SslHandler newHandler() {
        return this.newHandler(this.newEngine());
    }
    
    public final SslHandler newHandler(final String peerHost, final int peerPort) {
        return this.newHandler(this.newEngine(peerHost, peerPort));
    }
    
    private SslHandler newHandler(final SSLEngine engine) {
        final SslHandler handler = new SslHandler(engine, this.bufferPool());
        if (this.isClient()) {
            handler.setIssueHandshake(true);
        }
        handler.setCloseOnSSLException(true);
        return handler;
    }
}
