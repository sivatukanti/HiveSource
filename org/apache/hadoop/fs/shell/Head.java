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
class Head extends FsCommand
{
    public static final String NAME = "head";
    public static final String USAGE = "<file>";
    public static final String DESCRIPTION = "Show the first 1KB of the file.\n";
    private long endingOffset;
    
    Head() {
        this.endingOffset = 1024L;
    }
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Head.class, "-head");
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) throws IOException {
        final CommandFormat cf = new CommandFormat(1, 1, new String[0]);
        cf.parse(args);
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
        this.dumpToOffset(item);
    }
    
    private void dumpToOffset(final PathData item) throws IOException {
        final FSDataInputStream in = item.fs.open(item.path);
        try {
            IOUtils.copyBytes(in, System.out, this.endingOffset, false);
        }
        finally {
            in.close();
        }
    }
}
