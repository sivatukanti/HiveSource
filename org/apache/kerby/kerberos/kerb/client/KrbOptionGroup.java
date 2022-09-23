// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.KOptionGroup;

public enum KrbOptionGroup implements KOptionGroup
{
    NONE, 
    KRB, 
    KDC_FLAGS, 
    PKINIT, 
    TOKEN;
    
    @Override
    public String getGroupName() {
        return this.name().toLowerCase();
    }
}
