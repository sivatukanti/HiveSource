// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.KOptions;
import java.io.IOException;
import java.io.File;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.client.KrbOption;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.client.KrbContext;

public class AsRequestWithKeytab extends AsRequest
{
    public AsRequestWithKeytab(final KrbContext context) {
        super(context);
        this.setAllowedPreauth(PaDataType.ENC_TIMESTAMP);
    }
    
    private Keytab getKeytab() {
        File keytabFile = null;
        final KOptions kOptions = this.getRequestOptions();
        if (kOptions.contains(KrbOption.KEYTAB_FILE)) {
            keytabFile = kOptions.getFileOption(KrbOption.KEYTAB_FILE);
        }
        if (kOptions.contains(KrbOption.USE_DFT_KEYTAB)) {
            final String clientKeytabEnv = System.getenv("KRB5_CLIENT_KTNAME");
            final String clientKeytabDft = this.getContext().getConfig().getString("default_client_keytab_name");
            if (clientKeytabEnv != null) {
                keytabFile = new File(clientKeytabEnv);
            }
            else if (clientKeytabDft != null) {
                keytabFile = new File(clientKeytabDft);
            }
            else {
                System.err.println("Default client keytab file not found.");
            }
        }
        Keytab keytab = null;
        try {
            keytab = Keytab.loadKeytab(keytabFile);
        }
        catch (IOException e) {
            System.err.println("Can not load keytab from file" + keytabFile.getAbsolutePath());
        }
        return keytab;
    }
    
    @Override
    public EncryptionKey getClientKey() throws KrbException {
        if (super.getClientKey() == null) {
            final EncryptionKey tmpKey = this.getKeytab().getKey(this.getClientPrincipal(), this.getChosenEncryptionType());
            this.setClientKey(tmpKey);
        }
        return super.getClientKey();
    }
}
