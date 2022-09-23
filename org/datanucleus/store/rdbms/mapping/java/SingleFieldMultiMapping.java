// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.MappingManager;

public abstract class SingleFieldMultiMapping extends JavaTypeMapping
{
    protected void addColumns(final String typeName) {
        final MappingManager mgr = this.storeMgr.getMappingManager();
        Column column = null;
        if (this.table != null) {
            column = mgr.createColumn(this, typeName, this.getNumberOfDatastoreMappings());
        }
        mgr.createDatastoreMapping(this, column, typeName);
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return this.datastoreMappings[index].getColumn().getStoredJavaType();
    }
    
    @Override
    public boolean hasSimpleDatastoreRepresentation() {
        return false;
    }
}
