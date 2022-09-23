// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.EnumType;

public enum FastOption implements EnumType
{
    NONE(-1), 
    RESERVED(0), 
    HIDE_CLIENT_NAMES(1), 
    KDC_FOLLOW_REFERRALS(16);
    
    private final int value;
    
    private FastOption(final int value) {
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
    
    public static FastOption fromValue(final int value) {
        for (final EnumType e : values()) {
            if (e.getValue() == value) {
                return (FastOption)e;
            }
        }
        return FastOption.NONE;
    }
}
