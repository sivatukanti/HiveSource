// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.JoinMetaData;
import javax.jdo.metadata.JoinMetadata;
import org.datanucleus.metadata.DiscriminatorMetaData;
import javax.jdo.metadata.DiscriminatorMetadata;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.InheritanceMetaData;
import javax.jdo.metadata.InheritanceMetadata;

public class InheritanceMetadataImpl extends AbstractMetadataImpl implements InheritanceMetadata
{
    public InheritanceMetadataImpl(final InheritanceMetaData internal) {
        super(internal);
    }
    
    public InheritanceMetaData getInternal() {
        return (InheritanceMetaData)this.internalMD;
    }
    
    public String getCustomStrategy() {
        final InheritanceStrategy str = this.getInternal().getStrategy();
        if (str != InheritanceStrategy.NEW_TABLE && str != InheritanceStrategy.SUBCLASS_TABLE && str != InheritanceStrategy.SUPERCLASS_TABLE && str != null) {
            return str.toString();
        }
        return null;
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
    
    public JoinMetadata getJoinMetadata() {
        final JoinMetaData internalJoinmd = this.getInternal().getJoinMetaData();
        if (internalJoinmd == null) {
            return null;
        }
        final JoinMetadataImpl joinmd = new JoinMetadataImpl(internalJoinmd);
        joinmd.parent = this;
        return joinmd;
    }
    
    public javax.jdo.annotations.InheritanceStrategy getStrategy() {
        final InheritanceStrategy str = this.getInternal().getStrategy();
        if (str == InheritanceStrategy.NEW_TABLE) {
            return javax.jdo.annotations.InheritanceStrategy.NEW_TABLE;
        }
        if (str == InheritanceStrategy.SUBCLASS_TABLE) {
            return javax.jdo.annotations.InheritanceStrategy.SUBCLASS_TABLE;
        }
        if (str == InheritanceStrategy.SUPERCLASS_TABLE) {
            return javax.jdo.annotations.InheritanceStrategy.SUPERCLASS_TABLE;
        }
        return javax.jdo.annotations.InheritanceStrategy.UNSPECIFIED;
    }
    
    public DiscriminatorMetadata newDiscriminatorMetadata() {
        final DiscriminatorMetaData internalDismd = this.getInternal().newDiscriminatorMetadata();
        final DiscriminatorMetadataImpl dismd = new DiscriminatorMetadataImpl(internalDismd);
        dismd.parent = this;
        return dismd;
    }
    
    public JoinMetadata newJoinMetadata() {
        final JoinMetaData internalJoinmd = this.getInternal().newJoinMetadata();
        final JoinMetadataImpl joinmd = new JoinMetadataImpl(internalJoinmd);
        joinmd.parent = this;
        return joinmd;
    }
    
    public InheritanceMetadata setCustomStrategy(final String str) {
        this.getInternal().setStrategy(str);
        return this;
    }
    
    public InheritanceMetadata setStrategy(final javax.jdo.annotations.InheritanceStrategy str) {
        if (str == javax.jdo.annotations.InheritanceStrategy.NEW_TABLE) {
            this.getInternal().setStrategy(InheritanceStrategy.NEW_TABLE);
        }
        else if (str == javax.jdo.annotations.InheritanceStrategy.SUBCLASS_TABLE) {
            this.getInternal().setStrategy(InheritanceStrategy.SUBCLASS_TABLE);
        }
        else if (str == javax.jdo.annotations.InheritanceStrategy.SUPERCLASS_TABLE) {
            this.getInternal().setStrategy(InheritanceStrategy.SUPERCLASS_TABLE);
        }
        return this;
    }
}
