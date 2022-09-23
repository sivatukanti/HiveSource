// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.base.Objects;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class AclStatus
{
    private final String owner;
    private final String group;
    private final boolean stickyBit;
    private final List<AclEntry> entries;
    private final FsPermission permission;
    
    public String getOwner() {
        return this.owner;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public boolean isStickyBit() {
        return this.stickyBit;
    }
    
    public List<AclEntry> getEntries() {
        return this.entries;
    }
    
    public FsPermission getPermission() {
        return this.permission;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final AclStatus other = (AclStatus)o;
        return Objects.equal(this.owner, other.owner) && Objects.equal(this.group, other.group) && this.stickyBit == other.stickyBit && Objects.equal(this.entries, other.entries);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.owner, this.group, this.stickyBit, this.entries);
    }
    
    @Override
    public String toString() {
        return "owner: " + this.owner + ", group: " + this.group + ", acl: {" + "entries: " + this.entries + ", stickyBit: " + this.stickyBit + '}';
    }
    
    private AclStatus(final String owner, final String group, final boolean stickyBit, final Iterable<AclEntry> entries, final FsPermission permission) {
        this.owner = owner;
        this.group = group;
        this.stickyBit = stickyBit;
        this.entries = (List<AclEntry>)Lists.newArrayList((Iterable<?>)entries);
        this.permission = permission;
    }
    
    public FsAction getEffectivePermission(final AclEntry entry) {
        return this.getEffectivePermission(entry, this.permission);
    }
    
    public FsAction getEffectivePermission(final AclEntry entry, FsPermission permArg) throws IllegalArgumentException {
        Preconditions.checkArgument(this.permission != null || permArg != null, (Object)"Permission bits are not available to calculate effective permission");
        if (this.permission != null) {
            permArg = this.permission;
        }
        if (entry.getName() == null && entry.getType() != AclEntryType.GROUP) {
            return entry.getPermission();
        }
        if (entry.getScope() == AclEntryScope.ACCESS) {
            final FsAction entryPerm = entry.getPermission();
            return entryPerm.and(permArg.getGroupAction());
        }
        Preconditions.checkArgument(this.entries.contains(entry) && this.entries.size() >= 3, (Object)"Passed default ACL entry not found in the list of ACLs");
        final FsAction defaultMask = this.entries.get(this.entries.size() - 2).getPermission();
        final FsAction entryPerm2 = entry.getPermission();
        return entryPerm2.and(defaultMask);
    }
    
    public static class Builder
    {
        private String owner;
        private String group;
        private boolean stickyBit;
        private List<AclEntry> entries;
        private FsPermission permission;
        
        public Builder() {
            this.entries = (List<AclEntry>)Lists.newArrayList();
            this.permission = null;
        }
        
        public Builder owner(final String owner) {
            this.owner = owner;
            return this;
        }
        
        public Builder group(final String group) {
            this.group = group;
            return this;
        }
        
        public Builder addEntry(final AclEntry e) {
            this.entries.add(e);
            return this;
        }
        
        public Builder addEntries(final Iterable<AclEntry> entries) {
            for (final AclEntry e : entries) {
                this.entries.add(e);
            }
            return this;
        }
        
        public Builder stickyBit(final boolean stickyBit) {
            this.stickyBit = stickyBit;
            return this;
        }
        
        public Builder setPermission(final FsPermission permission) {
            this.permission = permission;
            return this;
        }
        
        public AclStatus build() {
            return new AclStatus(this.owner, this.group, this.stickyBit, this.entries, this.permission, null);
        }
    }
}
