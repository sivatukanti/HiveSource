// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Table;

public class CreateTableEvent extends ListenerEvent
{
    private final Table table;
    
    public CreateTableEvent(final Table table, final boolean status, final HiveMetaStore.HMSHandler handler) {
        super(status, handler);
        this.table = table;
    }
    
    public Table getTable() {
        return this.table;
    }
}
