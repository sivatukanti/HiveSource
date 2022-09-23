// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.TokenOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.client.KrbContext;

public class AsRequestWithToken extends ArmoredAsRequest
{
    public AsRequestWithToken(final KrbContext context) {
        super(context);
        this.setAllowedPreauth(PaDataType.TOKEN_REQUEST);
    }
    
    @Override
    public KOptions getPreauthOptions() {
        final KOptions results = super.getPreauthOptions();
        final KOptions krbOptions = this.getRequestOptions();
        results.add(krbOptions.getOption(TokenOption.USE_TOKEN));
        results.add(krbOptions.getOption(TokenOption.USER_ID_TOKEN));
        return results;
    }
}
