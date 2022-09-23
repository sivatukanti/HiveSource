// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.BlockLocation;
import java.util.Iterator;
import org.apache.hadoop.fs.PathIOException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class SetReplication extends FsCommand
{
    public static final String NAME = "setrep";
    public static final String USAGE = "[-R] [-w] <rep> <path> ...";
    public static final String DESCRIPTION = "Set the replication level of a file. If <path> is a directory then the command recursively changes the replication factor of all files under the directory tree rooted at <path>. The EC files will be ignored here.\n-w: It requests that the command waits for the replication to complete. This can potentially take a very long time.\n-R: It is accepted for backwards compatibility. It has no effect.";
    protected short newRep;
    protected List<PathData> waitList;
    protected boolean waitOpt;
    
    SetReplication() {
        this.newRep = 0;
        this.waitList = new LinkedList<PathData>();
        this.waitOpt = false;
    }
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(SetReplication.class, "-setrep");
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) throws IOException {
        final CommandFormat cf = new CommandFormat(2, Integer.MAX_VALUE, new String[] { "R", "w" });
        cf.parse(args);
        this.waitOpt = cf.getOpt("w");
        this.setRecursive(true);
        try {
            this.newRep = Short.parseShort(args.removeFirst());
        }
        catch (NumberFormatException nfe) {
            this.displayWarning("Illegal replication, a positive integer expected");
            throw nfe;
        }
        if (this.newRep < 1) {
            throw new IllegalArgumentException("replication must be >= 1");
        }
    }
    
    @Override
    protected void processArguments(final LinkedList<PathData> args) throws IOException {
        super.processArguments(args);
        if (this.waitOpt) {
            this.waitForReplication();
        }
    }
    
    @Override
    protected void processPath(final PathData item) throws IOException {
        if (item.stat.isSymlink()) {
            throw new PathIOException(item.toString(), "Symlinks unsupported");
        }
        if (item.stat.isFile()) {
            if (!item.stat.isErasureCoded()) {
                if (!item.fs.setReplication(item.path, this.newRep)) {
                    throw new IOException("Could not set replication for: " + item);
                }
                this.out.println("Replication " + this.newRep + " set: " + item);
                if (this.waitOpt) {
                    this.waitList.add(item);
                }
            }
            else {
                this.out.println("Did not set replication for: " + item + ", because it's an erasure coded file.");
            }
        }
    }
    
    private void waitForReplication() throws IOException {
        for (final PathData item : this.waitList) {
            this.out.print("Waiting for " + item + " ...");
            this.out.flush();
            boolean printedWarning = false;
            boolean done = false;
            while (!done) {
                item.refreshStatus();
                final BlockLocation[] locations = item.fs.getFileBlockLocations(item.stat, 0L, item.stat.getLen());
                int i = 0;
                while (i < locations.length) {
                    final int currentRep = locations[i].getHosts().length;
                    if (currentRep != this.newRep) {
                        if (!printedWarning && currentRep > this.newRep) {
                            this.out.println("\nWARNING: the waiting time may be long for DECREASING the number of replications.");
                            printedWarning = true;
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
                done = (i == locations.length);
                if (done) {
                    break;
                }
                this.out.print(".");
                this.out.flush();
                try {
                    Thread.sleep(10000L);
                }
                catch (InterruptedException ex) {}
            }
            this.out.println(" done");
        }
    }
}
