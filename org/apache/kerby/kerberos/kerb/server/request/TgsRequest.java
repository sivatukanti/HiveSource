// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.request;

import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlag;
import org.apache.kerby.kerberos.kerb.type.base.LastReqType;
import org.apache.kerby.kerberos.kerb.type.base.LastReqEntry;
import org.apache.kerby.kerberos.kerb.type.base.LastReq;
import org.apache.kerby.kerberos.kerb.type.kdc.EncTgsRepPart;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcRep;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.type.kdc.TgsRep;
import org.apache.kerby.kerberos.kerb.identity.KrbIdentity;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.ap.ApOption;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.ap.Authenticator;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.ticket.EncTicketPart;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.type.kdc.TgsReq;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.slf4j.Logger;

public class TgsRequest extends KdcRequest
{
    private static final Logger LOG;
    private EncryptionKey tgtSessionKey;
    private Ticket tgtTicket;
    
    public TgsRequest(final TgsReq tgsReq, final KdcContext kdcContext) {
        super(tgsReq, kdcContext);
        this.setPreauthRequired(true);
    }
    
    public EncryptionKey getTgtSessionKey() {
        return this.tgtSessionKey;
    }
    
    public void setTgtSessionKey(final EncryptionKey tgtSessionKey) {
        this.tgtSessionKey = tgtSessionKey;
    }
    
    @Override
    protected void checkClient() throws KrbException {
    }
    
    public Ticket getTgtTicket() {
        return this.tgtTicket;
    }
    
    @Override
    protected void issueTicket() throws KrbException {
        final TicketIssuer issuer = new ServiceTicketIssuer(this);
        final Ticket newTicket = issuer.issueTicket();
        TgsRequest.LOG.info("TGS_REQ ISSUE: authtime " + newTicket.getEncPart().getAuthTime().getTime() + "," + newTicket.getEncPart().getCname() + " for " + newTicket.getSname());
        this.setTicket(newTicket);
    }
    
    public void verifyAuthenticator(final PaDataEntry paDataEntry) throws KrbException {
        final ApReq apReq = KrbCodec.decode(paDataEntry.getPaDataValue(), ApReq.class);
        if (apReq.getPvno() != 5) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BADVERSION);
        }
        if (apReq.getMsgType() != KrbMessageType.AP_REQ) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_MSG_TYPE);
        }
        this.tgtTicket = apReq.getTicket();
        final EncryptionType encType = this.tgtTicket.getEncryptedEncPart().getEType();
        final EncryptionKey tgsKey = this.getTgsEntry().getKeys().get(encType);
        if (this.tgtTicket.getTktvno() != 5) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BADVERSION);
        }
        final EncTicketPart encPart = EncryptionUtil.unseal(this.tgtTicket.getEncryptedEncPart(), tgsKey, KeyUsage.KDC_REP_TICKET, EncTicketPart.class);
        this.tgtTicket.setEncPart(encPart);
        EncryptionKey encKey = null;
        encKey = this.tgtTicket.getEncPart().getKey();
        if (encKey == null) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_NOKEY);
        }
        final Authenticator authenticator = EncryptionUtil.unseal(apReq.getEncryptedAuthenticator(), encKey, KeyUsage.TGS_REQ_AUTH, Authenticator.class);
        if (!authenticator.getCname().equals(this.tgtTicket.getEncPart().getCname())) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BADMATCH);
        }
        final HostAddresses hostAddresses = this.tgtTicket.getEncPart().getClientAddresses();
        if (hostAddresses == null || hostAddresses.isEmpty()) {
            if (!this.getKdcContext().getConfig().isEmptyAddressesAllowed()) {
                throw new KrbException(KrbErrorCode.KRB_AP_ERR_BADADDR);
            }
        }
        else if (!hostAddresses.contains(this.getClientAddress())) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BADADDR);
        }
        final PrincipalName serverPrincipal = this.tgtTicket.getSname();
        serverPrincipal.setRealm(this.tgtTicket.getRealm());
        final PrincipalName clientPrincipal = authenticator.getCname();
        clientPrincipal.setRealm(authenticator.getCrealm());
        final KrbIdentity clientEntry = this.getEntry(clientPrincipal.getName());
        this.setClientEntry(clientEntry);
        if (!authenticator.getCtime().isInClockSkew(this.getKdcContext().getConfig().getAllowableClockSkew() * 1000L)) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_SKEW);
        }
        final KerberosTime now = KerberosTime.now();
        KerberosTime startTime = this.tgtTicket.getEncPart().getStartTime();
        if (startTime == null) {
            startTime = this.tgtTicket.getEncPart().getAuthTime();
        }
        if (!startTime.lessThan(now)) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_TKT_NYV);
        }
        final KerberosTime endTime = this.tgtTicket.getEncPart().getEndTime();
        if (!endTime.greaterThan(now)) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_TKT_EXPIRED);
        }
        apReq.getApOptions().setFlag(ApOption.MUTUAL_REQUIRED);
        this.setTgtSessionKey(this.tgtTicket.getEncPart().getKey());
    }
    
    @Override
    protected void makeReply() throws KrbException {
        final Ticket ticket = this.getTicket();
        final TgsRep reply = new TgsRep();
        if (this.getClientEntry() == null) {
            reply.setCname(ticket.getEncPart().getCname());
        }
        else {
            reply.setCname(this.getClientEntry().getPrincipal());
        }
        reply.setCrealm(this.getKdcContext().getKdcRealm());
        reply.setTicket(ticket);
        final EncKdcRepPart encKdcRepPart = this.makeEncKdcRepPart();
        reply.setEncPart(encKdcRepPart);
        EncryptionKey sessionKey;
        if (this.getToken() != null) {
            sessionKey = this.getSessionKey();
        }
        else {
            sessionKey = this.getTgtSessionKey();
        }
        final EncryptedData encryptedData = EncryptionUtil.seal(encKdcRepPart, sessionKey, KeyUsage.TGS_REP_ENCPART_SESSKEY);
        reply.setEncryptedEncPart(encryptedData);
        this.setReply(reply);
    }
    
    private EncKdcRepPart makeEncKdcRepPart() {
        final KdcReq request = this.getKdcReq();
        final Ticket ticket = this.getTicket();
        final EncKdcRepPart encKdcRepPart = new EncTgsRepPart();
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
    
    public ByteBuffer getRequestBody() throws KrbException {
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TgsRequest.class);
    }
}
