// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.io.FileNotFoundException;
import org.apache.hadoop.security.AccessControlException;
import java.io.IOException;
import org.apache.hadoop.fs.permission.FsAction;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class Test extends FsCommand
{
    public static final String NAME = "test";
    public static final String USAGE = "-[defsz] <path>";
    public static final String DESCRIPTION = "Answer various questions about <path>, with result via exit status.\n  -d  return 0 if <path> is a directory.\n  -e  return 0 if <path> exists.\n  -f  return 0 if <path> is a file.\n  -s  return 0 if file <path> is greater         than zero bytes in size.\n  -w  return 0 if file <path> exists         and write permission is granted.\n  -r  return 0 if file <path> exists         and read permission is granted.\n  -z  return 0 if file <path> is         zero bytes in size, else return 1.";
    private char flag;
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Test.class, "-test");
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) {
        final CommandFormat cf = new CommandFormat(1, 1, new String[] { "e", "d", "f", "s", "z", "w", "r" });
        cf.parse(args);
        final String[] opts = cf.getOpts().toArray(new String[0]);
        switch (opts.length) {
            case 0: {
                throw new IllegalArgumentException("No test flag given");
            }
            case 1: {
                this.flag = opts[0].charAt(0);
            }
            default: {
                throw new IllegalArgumentException("Only one test flag is allowed");
            }
        }
    }
    
    @Override
    protected void processPath(final PathData item) throws IOException {
        boolean test = false;
        switch (this.flag) {
            case 'e': {
                test = true;
                break;
            }
            case 'd': {
                test = item.stat.isDirectory();
                break;
            }
            case 'f': {
                test = item.stat.isFile();
                break;
            }
            case 's': {
                test = (item.stat.getLen() > 0L);
                break;
            }
            case 'z': {
                test = (item.stat.getLen() == 0L);
                break;
            }
            case 'w': {
                test = this.testAccess(item, FsAction.WRITE);
                break;
            }
            case 'r': {
                test = this.testAccess(item, FsAction.READ);
                break;
            }
        }
        if (!test) {
            this.exitCode = 1;
        }
    }
    
    private boolean testAccess(final PathData item, final FsAction action) throws IOException {
        try {
            item.fs.access(item.path, action);
            return true;
        }
        catch (AccessControlException | FileNotFoundException ex2) {
            final IOException ex;
            final IOException e = ex;
            return false;
        }
    }
    
    @Override
    protected void processNonexistentPath(final PathData item) throws IOException {
        this.exitCode = 1;
    }
}
