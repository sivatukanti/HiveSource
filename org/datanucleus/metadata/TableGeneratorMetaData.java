// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;

public class TableGeneratorMetaData extends MetaData
{
    protected final String name;
    protected String tableName;
    protected String catalogName;
    protected String schemaName;
    protected String pkColumnName;
    protected String valueColumnName;
    protected String pkColumnValue;
    protected long initialValue;
    protected long allocationSize;
    
    TableGeneratorMetaData(final String name) {
        this.initialValue = 0L;
        this.allocationSize = 50L;
        if (StringUtils.isWhitespace(name)) {
            throw new InvalidMetaDataException(TableGeneratorMetaData.LOCALISER, "044155");
        }
        this.name = name;
    }
    
    public String getFullyQualifiedName() {
        final PackageMetaData pmd = (PackageMetaData)this.getParent();
        return pmd.getName() + "." + this.name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public TableGeneratorMetaData setTableName(final String tableName) {
        this.tableName = (StringUtils.isWhitespace(tableName) ? null : tableName);
        return this;
    }
    
    public String getCatalogName() {
        return this.catalogName;
    }
    
    public TableGeneratorMetaData setCatalogName(final String catalogName) {
        this.catalogName = (StringUtils.isWhitespace(catalogName) ? null : catalogName);
        return this;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public TableGeneratorMetaData setSchemaName(final String schemaName) {
        this.schemaName = (StringUtils.isWhitespace(schemaName) ? null : schemaName);
        return this;
    }
    
    public String getPKColumnName() {
        return this.pkColumnName;
    }
    
    public TableGeneratorMetaData setPKColumnName(final String pkColumnName) {
        this.pkColumnName = (StringUtils.isWhitespace(pkColumnName) ? null : pkColumnName);
        return this;
    }
    
    public String getValueColumnName() {
        return this.valueColumnName;
    }
    
    public TableGeneratorMetaData setValueColumnName(final String valueColumnName) {
        this.valueColumnName = (StringUtils.isWhitespace(valueColumnName) ? null : valueColumnName);
        return this;
    }
    
    public String getPKColumnValue() {
        return this.pkColumnValue;
    }
    
    public TableGeneratorMetaData setPKColumnValue(final String pkColumnValue) {
        this.pkColumnValue = (StringUtils.isWhitespace(pkColumnValue) ? null : pkColumnValue);
        return this;
    }
    
    public long getInitialValue() {
        return this.initialValue;
    }
    
    public TableGeneratorMetaData setInitialValue(final long initialValue) {
        this.initialValue = initialValue;
        return this;
    }
    
    public TableGeneratorMetaData setInitialValue(final String initialValue) {
        if (!StringUtils.isWhitespace(initialValue)) {
            try {
                this.initialValue = Integer.parseInt(initialValue);
            }
            catch (NumberFormatException ex) {}
        }
        return this;
    }
    
    public long getAllocationSize() {
        return this.allocationSize;
    }
    
    public TableGeneratorMetaData setAllocationSize(final long allocationSize) {
        this.allocationSize = allocationSize;
        return this;
    }
    
    public TableGeneratorMetaData setAllocationSize(final String allocationSize) {
        if (!StringUtils.isWhitespace(allocationSize)) {
            try {
                this.allocationSize = Integer.parseInt(allocationSize);
            }
            catch (NumberFormatException ex) {}
        }
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<table-generator name=\"" + this.name + "\"\n");
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix + "</table-generator>\n");
        return sb.toString();
    }
}
