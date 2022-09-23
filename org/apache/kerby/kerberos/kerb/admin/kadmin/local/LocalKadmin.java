// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.local;

import org.apache.kerby.kerberos.kerb.identity.KrbIdentity;
import org.apache.kerby.kerberos.kerb.identity.backend.IdentityBackend;
import org.apache.kerby.kerberos.kerb.identity.backend.BackendConfig;
import org.apache.kerby.kerberos.kerb.server.KdcConfig;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.Kadmin;

public interface LocalKadmin extends Kadmin
{
    void checkBuiltinPrincipals() throws KrbException;
    
    void createBuiltinPrincipals() throws KrbException;
    
    void deleteBuiltinPrincipals() throws KrbException;
    
    KdcConfig getKdcConfig();
    
    BackendConfig getBackendConfig();
    
    IdentityBackend getIdentityBackend();
    
    KrbIdentity getPrincipal(final String p0) throws KrbException;
    
    int size() throws KrbException;
}
