// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.MappingManager;

public class SerialisedElementPCMapping extends SerialisedPCMapping
{
    @Override
    protected void prepareDatastoreMapping() {
        final MappingManager mmgr = this.storeMgr.getMappingManager();
        ColumnMetaData colmd = null;
        if (this.mmd.getElementMetaData() != null && this.mmd.getElementMetaData().getColumnMetaData() != null && this.mmd.getElementMetaData().getColumnMetaData().length > 0) {
            colmd = this.mmd.getElementMetaData().getColumnMetaData()[0];
        }
        final Column col = mmgr.createColumn(this, this.getType(), colmd);
        mmgr.createDatastoreMapping(this, this.mmd, 0, col);
    }
}
