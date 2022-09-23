// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.impl;

import org.apache.kerby.kerberos.kerb.client.request.TgsRequest;
import org.apache.kerby.kerberos.kerb.client.request.TgsRequestWithTgt;
import org.apache.kerby.kerberos.kerb.client.request.TgsRequestWithToken;
import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.kerberos.kerb.client.request.AsRequest;
import org.apache.kerby.kerberos.kerb.type.base.NameType;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.client.request.AsRequestWithToken;
import org.apache.kerby.kerberos.kerb.client.TokenOption;
import org.apache.kerby.kerberos.kerb.client.request.AsRequestWithCert;
import org.apache.kerby.kerberos.kerb.client.PkinitOption;
import org.apache.kerby.kerberos.kerb.client.request.AsRequestWithKeytab;
import org.apache.kerby.kerberos.kerb.client.request.AsRequestWithPasswd;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.KrbOption;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.KrbSetting;
import org.apache.kerby.kerberos.kerb.client.KrbContext;

public abstract class AbstractInternalKrbClient implements InternalKrbClient
{
    private KrbContext context;
    private final KrbSetting krbSetting;
    
    public AbstractInternalKrbClient(final KrbSetting krbSetting) {
        this.krbSetting = krbSetting;
    }
    
    protected KrbContext getContext() {
        return this.context;
    }
    
    @Override
    public KrbSetting getSetting() {
        return this.krbSetting;
    }
    
    @Override
    public void init() throws KrbException {
        (this.context = new KrbContext()).init(this.krbSetting);
    }
    
    @Override
    public TgtTicket requestTgt(final KOptions requestOptions) throws KrbException {
        AsRequest asRequest = null;
        if (requestOptions.contains(KrbOption.USE_PASSWD)) {
            asRequest = new AsRequestWithPasswd(this.context);
        }
        else if (requestOptions.contains(KrbOption.USE_KEYTAB)) {
            asRequest = new AsRequestWithKeytab(this.context);
        }
        else if (requestOptions.contains(PkinitOption.USE_ANONYMOUS)) {
            asRequest = new AsRequestWithCert(this.context);
        }
        else if (requestOptions.contains(PkinitOption.USE_PKINIT)) {
            asRequest = new AsRequestWithCert(this.context);
        }
        else if (requestOptions.contains(TokenOption.USE_TOKEN)) {
            asRequest = new AsRequestWithToken(this.context);
        }
        else if (requestOptions.contains(TokenOption.USER_ID_TOKEN)) {
            asRequest = new AsRequestWithToken(this.context);
        }
        if (asRequest == null) {
            throw new IllegalArgumentException("No valid krb client request option found");
        }
        if (requestOptions.contains(KrbOption.CLIENT_PRINCIPAL)) {
            String principal = requestOptions.getStringOption(KrbOption.CLIENT_PRINCIPAL);
            principal = this.fixPrincipal(principal);
            final PrincipalName principalName = new PrincipalName(principal);
            if (requestOptions.contains(PkinitOption.USE_ANONYMOUS)) {
                principalName.setNameType(NameType.NT_WELLKNOWN);
            }
            asRequest.setClientPrincipal(principalName);
        }
        if (requestOptions.contains(KrbOption.SERVER_PRINCIPAL)) {
            String serverPrincipalName = requestOptions.getStringOption(KrbOption.SERVER_PRINCIPAL);
            serverPrincipalName = this.fixPrincipal(serverPrincipalName);
            final PrincipalName serverPrincipal = new PrincipalName(serverPrincipalName, NameType.NT_PRINCIPAL);
            asRequest.setServerPrincipal(serverPrincipal);
        }
        asRequest.setRequestOptions(requestOptions);
        return this.doRequestTgt(asRequest);
    }
    
    @Override
    public SgtTicket requestSgt(final KOptions requestOptions) throws KrbException {
        TgsRequest tgsRequest = null;
        if (requestOptions.contains(TokenOption.USER_AC_TOKEN)) {
            tgsRequest = new TgsRequestWithToken(this.context);
        }
        else if (requestOptions.contains(KrbOption.USE_TGT)) {
            final KOption kOpt = requestOptions.getOption(KrbOption.USE_TGT);
            tgsRequest = new TgsRequestWithTgt(this.context, (TgtTicket)kOpt.getOptionInfo().getValue());
        }
        if (tgsRequest == null) {
            throw new IllegalArgumentException("No valid krb client request option found");
        }
        final String serverPrincipal = this.fixPrincipal(requestOptions.getStringOption(KrbOption.SERVER_PRINCIPAL));
        tgsRequest.setServerPrincipal(new PrincipalName(serverPrincipal));
        tgsRequest.setRequestOptions(requestOptions);
        return this.doRequestSgt(tgsRequest);
    }
    
    protected abstract TgtTicket doRequestTgt(final AsRequest p0) throws KrbException;
    
    protected abstract SgtTicket doRequestSgt(final TgsRequest p0) throws KrbException;
    
    protected String fixPrincipal(String principal) {
        if (!principal.contains("@")) {
            principal = principal + "@" + this.krbSetting.getKdcRealm();
        }
        return principal;
    }
}
