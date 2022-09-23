// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminClient;

public abstract class RemoteCommand
{
    AdminClient adminClient;
    
    public RemoteCommand(final AdminClient adminClient) {
        this.adminClient = adminClient;
    }
    
    public abstract void execute(final String p0) throws KrbException;
}
