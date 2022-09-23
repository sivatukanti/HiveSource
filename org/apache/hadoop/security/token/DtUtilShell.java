// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import java.util.Iterator;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.File;
import java.util.ArrayList;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.apache.hadoop.tools.CommandShell;

public class DtUtilShell extends CommandShell
{
    private static final Logger LOG;
    private static final String FORMAT_SUBSTRING = "[-format (java|protobuf)]";
    public static final String DT_USAGE = "hadoop dtutil [-keytab <keytab_file> -principal <principal_name>] subcommand (help|print|get|edit|append|cancel|remove|renew) [-format (java|protobuf)] [-alias <alias>] filename...";
    private static final String HELP = "help";
    private static final String KEYTAB = "-keytab";
    private static final String PRINCIPAL = "-principal";
    private static final String PRINT = "print";
    private static final String GET = "get";
    private static final String EDIT = "edit";
    private static final String APPEND = "append";
    private static final String CANCEL = "cancel";
    private static final String REMOVE = "remove";
    private static final String RENEW = "renew";
    private static final String RENEWER = "-renewer";
    private static final String SERVICE = "-service";
    private static final String ALIAS = "-alias";
    private static final String FORMAT = "-format";
    private String keytab;
    private String principal;
    private Text alias;
    private Text service;
    private String renewer;
    private String format;
    private ArrayList<File> tokenFiles;
    private File firstFile;
    
    public DtUtilShell() {
        this.keytab = null;
        this.principal = null;
        this.alias = null;
        this.service = null;
        this.renewer = null;
        this.format = "protobuf";
        this.tokenFiles = null;
        this.firstFile = null;
    }
    
    private String[] maybeDoLoginFromKeytabAndPrincipal(final String[] args) throws IOException {
        final ArrayList<String> savedArgs = new ArrayList<String>(args.length);
        for (int i = 0; i < args.length; ++i) {
            final String current = args[i];
            if (current.equals("-principal")) {
                this.principal = args[++i];
            }
            else if (current.equals("-keytab")) {
                this.keytab = args[++i];
            }
            else {
                savedArgs.add(current);
            }
        }
        final int newSize = savedArgs.size();
        if (newSize != args.length) {
            if (this.principal != null && this.keytab != null) {
                UserGroupInformation.loginUserFromKeytab(this.principal, this.keytab);
            }
            else {
                DtUtilShell.LOG.warn("-principal and -keytab not both specified!  Kerberos login not attempted.");
            }
            return savedArgs.toArray(new String[newSize]);
        }
        return args;
    }
    
    @Override
    protected int init(String[] args) throws Exception {
        if (0 == args.length) {
            return 1;
        }
        this.tokenFiles = new ArrayList<File>();
        args = this.maybeDoLoginFromKeytabAndPrincipal(args);
        for (int i = 0; i < args.length; ++i) {
            if (i == 0) {
                final String command = args[0];
                if (command.equals("help")) {
                    return 1;
                }
                if (command.equals("print")) {
                    this.setSubCommand(new Print());
                }
                else if (command.equals("get")) {
                    this.setSubCommand(new Get(args[++i]));
                }
                else if (command.equals("edit")) {
                    this.setSubCommand(new Edit());
                }
                else if (command.equals("append")) {
                    this.setSubCommand(new Append());
                }
                else if (command.equals("cancel")) {
                    this.setSubCommand(new Remove(true));
                }
                else if (command.equals("remove")) {
                    this.setSubCommand(new Remove(false));
                }
                else if (command.equals("renew")) {
                    this.setSubCommand(new Renew());
                }
            }
            else if (args[i].equals("-alias")) {
                this.alias = new Text(args[++i]);
            }
            else if (args[i].equals("-service")) {
                this.service = new Text(args[++i]);
            }
            else if (args[i].equals("-renewer")) {
                this.renewer = args[++i];
            }
            else if (args[i].equals("-format")) {
                this.format = args[++i];
                if (!this.format.equals("java") && !this.format.equals("protobuf")) {
                    DtUtilShell.LOG.error("-format must be 'java' or 'protobuf' not '" + this.format + "'");
                    return 1;
                }
            }
            else {
                while (i < args.length) {
                    final File f = new File(args[i]);
                    if (f.exists()) {
                        this.tokenFiles.add(f);
                    }
                    if (this.firstFile == null) {
                        this.firstFile = f;
                    }
                    ++i;
                }
                if (this.tokenFiles.size() == 0 && this.firstFile == null) {
                    DtUtilShell.LOG.error("Must provide a filename to all commands.");
                    return 1;
                }
            }
        }
        return 0;
    }
    
    @Override
    public String getCommandUsage() {
        return String.format("%n%s%n   %s%n   %s%n   %s%n   %s%n   %s%n   %s%n   %s%n%n", "hadoop dtutil [-keytab <keytab_file> -principal <principal_name>] subcommand (help|print|get|edit|append|cancel|remove|renew) [-format (java|protobuf)] [-alias <alias>] filename...", new Print().getUsage(), new Get().getUsage(), new Edit().getUsage(), new Append().getUsage(), new Remove(true).getUsage(), new Remove(false).getUsage(), new Renew().getUsage());
    }
    
    public static void main(final String[] args) throws Exception {
        System.exit(ToolRunner.run(new Configuration(), new DtUtilShell(), args));
    }
    
    static {
        LOG = LoggerFactory.getLogger(DtUtilShell.class);
    }
    
    private class Print extends SubCommand
    {
        public static final String PRINT_USAGE = "dtutil print [-alias <alias>] filename...";
        
