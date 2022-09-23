// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.MappingManager;

public class SerialisedKeyPCMapping extends SerialisedPCMapping
{
    @Override
    protected void prepareDatastoreMapping() {
        final MappingManager mmgr = this.storeMgr.getMappingManager();
        ColumnMetaData colmd = null;
        if (this.mmd.getKeyMetaData() != null && this.mmd.getKeyMetaData().getColumnMetaData() != null && this.mmd.getKeyMetaData().getColumnMetaData().length > 0) {
            colmd = this.mmd.getKeyMetaData().getColumnMetaData()[0];
        }
        final Column col = mmgr.createColumn(this, this.getType(), colmd);
        mmgr.createDatastoreMapping(this, this.mmd, 0, col);
    }
}
