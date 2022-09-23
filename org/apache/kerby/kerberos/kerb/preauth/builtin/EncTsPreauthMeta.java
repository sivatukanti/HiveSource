// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.builtin;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;

public class EncTsPreauthMeta implements PreauthPluginMeta
{
    private static final String NAME = "encrypted_timestamp";
    private static final int VERSION = 1;
    private static final PaDataType[] PA_TYPES;
    
    @Override
    public String getName() {
        return "encrypted_timestamp";
    }
    
    @Override
    public int getVersion() {
        return 1;
    }
    
    @Override
    public PaDataType[] getPaTypes() {
        return EncTsPreauthMeta.PA_TYPES.clone();
    }
    
    static {
        PA_TYPES = new PaDataType[] { PaDataType.ENC_TIMESTAMP };
    }
}
