// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum TokenFormat implements EnumType
{
    NONE(0), 
    JWT(1);
    
    private final int value;
    
    private TokenFormat(final int value) {
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
    
    public static TokenFormat fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (TokenFormat)e;
                }
            }
        }
        return TokenFormat.NONE;
    }
}
