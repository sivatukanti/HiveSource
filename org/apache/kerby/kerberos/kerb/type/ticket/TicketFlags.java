// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ticket;

import org.apache.kerby.asn1.type.Asn1Flags;

public class TicketFlags extends Asn1Flags
{
    public TicketFlags() {
        this(0);
    }
    
    public TicketFlags(final int value) {
        this.setFlags(value);
    }
    
    public boolean isInvalid() {
        return this.isFlagSet(TicketFlag.INVALID.getValue());
    }
}
