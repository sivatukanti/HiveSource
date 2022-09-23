// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.LinkedList;
import org.slf4j.LoggerFactory;
import org.apache.htrace.core.TraceScope;
import java.util.Arrays;
import org.apache.hadoop.tracing.TraceUtils;
import org.apache.htrace.core.Tracer;
import org.apache.hadoop.tools.TableListing;
import org.apache.hadoop.util.StringUtils;
import java.util.Iterator;
import org.apache.hadoop.util.ToolRunner;
import java.util.ArrayList;
import java.io.PrintStream;
import org.apache.hadoop.fs.shell.FsCommand;
import org.apache.hadoop.fs.shell.Command;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.shell.CommandFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Private
public class FsShell extends Configured implements Tool
{
    static final Logger LOG;
    private static final int MAX_LINE_WIDTH = 80;
    private FileSystem fs;
    private Trash trash;
    private Help help;
    protected CommandFactory commandFactory;
    private final String usagePrefix = "Usage: hadoop fs [generic options]";
    static final String SHELL_HTRACE_PREFIX = "fs.shell.htrace.";
    
    public FsShell() {
        this(null);
    }
    
    public FsShell(final Configuration conf) {
        super(conf);
    }
    
    protected FileSystem getFS() throws IOException {
        if (this.fs == null) {
            this.fs = FileSystem.get(this.getConf());
        }
        return this.fs;
    }
    
    protected Trash getTrash() throws IOException {
        if (this.trash == null) {
            this.trash = new Trash(this.getConf());
        }
        return this.trash;
    }
    
    protected Help getHelp() throws IOException {
        if (this.help == null) {
            this.help = new Help();
        }
        return this.help;
    }
    
    protected void init() throws IOException {
        this.getConf().setQuietMode(true);
        UserGroupInformation.setConfiguration(this.getConf());
        if (this.commandFactory == null) {
            (this.commandFactory = new CommandFactory(this.getConf())).addObject(new Help(), "-help");
            this.commandFactory.addObject(new Usage(), "-usage");
            this.registerCommands(this.commandFactory);
        }
    }
    
    protected void registerCommands(final CommandFactory factory) {
        if (this.getClass().equals(FsShell.class)) {
            factory.registerCommands(FsCommand.class);
        }
    }
    
    public Path getCurrentTrashDir() throws IOException {
        return this.getTrash().getCurrentTrashDir();
    }
    
    public Path getCurrentTrashDir(final Path path) throws IOException {
        return this.getTrash().getCurrentTrashDir(path);
    }
    
    protected String getUsagePrefix() {
        return "Usage: hadoop fs [generic options]";
    }
    
    private void printUsage(final PrintStream out) {
        this.printInfo(out, null, false);
    }
    
    private void printUsage(final PrintStream out, final String cmd) {
        this.printInfo(out, cmd, false);
    }
    
    private void printHelp(final PrintStream out) {
        this.printInfo(out, null, true);
    }
    
    private void printHelp(final PrintStream out, final String cmd) {
        this.printInfo(out, cmd, true);
    }
    
    private void printInfo(final PrintStream out, final String cmd, final boolean showHelp) {
        if (cmd != null) {
            final Command instance = this.commandFactory.getInstance("-" + cmd);
            if (instance == null) {
                throw new UnknownCommandException(cmd);
            }
            if (showHelp) {
                this.printInstanceHelp(out, instance);
            }
            else {
                this.printInstanceUsage(out, instance);
            }
        }
        else {
            out.println(this.getUsagePrefix());
            final ArrayList<Command> instances = new ArrayList<Command>();
            for (final String name : this.commandFactory.getNames()) {
                final Command instance2 = this.commandFactory.getInstance(name);
                if (!instance2.isDeprecated()) {
                    out.println("\t[" + instance2.getUsage() + "]");
                    instances.add(instance2);
                }
            }
            if (showHelp) {
                for (final Command instance3 : instances) {
                    out.println();
                    this.printInstanceHelp(out, instance3);
                }
            }
            out.println();
            ToolRunner.printGenericCommandUsage(out);
        }
    }
    
    private void printInstanceUsage(final PrintStream out, final Command instance) {
        out.println(this.getUsagePrefix() + " " + instance.getUsage());
    }
    
    private void printInstanceHelp(final PrintStream out, final Command instance) {
        out.println(instance.getUsage() + " :");
        TableListing listing = null;
        final String prefix = "  ";
        for (final String line : instance.getDescription().split("\n")) {
            Label_0269: {
                if (line.matches("^[ \t]*[-<].*$")) {
                    final String[] segments = line.split(":");
                    if (segments.length == 2) {
                        if (listing == null) {
                            listing = this.createOptionTableListing();
                        }
                        listing.addRow(segments[0].trim(), segments[1].trim());
                        break Label_0269;
                    }
                }
                if (listing != null) {
                    for (final String listingLine : listing.toString().split("\n")) {
                        out.println("  " + listingLine);
                    }
                    listing = null;
                }
                for (final String descLine : StringUtils.wrap(line, 80, "\n", true).split("\n")) {
                    out.println("  " + descLine);
                }
            }
        }
        if (listing != null) {
            for (final String listingLine2 : listing.toString().split("\n")) {
                out.println("  " + listingLine2);
            }
        }
    }
    
