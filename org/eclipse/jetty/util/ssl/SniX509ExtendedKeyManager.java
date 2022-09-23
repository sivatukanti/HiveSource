// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ssl;

import org.eclipse.jetty.util.log.Log;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSocket;
import java.util.Iterator;
import java.util.Arrays;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SNIMatcher;
import java.util.Collection;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import org.eclipse.jetty.util.log.Logger;
import javax.net.ssl.X509ExtendedKeyManager;

public class SniX509ExtendedKeyManager extends X509ExtendedKeyManager
{
    public static final String SNI_X509 = "org.eclipse.jetty.util.ssl.snix509";
    private static final String NO_MATCHERS = "no_matchers";
    private static final Logger LOG;
    private final X509ExtendedKeyManager _delegate;
    
    public SniX509ExtendedKeyManager(final X509ExtendedKeyManager keyManager) {
        this._delegate = keyManager;
    }
    
    @Override
    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        return this._delegate.chooseClientAlias(keyType, issuers, socket);
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] keyType, final Principal[] issuers, final SSLEngine engine) {
        return this._delegate.chooseEngineClientAlias(keyType, issuers, engine);
    }
    
    protected String chooseServerAlias(final String keyType, final Principal[] issuers, final Collection<SNIMatcher> matchers, final SSLSession session) {
        final String[] aliases = this._delegate.getServerAliases(keyType, issuers);
        if (aliases == null || aliases.length == 0) {
            return null;
        }
        String host = null;
        X509 x509 = null;
        if (matchers != null) {
            for (final SNIMatcher m : matchers) {
                if (m instanceof SslContextFactory.AliasSNIMatcher) {
                    final SslContextFactory.AliasSNIMatcher matcher = (SslContextFactory.AliasSNIMatcher)m;
                    host = matcher.getHost();
                    x509 = matcher.getX509();
                    break;
                }
            }
        }
        if (SniX509ExtendedKeyManager.LOG.isDebugEnabled()) {
            SniX509ExtendedKeyManager.LOG.debug("Matched {} with {} from {}", host, x509, Arrays.asList(aliases));
        }
        if (x509 != null) {
            for (final String a : aliases) {
                if (a.equals(x509.getAlias())) {
                    session.putValue("org.eclipse.jetty.util.ssl.snix509", x509);
                    return a;
                }
            }
            return null;
        }
        return "no_matchers";
    }
    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        final SSLSocket sslSocket = (SSLSocket)socket;
        String alias = this.chooseServerAlias(keyType, issuers, sslSocket.getSSLParameters().getSNIMatchers(), sslSocket.getHandshakeSession());
        if (alias == "no_matchers") {
            alias = this._delegate.chooseServerAlias(keyType, issuers, socket);
        }
        if (SniX509ExtendedKeyManager.LOG.isDebugEnabled()) {
            SniX509ExtendedKeyManager.LOG.debug("Chose alias {}/{} on {}", alias, keyType, socket);
        }
        return alias;
    }
    
    @Override
    public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        String alias = this.chooseServerAlias(keyType, issuers, engine.getSSLParameters().getSNIMatchers(), engine.getHandshakeSession());
        if (alias == "no_matchers") {
            alias = this._delegate.chooseEngineServerAlias(keyType, issuers, engine);
        }
        if (SniX509ExtendedKeyManager.LOG.isDebugEnabled()) {
            SniX509ExtendedKeyManager.LOG.debug("Chose alias {}/{} on {}", alias, keyType, engine);
        }
        return alias;
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        return this._delegate.getCertificateChain(alias);
    }
    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return this._delegate.getClientAliases(keyType, issuers);
    }
    
    @Override
    public PrivateKey getPrivateKey(final String alias) {
        return this._delegate.getPrivateKey(alias);
    }
    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return this._delegate.getServerAliases(keyType, issuers);
    }
    
    static {
        LOG = Log.getLogger(SniX509ExtendedKeyManager.class);
    }
}