        @Override
        public void execute() throws Exception {
            for (final File tokenFile : DtUtilShell.this.tokenFiles) {
                DtFileOperations.printTokenFile(tokenFile, DtUtilShell.this.alias, DtUtilShell.this.getConf(), DtUtilShell.this.getOut());
            }
        }
        
        @Override
        public String getUsage() {
            return "dtutil print [-alias <alias>] filename...";
        }
    }
    
    private class Get extends SubCommand
    {
        public static final String GET_USAGE = "dtutil get URL [-service <scheme>] [-format (java|protobuf)][-alias <alias>] [-renewer <renewer>] filename";
        private static final String PREFIX_HTTP = "http://";
        private static final String PREFIX_HTTPS = "https://";
        private String url;
        
        public Get() {
            this.url = null;
        }
        
        public Get(final String arg) {
            this.url = null;
            this.url = arg;
        }
        
        public boolean isGenericUrl() {
            return this.url.startsWith("http://") || this.url.startsWith("https://");
        }
        
        @Override
        public boolean validate() {
            if (DtUtilShell.this.service != null && !this.isGenericUrl()) {
                DtUtilShell.LOG.error("Only provide -service with http/https URL.");
                return false;
            }
            if (DtUtilShell.this.service == null && this.isGenericUrl()) {
                DtUtilShell.LOG.error("Must provide -service with http/https URL.");
                return false;
            }
            if (this.url.indexOf("://") == -1) {
                DtUtilShell.LOG.error("URL does not contain a service specification: " + this.url);
                return false;
            }
            return true;
        }
        
        @Override
        public void execute() throws Exception {
            DtFileOperations.getTokenFile(DtUtilShell.this.firstFile, DtUtilShell.this.format, DtUtilShell.this.alias, DtUtilShell.this.service, this.url, DtUtilShell.this.renewer, DtUtilShell.this.getConf());
        }
        
        @Override
        public String getUsage() {
            return "dtutil get URL [-service <scheme>] [-format (java|protobuf)][-alias <alias>] [-renewer <renewer>] filename";
        }
    }
    
    private class Edit extends SubCommand
    {
        public static final String EDIT_USAGE = "dtutil edit -service <service> -alias <alias> [-format (java|protobuf)]filename...";
        
        @Override
        public boolean validate() {
            if (DtUtilShell.this.service == null) {
                DtUtilShell.LOG.error("must pass -service field with dtutil edit command");
                return false;
            }
            if (DtUtilShell.this.alias == null) {
                DtUtilShell.LOG.error("must pass -alias field with dtutil edit command");
                return false;
            }
            return true;
        }
        
        @Override
        public void execute() throws Exception {
            for (final File tokenFile : DtUtilShell.this.tokenFiles) {
                DtFileOperations.aliasTokenFile(tokenFile, DtUtilShell.this.format, DtUtilShell.this.alias, DtUtilShell.this.service, DtUtilShell.this.getConf());
            }
        }
        
        @Override
        public String getUsage() {
            return "dtutil edit -service <service> -alias <alias> [-format (java|protobuf)]filename...";
        }
    }
    
    private class Append extends SubCommand
    {
        public static final String APPEND_USAGE = "dtutil append [-format (java|protobuf)]filename...";
        
        @Override
        public void execute() throws Exception {
            DtFileOperations.appendTokenFiles(DtUtilShell.this.tokenFiles, DtUtilShell.this.format, DtUtilShell.this.getConf());
        }
        
        @Override
        public String getUsage() {
            return "dtutil append [-format (java|protobuf)]filename...";
        }
    }
    
    private class Remove extends SubCommand
    {
        public static final String REMOVE_USAGE = "dtutil remove -alias <alias> [-format (java|protobuf)] filename...";
        public static final String CANCEL_USAGE = "dtutil cancel -alias <alias> [-format (java|protobuf)] filename...";
        private boolean cancel;
        
        public Remove(final boolean arg) {
            this.cancel = false;
            this.cancel = arg;
        }
        
        @Override
        public boolean validate() {
            if (DtUtilShell.this.alias == null) {
                DtUtilShell.LOG.error("-alias flag is not optional for remove or cancel");
                return false;
            }
            return true;
        }
        
        @Override
        public void execute() throws Exception {
            for (final File tokenFile : DtUtilShell.this.tokenFiles) {
                DtFileOperations.removeTokenFromFile(this.cancel, tokenFile, DtUtilShell.this.format, DtUtilShell.this.alias, DtUtilShell.this.getConf());
            }
        }
        
        @Override
        public String getUsage() {
            if (this.cancel) {
                return "dtutil cancel -alias <alias> [-format (java|protobuf)] filename...";
            }
            return "dtutil remove -alias <alias> [-format (java|protobuf)] filename...";
        }
    }
    
    private class Renew extends SubCommand
    {
        public static final String RENEW_USAGE = "dtutil renew -alias <alias> filename...";
        
        @Override
        public boolean validate() {
            if (DtUtilShell.this.alias == null) {
                DtUtilShell.LOG.error("-alias flag is not optional for renew");
                return false;
            }
            return true;
        }
        
        @Override
        public void execute() throws Exception {
            for (final File tokenFile : DtUtilShell.this.tokenFiles) {
                DtFileOperations.renewTokenFile(tokenFile, DtUtilShell.this.format, DtUtilShell.this.alias, DtUtilShell.this.getConf());
            }
        }
        
        @Override
        public String getUsage() {
            return "dtutil renew -alias <alias> filename...";
        }
    }
}
