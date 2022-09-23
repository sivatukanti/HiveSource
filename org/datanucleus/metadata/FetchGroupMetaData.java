// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class FetchGroupMetaData extends MetaData
{
    boolean postLoad;
    final String name;
    protected Set<FetchGroupMetaData> fetchGroups;
    protected Set<FetchGroupMemberMetaData> members;
    
    public FetchGroupMetaData(final String name) {
        this.postLoad = false;
        this.fetchGroups = null;
        this.members = null;
        this.name = name;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final Boolean getPostLoad() {
        return this.postLoad;
    }
    
    public FetchGroupMetaData setPostLoad(final Boolean postLoad) {
        this.postLoad = postLoad;
        return this;
    }
    
    public final Set<FetchGroupMetaData> getFetchGroups() {
        return this.fetchGroups;
    }
    
    public final Set<FetchGroupMemberMetaData> getMembers() {
        return this.members;
    }
    
    public int getNumberOfMembers() {
        return (this.members != null) ? this.members.size() : 0;
    }
    
    public void addFetchGroup(final FetchGroupMetaData fgmd) {
        if (this.fetchGroups == null) {
            this.fetchGroups = new HashSet<FetchGroupMetaData>();
        }
        this.fetchGroups.add(fgmd);
        fgmd.parent = this;
    }
    
    public void addMember(final FetchGroupMemberMetaData fgmmd) {
        if (this.members == null) {
            this.members = new HashSet<FetchGroupMemberMetaData>();
        }
        this.members.add(fgmmd);
        fgmmd.parent = this;
    }
    
    public FetchGroupMemberMetaData newMemberMetaData(final String name) {
        final FetchGroupMemberMetaData fgmmd = new FetchGroupMemberMetaData(this, name);
        this.addMember(fgmmd);
        return fgmmd;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<fetch-group name=\"" + this.name + "\"\n");
        if (this.fetchGroups != null) {
            for (final FetchGroupMetaData fgmd : this.fetchGroups) {
                sb.append(fgmd.toString(prefix + indent, indent));
            }
        }
        if (this.members != null) {
            for (final FetchGroupMemberMetaData fgmmd : this.members) {
                sb.append(fgmmd.toString(prefix + indent, indent));
            }
        }
        sb.append(prefix + "</fetch-group>\n");
        return sb.toString();
    }
}
