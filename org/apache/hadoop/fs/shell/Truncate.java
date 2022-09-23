// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.util.Iterator;
import org.apache.hadoop.fs.PathIsDirectoryException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class Truncate extends FsCommand
{
    public static final String NAME = "truncate";
    public static final String USAGE = "[-w] <length> <path> ...";
    public static final String DESCRIPTION = "Truncate all files that match the specified file pattern to the specified length.\n-w: Requests that the command wait for block recovery to complete, if necessary.";
    protected long newLength;
    protected List<PathData> waitList;
    protected boolean waitOpt;
    
    public Truncate() {
        this.newLength = -1L;
        this.waitList = new LinkedList<PathData>();
        this.waitOpt = false;
    }
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Truncate.class, "-truncate");
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) throws IOException {
        final CommandFormat cf = new CommandFormat(2, Integer.MAX_VALUE, new String[] { "w" });
        cf.parse(args);
        this.waitOpt = cf.getOpt("w");
        try {
            this.newLength = Long.parseLong(args.removeFirst());
        }
        catch (NumberFormatException nfe) {
            this.displayWarning("Illegal length, a non-negative integer expected");
            throw nfe;
        }
        if (this.newLength < 0L) {
            throw new IllegalArgumentException("length must be >= 0");
        }
    }
    
    @Override
    protected void processArguments(final LinkedList<PathData> args) throws IOException {
        super.processArguments(args);
        if (this.waitOpt) {
            this.waitForRecovery();
        }
    }
    
    @Override
    protected void processPath(final PathData item) throws IOException {
        if (item.stat.isDirectory()) {
            throw new PathIsDirectoryException(item.toString());
        }
        final long oldLength = item.stat.getLen();
        if (this.newLength > oldLength) {
            throw new IllegalArgumentException("Cannot truncate to a larger file size. Current size: " + oldLength + ", truncate size: " + this.newLength + ".");
        }
        if (item.fs.truncate(item.path, this.newLength)) {
            this.out.println("Truncated " + item + " to length: " + this.newLength);
        }
        else if (this.waitOpt) {
            this.waitList.add(item);
        }
        else {
            this.out.println("Truncating " + item + " to length: " + this.newLength + ". Wait for block recovery to complete before further updating this file.");
        }
    }
    
    private void waitForRecovery() throws IOException {
        for (final PathData item : this.waitList) {
            this.out.println("Waiting for " + item + " ...");
            this.out.flush();
            while (true) {
                item.refreshStatus();
                if (item.stat.getLen() == this.newLength) {
                    break;
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ex) {}
            }
            this.out.println("Truncated " + item + " to length: " + this.newLength);
            this.out.flush();
        }
    }
}
