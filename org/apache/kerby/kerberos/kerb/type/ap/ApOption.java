// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ap;

import org.apache.kerby.asn1.EnumType;

public enum ApOption implements EnumType
{
    NONE(-1), 
    RESERVED(Integer.MIN_VALUE), 
    USE_SESSION_KEY(1073741824), 
    MUTUAL_REQUIRED(536870912), 
    ETYPE_NEGOTIATION(2), 
    USE_SUBKEY(1);
    
    private final int value;
    
    private ApOption(final int value) {
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
    
    public static ApOption fromValue(final int value) {
        for (final EnumType e : values()) {
            if (e.getValue() == value) {
                return (ApOption)e;
            }
        }
        return ApOption.NONE;
    }
}
