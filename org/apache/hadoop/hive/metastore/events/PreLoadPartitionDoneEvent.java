// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import java.util.Map;

public class PreLoadPartitionDoneEvent extends PreEventContext
{
    private final String dbName;
    private final String tableName;
    private final Map<String, String> partSpec;
    
    public PreLoadPartitionDoneEvent(final String dbName, final String tableName, final Map<String, String> partSpec, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.LOAD_PARTITION_DONE, handler);
        this.dbName = dbName;
        this.tableName = tableName;
        this.partSpec = partSpec;
    }
    
    public String getDbName() {
        return this.dbName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public Map<String, String> getPartitionName() {
        return this.partSpec;
    }
}
