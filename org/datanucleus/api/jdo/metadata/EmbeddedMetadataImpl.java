// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.DiscriminatorMetaData;
import javax.jdo.metadata.DiscriminatorMetadata;
import javax.jdo.metadata.PropertyMetadata;
import javax.jdo.metadata.FieldMetadata;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.metadata.FieldMetaData;
import javax.jdo.metadata.MemberMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.EmbeddedMetaData;
import javax.jdo.metadata.EmbeddedMetadata;

public class EmbeddedMetadataImpl extends AbstractMetadataImpl implements EmbeddedMetadata
{
    public EmbeddedMetadataImpl(final EmbeddedMetaData internal) {
        super(internal);
    }
    
    public EmbeddedMetaData getInternal() {
        return (EmbeddedMetaData)this.internalMD;
    }
    
    public MemberMetadata[] getMembers() {
        final AbstractMemberMetaData[] internalMmds = this.getInternal().getMemberMetaData();
        if (internalMmds == null) {
            return null;
        }
        final MemberMetadataImpl[] mmds = new MemberMetadataImpl[internalMmds.length];
        for (int i = 0; i < mmds.length; ++i) {
            if (internalMmds[i] instanceof FieldMetaData) {
                mmds[i] = new FieldMetadataImpl((FieldMetaData)internalMmds[i]);
            }
            else {
                mmds[i] = new PropertyMetadataImpl((PropertyMetaData)internalMmds[i]);
            }
        }
        return mmds;
    }
    
    public String getNullIndicatorColumn() {
        return this.getInternal().getNullIndicatorColumn();
    }
    
    public String getNullIndicatorValue() {
        return this.getInternal().getNullIndicatorValue();
    }
    
    public int getNumberOfMembers() {
        final AbstractMemberMetaData[] mmds = this.getInternal().getMemberMetaData();
        return (mmds != null) ? mmds.length : 0;
    }
    
    public String getOwnerMember() {
        return this.getInternal().getOwnerMember();
    }
    
    public FieldMetadata newFieldMetadata(final String name) {
        final FieldMetaData internalFmd = this.getInternal().newFieldMetaData(name);
        final FieldMetadataImpl fmd = new FieldMetadataImpl(internalFmd);
        fmd.parent = this;
        return fmd;
    }
    
    public PropertyMetadata newPropertyMetadata(final String name) {
        final PropertyMetaData internalPmd = this.getInternal().newPropertyMetaData(name);
        final PropertyMetadataImpl pmd = new PropertyMetadataImpl(internalPmd);
        pmd.parent = this;
        return pmd;
    }
    
    public EmbeddedMetadata setNullIndicatorColumn(final String col) {
        this.getInternal().setNullIndicatorColumn(col);
        return this;
    }
    
    public EmbeddedMetadata setNullIndicatorValue(final String value) {
        this.getInternal().setNullIndicatorValue(value);
        return this;
    }
    
    public EmbeddedMetadata setOwnerMember(final String member) {
        this.getInternal().setOwnerMember(member);
        return this;
    }
    
    public DiscriminatorMetadata getDiscriminatorMetadata() {
        final DiscriminatorMetaData internalDismd = this.getInternal().getDiscriminatorMetaData();
        if (internalDismd == null) {
            return null;
        }
        final DiscriminatorMetadataImpl dismd = new DiscriminatorMetadataImpl(internalDismd);
        dismd.parent = this;
        return dismd;
    }
    
    public DiscriminatorMetadata newDiscriminatorMetadata() {
        final DiscriminatorMetaData internalDismd = this.getInternal().newDiscriminatorMetadata();
        final DiscriminatorMetadataImpl dismd = new DiscriminatorMetadataImpl(internalDismd);
        dismd.parent = this;
        return dismd;
    }
}
