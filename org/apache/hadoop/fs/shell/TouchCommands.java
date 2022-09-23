// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.text.ParseException;
import org.apache.hadoop.util.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.hadoop.fs.PathNotFoundException;
import java.io.IOException;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.fs.PathIsDirectoryException;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TouchCommands extends FsCommand
{
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Touchz.class, "-touchz");
        factory.addClass(Touch.class, "-touch");
    }
    
    public static class Touchz extends TouchCommands
    {
        public static final String NAME = "touchz";
        public static final String USAGE = "<path> ...";
        public static final String DESCRIPTION = "Creates a file of zero length at <path> with current time as the timestamp of that <path>. An error is returned if the file exists with non-zero length\n";
        
        @Override
        protected void processOptions(final LinkedList<String> args) {
            final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[0]);
            cf.parse(args);
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (item.stat.isDirectory()) {
                throw new PathIsDirectoryException(item.toString());
            }
            if (item.stat.getLen() != 0L) {
                throw new PathIOException(item.toString(), "Not a zero-length file");
            }
            this.touchz(item);
        }
        
        @Override
        protected void processNonexistentPath(final PathData item) throws IOException {
            if (!item.parentExists()) {
                throw new PathNotFoundException(item.toString()).withFullyQualifiedPath(item.path.toUri().toString());
            }
            this.touchz(item);
        }
        
        private void touchz(final PathData item) throws IOException {
            item.fs.create(item.path).close();
        }
    }
    
    public static class Touch extends TouchCommands
    {
        private static final String OPTION_CHANGE_ONLY_MODIFICATION_TIME = "m";
        private static final String OPTION_CHANGE_ONLY_ACCESS_TIME = "a";
        private static final String OPTION_USE_TIMESTAMP = "t";
        private static final String OPTION_DO_NOT_CREATE_FILE = "c";
        public static final String NAME = "touch";
        public static final String USAGE = "[-a] [-m] [-t TIMESTAMP ] [-c] <path> ...";
        public static final String DESCRIPTION = "Updates the access and modification times of the file specified by the <path> to the current time. If the file does not exist, then a zero length file is created at <path> with current time as the timestamp of that <path>.\n-a Change only the access time \n-m Change only the modification time \n-t TIMESTAMP Use specified timestamp (in format yyyyMMddHHmmss) instead of current time \n-c Do not create any files";
        private boolean changeModTime;
        private boolean changeAccessTime;
        private boolean doNotCreate;
        private String timestamp;
        private final SimpleDateFormat dateFormat;
        
        public Touch() {
            this.changeModTime = false;
            this.changeAccessTime = false;
            this.doNotCreate = false;
            this.dateFormat = new SimpleDateFormat("yyyyMMdd:HHmmss");
        }
        
        @InterfaceAudience.Private
        @VisibleForTesting
        public DateFormat getDateFormat() {
            return this.dateFormat;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) {
            this.timestamp = StringUtils.popOptionWithArgument("-t", args);
            final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "t", "a", "m" });
            cf.parse(args);
            this.changeModTime = cf.getOpt("m");
            this.changeAccessTime = cf.getOpt("a");
            this.doNotCreate = cf.getOpt("c");
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (item.stat.isDirectory()) {
                throw new PathIsDirectoryException(item.toString());
            }
            this.touch(item);
        }
        
        @Override
        protected void processNonexistentPath(final PathData item) throws IOException {
            if (!item.parentExists()) {
                throw new PathNotFoundException(item.toString()).withFullyQualifiedPath(item.path.toUri().toString());
            }
            this.touch(item);
        }
        
        private void touch(final PathData item) throws IOException {
            if (!item.fs.exists(item.path)) {
                if (this.doNotCreate) {
                    return;
                }
                item.fs.create(item.path).close();
                if (this.timestamp != null) {
                    this.updateTime(item);
                }
            }
            else {
                this.updateTime(item);
            }
        }
        
        private void updateTime(final PathData item) throws IOException {
            long time = System.currentTimeMillis();
            if (this.timestamp != null) {
                try {
                    time = this.dateFormat.parse(this.timestamp).getTime();
                }
                catch (ParseException e) {
                    throw new IllegalArgumentException("Unable to parse the specified timestamp " + this.timestamp, e);
                }
            }
            if (this.changeModTime ^ this.changeAccessTime) {
                final long atime = this.changeModTime ? -1L : time;
                final long mtime = this.changeAccessTime ? -1L : time;
                item.fs.setTimes(item.path, mtime, atime);
            }
            else {
                item.fs.setTimes(item.path, time, time);
            }
        }
    }
}
