// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import java.util.Iterator;
import java.util.Collections;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.Partition;

public class PreDropPartitionEvent extends PreEventContext
{
    private final Iterable<Partition> partitions;
    private final Table table;
    private final boolean deleteData;
    
    public PreDropPartitionEvent(final Table table, final Partition partition, final boolean deleteData, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.DROP_PARTITION, handler);
        this.partitions = Collections.singletonList(partition);
        this.table = table;
        this.deleteData = false;
    }
    
    public Iterator<Partition> getPartitionIterator() {
        return this.partitions.iterator();
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public boolean getDeleteData() {
        return this.deleteData;
    }
}
