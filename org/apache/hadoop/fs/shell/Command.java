// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import org.apache.hadoop.util.StringUtils;
import java.io.InterruptedIOException;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.PathNotFoundException;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import java.io.PrintStream;
import org.slf4j.Logger;
import java.util.ArrayList;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class Command extends Configured
{
    public static final String COMMAND_NAME_FIELD = "NAME";
    public static final String COMMAND_USAGE_FIELD = "USAGE";
    public static final String COMMAND_DESCRIPTION_FIELD = "DESCRIPTION";
    protected String[] args;
    protected String name;
    protected int exitCode;
    protected int numErrors;
    protected boolean recursive;
    private int depth;
    protected ArrayList<Exception> exceptions;
    private static final Logger LOG;
    public PrintStream out;
    public PrintStream err;
    private CommandFactory commandFactory;
    
    protected Command() {
        this.exitCode = 0;
        this.numErrors = 0;
        this.recursive = false;
        this.depth = 0;
        this.exceptions = new ArrayList<Exception>();
        this.out = System.out;
        this.err = System.err;
        this.commandFactory = null;
        this.out = System.out;
        this.err = System.err;
    }
    
    protected Command(final Configuration conf) {
        super(conf);
        this.exitCode = 0;
        this.numErrors = 0;
        this.recursive = false;
        this.depth = 0;
        this.exceptions = new ArrayList<Exception>();
        this.out = System.out;
        this.err = System.err;
        this.commandFactory = null;
    }
    
    public abstract String getCommandName();
    
    protected void setRecursive(final boolean flag) {
        this.recursive = flag;
    }
    
    protected boolean isRecursive() {
        return this.recursive;
    }
    
    protected int getDepth() {
        return this.depth;
    }
    
    protected abstract void run(final Path p0) throws IOException;
    
    protected void run(final PathData pathData) throws IOException {
        this.run(pathData.path);
    }
    
    public int runAll() {
        int exitCode = 0;
        for (final String src : this.args) {
            try {
                final PathData[] expandAsGlob;
                final PathData[] srcs = expandAsGlob = PathData.expandAsGlob(src, this.getConf());
                for (final PathData s : expandAsGlob) {
                    this.run(s);
                }
            }
            catch (IOException e) {
                exitCode = -1;
                this.displayError(e);
            }
        }
        return exitCode;
    }
    
    public void setCommandFactory(final CommandFactory factory) {
        this.commandFactory = factory;
    }
    
    protected CommandFactory getCommandFactory() {
        return this.commandFactory;
    }
    
    public int run(final String... argv) {
        final LinkedList<String> args = new LinkedList<String>(Arrays.asList(argv));
        try {
            if (this.isDeprecated()) {
                this.displayWarning("DEPRECATED: Please use '" + this.getReplacementCommand() + "' instead.");
            }
            this.processOptions(args);
            this.processRawArguments(args);
        }
        catch (CommandInterruptException e2) {
            this.displayError("Interrupted");
            return 130;
        }
        catch (IOException e) {
            this.displayError(e);
        }
        return (this.numErrors == 0) ? this.exitCode : this.exitCodeForError();
    }
    
    protected int exitCodeForError() {
        return 1;
    }
    
    protected void processOptions(final LinkedList<String> args) throws IOException {
    }
    
    protected void processRawArguments(final LinkedList<String> args) throws IOException {
        this.processArguments(this.expandArguments(args));
    }
    
    protected LinkedList<PathData> expandArguments(final LinkedList<String> args) throws IOException {
        final LinkedList<PathData> expandedArgs = new LinkedList<PathData>();
        for (final String arg : args) {
            try {
                expandedArgs.addAll(this.expandArgument(arg));
            }
            catch (IOException e) {
                this.displayError(e);
            }
        }
        return expandedArgs;
    }
    
    protected List<PathData> expandArgument(final String arg) throws IOException {
        final PathData[] items = PathData.expandAsGlob(arg, this.getConf());
        if (items.length == 0) {
            throw new PathNotFoundException(arg);
        }
        return Arrays.asList(items);
    }
    
    protected void processArguments(final LinkedList<PathData> args) throws IOException {
        for (final PathData arg : args) {
            try {
                this.processArgument(arg);
            }
            catch (IOException e) {
                this.displayError(e);
            }
        }
    }
    
    protected void processArgument(final PathData item) throws IOException {
        if (item.exists) {
            this.processPathArgument(item);
        }
        else {
            this.processNonexistentPath(item);
        }
    }
    
    protected void processPathArgument(final PathData item) throws IOException {
        this.depth = 0;
        this.processPaths(null, item);
    }
    
    protected void processNonexistentPath(final PathData item) throws IOException {
        throw new PathNotFoundException(item.toString());
    }
    
    protected void processPaths(final PathData parent, final PathData... items) throws IOException {
        for (final PathData item : items) {
            try {
                this.processPathInternal(item);
            }
            catch (IOException e) {
                this.displayError(e);
            }
        }
    }
    
    protected void processPaths(final PathData parent, final RemoteIterator<PathData> itemsIterator) throws IOException {
        final int groupSize = this.getListingGroupSize();
        if (groupSize == 0) {
            while (itemsIterator.hasNext()) {
                this.processPaths(parent, itemsIterator.next());
            }
        }
        else {
            final List<PathData> items = new ArrayList<PathData>(groupSize);
            while (itemsIterator.hasNext()) {
                items.add(itemsIterator.next());
                if (!itemsIterator.hasNext() || items.size() == groupSize) {
                    this.processPaths(parent, (PathData[])items.toArray(new PathData[items.size()]));
                    items.clear();
                }
            }
        }
    }
    
    private void processPathInternal(final PathData item) throws IOException {
        this.processPath(item);
        if (this.recursive && this.isPathRecursable(item)) {
            this.recursePath(item);
        }
        this.postProcessPath(item);
    }
    
    protected boolean isSorted() {
        return false;
    }
    
    protected int getListingGroupSize() {
        return 0;
    }
    
    protected boolean isPathRecursable(final PathData item) throws IOException {
        return item.stat.isDirectory();
    }
    
    protected void processPath(final PathData item) throws IOException {
        throw new RuntimeException("processPath() is not implemented");
    }
    
    protected void postProcessPath(final PathData item) throws IOException {
    }
    
    protected void recursePath(final PathData item) throws IOException {
        try {
            ++this.depth;
            if (this.isSorted()) {
                this.processPaths(item, item.getDirectoryContents());
            }
            else {
                this.processPaths(item, item.getDirectoryContentsIterator());
            }
        }
        finally {
            --this.depth;
        }
    }
    
    public void displayError(final Exception e) {
        this.exceptions.add(e);
        if (e instanceof InterruptedIOException) {
            throw new CommandInterruptException();
        }
        String errorMessage = e.getLocalizedMessage();
        if (errorMessage == null) {
            errorMessage = StringUtils.stringifyException(e);
            Command.LOG.debug(errorMessage);
        }
        else {
            errorMessage = errorMessage.split("\n", 2)[0];
        }
        this.displayError(errorMessage);
    }
    
    public void displayError(final String message) {
        ++this.numErrors;
        this.displayWarning(message);
    }
    
    public void displayWarning(final String message) {
        this.err.println(this.getName() + ": " + message);
    }
    
    public String getName() {
        return (this.name == null) ? this.getCommandField("NAME") : (this.name.startsWith("-") ? this.name.substring(1) : this.name);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getUsage() {
        final String cmd = "-" + this.getName();
        final String usage = this.isDeprecated() ? "" : this.getCommandField("USAGE");
        return usage.isEmpty() ? cmd : (cmd + " " + usage);
    }
    
    public String getDescription() {
        return this.isDeprecated() ? ("(DEPRECATED) Same as '" + this.getReplacementCommand() + "'") : this.getCommandField("DESCRIPTION");
    }
    
    public final boolean isDeprecated() {
        return this.getReplacementCommand() != null;
    }
    
    public String getReplacementCommand() {
        return null;
    }
    
    private String getCommandField(final String field) {
        String value;
        try {
            final Field f = this.getClass().getDeclaredField(field);
            f.setAccessible(true);
            value = f.get(this).toString();
        }
        catch (Exception e) {
            throw new RuntimeException("failed to get " + this.getClass().getSimpleName() + "." + field, e);
        }
        return value;
    }
    
    static {
        LOG = LoggerFactory.getLogger(Command.class);
    }
    
    static class CommandInterruptException extends RuntimeException
    {
    }
}
