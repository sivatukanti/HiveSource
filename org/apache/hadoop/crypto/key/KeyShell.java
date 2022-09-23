// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.util.ToolRunner;
import java.util.HashMap;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.tools.CommandShell;

public class KeyShell extends CommandShell
{
    private static final String USAGE_PREFIX = "Usage: hadoop key [generic options]\n";
    private static final String COMMANDS = "   [-help]\n   [create <keyname> [-cipher <cipher>] [-size <size>]\n                     [-description <description>]\n                     [-attr <attribute=value>]\n                     [-provider <provider>] [-strict]\n                     [-help]]\n   [roll <keyname> [-provider <provider>] [-strict] [-help]]\n   [delete <keyname> [-provider <provider>] [-strict] [-f] [-help]]\n   [list [-provider <provider>] [-strict] [-metadata] [-help]]\n   [invalidateCache <keyname> [-provider <provider>] [-help]]\n";
    private static final String LIST_METADATA = "keyShell.list.metadata";
    @VisibleForTesting
    public static final String NO_VALID_PROVIDERS = "There are no valid (non-transient) providers configured.\nNo action has been taken. Use the -provider option to specify\na provider. If you want to use a transient provider then you\nMUST use the -provider argument.";
    private boolean interactive;
    private boolean strict;
    private boolean userSuppliedProvider;
    
    public KeyShell() {
        this.interactive = true;
        this.strict = false;
        this.userSuppliedProvider = false;
    }
    
    @Override
    protected int init(final String[] args) throws IOException {
        final KeyProvider.Options options = KeyProvider.options(this.getConf());
        final Map<String, String> attributes = new HashMap<String, String>();
        for (int i = 0; i < args.length; ++i) {
            final boolean moreTokens = i < args.length - 1;
            if (args[i].equals("create")) {
                String keyName = "-help";
                if (moreTokens) {
                    keyName = args[++i];
                }
                this.setSubCommand(new CreateCommand(keyName, options));
                if ("-help".equals(keyName)) {
                    return 1;
                }
            }
            else if (args[i].equals("delete")) {
                String keyName = "-help";
                if (moreTokens) {
                    keyName = args[++i];
                }
                this.setSubCommand(new DeleteCommand(keyName));
                if ("-help".equals(keyName)) {
                    return 1;
                }
            }
            else if (args[i].equals("roll")) {
                String keyName = "-help";
                if (moreTokens) {
                    keyName = args[++i];
                }
                this.setSubCommand(new RollCommand(keyName));
                if ("-help".equals(keyName)) {
                    return 1;
                }
            }
            else if ("list".equals(args[i])) {
                this.setSubCommand(new ListCommand());
            }
            else if ("invalidateCache".equals(args[i])) {
                String keyName = "-help";
                if (moreTokens) {
                    keyName = args[++i];
                }
                this.setSubCommand(new InvalidateCacheCommand(keyName));
                if ("-help".equals(keyName)) {
                    return 1;
                }
            }
            else if ("-size".equals(args[i]) && moreTokens) {
                options.setBitLength(Integer.parseInt(args[++i]));
            }
            else if ("-cipher".equals(args[i]) && moreTokens) {
                options.setCipher(args[++i]);
            }
            else if ("-description".equals(args[i]) && moreTokens) {
                options.setDescription(args[++i]);
            }
            else if ("-attr".equals(args[i]) && moreTokens) {
                final String[] attrval = args[++i].split("=", 2);
                final String attr = attrval[0].trim();
                final String val = attrval[1].trim();
                if (attr.isEmpty() || val.isEmpty()) {
                    this.getOut().println("\nAttributes must be in attribute=value form, or quoted\nlike \"attribute = value\"\n");
                    return 1;
                }
                if (attributes.containsKey(attr)) {
                    this.getOut().println("\nEach attribute must correspond to only one value:\natttribute \"" + attr + "\" was repeated\n");
                    return 1;
                }
                attributes.put(attr, val);
            }
            else if ("-provider".equals(args[i]) && moreTokens) {
                this.userSuppliedProvider = true;
                this.getConf().set("hadoop.security.key.provider.path", args[++i]);
            }
            else if ("-metadata".equals(args[i])) {
                this.getConf().setBoolean("keyShell.list.metadata", true);
            }
            else if ("-f".equals(args[i]) || "-force".equals(args[i])) {
                this.interactive = false;
            }
            else if (args[i].equals("-strict")) {
                this.strict = true;
            }
            else {
                if ("-help".equals(args[i])) {
                    return 1;
                }
                ToolRunner.printGenericCommandUsage(this.getErr());
                return 1;
            }
        }
        if (!attributes.isEmpty()) {
            options.setAttributes(attributes);
        }
        return 0;
    }
    
