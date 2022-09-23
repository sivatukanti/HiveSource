// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.kdc;

import org.apache.kerby.asn1.EnumType;

public enum KdcOption implements EnumType
{
    NONE(-1), 
    FORWARDABLE(1073741824), 
    FORWARDED(536870912), 
    PROXIABLE(268435456), 
    PROXY(134217728), 
    ALLOW_POSTDATE(67108864), 
    POSTDATED(33554432), 
    RENEWABLE(8388608), 
    CNAME_IN_ADDL_TKT(131072), 
    CANONICALIZE(65536), 
    REQUEST_ANONYMOUS(32768), 
    DISABLE_TRANSITED_CHECK(32), 
    RENEWABLE_OK(16), 
    ENC_TKT_IN_SKEY(8), 
    RENEW(2), 
    VALIDATE(1);
    
    private final int value;
    
    private KdcOption(final int value) {
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
    
    public static KdcOption fromValue(final int value) {
        for (final EnumType e : values()) {
            if (e.getValue() == value) {
                return (KdcOption)e;
            }
        }
        return KdcOption.NONE;
    }
}
