// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileStatus;
import java.util.Date;
import java.util.Arrays;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.conf.Configuration;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class Ls extends FsCommand
{
    private static final String OPTION_PATHONLY = "C";
    private static final String OPTION_DIRECTORY = "d";
    private static final String OPTION_HUMAN = "h";
    private static final String OPTION_HIDENONPRINTABLE = "q";
    private static final String OPTION_RECURSIVE = "R";
    private static final String OPTION_REVERSE = "r";
    private static final String OPTION_MTIME = "t";
    private static final String OPTION_ATIME = "u";
    private static final String OPTION_SIZE = "S";
    private static final String OPTION_ECPOLICY = "e";
    public static final String NAME = "ls";
    public static final String USAGE = "[-C] [-d] [-h] [-q] [-R] [-t] [-S] [-r] [-u] [-e] [<path> ...]";
    public static final String DESCRIPTION = "List the contents that match the specified file pattern. If path is not specified, the contents of /user/<currentUser> will be listed. For a directory a list of its direct children is returned (unless -d option is specified).\n\nDirectory entries are of the form:\n\tpermissions - userId groupId sizeOfDirectory(in bytes) modificationDate(yyyy-MM-dd HH:mm) directoryName\n\nand file entries are of the form:\n\tpermissions numberOfReplicas userId groupId sizeOfFile(in bytes) modificationDate(yyyy-MM-dd HH:mm) fileName\n\n  -C  Display the paths of files and directories only.\n  -d  Directories are listed as plain files.\n  -h  Formats the sizes of files in a human-readable fashion\n      rather than a number of bytes.\n  -q  Print ? instead of non-printable characters.\n  -R  Recursively list the contents of directories.\n  -t  Sort files by modification time (most recent first).\n  -S  Sort files by size.\n  -r  Reverse the order of the sort.\n  -u  Use time of last access instead of modification for\n      display and sorting.\n  -e  Display the erasure coding policy of files and directories.\n";
    protected final SimpleDateFormat dateFormat;
    protected int maxRepl;
    protected int maxLen;
    protected int maxOwner;
    protected int maxGroup;
    protected String lineFormat;
    private boolean pathOnly;
    protected boolean dirRecurse;
    private boolean orderReverse;
    private boolean orderTime;
    private boolean orderSize;
    private boolean useAtime;
    private boolean displayECPolicy;
    private Comparator<PathData> orderComparator;
    protected boolean humanReadable;
    private boolean hideNonPrintable;
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Ls.class, "-ls");
        factory.addClass(Lsr.class, "-lsr");
    }
    
    protected Ls() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.maxRepl = 3;
        this.maxLen = 10;
        this.maxOwner = 0;
        this.maxGroup = 0;
        this.humanReadable = false;
        this.hideNonPrintable = false;
    }
    
    protected Ls(final Configuration conf) {
        super(conf);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.maxRepl = 3;
        this.maxLen = 10;
        this.maxOwner = 0;
        this.maxGroup = 0;
        this.humanReadable = false;
        this.hideNonPrintable = false;
    }
    
    protected String formatSize(final long size) {
        return this.humanReadable ? StringUtils.TraditionalBinaryPrefix.long2String(size, "", 1) : String.valueOf(size);
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) throws IOException {
        final CommandFormat cf = new CommandFormat(0, Integer.MAX_VALUE, new String[] { "C", "d", "h", "q", "R", "r", "t", "S", "u", "e" });
        cf.parse(args);
        this.pathOnly = cf.getOpt("C");
        this.dirRecurse = !cf.getOpt("d");
        this.setRecursive(cf.getOpt("R") && this.dirRecurse);
        this.humanReadable = cf.getOpt("h");
        this.hideNonPrintable = cf.getOpt("q");
        this.orderReverse = cf.getOpt("r");
        this.orderTime = cf.getOpt("t");
        this.orderSize = (!this.orderTime && cf.getOpt("S"));
        this.useAtime = cf.getOpt("u");
        this.displayECPolicy = cf.getOpt("e");
        if (args.isEmpty()) {
            args.add(".");
        }
        this.initialiseOrderComparator();
    }
    
    @InterfaceAudience.Private
    boolean isPathOnly() {
        return this.pathOnly;
    }
    
    @InterfaceAudience.Private
    boolean isDirRecurse() {
        return this.dirRecurse;
    }
    
    @InterfaceAudience.Private
    boolean isHumanReadable() {
        return this.humanReadable;
    }
    
    @InterfaceAudience.Private
    private boolean isHideNonPrintable() {
        return this.hideNonPrintable;
    }
    
    @InterfaceAudience.Private
    boolean isOrderReverse() {
        return this.orderReverse;
    }
    
    @InterfaceAudience.Private
    boolean isOrderTime() {
        return this.orderTime;
    }
    
    @InterfaceAudience.Private
    boolean isOrderSize() {
        return this.orderSize;
    }
    
    @InterfaceAudience.Private
    boolean isUseAtime() {
        return this.useAtime;
    }
    
    @Override
    protected void processPathArgument(final PathData item) throws IOException {
        if (this.dirRecurse && item.stat.isDirectory()) {
            this.recursePath(item);
        }
        else {
            super.processPathArgument(item);
        }
    }
    
    @Override
    protected boolean isSorted() {
        return !this.isRecursive() || this.isOrderTime() || this.isOrderSize() || this.isOrderReverse();
    }
    
    @Override
    protected int getListingGroupSize() {
        if (this.pathOnly) {
            return 0;
        }
        return 100;
    }
    
    @Override
    protected void processPaths(final PathData parent, final PathData... items) throws IOException {
        if (parent != null && !this.isRecursive() && items.length != 0) {
            if (!this.pathOnly) {
                this.out.println("Found " + items.length + " items");
            }
            Arrays.sort(items, this.getOrderComparator());
        }
        if (!this.pathOnly) {
            this.adjustColumnWidths(items);
        }
        super.processPaths(parent, items);
    }
    
    @Override
    protected void processPath(final PathData item) throws IOException {
        if (this.pathOnly) {
            this.out.println(item.toString());
            return;
        }
        final FileStatus stat = item.stat;
        if (this.displayECPolicy) {
            final ContentSummary contentSummary = item.fs.getContentSummary(item.path);
            final String line = String.format(this.lineFormat, stat.isDirectory() ? "d" : "-", stat.getPermission() + (stat.hasAcl() ? "+" : " "), stat.isFile() ? Short.valueOf(stat.getReplication()) : "-", stat.getOwner(), stat.getGroup(), contentSummary.getErasureCodingPolicy(), this.formatSize(stat.getLen()), this.dateFormat.format(new Date(this.isUseAtime() ? stat.getAccessTime() : stat.getModificationTime())), this.isHideNonPrintable() ? new PrintableString(item.toString()) : item);
            this.out.println(line);
        }
        else {
            final String line2 = String.format(this.lineFormat, stat.isDirectory() ? "d" : "-", stat.getPermission() + (stat.hasAcl() ? "+" : " "), stat.isFile() ? Short.valueOf(stat.getReplication()) : "-", stat.getOwner(), stat.getGroup(), this.formatSize(stat.getLen()), this.dateFormat.format(new Date(this.isUseAtime() ? stat.getAccessTime() : stat.getModificationTime())), this.isHideNonPrintable() ? new PrintableString(item.toString()) : item);
            this.out.println(line2);
        }
    }
    
    private void adjustColumnWidths(final PathData[] items) throws IOException {
        for (final PathData item : items) {
            final FileStatus stat = item.stat;
            this.maxRepl = this.maxLength(this.maxRepl, stat.getReplication());
            this.maxLen = this.maxLength(this.maxLen, stat.getLen());
            this.maxOwner = this.maxLength(this.maxOwner, stat.getOwner());
            this.maxGroup = this.maxLength(this.maxGroup, stat.getGroup());
        }
        final StringBuilder fmt = new StringBuilder();
        fmt.append("%s%s");
        fmt.append("%" + this.maxRepl + "s ");
        if (this.displayECPolicy) {
            int maxEC = 0;
            for (final PathData item2 : items) {
                final ContentSummary contentSummary = item2.fs.getContentSummary(item2.path);
                maxEC = this.maxLength(maxEC, contentSummary.getErasureCodingPolicy().length());
            }
            fmt.append(" %" + maxEC + "s ");
        }
        fmt.append((this.maxOwner > 0) ? ("%-" + this.maxOwner + "s ") : "%s");
        fmt.append((this.maxGroup > 0) ? ("%-" + this.maxGroup + "s ") : "%s");
        fmt.append("%" + this.maxLen + "s ");
        fmt.append("%s %s");
        this.lineFormat = fmt.toString();
    }
    
    private int maxLength(final int n, final Object value) {
        return Math.max(n, (value != null) ? String.valueOf(value).length() : 0);
    }
    
    private Comparator<PathData> getOrderComparator() {
        return this.orderComparator;
    }
    
    private void initialiseOrderComparator() {
        if (this.isOrderTime()) {
            this.orderComparator = new Comparator<PathData>() {
                @Override
                public int compare(final PathData o1, final PathData o2) {
                    final Long o1Time = Ls.this.isUseAtime() ? o1.stat.getAccessTime() : o1.stat.getModificationTime();
                    final Long o2Time = Ls.this.isUseAtime() ? o2.stat.getAccessTime() : o2.stat.getModificationTime();
                    return o2Time.compareTo(o1Time) * (Ls.this.isOrderReverse() ? -1 : 1);
                }
            };
        }
        else if (this.isOrderSize()) {
            this.orderComparator = new Comparator<PathData>() {
                @Override
                public int compare(final PathData o1, final PathData o2) {
                    final Long o1Length = o1.stat.getLen();
                    final Long o2Length = o2.stat.getLen();
                    return o2Length.compareTo(o1Length) * (Ls.this.isOrderReverse() ? -1 : 1);
                }
            };
        }
        else {
            this.orderComparator = new Comparator<PathData>() {
                @Override
                public int compare(final PathData o1, final PathData o2) {
                    return o1.compareTo(o2) * (Ls.this.isOrderReverse() ? -1 : 1);
                }
            };
        }
    }
    
    public static class Lsr extends Ls
    {
        public static final String NAME = "lsr";
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            args.addFirst("-R");
            super.processOptions(args);
        }
        
        @Override
        public String getReplacementCommand() {
            return "ls -R";
        }
    }
}