    @Override
    public String getCommandUsage() {
        final StringBuffer sbuf = new StringBuffer("Usage: hadoop key [generic options]\n   [-help]\n   [create <keyname> [-cipher <cipher>] [-size <size>]\n                     [-description <description>]\n                     [-attr <attribute=value>]\n                     [-provider <provider>] [-strict]\n                     [-help]]\n   [roll <keyname> [-provider <provider>] [-strict] [-help]]\n   [delete <keyname> [-provider <provider>] [-strict] [-f] [-help]]\n   [list [-provider <provider>] [-strict] [-metadata] [-help]]\n   [invalidateCache <keyname> [-provider <provider>] [-help]]\n");
        final String banner = StringUtils.repeat("=", 66);
        sbuf.append(banner + "\n");
        sbuf.append("create <keyname> [-cipher <cipher>] [-size <size>]\n                     [-description <description>]\n                     [-attr <attribute=value>]\n                     [-provider <provider>] [-strict]\n                     [-help]:\n\nThe create subcommand creates a new key for the name specified\nby the <keyname> argument within the provider specified by the\n-provider argument. You may specify a cipher with the -cipher\nargument. The default cipher is currently \"AES/CTR/NoPadding\".\nThe default keysize is 128. You may specify the requested key\nlength using the -size argument. Arbitrary attribute=value\nstyle attributes may be specified using the -attr argument.\n-attr may be specified multiple times, once per attribute.\n\n");
        sbuf.append(banner + "\n");
        sbuf.append("roll <keyname> [-provider <provider>] [-strict] [-help]:\n\nThe roll subcommand creates a new version for the specified key\nwithin the provider indicated using the -provider argument.\nIf -strict is supplied, fail immediately if the provider requires\na password and none is given.\n");
        sbuf.append(banner + "\n");
        sbuf.append("delete <keyname> [-provider <provider>] [-strict] [-f] [-help]:\n\nThe delete subcommand deletes all versions of the key\nspecified by the <keyname> argument from within the\nprovider specified by -provider. The command asks for\nuser confirmation unless -f is specified. If -strict is\nsupplied, fail immediately if the provider requires a\npassword and none is given.\n");
        sbuf.append(banner + "\n");
        sbuf.append("list [-provider <provider>] [-strict] [-metadata] [-help]:\n\nThe list subcommand displays the keynames contained within\na particular provider as configured in core-site.xml or\nspecified with the -provider argument. -metadata displays\nthe metadata. If -strict is supplied, fail immediately if\nthe provider requires a password and none is given.\n");
        sbuf.append(banner + "\n");
        sbuf.append("invalidateCache <keyname> [-provider <provider>] [-help]:\n\nThe invalidateCache subcommand invalidates the cached key versions\nof the specified key, on the provider indicated using the -provider argument.\n\n");
        return sbuf.toString();
    }
    
    @Override
    protected void printException(final Exception e) {
        this.getErr().println("Executing command failed with the following exception: " + this.prettifyException(e));
    }
    
    private String prettifyException(final Exception e) {
        return e.getClass().getSimpleName() + ": " + e.getLocalizedMessage().split("\n")[0];
    }
    
    public static void main(final String[] args) throws Exception {
        final int res = ToolRunner.run(new Configuration(), new KeyShell(), args);
        System.exit(res);
    }
    
    private abstract class Command extends SubCommand
    {
        protected KeyProvider provider;
        
        private Command() {
            this.provider = null;
        }
        
