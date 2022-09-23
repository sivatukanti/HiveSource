// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Table;

public class PreAlterTableEvent extends PreEventContext
{
    private final Table newTable;
    private final Table oldTable;
    
    public PreAlterTableEvent(final Table oldTable, final Table newTable, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.ALTER_TABLE, handler);
        this.oldTable = oldTable;
        this.newTable = newTable;
    }
    
    public Table getOldTable() {
        return this.oldTable;
    }
    
    public Table getNewTable() {
        return this.newTable;
    }
}
