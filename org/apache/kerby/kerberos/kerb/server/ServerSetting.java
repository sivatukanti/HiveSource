// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.apache.kerby.kerberos.kerb.identity.backend.BackendConfig;

public interface ServerSetting
{
    String getKdcRealm();
    
    KdcConfig getKdcConfig();
    
    BackendConfig getBackendConfig();
}