    private TableListing createOptionTableListing() {
        return new TableListing.Builder().addField("").addField("", true).wrapWidth(80).build();
    }
    
    @Override
    public int run(final String[] argv) throws Exception {
        this.init();
        final Tracer tracer = new Tracer.Builder("FsShell").conf(TraceUtils.wrapHadoopConf("fs.shell.htrace.", this.getConf())).build();
        int exitCode = -1;
        if (argv.length < 1) {
            this.printUsage(System.err);
        }
        else {
            final String cmd = argv[0];
            Command instance = null;
            try {
                instance = this.commandFactory.getInstance(cmd);
                if (instance == null) {
                    throw new UnknownCommandException();
                }
                final TraceScope scope = tracer.newScope(instance.getCommandName());
                if (scope.getSpan() != null) {
                    String args = StringUtils.join(" ", argv);
                    if (args.length() > 2048) {
                        args = args.substring(0, 2048);
                    }
                    scope.getSpan().addKVAnnotation("args", args);
                }
                try {
                    exitCode = instance.run((String[])Arrays.copyOfRange(argv, 1, argv.length));
                }
                finally {
                    scope.close();
                }
            }
            catch (IllegalArgumentException e) {
                if (e.getMessage() == null) {
                    this.displayError(cmd, "Null exception message");
                    e.printStackTrace(System.err);
                }
                else {
                    this.displayError(cmd, e.getLocalizedMessage());
                }
                this.printUsage(System.err);
                if (instance != null) {
                    this.printInstanceUsage(System.err, instance);
                }
            }
            catch (Exception e2) {
                FsShell.LOG.debug("Error", e2);
                this.displayError(cmd, "Fatal internal error");
                e2.printStackTrace(System.err);
            }
        }
        tracer.close();
        return exitCode;
    }
    
    private void displayError(final String cmd, final String message) {
        for (final String line : message.split("\n")) {
            System.err.println(cmd + ": " + line);
            if (cmd.charAt(0) != '-') {
                Command instance = null;
                instance = this.commandFactory.getInstance("-" + cmd);
                if (instance != null) {
                    System.err.println("Did you mean -" + cmd + "?  This command begins with a dash.");
                }
            }
        }
    }
    
    public void close() throws IOException {
        if (this.fs != null) {
            this.fs.close();
            this.fs = null;
        }
    }
    
    public static void main(final String[] argv) throws Exception {
        final FsShell shell = newShellInstance();
        final Configuration conf = new Configuration();
        conf.setQuietMode(false);
        shell.setConf(conf);
        int res;
        try {
            res = ToolRunner.run(shell, argv);
        }
        finally {
            shell.close();
        }
        System.exit(res);
    }
    
    protected static FsShell newShellInstance() {
        return new FsShell();
    }
    
    static {
        LOG = LoggerFactory.getLogger(FsShell.class);
    }
    
    protected class Usage extends FsCommand
    {
        public static final String NAME = "usage";
        public static final String USAGE = "[cmd ...]";
        public static final String DESCRIPTION = "Displays the usage for given command or all commands if none is specified.";
        
        @Override
        protected void processRawArguments(final LinkedList<String> args) {
            if (args.isEmpty()) {
                FsShell.this.printUsage(System.out);
            }
            else {
                for (final String arg : args) {
                    FsShell.this.printUsage(System.out, arg);
                }
            }
        }
    }
    
    protected class Help extends FsCommand
    {
        public static final String NAME = "help";
        public static final String USAGE = "[cmd ...]";
        public static final String DESCRIPTION = "Displays help for given command or all commands if none is specified.";
        
        @Override
        protected void processRawArguments(final LinkedList<String> args) {
            if (args.isEmpty()) {
                FsShell.this.printHelp(System.out);
            }
            else {
                for (final String arg : args) {
                    FsShell.this.printHelp(System.out, arg);
                }
            }
        }
    }
    
    static class UnknownCommandException extends IllegalArgumentException
    {
        private final String cmd;
        
        UnknownCommandException() {
            this((String)null);
        }
        
        UnknownCommandException(final String cmd) {
            this.cmd = cmd;
        }
        
        @Override
        public String getMessage() {
            return ((this.cmd != null) ? ("`" + this.cmd + "': ") : "") + "Unknown command";
        }
    }
}
