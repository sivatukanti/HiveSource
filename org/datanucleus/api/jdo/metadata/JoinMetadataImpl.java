// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.UniqueMetaData;
import javax.jdo.metadata.UniqueMetadata;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import javax.jdo.metadata.PrimaryKeyMetadata;
import org.datanucleus.metadata.IndexedValue;
import javax.jdo.metadata.Indexed;
import org.datanucleus.metadata.IndexMetaData;
import javax.jdo.metadata.IndexMetadata;
import javax.jdo.metadata.ForeignKeyMetadata;
import org.datanucleus.metadata.ForeignKeyMetaData;
import javax.jdo.annotations.ForeignKeyAction;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.JoinMetaData;
import javax.jdo.metadata.JoinMetadata;

public class JoinMetadataImpl extends AbstractMetadataImpl implements JoinMetadata
{
    public JoinMetadataImpl(final JoinMetaData internal) {
        super(internal);
    }
    
    public JoinMetaData getInternal() {
        return (JoinMetaData)this.internalMD;
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
    
    public ForeignKeyAction getDeleteAction() {
        final ForeignKeyMetaData fkmd = this.getInternal().getForeignKeyMetaData();
        if (fkmd != null) {
            final org.datanucleus.metadata.ForeignKeyAction fk = fkmd.getDeleteAction();
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
        }
        return ForeignKeyAction.UNSPECIFIED;
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
        final IndexedValue idxVal = this.getInternal().getIndexed();
        if (idxVal == IndexedValue.TRUE) {
            return Indexed.TRUE;
        }
        if (idxVal == IndexedValue.FALSE) {
            return Indexed.FALSE;
        }
        if (idxVal == IndexedValue.UNIQUE) {
            return Indexed.UNIQUE;
        }
        return Indexed.UNSPECIFIED;
    }
    
    public int getNumberOfColumns() {
        final ColumnMetaData[] colmds = this.getInternal().getColumnMetaData();
        return (colmds != null) ? colmds.length : 0;
    }
    
    public boolean getOuter() {
        return this.getInternal().isOuter();
    }
    
    public PrimaryKeyMetadata getPrimaryKeyMetadata() {
        final PrimaryKeyMetaData internalPkmd = this.getInternal().getPrimaryKeyMetaData();
        if (internalPkmd == null) {
            return null;
        }
        final PrimaryKeyMetadataImpl pkmd = new PrimaryKeyMetadataImpl(internalPkmd);
        pkmd.parent = this;
        return pkmd;
    }
    
    public String getTable() {
        return this.getInternal().getTable();
    }
    
    public Boolean getUnique() {
        return this.getInternal().isUnique();
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
    
    public ColumnMetadata newColumnMetadata() {
        final ColumnMetaData internalColmd = this.getInternal().newColumnMetaData();
        final ColumnMetadataImpl colmd = new ColumnMetadataImpl(internalColmd);
        colmd.parent = this;
        return colmd;
    }
    
    public ForeignKeyMetadata newForeignKeyMetadata() {
        final ForeignKeyMetaData internalFkmd = this.getInternal().newForeignKeyMetaData();
        final ForeignKeyMetadataImpl fkmd = new ForeignKeyMetadataImpl(internalFkmd);
        fkmd.parent = this;
        return fkmd;
    }
    
    public IndexMetadata newIndexMetadata() {
        final IndexMetaData internalIdxmd = this.getInternal().newIndexMetaData();
        final IndexMetadataImpl idxmd = new IndexMetadataImpl(internalIdxmd);
        idxmd.parent = this;
        return idxmd;
    }
    
    public PrimaryKeyMetadata newPrimaryKeyMetadata() {
        final PrimaryKeyMetaData internalPkmd = this.getInternal().newPrimaryKeyMetaData();
        final PrimaryKeyMetadataImpl pkmd = new PrimaryKeyMetadataImpl(internalPkmd);
        pkmd.parent = this;
        return pkmd;
    }
    
    public UniqueMetadata newUniqueMetadata() {
        final UniqueMetaData internalUnimd = this.getInternal().newUniqueMetaData();
        final UniqueMetadataImpl unimd = new UniqueMetadataImpl(internalUnimd);
        unimd.parent = this;
        return unimd;
    }
    
    public JoinMetadata setColumn(final String name) {
        this.getInternal().setColumnName(name);
        return this;
    }
    
    public JoinMetadata setDeleteAction(final ForeignKeyAction fk) {
        final ForeignKeyMetaData fkmd = this.getInternal().getForeignKeyMetaData();
        if (fk == ForeignKeyAction.CASCADE) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.CASCADE);
        }
        else if (fk == ForeignKeyAction.DEFAULT) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.DEFAULT);
        }
        else if (fk == ForeignKeyAction.NONE) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.NONE);
        }
        else if (fk == ForeignKeyAction.NULL) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.NULL);
        }
        else if (fk == ForeignKeyAction.RESTRICT) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.RESTRICT);
        }
        return this;
    }
    
    public JoinMetadata setIndexed(final Indexed val) {
        if (val == Indexed.TRUE) {
            this.getInternal().setIndexed(IndexedValue.TRUE);
        }
        else if (val == Indexed.FALSE) {
            this.getInternal().setIndexed(IndexedValue.FALSE);
        }
        else if (val == Indexed.UNIQUE) {
            this.getInternal().setIndexed(IndexedValue.UNIQUE);
        }
        return this;
    }
    
    public JoinMetadata setOuter(final boolean flag) {
        this.getInternal().setOuter(flag);
        return this;
    }
    
    public JoinMetadata setTable(final String table) {
        this.getInternal().setTable(table);
        return this;
    }
    
    public JoinMetadata setUnique(final boolean flag) {
        this.getInternal().setUnique(flag);
        return this;
    }
}
