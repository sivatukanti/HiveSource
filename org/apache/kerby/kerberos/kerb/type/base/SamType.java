// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum SamType implements EnumType
{
    SAM_NONE(0), 
    SAM_TYPE_ENIGMA(1), 
    SAM_TYPE_DIGI_PATH(2), 
    SAM_TYPE_SKEY_K0(3), 
    SAM_TYPE_SKEY(4), 
    SAM_TYPE_SECURID(5), 
    SAM_TYPE_CRYPTOCARD(6);
    
    private int value;
    
    private SamType(final int value) {
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
    
    public static SamType fromValue(final Integer value) {
        if (value != null) {
            for (final SamType st : values()) {
                if (value == st.getValue()) {
                    return st;
                }
            }
        }
        return SamType.SAM_NONE;
    }
}
