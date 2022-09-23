// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import javax.jdo.metadata.PrimaryKeyMetadata;

public class PrimaryKeyMetadataImpl extends AbstractMetadataImpl implements PrimaryKeyMetadata
{
    public PrimaryKeyMetadataImpl(final PrimaryKeyMetaData internal) {
        super(internal);
    }
    
    public PrimaryKeyMetaData getInternal() {
        return (PrimaryKeyMetaData)this.internalMD;
    }
    
    public String getColumn() {
        return this.getInternal().getColumnName();
    }
    
    public ColumnMetadata[] getColumns() {
        final ColumnMetaData[] internalColmds = this.getInternal().getColumnMetaData();
        if (internalColmds == null) {
            return null;
        }
        final ColumnMetadataImpl[] colmds = new ColumnMetadataImpl[internalColmds.length];
        for (int i = 0; i < colmds.length; ++i) {
            colmds[i] = new ColumnMetadataImpl(internalColmds[i]);
            colmds[i].parent = this;
        }
        return colmds;
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public int getNumberOfColumns() {
        final ColumnMetaData[] colmds = this.getInternal().getColumnMetaData();
        return (colmds != null) ? colmds.length : 0;
    }
    
    public ColumnMetadata newColumnMetadata() {
        final ColumnMetaData internalColmd = this.getInternal().newColumnMetadata();
        final ColumnMetadataImpl colmd = new ColumnMetadataImpl(internalColmd);
        colmd.parent = this;
        return colmd;
    }
    
    public PrimaryKeyMetadata setColumn(final String name) {
        this.getInternal().setColumnName(name);
        return this;
    }
    
    public PrimaryKeyMetadata setName(final String name) {
        this.getInternal().setName(name);
        return this;
    }
}
