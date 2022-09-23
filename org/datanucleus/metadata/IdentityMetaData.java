// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;

public class IdentityMetaData extends MetaData
{
    protected String columnName;
    protected ColumnMetaData columnMetaData;
    protected IdentityStrategy strategy;
    protected String sequence;
    protected String valueGeneratorName;
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.strategy == null) {
            this.strategy = IdentityStrategy.NATIVE;
        }
        if (this.columnMetaData == null && this.columnName != null) {
            (this.columnMetaData = new ColumnMetaData()).setName(this.columnName);
            this.columnMetaData.parent = this;
            this.columnMetaData.initialise(clr, mmgr);
        }
        this.setInitialised();
    }
    
    public ColumnMetaData getColumnMetaData() {
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
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public IdentityMetaData setColumnName(final String columnName) {
        this.columnName = (StringUtils.isWhitespace(columnName) ? null : columnName);
        return this;
    }
    
    public IdentityStrategy getValueStrategy() {
        return this.strategy;
    }
    
    public IdentityMetaData setValueStrategy(final IdentityStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
    
    public String getSequence() {
        return this.sequence;
    }
    
    public IdentityMetaData setSequence(final String sequence) {
        this.sequence = (StringUtils.isWhitespace(sequence) ? null : sequence);
        if (this.sequence != null && this.strategy == null) {
            this.strategy = IdentityStrategy.SEQUENCE;
        }
        return this;
    }
    
    public String getValueGeneratorName() {
        return this.valueGeneratorName;
    }
    
    public IdentityMetaData setValueGeneratorName(final String generator) {
        if (StringUtils.isWhitespace(generator)) {
            this.valueGeneratorName = null;
        }
        else {
            this.valueGeneratorName = generator;
        }
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        if (this.strategy != null) {
            sb.append(prefix).append("<datastore-identity strategy=\"" + this.strategy + "\"");
        }
        else {
            sb.append(prefix).append("<datastore-identity");
        }
        if (this.columnName != null) {
            sb.append("\n").append(prefix).append("        column=\"" + this.columnName + "\"");
        }
        if (this.sequence != null) {
            sb.append("\n").append(prefix).append("        sequence=\"" + this.sequence + "\"");
        }
        if (this.columnMetaData != null || this.getNoOfExtensions() > 0) {
            sb.append(">\n");
            if (this.columnMetaData != null) {
                sb.append(this.columnMetaData.toString(prefix + indent, indent));
            }
            sb.append(super.toString(prefix + indent, indent));
            sb.append(prefix).append("</datastore-identity>\n");
        }
        else {
            sb.append("/>\n");
        }
        return sb.toString();
    }
}
