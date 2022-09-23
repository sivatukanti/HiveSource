// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.alias;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.conf.Configuration;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import org.apache.hadoop.util.ToolRunner;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.tools.CommandShell;

public class CredentialShell extends CommandShell
{
    private static final String USAGE_PREFIX = "Usage: hadoop credential [generic options]\n";
    private static final String COMMANDS = "   [-help]\n   [create <alias> [-value alias-value] [-provider provider-path] [-strict]]\n   [delete <alias> [-f] [-provider provider-path] [-strict]]\n   [list [-provider provider-path] [-strict]]\n";
    @VisibleForTesting
    public static final String NO_VALID_PROVIDERS = "There are no valid (non-transient) providers configured.\nNo action has been taken. Use the -provider option to specify\na provider. If you want to use a transient provider then you\nMUST use the -provider argument.";
    private boolean interactive;
    private boolean strict;
    private boolean userSuppliedProvider;
    private String value;
    private PasswordReader passwordReader;
    
    public CredentialShell() {
        this.interactive = true;
        this.strict = false;
        this.userSuppliedProvider = false;
        this.value = null;
    }
    
    @Override
    protected int init(final String[] args) throws IOException {
        if (0 == args.length) {
            ToolRunner.printGenericCommandUsage(this.getErr());
            return 1;
        }
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("create")) {
                if (i == args.length - 1) {
                    return 1;
                }
                this.setSubCommand(new CreateCommand(args[++i]));
            }
            else if (args[i].equals("delete")) {
                if (i == args.length - 1) {
                    return 1;
                }
                this.setSubCommand(new DeleteCommand(args[++i]));
            }
            else if (args[i].equals("list")) {
                this.setSubCommand(new ListCommand());
            }
            else if (args[i].equals("-provider")) {
                if (i == args.length - 1) {
                    return 1;
                }
                this.userSuppliedProvider = true;
                this.getConf().set("hadoop.security.credential.provider.path", args[++i]);
            }
            else if (args[i].equals("-f") || args[i].equals("-force")) {
                this.interactive = false;
            }
            else if (args[i].equals("-strict")) {
                this.strict = true;
            }
            else if (args[i].equals("-v") || args[i].equals("-value")) {
                this.value = args[++i];
            }
            else {
                if (args[i].equals("-help")) {
                    this.printShellUsage();
                    return 0;
                }
                ToolRunner.printGenericCommandUsage(this.getErr());
                return 1;
            }
        }
        return 0;
    }
    
    @Override
    public String getCommandUsage() {
        final StringBuffer sbuf = new StringBuffer("Usage: hadoop credential [generic options]\n   [-help]\n   [create <alias> [-value alias-value] [-provider provider-path] [-strict]]\n   [delete <alias> [-f] [-provider provider-path] [-strict]]\n   [list [-provider provider-path] [-strict]]\n");
        final String banner = StringUtils.repeat("=", 66);
        sbuf.append(banner + "\n");
        sbuf.append("create <alias> [-value alias-value] [-provider provider-path] [-strict]:\n\nThe create subcommand creates a new credential for the name\nspecified as the <alias> argument within the provider indicated\nthrough the -provider argument. If -strict is supplied, fail\nimmediately if the provider requires a password and none is given.\nIf -value is provided, use that for the value of the credential\ninstead of prompting the user.\n");
        sbuf.append(banner + "\n");
        sbuf.append("delete <alias> [-f] [-provider provider-path] [-strict]:\n\nThe delete subcommand deletes the credential\nspecified as the <alias> argument from within the provider\nindicated through the -provider argument. The command asks for\nconfirmation unless the -f option is specified. If -strict is\nsupplied, fail immediately if the provider requires a password\nand none is given.\n");
        sbuf.append(banner + "\n");
        sbuf.append("list [-provider provider-path] [-strict]:\n\nThe list subcommand displays the aliases contained within \na particular provider - as configured in core-site.xml or\nindicated through the -provider argument. If -strict is supplied,\nfail immediately if the provider requires a password and none is\nprovided.\n");
        return sbuf.toString();
    }
    
    protected char[] promptForCredential() throws IOException {
        final PasswordReader c = this.getPasswordReader();
        if (c == null) {
            throw new IOException("No console available for prompting user.");
        }
        char[] cred = null;
        boolean noMatch;
        do {
            final char[] newPassword1 = c.readPassword("Enter alias password: ");
            final char[] newPassword2 = c.readPassword("Enter alias password again: ");
            noMatch = !Arrays.equals(newPassword1, newPassword2);
            if (noMatch) {
                if (newPassword1 != null) {
                    Arrays.fill(newPassword1, ' ');
                }
                c.format("Passwords don't match. Try again.%n");
            }
            else {
                cred = newPassword1;
            }
            if (newPassword2 != null) {
                Arrays.fill(newPassword2, ' ');
            }
        } while (noMatch);
        return cred;
    }
    
    public PasswordReader getPasswordReader() {
        if (this.passwordReader == null) {
            this.passwordReader = new PasswordReader();
        }
        return this.passwordReader;
    }
    
    public void setPasswordReader(final PasswordReader reader) {
        this.passwordReader = reader;
    }
    
    public static void main(final String[] args) throws Exception {
        final int res = ToolRunner.run(new Configuration(), new CredentialShell(), args);
        System.exit(res);
    }
    
    private abstract class Command extends SubCommand
    {
        protected CredentialProvider provider;
        
        private Command() {
            this.provider = null;
        }
        
        protected CredentialProvider getCredentialProvider() {
            CredentialProvider prov = null;
            try {
                final List<CredentialProvider> providers = CredentialProviderFactory.getProviders(CredentialShell.this.getConf());
                if (CredentialShell.this.userSuppliedProvider) {
                    prov = providers.get(0);
                }
                else {
                    for (final CredentialProvider p : providers) {
                        if (!p.isTransient()) {
                            prov = p;
                            break;
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace(CredentialShell.this.getErr());
            }
            if (prov == null) {
                CredentialShell.this.getOut().println("There are no valid (non-transient) providers configured.\nNo action has been taken. Use the -provider option to specify\na provider. If you want to use a transient provider then you\nMUST use the -provider argument.");
            }
            return prov;
        }
        
        protected void printProviderWritten() {
            CredentialShell.this.getOut().println("Provider " + this.provider.toString() + " was updated.");
        }
        
        protected void warnIfTransientProvider() {
            if (this.provider.isTransient()) {
                CredentialShell.this.getOut().println("WARNING: you are modifying a transient provider.");
            }
        }
        
        protected void doHelp() {
            CredentialShell.this.getOut().println("Usage: hadoop credential [generic options]\n   [-help]\n   [create <alias> [-value alias-value] [-provider provider-path] [-strict]]\n   [delete <alias> [-f] [-provider provider-path] [-strict]]\n   [list [-provider provider-path] [-strict]]\n");
            CommandShell.this.printShellUsage();
        }
        
        @Override
        public abstract void execute() throws Exception;
        
        @Override
        public abstract String getUsage();
    }
    
    private class ListCommand extends Command
    {
        public static final String USAGE = "list [-provider provider-path] [-strict]";
        public static final String DESC = "The list subcommand displays the aliases contained within \na particular provider - as configured in core-site.xml or\nindicated through the -provider argument. If -strict is supplied,\nfail immediately if the provider requires a password and none is\nprovided.";
        
        @Override
        public boolean validate() {
            this.provider = this.getCredentialProvider();
            return this.provider != null;
        }
        
        @Override
        public void execute() throws IOException {
            try {
                final List<String> aliases = this.provider.getAliases();
                CredentialShell.this.getOut().println("Listing aliases for CredentialProvider: " + this.provider.toString());
                for (final String alias : aliases) {
                    CredentialShell.this.getOut().println(alias);
                }
            }
            catch (IOException e) {
                CredentialShell.this.getOut().println("Cannot list aliases for CredentialProvider: " + this.provider.toString() + ": " + e.getMessage());
                throw e;
            }
        }
        
        @Override
        public String getUsage() {
            return "list [-provider provider-path] [-strict]:\n\nThe list subcommand displays the aliases contained within \na particular provider - as configured in core-site.xml or\nindicated through the -provider argument. If -strict is supplied,\nfail immediately if the provider requires a password and none is\nprovided.";
        }
    }
    
    private class DeleteCommand extends Command
    {
        public static final String USAGE = "delete <alias> [-f] [-provider provider-path] [-strict]";
        public static final String DESC = "The delete subcommand deletes the credential\nspecified as the <alias> argument from within the provider\nindicated through the -provider argument. The command asks for\nconfirmation unless the -f option is specified. If -strict is\nsupplied, fail immediately if the provider requires a password\nand none is given.";
        private String alias;
        private boolean cont;
        
        public DeleteCommand(final String alias) {
            this.alias = null;
            this.cont = true;
            this.alias = alias;
        }
        
        @Override
        public boolean validate() {
            if (this.alias == null) {
                CredentialShell.this.getOut().println("There is no alias specified. Please provide themandatory <alias>. See the usage description with -help.");
                return false;
            }
            if (this.alias.equals("-help")) {
                return true;
            }
            this.provider = this.getCredentialProvider();
            if (this.provider == null) {
                return false;
            }
            if (CredentialShell.this.interactive) {
                try {
                    if (!(this.cont = ToolRunner.confirmPrompt("You are about to DELETE the credential " + this.alias + " from CredentialProvider " + this.provider.toString() + ". Continue? "))) {
                        CredentialShell.this.getOut().println("Nothing has been deleted.");
                    }
                    return this.cont;
                }
                catch (IOException e) {
                    CredentialShell.this.getOut().println(this.alias + " will not be deleted.");
                    e.printStackTrace(CredentialShell.this.getErr());
                }
            }
            return true;
        }
        
        @Override
        public void execute() throws IOException {
            if (this.alias.equals("-help")) {
                this.doHelp();
                return;
            }
            this.warnIfTransientProvider();
            CredentialShell.this.getOut().println("Deleting credential: " + this.alias + " from CredentialProvider: " + this.provider.toString());
            if (this.cont) {
                try {
                    this.provider.deleteCredentialEntry(this.alias);
                    CredentialShell.this.getOut().println("Credential " + this.alias + " has been successfully deleted.");
                    this.provider.flush();
                    this.printProviderWritten();
                }
                catch (IOException e) {
                    CredentialShell.this.getOut().println("Credential " + this.alias + " has NOT been deleted.");
                    throw e;
                }
            }
        }
        
        @Override
        public String getUsage() {
            return "delete <alias> [-f] [-provider provider-path] [-strict]:\n\nThe delete subcommand deletes the credential\nspecified as the <alias> argument from within the provider\nindicated through the -provider argument. The command asks for\nconfirmation unless the -f option is specified. If -strict is\nsupplied, fail immediately if the provider requires a password\nand none is given.";
        }
    }
    
    private class CreateCommand extends Command
    {
        public static final String USAGE = "create <alias> [-value alias-value] [-provider provider-path] [-strict]";
        public static final String DESC = "The create subcommand creates a new credential for the name\nspecified as the <alias> argument within the provider indicated\nthrough the -provider argument. If -strict is supplied, fail\nimmediately if the provider requires a password and none is given.\nIf -value is provided, use that for the value of the credential\ninstead of prompting the user.";
        private String alias;
        
        public CreateCommand(final String alias) {
            this.alias = null;
            this.alias = alias;
        }
        
        @Override
        public boolean validate() {
            if (this.alias == null) {
                CredentialShell.this.getOut().println("There is no alias specified. Please provide themandatory <alias>. See the usage description with -help.");
                return false;
            }
            if (this.alias.equals("-help")) {
                return true;
            }
            try {
                this.provider = this.getCredentialProvider();
                if (this.provider == null) {
                    return false;
                }
                if (this.provider.needsPassword()) {
                    if (CredentialShell.this.strict) {
                        CredentialShell.this.getOut().println(this.provider.noPasswordError());
                        return false;
                    }
                    CredentialShell.this.getOut().println(this.provider.noPasswordWarning());
                }
            }
            catch (IOException e) {
                e.printStackTrace(CredentialShell.this.getErr());
            }
            return true;
        }
        
        @Override
        public void execute() throws IOException, NoSuchAlgorithmException {
            if (this.alias.equals("-help")) {
                this.doHelp();
                return;
            }
            this.warnIfTransientProvider();
            try {
                char[] credential = null;
                if (CredentialShell.this.value != null) {
                    credential = CredentialShell.this.value.toCharArray();
                }
                else {
                    credential = CredentialShell.this.promptForCredential();
                }
                this.provider.createCredentialEntry(this.alias, credential);
                this.provider.flush();
                CredentialShell.this.getOut().println(this.alias + " has been successfully created.");
                this.printProviderWritten();
            }
            catch (InvalidParameterException e) {
                CredentialShell.this.getOut().println("Credential " + this.alias + " has NOT been created. " + e.getMessage());
                throw e;
            }
            catch (IOException e2) {
                CredentialShell.this.getOut().println("Credential " + this.alias + " has NOT been created. " + e2.getMessage());
                throw e2;
            }
        }
        
        @Override
        public String getUsage() {
            return "create <alias> [-value alias-value] [-provider provider-path] [-strict]:\n\nThe create subcommand creates a new credential for the name\nspecified as the <alias> argument within the provider indicated\nthrough the -provider argument. If -strict is supplied, fail\nimmediately if the provider requires a password and none is given.\nIf -value is provided, use that for the value of the credential\ninstead of prompting the user.";
        }
    }
    
    public static class PasswordReader
    {
        public char[] readPassword(final String prompt) {
            final Console console = System.console();
            final char[] pass = console.readPassword(prompt, new Object[0]);
            return pass;
        }
        
        public void format(final String message) {
            final Console console = System.console();
            console.format(message, new Object[0]);
        }
    }
}
