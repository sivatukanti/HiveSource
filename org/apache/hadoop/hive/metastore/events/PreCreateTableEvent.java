// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Table;

public class PreCreateTableEvent extends PreEventContext
{
    private final Table table;
    
    public PreCreateTableEvent(final Table table, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.CREATE_TABLE, handler);
        this.table = table;
    }
    
    public Table getTable() {
        return this.table;
    }
}
