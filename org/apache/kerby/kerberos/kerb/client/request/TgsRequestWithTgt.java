// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.common.CheckSumUtil;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.KrbKdcOption;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.ap.Authenticator;
import org.apache.kerby.kerberos.kerb.type.ap.ApOptions;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;

public class TgsRequestWithTgt extends TgsRequest
{
    private TgtTicket tgt;
    private ApReq apReq;
    
    public TgsRequestWithTgt(final KrbContext context, final TgtTicket tgt) {
        super(context);
        this.tgt = tgt;
        this.setAllowedPreauth(PaDataType.TGS_REQ);
    }
    
    @Override
    public PrincipalName getClientPrincipal() {
        return this.tgt.getClientPrincipal();
    }
    
    @Override
    public EncryptionKey getClientKey() throws KrbException {
        return this.getSessionKey();
    }
    
    @Override
    public EncryptionKey getSessionKey() {
        return this.tgt.getSessionKey();
    }
    
    private ApReq makeApReq() throws KrbException {
        final ApReq apReq = new ApReq();
        final Authenticator authenticator = this.makeAuthenticator();
        final EncryptionKey sessionKey = this.tgt.getSessionKey();
        final EncryptedData authnData = EncryptionUtil.seal(authenticator, sessionKey, KeyUsage.TGS_REQ_AUTH);
        apReq.setEncryptedAuthenticator(authnData);
        apReq.setAuthenticator(authenticator);
        apReq.setTicket(this.tgt.getTicket());
        final ApOptions apOptions = new ApOptions();
        apReq.setApOptions(apOptions);
        return apReq;
    }
    
    public ApReq getApReq() throws KrbException {
        if (this.apReq == null) {
            this.apReq = this.makeApReq();
        }
        return this.apReq;
    }
    
    private Authenticator makeAuthenticator() throws KrbException {
        final Authenticator authenticator = new Authenticator();
        authenticator.setAuthenticatorVno(5);
        authenticator.setCname(this.tgt.getClientPrincipal());
        authenticator.setCrealm(this.tgt.getRealm());
        authenticator.setCtime(KerberosTime.now());
        authenticator.setCusec(0);
        authenticator.setSubKey(this.tgt.getSessionKey());
        KerberosTime renewTill = null;
        if (this.getRequestOptions().contains(KrbKdcOption.RENEW)) {
            renewTill = this.tgt.getEncKdcRepPart().getRenewTill();
        }
        final KdcReqBody reqBody = this.getReqBody(renewTill);
        final CheckSum checksum = CheckSumUtil.seal(reqBody, null, this.tgt.getSessionKey(), KeyUsage.TGS_REQ_AUTH_CKSUM);
        authenticator.setCksum(checksum);
        return authenticator;
    }
}
