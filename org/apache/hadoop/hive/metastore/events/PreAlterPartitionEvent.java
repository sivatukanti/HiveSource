// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Partition;
import java.util.List;

public class PreAlterPartitionEvent extends PreEventContext
{
    private final String dbName;
    private final String tableName;
    private final List<String> oldPartVals;
    private final Partition newPart;
    
    public PreAlterPartitionEvent(final String dbName, final String tableName, final List<String> oldPartVals, final Partition newPart, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.ALTER_PARTITION, handler);
        this.dbName = dbName;
        this.tableName = tableName;
        this.oldPartVals = oldPartVals;
        this.newPart = newPart;
    }
    
    public String getDbName() {
        return this.dbName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public List<String> getOldPartVals() {
        return this.oldPartVals;
    }
    
    public Partition getNewPartition() {
        return this.newPart;
    }
}
