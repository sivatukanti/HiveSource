// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command;

import org.apache.kerby.kerberos.kerb.KrbException;
import java.io.Console;
import java.util.Scanner;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminClient;

public class RemoteDeletePrincipalCommand extends RemoteCommand
{
    public static final String USAGE = "Usage: delete_principal <principal-name>\n\tExample:\n\t\tdelete_principal alice\n";
    
    public RemoteDeletePrincipalCommand(final AdminClient adminClient) {
        super(adminClient);
    }
    
    @Override
    public void execute(final String input) throws KrbException {
        final String[] items = input.split("\\s+");
        if (items.length < 2) {
            System.err.println("Usage: delete_principal <principal-name>\n\tExample:\n\t\tdelete_principal alice\n");
            return;
        }
        final String principal = items[items.length - 1] + "@" + this.adminClient.getAdminConfig().getAdminRealm();
        final Console console = System.console();
        final String prompt = "Are you sure to delete the principal? (yes/no, YES/NO, y/n, Y/N) ";
        String reply;
        if (console == null) {
            System.out.println("Couldn't get Console instance, maybe you're running this from within an IDE. Use scanner to read password.");
            final Scanner scanner = new Scanner(System.in, "UTF-8");
            reply = this.getReply(scanner, prompt);
        }
        else {
            reply = this.getReply(console, prompt);
        }
        if (reply.equals("yes") || reply.equals("YES") || reply.equals("y") || reply.equals("Y")) {
            this.adminClient.requestDeletePrincipal(principal);
        }
        else if (reply.equals("no") || reply.equals("NO") || reply.equals("n") || reply.equals("N")) {
            System.out.println("Principal \"" + principal + "\"  not deleted.");
        }
        else {
            System.err.println("Unknown request, fail to delete the principal.");
            System.err.println("Usage: delete_principal <principal-name>\n\tExample:\n\t\tdelete_principal alice\n");
        }
    }
    
    private String getReply(final Scanner scanner, final String prompt) {
        System.out.println(prompt);
        return scanner.nextLine().trim();
    }
    
    private String getReply(final Console console, final String prompt) {
        console.printf(prompt, new Object[0]);
        final String line = console.readLine();
        return line;
    }
}
