// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.kdc.EncTgsRepPart;
import org.apache.kerby.kerberos.kerb.type.kdc.TgsRep;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcRep;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.kdc.TgsReq;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.KrbOption;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.slf4j.Logger;

public class TgsRequest extends KdcRequest
{
    private static final Logger LOG;
    
    public TgsRequest(final KrbContext context) {
        super(context);
    }
    
    @Override
    public PrincipalName getClientPrincipal() {
        return null;
    }
    
    @Override
    public EncryptionKey getClientKey() throws KrbException {
        return null;
    }
    
    public EncryptionKey getSessionKey() {
        return null;
    }
    
    @Override
    public void process() throws KrbException {
        final String serverPrincipal = this.getRequestOptions().getStringOption(KrbOption.SERVER_PRINCIPAL);
        if (serverPrincipal == null) {
            TgsRequest.LOG.warn("Server principal is null.");
        }
        this.setServerPrincipal(new PrincipalName(serverPrincipal));
        super.process();
        final TgsReq tgsReq = new TgsReq();
        final KdcReqBody tgsReqBody = this.getReqBody(null);
        tgsReq.setReqBody(tgsReqBody);
        tgsReq.setPaData(this.getPreauthContext().getOutputPaData());
        this.setKdcReq(tgsReq);
    }
    
    @Override
    public void processResponse(final KdcRep kdcRep) throws KrbException {
        this.setKdcRep(kdcRep);
        final TgsRep tgsRep = (TgsRep)this.getKdcRep();
        EncTgsRepPart encTgsRepPart;
        try {
            encTgsRepPart = EncryptionUtil.unseal(tgsRep.getEncryptedEncPart(), this.getSessionKey(), KeyUsage.TGS_REP_ENCPART_SESSKEY, EncTgsRepPart.class);
        }
        catch (KrbException e) {
            encTgsRepPart = EncryptionUtil.unseal(tgsRep.getEncryptedEncPart(), this.getSessionKey(), KeyUsage.TGS_REP_ENCPART_SUBKEY, EncTgsRepPart.class);
        }
        tgsRep.setEncPart(encTgsRepPart);
        if (this.getChosenNonce() != encTgsRepPart.getNonce()) {
            TgsRequest.LOG.error("Nonce " + this.getChosenNonce() + "didn't match " + encTgsRepPart.getNonce());
            throw new KrbException("Nonce didn't match");
        }
    }
    
    public SgtTicket getSgt() {
        final SgtTicket serviceTkt = new SgtTicket(this.getKdcRep().getTicket(), (EncTgsRepPart)this.getKdcRep().getEncPart());
        return serviceTkt;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TgsRequest.class);
    }
}
