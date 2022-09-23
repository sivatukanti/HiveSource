// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.impl;

import org.apache.kerby.kerberos.kerb.identity.backend.IdentityBackend;
import org.apache.kerby.kerberos.kerb.server.KdcSetting;
import org.apache.kerby.kerberos.kerb.KrbException;

public interface InternalKdcServer
{
    void init() throws KrbException;
    
    void start() throws KrbException;
    
    void stop() throws KrbException;
    
    KdcSetting getSetting();
    
    IdentityBackend getIdentityBackend();
}
