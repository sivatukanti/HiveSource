// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public final class AclUtil
{
    public static List<AclEntry> getAclFromPermAndEntries(final FsPermission perm, final List<AclEntry> entries) {
        final List<AclEntry> acl = (List<AclEntry>)Lists.newArrayListWithCapacity(entries.size() + 3);
        acl.add(new AclEntry.Builder().setScope(AclEntryScope.ACCESS).setType(AclEntryType.USER).setPermission(perm.getUserAction()).build());
        boolean hasAccessAcl = false;
        final Iterator<AclEntry> entryIter = entries.iterator();
        AclEntry curEntry = null;
        while (entryIter.hasNext()) {
            curEntry = entryIter.next();
            if (curEntry.getScope() == AclEntryScope.DEFAULT) {
                break;
            }
            hasAccessAcl = true;
            acl.add(curEntry);
        }
        acl.add(new AclEntry.Builder().setScope(AclEntryScope.ACCESS).setType(hasAccessAcl ? AclEntryType.MASK : AclEntryType.GROUP).setPermission(perm.getGroupAction()).build());
        acl.add(new AclEntry.Builder().setScope(AclEntryScope.ACCESS).setType(AclEntryType.OTHER).setPermission(perm.getOtherAction()).build());
        if (curEntry != null && curEntry.getScope() == AclEntryScope.DEFAULT) {
            acl.add(curEntry);
            while (entryIter.hasNext()) {
                acl.add(entryIter.next());
            }
        }
        return acl;
    }
    
    public static List<AclEntry> getMinimalAcl(final FsPermission perm) {
        return Lists.newArrayList(new AclEntry.Builder().setScope(AclEntryScope.ACCESS).setType(AclEntryType.USER).setPermission(perm.getUserAction()).build(), new AclEntry.Builder().setScope(AclEntryScope.ACCESS).setType(AclEntryType.GROUP).setPermission(perm.getGroupAction()).build(), new AclEntry.Builder().setScope(AclEntryScope.ACCESS).setType(AclEntryType.OTHER).setPermission(perm.getOtherAction()).build());
    }
    
    public static boolean isMinimalAcl(final List<AclEntry> entries) {
        return entries.size() == 3;
    }
    
    private AclUtil() {
    }
}
