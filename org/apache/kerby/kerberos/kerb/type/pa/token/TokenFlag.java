// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.token;

import org.apache.kerby.asn1.EnumType;

public enum TokenFlag implements EnumType
{
    NONE(-1), 
    ID_TOKEN_REQUIRED(1073741824), 
    AC_TOKEN_REQUIRED(536870912), 
    BEARER_TOKEN_REQUIRED(268435456), 
    HOK_TOKEN_REQUIRED(134217728);
    
    private final int value;
    
    private TokenFlag(final int value) {
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
    
    public static TokenFlag fromValue(final int value) {
        for (final EnumType e : values()) {
            if (e.getValue() == value) {
                return (TokenFlag)e;
            }
        }
        return TokenFlag.NONE;
    }
}
