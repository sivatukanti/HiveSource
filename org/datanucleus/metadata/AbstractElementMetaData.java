// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractElementMetaData extends MetaData implements ColumnMetaDataContainer
{
    protected boolean unique;
    protected String columnName;
    protected String mappedBy;
    protected IndexedValue indexed;
    protected IndexMetaData indexMetaData;
    protected UniqueMetaData uniqueMetaData;
    protected ForeignKeyMetaData foreignKeyMetaData;
    protected EmbeddedMetaData embeddedMetaData;
    protected final List<ColumnMetaData> columns;
    protected ColumnMetaData[] columnMetaData;
    
    public AbstractElementMetaData(final AbstractElementMetaData aemd) {
        super(null, aemd);
        this.indexed = null;
        this.columns = new ArrayList<ColumnMetaData>();
        this.columnName = aemd.columnName;
        this.unique = aemd.unique;
        this.indexed = aemd.indexed;
        this.mappedBy = aemd.mappedBy;
        if (aemd.indexMetaData != null) {
            this.setIndexMetaData(new IndexMetaData(aemd.indexMetaData));
        }
        if (aemd.uniqueMetaData != null) {
            this.setUniqueMetaData(new UniqueMetaData(aemd.uniqueMetaData));
        }
        if (aemd.foreignKeyMetaData != null) {
            this.setForeignKeyMetaData(new ForeignKeyMetaData(aemd.foreignKeyMetaData));
        }
        if (aemd.embeddedMetaData != null) {
            this.setEmbeddedMetaData(new EmbeddedMetaData(aemd.embeddedMetaData));
        }
        for (int i = 0; i < this.columns.size(); ++i) {
            this.addColumn(new ColumnMetaData(aemd.columns.get(i)));
        }
    }
    
    public AbstractElementMetaData() {
        this.indexed = null;
        this.columns = new ArrayList<ColumnMetaData>();
    }
    
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        if (this.embeddedMetaData != null) {
            this.embeddedMetaData.populate(clr, primary, mmgr);
        }
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.columns.size() == 0 && this.columnName != null) {
            this.columnMetaData = new ColumnMetaData[1];
            (this.columnMetaData[0] = new ColumnMetaData()).setName(this.columnName);
            this.columnMetaData[0].parent = this;
            this.columnMetaData[0].initialise(clr, mmgr);
        }
        else {
            this.columnMetaData = new ColumnMetaData[this.columns.size()];
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                (this.columnMetaData[i] = this.columns.get(i)).initialise(clr, mmgr);
            }
        }
        if (this.indexMetaData == null && this.columnMetaData != null && this.indexed != null && this.indexed != IndexedValue.FALSE) {
            (this.indexMetaData = new IndexMetaData()).setUnique(this.indexed == IndexedValue.UNIQUE);
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                this.indexMetaData.addColumn(this.columnMetaData[i]);
            }
        }
        if (this.indexMetaData != null) {
            this.indexMetaData.initialise(clr, mmgr);
        }
        if (this.uniqueMetaData == null && this.unique) {
            (this.uniqueMetaData = new UniqueMetaData()).setTable(this.columnName);
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                this.uniqueMetaData.addColumn(this.columnMetaData[i]);
            }
        }
        if (this.uniqueMetaData != null) {
            this.uniqueMetaData.initialise(clr, mmgr);
        }
        if (this.foreignKeyMetaData != null) {
            this.foreignKeyMetaData.initialise(clr, mmgr);
        }
        if (this.embeddedMetaData != null) {
            this.embeddedMetaData.initialise(clr, mmgr);
        }
        this.setInitialised();
    }
    
    public final String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = (StringUtils.isWhitespace(columnName) ? null : columnName);
    }
    
    public String getMappedBy() {
        return this.mappedBy;
    }
    
    public void setMappedBy(final String mappedBy) {
        this.mappedBy = (StringUtils.isWhitespace(mappedBy) ? null : mappedBy);
    }
    
    public IndexedValue getIndexed() {
        return this.indexed;
    }
    
    public void setIndexed(final IndexedValue indexed) {
        this.indexed = indexed;
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public void setUnique(final boolean unique) {
        this.unique = unique;
    }
    
    public ForeignKeyAction getDeleteAction() {
        if (this.foreignKeyMetaData != null) {
            return this.foreignKeyMetaData.getDeleteAction();
        }
        return null;
    }
    
    public void setDeleteAction(final String deleteAction) {
        if (!StringUtils.isWhitespace(deleteAction)) {
            (this.foreignKeyMetaData = new ForeignKeyMetaData()).setDeleteAction(ForeignKeyAction.getForeignKeyAction(deleteAction));
        }
    }
    
    public void setDeleteAction(final ForeignKeyAction deleteAction) {
        if (deleteAction != null) {
            (this.foreignKeyMetaData = new ForeignKeyMetaData()).setDeleteAction(deleteAction);
        }
    }
    
    public ForeignKeyAction getUpdateAction() {
        if (this.foreignKeyMetaData != null) {
            return this.foreignKeyMetaData.getUpdateAction();
        }
        return null;
    }
    
    public void setUpdateAction(final String updateAction) {
        if (!StringUtils.isWhitespace(updateAction)) {
            (this.foreignKeyMetaData = new ForeignKeyMetaData()).setUpdateAction(ForeignKeyAction.getForeignKeyAction(updateAction));
        }
    }
    
    public void setUpdateAction(final ForeignKeyAction updateAction) {
        if (updateAction != null) {
            (this.foreignKeyMetaData = new ForeignKeyMetaData()).setUpdateAction(updateAction);
        }
    }
    
    @Override
    public final ColumnMetaData[] getColumnMetaData() {
        return this.columnMetaData;
    }
    
    public final EmbeddedMetaData getEmbeddedMetaData() {
        return this.embeddedMetaData;
    }
    
    public final ForeignKeyMetaData getForeignKeyMetaData() {
        return this.foreignKeyMetaData;
    }
    
    public final IndexMetaData getIndexMetaData() {
        return this.indexMetaData;
    }
    
    public final UniqueMetaData getUniqueMetaData() {
        return this.uniqueMetaData;
    }
    
    @Override
    public void addColumn(final ColumnMetaData colmd) {
        this.columns.add(colmd);
        colmd.parent = this;
    }
    
    public ColumnMetaData newColumnMetaData() {
        final ColumnMetaData colmd = new ColumnMetaData();
        this.addColumn(colmd);
        return colmd;
    }
    
    public final void setEmbeddedMetaData(final EmbeddedMetaData embeddedMetaData) {
        this.embeddedMetaData = embeddedMetaData;
        embeddedMetaData.parent = this;
    }
    
    public EmbeddedMetaData newEmbeddedMetaData() {
        final EmbeddedMetaData embmd = new EmbeddedMetaData();
        this.setEmbeddedMetaData(embmd);
        return embmd;
    }
    
    public final void setForeignKeyMetaData(final ForeignKeyMetaData foreignKeyMetaData) {
        this.foreignKeyMetaData = foreignKeyMetaData;
        foreignKeyMetaData.parent = this;
    }
    
    public ForeignKeyMetaData newForeignKeyMetaData() {
        final ForeignKeyMetaData fkmd = new ForeignKeyMetaData();
        this.setForeignKeyMetaData(fkmd);
        return fkmd;
    }
    
    public final void setIndexMetaData(final IndexMetaData indexMetaData) {
        this.indexMetaData = indexMetaData;
        indexMetaData.parent = this;
    }
    
    public IndexMetaData newIndexMetaData() {
        final IndexMetaData idxmd = new IndexMetaData();
        this.setIndexMetaData(idxmd);
        return idxmd;
    }
    
    public final void setUniqueMetaData(final UniqueMetaData uniqueMetaData) {
        this.uniqueMetaData = uniqueMetaData;
        uniqueMetaData.parent = this;
    }
    
    public UniqueMetaData newUniqueMetaData() {
        final UniqueMetaData unimd = new UniqueMetaData();
        this.setUniqueMetaData(unimd);
        return unimd;
    }
}
