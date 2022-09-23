// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.catalog.UUID;

public abstract class IndexConstantAction extends DDLSingleTableConstantAction
{
    String indexName;
    String tableName;
    String schemaName;
    
    protected IndexConstantAction(final UUID uuid, final String indexName, final String tableName, final String schemaName) {
        super(uuid);
        this.indexName = indexName;
        this.tableName = tableName;
        this.schemaName = schemaName;
    }
    
    public String getIndexName() {
        return this.indexName;
    }
    
    public void setIndexName(final String indexName) {
        this.indexName = indexName;
    }
}
