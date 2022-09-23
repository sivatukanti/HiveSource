// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth;

import org.apache.kerby.asn1.EnumType;

public enum PaFlag implements EnumType
{
    NONE(-1), 
    PA_REAL(1), 
    PA_INFO(2);
    
    private final int value;
    
    private PaFlag(final int value) {
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
    
    public static PaFlag fromValue(final int value) {
        for (final EnumType e : values()) {
            if (e.getValue() == value) {
                return (PaFlag)e;
            }
        }
        return PaFlag.NONE;
    }
}
