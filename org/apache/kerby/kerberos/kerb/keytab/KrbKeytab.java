// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.keytab;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.util.List;

public interface KrbKeytab
{
    List<PrincipalName> getPrincipals();
    
    void addKeytabEntries(final List<KeytabEntry> p0);
    
    void removeKeytabEntries(final PrincipalName p0);
    
    void removeKeytabEntries(final PrincipalName p0, final int p1);
    
    void removeKeytabEntry(final KeytabEntry p0);
    
    List<KeytabEntry> getKeytabEntries(final PrincipalName p0);
    
    EncryptionKey getKey(final PrincipalName p0, final EncryptionType p1);
    
    void load(final File p0) throws IOException;
    
    void load(final InputStream p0) throws IOException;
    
    void addEntry(final KeytabEntry p0);
    
    void store(final File p0) throws IOException;
    
    void store(final OutputStream p0) throws IOException;
}
