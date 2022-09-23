// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import java.util.Iterator;
import java.util.Arrays;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.hadoop.hive.metastore.api.Partition;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.Table;

public class PreAddPartitionEvent extends PreEventContext
{
    private final Table table;
    private final List<Partition> partitions;
    private PartitionSpecProxy partitionSpecProxy;
    
    public PreAddPartitionEvent(final Table table, final List<Partition> partitions, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.ADD_PARTITION, handler);
        this.table = table;
        this.partitions = partitions;
        this.partitionSpecProxy = null;
    }
    
    public PreAddPartitionEvent(final Table table, final Partition partition, final HiveMetaStore.HMSHandler handler) {
        this(table, Arrays.asList(partition), handler);
    }
    
    public PreAddPartitionEvent(final Table table, final PartitionSpecProxy partitionSpecProxy, final HiveMetaStore.HMSHandler handler) {
        this(table, (List<Partition>)null, handler);
        this.partitionSpecProxy = partitionSpecProxy;
    }
    
    public List<Partition> getPartitions() {
        return this.partitions;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public Iterator<Partition> getPartitionIterator() {
        return (this.partitionSpecProxy == null) ? null : this.partitionSpecProxy.getPartitionIterator();
    }
}
