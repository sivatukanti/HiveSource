// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ticket;

import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;
import org.apache.kerby.kerberos.kerb.type.kdc.EncTgsRepPart;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class SgtTicket extends KrbTicket
{
    private PrincipalName clientPrincipal;
    
    public SgtTicket(final Ticket ticket, final EncTgsRepPart encKdcRepPart) {
        super(ticket, encKdcRepPart);
    }
    
    public PrincipalName getClientPrincipal() {
        return this.clientPrincipal;
    }
    
    public void setClientPrincipal(final PrincipalName clientPrincipal) {
        this.clientPrincipal = clientPrincipal;
    }
}
