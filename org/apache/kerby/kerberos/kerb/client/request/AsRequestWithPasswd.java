// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.KrbOption;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.client.KrbContext;

public class AsRequestWithPasswd extends AsRequest
{
    public AsRequestWithPasswd(final KrbContext context) {
        super(context);
        this.setAllowedPreauth(PaDataType.ENC_TIMESTAMP);
    }
    
    public String getPassword() {
        return this.getRequestOptions().getStringOption(KrbOption.USER_PASSWD);
    }
    
    @Override
    public EncryptionKey getClientKey() throws KrbException {
        if (super.getClientKey() == null) {
            final EncryptionKey tmpKey = EncryptionHandler.string2Key(this.getClientPrincipal().getName(), this.getPassword(), this.getChosenEncryptionType());
            this.setClientKey(tmpKey);
        }
        return super.getClientKey();
    }
}
