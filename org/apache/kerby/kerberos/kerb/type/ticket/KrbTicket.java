// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ticket;

import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;

public class KrbTicket
{
    private Ticket ticket;
    private EncKdcRepPart encKdcRepPart;
    
    public KrbTicket(final Ticket ticket, final EncKdcRepPart encKdcRepPart) {
        this.ticket = ticket;
        this.encKdcRepPart = encKdcRepPart;
    }
    
    public Ticket getTicket() {
        return this.ticket;
    }
    
    public EncKdcRepPart getEncKdcRepPart() {
        return this.encKdcRepPart;
    }
    
    public EncryptionKey getSessionKey() {
        return this.encKdcRepPart.getKey();
    }
    
    public String getRealm() {
        return this.ticket.getRealm();
    }
}
