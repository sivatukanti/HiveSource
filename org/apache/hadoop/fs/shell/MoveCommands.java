// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.fs.PathIOException;
import java.io.IOException;
import org.apache.hadoop.fs.PathExistsException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class MoveCommands
{
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(MoveFromLocal.class, "-moveFromLocal");
        factory.addClass(MoveToLocal.class, "-moveToLocal");
        factory.addClass(Rename.class, "-mv");
    }
    
    public static class MoveFromLocal extends CopyCommands.Put
    {
        public static final String NAME = "moveFromLocal";
        public static final String USAGE = "<localsrc> ... <dst>";
        public static final String DESCRIPTION = "Same as -put, except that the source is deleted after it's copied.";
        
        @Override
        protected void processPath(final PathData src, final PathData target) throws IOException {
            if (target.exists && target.stat.isDirectory()) {
                throw new PathExistsException(target.toString());
            }
            super.processPath(src, target);
        }
        
        @Override
        protected void postProcessPath(final PathData src) throws IOException {
            if (!src.fs.delete(src.path, false)) {
                final PathIOException e = new PathIOException(src.toString());
                e.setOperation("remove");
                throw e;
            }
        }
    }
    
    public static class MoveToLocal extends FsCommand
    {
        public static final String NAME = "moveToLocal";
        public static final String USAGE = "<src> <localdst>";
        public static final String DESCRIPTION = "Not implemented yet";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            throw new IOException("Option '-moveToLocal' is not implemented yet.");
        }
    }
    
    public static class Rename extends CommandWithDestination
    {
        public static final String NAME = "mv";
        public static final String USAGE = "<src> ... <dst>";
        public static final String DESCRIPTION = "Move files that match the specified file pattern <src> to a destination <dst>.  When moving multiple files, the destination must be a directory.";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(2, Integer.MAX_VALUE, new String[0]);
            cf.parse(args);
            this.getRemoteDestination(args);
        }
        
        @Override
        protected void processPath(final PathData src, final PathData target) throws IOException {
            final String srcUri = src.fs.getUri().getScheme() + "://" + src.fs.getUri().getHost();
            final String dstUri = target.fs.getUri().getScheme() + "://" + target.fs.getUri().getHost();
            if (!srcUri.equals(dstUri)) {
                throw new PathIOException(src.toString(), "Does not match target filesystem");
            }
            if (target.exists) {
                throw new PathExistsException(target.toString());
            }
            if (!target.fs.rename(src.path, target.path)) {
                throw new PathIOException(src.toString());
            }
        }
    }
}
