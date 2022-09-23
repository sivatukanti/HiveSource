// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.fs.PathNotFoundException;
import org.apache.hadoop.fs.Path;
import java.io.IOException;
import org.apache.hadoop.fs.PathExistsException;
import org.apache.hadoop.fs.PathIsNotDirectoryException;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class Mkdir extends FsCommand
{
    public static final String NAME = "mkdir";
    public static final String USAGE = "[-p] <path> ...";
    public static final String DESCRIPTION = "Create a directory in specified location.\n-p: Do not fail if the directory already exists";
    private boolean createParents;
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Mkdir.class, "-mkdir");
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) {
        final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "p" });
        cf.parse(args);
        this.createParents = cf.getOpt("p");
    }
    
    @Override
    protected void processPath(final PathData item) throws IOException {
        if (!item.stat.isDirectory()) {
            throw new PathIsNotDirectoryException(item.toString());
        }
        if (!this.createParents) {
            throw new PathExistsException(item.toString());
        }
    }
    
    @Override
    protected void processNonexistentPath(final PathData item) throws IOException {
        if (!this.createParents) {
            final Path itemPath = new Path(item.path.toString());
            final Path itemParentPath = itemPath.getParent();
            if (!item.fs.exists(itemParentPath)) {
                throw new PathNotFoundException(itemParentPath.toString());
            }
        }
        if (!item.fs.mkdirs(item.path)) {
            throw new PathIOException(item.toString());
        }
    }
}
