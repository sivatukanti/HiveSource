// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;

public class PkinitPreauthMeta implements PreauthPluginMeta
{
    private static final String NAME = "PKINIT";
    private static final int VERSION = 1;
    private static final PaDataType[] PA_TYPES;
    
    @Override
    public String getName() {
        return "PKINIT";
    }
    
    @Override
    public int getVersion() {
        return 1;
    }
    
    @Override
    public PaDataType[] getPaTypes() {
        return PkinitPreauthMeta.PA_TYPES.clone();
    }
    
    static {
        PA_TYPES = new PaDataType[] { PaDataType.PK_AS_REQ, PaDataType.PK_AS_REP };
    }
}
