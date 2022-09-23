// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

public class TableName extends QueryTreeNode
{
    String tableName;
    String schemaName;
    private boolean hasSchema;
    
    public void init(final Object o, final Object o2) {
        this.hasSchema = (o != null);
        this.schemaName = (String)o;
        this.tableName = (String)o2;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) {
        this.init(o, o2);
        this.setBeginOffset((int)o3);
        this.setEndOffset((int)o4);
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public boolean hasSchema() {
        return this.hasSchema;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }
    
    public String getFullTableName() {
        if (this.schemaName != null) {
            return this.schemaName + "." + this.tableName;
        }
        return this.tableName;
    }
    
    public String toString() {
        if (this.hasSchema) {
            return this.getFullTableName();
        }
        return this.tableName;
    }
    
    public boolean equals(final TableName tableName) {
        if (tableName == null) {
            return false;
        }
        final String fullTableName = this.getFullTableName();
        if (fullTableName == null) {
            return true;
        }
        if (this.schemaName == null || tableName.getSchemaName() == null) {
            return this.tableName.equals(tableName.getTableName());
        }
        return fullTableName.equals(tableName.getFullTableName());
    }
    
    public boolean equals(final String str, final String s) {
        final String fullTableName = this.getFullTableName();
        if (fullTableName == null) {
            return true;
        }
        if (this.schemaName == null || str == null) {
            return this.tableName.equals(s);
        }
        return fullTableName.equals(str + "." + s);
    }
    
    public void bind(final DataDictionary dataDictionary) throws StandardException {
        this.schemaName = this.getSchemaDescriptor(this.schemaName).getSchemaName();
    }
    
    public int hashCode() {
        return this.getFullTableName().hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof TableName && this.getFullTableName().equals(((TableName)o).getFullTableName());
    }
}
