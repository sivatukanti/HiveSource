// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.EmbeddedMetaData;
import javax.jdo.metadata.EmbeddedMetadata;
import org.datanucleus.metadata.UniqueMetaData;
import javax.jdo.metadata.UniqueMetadata;
import org.datanucleus.metadata.IndexMetaData;
import javax.jdo.metadata.IndexMetadata;
import org.datanucleus.metadata.ForeignKeyMetaData;
import javax.jdo.metadata.ForeignKeyMetadata;
import javax.jdo.annotations.ForeignKeyAction;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.KeyMetaData;
import javax.jdo.metadata.KeyMetadata;

public class KeyMetadataImpl extends AbstractMetadataImpl implements KeyMetadata
{
    public KeyMetadataImpl(final KeyMetaData internal) {
        super(internal);
    }
    
    public KeyMetaData getInternal() {
        return (KeyMetaData)this.internalMD;
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
    
    public int getNumberOfColumns() {
        final ColumnMetaData[] colmds = this.getInternal().getColumnMetaData();
        return (colmds != null) ? colmds.length : 0;
    }
    
    public KeyMetadata setColumn(final String name) {
        this.getInternal().setColumnName(name);
        return this;
    }
    
    public ColumnMetadata newColumnMetadata() {
        final ColumnMetaData internalColmd = this.getInternal().newColumnMetaData();
        final ColumnMetadataImpl colmd = new ColumnMetadataImpl(internalColmd);
        colmd.parent = this;
        return colmd;
    }
    
    public ForeignKeyAction getDeleteAction() {
        final org.datanucleus.metadata.ForeignKeyAction fk = this.getInternal().getDeleteAction();
        if (fk == org.datanucleus.metadata.ForeignKeyAction.CASCADE) {
            return ForeignKeyAction.CASCADE;
        }
        if (fk == org.datanucleus.metadata.ForeignKeyAction.DEFAULT) {
            return ForeignKeyAction.DEFAULT;
        }
        if (fk == org.datanucleus.metadata.ForeignKeyAction.NONE) {
            return ForeignKeyAction.NONE;
        }
        if (fk == org.datanucleus.metadata.ForeignKeyAction.NULL) {
            return ForeignKeyAction.NULL;
        }
        if (fk == org.datanucleus.metadata.ForeignKeyAction.RESTRICT) {
            return ForeignKeyAction.RESTRICT;
        }
        return ForeignKeyAction.UNSPECIFIED;
    }
    
    public KeyMetadata setDeleteAction(final ForeignKeyAction fk) {
        if (fk == ForeignKeyAction.CASCADE) {
            this.getInternal().setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.CASCADE);
        }
        else if (fk == ForeignKeyAction.DEFAULT) {
            this.getInternal().setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.DEFAULT);
        }
        else if (fk == ForeignKeyAction.NONE) {
            this.getInternal().setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.NONE);
        }
        else if (fk == ForeignKeyAction.NULL) {
            this.getInternal().setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.NULL);
        }
        else if (fk == ForeignKeyAction.RESTRICT) {
            this.getInternal().setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.RESTRICT);
        }
        return this;
    }
    
    public ForeignKeyAction getUpdateAction() {
        final org.datanucleus.metadata.ForeignKeyAction fk = this.getInternal().getUpdateAction();
        if (fk == org.datanucleus.metadata.ForeignKeyAction.CASCADE) {
            return ForeignKeyAction.CASCADE;
        }
        if (fk == org.datanucleus.metadata.ForeignKeyAction.DEFAULT) {
            return ForeignKeyAction.DEFAULT;
        }
        if (fk == org.datanucleus.metadata.ForeignKeyAction.NONE) {
            return ForeignKeyAction.NONE;
        }
        if (fk == org.datanucleus.metadata.ForeignKeyAction.NULL) {
            return ForeignKeyAction.NULL;
        }
        if (fk == org.datanucleus.metadata.ForeignKeyAction.RESTRICT) {
            return ForeignKeyAction.RESTRICT;
        }
        return ForeignKeyAction.UNSPECIFIED;
    }
    
    public KeyMetadata setUpdateAction(final ForeignKeyAction fk) {
        if (fk == ForeignKeyAction.CASCADE) {
            this.getInternal().setUpdateAction(org.datanucleus.metadata.ForeignKeyAction.CASCADE);
        }
        else if (fk == ForeignKeyAction.DEFAULT) {
            this.getInternal().setUpdateAction(org.datanucleus.metadata.ForeignKeyAction.DEFAULT);
        }
        else if (fk == ForeignKeyAction.NONE) {
            this.getInternal().setUpdateAction(org.datanucleus.metadata.ForeignKeyAction.NONE);
        }
        else if (fk == ForeignKeyAction.NULL) {
            this.getInternal().setUpdateAction(org.datanucleus.metadata.ForeignKeyAction.NULL);
        }
        else if (fk == ForeignKeyAction.RESTRICT) {
            this.getInternal().setUpdateAction(org.datanucleus.metadata.ForeignKeyAction.RESTRICT);
        }
        return this;
    }
    
    public ForeignKeyMetadata getForeignKeyMetadata() {
        final ForeignKeyMetaData internalFkmd = this.getInternal().getForeignKeyMetaData();
        if (internalFkmd == null) {
            return null;
        }
        final ForeignKeyMetadataImpl fkmd = new ForeignKeyMetadataImpl(internalFkmd);
        fkmd.parent = this;
        return fkmd;
    }
    
    public ForeignKeyMetadata newForeignKeyMetadata() {
        final ForeignKeyMetaData internalFkmd = this.getInternal().newForeignKeyMetaData();
        final ForeignKeyMetadataImpl fkmd = new ForeignKeyMetadataImpl(internalFkmd);
        fkmd.parent = this;
        return fkmd;
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
    
    public IndexMetadata newIndexMetadata() {
        final IndexMetaData internalIdxmd = this.getInternal().newIndexMetaData();
        final IndexMetadataImpl idxmd = new IndexMetadataImpl(internalIdxmd);
        idxmd.parent = this;
        return idxmd;
    }
    
    public UniqueMetadata getUniqueMetadata() {
        final UniqueMetaData internalUnimd = this.getInternal().getUniqueMetaData();
        if (internalUnimd == null) {
            return null;
        }
        final UniqueMetadataImpl unimd = new UniqueMetadataImpl(internalUnimd);
        unimd.parent = this;
        return unimd;
    }
    
    public UniqueMetadata newUniqueMetadata() {
        final UniqueMetaData internalUnimd = this.getInternal().newUniqueMetaData();
        final UniqueMetadataImpl unimd = new UniqueMetadataImpl(internalUnimd);
        unimd.parent = this;
        return unimd;
    }
    
    public EmbeddedMetadata getEmbeddedMetadata() {
        final EmbeddedMetaData internalEmbmd = this.getInternal().getEmbeddedMetaData();
        if (internalEmbmd == null) {
            return null;
        }
        final EmbeddedMetadataImpl embmd = new EmbeddedMetadataImpl(internalEmbmd);
        embmd.parent = this;
        return embmd;
    }
    
    public EmbeddedMetadata newEmbeddedMetadata() {
        final EmbeddedMetaData internalEmbmd = this.getInternal().newEmbeddedMetaData();
        final EmbeddedMetadataImpl embmd = new EmbeddedMetadataImpl(internalEmbmd);
        embmd.parent = this;
        return embmd;
    }
    
    public String getTable() {
        return null;
    }
    
    public KeyMetadata setTable(final String name) {
        return null;
    }
}
