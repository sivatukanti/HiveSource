// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.X509KeyManager;

public class AliasedX509KeyManager implements X509KeyManager
{
    private String _keyAlias;
    private X509KeyManager _keyManager;
    
    public AliasedX509KeyManager(final String keyAlias, final X509KeyManager keyManager) throws Exception {
        this._keyAlias = keyAlias;
        this._keyManager = keyManager;
    }
    
    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        return (this._keyAlias == null) ? this._keyManager.chooseClientAlias(keyType, issuers, socket) : this._keyAlias;
    }
    
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        return (this._keyAlias == null) ? this._keyManager.chooseServerAlias(keyType, issuers, socket) : this._keyAlias;
    }
    
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return this._keyManager.getClientAliases(keyType, issuers);
    }
    
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return this._keyManager.getServerAliases(keyType, issuers);
    }
    
    public X509Certificate[] getCertificateChain(final String alias) {
        return this._keyManager.getCertificateChain(alias);
    }
    
    public PrivateKey getPrivateKey(final String alias) {
        return this._keyManager.getPrivateKey(alias);
    }
}
