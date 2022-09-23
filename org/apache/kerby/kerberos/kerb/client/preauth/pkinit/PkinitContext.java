// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.preauth.pkinit.IdentityOpts;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PluginOpts;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.PkinitPlgCryptoContext;

public class PkinitContext
{
    public PkinitPlgCryptoContext cryptoctx;
    public PluginOpts pluginOpts;
    public IdentityOpts identityOpts;
    
    public PkinitContext() {
        this.cryptoctx = new PkinitPlgCryptoContext();
        this.pluginOpts = new PluginOpts();
        this.identityOpts = new IdentityOpts();
    }
}
