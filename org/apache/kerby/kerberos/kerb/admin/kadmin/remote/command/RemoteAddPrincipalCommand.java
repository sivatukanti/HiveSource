// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminClient;

public class RemoteAddPrincipalCommand extends RemoteCommand
{
    public static final String USAGE = "Usage: add_principal [options] <principal-name>\n\toptions are:\n\t\t[-randkey|-nokey]\n\t\t[-pw password]\tExample:\n\t\tadd_principal -pw mypassword alice\n";
    
    public RemoteAddPrincipalCommand(final AdminClient adminClient) {
        super(adminClient);
    }
    
    @Override
    public void execute(final String input) throws KrbException {
        final String[] items = input.split("\\s+");
        if (items.length < 2) {
            System.err.println("Usage: add_principal [options] <principal-name>\n\toptions are:\n\t\t[-randkey|-nokey]\n\t\t[-pw password]\tExample:\n\t\tadd_principal -pw mypassword alice\n");
            return;
        }
        final String adminRealm = this.adminClient.getAdminConfig().getAdminRealm();
        final String clientPrincipal = items[items.length - 1] + "@" + adminRealm;
        if (!items[1].startsWith("-")) {
            this.adminClient.requestAddPrincipal(clientPrincipal);
        }
        else if (items[1].startsWith("-nokey")) {
            this.adminClient.requestAddPrincipal(clientPrincipal);
        }
        else if (items[1].startsWith("-pw")) {
            final String password = items[2];
            this.adminClient.requestAddPrincipal(clientPrincipal, password);
        }
        else {
            System.err.println("add_principal command format error.");
            System.err.println("Usage: add_principal [options] <principal-name>\n\toptions are:\n\t\t[-randkey|-nokey]\n\t\t[-pw password]\tExample:\n\t\tadd_principal -pw mypassword alice\n");
        }
    }
}
