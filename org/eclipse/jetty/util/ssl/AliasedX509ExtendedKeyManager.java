// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ssl;

import javax.net.ssl.SSLEngine;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.X509ExtendedKeyManager;

public class AliasedX509ExtendedKeyManager extends X509ExtendedKeyManager
{
    private final String _alias;
    private final X509ExtendedKeyManager _delegate;
    
    public AliasedX509ExtendedKeyManager(final X509ExtendedKeyManager keyManager, final String keyAlias) {
        this._alias = keyAlias;
        this._delegate = keyManager;
    }
    
    public X509ExtendedKeyManager getDelegate() {
        return this._delegate;
    }
    
    @Override
    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        if (this._alias == null) {
            return this._delegate.chooseClientAlias(keyType, issuers, socket);
        }
        for (final String kt : keyType) {
            final String[] aliases = this._delegate.getClientAliases(kt, issuers);
            if (aliases != null) {
                for (final String a : aliases) {
                    if (this._alias.equals(a)) {
                        return this._alias;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        if (this._alias == null) {
            return this._delegate.chooseServerAlias(keyType, issuers, socket);
        }
        final String[] aliases = this._delegate.getServerAliases(keyType, issuers);
        if (aliases != null) {
            for (final String a : aliases) {
                if (this._alias.equals(a)) {
                    return this._alias;
                }
            }
        }
        return null;
    }
    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return this._delegate.getClientAliases(keyType, issuers);
    }
    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return this._delegate.getServerAliases(keyType, issuers);
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        return this._delegate.getCertificateChain(alias);
    }
    
    @Override
    public PrivateKey getPrivateKey(final String alias) {
        return this._delegate.getPrivateKey(alias);
    }
    
    @Override
    public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        if (this._alias == null) {
            return this._delegate.chooseEngineServerAlias(keyType, issuers, engine);
        }
        final String[] aliases = this._delegate.getServerAliases(keyType, issuers);
        if (aliases != null) {
            for (final String a : aliases) {
                if (this._alias.equals(a)) {
                    return this._alias;
                }
            }
        }
        return null;
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] keyType, final Principal[] issuers, final SSLEngine engine) {
        if (this._alias == null) {
            return this._delegate.chooseEngineClientAlias(keyType, issuers, engine);
        }
        for (final String kt : keyType) {
            final String[] aliases = this._delegate.getClientAliases(kt, issuers);
            if (aliases != null) {
                for (final String a : aliases) {
                    if (this._alias.equals(a)) {
                        return this._alias;
                    }
                }
            }
        }
        return null;
    }
}
