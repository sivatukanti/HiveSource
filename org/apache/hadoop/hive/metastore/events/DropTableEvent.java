// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Table;

public class DropTableEvent extends ListenerEvent
{
    private final Table table;
    private final boolean deleteData;
    
    public DropTableEvent(final Table table, final boolean status, final boolean deleteData, final HiveMetaStore.HMSHandler handler) {
        super(status, handler);
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
