// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth;

import org.apache.kerby.kerberos.kerb.type.fast.FastOptions;
import org.apache.kerby.kerberos.kerb.type.fast.KrbFastArmor;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;

public class KrbFastRequestState
{
    private KdcReq fastOuterRequest;
    private EncryptionKey armorKey;
    private KrbFastArmor fastArmor;
    private FastOptions fastOptions;
    private int nonce;
    private int fastFlags;
    
    public KdcReq getFastOuterRequest() {
        return this.fastOuterRequest;
    }
    
    public void setFastOuterRequest(final KdcReq fastOuterRequest) {
        this.fastOuterRequest = fastOuterRequest;
    }
    
    public EncryptionKey getArmorKey() {
        return this.armorKey;
    }
    
    public void setArmorKey(final EncryptionKey armorKey) {
        this.armorKey = armorKey;
    }
    
    public KrbFastArmor getFastArmor() {
        return this.fastArmor;
    }
    
    public void setFastArmor(final KrbFastArmor fastArmor) {
        this.fastArmor = fastArmor;
    }
    
    public FastOptions getFastOptions() {
        return this.fastOptions;
    }
    
    public void setFastOptions(final FastOptions fastOptions) {
        this.fastOptions = fastOptions;
    }
    
    public int getNonce() {
        return this.nonce;
    }
    
    public void setNonce(final int nonce) {
        this.nonce = nonce;
    }
    
    public int getFastFlags() {
        return this.fastFlags;
    }
    
    public void setFastFlags(final int fastFlags) {
        this.fastFlags = fastFlags;
    }
}
