// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.io.PrintStream;
import java.util.ArrayList;
import org.apache.hadoop.fs.ContentSummary;
import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.viewfs.ViewFileSystem;
import org.apache.hadoop.fs.viewfs.ViewFileSystemUtil;
import org.apache.hadoop.fs.FsStatus;
import java.net.URI;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class FsUsage extends FsCommand
{
    private boolean humanReadable;
    private TableBuilder usagesTable;
    
    FsUsage() {
        this.humanReadable = false;
    }
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Df.class, "-df");
        factory.addClass(Du.class, "-du");
        factory.addClass(Dus.class, "-dus");
    }
    
    protected String formatSize(final long size) {
        return this.humanReadable ? StringUtils.TraditionalBinaryPrefix.long2String(size, "", 1) : String.valueOf(size);
    }
    
    public TableBuilder getUsagesTable() {
        return this.usagesTable;
    }
    
    public void setUsagesTable(final TableBuilder usagesTable) {
        this.usagesTable = usagesTable;
    }
    
    public void setHumanReadable(final boolean humanReadable) {
        this.humanReadable = humanReadable;
    }
    
    public static class Df extends FsUsage
    {
        public static final String NAME = "df";
        public static final String USAGE = "[-h] [<path> ...]";
        public static final String DESCRIPTION = "Shows the capacity, free and used space of the filesystem. If the filesystem has multiple partitions, and no path to a particular partition is specified, then the status of the root partitions will be shown.\n-h: Formats the sizes of files in a human-readable fashion rather than a number of bytes.";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(0, Integer.MAX_VALUE, new String[] { "h" });
            cf.parse(args);
            this.setHumanReadable(cf.getOpt("h"));
            if (args.isEmpty()) {
                args.add("/");
            }
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> args) throws IOException {
            this.setUsagesTable(new TableBuilder(new Object[] { "Filesystem", "Size", "Used", "Available", "Use%", "Mounted on" }));
            this.getUsagesTable().setRightAlign(1, 2, 3, 4);
            super.processArguments(args);
            if (!this.getUsagesTable().isEmpty()) {
                this.getUsagesTable().printToStream(this.out);
            }
        }
        
        private void addToUsagesTable(final URI uri, final FsStatus fsStatus, final String mountedOnPath) {
            final long size = fsStatus.getCapacity();
            final long used = fsStatus.getUsed();
            final long free = fsStatus.getRemaining();
            this.getUsagesTable().addRow(uri, this.formatSize(size), this.formatSize(used), this.formatSize(free), StringUtils.formatPercent(used / (double)size, 0), mountedOnPath);
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (ViewFileSystemUtil.isViewFileSystem(item.fs)) {
                final ViewFileSystem viewFileSystem = (ViewFileSystem)item.fs;
                final Map<ViewFileSystem.MountPoint, FsStatus> fsStatusMap = ViewFileSystemUtil.getStatus(viewFileSystem, item.path);
                for (final Map.Entry<ViewFileSystem.MountPoint, FsStatus> entry : fsStatusMap.entrySet()) {
                    final ViewFileSystem.MountPoint viewFsMountPoint = entry.getKey();
                    final FsStatus fsStatus = entry.getValue();
                    final URI[] mountPointFileSystemURIs = viewFsMountPoint.getTargetFileSystemURIs();
                    this.addToUsagesTable(mountPointFileSystemURIs[0], fsStatus, viewFsMountPoint.getMountedOnPath().toString());
                }
            }
            else {
                this.getUsagesTable().setColumnHide(5, true);
                final FsStatus fsStatus2 = item.fs.getStatus(item.path);
                this.addToUsagesTable(item.fs.getUri(), fsStatus2, "/");
            }
        }
    }
    
    public static class Du extends FsUsage
    {
        public static final String NAME = "du";
        public static final String USAGE = "[-s] [-h] [-v] [-x] <path> ...";
        public static final String DESCRIPTION = "Show the amount of space, in bytes, used by the files that match the specified file pattern. The following flags are optional:\n-s: Rather than showing the size of each individual file that matches the pattern, shows the total (summary) size.\n-h: Formats the sizes of files in a human-readable fashion rather than a number of bytes.\n-v: option displays a header line.\n-x: Excludes snapshots from being counted.\n\nNote that, even without the -s option, this only shows size summaries one level deep into a directory.\n\nThe output is in the form \n\tsize\tdisk space consumed\tname(full path)\n";
        protected boolean summary;
        private boolean showHeaderLine;
        private boolean excludeSnapshots;
        
        public Du() {
            this.summary = false;
            this.showHeaderLine = false;
            this.excludeSnapshots = false;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(0, Integer.MAX_VALUE, new String[] { "h", "s", "v", "x" });
            cf.parse(args);
            this.setHumanReadable(cf.getOpt("h"));
            this.summary = cf.getOpt("s");
            this.showHeaderLine = cf.getOpt("v");
            this.excludeSnapshots = cf.getOpt("x");
            if (args.isEmpty()) {
                args.add(".");
            }
        }
        
        @Override
        protected void processArguments(final LinkedList<PathData> args) throws IOException {
            if (this.showHeaderLine) {
                this.setUsagesTable(new TableBuilder(new Object[] { "SIZE", "DISK_SPACE_CONSUMED_WITH_ALL_REPLICAS", "FULL_PATH_NAME" }));
            }
            else {
                this.setUsagesTable(new TableBuilder(3));
            }
            super.processArguments(args);
            if (!this.getUsagesTable().isEmpty()) {
                this.getUsagesTable().printToStream(this.out);
            }
        }
        
        @Override
        protected void processPathArgument(final PathData item) throws IOException {
            if (!this.summary && item.stat.isDirectory()) {
                this.recursePath(item);
            }
            else {
                super.processPathArgument(item);
            }
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            final ContentSummary contentSummary = item.fs.getContentSummary(item.path);
            long length = contentSummary.getLength();
            long spaceConsumed = contentSummary.getSpaceConsumed();
            if (this.excludeSnapshots) {
                length -= contentSummary.getSnapshotLength();
                spaceConsumed -= contentSummary.getSnapshotSpaceConsumed();
            }
            this.getUsagesTable().addRow(this.formatSize(length), this.formatSize(spaceConsumed), item);
        }
    }
    
    public static class Dus extends Du
    {
        public static final String NAME = "dus";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            args.addFirst("-s");
            super.processOptions(args);
        }
        
        @Override
        public String getReplacementCommand() {
            return "du -s";
        }
    }
    
    private static class TableBuilder
    {
        protected boolean hasHeader;
        protected List<String[]> rows;
        protected int[] widths;
        protected boolean[] rightAlign;
        private boolean[] hide;
        
        public TableBuilder(final int columns) {
            this.hasHeader = false;
            this.rows = new ArrayList<String[]>();
            this.widths = new int[columns];
            this.rightAlign = new boolean[columns];
            this.hide = new boolean[columns];
        }
        
        public TableBuilder(final Object... headers) {
            this(headers.length);
            this.addRow(headers);
            this.hasHeader = true;
        }
        
        public void setRightAlign(final int... indexes) {
            for (final int i : indexes) {
                this.rightAlign[i] = true;
            }
        }
        
        public void setColumnHide(final int columnIndex, final boolean hideCol) {
            this.hide[columnIndex] = hideCol;
        }
        
        public void addRow(final Object... objects) {
            final String[] row = new String[this.widths.length];
            for (int col = 0; col < this.widths.length; ++col) {
                row[col] = String.valueOf(objects[col]);
                this.widths[col] = Math.max(this.widths[col], row[col].length());
            }
            this.rows.add(row);
        }
        
        public void printToStream(final PrintStream out) {
            if (this.isEmpty()) {
                return;
            }
            final StringBuilder fmt = new StringBuilder();
            for (int i = 0; i < this.widths.length; ++i) {
                if (!this.hide[i]) {
                    if (fmt.length() != 0) {
                        fmt.append("  ");
                    }
                    if (this.rightAlign[i]) {
                        fmt.append("%" + this.widths[i] + "s");
                    }
                    else if (i != this.widths.length - 1) {
                        fmt.append("%-" + this.widths[i] + "s");
                    }
                    else {
                        fmt.append("%s");
                    }
                }
            }
            for (final Object[] row : this.rows) {
                out.println(String.format(fmt.toString(), row));
            }
        }
        
        public int size() {
            return this.rows.size() - (this.hasHeader ? 1 : 0);
        }
        
        public boolean isEmpty() {
            return this.size() == 0;
        }
    }
}
