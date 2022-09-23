// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.NucleusLogger;
import java.util.Date;
import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;

public class VersionMetaData extends MetaData
{
    private VersionStrategy versionStrategy;
    private String columnName;
    protected ColumnMetaData columnMetaData;
    protected IndexMetaData indexMetaData;
    protected IndexedValue indexed;
    protected String fieldName;
    
    public VersionMetaData() {
        this.indexed = null;
        this.fieldName = null;
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.hasExtension("field-name")) {
            final String val = this.getValueForExtension("field-name");
            if (!StringUtils.isWhitespace(val)) {
                this.fieldName = val;
                this.columnName = null;
            }
        }
        if (this.fieldName == null) {
            if (this.columnMetaData == null && this.columnName != null) {
                (this.columnMetaData = new ColumnMetaData()).setName(this.columnName);
                this.columnMetaData.parent = this;
                this.columnMetaData.initialise(clr, mmgr);
            }
            if (this.indexMetaData == null && this.columnMetaData != null && this.indexed != null && this.indexed != IndexedValue.FALSE) {
                (this.indexMetaData = new IndexMetaData()).setUnique(this.indexed == IndexedValue.UNIQUE);
                this.indexMetaData.addColumn(this.columnMetaData);
            }
            if (this.indexMetaData != null) {
                this.indexMetaData.initialise(clr, mmgr);
            }
        }
        else if (this.getParent() instanceof AbstractClassMetaData) {
            final AbstractMemberMetaData vermmd = ((AbstractClassMetaData)this.getParent()).getMetaDataForMember(this.fieldName);
            if (vermmd != null && Date.class.isAssignableFrom(vermmd.getType())) {
                NucleusLogger.GENERAL.debug("Setting version-strategy of field " + vermmd.getFullFieldName() + " to DATE_TIME since is Date-based");
                this.versionStrategy = VersionStrategy.DATE_TIME;
            }
        }
    }
    
    public final ColumnMetaData getColumnMetaData() {
        return this.columnMetaData;
    }
    
    public void setColumnMetaData(final ColumnMetaData columnMetaData) {
        this.columnMetaData = columnMetaData;
        this.columnMetaData.parent = this;
    }
    
    public ColumnMetaData newColumnMetaData() {
        final ColumnMetaData colmd = new ColumnMetaData();
        this.setColumnMetaData(colmd);
        return colmd;
    }
    
    public final VersionStrategy getVersionStrategy() {
        return this.versionStrategy;
    }
    
    public VersionMetaData setStrategy(final VersionStrategy strategy) {
        this.versionStrategy = strategy;
        return this;
    }
    
    public VersionMetaData setStrategy(final String strategy) {
        if (StringUtils.isWhitespace(strategy) || VersionStrategy.getVersionStrategy(strategy) == null) {
            throw new RuntimeException(VersionMetaData.LOCALISER.msg("044156"));
        }
        this.versionStrategy = VersionStrategy.getVersionStrategy(strategy);
        return this;
    }
    
    public final IndexMetaData getIndexMetaData() {
        return this.indexMetaData;
    }
    
    public final void setIndexMetaData(final IndexMetaData indexMetaData) {
        this.indexMetaData = indexMetaData;
    }
    
    public IndexMetaData newIndexMetaData() {
        final IndexMetaData idxmd = new IndexMetaData();
        this.setIndexMetaData(idxmd);
        return idxmd;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public VersionMetaData setColumnName(final String columnName) {
        this.columnName = (StringUtils.isWhitespace(columnName) ? null : columnName);
        return this;
    }
    
    public IndexedValue getIndexed() {
        return this.indexed;
    }
    
    public VersionMetaData setIndexed(final IndexedValue indexed) {
        this.indexed = indexed;
        return this;
    }
    
    public final String getFieldName() {
        return this.fieldName;
    }
    
    public VersionMetaData setFieldName(final String fieldName) {
        this.fieldName = fieldName;
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<version strategy=\"" + this.versionStrategy.toString() + "\"" + ((this.indexed != null) ? (" indexed=\"" + this.indexed.toString() + "\"") : ""));
        if (this.columnName != null && this.columnMetaData == null) {
            sb.append(" column=\"" + this.columnName + "\"");
        }
        sb.append(">\n");
        if (this.columnMetaData != null) {
            sb.append(this.columnMetaData.toString(prefix + indent, indent));
        }
        if (this.indexMetaData != null) {
            sb.append(this.indexMetaData.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</version>\n");
        return sb.toString();
    }
}
