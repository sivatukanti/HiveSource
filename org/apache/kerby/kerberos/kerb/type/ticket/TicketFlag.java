// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ticket;

import org.apache.kerby.asn1.EnumType;

public enum TicketFlag implements EnumType
{
    NONE(-1), 
    FORWARDABLE(1073741824), 
    FORWARDED(536870912), 
    PROXIABLE(268435456), 
    PROXY(134217728), 
    MAY_POSTDATE(67108864), 
    POSTDATED(33554432), 
    INVALID(16777216), 
    RENEWABLE(8388608), 
    INITIAL(4194304), 
    PRE_AUTH(2097152), 
    HW_AUTH(1048576), 
    TRANSIT_POLICY_CHECKED(524288), 
    OK_AS_DELEGATE(262144), 
    ENC_PA_REP(65536), 
    ANONYMOUS(32768);
    
    private final int value;
    
    private TicketFlag(final int value) {
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
    
    public static TicketFlag fromValue(final int value) {
        for (final EnumType e : values()) {
            if (e.getValue() == value) {
                return (TicketFlag)e;
            }
        }
        return TicketFlag.NONE;
    }
}
