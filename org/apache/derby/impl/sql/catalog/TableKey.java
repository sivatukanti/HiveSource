// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.catalog.UUID;

final class TableKey
{
    private final String tableName;
    private final UUID schemaId;
    
    TableKey(final UUID schemaId, final String tableName) {
        this.tableName = tableName;
        this.schemaId = schemaId;
    }
    
    String getTableName() {
        return this.tableName;
    }
    
    UUID getSchemaId() {
        return this.schemaId;
    }
    
    public boolean equals(final Object o) {
        if (o instanceof TableKey) {
            final TableKey tableKey = (TableKey)o;
            if (this.tableName.equals(tableKey.tableName) && this.schemaId.equals(tableKey.schemaId)) {
                return true;
            }
        }
        return false;
    }
    
    public int hashCode() {
        return this.tableName.hashCode();
    }
}
