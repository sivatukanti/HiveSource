// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.request;

import org.apache.kerby.kerberos.kerb.type.base.TransitedEncoding;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;

public class ServiceTicketIssuer extends TicketIssuer
{
    private final Ticket tgtTicket;
    private final AuthToken token;
    
    public ServiceTicketIssuer(final TgsRequest kdcRequest) {
        super(kdcRequest);
        this.tgtTicket = kdcRequest.getTgtTicket();
        this.token = kdcRequest.getToken();
    }
    
    protected KdcRequest getTgsRequest() {
        return this.getKdcRequest();
    }
    
    @Override
    protected PrincipalName getclientPrincipal() {
        if (this.token != null) {
            return new PrincipalName(this.token.getSubject());
        }
        return this.tgtTicket.getEncPart().getCname();
    }
    
    @Override
    protected TransitedEncoding getTransitedEncoding() {
        if (this.token != null) {
            return super.getTransitedEncoding();
        }
        return this.tgtTicket.getEncPart().getTransited();
    }
}
