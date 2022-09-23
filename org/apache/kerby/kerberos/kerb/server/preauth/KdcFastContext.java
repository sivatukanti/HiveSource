// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth;

import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.kerberos.kerb.type.fast.FastOptions;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;

public class KdcFastContext
{
    private EncryptionKey armorKey;
    private EncryptionKey strengthenKey;
    private FastOptions fastOptions;
    private int fastFlags;
    
    public EncryptionKey getArmorKey() {
        return this.armorKey;
    }
    
    public void setArmorKey(final EncryptionKey armorKey) {
        this.armorKey = armorKey;
    }
    
    public EncryptionKey getStrengthenKey() {
        return this.strengthenKey;
    }
    
    public void setStrengthenKey(final EncryptionKey strengthenKey) {
        this.strengthenKey = strengthenKey;
    }
    
    public FastOptions getFastOptions() {
        return this.fastOptions;
    }
    
    public void setFastOptions(final FastOptions fastOptions) {
        this.fastOptions = fastOptions;
    }
    
    public int getFastFlags() {
        return this.fastFlags;
    }
    
    public void setFastFlags(final int fastFlags) {
        this.fastFlags = fastFlags;
    }
    
    public byte[] findAndProcessFast(final KdcReq kdcReq, final byte[] checksumData, final EncryptionKey tgsSubKey, final EncryptionKey tgsSessionKey) {
        return null;
    }
}
