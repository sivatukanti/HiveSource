// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum KrbMessageType implements EnumType
{
    NONE(-1), 
    AS_REQ(10), 
    AS_REP(11), 
    TGS_REQ(12), 
    TGS_REP(13), 
    AP_REQ(14), 
    AP_REP(15), 
    KRB_SAFE(20), 
    KRB_PRIV(21), 
    KRB_CRED(22), 
    KRB_ERROR(30);
    
    private int value;
    
    private KrbMessageType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    public static KrbMessageType fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (KrbMessageType)e;
                }
            }
        }
        return KrbMessageType.NONE;
    }
}
