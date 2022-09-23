// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.KOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import java.io.File;
import org.apache.kerby.kerberos.kerb.KrbException;

public class KrbClient extends KrbClientBase
{
    public KrbClient() throws KrbException {
    }
    
    public KrbClient(final KrbConfig krbConfig) {
        super(krbConfig);
    }
    
    public KrbClient(final File confDir) throws KrbException {
        super(confDir);
    }
    
    public TgtTicket requestTgt(final String principal, final String password) throws KrbException {
        final KOptions requestOptions = new KOptions();
        requestOptions.add(KrbOption.CLIENT_PRINCIPAL, principal);
        requestOptions.add(KrbOption.USE_PASSWD, true);
        requestOptions.add(KrbOption.USER_PASSWD, password);
        return this.requestTgt(requestOptions);
    }
    
    public TgtTicket requestTgt(final String principal, final File keytabFile) throws KrbException {
        final KOptions requestOptions = new KOptions();
        requestOptions.add(KrbOption.CLIENT_PRINCIPAL, principal);
        requestOptions.add(KrbOption.USE_KEYTAB, true);
        requestOptions.add(KrbOption.KEYTAB_FILE, keytabFile);
        return this.requestTgt(requestOptions);
    }
}
