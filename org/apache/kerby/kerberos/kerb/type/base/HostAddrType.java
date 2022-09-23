// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum HostAddrType implements EnumType
{
    NONE(0), 
    ADDRTYPE_INET(2), 
    ADDRTYPE_IMPLINK(3), 
    ADDRTYPE_CHAOS(5), 
    ADDRTYPE_XNS(6), 
    ADDRTYPE_OSI(7), 
    ADDRTYPE_DECNET(12), 
    ADDRTYPE_APPLETALK(16), 
    ADDRTYPE_NETBIOS(20), 
    ADDRTYPE_INET6(24);
    
    private final int value;
    
    private HostAddrType(final int value) {
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
    
    public static HostAddrType fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (HostAddrType)e;
                }
            }
        }
        return HostAddrType.NONE;
    }
}
