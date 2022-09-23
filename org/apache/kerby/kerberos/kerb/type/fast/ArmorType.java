// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.EnumType;

public enum ArmorType implements EnumType
{
    NONE(0), 
    ARMOR_AP_REQUEST(1);
    
    private final int value;
    
    private ArmorType(final int value) {
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
    
    public static ArmorType fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (ArmorType)e;
                }
            }
        }
        return ArmorType.NONE;
    }
}
