// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.identity;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.Map;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class KrbIdentity
{
    private PrincipalName principal;
    private int keyVersion;
    private int kdcFlags;
    private boolean disabled;
    private boolean locked;
    private KerberosTime expireTime;
    private KerberosTime createdTime;
    private Map<EncryptionType, EncryptionKey> keys;
    
    public KrbIdentity(final String principalName) {
        this.keyVersion = 1;
        this.kdcFlags = 0;
        this.expireTime = KerberosTime.NEVER;
        this.createdTime = KerberosTime.now();
        this.keys = new HashMap<EncryptionType, EncryptionKey>();
        this.principal = new PrincipalName(principalName);
    }
    
    public String getPrincipalName() {
        return this.principal.getName();
    }
    
    public void setPrincipalName(final String newPrincipalName) {
        this.principal = new PrincipalName(newPrincipalName);
    }
    
    public PrincipalName getPrincipal() {
        return this.principal;
    }
    
    public void setPrincipal(final PrincipalName principal) {
        this.principal = principal;
    }
    
    public KerberosTime getExpireTime() {
        return this.expireTime;
    }
    
    public void setExpireTime(final KerberosTime expireTime) {
        this.expireTime = expireTime;
    }
    
    public KerberosTime getCreatedTime() {
        return this.createdTime;
    }
    
    public void setCreatedTime(final KerberosTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public boolean isDisabled() {
        return this.disabled;
    }
    
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }
    
    public void addKey(final EncryptionKey encKey) {
        this.keys.put(encKey.getKeyType(), encKey);
    }
    
    public void addKeys(final List<EncryptionKey> encKeys) {
        for (final EncryptionKey key : encKeys) {
            this.keys.put(key.getKeyType(), key);
        }
    }
    
    public Map<EncryptionType, EncryptionKey> getKeys() {
        return this.keys;
    }
    
    public EncryptionKey getKey(final EncryptionType encType) {
        return this.keys.get(encType);
    }
    
    public int getKdcFlags() {
        return this.kdcFlags;
    }
    
    public void setKdcFlags(final int kdcFlags) {
        this.kdcFlags = kdcFlags;
    }
    
    public int getKeyVersion() {
        return this.keyVersion;
    }
    
    public void setKeyVersion(final int keyVersion) {
        this.keyVersion = keyVersion;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof KrbIdentity) {
            final KrbIdentity other = (KrbIdentity)obj;
            if (this.principal == null) {
                if (other.principal != null) {
                    return false;
                }
            }
            else if (!this.principal.equals(other.principal)) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.principal == null) ? 0 : this.principal.hashCode());
        return result;
    }
}
