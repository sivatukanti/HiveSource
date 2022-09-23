// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.partition.spec;

import java.util.Map;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.MetaException;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import java.util.List;

public class CompositePartitionSpecProxy extends PartitionSpecProxy
{
    private String dbName;
    private String tableName;
    private List<PartitionSpec> partitionSpecs;
    private List<PartitionSpecProxy> partitionSpecProxies;
    private int size;
    
    protected CompositePartitionSpecProxy(final List<PartitionSpec> partitionSpecs) {
        this.size = 0;
        this.partitionSpecs = partitionSpecs;
        if (partitionSpecs.isEmpty()) {
            this.dbName = null;
            this.tableName = null;
        }
        else {
            this.dbName = partitionSpecs.get(0).getDbName();
            this.tableName = partitionSpecs.get(0).getTableName();
            this.partitionSpecProxies = new ArrayList<PartitionSpecProxy>(partitionSpecs.size());
            for (final PartitionSpec partitionSpec : partitionSpecs) {
                final PartitionSpecProxy partitionSpecProxy = Factory.get(partitionSpec);
                this.partitionSpecProxies.add(partitionSpecProxy);
                this.size += partitionSpecProxy.size();
            }
        }
        assert this.isValid() : "Invalid CompositePartitionSpecProxy!";
    }
    
    protected CompositePartitionSpecProxy(final String dbName, final String tableName, final List<PartitionSpec> partitionSpecs) {
        this.size = 0;
        this.dbName = dbName;
        this.tableName = tableName;
        this.partitionSpecs = partitionSpecs;
        this.partitionSpecProxies = new ArrayList<PartitionSpecProxy>(partitionSpecs.size());
        for (final PartitionSpec partitionSpec : partitionSpecs) {
            this.partitionSpecProxies.add(Factory.get(partitionSpec));
        }
        assert this.isValid() : "Invalid CompositePartitionSpecProxy!";
    }
    
    private boolean isValid() {
        for (final PartitionSpecProxy partitionSpecProxy : this.partitionSpecProxies) {
            if (partitionSpecProxy instanceof CompositePartitionSpecProxy) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public void setDbName(final String dbName) {
        this.dbName = dbName;
        for (final PartitionSpecProxy partSpecProxy : this.partitionSpecProxies) {
            partSpecProxy.setDbName(dbName);
        }
    }
    
    @Override
    public void setTableName(final String tableName) {
        this.tableName = tableName;
        for (final PartitionSpecProxy partSpecProxy : this.partitionSpecProxies) {
            partSpecProxy.setTableName(tableName);
        }
    }
    
    @Override
    public String getDbName() {
        return this.dbName;
    }
    
    @Override
    public String getTableName() {
        return this.tableName;
    }
    
    @Override
    public PartitionIterator getPartitionIterator() {
        return new Iterator(this);
    }
    
    @Override
    public List<PartitionSpec> toPartitionSpec() {
        return this.partitionSpecs;
    }
    
    @Override
    public void setRootLocation(final String rootLocation) throws MetaException {
        for (final PartitionSpecProxy partSpecProxy : this.partitionSpecProxies) {
            partSpecProxy.setRootLocation(rootLocation);
        }
    }
    
    public static class Iterator implements PartitionIterator
    {
        private CompositePartitionSpecProxy composite;
        private List<PartitionSpecProxy> partitionSpecProxies;
        private int index;
        private PartitionIterator iterator;
        
        public Iterator(final CompositePartitionSpecProxy composite) {
            this.index = -1;
            this.iterator = null;
            this.composite = composite;
            this.partitionSpecProxies = composite.partitionSpecProxies;
            if (this.partitionSpecProxies != null && !this.partitionSpecProxies.isEmpty()) {
                this.index = 0;
                this.iterator = this.partitionSpecProxies.get(this.index).getPartitionIterator();
            }
        }
        
        @Override
        public boolean hasNext() {
            if (this.iterator == null) {
                return false;
            }
            if (this.iterator.hasNext()) {
                return true;
            }
            while (++this.index < this.partitionSpecProxies.size()) {
                final PartitionIterator partitionIterator = this.partitionSpecProxies.get(this.index).getPartitionIterator();
                this.iterator = partitionIterator;
                if (!partitionIterator.hasNext()) {
                    continue;
                }
                break;
            }
            return this.index < this.partitionSpecProxies.size() && this.iterator.hasNext();
        }
        
        @Override
        public Partition next() {
            if (this.iterator.hasNext()) {
                return this.iterator.next();
            }
            while (++this.index < this.partitionSpecProxies.size()) {
                final PartitionIterator partitionIterator = this.partitionSpecProxies.get(this.index).getPartitionIterator();
                this.iterator = partitionIterator;
                if (!partitionIterator.hasNext()) {
                    continue;
                }
                break;
            }
            return (this.index == this.partitionSpecProxies.size()) ? null : this.iterator.next();
        }
        
        @Override
        public void remove() {
            this.iterator.remove();
        }
        
        @Override
        public Partition getCurrent() {
            return this.iterator.getCurrent();
        }
        
        @Override
        public String getDbName() {
            return this.composite.dbName;
        }
        
        @Override
        public String getTableName() {
            return this.composite.tableName;
        }
        
        @Override
        public Map<String, String> getParameters() {
            return this.iterator.getParameters();
        }
        
        @Override
        public void setParameters(final Map<String, String> parameters) {
            this.iterator.setParameters(parameters);
        }
        
        @Override
        public String getLocation() {
            return this.iterator.getLocation();
        }
        
        @Override
        public void putToParameters(final String key, final String value) {
            this.iterator.putToParameters(key, value);
        }
        
        @Override
        public void setCreateTime(final long time) {
            this.iterator.setCreateTime(time);
        }
    }
}
