// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin;

import java.util.List;
import java.io.File;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.KrbException;

public interface Kadmin
{
    String getKadminPrincipal();
    
    void addPrincipal(final String p0) throws KrbException;
    
    void addPrincipal(final String p0, final KOptions p1) throws KrbException;
    
    void addPrincipal(final String p0, final String p1) throws KrbException;
    
    void addPrincipal(final String p0, final String p1, final KOptions p2) throws KrbException;
    
    void exportKeytab(final File p0, final String p1) throws KrbException;
    
    void exportKeytab(final File p0, final List<String> p1) throws KrbException;
    
    void exportKeytab(final File p0) throws KrbException;
    
    void removeKeytabEntriesOf(final File p0, final String p1) throws KrbException;
    
    void removeKeytabEntriesOf(final File p0, final String p1, final int p2) throws KrbException;
    
    void removeOldKeytabEntriesOf(final File p0, final String p1) throws KrbException;
    
    void deletePrincipal(final String p0) throws KrbException;
    
    void modifyPrincipal(final String p0, final KOptions p1) throws KrbException;
    
    void renamePrincipal(final String p0, final String p1) throws KrbException;
    
    List<String> getPrincipals() throws KrbException;
    
    List<String> getPrincipals(final String p0) throws KrbException;
    
    void changePassword(final String p0, final String p1) throws KrbException;
    
    void updateKeys(final String p0) throws KrbException;
    
    void release() throws KrbException;
}
