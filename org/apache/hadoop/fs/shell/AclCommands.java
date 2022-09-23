// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.permission.AclEntryScope;
import com.google.common.collect.Lists;
import org.apache.hadoop.fs.permission.AclEntryType;
import java.util.Iterator;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.ScopedAclEntries;
import org.apache.hadoop.fs.permission.AclUtil;
import java.util.Collections;
import org.apache.hadoop.fs.permission.FsAction;
import java.io.IOException;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class AclCommands extends FsCommand
{
    private static String GET_FACL;
    private static String SET_FACL;
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(GetfaclCommand.class, "-" + AclCommands.GET_FACL);
        factory.addClass(SetfaclCommand.class, "-" + AclCommands.SET_FACL);
    }
    
    static {
        AclCommands.GET_FACL = "getfacl";
        AclCommands.SET_FACL = "setfacl";
    }
    
    public static class GetfaclCommand extends FsCommand
    {
        public static String NAME;
        public static String USAGE;
        public static String DESCRIPTION;
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            final CommandFormat cf = new CommandFormat(0, Integer.MAX_VALUE, new String[] { "R" });
            cf.parse(args);
            this.setRecursive(cf.getOpt("R"));
            if (args.isEmpty()) {
                throw new HadoopIllegalArgumentException("<path> is missing");
            }
            if (args.size() > 1) {
                throw new HadoopIllegalArgumentException("Too many arguments");
            }
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            this.out.println("# file: " + item);
            this.out.println("# owner: " + item.stat.getOwner());
            this.out.println("# group: " + item.stat.getGroup());
            final FsPermission perm = item.stat.getPermission();
            if (perm.getStickyBit()) {
                this.out.println("# flags: --" + (perm.getOtherAction().implies(FsAction.EXECUTE) ? "t" : "T"));
            }
            AclStatus aclStatus;
            List<AclEntry> entries;
            if (item.stat.hasAcl()) {
                aclStatus = item.fs.getAclStatus(item.path);
                entries = aclStatus.getEntries();
            }
            else {
                aclStatus = null;
                entries = Collections.emptyList();
            }
            final ScopedAclEntries scopedEntries = new ScopedAclEntries(AclUtil.getAclFromPermAndEntries(perm, entries));
            this.printAclEntriesForSingleScope(aclStatus, perm, scopedEntries.getAccessEntries());
            this.printAclEntriesForSingleScope(aclStatus, perm, scopedEntries.getDefaultEntries());
            this.out.println();
        }
        
        private void printAclEntriesForSingleScope(final AclStatus aclStatus, final FsPermission fsPerm, final List<AclEntry> entries) {
            if (entries.isEmpty()) {
                return;
            }
            if (AclUtil.isMinimalAcl(entries)) {
                for (final AclEntry entry : entries) {
                    this.out.println(entry.toStringStable());
                }
            }
            else {
                for (final AclEntry entry : entries) {
                    this.printExtendedAclEntry(aclStatus, fsPerm, entry);
                }
            }
        }
        
        private void printExtendedAclEntry(final AclStatus aclStatus, final FsPermission fsPerm, final AclEntry entry) {
            if (entry.getName() != null || entry.getType() == AclEntryType.GROUP) {
                final FsAction entryPerm = entry.getPermission();
                final FsAction effectivePerm = aclStatus.getEffectivePermission(entry, fsPerm);
                if (entryPerm != effectivePerm) {
                    this.out.println(String.format("%s\t#effective:%s", entry, effectivePerm.SYMBOL));
                }
                else {
                    this.out.println(entry.toStringStable());
                }
            }
            else {
                this.out.println(entry.toStringStable());
            }
        }
        
        static {
            GetfaclCommand.NAME = AclCommands.GET_FACL;
            GetfaclCommand.USAGE = "[-R] <path>";
            GetfaclCommand.DESCRIPTION = "Displays the Access Control Lists (ACLs) of files and directories. If a directory has a default ACL, then getfacl also displays the default ACL.\n  -R: List the ACLs of all files and directories recursively.\n  <path>: File or directory to list.\n";
        }
    }
    
    public static class SetfaclCommand extends FsCommand
    {
        public static String NAME;
        public static String USAGE;
        public static String DESCRIPTION;
        CommandFormat cf;
        List<AclEntry> aclEntries;
        List<AclEntry> accessAclEntries;
        
        public SetfaclCommand() {
            this.cf = new CommandFormat(0, Integer.MAX_VALUE, new String[] { "b", "k", "R", "m", "x", "-set" });
            this.aclEntries = null;
            this.accessAclEntries = null;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            this.cf.parse(args);
            this.setRecursive(this.cf.getOpt("R"));
            final boolean bothRemoveOptions = this.cf.getOpt("b") && this.cf.getOpt("k");
            final boolean bothModifyOptions = this.cf.getOpt("m") && this.cf.getOpt("x");
            final boolean oneRemoveOption = this.cf.getOpt("b") || this.cf.getOpt("k");
            final boolean oneModifyOption = this.cf.getOpt("m") || this.cf.getOpt("x");
            final boolean setOption = this.cf.getOpt("-set");
            final boolean hasExpectedOptions = this.cf.getOpt("b") || this.cf.getOpt("k") || this.cf.getOpt("m") || this.cf.getOpt("x") || this.cf.getOpt("-set");
            if (bothRemoveOptions || bothModifyOptions || (oneRemoveOption && oneModifyOption) || (setOption && (oneRemoveOption || oneModifyOption))) {
                throw new HadoopIllegalArgumentException("Specified flags contains both remove and modify flags");
            }
            if (oneModifyOption || setOption) {
                if (args.isEmpty()) {
                    throw new HadoopIllegalArgumentException("Missing arguments: <acl_spec> <path>");
                }
                if (args.size() < 2) {
                    throw new HadoopIllegalArgumentException("Missing either <acl_spec> or <path>");
                }
                this.aclEntries = AclEntry.parseAclSpec(args.removeFirst(), !this.cf.getOpt("x"));
                if (this.aclEntries.isEmpty()) {
                    throw new HadoopIllegalArgumentException("Missing <acl_spec> entry");
                }
            }
            if (args.isEmpty()) {
                throw new HadoopIllegalArgumentException("<path> is missing");
            }
            if (args.size() > 1) {
                throw new HadoopIllegalArgumentException("Too many arguments");
            }
            if (!hasExpectedOptions) {
                throw new HadoopIllegalArgumentException("Expected one of -b, -k, -m, -x or --set options");
            }
            if (this.isRecursive() && (oneModifyOption || setOption)) {
                this.accessAclEntries = (List<AclEntry>)Lists.newArrayList();
                for (final AclEntry entry : this.aclEntries) {
                    if (entry.getScope() == AclEntryScope.ACCESS) {
                        this.accessAclEntries.add(entry);
                    }
                }
            }
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (this.cf.getOpt("b")) {
                item.fs.removeAcl(item.path);
            }
            else if (this.cf.getOpt("k")) {
                item.fs.removeDefaultAcl(item.path);
            }
            else if (this.cf.getOpt("m")) {
                final List<AclEntry> entries = this.getAclEntries(item);
                if (!entries.isEmpty()) {
                    item.fs.modifyAclEntries(item.path, entries);
                }
            }
            else if (this.cf.getOpt("x")) {
                final List<AclEntry> entries = this.getAclEntries(item);
                if (!entries.isEmpty()) {
                    item.fs.removeAclEntries(item.path, entries);
                }
            }
            else if (this.cf.getOpt("-set")) {
                final List<AclEntry> entries = this.getAclEntries(item);
                if (!entries.isEmpty()) {
                    item.fs.setAcl(item.path, entries);
                }
            }
        }
        
        private List<AclEntry> getAclEntries(final PathData item) {
            if (this.isRecursive()) {
                return item.stat.isDirectory() ? this.aclEntries : this.accessAclEntries;
            }
            return this.aclEntries;
        }
        
        static {
            SetfaclCommand.NAME = AclCommands.SET_FACL;
            SetfaclCommand.USAGE = "[-R] [{-b|-k} {-m|-x <acl_spec>} <path>]|[--set <acl_spec> <path>]";
            SetfaclCommand.DESCRIPTION = "Sets Access Control Lists (ACLs) of files and directories.\nOptions:\n  -b :Remove all but the base ACL entries. The entries for user, group and others are retained for compatibility with permission bits.\n  -k :Remove the default ACL.\n  -R :Apply operations to all files and directories recursively.\n  -m :Modify ACL. New entries are added to the ACL, and existing entries are retained.\n  -x :Remove specified ACL entries. Other ACL entries are retained.\n  --set :Fully replace the ACL, discarding all existing entries. The <acl_spec> must include entries for user, group, and others for compatibility with permission bits.\n  <acl_spec>: Comma separated list of ACL entries.\n  <path>: File or directory to modify.\n";
        }
    }
}
