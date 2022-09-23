// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.client.preauth.PreauthContext;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcRep;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcOption;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.PkinitOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReqBody;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.kerberos.kerb.type.kdc.AsReq;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.client.KrbContext;

public class AsRequestWithCert extends AsRequest
{
    public static final String ANONYMOUS_PRINCIPAL = "ANONYMOUS@WELLKNOWN:ANONYMOUS";
    
    public AsRequestWithCert(final KrbContext context) {
        super(context);
        this.setAllowedPreauth(PaDataType.PK_AS_REQ);
    }
    
    @Override
    public void process() throws KrbException {
        final KdcReqBody body = this.getReqBody(null);
        final AsReq asReq = new AsReq();
        asReq.setReqBody(body);
        this.setKdcReq(asReq);
        this.preauth();
        asReq.setPaData(this.getPreauthContext().getOutputPaData());
        this.setKdcReq(asReq);
    }
    
    @Override
    public KOptions getPreauthOptions() {
        final KOptions results = new KOptions();
        final KOptions krbOptions = this.getRequestOptions();
        results.add(krbOptions.getOption(PkinitOption.X509_CERTIFICATE));
        results.add(krbOptions.getOption(PkinitOption.X509_ANCHORS));
        results.add(krbOptions.getOption(PkinitOption.X509_PRIVATE_KEY));
        results.add(krbOptions.getOption(PkinitOption.X509_IDENTITY));
        results.add(krbOptions.getOption(PkinitOption.USING_RSA));
        if (krbOptions.contains(PkinitOption.USE_ANONYMOUS)) {
            this.getKdcOptions().setFlag(KdcOption.REQUEST_ANONYMOUS);
        }
        return results;
    }
    
    @Override
    public void processResponse(final KdcRep kdcRep) throws KrbException {
        final PreauthContext preauthContext = this.getPreauthContext();
        preauthContext.setInputPaData(kdcRep.getPaData());
        this.preauth();
        super.processResponse(kdcRep);
    }
    
    @Override
    public EncryptionKey getClientKey() throws KrbException {
        return this.getAsKey();
    }
}
