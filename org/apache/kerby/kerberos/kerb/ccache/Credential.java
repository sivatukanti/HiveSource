// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.ccache;

import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;
import org.apache.kerby.kerberos.kerb.type.ticket.KrbTicket;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlags;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class Credential
{
    private static final String CONF_REALM = "X-CACHECONF:";
    private PrincipalName clientName;
    private String clientRealm;
    private PrincipalName serverName;
    private String serverRealm;
    private EncryptionKey key;
    private KerberosTime authTime;
    private KerberosTime startTime;
    private KerberosTime endTime;
    private KerberosTime renewTill;
    private HostAddresses clientAddresses;
    private AuthorizationData authzData;
    private boolean isEncInSKey;
    private TicketFlags ticketFlags;
    private Ticket ticket;
    private Ticket secondTicket;
    
    public Credential() {
    }
    
    public Credential(final TgtTicket tgt) {
        final PrincipalName clientPrincipal = tgt.getClientPrincipal();
        clientPrincipal.setRealm(tgt.getRealm());
        this.init(tgt, clientPrincipal);
    }
    
    public Credential(final KrbTicket tkt, final PrincipalName clientPrincipal) {
        this.init(tkt, clientPrincipal);
    }
    
    private void init(final KrbTicket tkt, final PrincipalName clientPrincipal) {
        final EncKdcRepPart kdcRepPart = tkt.getEncKdcRepPart();
        this.serverName = kdcRepPart.getSname();
        this.serverRealm = kdcRepPart.getSrealm();
        this.serverName.setRealm(this.serverRealm);
        this.clientName = clientPrincipal;
        this.key = kdcRepPart.getKey();
        this.authTime = kdcRepPart.getAuthTime();
        this.startTime = kdcRepPart.getStartTime();
        this.endTime = kdcRepPart.getEndTime();
        this.renewTill = kdcRepPart.getRenewTill();
        this.ticketFlags = kdcRepPart.getFlags();
        this.clientAddresses = kdcRepPart.getCaddr();
        this.ticket = tkt.getTicket();
        this.clientRealm = kdcRepPart.getSrealm();
        this.isEncInSKey = false;
        this.secondTicket = null;
    }
    
    public PrincipalName getServicePrincipal() {
        return this.serverName;
    }
    
    public KerberosTime getAuthTime() {
        return this.authTime;
    }
    
    public KerberosTime getEndTime() {
        return this.endTime;
    }
    
    public int getEType() {
        return this.key.getKeyType().getValue();
    }
    
    public PrincipalName getClientName() {
        return this.clientName;
    }
    
    public PrincipalName getServerName() {
        return this.serverName;
    }
    
    public String getClientRealm() {
        return this.clientRealm;
    }
    
    public EncryptionKey getKey() {
        return this.key;
    }
    
    public KerberosTime getStartTime() {
        return this.startTime;
    }
    
    public KerberosTime getRenewTill() {
        return this.renewTill;
    }
    
    public HostAddresses getClientAddresses() {
        return this.clientAddresses;
    }
    
    public AuthorizationData getAuthzData() {
        return this.authzData;
    }
    
    public boolean isEncInSKey() {
        return this.isEncInSKey;
    }
    
    public TicketFlags getTicketFlags() {
        return this.ticketFlags;
    }
    
    public Ticket getTicket() {
        return this.ticket;
    }
    
    public Ticket getSecondTicket() {
        return this.secondTicket;
    }
    
    public void load(final CredCacheInputStream ccis, final int version) throws IOException {
        this.clientName = ccis.readPrincipal(version);
        if (this.clientName == null) {
            throw new IOException("Invalid client principal name");
        }
        this.serverName = ccis.readPrincipal(version);
        if (this.serverName == null) {
            throw new IOException("Invalid server principal name");
        }
        boolean isConfEntry = false;
        if (this.serverName.getRealm().equals("X-CACHECONF:")) {
            isConfEntry = true;
        }
        this.key = ccis.readKey(version);
        final KerberosTime[] times = ccis.readTimes();
        this.authTime = times[0];
        this.startTime = times[1];
        this.endTime = times[2];
        this.renewTill = times[3];
        this.isEncInSKey = ccis.readIsSkey();
        this.ticketFlags = ccis.readTicketFlags();
        this.clientAddresses = ccis.readAddr();
        this.authzData = ccis.readAuthzData();
        if (isConfEntry) {
            ccis.readCountedOctets();
        }
        else {
            this.ticket = ccis.readTicket();
        }
        this.secondTicket = ccis.readTicket();
    }
    
    public void store(final CredCacheOutputStream ccos, final int version) throws IOException {
        ccos.writePrincipal(this.clientName, version);
        ccos.writePrincipal(this.serverName, version);
        ccos.writeKey(this.key, version);
        ccos.writeTimes(new KerberosTime[] { this.authTime, this.startTime, this.endTime, this.renewTill });
        ccos.writeIsSkey(this.isEncInSKey);
        ccos.writeTicketFlags(this.ticketFlags);
        ccos.writeAddresses(this.clientAddresses);
        ccos.writeAuthzData(this.authzData);
        ccos.writeTicket(this.ticket);
        ccos.writeTicket(this.secondTicket);
    }
}
