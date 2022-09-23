// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.annotations.VersionStrategy;
import org.datanucleus.metadata.IndexedValue;
import javax.jdo.metadata.Indexed;
import org.datanucleus.metadata.IndexMetaData;
import javax.jdo.metadata.IndexMetadata;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.VersionMetaData;
import javax.jdo.metadata.VersionMetadata;

public class VersionMetadataImpl extends AbstractMetadataImpl implements VersionMetadata
{
    public VersionMetadataImpl(final VersionMetaData internal) {
        super(internal);
    }
    
    public VersionMetaData getInternal() {
        return (VersionMetaData)this.internalMD;
    }
    
    public String getColumn() {
        return this.getInternal().getColumnName();
    }
    
    public ColumnMetadata[] getColumns() {
        final ColumnMetaData internalColmd = this.getInternal().getColumnMetaData();
        if (internalColmd == null) {
            return null;
        }
        final ColumnMetadataImpl[] colmds = { null };
        for (int i = 0; i < colmds.length; ++i) {
            colmds[i] = new ColumnMetadataImpl(internalColmd);
            colmds[i].parent = this;
        }
        return colmds;
    }
    
    public IndexMetadata getIndexMetadata() {
        final IndexMetaData internalIdxmd = this.getInternal().getIndexMetaData();
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
        final ColumnMetaData colmd = this.getInternal().getColumnMetaData();
        return (colmd != null) ? 1 : 0;
    }
    
    public VersionStrategy getStrategy() {
        final org.datanucleus.metadata.VersionStrategy strategy = this.getInternal().getVersionStrategy();
        if (strategy == org.datanucleus.metadata.VersionStrategy.DATE_TIME) {
            return VersionStrategy.DATE_TIME;
        }
        if (strategy == org.datanucleus.metadata.VersionStrategy.VERSION_NUMBER) {
            return VersionStrategy.VERSION_NUMBER;
        }
        if (strategy == org.datanucleus.metadata.VersionStrategy.STATE_IMAGE) {
            return VersionStrategy.STATE_IMAGE;
        }
        if (strategy == org.datanucleus.metadata.VersionStrategy.NONE) {
            return VersionStrategy.NONE;
        }
        return VersionStrategy.UNSPECIFIED;
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
    
    public VersionMetadata setColumn(final String name) {
        this.getInternal().setColumnName(name);
        return this;
    }
    
    public VersionMetadata setIndexed(final Indexed idx) {
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
    
    public VersionMetadata setStrategy(final VersionStrategy str) {
        if (str == VersionStrategy.DATE_TIME) {
            this.getInternal().setStrategy(org.datanucleus.metadata.VersionStrategy.DATE_TIME);
        }
        else if (str == VersionStrategy.VERSION_NUMBER) {
            this.getInternal().setStrategy(org.datanucleus.metadata.VersionStrategy.VERSION_NUMBER);
        }
        else if (str == VersionStrategy.STATE_IMAGE) {
            this.getInternal().setStrategy(org.datanucleus.metadata.VersionStrategy.STATE_IMAGE);
        }
        else if (str == VersionStrategy.NONE) {
            this.getInternal().setStrategy(org.datanucleus.metadata.VersionStrategy.NONE);
        }
        return this;
    }
}
