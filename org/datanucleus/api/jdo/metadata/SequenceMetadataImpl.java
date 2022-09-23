// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.annotations.SequenceStrategy;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.SequenceMetaData;
import javax.jdo.metadata.SequenceMetadata;

public class SequenceMetadataImpl extends AbstractMetadataImpl implements SequenceMetadata
{
    public SequenceMetadataImpl(final SequenceMetaData internal) {
        super(internal);
    }
    
    public SequenceMetaData getInternal() {
        return (SequenceMetaData)this.internalMD;
    }
    
    public Integer getAllocationSize() {
        return this.getInternal().getAllocationSize();
    }
    
    public Integer getInitialValue() {
        return this.getInternal().getInitialValue();
    }
    
    public String getDatastoreSequence() {
        return this.getInternal().getDatastoreSequence();
    }
    
    public String getFactoryClass() {
        return this.getInternal().getFactoryClass();
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public SequenceStrategy getSequenceStrategy() {
        final org.datanucleus.metadata.SequenceStrategy strategy = this.getInternal().getStrategy();
        if (strategy == org.datanucleus.metadata.SequenceStrategy.CONTIGUOUS) {
            return SequenceStrategy.CONTIGUOUS;
        }
        if (strategy == org.datanucleus.metadata.SequenceStrategy.NONCONTIGUOUS) {
            return SequenceStrategy.NONCONTIGUOUS;
        }
        if (strategy == org.datanucleus.metadata.SequenceStrategy.NONTRANSACTIONAL) {
            return SequenceStrategy.NONTRANSACTIONAL;
        }
        return null;
    }
    
    public SequenceMetadata setAllocationSize(final int size) {
        this.getInternal().setAllocationSize(size);
        return this;
    }
    
    public SequenceMetadata setDatastoreSequence(final String seq) {
        this.getInternal().setDatastoreSequence(seq);
        return this;
    }
    
    public SequenceMetadata setFactoryClass(final String cls) {
        this.getInternal().setFactoryClass(cls);
        return this;
    }
    
    public SequenceMetadata setInitialValue(final int value) {
        this.getInternal().setInitialValue(value);
        return this;
    }
}
