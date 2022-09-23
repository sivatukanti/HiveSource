// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminClient;

public class RemotePrintUsageCommand extends RemoteCommand
{
    private static final String LISTPRINCSUSAGE = "Usage: list_principals [expression]\n\t'expression' is a shell-style glob expression that can contain the wild-card characters ?, *, and [].\n\tExample:\n\t\tlist_principals [expression]\n";
    
    public RemotePrintUsageCommand() {
        super(null);
    }
    
    @Override
    public void execute(final String input) throws KrbException {
        if (input.startsWith("listprincs")) {
            System.out.println("Usage: list_principals [expression]\n\t'expression' is a shell-style glob expression that can contain the wild-card characters ?, *, and [].\n\tExample:\n\t\tlist_principals [expression]\n");
        }
    }
}
