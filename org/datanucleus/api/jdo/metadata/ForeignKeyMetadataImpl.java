// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.PropertyMetaData;
import javax.jdo.metadata.PropertyMetadata;
import javax.jdo.metadata.FieldMetadata;
import org.datanucleus.metadata.FieldMetaData;
import javax.jdo.metadata.MemberMetadata;
import javax.jdo.annotations.ForeignKeyAction;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.ForeignKeyMetaData;
import javax.jdo.metadata.ForeignKeyMetadata;

public class ForeignKeyMetadataImpl extends AbstractMetadataImpl implements ForeignKeyMetadata
{
    public ForeignKeyMetadataImpl(final ForeignKeyMetaData internal) {
        super(internal);
    }
    
    public ForeignKeyMetaData getInternal() {
        return (ForeignKeyMetaData)this.internalMD;
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
    
    public Boolean getDeferred() {
        return this.getInternal().isDeferred();
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
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public int getNumberOfColumns() {
        final ColumnMetaData[] colmds = this.getInternal().getColumnMetaData();
        return (colmds != null) ? colmds.length : 0;
    }
    
    public int getNumberOfMembers() {
        final String[] internalMemberNames = this.getInternal().getMemberNames();
        return (internalMemberNames != null) ? internalMemberNames.length : 0;
    }
    
    public MemberMetadata[] getMembers() {
        final String[] internalMemberNames = this.getInternal().getMemberNames();
        if (internalMemberNames == null) {
            return null;
        }
        final MemberMetadataImpl[] mmds = new MemberMetadataImpl[internalMemberNames.length];
        for (int i = 0; i < mmds.length; ++i) {
            final FieldMetaData fmd = new FieldMetaData(this.getInternal(), internalMemberNames[i]);
            mmds[i] = new FieldMetadataImpl(fmd);
            mmds[i].parent = this;
        }
        return mmds;
    }
    
    public String getTable() {
        return this.getInternal().getTable();
    }
    
    public Boolean getUnique() {
        return this.getInternal().isUnique();
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
    
    public ColumnMetadata newColumnMetadata() {
        final ColumnMetaData internalColmd = this.getInternal().newColumnMetaData();
        final ColumnMetadataImpl colmd = new ColumnMetadataImpl(internalColmd);
        colmd.parent = this;
        return colmd;
    }
    
    public FieldMetadata newFieldMetadata(final String name) {
        final FieldMetaData internalFmd = new FieldMetaData(this.getInternal(), name);
        final FieldMetadataImpl fmd = new FieldMetadataImpl(internalFmd);
        fmd.parent = this;
        return fmd;
    }
    
    public PropertyMetadata newPropertyMetadata(final String name) {
        final PropertyMetaData internalPmd = new PropertyMetaData(this.getInternal(), name);
        final PropertyMetadataImpl pmd = new PropertyMetadataImpl(internalPmd);
        pmd.parent = this;
        return pmd;
    }
    
    public ForeignKeyMetadata setDeferred(final boolean flag) {
        this.getInternal().setDeferred(flag);
        return this;
    }
    
    public ForeignKeyMetadata setDeleteAction(final ForeignKeyAction fk) {
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
    
    public ForeignKeyMetadata setName(final String name) {
        this.getInternal().setName(name);
        return this;
    }
    
    public ForeignKeyMetadata setTable(final String name) {
        this.getInternal().setTable(name);
        return this;
    }
    
    public ForeignKeyMetadata setUnique(final boolean flag) {
        this.getInternal().setUnique(flag);
        return this;
    }
    
    public ForeignKeyMetadata setUpdateAction(final ForeignKeyAction fk) {
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
}
