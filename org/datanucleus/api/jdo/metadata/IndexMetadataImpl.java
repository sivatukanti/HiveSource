// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.PropertyMetaData;
import javax.jdo.metadata.PropertyMetadata;
import javax.jdo.metadata.FieldMetadata;
import org.datanucleus.metadata.FieldMetaData;
import javax.jdo.metadata.MemberMetadata;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.IndexMetaData;
import javax.jdo.metadata.IndexMetadata;

public class IndexMetadataImpl extends AbstractMetadataImpl implements IndexMetadata
{
    public IndexMetadataImpl(final IndexMetaData internal) {
        super(internal);
    }
    
    public IndexMetaData getInternal() {
        return (IndexMetaData)this.internalMD;
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
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public int getNumberOfColumns() {
        final ColumnMetaData[] colmds = this.getInternal().getColumnMetaData();
        return (colmds != null) ? colmds.length : 0;
    }
    
    public int getNumberOfMembers() {
        final String[] memberNames = this.getInternal().getMemberNames();
        return (memberNames != null) ? memberNames.length : 0;
    }
    
    public String getTable() {
        return this.getInternal().getTable();
    }
    
    public boolean getUnique() {
        return this.getInternal().isUnique();
    }
    
    public ColumnMetadata newColumn() {
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
    
    public IndexMetadata setName(final String name) {
        this.getInternal().setName(name);
        return this;
    }
    
    public IndexMetadata setTable(final String name) {
        this.getInternal().setTable(name);
        return this;
    }
    
    public IndexMetadata setUnique(final boolean flag) {
        this.getInternal().setUnique(flag);
        return this;
    }
}
