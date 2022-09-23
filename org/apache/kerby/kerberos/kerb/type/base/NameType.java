// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum NameType implements EnumType
{
    NT_UNKNOWN(0), 
    NT_PRINCIPAL(1), 
    NT_SRV_INST(2), 
    NT_SRV_HST(3), 
    NT_SRV_XHST(4), 
    NT_UID(5), 
    NT_X500_PRINCIPAL(6), 
    NT_SMTP_NAME(7), 
    NT_ENTERPRISE(10), 
    NT_WELLKNOWN(11);
    
    private int value;
    
    private NameType(final int value) {
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
    
    public static NameType fromValue(final Integer value) {
        if (value != null) {
            for (final NameType nameType : values()) {
                if (nameType.getValue() == value) {
                    return nameType;
                }
            }
        }
        return NameType.NT_UNKNOWN;
    }
}
