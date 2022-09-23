// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Table;

public class PreDropTableEvent extends PreEventContext
{
    private final Table table;
    private final boolean deleteData;
    
    public PreDropTableEvent(final Table table, final boolean deleteData, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.DROP_TABLE, handler);
        this.table = table;
        this.deleteData = false;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public boolean getDeleteData() {
        return this.deleteData;
    }
}
