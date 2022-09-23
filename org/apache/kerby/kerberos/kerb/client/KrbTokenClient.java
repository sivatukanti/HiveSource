// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.KOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import org.apache.kerby.kerberos.kerb.type.base.KrbToken;
import java.io.File;
import org.apache.kerby.kerberos.kerb.KrbException;

public class KrbTokenClient extends KrbClientBase
{
    public KrbTokenClient() throws KrbException {
    }
    
    public KrbTokenClient(final KrbConfig krbConfig) {
        super(krbConfig);
    }
    
    public KrbTokenClient(final File confDir) throws KrbException {
        super(confDir);
    }
    
    public KrbTokenClient(final KrbClient krbClient) {
        super(krbClient);
    }
    
    public TgtTicket requestTgt(final KrbToken token, final String armorCache) throws KrbException {
        if (!token.isIdToken()) {
            throw new IllegalArgumentException("Identity token is expected");
        }
        final KOptions requestOptions = new KOptions();
        requestOptions.add(TokenOption.USER_ID_TOKEN, token);
        requestOptions.add(KrbOption.ARMOR_CACHE, armorCache);
        return this.requestTgt(requestOptions);
    }
    
    public TgtTicket requestTgt(final KrbToken token, final TgtTicket tgt) throws KrbException {
        if (!token.isIdToken()) {
            throw new IllegalArgumentException("Identity token is expected");
        }
        final KOptions requestOptions = new KOptions();
        requestOptions.add(TokenOption.USER_ID_TOKEN, token);
        requestOptions.add(KrbOption.TGT, tgt);
        return this.requestTgt(requestOptions);
    }
    
    public SgtTicket requestSgt(final KrbToken token, final String serverPrincipal, final String armorCache) throws KrbException {
        if (!token.isAcToken()) {
            throw new IllegalArgumentException("Access token is expected");
        }
        final KOptions requestOptions = new KOptions();
        requestOptions.add(TokenOption.USER_AC_TOKEN, token);
        requestOptions.add(KrbOption.ARMOR_CACHE, armorCache);
        requestOptions.add(KrbOption.SERVER_PRINCIPAL, serverPrincipal);
        return this.requestSgt(requestOptions);
    }
    
    public SgtTicket requestSgt(final KrbToken token, final String serverPrincipal, final TgtTicket tgt) throws KrbException {
        if (!token.isAcToken()) {
            throw new IllegalArgumentException("Access token is expected");
        }
        final KOptions requestOptions = new KOptions();
        requestOptions.add(TokenOption.USER_AC_TOKEN, token);
        requestOptions.add(KrbOption.TGT, tgt);
        requestOptions.add(KrbOption.SERVER_PRINCIPAL, serverPrincipal);
        return this.requestSgt(requestOptions);
    }
}
