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
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "Apache Sentry (Incubating)" })
@InterfaceStability.Evolving
public interface MetaStoreFilterHook
{
    List<String> filterDatabases(final List<String> p0) throws MetaException;
    
    Database filterDatabase(final Database p0) throws MetaException, NoSuchObjectException;
    
    List<String> filterTableNames(final String p0, final List<String> p1) throws MetaException;
    
    Table filterTable(final Table p0) throws MetaException, NoSuchObjectException;
    
    List<Table> filterTables(final List<Table> p0) throws MetaException;
    
    List<Partition> filterPartitions(final List<Partition> p0) throws MetaException;
    
    List<PartitionSpec> filterPartitionSpecs(final List<PartitionSpec> p0) throws MetaException;
    
    Partition filterPartition(final Partition p0) throws MetaException, NoSuchObjectException;
    
    List<String> filterPartitionNames(final String p0, final String p1, final List<String> p2) throws MetaException;
    
    Index filterIndex(final Index p0) throws MetaException, NoSuchObjectException;
    
    List<String> filterIndexNames(final String p0, final String p1, final List<String> p2) throws MetaException;
    
    List<Index> filterIndexes(final List<Index> p0) throws MetaException;
}
