// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.builtin;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;

public class TgtPreauthMeta implements PreauthPluginMeta
{
    private static final String NAME = "TGT_preauth";
    private static final int VERSION = 1;
    private static final PaDataType[] PA_TYPES;
    
    @Override
    public String getName() {
        return "TGT_preauth";
    }
    
    @Override
    public int getVersion() {
        return 1;
    }
    
    @Override
    public PaDataType[] getPaTypes() {
        return TgtPreauthMeta.PA_TYPES.clone();
    }
    
    static {
        PA_TYPES = new PaDataType[] { PaDataType.TGS_REQ };
    }
}
