// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.ccache;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public interface KrbCredentialCache
{
    PrincipalName getPrimaryPrincipal();
    
    void setPrimaryPrincipal(final PrincipalName p0);
    
    int getVersion();
    
    void setVersion(final int p0);
    
    List<Credential> getCredentials();
    
    void addCredential(final Credential p0);
    
    void addCredentials(final List<Credential> p0);
    
    void removeCredentials(final List<Credential> p0);
    
    void removeCredential(final Credential p0);
    
    void load(final File p0) throws IOException;
    
    void load(final InputStream p0) throws IOException;
    
    void store(final File p0) throws IOException;
    
    void store(final OutputStream p0) throws IOException;
}
