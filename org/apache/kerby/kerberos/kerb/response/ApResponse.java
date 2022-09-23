// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.response;

import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.ap.EncAPRepPart;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.request.ApRequest;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.ap.ApRep;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;

public class ApResponse
{
    private ApReq apReq;
    private ApRep apRep;
    EncryptionKey encryptionKey;
    
    public ApResponse(final ApReq apReq, final EncryptionKey encryptionKey) {
        this.apReq = apReq;
        this.encryptionKey = encryptionKey;
    }
    
    public ApRep getApRep() throws KrbException {
        ApRequest.validate(this.encryptionKey, this.apReq);
        if (this.apRep == null) {
            this.apRep = this.makeApRep();
        }
        return this.apRep;
    }
    
    public void setApRep(final ApRep apRep) {
        this.apRep = apRep;
    }
    
    private ApRep makeApRep() throws KrbException {
        final ApRep apRep = new ApRep();
        final EncAPRepPart encAPRepPart = new EncAPRepPart();
        encAPRepPart.setCtime(KerberosTime.now());
        encAPRepPart.setCusec((int)KerberosTime.now().getTimeInSeconds());
        encAPRepPart.setSubkey(this.apReq.getAuthenticator().getSubKey());
        encAPRepPart.setSeqNumber(0);
        apRep.setEncRepPart(encAPRepPart);
        final EncryptedData encPart = EncryptionUtil.seal(encAPRepPart, this.apReq.getAuthenticator().getSubKey(), KeyUsage.AP_REP_ENCPART);
        apRep.setEncryptedEncPart(encPart);
        return apRep;
    }
}
