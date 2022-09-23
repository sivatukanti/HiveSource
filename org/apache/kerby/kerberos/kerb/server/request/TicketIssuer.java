// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.request;

import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.type.base.TransitedEncodingType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.type.base.NameType;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcClientRequest;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcOptions;
import org.apache.kerby.kerberos.kerb.type.base.TransitedEncoding;
import org.apache.kerby.kerberos.kerb.server.KdcConfig;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcOption;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlag;
import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlags;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.ticket.EncTicketPart;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.slf4j.Logger;

public abstract class TicketIssuer
{
    private static final Logger LOG;
    private final KdcRequest kdcRequest;
    
    public TicketIssuer(final KdcRequest kdcRequest) {
        this.kdcRequest = kdcRequest;
    }
    
    protected KdcRequest getKdcRequest() {
        return this.kdcRequest;
    }
    
    public Ticket issueTicket() throws KrbException {
        final KdcReq request = this.kdcRequest.getKdcReq();
        final Ticket issuedTicket = new Ticket();
        final PrincipalName serverPrincipal = this.getServerPrincipal();
        issuedTicket.setSname(serverPrincipal);
        final String serverRealm = request.getReqBody().getRealm();
        issuedTicket.setRealm(serverRealm);
        final EncTicketPart encTicketPart = this.makeEncTicketPart();
        final EncryptionKey encryptionKey = this.getTicketEncryptionKey();
        final EncryptedData encryptedData = EncryptionUtil.seal(encTicketPart, encryptionKey, KeyUsage.KDC_REP_TICKET);
        issuedTicket.setEncryptedEncPart(encryptedData);
        issuedTicket.setEncPart(encTicketPart);
        return issuedTicket;
    }
    
    public EncTicketPart makeEncTicketPart() throws KrbException {
        final KdcReq request = this.kdcRequest.getKdcReq();
        final EncTicketPart encTicketPart = new EncTicketPart();
        final KdcConfig config = this.kdcRequest.getKdcContext().getConfig();
        final TicketFlags ticketFlags = new TicketFlags();
        encTicketPart.setFlags(ticketFlags);
        ticketFlags.setFlag(TicketFlag.INITIAL);
        if (this.kdcRequest.isPreAuthenticated()) {
            ticketFlags.setFlag(TicketFlag.PRE_AUTH);
        }
        if (request.getReqBody().getKdcOptions().isFlagSet(KdcOption.FORWARDABLE)) {
            if (!config.isForwardableAllowed()) {
                TicketIssuer.LOG.warn("Forward is not allowed.");
                throw new KrbException(KrbErrorCode.KDC_ERR_POLICY);
            }
            ticketFlags.setFlag(TicketFlag.FORWARDABLE);
        }
        if (request.getReqBody().getKdcOptions().isFlagSet(KdcOption.PROXIABLE)) {
            if (!config.isProxiableAllowed()) {
                TicketIssuer.LOG.warn("Proxy is not allowed.");
                throw new KrbException(KrbErrorCode.KDC_ERR_POLICY);
            }
            ticketFlags.setFlag(TicketFlag.PROXIABLE);
        }
        if (request.getReqBody().getKdcOptions().isFlagSet(KdcOption.ALLOW_POSTDATE)) {
            if (!config.isPostdatedAllowed()) {
                TicketIssuer.LOG.warn("Post date is not allowed.");
                throw new KrbException(KrbErrorCode.KDC_ERR_POLICY);
            }
            ticketFlags.setFlag(TicketFlag.MAY_POSTDATE);
        }
        final EncryptionKey sessionKey = EncryptionHandler.random2Key(this.kdcRequest.getEncryptionType());
        encTicketPart.setKey(sessionKey);
        encTicketPart.setCname(this.getclientPrincipal());
        encTicketPart.setCrealm(request.getReqBody().getRealm());
        final TransitedEncoding transEnc = this.getTransitedEncoding();
        encTicketPart.setTransited(transEnc);
        final KdcOptions kdcOptions = request.getReqBody().getKdcOptions();
        final KerberosTime now = KerberosTime.now();
        encTicketPart.setAuthTime(now);
        KerberosTime krbStartTime = request.getReqBody().getFrom();
        if (krbStartTime == null || krbStartTime.lessThan(now) || krbStartTime.isInClockSkew(config.getAllowableClockSkew())) {
            krbStartTime = now;
        }
        if (krbStartTime.greaterThan(now) && !krbStartTime.isInClockSkew(config.getAllowableClockSkew()) && !kdcOptions.isFlagSet(KdcOption.POSTDATED)) {
            throw new KrbException(KrbErrorCode.KDC_ERR_CANNOT_POSTDATE);
        }
        if (kdcOptions.isFlagSet(KdcOption.POSTDATED)) {
            if (!config.isPostdatedAllowed()) {
                throw new KrbException(KrbErrorCode.KDC_ERR_POLICY);
            }
            ticketFlags.setFlag(TicketFlag.POSTDATED);
            encTicketPart.setStartTime(krbStartTime);
        }
        KerberosTime krbEndTime = request.getReqBody().getTill();
        if (krbEndTime == null || krbEndTime.getTime() == 0L) {
            krbEndTime = krbStartTime.extend(config.getMaximumTicketLifetime() * 1000L);
        }
        else if (krbStartTime.greaterThan(krbEndTime)) {
            throw new KrbException(KrbErrorCode.KDC_ERR_NEVER_VALID);
        }
        encTicketPart.setEndTime(krbEndTime);
        final long ticketLifeTime = Math.abs(krbEndTime.diff(krbStartTime));
        if (ticketLifeTime < config.getMinimumTicketLifetime()) {
            throw new KrbException(KrbErrorCode.KDC_ERR_NEVER_VALID);
        }
        KerberosTime krbRtime = request.getReqBody().getRtime();
        if (kdcOptions.isFlagSet(KdcOption.RENEWABLE_OK)) {
            kdcOptions.setFlag(KdcOption.RENEWABLE);
        }
        if (kdcOptions.isFlagSet(KdcOption.RENEWABLE)) {
            if (!config.isRenewableAllowed()) {
                throw new KrbException(KrbErrorCode.KDC_ERR_POLICY);
            }
            ticketFlags.setFlag(TicketFlag.RENEWABLE);
            if (krbRtime == null || krbRtime.getTime() == 0L) {
                krbRtime = krbEndTime;
            }
            KerberosTime allowedMaximumRenewableTime = krbStartTime;
            allowedMaximumRenewableTime = allowedMaximumRenewableTime.extend(config.getMaximumRenewableLifetime() * 1000L);
            if (krbRtime.greaterThan(allowedMaximumRenewableTime)) {
                krbRtime = allowedMaximumRenewableTime;
            }
            encTicketPart.setRenewtill(krbRtime);
        }
        final HostAddresses hostAddresses = request.getReqBody().getAddresses();
        if (hostAddresses == null || hostAddresses.isEmpty()) {
            if (!config.isEmptyAddressesAllowed()) {
                throw new KrbException(KrbErrorCode.KDC_ERR_POLICY);
            }
        }
        else {
            encTicketPart.setClientAddresses(hostAddresses);
        }
        final AuthorizationData authData = this.makeAuthorizationData(this.kdcRequest, encTicketPart);
        if (authData != null) {
            encTicketPart.setAuthorizationData(authData);
        }
        return encTicketPart;
    }
    
