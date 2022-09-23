// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.request;

import org.slf4j.LoggerFactory;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlag;
import org.apache.kerby.kerberos.kerb.type.base.LastReqType;
import org.apache.kerby.kerberos.kerb.type.base.LastReqEntry;
import org.apache.kerby.kerberos.kerb.type.base.LastReq;
import org.apache.kerby.kerberos.kerb.type.kdc.EncAsRepPart;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcRep;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.kdc.AsRep;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.type.base.NameType;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.identity.KrbIdentity;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.type.kdc.AsReq;
import org.slf4j.Logger;

public class AsRequest extends KdcRequest
{
    private static final Logger LOG;
    
    public AsRequest(final AsReq asReq, final KdcContext kdcContext) {
        super(asReq, kdcContext);
    }
    
    @Override
    protected void checkClient() throws KrbException {
        final KdcReq request = this.getKdcReq();
        PrincipalName clientPrincipal;
        if (this.isToken()) {
            AsRequest.LOG.info("The request has a token with subject: " + this.getToken().getSubject());
            clientPrincipal = new PrincipalName(this.getToken().getSubject());
        }
        else {
            clientPrincipal = request.getReqBody().getCname();
        }
        if (clientPrincipal == null) {
            AsRequest.LOG.warn("Client principal name is null.");
            throw new KrbException(KrbErrorCode.KDC_ERR_C_PRINCIPAL_UNKNOWN);
        }
        String clientRealm = request.getReqBody().getRealm();
        if (clientRealm == null || clientRealm.isEmpty()) {
            clientRealm = this.getKdcContext().getKdcRealm();
        }
        clientPrincipal.setRealm(clientRealm);
        this.setClientPrincipal(clientPrincipal);
        KrbIdentity clientEntry;
        if (this.isToken()) {
            clientEntry = new KrbIdentity(clientPrincipal.getName());
            clientEntry.setExpireTime(new KerberosTime(this.getToken().getExpiredTime().getTime()));
        }
        else {
            clientEntry = this.getEntry(clientPrincipal.getName());
        }
        if (clientEntry == null) {
            AsRequest.LOG.warn("Can't get the client entry.");
            throw new KrbException(KrbErrorCode.KDC_ERR_C_PRINCIPAL_UNKNOWN);
        }
        if (this.isAnonymous()) {
            clientEntry.setPrincipal(new PrincipalName(clientPrincipal.getName(), NameType.NT_WELLKNOWN));
        }
        this.setClientEntry(clientEntry);
        for (final EncryptionType encType : request.getReqBody().getEtypes()) {
            if (clientEntry.getKeys().containsKey(encType)) {
                final EncryptionKey clientKey = clientEntry.getKeys().get(encType);
                this.setClientKey(clientKey);
                break;
            }
        }
    }
    
    @Override
    protected void issueTicket() throws KrbException {
        final TicketIssuer issuer = new TgtTicketIssuer(this);
        final Ticket newTicket = issuer.issueTicket();
        AsRequest.LOG.info("AS_REQ ISSUE: authtime " + newTicket.getEncPart().getAuthTime().getTime() + "," + newTicket.getEncPart().getCname() + " for " + newTicket.getSname());
        this.setTicket(newTicket);
    }
    
    @Override
    protected void makeReply() throws KrbException {
        final Ticket ticket = this.getTicket();
        final AsRep reply = new AsRep();
        reply.setTicket(ticket);
        reply.setCname(this.getClientEntry().getPrincipal());
        reply.setCrealm(this.getKdcContext().getKdcRealm());
        final EncKdcRepPart encKdcRepPart = this.makeEncKdcRepPart();
        reply.setEncPart(encKdcRepPart);
        final EncryptionKey clientKey = this.getClientKey();
        if (clientKey != null) {
            final EncryptedData encryptedData = EncryptionUtil.seal(encKdcRepPart, clientKey, KeyUsage.AS_REP_ENCPART);
            reply.setEncryptedEncPart(encryptedData);
            if (this.isPkinit()) {
                reply.setPaData(this.getPreauthContext().getOutputPaData());
            }
            this.setReply(reply);
            return;
        }
        throw new KrbException("Cant't get the client key to encrypt the kdc rep part.");
    }
    
    protected EncKdcRepPart makeEncKdcRepPart() {
        final KdcReq request = this.getKdcReq();
        final Ticket ticket = this.getTicket();
        final EncKdcRepPart encKdcRepPart = new EncAsRepPart();
        encKdcRepPart.setKey(ticket.getEncPart().getKey());
        final LastReq lastReq = new LastReq();
        final LastReqEntry entry = new LastReqEntry();
        entry.setLrType(LastReqType.THE_LAST_INITIAL);
        entry.setLrValue(new KerberosTime());
        lastReq.add(entry);
        encKdcRepPart.setLastReq(lastReq);
        encKdcRepPart.setNonce(request.getReqBody().getNonce());
        encKdcRepPart.setFlags(ticket.getEncPart().getFlags());
        encKdcRepPart.setAuthTime(ticket.getEncPart().getAuthTime());
        encKdcRepPart.setStartTime(ticket.getEncPart().getStartTime());
        encKdcRepPart.setEndTime(ticket.getEncPart().getEndTime());
        if (ticket.getEncPart().getFlags().isFlagSet(TicketFlag.RENEWABLE)) {
            encKdcRepPart.setRenewTill(ticket.getEncPart().getRenewtill());
        }
        encKdcRepPart.setSname(ticket.getSname());
        encKdcRepPart.setSrealm(ticket.getRealm());
        encKdcRepPart.setCaddr(ticket.getEncPart().getClientAddresses());
        return encKdcRepPart;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AsRequest.class);
    }
}
