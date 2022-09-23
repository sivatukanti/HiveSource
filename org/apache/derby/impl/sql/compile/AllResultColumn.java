// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public class AllResultColumn extends ResultColumn
{
    private TableName tableName;
    
    public void init(final Object o) {
        this.tableName = (TableName)o;
    }
    
    public String getFullTableName() {
        if (this.tableName == null) {
            return null;
        }
        return this.tableName.getFullTableName();
    }
    
    ResultColumn cloneMe() throws StandardException {
        return (ResultColumn)this.getNodeFactory().getNode(16, this.tableName, this.getContextManager());
    }
    
    public TableName getTableNameObject() {
        return this.tableName;
    }
}
