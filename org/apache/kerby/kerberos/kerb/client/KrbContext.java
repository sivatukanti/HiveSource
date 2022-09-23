// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.kerberos.kerb.crypto.util.Nonce;
import org.apache.kerby.kerberos.kerb.client.preauth.PreauthHandler;

public class KrbContext
{
    private KrbSetting krbSetting;
    private PreauthHandler preauthHandler;
    
    public void init(final KrbSetting krbSetting) {
        this.krbSetting = krbSetting;
        (this.preauthHandler = new PreauthHandler()).init(this);
    }
    
    public KrbSetting getKrbSetting() {
        return this.krbSetting;
    }
    
    public KrbConfig getConfig() {
        return this.krbSetting.getKrbConfig();
    }
    
    public int generateNonce() {
        return Nonce.value();
    }
    
    public long getTicketValidTime() {
        return this.getConfig().getTicketLifetime() * 1000L;
    }
    
    public PreauthHandler getPreauthHandler() {
        return this.preauthHandler;
    }
}
