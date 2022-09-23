// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.request;

import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.kerberos.kerb.type.ticket.EncTicketPart;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.ap.Authenticator;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.ap.ApOption;
import org.apache.kerby.kerberos.kerb.type.ap.ApOptions;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;
import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class ApRequest
{
    private PrincipalName clientPrincipal;
    private SgtTicket sgtTicket;
    private ApReq apReq;
    
    public ApRequest(final PrincipalName clientPrincipal, final SgtTicket sgtTicket) {
        this.clientPrincipal = clientPrincipal;
        this.sgtTicket = sgtTicket;
    }
    
    public ApReq getApReq() throws KrbException {
        if (this.apReq == null) {
            this.apReq = this.makeApReq();
        }
        return this.apReq;
    }
    
    public void setApReq(final ApReq apReq) {
        this.apReq = apReq;
    }
    
    private ApReq makeApReq() throws KrbException {
        final ApReq apReq = new ApReq();
        final Authenticator authenticator = this.makeAuthenticator();
        final EncryptionKey sessionKey = this.sgtTicket.getSessionKey();
        final EncryptedData authData = EncryptionUtil.seal(authenticator, sessionKey, KeyUsage.AP_REQ_AUTH);
        apReq.setEncryptedAuthenticator(authData);
        apReq.setAuthenticator(authenticator);
        apReq.setTicket(this.sgtTicket.getTicket());
        final ApOptions apOptions = new ApOptions();
        apOptions.setFlag(ApOption.USE_SESSION_KEY);
        apReq.setApOptions(apOptions);
        return apReq;
    }
    
    private Authenticator makeAuthenticator() throws KrbException {
        final Authenticator authenticator = new Authenticator();
        authenticator.setAuthenticatorVno(5);
        authenticator.setCname(this.clientPrincipal);
        authenticator.setCrealm(this.sgtTicket.getRealm());
        authenticator.setCtime(KerberosTime.now());
        authenticator.setCusec(0);
        authenticator.setSubKey(this.sgtTicket.getSessionKey());
        return authenticator;
    }
    
    public static void validate(final EncryptionKey encKey, final ApReq apReq) throws KrbException {
        final Ticket ticket = apReq.getTicket();
        if (encKey == null) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_NOKEY);
        }
        final EncTicketPart encPart = EncryptionUtil.unseal(ticket.getEncryptedEncPart(), encKey, KeyUsage.KDC_REP_TICKET, EncTicketPart.class);
        ticket.setEncPart(encPart);
        unsealAuthenticator(encPart.getKey(), apReq);
        final Authenticator authenticator = apReq.getAuthenticator();
        if (!authenticator.getCname().equals(ticket.getEncPart().getCname())) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BADMATCH);
        }
        if (!authenticator.getCrealm().equals(ticket.getEncPart().getCrealm())) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BADMATCH);
        }
    }
    
    public static void unsealAuthenticator(final EncryptionKey encKey, final ApReq apReq) throws KrbException {
        final EncryptedData authData = apReq.getEncryptedAuthenticator();
        final Authenticator authenticator = EncryptionUtil.unseal(authData, encKey, KeyUsage.AP_REQ_AUTH, Authenticator.class);
        apReq.setAuthenticator(authenticator);
    }
}
