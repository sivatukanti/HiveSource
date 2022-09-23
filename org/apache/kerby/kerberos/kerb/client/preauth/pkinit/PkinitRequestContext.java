// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.preauth.pkinit.PluginOpts;
import org.apache.kerby.kerberos.kerb.crypto.dh.DiffieHellmanClient;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.preauth.pkinit.IdentityOpts;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;

public class PkinitRequestContext implements PluginRequestContext
{
    public PkinitRequestOpts requestOpts;
    public IdentityOpts identityOpts;
    public boolean doIdentityMatching;
    public PaDataType paType;
    public boolean rfc6112Kdc;
    public boolean identityInitialized;
    public boolean identityPrompted;
    private DiffieHellmanClient dhClient;
    
    public PkinitRequestContext() {
        this.requestOpts = new PkinitRequestOpts();
        this.identityOpts = new IdentityOpts();
    }
    
    public void updateRequestOpts(final PluginOpts pluginOpts) {
        this.requestOpts.requireEku = pluginOpts.requireEku;
        this.requestOpts.acceptSecondaryEku = pluginOpts.acceptSecondaryEku;
        this.requestOpts.allowUpn = pluginOpts.allowUpn;
        this.requestOpts.usingRsa = pluginOpts.usingRsa;
        this.requestOpts.requireCrlChecking = pluginOpts.requireCrlChecking;
    }
    
    public void setDhClient(final DiffieHellmanClient client) {
        this.dhClient = client;
    }
    
    public DiffieHellmanClient getDhClient() {
        return this.dhClient;
    }
}
