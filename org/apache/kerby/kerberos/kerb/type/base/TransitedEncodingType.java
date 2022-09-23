// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum TransitedEncodingType implements EnumType
{
    UNKNOWN(-1), 
    NULL(0), 
    DOMAIN_X500_COMPRESS(1);
    
    private final int value;
    
    private TransitedEncodingType(final int value) {
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
    
    public static TransitedEncodingType fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (TransitedEncodingType)e;
                }
            }
        }
        return TransitedEncodingType.NULL;
    }
}
