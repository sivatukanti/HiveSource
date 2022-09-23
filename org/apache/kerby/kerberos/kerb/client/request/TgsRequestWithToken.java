// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.TokenOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.client.KrbContext;

public class TgsRequestWithToken extends ArmoredTgsRequest
{
    public TgsRequestWithToken(final KrbContext context) throws KrbException {
        super(context);
        this.setAllowedPreauth(PaDataType.TOKEN_REQUEST);
    }
    
    @Override
    public KOptions getPreauthOptions() {
        final KOptions results = super.getPreauthOptions();
        final KOptions krbOptions = this.getRequestOptions();
        results.add(krbOptions.getOption(TokenOption.USE_TOKEN));
        results.add(krbOptions.getOption(TokenOption.USER_AC_TOKEN));
        return results;
    }
    
    @Override
    public PrincipalName getClientPrincipal() {
        final KOption acToken = this.getPreauthOptions().getOption(TokenOption.USER_AC_TOKEN);
        final AuthToken authToken = (AuthToken)acToken.getOptionInfo().getValue();
        return new PrincipalName(authToken.getSubject());
    }
}
