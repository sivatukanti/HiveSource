// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import org.apache.hadoop.HadoopIllegalArgumentException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.util.StringUtils;
import com.google.common.base.Objects;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class AclEntry
{
    private final AclEntryType type;
    private final String name;
    private final FsAction permission;
    private final AclEntryScope scope;
    
    public AclEntryType getType() {
        return this.type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public FsAction getPermission() {
        return this.permission;
    }
    
    public AclEntryScope getScope() {
        return this.scope;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final AclEntry other = (AclEntry)o;
        return Objects.equal(this.type, other.type) && Objects.equal(this.name, other.name) && Objects.equal(this.permission, other.permission) && Objects.equal(this.scope, other.scope);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.type, this.name, this.permission, this.scope);
    }
    
    @InterfaceStability.Unstable
    @Override
    public String toString() {
        return this.toStringStable();
    }
    
    public String toStringStable() {
        final StringBuilder sb = new StringBuilder();
        if (this.scope == AclEntryScope.DEFAULT) {
            sb.append("default:");
        }
        if (this.type != null) {
            sb.append(StringUtils.toLowerCase(this.type.toStringStable()));
        }
        sb.append(':');
        if (this.name != null) {
            sb.append(this.name);
        }
        sb.append(':');
        if (this.permission != null) {
            sb.append(this.permission.SYMBOL);
        }
        return sb.toString();
    }
    
    private AclEntry(final AclEntryType type, final String name, final FsAction permission, final AclEntryScope scope) {
        this.type = type;
        this.name = name;
        this.permission = permission;
        this.scope = scope;
    }
    
    public static List<AclEntry> parseAclSpec(final String aclSpec, final boolean includePermission) {
        final List<AclEntry> aclEntries = new ArrayList<AclEntry>();
        final Collection<String> aclStrings = StringUtils.getStringCollection(aclSpec, ",");
        for (final String aclStr : aclStrings) {
            final AclEntry aclEntry = parseAclEntry(aclStr, includePermission);
            aclEntries.add(aclEntry);
        }
        return aclEntries;
    }
    
    public static AclEntry parseAclEntry(final String aclStr, final boolean includePermission) {
        final Builder builder = new Builder();
        final String[] split = aclStr.split(":");
        if (split.length == 0) {
            throw new HadoopIllegalArgumentException("Invalid <aclSpec> : " + aclStr);
        }
        int index = 0;
        if ("default".equals(split[0])) {
            ++index;
            builder.setScope(AclEntryScope.DEFAULT);
        }
        if (split.length <= index) {
            throw new HadoopIllegalArgumentException("Invalid <aclSpec> : " + aclStr);
        }
        AclEntryType aclType = null;
        try {
            aclType = Enum.valueOf(AclEntryType.class, StringUtils.toUpperCase(split[index]));
            builder.setType(aclType);
            ++index;
        }
        catch (IllegalArgumentException iae) {
            throw new HadoopIllegalArgumentException("Invalid type of acl in <aclSpec> :" + aclStr);
        }
        if (split.length > index) {
            final String name = split[index];
            if (!name.isEmpty()) {
                builder.setName(name);
            }
            ++index;
        }
        if (includePermission) {
            if (split.length <= index) {
                throw new HadoopIllegalArgumentException("Invalid <aclSpec> : " + aclStr);
            }
            final String permission = split[index];
            final FsAction fsAction = FsAction.getFsAction(permission);
            if (null == fsAction) {
                throw new HadoopIllegalArgumentException("Invalid permission in <aclSpec> : " + aclStr);
            }
            builder.setPermission(fsAction);
            ++index;
        }
        if (split.length > index) {
            throw new HadoopIllegalArgumentException("Invalid <aclSpec> : " + aclStr);
        }
        final AclEntry aclEntry = builder.build();
        return aclEntry;
    }
    
    public static String aclSpecToString(final List<AclEntry> aclSpec) {
        final StringBuilder buf = new StringBuilder();
        for (final AclEntry e : aclSpec) {
            buf.append(e.toString());
            buf.append(",");
        }
        return buf.substring(0, buf.length() - 1);
    }
    
    public static class Builder
    {
        private AclEntryType type;
        private String name;
        private FsAction permission;
        private AclEntryScope scope;
        
        public Builder() {
            this.scope = AclEntryScope.ACCESS;
        }
        
        public Builder setType(final AclEntryType type) {
            this.type = type;
            return this;
        }
        
        public Builder setName(final String name) {
            if (name != null && !name.isEmpty()) {
                this.name = name;
            }
            return this;
        }
        
        public Builder setPermission(final FsAction permission) {
            this.permission = permission;
            return this;
        }
        
        public Builder setScope(final AclEntryScope scope) {
            this.scope = scope;
            return this;
        }
        
        public AclEntry build() {
            return new AclEntry(this.type, this.name, this.permission, this.scope, null);
        }
    }
}
