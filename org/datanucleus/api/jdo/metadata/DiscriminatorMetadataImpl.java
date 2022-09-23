// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.annotations.DiscriminatorStrategy;
import org.datanucleus.metadata.IndexedValue;
import javax.jdo.metadata.Indexed;
import org.datanucleus.metadata.IndexMetaData;
import javax.jdo.metadata.IndexMetadata;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.DiscriminatorMetaData;
import javax.jdo.metadata.DiscriminatorMetadata;

public class DiscriminatorMetadataImpl extends AbstractMetadataImpl implements DiscriminatorMetadata
{
    public DiscriminatorMetadataImpl(final DiscriminatorMetaData internal) {
        super(internal);
    }
    
    public DiscriminatorMetaData getInternal() {
        return (DiscriminatorMetaData)this.internalMD;
    }
    
    public String getColumn() {
        return this.getInternal().getColumnName();
    }
    
    public ColumnMetadata[] getColumns() {
        final ColumnMetaData internalColmd = this.getInternal().getColumnMetaData();
        if (internalColmd == null) {
            return null;
        }
        final ColumnMetadataImpl[] colmds = { new ColumnMetadataImpl(internalColmd) };
        colmds[0].parent = this;
        return colmds;
    }
    
    public IndexMetadata getIndexMetadata() {
        final IndexMetaData internalIdxmd = this.getInternal().getIndexMetaData();
        if (internalIdxmd == null) {
            return null;
        }
        final IndexMetadataImpl idxmd = new IndexMetadataImpl(internalIdxmd);
        idxmd.parent = this;
        return idxmd;
    }
    
    public Indexed getIndexed() {
        final IndexedValue val = this.getInternal().getIndexed();
        if (val == IndexedValue.FALSE) {
            return Indexed.FALSE;
        }
        if (val == IndexedValue.TRUE) {
            return Indexed.TRUE;
        }
        if (val == IndexedValue.UNIQUE) {
            return Indexed.UNIQUE;
        }
        return Indexed.UNSPECIFIED;
    }
    
    public int getNumberOfColumns() {
        return 1;
    }
    
    public DiscriminatorStrategy getStrategy() {
        final org.datanucleus.metadata.DiscriminatorStrategy str = this.getInternal().getStrategy();
        if (str == org.datanucleus.metadata.DiscriminatorStrategy.CLASS_NAME) {
            return DiscriminatorStrategy.CLASS_NAME;
        }
        if (str == org.datanucleus.metadata.DiscriminatorStrategy.VALUE_MAP) {
            return DiscriminatorStrategy.VALUE_MAP;
        }
        if (str == org.datanucleus.metadata.DiscriminatorStrategy.NONE) {
            return DiscriminatorStrategy.NONE;
        }
        return DiscriminatorStrategy.UNSPECIFIED;
    }
    
    public String getValue() {
        return this.getInternal().getValue();
    }
    
    public ColumnMetadata newColumnMetadata() {
        final ColumnMetaData internalColmd = this.getInternal().newColumnMetaData();
        final ColumnMetadataImpl colmd = new ColumnMetadataImpl(internalColmd);
        colmd.parent = this;
        return colmd;
    }
    
    public IndexMetadata newIndexMetadata() {
        final IndexMetaData internalIdxmd = this.getInternal().newIndexMetaData();
        final IndexMetadataImpl idxmd = new IndexMetadataImpl(internalIdxmd);
        idxmd.parent = this;
        return idxmd;
    }
    
    public DiscriminatorMetadata setColumn(final String name) {
        this.getInternal().setColumnName(name);
        return this;
    }
    
    public DiscriminatorMetadata setIndexed(final Indexed idx) {
        if (idx == Indexed.FALSE) {
            this.getInternal().setIndexed(IndexedValue.FALSE);
        }
        else if (idx == Indexed.TRUE) {
            this.getInternal().setIndexed(IndexedValue.TRUE);
        }
        else if (idx == Indexed.UNIQUE) {
            this.getInternal().setIndexed(IndexedValue.UNIQUE);
        }
        return this;
    }
    
    public DiscriminatorMetadata setStrategy(final DiscriminatorStrategy str) {
        if (str == DiscriminatorStrategy.CLASS_NAME) {
            this.getInternal().setStrategy(org.datanucleus.metadata.DiscriminatorStrategy.CLASS_NAME);
        }
        else if (str == DiscriminatorStrategy.VALUE_MAP) {
            this.getInternal().setStrategy(org.datanucleus.metadata.DiscriminatorStrategy.VALUE_MAP);
        }
        else if (str == DiscriminatorStrategy.NONE) {
            this.getInternal().setStrategy(org.datanucleus.metadata.DiscriminatorStrategy.NONE);
        }
        return this;
    }
    
    public DiscriminatorMetadata setValue(final String val) {
        this.getInternal().setValue(val);
        return this;
    }
}
