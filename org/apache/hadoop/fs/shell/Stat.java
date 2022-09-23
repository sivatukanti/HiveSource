// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.FileStatus;
import java.util.Date;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class Stat extends FsCommand
{
    private static final String NEWLINE;
    public static final String NAME = "stat";
    public static final String USAGE = "[format] <path> ...";
    public static final String DESCRIPTION;
    protected final SimpleDateFormat timeFmt;
    protected String format;
    
    Stat() {
        (this.timeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).setTimeZone(TimeZone.getTimeZone("UTC"));
        this.format = "%y";
    }
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Stat.class, "-stat");
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) throws IOException {
        final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "R" });
        cf.parse(args);
        this.setRecursive(cf.getOpt("R"));
        if (args.getFirst().contains("%")) {
            this.format = args.removeFirst();
        }
        cf.parse(args);
    }
    
    @Override
    protected void processPath(final PathData item) throws IOException {
        final FileStatus stat = item.stat;
        final StringBuilder buf = new StringBuilder();
        final char[] fmt = this.format.toCharArray();
        for (int i = 0; i < fmt.length; ++i) {
            if (fmt[i] != '%') {
                buf.append(fmt[i]);
            }
            else {
                if (i + 1 == fmt.length) {
                    break;
                }
                switch (fmt[++i]) {
                    case 'a': {
                        buf.append(stat.getPermission().toOctal());
                        break;
                    }
                    case 'A': {
                        buf.append(stat.getPermission());
                        break;
                    }
                    case 'b': {
                        buf.append(stat.getLen());
                        break;
                    }
                    case 'F': {
                        buf.append(stat.isDirectory() ? "directory" : (stat.isFile() ? "regular file" : "symlink"));
                        break;
                    }
                    case 'g': {
                        buf.append(stat.getGroup());
                        break;
                    }
                    case 'n': {
                        buf.append(item.path.getName());
                        break;
                    }
                    case 'o': {
                        buf.append(stat.getBlockSize());
                        break;
                    }
                    case 'r': {
                        buf.append(stat.getReplication());
                        break;
                    }
                    case 'u': {
                        buf.append(stat.getOwner());
                        break;
                    }
                    case 'x': {
                        buf.append(this.timeFmt.format(new Date(stat.getAccessTime())));
                        break;
                    }
                    case 'X': {
                        buf.append(stat.getAccessTime());
                        break;
                    }
                    case 'y': {
                        buf.append(this.timeFmt.format(new Date(stat.getModificationTime())));
                        break;
                    }
                    case 'Y': {
                        buf.append(stat.getModificationTime());
                        break;
                    }
                    default: {
                        buf.append(fmt[i]);
                        break;
                    }
                }
            }
        }
        this.out.println(buf.toString());
    }
    
    static {
        NEWLINE = System.getProperty("line.separator");
        DESCRIPTION = "Print statistics about the file/directory at <path>" + Stat.NEWLINE + "in the specified format. Format accepts permissions in" + Stat.NEWLINE + "octal (%a) and symbolic (%A), filesize in" + Stat.NEWLINE + "bytes (%b), type (%F), group name of owner (%g)," + Stat.NEWLINE + "name (%n), block size (%o), replication (%r), user name" + Stat.NEWLINE + "of owner (%u), access date (%x, %X)." + Stat.NEWLINE + "modification date (%y, %Y)." + Stat.NEWLINE + "%x and %y show UTC date as \"yyyy-MM-dd HH:mm:ss\" and" + Stat.NEWLINE + "%X and %Y show milliseconds since January 1, 1970 UTC." + Stat.NEWLINE + "If the format is not specified, %y is used by default." + Stat.NEWLINE;
    }
}
