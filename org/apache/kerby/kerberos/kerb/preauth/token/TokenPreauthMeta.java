// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.token;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;

public class TokenPreauthMeta implements PreauthPluginMeta
{
    private static final String NAME = "TokenPreauth";
    private static final int VERSION = 1;
    private static final PaDataType[] PA_TYPES;
    
    @Override
    public String getName() {
        return "TokenPreauth";
    }
    
    @Override
    public int getVersion() {
        return 1;
    }
    
    @Override
    public PaDataType[] getPaTypes() {
        return TokenPreauthMeta.PA_TYPES.clone();
    }
    
    static {
        PA_TYPES = new PaDataType[] { PaDataType.TOKEN_CHALLENGE, PaDataType.TOKEN_REQUEST };
    }
}
