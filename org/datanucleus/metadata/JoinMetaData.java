// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import java.util.ArrayList;
import java.util.List;

public class JoinMetaData extends MetaData implements ColumnMetaDataContainer
{
    ForeignKeyMetaData foreignKeyMetaData;
    IndexMetaData indexMetaData;
    UniqueMetaData uniqueMetaData;
    protected PrimaryKeyMetaData primaryKeyMetaData;
    final List<ColumnMetaData> columns;
    boolean outer;
    String table;
    String catalog;
    String schema;
    String columnName;
    protected IndexedValue indexed;
    protected boolean unique;
    protected ColumnMetaData[] columnMetaData;
    
    public JoinMetaData(final JoinMetaData joinmd) {
        this.columns = new ArrayList<ColumnMetaData>();
        this.outer = false;
        this.indexed = null;
        this.table = joinmd.table;
        this.catalog = joinmd.catalog;
        this.schema = joinmd.schema;
        this.columnName = joinmd.columnName;
        this.outer = joinmd.outer;
        this.indexed = joinmd.indexed;
        this.unique = joinmd.unique;
        for (int i = 0; i < joinmd.columns.size(); ++i) {
            this.addColumn(new ColumnMetaData(joinmd.columns.get(i)));
        }
    }
    
    public JoinMetaData() {
        this.columns = new ArrayList<ColumnMetaData>();
        this.outer = false;
        this.indexed = null;
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.table != null && this.parent instanceof AbstractMemberMetaData) {
            final AbstractMemberMetaData mmd = (AbstractMemberMetaData)this.parent;
            throw new InvalidMemberMetaDataException(JoinMetaData.LOCALISER, "044130", mmd.getClassName(), mmd.getFullFieldName());
        }
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
        if (this.foreignKeyMetaData != null) {
            this.foreignKeyMetaData.initialise(clr, mmgr);
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
        if (this.primaryKeyMetaData != null) {
            this.primaryKeyMetaData.initialise(clr, mmgr);
        }
        this.setInitialised();
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
    
    public final boolean isOuter() {
        return this.outer;
    }
    
    public JoinMetaData setOuter(final boolean outer) {
        this.outer = outer;
        return this;
    }
    
    public String getDeleteAction() {
        if (this.foreignKeyMetaData != null) {
            return this.foreignKeyMetaData.getDeleteAction().toString();
        }
        return null;
    }
    
    public JoinMetaData setDeleteAction(final String deleteAction) {
        if (!StringUtils.isWhitespace(deleteAction)) {
            (this.foreignKeyMetaData = new ForeignKeyMetaData()).setDeleteAction(ForeignKeyAction.getForeignKeyAction(deleteAction));
        }
        return this;
    }
    
    public IndexedValue getIndexed() {
        return this.indexed;
    }
    
    public JoinMetaData setIndexed(final IndexedValue indexed) {
        if (indexed != null) {
            this.indexed = indexed;
        }
        return this;
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public JoinMetaData setUnique(final boolean unique) {
        this.unique = unique;
        return this;
    }
    
    public JoinMetaData setUnique(final String unique) {
        this.unique = MetaDataUtils.getBooleanForString(unique, false);
        return this;
    }
    
    public final String getTable() {
        return this.table;
    }
    
    public JoinMetaData setTable(final String table) {
        this.table = (StringUtils.isWhitespace(table) ? null : table);
        return this;
    }
    
    public final String getCatalog() {
        return this.catalog;
    }
    
    public JoinMetaData setCatalog(final String catalog) {
        this.catalog = (StringUtils.isWhitespace(catalog) ? null : catalog);
        return this;
    }
    
    public final String getSchema() {
        return this.schema;
    }
    
    public JoinMetaData setSchema(final String schema) {
        this.schema = (StringUtils.isWhitespace(schema) ? null : schema);
        return this;
    }
    
    public final String getColumnName() {
        return this.columnName;
    }
    
    public JoinMetaData setColumnName(final String columnName) {
        this.columnName = (StringUtils.isWhitespace(columnName) ? null : columnName);
        return this;
    }
    
    @Override
    public final ColumnMetaData[] getColumnMetaData() {
        return this.columnMetaData;
    }
    
    public final IndexMetaData getIndexMetaData() {
        return this.indexMetaData;
    }
    
    public final UniqueMetaData getUniqueMetaData() {
        return this.uniqueMetaData;
    }
    
    public final ForeignKeyMetaData getForeignKeyMetaData() {
        return this.foreignKeyMetaData;
    }
    
    public final PrimaryKeyMetaData getPrimaryKeyMetaData() {
        return this.primaryKeyMetaData;
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
    
    public final void setPrimaryKeyMetaData(final PrimaryKeyMetaData primaryKeyMetaData) {
        this.primaryKeyMetaData = primaryKeyMetaData;
        primaryKeyMetaData.parent = this;
    }
    
    public PrimaryKeyMetaData newPrimaryKeyMetaData() {
        final PrimaryKeyMetaData pkmd = new PrimaryKeyMetaData();
        this.setPrimaryKeyMetaData(pkmd);
        return pkmd;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<join");
        if (this.table != null) {
            sb.append(" table=\"" + this.table + "\"");
        }
        if (this.columnName != null) {
            sb.append(" column=\"" + this.columnName + "\"");
        }
        sb.append(" outer=\"" + this.outer + "\"");
        sb.append(">\n");
        if (this.primaryKeyMetaData != null) {
            sb.append(this.primaryKeyMetaData.toString(prefix + indent, indent));
        }
        if (this.columnMetaData != null) {
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                sb.append(this.columnMetaData[i].toString(prefix + indent, indent));
            }
        }
        if (this.foreignKeyMetaData != null) {
            sb.append(this.foreignKeyMetaData.toString(prefix + indent, indent));
        }
        if (this.indexMetaData != null) {
            sb.append(this.indexMetaData.toString(prefix + indent, indent));
        }
        if (this.uniqueMetaData != null) {
            sb.append(this.uniqueMetaData.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</join>\n");
        return sb.toString();
    }
}
