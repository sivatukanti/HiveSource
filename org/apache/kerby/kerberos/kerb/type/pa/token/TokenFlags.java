// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.token;

import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlag;
import org.apache.kerby.asn1.type.Asn1Flags;

public class TokenFlags extends Asn1Flags
{
    public TokenFlags() {
        this(0);
    }
    
    public TokenFlags(final int value) {
        this.setFlags(value);
    }
    
    public boolean isInvalid() {
        return this.isFlagSet(TicketFlag.INVALID.getValue());
    }
}
