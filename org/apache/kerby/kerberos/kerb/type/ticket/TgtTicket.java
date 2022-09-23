// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ticket;

import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;
import org.apache.kerby.kerberos.kerb.type.kdc.EncAsRepPart;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class TgtTicket extends KrbTicket
{
    private PrincipalName clientPrincipal;
    
    public TgtTicket(final Ticket ticket, final EncAsRepPart encKdcRepPart, final PrincipalName clientPrincipal) {
        super(ticket, encKdcRepPart);
        this.clientPrincipal = clientPrincipal;
    }
    
    public PrincipalName getClientPrincipal() {
        return this.clientPrincipal;
    }
}
