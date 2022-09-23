// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.KOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import java.io.File;
import org.apache.kerby.kerberos.kerb.KrbException;

public class KrbPkinitClient extends KrbClientBase
{
    public KrbPkinitClient() throws KrbException {
    }
    
    public KrbPkinitClient(final KrbConfig krbConfig) {
        super(krbConfig);
    }
    
    public KrbPkinitClient(final File confDir) throws KrbException {
        super(confDir);
    }
    
    public KrbPkinitClient(final KrbClient krbClient) {
        super(krbClient);
    }
    
    public TgtTicket requestTgt(final String principal, final String certificate, final String privateKey) throws KrbException {
        final KOptions requestOptions = new KOptions();
        requestOptions.add(KrbOption.CLIENT_PRINCIPAL, principal);
        requestOptions.add(PkinitOption.USE_PKINIT);
        requestOptions.add(PkinitOption.USING_RSA);
        requestOptions.add(PkinitOption.X509_IDENTITY, certificate);
        requestOptions.add(PkinitOption.X509_PRIVATE_KEY, privateKey);
        return this.requestTgt(requestOptions);
    }
    
    public TgtTicket requestTgt() throws KrbException {
        final KOptions requestOptions = new KOptions();
        requestOptions.add(PkinitOption.USE_ANONYMOUS);
        requestOptions.add(KrbOption.CLIENT_PRINCIPAL, "WELLKNOWN/ANONYMOUS");
        return this.requestTgt(requestOptions);
    }
}
