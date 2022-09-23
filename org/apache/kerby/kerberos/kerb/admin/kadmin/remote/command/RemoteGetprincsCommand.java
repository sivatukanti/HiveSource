// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command;

import org.apache.kerby.kerberos.kerb.KrbException;
import java.util.List;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminClient;

public class RemoteGetprincsCommand extends RemoteCommand
{
    private static final String USAGE = "Usage: list_principals [expression]\n\t'expression' is a shell-style glob expression that can contain the wild-card characters ?, *, and [].\tExample:\n\t\tlist_principals [expression]\n";
    
    public RemoteGetprincsCommand(final AdminClient adminClient) {
        super(adminClient);
    }
    
    @Override
    public void execute(final String input) throws KrbException {
        final String[] items = input.split("\\s+");
        if (items.length > 2) {
            System.err.println("Usage: list_principals [expression]\n\t'expression' is a shell-style glob expression that can contain the wild-card characters ?, *, and [].\tExample:\n\t\tlist_principals [expression]\n");
            return;
        }
        List<String> principalLists = null;
        if (items.length == 1) {
            principalLists = this.adminClient.requestGetprincs();
        }
        else {
            final String exp = items[1];
            principalLists = this.adminClient.requestGetprincsWithExp(exp);
        }
        if (principalLists.size() == 0 || (principalLists.size() == 1 && principalLists.get(0).isEmpty())) {
            return;
        }
        System.out.println("Principals are listed:");
        for (int i = 0; i < principalLists.size(); ++i) {
            System.out.println(principalLists.get(i));
        }
    }
}
