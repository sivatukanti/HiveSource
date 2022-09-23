// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.shell.PathData;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.shell.CommandFormat;
import java.util.LinkedList;
import org.apache.hadoop.fs.permission.ChmodParser;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.fs.shell.Command;
import org.apache.hadoop.fs.shell.CommandFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.shell.FsCommand;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FsShellPermissions extends FsCommand
{
    static final Logger LOG;
    private static String allowedChars;
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Chmod.class, "-chmod");
        factory.addClass(Chown.class, "-chown");
        factory.addClass(Chgrp.class, "-chgrp");
    }
    
    static {
        LOG = FsShell.LOG;
        FsShellPermissions.allowedChars = (Shell.WINDOWS ? "[-_./@a-zA-Z0-9 ]" : "[-_./@a-zA-Z0-9]");
    }
    
    public static class Chmod extends FsShellPermissions
    {
        public static final String NAME = "chmod";
        public static final String USAGE = "[-R] <MODE[,MODE]... | OCTALMODE> PATH...";
        public static final String DESCRIPTION = "Changes permissions of a file. This works similar to the shell's chmod command with a few exceptions.\n-R: modifies the files recursively. This is the only option currently supported.\n<MODE>: Mode is the same as mode used for the shell's command. The only letters recognized are 'rwxXt', e.g. +t,a+r,g-w,+rwx,o=r.\n<OCTALMODE>: Mode specifed in 3 or 4 digits. If 4 digits, the first may be 1 or 0 to turn the sticky bit on or off, respectively.  Unlike the shell command, it is not possible to specify only part of the mode, e.g. 754 is same as u=rwx,g=rx,o=r.\n\nIf none of 'augo' is specified, 'a' is assumed and unlike the shell command, no umask is applied.";
        protected ChmodParser pp;
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(2, Integer.MAX_VALUE, new String[] { "R", null });
            cf.parse(args);
            this.setRecursive(cf.getOpt("R"));
            final String modeStr = args.removeFirst();
            try {
                this.pp = new ChmodParser(modeStr);
            }
            catch (IllegalArgumentException iea) {
                throw new IllegalArgumentException("chmod : mode '" + modeStr + "' does not match the expected pattern.");
            }
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            final short newperms = this.pp.applyNewPermission(item.stat);
            if (item.stat.getPermission().toShort() != newperms) {
                try {
                    item.fs.setPermission(item.path, new FsPermission(newperms));
                }
                catch (IOException e) {
                    Chmod.LOG.debug("Error changing permissions of " + item, e);
                    throw new IOException("changing permissions of '" + item + "': " + e.getMessage());
                }
            }
        }
    }
    
    public static class Chown extends FsShellPermissions
    {
        public static final String NAME = "chown";
        public static final String USAGE = "[-R] [OWNER][:[GROUP]] PATH...";
        public static final String DESCRIPTION;
        private static final Pattern chownPattern;
        protected String owner;
        protected String group;
        
        public Chown() {
            this.owner = null;
            this.group = null;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(2, Integer.MAX_VALUE, new String[] { "R" });
            cf.parse(args);
            this.setRecursive(cf.getOpt("R"));
            this.parseOwnerGroup(args.removeFirst());
        }
        
        protected void parseOwnerGroup(final String ownerStr) {
            final Matcher matcher = Chown.chownPattern.matcher(ownerStr);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("'" + ownerStr + "' does not match expected pattern for [owner][:group].");
            }
            this.owner = matcher.group(1);
            this.group = matcher.group(3);
            if (this.group != null && this.group.length() == 0) {
                this.group = null;
            }
            if (this.owner == null && this.group == null) {
                throw new IllegalArgumentException("'" + ownerStr + "' does not specify owner or group.");
            }
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            final String newOwner = (this.owner == null || this.owner.equals(item.stat.getOwner())) ? null : this.owner;
            final String newGroup = (this.group == null || this.group.equals(item.stat.getGroup())) ? null : this.group;
            if (newOwner == null) {
                if (newGroup == null) {
                    return;
                }
            }
            try {
                item.fs.setOwner(item.path, newOwner, newGroup);
            }
            catch (IOException e) {
                Chown.LOG.debug("Error changing ownership of " + item, e);
                throw new IOException("changing ownership of '" + item + "': " + e.getMessage());
            }
        }
        
        static {
            DESCRIPTION = "Changes owner and group of a file. This is similar to the shell's chown command with a few exceptions.\n-R: modifies the files recursively. This is the only option currently supported.\n\nIf only the owner or group is specified, then only the owner or group is modified. The owner and group names may only consist of digits, alphabet, and any of " + FsShellPermissions.allowedChars + ". The names are case sensitive.\n\nWARNING: Avoid using '.' to separate user name and group though Linux allows it. If user names have dots in them and you are using local file system, you might see surprising results since the shell command 'chown' is used for local files.";
            chownPattern = Pattern.compile("^\\s*(" + FsShellPermissions.allowedChars + "+)?([:](" + FsShellPermissions.allowedChars + "*))?\\s*$");
        }
    }
    
    public static class Chgrp extends Chown
    {
        public static final String NAME = "chgrp";
        public static final String USAGE = "[-R] GROUP PATH...";
        public static final String DESCRIPTION = "This is equivalent to -chown ... :GROUP ...";
        private static final Pattern chgrpPattern;
        
        @Override
        protected void parseOwnerGroup(final String groupStr) {
            final Matcher matcher = Chgrp.chgrpPattern.matcher(groupStr);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("'" + groupStr + "' does not match expected pattern for group");
            }
            this.owner = null;
            this.group = matcher.group(1);
        }
        
        static {
            chgrpPattern = Pattern.compile("^\\s*(" + FsShellPermissions.allowedChars + "+)\\s*$");
        }
    }
}
