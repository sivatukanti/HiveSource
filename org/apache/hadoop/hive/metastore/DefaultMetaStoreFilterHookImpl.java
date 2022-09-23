// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import java.util.List;
import org.apache.hadoop.hive.conf.HiveConf;

public class DefaultMetaStoreFilterHookImpl implements MetaStoreFilterHook
{
    public DefaultMetaStoreFilterHookImpl(final HiveConf conf) {
    }
    
    @Override
    public List<String> filterDatabases(final List<String> dbList) throws MetaException {
        return dbList;
    }
    
    @Override
    public Database filterDatabase(final Database dataBase) throws NoSuchObjectException {
        return dataBase;
    }
    
    @Override
    public List<String> filterTableNames(final String dbName, final List<String> tableList) throws MetaException {
        return tableList;
    }
    
    @Override
    public Table filterTable(final Table table) throws NoSuchObjectException {
        return table;
    }
    
    @Override
    public List<Table> filterTables(final List<Table> tableList) throws MetaException {
        return tableList;
    }
    
    @Override
    public List<Partition> filterPartitions(final List<Partition> partitionList) throws MetaException {
        return partitionList;
    }
    
    @Override
    public List<PartitionSpec> filterPartitionSpecs(final List<PartitionSpec> partitionSpecList) throws MetaException {
        return partitionSpecList;
    }
    
    @Override
    public Partition filterPartition(final Partition partition) throws NoSuchObjectException {
        return partition;
    }
    
    @Override
    public List<String> filterPartitionNames(final String dbName, final String tblName, final List<String> partitionNames) throws MetaException {
        return partitionNames;
    }
    
    @Override
    public Index filterIndex(final Index index) throws NoSuchObjectException {
        return index;
    }
    
    @Override
    public List<String> filterIndexNames(final String dbName, final String tblName, final List<String> indexList) throws MetaException {
        return indexList;
    }
    
    @Override
    public List<Index> filterIndexes(final List<Index> indexeList) throws MetaException {
        return indexeList;
    }
}
