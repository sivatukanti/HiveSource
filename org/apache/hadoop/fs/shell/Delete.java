// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.PathIsNotEmptyDirectoryException;
import org.apache.hadoop.fs.PathIsNotDirectoryException;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.Trash;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.fs.PathIsDirectoryException;
import org.apache.hadoop.fs.PathNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class Delete
{
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Rm.class, "-rm");
        factory.addClass(Rmdir.class, "-rmdir");
        factory.addClass(Rmr.class, "-rmr");
        factory.addClass(Expunge.class, "-expunge");
    }
    
    public static class Rm extends FsCommand
    {
        public static final String NAME = "rm";
        public static final String USAGE = "[-f] [-r|-R] [-skipTrash] [-safely] <src> ...";
        public static final String DESCRIPTION = "Delete all files that match the specified file pattern. Equivalent to the Unix command \"rm <src>\"\n-f: If the file does not exist, do not display a diagnostic message or modify the exit status to reflect an error.\n-[rR]:  Recursively deletes directories.\n-skipTrash: option bypasses trash, if enabled, and immediately deletes <src>.\n-safely: option requires safety confirmation, if enabled, requires confirmation before deleting large directory with more than <hadoop.shell.delete.limit.num.files> files. Delay is expected when walking over large directory recursively to count the number of files to be deleted before the confirmation.\n";
        private boolean skipTrash;
        private boolean deleteDirs;
        private boolean ignoreFNF;
        private boolean safeDelete;
        
        public Rm() {
            this.skipTrash = false;
            this.deleteDirs = false;
            this.ignoreFNF = false;
            this.safeDelete = false;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "f", "r", "R", "skipTrash", "safely" });
            cf.parse(args);
            this.ignoreFNF = cf.getOpt("f");
            this.deleteDirs = (cf.getOpt("r") || cf.getOpt("R"));
            this.skipTrash = cf.getOpt("skipTrash");
            this.safeDelete = cf.getOpt("safely");
        }
        
        @Override
        protected List<PathData> expandArgument(final String arg) throws IOException {
            try {
                return super.expandArgument(arg);
            }
            catch (PathNotFoundException e) {
                if (!this.ignoreFNF) {
                    throw e;
                }
                return new LinkedList<PathData>();
            }
        }
        
        @Override
        protected void processNonexistentPath(final PathData item) throws IOException {
            if (!this.ignoreFNF) {
                super.processNonexistentPath(item);
            }
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (item.stat.isDirectory() && !this.deleteDirs) {
                throw new PathIsDirectoryException(item.toString());
            }
            if (this.moveToTrash(item) || !this.canBeSafelyDeleted(item)) {
                return;
            }
            if (!item.fs.delete(item.path, this.deleteDirs)) {
                throw new PathIOException(item.toString());
            }
            this.out.println("Deleted " + item);
        }
        
        private boolean canBeSafelyDeleted(final PathData item) throws IOException {
            boolean shouldDelete = true;
            if (this.safeDelete) {
                final long deleteLimit = this.getConf().getLong("hadoop.shell.safely.delete.limit.num.files", 100L);
                if (deleteLimit > 0L) {
                    final ContentSummary cs = item.fs.getContentSummary(item.path);
                    final long numFiles = cs.getFileCount();
                    if (numFiles > deleteLimit && !ToolRunner.confirmPrompt("Proceed deleting " + numFiles + " files?")) {
                        System.err.println("Delete aborted at user request.\n");
                        shouldDelete = false;
                    }
                }
            }
            return shouldDelete;
        }
        
        private boolean moveToTrash(final PathData item) throws IOException {
            boolean success = false;
            if (!this.skipTrash) {
                try {
                    success = Trash.moveToAppropriateTrash(item.fs, item.path, this.getConf());
                }
                catch (FileNotFoundException fnfe) {
                    throw fnfe;
                }
                catch (IOException ioe) {
                    String msg = ioe.getMessage();
                    if (ioe.getCause() != null) {
                        msg = msg + ": " + ioe.getCause().getMessage();
                    }
                    throw new IOException(msg + ". Consider using -skipTrash option", ioe);
                }
            }
            return success;
        }
    }
    
    static class Rmr extends Rm
    {
        public static final String NAME = "rmr";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            args.addFirst("-r");
            super.processOptions(args);
        }
        
        @Override
        public String getReplacementCommand() {
            return "-rm -r";
        }
    }
    
    static class Rmdir extends FsCommand
    {
        public static final String NAME = "rmdir";
        public static final String USAGE = "[--ignore-fail-on-non-empty] <dir> ...";
        public static final String DESCRIPTION = "Removes the directory entry specified by each directory argument, provided it is empty.\n";
        private boolean ignoreNonEmpty;
        
        Rmdir() {
            this.ignoreNonEmpty = false;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "-ignore-fail-on-non-empty" });
            cf.parse(args);
            this.ignoreNonEmpty = cf.getOpt("-ignore-fail-on-non-empty");
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (!item.stat.isDirectory()) {
                throw new PathIsNotDirectoryException(item.toString());
            }
            if (item.fs.listStatus(item.path).length == 0) {
                if (!item.fs.delete(item.path, false)) {
                    throw new PathIOException(item.toString());
                }
            }
            else if (!this.ignoreNonEmpty) {
                throw new PathIsNotEmptyDirectoryException(item.toString());
            }
        }
    }
    
    static class Expunge extends FsCommand
    {
        public static final String NAME = "expunge";
        public static final String USAGE = "";
        public static final String DESCRIPTION = "Delete files from the trash that are older than the retention threshold";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(0, 0, new String[0]);
            cf.parse(args);
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> args) throws IOException {
            final FileSystem[] childFileSystems = FileSystem.get(this.getConf()).getChildFileSystems();
            if (null != childFileSystems) {
                for (final FileSystem fs : childFileSystems) {
                    final Trash trash = new Trash(fs, this.getConf());
                    trash.expunge();
                    trash.checkpoint();
                }
            }
            else {
                final Trash trash2 = new Trash(this.getConf());
                trash2.expunge();
                trash2.checkpoint();
            }
        }
    }
}