        protected KeyProvider getKeyProvider() {
            KeyProvider prov = null;
            try {
                final List<KeyProvider> providers = KeyProviderFactory.getProviders(KeyShell.this.getConf());
                if (KeyShell.this.userSuppliedProvider) {
                    prov = providers.get(0);
                }
                else {
                    for (final KeyProvider p : providers) {
                        if (!p.isTransient()) {
                            prov = p;
                            break;
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace(KeyShell.this.getErr());
            }
            if (prov == null) {
                KeyShell.this.getOut().println("There are no valid (non-transient) providers configured.\nNo action has been taken. Use the -provider option to specify\na provider. If you want to use a transient provider then you\nMUST use the -provider argument.");
            }
            return prov;
        }
        
        protected void printProviderWritten() {
            KeyShell.this.getOut().println(this.provider + " has been updated.");
        }
        
        protected void warnIfTransientProvider() {
            if (this.provider.isTransient()) {
                KeyShell.this.getOut().println("WARNING: you are modifying a transient provider.");
            }
        }
        
        @Override
        public abstract void execute() throws Exception;
        
        @Override
        public abstract String getUsage();
    }
    
    private class ListCommand extends Command
    {
        public static final String USAGE = "list [-provider <provider>] [-strict] [-metadata] [-help]";
        public static final String DESC = "The list subcommand displays the keynames contained within\na particular provider as configured in core-site.xml or\nspecified with the -provider argument. -metadata displays\nthe metadata. If -strict is supplied, fail immediately if\nthe provider requires a password and none is given.";
        private boolean metadata;
        
        private ListCommand() {
            this.metadata = false;
        }
        
        @Override
        public boolean validate() {
            boolean rc = true;
            this.provider = this.getKeyProvider();
            if (this.provider == null) {
                rc = false;
            }
            this.metadata = KeyShell.this.getConf().getBoolean("keyShell.list.metadata", false);
            return rc;
        }
        
        @Override
        public void execute() throws IOException {
            try {
                final List<String> keys = this.provider.getKeys();
                KeyShell.this.getOut().println("Listing keys for KeyProvider: " + this.provider);
                if (this.metadata) {
                    final KeyProvider.Metadata[] meta = this.provider.getKeysMetadata((String[])keys.toArray(new String[keys.size()]));
                    for (int i = 0; i < meta.length; ++i) {
                        KeyShell.this.getOut().println(keys.get(i) + " : " + meta[i]);
                    }
                }
                else {
                    for (final String keyName : keys) {
                        KeyShell.this.getOut().println(keyName);
                    }
                }
            }
            catch (IOException e) {
                KeyShell.this.getOut().println("Cannot list keys for KeyProvider: " + this.provider);
                throw e;
            }
        }
        
        @Override
        public String getUsage() {
            return "list [-provider <provider>] [-strict] [-metadata] [-help]:\n\nThe list subcommand displays the keynames contained within\na particular provider as configured in core-site.xml or\nspecified with the -provider argument. -metadata displays\nthe metadata. If -strict is supplied, fail immediately if\nthe provider requires a password and none is given.";
        }
    }
    
    private class RollCommand extends Command
    {
        public static final String USAGE = "roll <keyname> [-provider <provider>] [-strict] [-help]";
        public static final String DESC = "The roll subcommand creates a new version for the specified key\nwithin the provider indicated using the -provider argument.\nIf -strict is supplied, fail immediately if the provider requires\na password and none is given.";
        private String keyName;
        
        public RollCommand(final String keyName) {
            this.keyName = null;
            this.keyName = keyName;
        }
        
        @Override
        public boolean validate() {
            boolean rc = true;
            this.provider = this.getKeyProvider();
            if (this.provider == null) {
                rc = false;
            }
            if (this.keyName == null) {
                KeyShell.this.getOut().println("Please provide a <keyname>.\nSee the usage description by using -help.");
                rc = false;
            }
            return rc;
        }
        
        @Override
        public void execute() throws NoSuchAlgorithmException, IOException {
            try {
                this.warnIfTransientProvider();
                KeyShell.this.getOut().println("Rolling key version from KeyProvider: " + this.provider + "\n  for key name: " + this.keyName);
                try {
                    this.provider.rollNewVersion(this.keyName);
                    this.provider.flush();
                    KeyShell.this.getOut().println(this.keyName + " has been successfully rolled.");
                    this.printProviderWritten();
                }
                catch (NoSuchAlgorithmException e) {
                    KeyShell.this.getOut().println("Cannot roll key: " + this.keyName + " within KeyProvider: " + this.provider + ".");
                    throw e;
                }
            }
            catch (IOException e2) {
                KeyShell.this.getOut().println("Cannot roll key: " + this.keyName + " within KeyProvider: " + this.provider + ".");
                throw e2;
            }
        }
        
        @Override
        public String getUsage() {
            return "roll <keyname> [-provider <provider>] [-strict] [-help]:\n\nThe roll subcommand creates a new version for the specified key\nwithin the provider indicated using the -provider argument.\nIf -strict is supplied, fail immediately if the provider requires\na password and none is given.";
        }
    }
    
    private class DeleteCommand extends Command
    {
        public static final String USAGE = "delete <keyname> [-provider <provider>] [-strict] [-f] [-help]";
        public static final String DESC = "The delete subcommand deletes all versions of the key\nspecified by the <keyname> argument from within the\nprovider specified by -provider. The command asks for\nuser confirmation unless -f is specified. If -strict is\nsupplied, fail immediately if the provider requires a\npassword and none is given.";
        private String keyName;
        private boolean cont;
        
        public DeleteCommand(final String keyName) {
            this.keyName = null;
            this.cont = true;
            this.keyName = keyName;
        }
        
        @Override
        public boolean validate() {
            this.provider = this.getKeyProvider();
            if (this.provider == null) {
                return false;
            }
            if (this.keyName == null) {
                KeyShell.this.getOut().println("There is no keyName specified. Please specify a <keyname>. See the usage description with -help.");
                return false;
            }
            if (KeyShell.this.interactive) {
                try {
                    if (!(this.cont = ToolRunner.confirmPrompt("You are about to DELETE all versions of  key " + this.keyName + " from KeyProvider " + this.provider + ". Continue? "))) {
                        KeyShell.this.getOut().println(this.keyName + " has not been deleted.");
                    }
                    return this.cont;
                }
                catch (IOException e) {
                    KeyShell.this.getOut().println(this.keyName + " will not be deleted. " + KeyShell.this.prettifyException(e));
                }
            }
            return true;
        }
        
        @Override
        public void execute() throws IOException {
            this.warnIfTransientProvider();
            KeyShell.this.getOut().println("Deleting key: " + this.keyName + " from KeyProvider: " + this.provider);
            if (this.cont) {
                try {
                    this.provider.deleteKey(this.keyName);
                    this.provider.flush();
                    KeyShell.this.getOut().println(this.keyName + " has been successfully deleted.");
                    this.printProviderWritten();
                }
                catch (IOException e) {
                    KeyShell.this.getOut().println(this.keyName + " has not been deleted.");
                    throw e;
                }
            }
        }
        
        @Override
        public String getUsage() {
            return "delete <keyname> [-provider <provider>] [-strict] [-f] [-help]:\n\nThe delete subcommand deletes all versions of the key\nspecified by the <keyname> argument from within the\nprovider specified by -provider. The command asks for\nuser confirmation unless -f is specified. If -strict is\nsupplied, fail immediately if the provider requires a\npassword and none is given.";
        }
    }
    
    private class CreateCommand extends Command
    {
        public static final String USAGE = "create <keyname> [-cipher <cipher>] [-size <size>]\n                     [-description <description>]\n                     [-attr <attribute=value>]\n                     [-provider <provider>] [-strict]\n                     [-help]";
        public static final String DESC = "The create subcommand creates a new key for the name specified\nby the <keyname> argument within the provider specified by the\n-provider argument. You may specify a cipher with the -cipher\nargument. The default cipher is currently \"AES/CTR/NoPadding\".\nThe default keysize is 128. You may specify the requested key\nlength using the -size argument. Arbitrary attribute=value\nstyle attributes may be specified using the -attr argument.\n-attr may be specified multiple times, once per attribute.\n";
        private final String keyName;
        private final KeyProvider.Options options;
        
        public CreateCommand(final String keyName, final KeyProvider.Options options) {
            this.keyName = keyName;
            this.options = options;
        }
        
        @Override
        public boolean validate() {
            boolean rc = true;
            try {
                this.provider = this.getKeyProvider();
                if (this.provider == null) {
                    rc = false;
                }
                else if (this.provider.needsPassword()) {
                    if (KeyShell.this.strict) {
                        KeyShell.this.getOut().println(this.provider.noPasswordError());
                        rc = false;
                    }
                    else {
                        KeyShell.this.getOut().println(this.provider.noPasswordWarning());
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace(KeyShell.this.getErr());
            }
            if (this.keyName == null) {
                KeyShell.this.getOut().println("Please provide a <keyname>.  See the usage description with -help.");
                rc = false;
            }
            return rc;
        }
        
        @Override
        public void execute() throws IOException, NoSuchAlgorithmException {
            this.warnIfTransientProvider();
            try {
                this.provider.createKey(this.keyName, this.options);
                this.provider.flush();
                KeyShell.this.getOut().println(this.keyName + " has been successfully created with options " + this.options.toString() + ".");
                this.printProviderWritten();
            }
            catch (InvalidParameterException e) {
                KeyShell.this.getOut().println(this.keyName + " has not been created.");
                throw e;
            }
            catch (IOException e2) {
                KeyShell.this.getOut().println(this.keyName + " has not been created.");
                throw e2;
            }
            catch (NoSuchAlgorithmException e3) {
                KeyShell.this.getOut().println(this.keyName + " has not been created.");
                throw e3;
            }
        }
        
        @Override
        public String getUsage() {
            return "create <keyname> [-cipher <cipher>] [-size <size>]\n                     [-description <description>]\n                     [-attr <attribute=value>]\n                     [-provider <provider>] [-strict]\n                     [-help]:\n\nThe create subcommand creates a new key for the name specified\nby the <keyname> argument within the provider specified by the\n-provider argument. You may specify a cipher with the -cipher\nargument. The default cipher is currently \"AES/CTR/NoPadding\".\nThe default keysize is 128. You may specify the requested key\nlength using the -size argument. Arbitrary attribute=value\nstyle attributes may be specified using the -attr argument.\n-attr may be specified multiple times, once per attribute.\n";
        }
    }
    
    private class InvalidateCacheCommand extends Command
    {
        public static final String USAGE = "invalidateCache <keyname> [-provider <provider>] [-help]";
        public static final String DESC = "The invalidateCache subcommand invalidates the cached key versions\nof the specified key, on the provider indicated using the -provider argument.\n";
        private String keyName;
        
        InvalidateCacheCommand(final String keyName) {
            this.keyName = null;
            this.keyName = keyName;
        }
        
        @Override
        public boolean validate() {
            boolean rc = true;
            this.provider = this.getKeyProvider();
            if (this.provider == null) {
                KeyShell.this.getOut().println("Invalid provider.");
                rc = false;
            }
            if (this.keyName == null) {
                KeyShell.this.getOut().println("Please provide a <keyname>.\nSee the usage description by using -help.");
                rc = false;
            }
            return rc;
        }
        
        @Override
        public void execute() throws NoSuchAlgorithmException, IOException {
            try {
                this.warnIfTransientProvider();
                KeyShell.this.getOut().println("Invalidating cache on KeyProvider: " + this.provider + "\n  for key name: " + this.keyName);
                this.provider.invalidateCache(this.keyName);
                KeyShell.this.getOut().println("Cached keyversions of " + this.keyName + " has been successfully invalidated.");
                this.printProviderWritten();
            }
            catch (IOException e) {
                KeyShell.this.getOut().println("Cannot invalidate cache for key: " + this.keyName + " within KeyProvider: " + this.provider + ".");
                throw e;
            }
        }
        
        @Override
        public String getUsage() {
            return "invalidateCache <keyname> [-provider <provider>] [-help]:\n\nThe invalidateCache subcommand invalidates the cached key versions\nof the specified key, on the provider indicated using the -provider argument.\n";
        }
    }
}
