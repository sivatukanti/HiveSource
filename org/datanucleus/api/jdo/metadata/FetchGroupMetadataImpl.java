// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.metadata.PropertyMetadata;
import javax.jdo.metadata.FieldMetadata;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.metadata.FetchGroupMemberMetaData;
import javax.jdo.metadata.MemberMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.FetchGroupMetaData;
import javax.jdo.metadata.FetchGroupMetadata;

public class FetchGroupMetadataImpl extends AbstractMetadataImpl implements FetchGroupMetadata
{
    public FetchGroupMetadataImpl(final FetchGroupMetaData fgmd) {
        super(fgmd);
    }
    
    public FetchGroupMetaData getInternal() {
        return (FetchGroupMetaData)this.internalMD;
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public MemberMetadata[] getMembers() {
        final Set<FetchGroupMemberMetaData> internalMmds = this.getInternal().getMembers();
        if (internalMmds == null) {
            return null;
        }
        final MemberMetadataImpl[] mmds = new MemberMetadataImpl[internalMmds.size()];
        int i = 0;
        for (final FetchGroupMemberMetaData fgmmd : internalMmds) {
            if (fgmmd.isProperty()) {
                mmds[i] = new PropertyMetadataImpl(fgmmd);
            }
            else {
                mmds[i] = new FieldMetadataImpl(fgmmd);
            }
            mmds[i].parent = this;
            ++i;
        }
        return mmds;
    }
    
    public int getNumberOfMembers() {
        return this.getInternal().getNumberOfMembers();
    }
    
    public FieldMetadata newFieldMetadata(final String name) {
        final FetchGroupMemberMetaData internalFgMmd = this.getInternal().newMemberMetaData(name);
        final FieldMetadataImpl fmd = new FieldMetadataImpl(internalFgMmd);
        fmd.parent = this;
        return fmd;
    }
    
    public PropertyMetadata newPropertyMetadata(final String name) {
        final FetchGroupMemberMetaData internalFgMmd = this.getInternal().newMemberMetaData(name);
        final PropertyMetadataImpl pmd = new PropertyMetadataImpl(internalFgMmd);
        internalFgMmd.setProperty();
        pmd.parent = this;
        return pmd;
    }
    
    public Boolean getPostLoad() {
        return this.getInternal().getPostLoad();
    }
    
    public FetchGroupMetadata setPostLoad(final boolean load) {
        this.getInternal().setPostLoad(load);
        return this;
    }
}
