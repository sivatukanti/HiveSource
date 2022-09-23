// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.partition.spec;

import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import java.util.List;

public abstract class PartitionSpecProxy
{
    public abstract int size();
    
    public abstract void setDbName(final String p0);
    
    public abstract void setTableName(final String p0);
    
    public abstract String getDbName();
    
    public abstract String getTableName();
    
    public abstract PartitionIterator getPartitionIterator();
    
    public abstract List<PartitionSpec> toPartitionSpec();
    
    public abstract void setRootLocation(final String p0) throws MetaException;
    
    public static class Factory
    {
        public static PartitionSpecProxy get(final PartitionSpec partSpec) {
            if (partSpec == null) {
                return null;
            }
            if (partSpec.isSetPartitionList()) {
                return new PartitionListComposingSpecProxy(partSpec);
            }
            if (partSpec.isSetSharedSDPartitionSpec()) {
                return new PartitionSpecWithSharedSDProxy(partSpec);
            }
            assert false : "Unsupported type of PartitionSpec!";
            return null;
        }
        
        public static PartitionSpecProxy get(final List<PartitionSpec> partitionSpecs) {
            return new CompositePartitionSpecProxy(partitionSpecs);
        }
    }
    
    public static class SimplePartitionWrapperIterator implements PartitionIterator
    {
        private Partition partition;
        
        public SimplePartitionWrapperIterator(final Partition partition) {
            this.partition = partition;
        }
        
        @Override
        public Partition getCurrent() {
            return this.partition;
        }
        
        @Override
        public String getDbName() {
            return this.partition.getDbName();
        }
        
        @Override
        public String getTableName() {
            return this.partition.getTableName();
        }
        
        @Override
        public Map<String, String> getParameters() {
            return this.partition.getParameters();
        }
        
        @Override
        public void setParameters(final Map<String, String> parameters) {
            this.partition.setParameters(parameters);
        }
        
        @Override
        public void putToParameters(final String key, final String value) {
            this.partition.putToParameters(key, value);
        }
        
        @Override
        public String getLocation() {
            return this.partition.getSd().getLocation();
        }
        
        @Override
        public void setCreateTime(final long time) {
            this.partition.setCreateTime((int)time);
        }
        
        @Override
        public boolean hasNext() {
            return false;
        }
        
        @Override
        public Partition next() {
            return null;
        }
        
        @Override
        public void remove() {
        }
    }
    
    public interface PartitionIterator extends Iterator<Partition>
    {
        Partition getCurrent();
        
        String getDbName();
        
        String getTableName();
        
        Map<String, String> getParameters();
        
        void setParameters(final Map<String, String> p0);
        
        void putToParameters(final String p0, final String p1);
        
        String getLocation();
        
        void setCreateTime(final long p0);
    }
}