    protected AuthorizationData makeAuthorizationData(final KdcRequest kdcRequest, final EncTicketPart encTicketPart) throws KrbException {
        final KdcClientRequest clientRequest = new KdcClientRequest();
        clientRequest.setAnonymous(kdcRequest.isAnonymous());
        clientRequest.setClientAddress(kdcRequest.getClientAddress());
        clientRequest.setClientKey(kdcRequest.getClientKey());
        clientRequest.setClientPrincipal(kdcRequest.getClientPrincipal());
        clientRequest.setEncryptionType(kdcRequest.getEncryptionType());
        clientRequest.setPkinit(kdcRequest.isPkinit());
        clientRequest.setPreAuthenticated(kdcRequest.isPreAuthenticated());
        clientRequest.setToken(kdcRequest.getToken());
        clientRequest.setToken(kdcRequest.isToken());
        return this.getKdcContext().getIdentityService().getIdentityAuthorizationData(clientRequest, encTicketPart);
    }
    
    protected KdcContext getKdcContext() {
        return this.kdcRequest.getKdcContext();
    }
    
    protected KdcReq getKdcReq() {
        return this.kdcRequest.getKdcReq();
    }
    
    protected PrincipalName getclientPrincipal() {
        if (this.kdcRequest.isToken()) {
            return new PrincipalName(this.kdcRequest.getToken().getSubject());
        }
        final PrincipalName principalName = this.getKdcReq().getReqBody().getCname();
        if (this.getKdcRequest().isAnonymous()) {
            principalName.setNameType(NameType.NT_WELLKNOWN);
        }
        return principalName;
    }
    
    protected PrincipalName getServerPrincipal() {
        return this.getKdcReq().getReqBody().getSname();
    }
    
    protected EncryptionType getTicketEncryptionType() throws KrbException {
        final EncryptionType encryptionType = this.kdcRequest.getEncryptionType();
        return encryptionType;
    }
    
    protected EncryptionKey getTicketEncryptionKey() throws KrbException {
        final EncryptionType encryptionType = this.getTicketEncryptionType();
        final EncryptionKey serverKey = this.kdcRequest.getServerEntry().getKeys().get(encryptionType);
        return serverKey;
    }
    
    protected TransitedEncoding getTransitedEncoding() {
        final TransitedEncoding transEnc = new TransitedEncoding();
        transEnc.setTrType(TransitedEncodingType.DOMAIN_X500_COMPRESS);
        final byte[] empty = new byte[0];
        transEnc.setContents(empty);
        return transEnc;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TicketIssuer.class);
    }
}
