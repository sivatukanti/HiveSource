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

public class AddPartitionEvent extends ListenerEvent
{
    private final Table table;
    private final List<Partition> partitions;
    private PartitionSpecProxy partitionSpecProxy;
    
    public AddPartitionEvent(final Table table, final List<Partition> partitions, final boolean status, final HiveMetaStore.HMSHandler handler) {
        super(status, handler);
        this.table = table;
        this.partitions = partitions;
        this.partitionSpecProxy = null;
    }
    
    public AddPartitionEvent(final Table table, final Partition partition, final boolean status, final HiveMetaStore.HMSHandler handler) {
        this(table, Arrays.asList(partition), status, handler);
    }
    
    public AddPartitionEvent(final Table table, final PartitionSpecProxy partitionSpec, final boolean status, final HiveMetaStore.HMSHandler handler) {
        super(status, handler);
        this.table = table;
        this.partitions = null;
        this.partitionSpecProxy = partitionSpec;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public Iterator<Partition> getPartitionIterator() {
        if (this.partitions != null) {
            return this.partitions.iterator();
        }
        return (this.partitionSpecProxy == null) ? null : this.partitionSpecProxy.getPartitionIterator();
    }
}
