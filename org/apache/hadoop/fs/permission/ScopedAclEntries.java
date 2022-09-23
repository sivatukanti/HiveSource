// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import java.util.Collections;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public final class ScopedAclEntries
{
    private static final int PIVOT_NOT_FOUND = -1;
    private final List<AclEntry> accessEntries;
    private final List<AclEntry> defaultEntries;
    
    public ScopedAclEntries(final List<AclEntry> aclEntries) {
        final int pivot = calculatePivotOnDefaultEntries(aclEntries);
        if (pivot != -1) {
            this.accessEntries = ((pivot != 0) ? aclEntries.subList(0, pivot) : Collections.emptyList());
            this.defaultEntries = aclEntries.subList(pivot, aclEntries.size());
        }
        else {
            this.accessEntries = aclEntries;
            this.defaultEntries = Collections.emptyList();
        }
    }
    
    public List<AclEntry> getAccessEntries() {
        return this.accessEntries;
    }
    
    public List<AclEntry> getDefaultEntries() {
        return this.defaultEntries;
    }
    
    private static int calculatePivotOnDefaultEntries(final List<AclEntry> aclBuilder) {
        for (int i = 0; i < aclBuilder.size(); ++i) {
            if (aclBuilder.get(i).getScope() == AclEntryScope.DEFAULT) {
                return i;
            }
        }
        return -1;
    }
}
