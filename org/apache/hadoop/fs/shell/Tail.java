// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.FSDataInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.fs.PathIsDirectoryException;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class Tail extends FsCommand
{
    public static final String NAME = "tail";
    public static final String USAGE = "[-f] <file>";
    public static final String DESCRIPTION = "Show the last 1KB of the file.\n-f: Shows appended data as the file grows.\n";
    private long startingOffset;
    private boolean follow;
    private long followDelay;
    
    Tail() {
        this.startingOffset = -1024L;
        this.follow = false;
        this.followDelay = 5000L;
    }
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Tail.class, "-tail");
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) throws IOException {
        final CommandFormat cf = new CommandFormat(1, 1, new String[] { "f" });
        cf.parse(args);
        this.follow = cf.getOpt("f");
    }
    
    @Override
    protected List<PathData> expandArgument(final String arg) throws IOException {
        final List<PathData> items = new LinkedList<PathData>();
        items.add(new PathData(arg, this.getConf()));
        return items;
    }
    
    @Override
    protected void processPath(final PathData item) throws IOException {
        if (item.stat.isDirectory()) {
            throw new PathIsDirectoryException(item.toString());
        }
        long offset = this.dumpFromOffset(item, this.startingOffset);
        while (this.follow) {
            try {
                Thread.sleep(this.followDelay);
            }
            catch (InterruptedException e) {
                break;
            }
            offset = this.dumpFromOffset(item, offset);
        }
    }
    
    private long dumpFromOffset(final PathData item, long offset) throws IOException {
        final long fileSize = item.refreshStatus().getLen();
        if (offset > fileSize) {
            return fileSize;
        }
        if (offset < 0L) {
            offset = Math.max(fileSize + offset, 0L);
        }
        final FSDataInputStream in = item.fs.open(item.path);
        try {
            in.seek(offset);
            IOUtils.copyBytes(in, System.out, this.getConf(), false);
            offset = in.getPos();
        }
        finally {
            in.close();
        }
        return offset;
    }
}
