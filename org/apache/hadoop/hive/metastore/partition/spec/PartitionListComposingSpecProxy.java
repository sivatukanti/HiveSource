// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.partition.spec;

import java.util.Map;
import org.apache.hadoop.hive.metastore.api.MetaException;
import java.util.Iterator;
import org.apache.hadoop.hive.metastore.api.Partition;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;

public class PartitionListComposingSpecProxy extends PartitionSpecProxy
{
    private PartitionSpec partitionSpec;
    
    protected PartitionListComposingSpecProxy(final PartitionSpec partitionSpec) {
        assert partitionSpec.isSetPartitionList() : "Partition-list should have been set.";
        this.partitionSpec = partitionSpec;
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
    public int size() {
        return this.partitionSpec.getPartitionList().getPartitionsSize();
    }
    
    @Override
    public void setDbName(final String dbName) {
        this.partitionSpec.setDbName(dbName);
        for (final Partition partition : this.partitionSpec.getPartitionList().getPartitions()) {
            partition.setDbName(dbName);
        }
    }
    
    @Override
    public void setTableName(final String tableName) {
        this.partitionSpec.setTableName(tableName);
        for (final Partition partition : this.partitionSpec.getPartitionList().getPartitions()) {
            partition.setTableName(tableName);
        }
    }
    
    @Override
    public void setRootLocation(final String newRootPath) throws MetaException {
        final String oldRootPath = this.partitionSpec.getRootPath();
        if (oldRootPath == null) {
            throw new MetaException("No common root-path. Can't replace root-path!");
        }
        for (final Partition partition : this.partitionSpec.getPartitionList().getPartitions()) {
            final String location = partition.getSd().getLocation();
            if (!location.startsWith(oldRootPath)) {
                throw new MetaException("Common root-path not found. Can't replace root-path!");
            }
            partition.getSd().setLocation(location.replace(oldRootPath, newRootPath));
        }
    }
    
    public static class Iterator implements PartitionIterator
    {
        PartitionListComposingSpecProxy partitionSpecProxy;
        List<Partition> partitionList;
        int index;
        
        public Iterator(final PartitionListComposingSpecProxy partitionSpecProxy) {
            this.partitionSpecProxy = partitionSpecProxy;
            this.partitionList = partitionSpecProxy.partitionSpec.getPartitionList().getPartitions();
            this.index = 0;
        }
        
        @Override
        public Partition getCurrent() {
            return this.partitionList.get(this.index);
        }
        
        @Override
        public String getDbName() {
            return this.partitionSpecProxy.getDbName();
        }
        
        @Override
        public String getTableName() {
            return this.partitionSpecProxy.getTableName();
        }
        
        @Override
        public Map<String, String> getParameters() {
            return this.partitionList.get(this.index).getParameters();
        }
        
        @Override
        public void setParameters(final Map<String, String> parameters) {
            this.partitionList.get(this.index).setParameters(parameters);
        }
        
        @Override
        public String getLocation() {
            return this.partitionList.get(this.index).getSd().getLocation();
        }
        
        @Override
        public void putToParameters(final String key, final String value) {
            this.partitionList.get(this.index).putToParameters(key, value);
        }
        
        @Override
        public void setCreateTime(final long time) {
            this.partitionList.get(this.index).setCreateTime((int)time);
        }
        
        @Override
        public boolean hasNext() {
            return this.index < this.partitionList.size();
        }
        
        @Override
        public Partition next() {
            return this.partitionList.get(this.index++);
        }
        
        @Override
        public void remove() {
            this.partitionList.remove(this.index);
        }
    }
}
