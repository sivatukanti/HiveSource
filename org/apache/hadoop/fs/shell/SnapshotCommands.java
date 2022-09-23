// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import com.google.common.base.Preconditions;
import org.apache.hadoop.fs.Path;
import java.util.LinkedList;
import java.io.IOException;
import org.apache.hadoop.fs.PathIsNotDirectoryException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class SnapshotCommands extends FsCommand
{
    private static final String CREATE_SNAPSHOT = "createSnapshot";
    private static final String DELETE_SNAPSHOT = "deleteSnapshot";
    private static final String RENAME_SNAPSHOT = "renameSnapshot";
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(CreateSnapshot.class, "-createSnapshot");
        factory.addClass(DeleteSnapshot.class, "-deleteSnapshot");
        factory.addClass(RenameSnapshot.class, "-renameSnapshot");
    }
    
    public static class CreateSnapshot extends FsCommand
    {
        public static final String NAME = "createSnapshot";
        public static final String USAGE = "<snapshotDir> [<snapshotName>]";
        public static final String DESCRIPTION = "Create a snapshot on a directory";
        private String snapshotName;
        
        public CreateSnapshot() {
            this.snapshotName = null;
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (!item.stat.isDirectory()) {
                throw new PathIsNotDirectoryException(item.toString());
            }
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            if (args.size() == 0) {
                throw new IllegalArgumentException("<snapshotDir> is missing.");
            }
            if (args.size() > 2) {
                throw new IllegalArgumentException("Too many arguments.");
            }
            if (args.size() == 2) {
                this.snapshotName = args.removeLast();
            }
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> items) throws IOException {
            super.processArguments(items);
            if (this.numErrors != 0) {
                return;
            }
            assert items.size() == 1;
            final PathData sroot = items.getFirst();
            final Path snapshotPath = sroot.fs.createSnapshot(sroot.path, this.snapshotName);
            this.out.println("Created snapshot " + snapshotPath);
        }
    }
    
    public static class DeleteSnapshot extends FsCommand
    {
        public static final String NAME = "deleteSnapshot";
        public static final String USAGE = "<snapshotDir> <snapshotName>";
        public static final String DESCRIPTION = "Delete a snapshot from a directory";
        private String snapshotName;
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (!item.stat.isDirectory()) {
                throw new PathIsNotDirectoryException(item.toString());
            }
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            if (args.size() != 2) {
                throw new IllegalArgumentException("Incorrect number of arguments.");
            }
            this.snapshotName = args.removeLast();
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> items) throws IOException {
            super.processArguments(items);
            if (this.numErrors != 0) {
                return;
            }
            assert items.size() == 1;
            final PathData sroot = items.getFirst();
            sroot.fs.deleteSnapshot(sroot.path, this.snapshotName);
        }
    }
    
    public static class RenameSnapshot extends FsCommand
    {
        public static final String NAME = "renameSnapshot";
        public static final String USAGE = "<snapshotDir> <oldName> <newName>";
        public static final String DESCRIPTION = "Rename a snapshot from oldName to newName";
        private String oldName;
        private String newName;
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (!item.stat.isDirectory()) {
                throw new PathIsNotDirectoryException(item.toString());
            }
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            if (args.size() != 3) {
                throw new IllegalArgumentException("Incorrect number of arguments.");
            }
            this.newName = args.removeLast();
            this.oldName = args.removeLast();
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> items) throws IOException {
            super.processArguments(items);
            if (this.numErrors != 0) {
                return;
            }
            Preconditions.checkArgument(items.size() == 1);
            final PathData sroot = items.getFirst();
            sroot.fs.renameSnapshot(sroot.path, this.oldName, this.newName);
        }
    }
}
