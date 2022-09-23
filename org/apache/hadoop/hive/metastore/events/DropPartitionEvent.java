// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import java.util.Iterator;
import java.util.Collections;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;

public class DropPartitionEvent extends ListenerEvent
{
    private final Table table;
    private final Iterable<Partition> partitions;
    private final boolean deleteData;
    
    public DropPartitionEvent(final Table table, final Partition partition, final boolean status, final boolean deleteData, final HiveMetaStore.HMSHandler handler) {
        super(status, handler);
        this.table = table;
        this.partitions = Collections.singletonList(partition);
        this.deleteData = deleteData;
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
