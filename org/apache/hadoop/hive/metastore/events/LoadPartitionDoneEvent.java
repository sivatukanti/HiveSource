// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import java.util.Map;
import org.apache.hadoop.hive.metastore.api.Table;

public class LoadPartitionDoneEvent extends ListenerEvent
{
    private final Table table;
    private final Map<String, String> partSpec;
    
    public LoadPartitionDoneEvent(final boolean status, final Table table, final Map<String, String> partSpec, final HiveMetaStore.HMSHandler handler) {
        super(status, handler);
        this.table = table;
        this.partSpec = partSpec;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public Map<String, String> getPartitionName() {
        return this.partSpec;
    }
}
