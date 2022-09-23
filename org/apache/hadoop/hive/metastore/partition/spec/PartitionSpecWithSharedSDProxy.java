// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.partition.spec;

import java.util.Map;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.PartitionWithoutSD;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionSpecWithSharedSD;
import org.apache.hadoop.hive.metastore.api.MetaException;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;

public class PartitionSpecWithSharedSDProxy extends PartitionSpecProxy
{
    private PartitionSpec partitionSpec;
    
    public PartitionSpecWithSharedSDProxy(final PartitionSpec partitionSpec) {
        assert partitionSpec.isSetSharedSDPartitionSpec();
        this.partitionSpec = partitionSpec;
    }
    
    @Override
    public int size() {
        return this.partitionSpec.getSharedSDPartitionSpec().getPartitionsSize();
    }
    
    @Override
    public void setDbName(final String dbName) {
        this.partitionSpec.setDbName(dbName);
    }
    
    @Override
    public void setTableName(final String tableName) {
        this.partitionSpec.setTableName(tableName);
    }
    
    @Override
    public String getDbName() {
        return this.partitionSpec.getDbName();
    }
    
    @Override
    public String getTableName() {
        return this.partitionSpec.getTableName();
    }
    
    @Override
    public PartitionIterator getPartitionIterator() {
        return new Iterator(this);
    }
    
    @Override
    public List<PartitionSpec> toPartitionSpec() {
        return Arrays.asList(this.partitionSpec);
    }
    
    @Override
    public void setRootLocation(final String rootLocation) throws MetaException {
        this.partitionSpec.setRootPath(rootLocation);
        this.partitionSpec.getSharedSDPartitionSpec().getSd().setLocation(rootLocation);
    }
    
    public static class Iterator implements PartitionIterator
    {
        private PartitionSpecWithSharedSDProxy partitionSpecWithSharedSDProxy;
        private PartitionSpecWithSharedSD pSpec;
        private int index;
        
        Iterator(final PartitionSpecWithSharedSDProxy partitionSpecWithSharedSDProxy) {
            this.partitionSpecWithSharedSDProxy = partitionSpecWithSharedSDProxy;
            this.pSpec = this.partitionSpecWithSharedSDProxy.partitionSpec.getSharedSDPartitionSpec();
            this.index = 0;
        }
        
        @Override
        public boolean hasNext() {
            return this.index < this.pSpec.getPartitions().size();
        }
        
        @Override
        public Partition next() {
            final Partition partition = this.getCurrent();
            ++this.index;
            return partition;
        }
        
        @Override
        public void remove() {
            this.pSpec.getPartitions().remove(this.index);
        }
        
        @Override
        public Partition getCurrent() {
            final PartitionWithoutSD partWithoutSD = this.pSpec.getPartitions().get(this.index);
            final StorageDescriptor partSD = new StorageDescriptor(this.pSpec.getSd());
            partSD.setLocation(partSD.getLocation() + partWithoutSD.getRelativePath());
            return new Partition(partWithoutSD.getValues(), this.partitionSpecWithSharedSDProxy.partitionSpec.getDbName(), this.partitionSpecWithSharedSDProxy.partitionSpec.getTableName(), partWithoutSD.getCreateTime(), partWithoutSD.getLastAccessTime(), partSD, partWithoutSD.getParameters());
        }
        
        @Override
        public String getDbName() {
            return this.partitionSpecWithSharedSDProxy.partitionSpec.getDbName();
        }
        
        @Override
        public String getTableName() {
            return this.partitionSpecWithSharedSDProxy.partitionSpec.getTableName();
        }
        
        @Override
        public Map<String, String> getParameters() {
            return this.pSpec.getPartitions().get(this.index).getParameters();
        }
        
        @Override
        public void setParameters(final Map<String, String> parameters) {
            this.pSpec.getPartitions().get(this.index).setParameters(parameters);
        }
        
        @Override
        public String getLocation() {
            return this.pSpec.getSd().getLocation() + this.pSpec.getPartitions().get(this.index).getRelativePath();
        }
        
        @Override
        public void putToParameters(final String key, final String value) {
            this.pSpec.getPartitions().get(this.index).putToParameters(key, value);
        }
        
        @Override
        public void setCreateTime(final long time) {
            this.pSpec.getPartitions().get(this.index).setCreateTime((int)time);
        }
    }
}
