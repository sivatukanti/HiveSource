// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.crypto.dh.DhGroup;
import javax.crypto.spec.DHParameterSpec;

public class ServerConfiguration
{
    private boolean isDhUsed;
    private DHParameterSpec dhGroup;
    private boolean isDhKeysReused;
    private long dhKeyExpiration;
    private int dhNonceLength;
    
    public ServerConfiguration() {
        this.dhGroup = DhGroup.MODP_GROUP2;
        this.dhKeyExpiration = 86400000L;
        this.dhNonceLength = 32;
    }
    
    public boolean isDhUsed() {
        return this.isDhUsed;
    }
    
    public void setDhUsed(final boolean isDhUsed) {
        this.isDhUsed = isDhUsed;
    }
    
    public DHParameterSpec getDhGroup() {
        return this.dhGroup;
    }
    
    public void setDhGroup(final DHParameterSpec dhGroup) {
        this.dhGroup = dhGroup;
    }
    
    public boolean isDhKeysReused() {
        return this.isDhKeysReused;
    }
    
    public void setDhKeysReused(final boolean isDhKeysReused) {
        this.isDhKeysReused = isDhKeysReused;
    }
    
    public long getDhKeyExpiration() {
        return this.dhKeyExpiration;
    }
    
    public void setDhKeyExpiration(final long dhKeyExpiration) {
        this.dhKeyExpiration = dhKeyExpiration;
    }
    
    public int getDhNonceLength() {
        return this.dhNonceLength;
    }
    
    public void setDhNonceLength(final int dhNonceLength) {
        this.dhNonceLength = dhNonceLength;
    }
}
