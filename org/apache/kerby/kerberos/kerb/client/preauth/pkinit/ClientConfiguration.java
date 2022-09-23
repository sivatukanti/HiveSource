// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.crypto.dh.DhGroup;
import javax.crypto.spec.DHParameterSpec;

public class ClientConfiguration
{
    private String certificatePath;
    private String cmsType;
    private boolean isDhUsed;
    private DHParameterSpec dhGroup;
    private boolean isDhKeysReused;
    
    public ClientConfiguration() {
        this.isDhUsed = true;
        this.dhGroup = DhGroup.MODP_GROUP2;
    }
    
    public String getCertificatePath() {
        return this.certificatePath;
    }
    
    public void setCertificatePath(final String certificatePath) {
        this.certificatePath = certificatePath;
    }
    
    public String getCmsType() {
        return this.cmsType;
    }
    
    public void setCmsType(final String cmsType) {
        this.cmsType = cmsType;
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
}
