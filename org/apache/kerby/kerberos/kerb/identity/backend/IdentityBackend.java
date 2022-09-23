// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.identity.backend;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.config.Configurable;
import org.apache.kerby.kerberos.kerb.identity.IdentityService;

public interface IdentityBackend extends IdentityService, Configurable
{
    void initialize() throws KrbException;
    
    void start();
    
    void stop() throws KrbException;
    
    void release();
}
