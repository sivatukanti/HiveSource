// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.ccache.CredentialCache;
import java.io.File;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;
import org.apache.kerby.kerberos.kerb.type.base.HostAddress;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.kdc.EncAsRepPart;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.client.TokenOption;
import org.apache.kerby.kerberos.kerb.common.KrbUtil;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.PkinitOption;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcRep;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.kerberos.kerb.type.kdc.AsReq;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.KrbContext;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class AsRequest extends KdcRequest
{
    private PrincipalName clientPrincipal;
    private EncryptionKey clientKey;
    
    public AsRequest(final KrbContext context) {
        super(context);
        this.setServerPrincipal(this.makeTgsPrincipal());
    }
    
    @Override
    public PrincipalName getClientPrincipal() {
        return this.clientPrincipal;
    }
    
    public void setClientPrincipal(final PrincipalName clientPrincipal) {
        this.clientPrincipal = clientPrincipal;
    }
    
    public void setClientKey(final EncryptionKey clientKey) {
        this.clientKey = clientKey;
    }
    
    @Override
    public EncryptionKey getClientKey() throws KrbException {
        return this.clientKey;
    }
    
    @Override
    public void process() throws KrbException {
        super.process();
        final KdcReqBody body = this.getReqBody(null);
        final AsReq asReq = new AsReq();
        asReq.setReqBody(body);
        asReq.setPaData(this.getPreauthContext().getOutputPaData());
        this.setKdcReq(asReq);
    }
    
    @Override
    public void processResponse(final KdcRep kdcRep) throws KrbException {
        this.setKdcRep(kdcRep);
        final PrincipalName clientPrincipal = this.getKdcRep().getCname();
        final String clientRealm = this.getKdcRep().getCrealm();
        clientPrincipal.setRealm(clientRealm);
        if ((!this.getRequestOptions().contains(PkinitOption.USE_ANONYMOUS) || !KrbUtil.pricipalCompareIgnoreRealm(clientPrincipal, this.getClientPrincipal())) && !this.getRequestOptions().contains(TokenOption.USER_ID_TOKEN) && !clientPrincipal.equals(this.getClientPrincipal())) {
            throw new KrbException(KrbErrorCode.KDC_ERR_CLIENT_NAME_MISMATCH);
        }
        final byte[] decryptedData = this.decryptWithClientKey(this.getKdcRep().getEncryptedEncPart(), KeyUsage.AS_REP_ENCPART);
        if ((decryptedData[0] & 0x1F) == 0x1A) {
            --decryptedData[0];
        }
        final EncKdcRepPart encKdcRepPart = new EncAsRepPart();
        try {
            encKdcRepPart.decode(decryptedData);
        }
        catch (IOException e) {
            throw new KrbException("Failed to decode EncAsRepPart", e);
        }
        this.getKdcRep().setEncPart(encKdcRepPart);
        if (this.getChosenNonce() != encKdcRepPart.getNonce()) {
            throw new KrbException("Nonce didn't match");
        }
        final PrincipalName returnedServerPrincipal = encKdcRepPart.getSname();
        returnedServerPrincipal.setRealm(encKdcRepPart.getSrealm());
        final PrincipalName requestedServerPrincipal = this.getServerPrincipal();
        if (requestedServerPrincipal.getRealm() == null) {
            requestedServerPrincipal.setRealm(this.getContext().getKrbSetting().getKdcRealm());
        }
        if (!returnedServerPrincipal.equals(requestedServerPrincipal)) {
            throw new KrbException(KrbErrorCode.KDC_ERR_SERVER_NOMATCH);
        }
        final HostAddresses hostAddresses = this.getHostAddresses();
        if (hostAddresses != null) {
            final List<HostAddress> requestHosts = hostAddresses.getElements();
            if (!requestHosts.isEmpty()) {
                final List<HostAddress> responseHosts = encKdcRepPart.getCaddr().getElements();
                for (final HostAddress h : requestHosts) {
                    if (!responseHosts.contains(h)) {
                        throw new KrbException("Unexpected client host");
                    }
                }
            }
        }
    }
    
    public TgtTicket getTicket() {
        final TgtTicket tgtTicket = new TgtTicket(this.getKdcRep().getTicket(), (EncAsRepPart)this.getKdcRep().getEncPart(), this.getKdcRep().getCname());
        return tgtTicket;
    }
    
    private PrincipalName makeTgsPrincipal() {
        return KrbUtil.makeTgsPrincipal(this.getContext().getKrbSetting().getKdcRealm());
    }
    
    protected CredentialCache resolveCredCache(final File ccacheFile) throws IOException {
        final CredentialCache cc = new CredentialCache();
        cc.load(ccacheFile);
        return cc;
    }
}
