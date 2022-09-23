// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;

public interface PreauthPluginMeta
{
    String getName();
    
    int getVersion();
    
    PaDataType[] getPaTypes();
}
